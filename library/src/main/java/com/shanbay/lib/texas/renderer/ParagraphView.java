package com.shanbay.lib.texas.renderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.shanbay.lib.log.Log;
import com.shanbay.lib.texas.annotations.Hidden;
import com.shanbay.lib.texas.text.Appearance;
import com.shanbay.lib.texas.text.Box;
import com.shanbay.lib.texas.text.DrawableBox;
import com.shanbay.lib.texas.text.Line;
import com.shanbay.lib.texas.text.OnClickedListener;
import com.shanbay.lib.texas.text.Paragraph;
import com.shanbay.lib.texas.text.TextBox;
import com.shanbay.lib.texas.text.TextStyle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 渲染文章段落视图
 */
@Hidden
public class ParagraphView extends View implements GestureDetector.OnGestureListener {
	private static final int DEFAULT_ROUND_RADIUS = 3;

	private Paragraph mParagraph;
	private TextPaint mPaint;
	private TextPaint mWorkPaint = new TextPaint();
	private GestureDetector mGestureDetector = null;
	private float mRectRadius;

	private float mTopPadding;
	private float mBottomPadding;
	private RenderOption mRenderOption;
	private OnSelectedChangedListener mOnTextSelectedListener;
	private Box mLastTouchBox = null;
	@Nullable
	private ParagraphSelection mParagraphSelection;

	public ParagraphView(Context context) {
		this(context, null);
	}

