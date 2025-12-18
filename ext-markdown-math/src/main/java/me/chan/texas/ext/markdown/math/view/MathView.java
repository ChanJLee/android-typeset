package me.chan.texas.ext.markdown.math.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.chan.texas.ext.markdown.math.R;
import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathCanvasImpl;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaintImpl;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.core.WorkerScheduler;
import me.chan.texas.renderer.core.graphics.GraphicsBuffer;
import me.chan.texas.renderer.core.graphics.TexasCanvasImpl;
import me.chan.texas.renderer.core.graphics.TexasPaintImpl;
import me.chan.texas.renderer.core.sync.MsgHandler;
import me.chan.texas.utils.TexasUtils;
import me.chan.texas.utils.concurrency.Worker;

public class MathView extends View implements AsyncMathViewRenderer {
	private final GraphicsBuffer mGraphicsBuffer;

	@Nullable
	private FormulaBackgroundTask.Result mResult;

	private final MathPaint mTexasPaint;
	private final MathCanvas mCanvas;

	private final Worker.Token mToken = Worker.Token.newInstance();
	private final Worker mBackgroundWorker = WorkerScheduler.getBackgroundWorker();
	private final MsgHandler mMsgHandler = WorkerScheduler.getMsgHandler();
	private final MsgHandler.Listener mListener = (id, value) -> {
		if (id != mToken) {
			return false;
		}

		Object arg = value.arg();
		if (arg instanceof FormulaBackgroundTask.BackgroundArgs) {
			handleBackgroundTaskResult(value);
		}

		return true;
	};
	private final OverScroller mScroller;
	private final GestureDetector mGestureDetector;


