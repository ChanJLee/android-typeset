package me.chan.te.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import me.chan.te.annotations.Hidden;
import me.chan.te.data.Box;
import me.chan.te.data.TextBox;
import me.chan.te.text.Background;
import me.chan.te.text.Foreground;
import me.chan.te.text.Line;
import me.chan.te.text.Paragraph;
import me.chan.te.log.Log;
import me.chan.te.text.Gravity;
import me.chan.te.text.TextStyle;

@Hidden
public class ParagraphView extends View implements GestureDetector.OnGestureListener {
	public static final int SELECTION_MODE_NONE = 0;
	public static final int SELECTION_MODE_CLICK = 1;
	public static final int SELECTION_MODE_LONG_PRESS = 2;

	private Paragraph mParagraph;
	private TextPaint mPaint;
	private TextPaint mWorkPaint = new TextPaint();
	private Paint mDebugPaint;
	private int mSelectionMode = SELECTION_MODE_NONE;
	private GestureDetector mGestureDetector = null;
	private OnTextSelectedListener mOnTextSelectedListener;
	private Box mSelectedBox;
	private Box mSelectedSuffix;
	private boolean mDebugMode = false;
	private float mLineSpaceVertical = 0;
	private float mDescent;

	public ParagraphView(Context context) {
		super(context);
	}

	public ParagraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ParagraphView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	// TODO opt
	void render(@NonNull Paragraph paragraph,
				@NonNull TextPaint paint) {
		mParagraph = paragraph;
		mPaint = paint;
		mLineSpaceVertical = paint.getFontSpacing();
		mDescent = paint.getFontMetrics().descent;
		requestLayout();
	}

	public void setDebugMode(boolean enable) {
		if (mDebugMode == enable) {
			return;
		}

		mDebugMode = enable;
		if (mDebugMode && mDebugPaint == null) {
			mDebugPaint = new Paint();
			mDebugPaint.setColor(Color.GREEN);
			mDebugPaint.setStyle(Paint.Style.FILL);
			mDebugPaint.setTextSize(40);
		}
		invalidate();
	}

	public boolean isDebugMode() {
		return mDebugMode;
	}

	public boolean isSelectable() {
		return mSelectionMode != SELECTION_MODE_NONE;
	}

	public void setSelectionMode(int mode) {
		if (mode != SELECTION_MODE_NONE &&
				mode != SELECTION_MODE_CLICK &&
				mode != SELECTION_MODE_LONG_PRESS) {
			throw new IllegalArgumentException("invalid selection mode:" + mode);
		}

		mSelectionMode = mode;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isSelectable() ||
				mParagraph == null) {
			return super.onTouchEvent(event);
		}

