package me.chan.texas.renderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.chan.texas.annotations.Hidden;
import me.chan.texas.log.Log;
import me.chan.texas.text.Background;
import me.chan.texas.text.Box;
import me.chan.texas.text.DrawableBox;
import me.chan.texas.text.Foreground;
import me.chan.texas.text.Gravity;
import me.chan.texas.text.Line;
import me.chan.texas.text.OnClickedListener;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextBox;
import me.chan.texas.text.TextStyle;

/**
 * 渲染文章段落view
 */
@Hidden
public class ParagraphView extends View implements GestureDetector.OnGestureListener {
	private static final int DEFAULT_ROUND_RADIUS = 3;

	private Paragraph mParagraph;
	private TextPaint mPaint;
	private TextPaint mWorkPaint = new TextPaint();
	private Paint mDebugPaint;
	private GestureDetector mGestureDetector = null;
	private float mRectRadius;

	private float mTopPadding;
	private float mBottomPadding;
	private RenderOption mRenderOption;
	private OnSelectedChangedListener mOnTextSelectedListener;
	private Box mLastTouchBox = null;
	@Nullable
	private ParagraphSelection mParagraphSelection;
	private float mLastYInView, mLastYInWindow;

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
		if (enable && mDebugPaint == null) {
			mDebugPaint = new Paint();
			mDebugPaint.setColor(Color.GREEN);
			mDebugPaint.setStyle(Paint.Style.FILL);
			mDebugPaint.setTextSize(40);
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

		mDrawVisitor.setCanvas(canvas);
		mDrawVisitor.setSelection(selection);
		visitParagraph(mParagraph, mDrawVisitor);
		mDrawVisitor.clear();
	}

	private boolean handleMotion(MotionEvent e, boolean isLongClicked) {
		if (mLastTouchBox == null) {
			return false;
		}

		OnClickedListener onClickedListener = getBoxOnClickedListener(mLastTouchBox, isLongClicked);
		if (onClickedListener == null) {
			return false;
		}

		if (mLastTouchBox instanceof DrawableBox) {
			handleDrawableTouched(isLongClicked);
		} else {
			handleTextTouched(isLongClicked, onClickedListener);
		}

		onClickedListener.onClicked(e.getRawX(), e.getRawY());
		return true;
	}

	private void handleTextTouched(boolean isLongClicked, OnClickedListener onClickedListener) {
		mSelectionVisitor.setOnClickedListener(onClickedListener);
		mSelectionVisitor.setLongClicked(isLongClicked);
		visitParagraph(mParagraph, mSelectionVisitor);
		TextParagraphSelection selection = mSelectionVisitor.getSelection();
		mSelectionVisitor.clear();
		if (mOnTextSelectedListener != null) {
			mOnTextSelectedListener.onTextSelected(selection);
		}
	}

	private void handleDrawableTouched(boolean isLongClicked) {
		DrawableParagraphSelection selection = new DrawableParagraphSelection(
				mParagraph,
				isLongClicked,
				(DrawableBox) mLastTouchBox
		);

		mLastTouchBox.setSelected(true);
		if (mOnTextSelectedListener != null) {
			mOnTextSelectedListener.onDrawSelected(selection);
		}
	}

	private void visitParagraph(Paragraph paragraph, Visitor visitor) {
		visitor.onVisitParagraph(paragraph);
		float y = 0;
		float width = getWidth();
		int lineCount = paragraph.getLineCount();
		for (int i = 0; i < lineCount; ++i) {

			Line line = paragraph.getLine(i);
			y += line.getLineHeight();
			if (i == 0) {
				y -= mBottomPadding;
			}

			float x;
			Gravity gravity = line.getGravity();
			if (gravity == Gravity.CENTER) {
				x = (width - line.getLineWidth()) / 2f;
			} else if (gravity == Gravity.RIGHT) {
				x = (width - line.getLineWidth());
			} else {
				x = 0;
			}
			visitLine(line, x, y, visitor);
			y += mRenderOption.getLineSpace();
		}
		visitor.onVisitParagraphEnd(paragraph);
	}

