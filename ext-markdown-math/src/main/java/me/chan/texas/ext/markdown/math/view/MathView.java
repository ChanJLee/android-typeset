package me.chan.texas.ext.markdown.math.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.chan.texas.ext.markdown.math.R;
import me.chan.texas.ext.markdown.math.TexMathParser;
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
	private int mGravity = Gravity.START | Gravity.TOP;

	public static MathPaint create(Context context) {
		// 从主题中获取默认的文本颜色和大小
		TypedArray themeArray = context.obtainStyledAttributes(new int[]{
				android.R.attr.textColorPrimary,
				android.R.attr.textSize
		});
		int defaultTextColor = themeArray.getColor(0, Color.BLACK);
		int defaultTextSize = themeArray.getDimensionPixelSize(1, 48);
		themeArray.recycle();

		TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTypeface(TexMathParser.getTypeface());
		textPaint.setStyle(Paint.Style.FILL);
		textPaint.setColor(defaultTextColor);
		textPaint.setTextSize(defaultTextSize);

		TexasPaintImpl paint = new TexasPaintImpl();
		paint.reset(new PaintSet(textPaint));
		return new MathPaintImpl(paint);
	}

	public MathView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);

		mTexasPaint = create(context);
		mCanvas = new MathCanvasImpl(new TexasCanvasImpl());

		mGraphicsBuffer = new GraphicsBuffer();
		mMsgHandler.addListener(mListener);

		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MathView);
		if (array.hasValue(R.styleable.MathView_android_textColor)) {
			mTexasPaint.setColor(array.getColor(R.styleable.MathView_android_textColor, 0));
		}
		if (array.hasValue(R.styleable.MathView_android_textSize)) {
			mTexasPaint.setTextSize(array.getDimensionPixelSize(R.styleable.MathView_android_textSize, 0));
		}
		if (array.hasValue(R.styleable.MathView_android_gravity)) {
			mGravity = array.getInt(R.styleable.MathView_android_gravity, Gravity.START | Gravity.TOP);
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
				// 显示滚动条
				awakenScrollBars();
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

		// 启用水平和垂直滚动条
		setHorizontalScrollBarEnabled(true);
		setVerticalScrollBarEnabled(true);
		// 设置滚动条样式为内部显示
		setScrollBarStyle(SCROLLBARS_INSIDE_OVERLAY);
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

		// 计算 gravity 偏移
		int gravityOffsetX = 0;
		int gravityOffsetY = 0;

		int contentWidth = getContentWidth();
		int contentHeight = getContentHeight();
		int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
		int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();

		// 只有当内容小于视图时才应用 gravity
		if (contentWidth < viewWidth) {
			int horizontalGravity = mGravity & Gravity.HORIZONTAL_GRAVITY_MASK;
			switch (horizontalGravity) {
				case Gravity.CENTER_HORIZONTAL:
					gravityOffsetX = (viewWidth - contentWidth) / 2;
					break;
				case Gravity.RIGHT:
				case Gravity.END:
					gravityOffsetX = viewWidth - contentWidth;
					break;
				case Gravity.LEFT:
				case Gravity.START:
				default:
					gravityOffsetX = 0;
					break;
			}
		}

		if (contentHeight < viewHeight) {
			int verticalGravity = mGravity & Gravity.VERTICAL_GRAVITY_MASK;
			switch (verticalGravity) {
				case Gravity.CENTER_VERTICAL:
					gravityOffsetY = (viewHeight - contentHeight) / 2;
					break;
				case Gravity.BOTTOM:
					gravityOffsetY = viewHeight - contentHeight;
					break;
				case Gravity.TOP:
				default:
					gravityOffsetY = 0;
					break;
			}
		}

		// 应用 padding、gravity 和滚动偏移
		canvas.translate(
				getPaddingLeft() + gravityOffsetX - getScrollX(),
				getPaddingTop() + gravityOffsetY - getScrollY()
		);

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
			// 显示滚动条
			awakenScrollBars();
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

	// 允许父容器拦截事件（解决嵌套滑动冲突）
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

	@Override
	protected int computeHorizontalScrollRange() {
		// 返回内容的总宽度
		return getContentWidth();
	}

	@Override
	protected int computeHorizontalScrollExtent() {
		// 返回可见区域的宽度（View的宽度减去padding）
		return getWidth() - getPaddingLeft() - getPaddingRight();
	}

	@Override
	protected int computeHorizontalScrollOffset() {
		// 返回当前的水平滚动偏移量
		return getScrollX();
	}

	@Override
	protected int computeVerticalScrollRange() {
		// 返回内容的总高度
		return getContentHeight();
	}

	@Override
	protected int computeVerticalScrollExtent() {
		// 返回可见区域的高度（View的高度减去padding）
		return getHeight() - getPaddingTop() - getPaddingBottom();
	}

	@Override
	protected int computeVerticalScrollOffset() {
		// 返回当前的垂直滚动偏移量
		return getScrollY();
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

	/**
	 * 设置内容的对齐方式
	 *
	 * @param gravity 对齐方式，使用 Gravity 常量，如 Gravity.CENTER、Gravity.START | Gravity.TOP 等
	 */
	public void setGravity(int gravity) {
		if (mGravity != gravity) {
			mGravity = gravity;
			invalidate();
		}
	}

	/**
	 * 获取当前的对齐方式
	 *
	 * @return 对齐方式
	 */
	public int getGravity() {
		return mGravity;
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