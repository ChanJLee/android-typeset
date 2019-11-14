package me.chan.te.renderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
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
	private static final int DEFAULT_ROUND_RADIUS = 3;

	private Paragraph mParagraph;
	private TextPaint mPaint;
	private TextPaint mWorkPaint = new TextPaint();
	private Paint mDebugPaint;
	private int mSelectionMode = SELECTION_MODE_LONG_PRESS;
	private GestureDetector mGestureDetector = null;
	private OnTextSelectedListener mOnTextSelectedListener;
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

			drawLine(canvas, line, x, y);

			y += mRenderOption.getLineSpace();
		}
	}

	private void drawLine(Canvas canvas, Line line, float x, float y) {
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

			offsetY = (nextOffsetY + mRenderOption.getLineSpace());
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

		if (target == null || !(target instanceof TextBox)) {
			return false;
		}

		TextBox textBox = (TextBox) target;
		if (textBox.isPenalty() || textBox.isSplit()) {
			return handleClickedPenaltyBox(textBox, lineNumber + 1);
		}

		textBox.setSelected(true);
		if (mOnTextSelectedListener != null) {
			mOnTextSelectedListener.onTextSelected(this, target, null);
		}
		invalidate();
		return true;
	}

	private boolean handleClickedPenaltyBox(TextBox current, int nextLineNumber) {
		int lineCount = mParagraph.getLineCount();
		if (nextLineNumber < 0 || nextLineNumber >= lineCount) {
			return false;
		}

		Line line = mParagraph.getLine(nextLineNumber);
		if (line.getCount() == 0) {
			return false;
		}

		Box suffix = line.getBox(0);
		if (!(suffix instanceof TextBox)) {
			suffix = null;
		} else {
			TextBox suffixTextBox = (TextBox) suffix;
			suffixTextBox.setSelected(true);
		}

		current.setSelected(true);
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
		if (mParagraph == null) {
			return;
		}

		int lineCount = mParagraph.getLineCount();
		for (int i = 0; i < lineCount; ++i) {
			Line line = mParagraph.getLine(i);
			int boxCount = line.getCount();
			for (int j = 0; j < boxCount; ++j) {
				Box box = line.getBox(j);
				if (box instanceof TextBox) {
					((TextBox) box).setSelected(false);
				}
			}
		}

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
