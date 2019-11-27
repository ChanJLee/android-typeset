package me.chan.te.renderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import me.chan.te.annotations.Hidden;
import me.chan.te.log.Log;
import me.chan.te.text.Background;
import me.chan.te.text.Box;
import me.chan.te.text.Foreground;
import me.chan.te.text.Gravity;
import me.chan.te.text.OnClickedListener;
import me.chan.te.text.Paragraph;
import me.chan.te.text.TextBox;
import me.chan.te.text.TextStyle;

@Hidden
public class ParagraphView extends View implements GestureDetector.OnGestureListener {
	private static final int DEFAULT_ROUND_RADIUS = 3;

	private Paragraph mParagraph;
	private TextPaint mPaint;
	private TextPaint mWorkPaint = new TextPaint();
	private Paint mDebugPaint;
	private GestureDetector mGestureDetector = null;
	private float mRectRadius;
	private Selection mSelection;

	private float mBaselineBelow;
	private RenderOption mRenderOption;
	private OnSelectionCreateListener mSelectionCreateListener;


	public ParagraphView(Context context) {
		this(context, null);
	}

	public ParagraphView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ParagraphView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mRectRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_ROUND_RADIUS, getResources().getDisplayMetrics());
	}

	// TODO opt
	void render(@NonNull Paragraph paragraph,
				@NonNull TextPaint paint,
				RenderOption renderOption,
				Selection selection) {
		mParagraph = paragraph;
		mPaint = paint;
		Paint.FontMetrics fontMetrics = paint.getFontMetrics();
		mBaselineBelow = fontMetrics.bottom;
		mRenderOption = renderOption;
		setDebugMode(renderOption.isEnableDebug());
		mSelection = selection;
		requestLayout();
	}

	void setSelectionCreateListener(OnSelectionCreateListener selectionCreateListener) {
		mSelectionCreateListener = selectionCreateListener;
	}

	private void setDebugMode(boolean enable) {
		if (enable && mDebugPaint == null) {
			mDebugPaint = new Paint();
			mDebugPaint.setColor(Color.GREEN);
			mDebugPaint.setStyle(Paint.Style.FILL);
			mDebugPaint.setTextSize(40);
		}
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
			int height = 0;
			for (int i = 0; i < lineCount; ++i) {
				Paragraph.Line line = mParagraph.getLine(i);
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

		if (mSelection != null && mSelection.getParagraph() == mParagraph) {
			mWorkPaint.set(mPaint);
			mWorkPaint.setColor(mRenderOption.getSelectedBackgroundColor());
			mSelection.draw(canvas, mWorkPaint, mRectRadius);
		}

		mDrawVisitor.setCanvas(canvas);
		visitParagraph(mParagraph, mDrawVisitor);
	}

	private boolean handleMotion(MotionEvent e, boolean isLongClicked) {
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

		OnClickedListener onClickedListener = getBoxOnClickedListener(target, isLongClicked);
		if (onClickedListener == null) {
			return false;
		}


		Selection selection = getIfParagraphSelection(mParagraph, onClickedListener, isLongClicked);
		mParagraph.setSelection(selection);
		// TODO 丢给外部处理
		mSelection = selection;

		Paragraph paragraph = mParagraph.getNext();
		if (paragraph != null) {
			selection = getIfParagraphSelection(paragraph, onClickedListener, isLongClicked);
			paragraph.setSelection(selection);
		}

		paragraph = mParagraph.getPrev();
		if (paragraph != null) {
			selection = getIfParagraphSelection(paragraph, onClickedListener, isLongClicked);
			paragraph.setSelection(selection);
		}

		if (mSelectionCreateListener != null) {
			mSelectionCreateListener.onSelectionCreated(mSelection);
		}

		invalidate();
		return true;
	}

	private Selection getIfParagraphSelection(Paragraph paragraph,
											OnClickedListener onClickedListener,
											boolean isLongClicked) {
		mSelectionVisitor.setOnClickedListener(onClickedListener);
		mSelectionVisitor.setLongClicked(isLongClicked);
		visitParagraph(paragraph, mSelectionVisitor);
		Selection selection = mSelectionVisitor.getSelection();
		mSelectionVisitor.clear();
		return selection;
	}

	private void visitParagraph(Paragraph paragraph, Visitor visitor) {
		visitor.onVisitParagraph(paragraph);
		float y = 0;
		float width = getWidth();
		int lineCount = paragraph.getLineCount();
		for (int i = 0; i < lineCount; ++i) {

			Paragraph.Line line = paragraph.getLine(i);
			y += line.getLineHeight();

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

	private void visitLine(Paragraph.Line line, float x, float y, Visitor visitor) {
		visitor.onVisitLine(line, x, y);

		float spaceWidth = line.getSpaceWidth();
		int boxSize = line.getCount();
		for (int i = 0; i < boxSize; ++i) {
			Box box = line.getBox(i);
			float width = box.getWidth();

			float left = x;
			float right = (float) Math.ceil(x + width);
			float top = (float) Math.ceil(y - line.getLineHeight());
			float bottom = y + mBaselineBelow;
			visitBox(box, left, top, right, bottom, visitor);
			x += (spaceWidth + width);
		}

		visitor.onVisitLineEnd(line, x, y);
	}

	private void visitBox(Box box, float left, float top, float right, float bottom, Visitor visitor) {
		visitor.onVisitBox(box, left, top, right, bottom);
	}

	private OnClickedListener getBoxOnClickedListener(Box target, boolean isLongClicked) {
		if (isLongClicked) {
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
		Log.d("TeTextView", msg);
	}

	public interface OnSelectionCreateListener {
		void onSelectionCreated(Selection selection);
	}

	private DrawVisitor mDrawVisitor = new DrawVisitor();

	private class DrawVisitor implements Visitor {
		private Canvas mCanvas;

		public void setCanvas(Canvas canvas) {
			mCanvas = canvas;
		}

		@Override
		public void onVisitParagraph(Paragraph paragraph) {
			/* */
		}

		@Override
		public void onVisitParagraphEnd(Paragraph paragraph) {

		}

		@Override
		public void onVisitLine(Paragraph.Line line, float x, float y) {
		}

		@Override
		public void onVisitLineEnd(Paragraph.Line line, float x, float y) {
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
			mWorkPaint.set(mPaint);

			if (box instanceof TextBox) {
				TextBox textBox = (TextBox) box;
				if (mSelection != null &&
						mSelection.getParagraph() == mParagraph &&
						box.isSelected()) {
					mWorkPaint.setColor(mRenderOption.getSelectedTextColor());
				} else {
					Background background = textBox.getBackground();
					if (background != null) {
						mWorkPaint.set(mPaint);
						background.draw(mCanvas, mWorkPaint, left, top, right, bottom);
					}
				}
			}

			if (mRenderOption.isEnableDebug()) {
				mDebugPaint.setColor(Color.GREEN);
				mCanvas.drawRect(left, top, right, bottom, mDebugPaint);
			}

			if (box instanceof TextBox) {
				TextBox textBox = (TextBox) box;
				TextStyle textStyle = textBox.getTextStyle();

				if (textStyle != null) {
					textStyle.update(mWorkPaint);
				}
			}

			float baseline = bottom - mBaselineBelow;
			box.draw(mCanvas, mWorkPaint, left, baseline);

			if (box instanceof TextBox) {
				TextBox textBox = (TextBox) box;
				Foreground foreground = textBox.getForeground();
				if (foreground != null) {
					mWorkPaint.set(mPaint);
					foreground.draw(mCanvas, mWorkPaint, left, top, right, bottom);
				}
			}
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
		public void onVisitLine(Paragraph.Line line, float x, float y) {

		}

		@Override
		public void onVisitLineEnd(Paragraph.Line line, float x, float y) {

		}

		@Override
		public void onVisitBox(Box box, float left, float top, float right, float bottom) {
			if ((left <= mX && mX <= right) &&
					(top <= mY && mY <= bottom)) {
				mBox = box;
			}
		}
	}

	private SelectionVisitor mSelectionVisitor = new SelectionVisitor();

	private class SelectionVisitor implements Visitor {

		private OnClickedListener mOnClickedListener;
		private boolean mHasContent;
		private Selection mSelection;
		private boolean mIsLongClicked;
		private RectF mRectF;

		@Override
		public void onVisitParagraph(Paragraph paragraph) {
			mSelection = new Selection(paragraph);
		}

		@Override
		public void onVisitParagraphEnd(Paragraph paragraph) {
			if (!mSelection.hasContent()) {
				mSelection = null;
			}
		}

		public void clear() {
			mOnClickedListener = null;
			mHasContent = false;
			mSelection = null;
			mIsLongClicked = false;
			mRectF = null;
		}

		public Selection getSelection() {
			return mSelection;
		}

		public void setOnClickedListener(OnClickedListener onClickedListener) {
			mOnClickedListener = onClickedListener;
		}

		public void setLongClicked(boolean longClicked) {
			mIsLongClicked = longClicked;
		}

		@Override
		public void onVisitLine(Paragraph.Line line, float x, float y) {
			mHasContent = false;
			mRectF = new RectF();
			mRectF.bottom = y + mBaselineBelow;
			mRectF.top = y - line.getLineHeight();
		}

		@Override
		public void onVisitLineEnd(Paragraph.Line line, float x, float y) {
			if (mHasContent) {
				mSelection.addSelectArea(mRectF);
			}
		}

		@Override
		public void onVisitBox(Box box, float left, float top, float right, float bottom) {
			OnClickedListener targetListener = getBoxOnClickedListener(box, mIsLongClicked);
			if (targetListener != mOnClickedListener) {
				box.setSelected(false);
			} else {
				if (!mHasContent) {
					mRectF.left = left;
				}

				mHasContent = true;
				mRectF.right = right;
				box.setSelected(true);
			}
		}
	}

	private interface Visitor {
		void onVisitParagraph(Paragraph paragraph);

		void onVisitParagraphEnd(Paragraph paragraph);

		void onVisitLine(Paragraph.Line line, float x, float y);

		void onVisitLineEnd(Paragraph.Line line, float x, float y);

		void onVisitBox(Box box, float left, float top, float right, float bottom);
	}
}