	public MathView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);

		TextPaint textPaint = new TextPaint();
		textPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "texas_markdown_ext/latinmodern-math.otf"));
		textPaint.setStyle(Paint.Style.FILL);
		textPaint.setColor(Color.BLACK);
		textPaint.setTextSize(64);

		TexasPaintImpl paint = new TexasPaintImpl();
		paint.reset(new PaintSet(textPaint));
		mTexasPaint = new MathPaintImpl(paint);
		mCanvas = new MathCanvasImpl(new TexasCanvasImpl());

		mGraphicsBuffer = new GraphicsBuffer();
		mMsgHandler.addListener(mListener);

		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MathView);
		if (array.hasValue(R.styleable.MathView_textColor)) {
			textPaint.setColor(array.getColor(R.styleable.MathView_textColor, Color.BLACK));
		}
		if (array.hasValue(R.styleable.MathView_textSize)) {
			textPaint.setTextSize(array.getDimensionPixelSize(R.styleable.MathView_textSize, 64));
		}

		if (array.hasValue(R.styleable.MathView_formula)) {
			String formula = array.getString(R.styleable.MathView_formula);
			if (formula != null) {
				render(formula);
			}
		}
		array.recycle();

		mScroller = new OverScroller(context);
		mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onDown(MotionEvent e) {
				// 按下时停止之前的滑动动画
				mScroller.forceFinished(true);
				return true;
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				// 跟随手指滑动
				scrollBy((int) distanceX, (int) distanceY);
				return true;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				// 处理惯性滑动
				int contentWidth = getContentWidth();
				int contentHeight = getContentHeight();

				// 计算可滑动的最大范围
				int maxX = Math.max(0, contentWidth - getWidth() + getPaddingLeft() + getPaddingRight());
				int maxY = Math.max(0, contentHeight - getHeight() + getPaddingTop() + getPaddingBottom());

				mScroller.fling(getScrollX(), getScrollY(),
						-(int) velocityX, -(int) velocityY,
						0, maxX, 0, maxY);
				invalidate();
				return true;
			}
		});
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int contentWidth = 0;
		int contentHeight = 0;

		// 获取内容的真实宽高
		if (mResult != null && mResult.rendererNode != null) {
			contentWidth = mResult.rendererNode.getWidth();
			contentHeight = mResult.rendererNode.getHeight();
		}

		// 加上 Padding
		contentWidth += getPaddingLeft() + getPaddingRight();
		contentHeight += getPaddingTop() + getPaddingBottom();

		// resolveSize 会自动处理:
		// 1. EXACTLY (固定值/match_parent): 使用 Spec 中的大小
		// 2. AT_MOST (wrap_content): 使用 contentWidth，但不超过 Spec 中的最大值
		// 3. UNSPECIFIED: 使用 contentWidth
		int measuredWidth = resolveSize(Math.max(contentWidth, getSuggestedMinimumWidth()), widthMeasureSpec);
		int measuredHeight = resolveSize(Math.max(contentHeight, getSuggestedMinimumHeight()), heightMeasureSpec);

		setMeasuredDimension(measuredWidth, measuredHeight);
	}

	@Override
	protected void onDraw(@NonNull Canvas canvas) {
		// 保存画布状态
		int saveCount = canvas.save();

		// 根据当前的滚动值平移画布，实现内容移动效果
		canvas.translate(-getScrollX(), -getScrollY());

		boolean ret = mGraphicsBuffer.draw(canvas);
		if (GraphicsBuffer.DEBUG && !ret) {
			Log.d("MathView", "draw failed: " + mResult);
		}

		// 恢复画布
		canvas.restoreToCount(saveCount);
	}

	@Override
	public void computeScroll() {
		// 如果 Scroller 正在计算偏移（惯性滑动中）
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate(); // 触发重绘以更新位置
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 将触摸事件交给 GestureDetector 处理
		boolean handled = mGestureDetector.onTouchEvent(event);
		// 如果 GestureDetector 没有消费（且不是 DOWN 事件），调用父类逻辑
		if (!handled && event.getAction() != MotionEvent.ACTION_DOWN) {
			return super.onTouchEvent(event);
		}
		return true;
	}

	@Override
	public void scrollTo(int x, int y) {
		// 限制滑动范围，防止滑出边界
		int contentWidth = getContentWidth();
		int contentHeight = getContentHeight();

		// 最大的 X 和 Y 偏移量
		int maxX = Math.max(0, contentWidth - getWidth() + getPaddingLeft() + getPaddingRight());
		int maxY = Math.max(0, contentHeight - getHeight() + getPaddingTop() + getPaddingBottom());

		// clamp 函数确保 x 在 0 到 maxX 之间
		x = clamp(x, 0, maxX);
		y = clamp(y, 0, maxY);

		if (x != getScrollX() || y != getScrollY()) {
			super.scrollTo(x, y);
		}
	}

	// 辅助方法：获取内容宽度
	private int getContentWidth() {
		return (mResult != null && mResult.rendererNode != null) ? mResult.rendererNode.getWidth() : 0;
	}

	// 辅助方法：获取内容高度
	private int getContentHeight() {
		return (mResult != null && mResult.rendererNode != null) ? mResult.rendererNode.getHeight() : 0;
	}

	// 辅助方法：范围限制
	private int clamp(int n, int min, int max) {
		return Math.max(min, Math.min(n, max));
	}

	// 允许父容器拦截事件（解决嵌套滑动冲突，如果需要的话可以重写 canScrollHorizontally）
	@Override
	public boolean canScrollHorizontally(int direction) {
		int offset = computeHorizontalScrollOffset();
		int range = computeHorizontalScrollRange() - computeHorizontalScrollExtent();
		if (range == 0) return false;
		if (direction < 0) return offset > 0;
		else return offset < range;
	}

	@Override
	public boolean canScrollVertically(int direction) {
		int offset = computeVerticalScrollOffset();
		int range = computeVerticalScrollRange() - computeVerticalScrollExtent();
		if (range == 0) return false;
		if (direction < 0) return offset > 0;
		else return offset < range;
	}

	private final FormulaBackgroundTask mFormulaParseTask = new FormulaBackgroundTask(mMsgHandler);

	public void render(String formula) {
		if (mResult != null && TexasUtils.equals(formula, mResult.args.formula)) {
			return;
		}

		if (!mGraphicsBuffer.isAttached()) {
			mGraphicsBuffer.attach(mToken);
		}

		if (!isInEditMode()) {
			mBackgroundWorker.async(mToken, new FormulaBackgroundTask.BackgroundArgs(formula, mTexasPaint, mCanvas, this), mFormulaParseTask);
			return;
		}

		try {
			FormulaBackgroundTask.Result result = mBackgroundWorker.sync(mToken, new FormulaBackgroundTask.BackgroundArgs(formula, mTexasPaint, mCanvas, this), mFormulaParseTask);
			render(result);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private void handleBackgroundTaskResult(MsgHandler.Msg msg) {
		int type = msg.type();
		if (type == FormulaBackgroundTask.TYPE_SUCCESS) {
			render((FormulaBackgroundTask.Result) msg.value());
		}
	}

	private void render(FormulaBackgroundTask.Result result) {
		boolean relayout = mResult == null || mResult.rendererNode != result.rendererNode;
		mResult = result;

		if (relayout) {
			requestLayout();
			return;
		}

		invalidate();
	}

	public void cancel() {
		mBackgroundWorker.cancel(mToken);
	}

	@Nullable
	@Override
	public Canvas lockCanvas(int width, int height) {
		if (width <= 0 || height <= 0) {
			return null;
		}

		return mGraphicsBuffer.lockCanvas(width, height);
	}

	@Override
	public void unlockCanvasAndPost(Canvas canvas) {
		if (canvas == null) {
			return;
		}

		mGraphicsBuffer.unlockCanvas();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		mMsgHandler.addListener(mListener);
		if (!mGraphicsBuffer.isAttached()) {
			mGraphicsBuffer.attach(mToken);
			if (mResult != null) {
				mBackgroundWorker.async(
						mToken,
						new FormulaBackgroundTask.BackgroundArgs(
								mResult.args.formula,
								mTexasPaint, mCanvas,
								this,
								mResult.rendererNode
						),
						mFormulaParseTask
				);
			}
		}
	}

	@Override
	protected final void onDetachedFromWindow() {
		mMsgHandler.removeListener(mListener);
		mGraphicsBuffer.detach();
		super.onDetachedFromWindow();
	}
}