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
	private RectF mRectF = new RectF();

	private float mBaselineBelow;
	private RenderOption mRenderOption;

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
				RenderOption renderOption) {
		mParagraph = paragraph;
		mPaint = paint;
		Paint.FontMetrics fontMetrics = paint.getFontMetrics();
		mBaselineBelow = fontMetrics.bottom;
		mRenderOption = renderOption;
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
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int lineCount = 0;
		if (mParagraph == null || (lineCount = mParagraph.getLineCount()) == 0 ||
				mRenderOption == null) {
			return;
		}

		float y = 0;
		float width = getWidth();

		for (int i = 0; i < lineCount; ++i) {

			Paragraph.Line line = mParagraph.getLine(i);
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

			drawLine(canvas, line, x, y);

			y += mRenderOption.getLineSpace();
		}
	}

	private void drawLine(Canvas canvas, Paragraph.Line line, float x, float y) {
		float spaceWidth = line.getSpaceWidth();
		int boxSize = line.getCount();

		for (int i = 0; i < boxSize; ++i) {
			Box box = line.getBox(i);
			float width = box.getWidth();

			float left = x;
			float right = (float) Math.ceil(x + width);
			float top = (float) Math.ceil(y - line.getLineHeight());
			float bottom = y + mBaselineBelow;

			mWorkPaint.set(mPaint);

			if (box instanceof TextBox) {
				TextBox textBox = (TextBox) box;
				if (textBox.isSelected()) {
					mWorkPaint.setColor(mRenderOption.getSelectedBackgroundColor());
					mRectF.set(left, top, right, bottom);
					canvas.drawRoundRect(mRectF, mRectRadius, mRectRadius, mWorkPaint);
					mWorkPaint.setColor(mRenderOption.getSelectedTextColor());
				} else {
					Background background = textBox.getBackground();
					if (background != null) {
						mWorkPaint.set(mPaint);
						background.draw(canvas, mWorkPaint, left, top, right, bottom);
					}
				}
			}

			if (mRenderOption.isEnableDebug()) {
				mDebugPaint.setColor(Color.GREEN);
				canvas.drawRect(x, (float) Math.ceil(y - line.getLineHeight()),
						(float) Math.ceil(x + width), y, mDebugPaint);
			}

			if (box instanceof TextBox) {
				TextBox textBox = (TextBox) box;
				TextStyle textStyle = textBox.getTextStyle();

				if (textStyle != null) {
					textStyle.update(mWorkPaint);
				}
			}

			box.draw(canvas, mWorkPaint, x, y);

			if (box instanceof TextBox) {
				TextBox textBox = (TextBox) box;
				Foreground foreground = textBox.getForeground();
				if (foreground != null) {
					mWorkPaint.set(mPaint);
					foreground.draw(canvas, mWorkPaint, left, top, right, bottom);
				}
			}

			x += (spaceWidth + width);
		}

		if (mRenderOption.isEnableDebug()) {
			float startX = 0;
			float startY = y + mRenderOption.getLineSpace();
			Rect rect = new Rect();
			String debugInfo = line.getRatio() + " " + spaceWidth;
			mDebugPaint.getTextBounds(debugInfo, 0, debugInfo.length(), rect);
			mDebugPaint.setColor(Color.BLUE);
			rect.offset((int) startX, (int) startY);
			canvas.drawRect(rect, mDebugPaint);
			mDebugPaint.setColor(Color.RED);
			canvas.drawText(debugInfo, startX, startY, mDebugPaint);
		}
	}

	private interface Predicate {
		boolean isSelected(Box clickedBox, Box target);
	}

	private boolean handleMotion(MotionEvent e, boolean isLongClicked) {
		float x = e.getX();
		float y = e.getY();

		int lineCount = 0;
		if (mParagraph == null || (lineCount = mParagraph.getLineCount()) == 0) {
			return false;
		}

		Paragraph.Line targetLine = null;
		float offsetY = 0;
		int lineNum = 0;
		for (; lineNum < lineCount; ++lineNum) {
			Paragraph.Line line = mParagraph.getLine(lineNum);
			float nextOffsetY = offsetY + line.getLineHeight();
			if (offsetY <= y && y <= nextOffsetY) {
				targetLine = line;
				break;
			}

			offsetY = (nextOffsetY + mRenderOption.getLineSpace());
		}

		if (targetLine == null) {
			return false;
		}

		int boxSize = targetLine.getCount();
		float spaceWidth = targetLine.getSpaceWidth();

		float offsetX = 0;
		Box target = null;
		int i = 0;
		for (; i < boxSize; ++i) {
			Box box = targetLine.getBox(i);
			float width = box.getWidth();
			float nextOffsetX = offsetX + width;
			if (offsetX <= x && x <= nextOffsetX) {
				target = box;
				break;
			}

			offsetX = (nextOffsetX + spaceWidth);
		}

		if (target == null) {
			return false;
		}

		handleBoxClicked(e, lineNum, i, target, isLongClicked);
		invalidate();
		return true;
	}

	private void handleBoxClicked(MotionEvent e, int lineNum, int boxIndex,
								  Box target, boolean isLongClicked) {
		OnClickedListener onClickedListener = getBoxOnClickedListener(target, isLongClicked);
		if (onClickedListener == null) {
			return;
		}

		int frontLineNum = lineNum;
		int frontBoxIndex = boxIndex - 1;

		while (true) {
			Paragraph.Line line = null;
			if (frontBoxIndex < 0) {
				--frontLineNum;
				if (frontLineNum < 0 || frontLineNum >= mParagraph.getLineCount()) {
					break;
				}
				line = mParagraph.getLine(frontLineNum);
				frontBoxIndex = line.getCount() - 1;
			} else {
				line = mParagraph.getLine(frontLineNum);
			}

			while (frontBoxIndex >= 0) {

			}
		}
	}

	private OnClickedListener getBoxOnClickedListener(Box target, boolean isLongClicked) {
		if (isLongClicked) {

			if (!(target instanceof TextBox)) {
				return null;
			}

			return ((TextBox) target).getSpanOnClickedListener();
		}

		return target.getOnClickedListener();
	}

	private boolean checkPrecondition(Box target, boolean isLongClicked) {
	}

	private Predicate mSingleClickedPredicate = new Predicate() {

		@Override
		public boolean isSelected(Box clickedBox, Box target) {
			OnClickedListener onClickedListener = clickedBox.getOnClickedListener();
			return onClickedListener != null && onClickedListener == target.getOnClickedListener();
		}
	};

	private Predicate mLongClickedPredicate = new Predicate() {

		@Override
		public boolean isSelected(Box clickedBox, Box target) {
			if (!(clickedBox instanceof TextBox)) {
				return false;
			}

			if (!(target instanceof TextBox)) {
				return false;
			}

			TextBox lhs = (TextBox) clickedBox;
			OnClickedListener onClickedListener = lhs.getSpanOnClickedListener();
			if (onClickedListener == null) {
				return false;
			}

			TextBox rhs = (TextBox) target;
			return onClickedListener == rhs.getSpanOnClickedListener();
		}
	};

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
}