		if (mGestureDetector == null) {
			mGestureDetector = new GestureDetector(getContext(), this);
		}
		mGestureDetector.setIsLongpressEnabled(mSelectionMode == SELECTION_MODE_LONG_PRESS);
		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int lineCount = 0;
		if (mParagraph != null && (lineCount = mParagraph.getLineCount()) != 0) {
			int height = 0;
			for (int i = 0; i < lineCount; ++i) {
				Line line = mParagraph.getLine(i);
				height += line.getLineHeight();
			}

			if (lineCount > 1) {
				height += ((lineCount - 1) * mLineSpaceVertical);
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
		if (mParagraph == null || (lineCount = mParagraph.getLineCount()) == 0) {
			return;
		}

		float y = 0;
		float width = getWidth();

		for (int i = 0; i < lineCount; ++i) {

			Line line = mParagraph.getLine(i);
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

			draw(canvas, line, x, y, mLineSpaceVertical);

			y += mLineSpaceVertical;
		}
	}

	private void draw(Canvas canvas, Line line, float x, float y, float lineSpace) {
		float spaceWidth = line.getSpaceWidth();
		int boxSize = line.getCount();

		for (int i = 0; i < boxSize; ++i) {
			Box box = line.getBox(i);
			float width = box.getWidth();

			float left = x;
			float right = (float) Math.ceil(x + width);
			float top = (float) Math.ceil(y - line.getLineHeight());
			float bottom = y + mDescent * 1.1f;

			if (box == mSelectedBox || box == mSelectedSuffix) {
				mWorkPaint.set(mPaint);
				mWorkPaint.setColor(Color.BLUE);
				canvas.drawRect(left, top, right, bottom, mWorkPaint);
				mWorkPaint.setColor(Color.WHITE);
			} else {
				if (box instanceof TextBox) {
					TextBox textBox = (TextBox) box;
					Background background = textBox.getBackground();
					if (background != null) {
						mWorkPaint.set(mPaint);
						background.draw(canvas, mWorkPaint, left, top, right, bottom);
					}
				}
			}

			if (mDebugMode) {
				mDebugPaint.setColor(Color.GREEN);
				canvas.drawRect(x, (float) Math.ceil(y - line.getLineHeight()),
						(float) Math.ceil(x + width), y, mDebugPaint);
			}

			mWorkPaint.set(mPaint);

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

		if (mDebugMode) {
			float startX = 0;
			float startY = y + lineSpace;
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

	private boolean handleClicked(float x, float y) {
		int lineCount = 0;
		if (mParagraph == null || (lineCount = mParagraph.getLineCount()) == 0) {
			return false;
		}

		Line targetLine = null;
		float offsetY = 0;
		int lineNumber = 0;
		for (; lineNumber < lineCount; ++lineNumber) {
			Line line = mParagraph.getLine(lineNumber);
			float nextOffsetY = offsetY + line.getLineHeight();
			if (offsetY <= y && y <= nextOffsetY) {
				targetLine = line;
				break;
			}

			offsetY = (nextOffsetY + mLineSpaceVertical);
		}

		if (targetLine == null) {
			return false;
		}

		int boxSize = targetLine.getCount();
		float spaceWidth = targetLine.getSpaceWidth();

		float offsetX = 0;
		Box target = null;
		for (int i = 0; i < boxSize; ++i) {
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

		if (target.isPenalty() || target.isSplit()) {
			return handleClickedPenaltyBox(target, lineNumber + 1);
		}

		mSelectedBox = target;
		mSelectedSuffix = null;
		if (mOnTextSelectedListener != null) {
			mOnTextSelectedListener.onTextSelected(this, target, null);
		}
		invalidate();
		return true;
	}

	private boolean handleClickedPenaltyBox(Box current, int nextLineNumber) {
		int lineCount = mParagraph.getLineCount();
		if (nextLineNumber < 0 || nextLineNumber >= lineCount) {
			return false;
		}

		Line line = mParagraph.getLine(nextLineNumber);
		if (line.getCount() == 0) {
			return false;
		}

		Box suffix = line.getBox(0);
		mSelectedBox = current;
		mSelectedSuffix = suffix;
		if (mOnTextSelectedListener != null) {
			mOnTextSelectedListener.onTextSelected(this, current, suffix);
		}
		invalidate();
		return true;
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
		return mSelectionMode == SELECTION_MODE_CLICK && handleClicked(e.getX(), e.getY());
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		if (mSelectionMode == SELECTION_MODE_LONG_PRESS) {
			handleClicked(e.getX(), e.getY());
		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		/* do nothing */
		return false;
	}

	public void clearSelected() {
		mSelectedBox = mSelectedSuffix = null;
		invalidate();
	}

	public void setOnTextSelectedListener(OnTextSelectedListener onTextSelectedListener) {
		mOnTextSelectedListener = onTextSelectedListener;
	}

	public interface OnTextSelectedListener {
		void onTextSelected(ParagraphView view, Box box, @Nullable Box suffix);
	}

	private static void d(String msg) {
		Log.d("TeTextView", msg);
	}
}