	private void visitLine(Line line, float x, float y, Visitor visitor) {
		visitor.onVisitLine(line, x, y);

		float spaceWidth = line.getSpaceWidth();
		int boxSize = line.getCount();
		for (int i = 0; i < boxSize; ++i) {
			Box box = line.getBox(i);
			float width = box.getWidth();

			float left = x;
			float right = (float) Math.ceil(x + width);
			float top = (float) Math.ceil(y - line.getLineHeight());
			float bottom = y;
			visitBox(box, left, top, right, bottom, visitor);
			x += (spaceWidth + width);
		}

		visitor.onVisitLineEnd(line, x, y);
	}

	private void visitBox(Box box, float left, float top, float right, float bottom, Visitor visitor) {
		visitor.onVisitBox(box, left, top, right, bottom);
	}

	private OnClickedListener getBoxOnClickedListener(Box target, boolean isLongClicked) {
		if (!isLongClicked) {
			return target.getOnClickedListener();
		}

		if (!(target instanceof TextBox)) {
			return null;
		}

		return ((TextBox) target).getSpanOnClickedListener();
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
		mMotionEventVisitor.setX(x);
		mMotionEventVisitor.setY(y);
		visitParagraph(mParagraph, mMotionEventVisitor);
		Box target = mMotionEventVisitor.getBox();
		mMotionEventVisitor.clear();
		if (target == null) {
			return false;
		}

		mLastYInView = y;
		mLastYInWindow = e.getRawY();
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

	private static void d(String msg) {
		Log.d("TexasParaView", msg);
	}

	public interface OnSelectedChangedListener {
		void onTextSelected(TextParagraphSelection selection);

		void onDrawSelected(DrawableParagraphSelection selection);
	}

	private DrawVisitor mDrawVisitor = new DrawVisitor();

	private class DrawVisitor implements Visitor {
		private Canvas mCanvas;
		private ParagraphSelection mSelection;

		public void setCanvas(Canvas canvas) {
			mCanvas = canvas;
		}

		public void setSelection(ParagraphSelection selection) {
			mSelection = selection;
		}

		@Override
		public void onVisitParagraph(Paragraph paragraph) {
			/* */
		}

		@Override
		public void onVisitParagraphEnd(Paragraph paragraph) {

		}

		@Override
		public void onVisitLine(Line line, float x, float y) {
		}

		@Override
		public void onVisitLineEnd(Line line, float x, float y) {
			if (mRenderOption.isEnableDebug()) {
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
		}

		@Override
		public void onVisitBox(Box box, float left, float top, float right, float bottom) {

			float belowBottom = bottom + mBottomPadding;

			// 先绘制背景
			if (box instanceof TextBox) {
				TextBox textBox = (TextBox) box;
				Background background = textBox.getBackground();
				if (background != null) {
					mWorkPaint.set(mPaint);
					background.draw(mCanvas, mWorkPaint, left, top, right, belowBottom);
				}
			}

			if (mRenderOption.isEnableDebug()) {
				mDebugPaint.setColor(Color.GREEN);
				mCanvas.drawRect(left, top, right, belowBottom, mDebugPaint);
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

			box.draw(mCanvas, mWorkPaint, left, bottom);

			if (box instanceof TextBox) {
				TextBox textBox = (TextBox) box;
				Foreground foreground = textBox.getForeground();
				if (foreground != null) {
					mWorkPaint.set(mPaint);
					foreground.draw(mCanvas, mWorkPaint, left, top, right, belowBottom);
				}
			}
		}

		public void clear() {
			mCanvas = null;
			mSelection = null;
		}
	}

	private MotionEventVisitor mMotionEventVisitor = new MotionEventVisitor();

	private class MotionEventVisitor implements Visitor {
		private Box mBox;
		private float mX;
		private float mY;

		public void clear() {
			mBox = null;
			mX = mY = -1;
		}

		public Box getBox() {
			return mBox;
		}

		public void setX(float x) {
			mX = x;
		}

		public void setY(float y) {
			mY = y;
		}

		@Override
		public void onVisitParagraph(Paragraph paragraph) {

		}

		@Override
		public void onVisitParagraphEnd(Paragraph paragraph) {

		}

		@Override
		public void onVisitLine(Line line, float x, float y) {

		}

		@Override
		public void onVisitLineEnd(Line line, float x, float y) {

		}

		@Override
		public void onVisitBox(Box box, float left, float top, float right, float bottom) {
			if ((left <= mX && mX <= right) &&
					(top <= mY && mY <= bottom + mBottomPadding)) {
				mBox = box;
			}
		}
	}

	private SelectionVisitor mSelectionVisitor = new SelectionVisitor();

	private class SelectionVisitor implements Visitor {

		private OnClickedListener mOnClickedListener;
		private TextParagraphSelection mSelection;
		private boolean mIsLongClicked;
		private RectF mRectF;
		private float mLastLineBottom;
		private float mLastLineTop;
		private float mEdgeTop = -1;
		private float mEdgeBottom = -1;
		private boolean mHasContent = false;

		@Override
		public void onVisitParagraph(Paragraph paragraph) {
			mSelection = new TextParagraphSelection(paragraph, mIsLongClicked);
		}

		@Override
		public void onVisitParagraphEnd(Paragraph paragraph) {
			if (mSelection != null) {
				mSelection.setTopEdgeInWindow(mEdgeTop);
				mSelection.setBottomEdgeInWindow(mEdgeBottom);
			}
		}

		public void clear() {
			mOnClickedListener = null;
			mSelection = null;
			mIsLongClicked = false;
			mLastLineBottom = mLastLineTop = -1;
			mRectF = null;
			mEdgeTop = -1;
			mEdgeBottom = -1;
			mHasContent = false;
		}

		public TextParagraphSelection getSelection() {
			return mSelection;
		}

		public void setOnClickedListener(OnClickedListener onClickedListener) {
			mOnClickedListener = onClickedListener;
		}

		public void setLongClicked(boolean longClicked) {
			mIsLongClicked = longClicked;
		}

		@Override
		public void onVisitLine(Line line, float x, float y) {
			mLastLineBottom = y + mBottomPadding;
			mLastLineTop = y - line.getLineHeight();
			mHasContent = false;

			if (mSelection.isEmpty()) {
				mEdgeTop = mLastYInWindow - (mLastYInView - mLastLineTop);
			}
		}

		@Override
		public void onVisitLineEnd(Line line, float x, float y) {
			if (mRectF != null) {
				mSelection.addSelectArea(mRectF);
				mRectF = null;
			}

			if (mHasContent) {
				mEdgeBottom = mLastLineBottom - mLastYInView + mLastYInWindow;
			}
		}

		@Override
		public void onVisitBox(Box box, float left, float top, float right, float bottom) {
			OnClickedListener targetListener = getBoxOnClickedListener(box, mIsLongClicked);
			if (targetListener != mOnClickedListener) {
				if (mRectF != null) {
					mSelection.addSelectArea(mRectF);
					mRectF = null;
				}

				box.setSelected(false);
			} else {
				if (mRectF == null) {
					mRectF = new RectF(left, mLastLineTop, right, mLastLineBottom);
				}

				mHasContent = true;
				mRectF.right = right;
				mSelection.addBox(box);
				box.setSelected(true);
			}
		}
	}

	private interface Visitor {
		void onVisitParagraph(Paragraph paragraph);

		void onVisitParagraphEnd(Paragraph paragraph);

		void onVisitLine(Line line, float x, float y);

		void onVisitLineEnd(Line line, float x, float y);

		void onVisitBox(Box box, float left, float top, float right, float bottom);
	}
}