	public ParagraphView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ParagraphView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}

		mRectRadius = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				DEFAULT_ROUND_RADIUS,
				getResources().getDisplayMetrics()
		);
	}

	void render(@NonNull Paragraph paragraph,
				@NonNull TextPaint paint,
				@NonNull RenderOption renderOption,
				ParagraphSelection selection,
				float topPadding,
				float bottomPadding) {
		mParagraph = paragraph;
		mPaint = paint;
		mBottomPadding = bottomPadding;
		mTopPadding = topPadding;
		mRenderOption = renderOption;
		mParagraphSelection = selection;
		setDebugMode(renderOption.isEnableDebug());
		requestLayout();
	}

	private void setDebugMode(boolean enable) {
		if (!enable) {
			return;
		}

		if (mDebugDrawVisitor == null) {
			mDebugDrawVisitor = new DebugDrawVisitor();
		}
	}

	public void setOnTextSelectedListener(OnSelectedChangedListener onTextSelectedListener) {
		mOnTextSelectedListener = onTextSelectedListener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mRenderOption == null || !mRenderOption.isWordSelectable()) {
			return super.onTouchEvent(event);
		}

		if (mGestureDetector == null) {
			mGestureDetector = new GestureDetector(getContext(), this);
		}
		mGestureDetector.setIsLongpressEnabled(true);
		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int lineCount = 0;
		if (mParagraph != null && (lineCount = mParagraph.getLineCount()) != 0) {
			/**
			 * 第一行和最后一行要包含当前typeface建议的padding，防止绘制的时候超出view显示区域
			 *
			 * {@link Paint.FontMetrics}
			 * */
			int height = (int) Math.ceil(mBottomPadding + mTopPadding);
			for (int i = 0; i < lineCount; ++i) {
				Line line = mParagraph.getLine(i);
				height += line.getLineHeight();
			}

			if (lineCount > 1) {
				height += ((lineCount - 1) * mRenderOption.getLineSpace());
			}

			heightMeasureSpec = MeasureSpec.makeMeasureSpec(
					height,
					MeasureSpec.AT_MOST
			);
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(final Canvas canvas) {
		super.onDraw(canvas);
		if (mParagraph == null ||
				mParagraph.getLineCount() == 0 ||
				mRenderOption == null) {
			return;
		}

		ParagraphSelection selection = mParagraphSelection;
		if (selection != null) {
			mWorkPaint.set(mPaint);
			mWorkPaint.setColor(selection.isSelectedByLongClick() ?
					mRenderOption.getSpanSelectedBackgroundColor() :
					mRenderOption.getSelectedBackgroundColor());
			selection.draw(canvas, mWorkPaint, mRectRadius);
		}

		int width = getWidth();

		mDrawVisitor.setCanvas(canvas);
		mDrawVisitor.setSelection(selection);
		mDrawVisitor.visit(mParagraph, width, mRenderOption, mTopPadding);
		mDrawVisitor.clear();

		// 绘制debug信息
		if (!mRenderOption.isEnableDebug()) {
			return;
		}

		mDebugDrawVisitor.setCanvas(canvas);
		mDebugDrawVisitor.setSelection(selection);
		mDebugDrawVisitor.visit(mParagraph, width, mRenderOption, mTopPadding);
		mDebugDrawVisitor.clear();
	}

	private boolean handleMotion(MotionEvent e, boolean isLongClicked) {
		if (mLastTouchBox == null || mOnTextSelectedListener == null) {
			return false;
		}

		OnClickedListener onClickedListener = getBoxOnClickedListener(mLastTouchBox, isLongClicked);
		if (onClickedListener == null) {
			return false;
		}

		if (mLastTouchBox instanceof DrawableBox) {
			mOnTextSelectedListener.onDrawSelected(e, mParagraph, isLongClicked, (DrawableBox) mLastTouchBox, onClickedListener);
		} else {
			mOnTextSelectedListener.onTextSelected(e, mParagraph, isLongClicked, onClickedListener, getWidth());
		}
		return true;
	}

	private boolean handleClicked(MotionEvent e) {
		return handleMotion(e, false);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		mLastTouchBox = null;
		if (mParagraph == null || mParagraph.getLineCount() == 0) {
			return false;
		}

		float x = e.getX();
		float y = e.getY();
		mMotionEventVisitor.setMotionLocation(x, y);
		mMotionEventVisitor.visit(mParagraph, getWidth(), mRenderOption, mTopPadding);
		Box target = mMotionEventVisitor.getBox();
		mMotionEventVisitor.clear();
		if (target == null) {
			return false;
		}

		mLastTouchBox = target;
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		/* do nothing */
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return handleClicked(e);
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		handleLongClicked(e);
	}

	private void handleLongClicked(MotionEvent e) {
		handleMotion(e, true);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		/* do nothing */
		return false;
	}

	public interface OnSelectedChangedListener {

		void onTextSelected(MotionEvent e, Paragraph paragraph, boolean isLongClicked, OnClickedListener onClickedListener, int width);

		void onDrawSelected(MotionEvent e, Paragraph paragraph, boolean isLongClicked, DrawableBox box, OnClickedListener onClickedListener);
	}

	private DrawVisitor mDrawVisitor = new DrawVisitor();

	private class DrawVisitor extends ParagraphVisitor {
		private Canvas mCanvas;
		private ParagraphSelection mSelection;

		void setCanvas(Canvas canvas) {
			mCanvas = canvas;
		}

		void setSelection(ParagraphSelection selection) {
			mSelection = selection;
		}

		@Override
		public void onVisitBox(Box box, float left, float top, float right, float bottom) {

			// 先绘制背景
			if (box instanceof TextBox) {
				TextBox textBox = (TextBox) box;
				Appearance background = textBox.getBackground();
				if (background != null) {
					mWorkPaint.set(mPaint);
					background.draw(mCanvas, mWorkPaint, left, top, right, bottom);
				}
			}

			mWorkPaint.set(mPaint);

			if (box instanceof TextBox) {
				TextBox textBox = (TextBox) box;
				TextStyle textStyle = textBox.getTextStyle();

				if (textStyle != null) {
					textStyle.update(mWorkPaint);
				}
			}

			if (mSelection != null && box.isSelected()) {
				mWorkPaint.setColor(mSelection.isSelectedByLongClick() ?
						mRenderOption.getSpanSelectedTextColor() :
						mRenderOption.getSelectedTextColor());
			}

			box.draw(mCanvas, mWorkPaint, left, bottom - mBottomPadding);

			if (box instanceof TextBox) {
				TextBox textBox = (TextBox) box;
				Appearance foreground = textBox.getForeground();
				if (foreground != null) {
					mWorkPaint.set(mPaint);
					foreground.draw(mCanvas, mWorkPaint, left, top, right, bottom);
				}
			}
		}

		public void clear() {
			mCanvas = null;
			mSelection = null;
		}
	}

	private MotionEventVisitor mMotionEventVisitor = new MotionEventVisitor();

	private class MotionEventVisitor extends ParagraphVisitor {

		private Box mBox;
		private float mX;
		private float mY;

		public void setMotionLocation(float x, float y) {
			mX = x;
			mY = y;
		}

		public void clear() {
			mBox = null;
			mX = mY = -1;
		}

		public Box getBox() {
			return mBox;
		}

		@Override
		public void onVisitBox(Box box, float left, float top, float right, float bottom) {
			if ((left <= mX && mX <= right) &&
					(top <= mY && mY <= bottom)) {
				mBox = box;
			}
		}
	}

	private DebugDrawVisitor mDebugDrawVisitor;

	private class DebugDrawVisitor extends ParagraphVisitor {
		private final Paint mDebugPaint;
		private Canvas mCanvas;
		private ParagraphSelection mSelection;
		private int[] mLocation = new int[2];

		DebugDrawVisitor() {
			mDebugPaint = new Paint();
			mDebugPaint.setColor(Color.GREEN);
			mDebugPaint.setStyle(Paint.Style.STROKE);
			mDebugPaint.setTextSize(40);
		}

		void setSelection(ParagraphSelection selection) {
			mSelection = selection;
		}

		void setCanvas(Canvas canvas) {
			mCanvas = canvas;
		}

		void clear() {
			mCanvas = null;
		}

		@Override
		public void onVisitParagraphEnd(Paragraph paragraph) {
			if (!(mSelection instanceof TextParagraphSelection)) {
				return;
			}

			d("para end, render debug");
			TextParagraphSelection textParagraphSelection = (TextParagraphSelection) mSelection;
			mWorkPaint.set(mDebugPaint);
			mWorkPaint.setColor(Color.RED);
			mWorkPaint.setStrokeWidth(200);
			getLocationOnScreen(mLocation);
			int x = getWidth() - 100;
			mCanvas.drawLine(x,
					textParagraphSelection.getTopEdgeOnScreen() - mLocation[1],
					x,
					textParagraphSelection.getBottomEdgeOnScreen() - mLocation[1],
					mWorkPaint);
		}

		@Override
		public void onVisitLineEnd(Line line, float x, float y) {
			float startX = 0;
			float startY = y + mRenderOption.getLineSpace();
			Rect rect = new Rect();
			String debugInfo = line.getRatio() + " " + line.getSpaceWidth();
			mDebugPaint.getTextBounds(debugInfo, 0, debugInfo.length(), rect);
			mDebugPaint.setColor(Color.BLUE);
			rect.offset((int) startX, (int) startY);
			mCanvas.drawRect(rect, mDebugPaint);
			mDebugPaint.setColor(Color.RED);
			mCanvas.drawText(debugInfo, startX, startY, mDebugPaint);
		}

		@Override
		public void onVisitBox(Box box, float left, float top, float right, float bottom) {
			mDebugPaint.setColor(Color.GREEN);
			mCanvas.drawRect(left, top, right, bottom, mDebugPaint);
		}
	}

	private static void d(String msg) {
		Log.d("TexasParaView", msg);
	}

	static OnClickedListener getBoxOnClickedListener(Box target, boolean isLongClicked) {
		if (!isLongClicked) {
			return target.getOnClickedListener();
		}

		if (!(target instanceof TextBox)) {
			return null;
		}

		return ((TextBox) target).getSpanOnClickedListener();
	}
}
