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

import java.util.List;

import me.chan.te.annotations.Hidden;
import me.chan.te.data.Box;
import me.chan.te.data.Gravity;
import me.chan.te.data.Line;
import me.chan.te.config.LineAttribute;
import me.chan.te.config.LineAttributes;
import me.chan.te.data.Paragraph;
import me.chan.te.log.Log;

@Hidden
public class TexTextView extends View implements GestureDetector.OnGestureListener {
	public static final int SELECTION_MODE_NONE = 0;
	public static final int SELECTION_MODE_CLICK = 1;
	public static final int SELECTION_MODE_LONG_PRESS = 2;

	private Paragraph mParagraph;
	private TextPaint mPaint;
	private Paint mDebugPaint;
	private int mSelectionMode = SELECTION_MODE_NONE;
	private GestureDetector mGestureDetector = null;
	private OnTextSelectedListener mOnTextSelectedListener;
	private Box mSelectedBox;
	private Box mSelectedSuffix;
	private TextPaint mWorkPaint = new TextPaint();

	public TexTextView(Context context) {
		super(context);
	}

	public TexTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TexTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void render(@NonNull Paragraph paragraph,
					   @NonNull TextPaint paint) {
		mParagraph = paragraph;
		mPaint = paint;
		requestLayout();
	}

	private boolean mDebugMode = false;

	public void setDebugMode(boolean enable) {
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
				mParagraph == null ||
				mParagraph.getLines() == null) {
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
		if (mParagraph != null && mParagraph.getLines() != null &&
				!mParagraph.getLines().isEmpty()) {
			LineAttributes lineAttributes = mParagraph.getLineAttributes();
			List<Line> lines = mParagraph.getLines();
			int height = getPaddingTop() + getPaddingBottom();
			for (int i = 0; i < lines.size(); ++i) {
				Line line = lines.get(i);
				height += line.getLineHeight();
				height += lineAttributes.get(i).getLineVerticalSpace();
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
		if (mParagraph == null ||
				mParagraph.getLines() == null ||
				mPaint == null) {
			return;
		}

		float y = getPaddingTop();
		float width = getWidth();

		LineAttributes lineAttributes = mParagraph.getLineAttributes();
		List<Line> lines = mParagraph.getLines();
		for (int i = 0; i < lines.size(); ++i) {
			Line line = lines.get(i);
			y += line.getLineHeight();
			float x = getPaddingLeft();
			LineAttribute lineAttribute = lineAttributes.get(i);
			if (lineAttribute.getGravity() == Gravity.CENTER) {
				x = (width - lineAttribute.getLineWidth()) / 2f;
			} else if (lineAttribute.getGravity() == Gravity.RIGHT) {
				x = (width - lineAttribute.getLineWidth());
			}

			float lineSpace = lineAttribute.getLineVerticalSpace();
			draw(canvas, line, x, y, lineSpace);
			y += lineSpace;
		}
	}

	private void draw(Canvas canvas, Line line, float x, float y, float lineSpace) {
		List<Box> boxes = line.getBoxes();

		if (mDebugMode) {
			d("=========================");
		}

		for (int i = 0; i < boxes.size(); ++i) {
			Box box = boxes.get(i);

			if (box == mSelectedBox || box == mSelectedSuffix) {
				Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
				float left = x;
				float right = (float) Math.ceil(x + box.getWidth());
				float top = (float) Math.ceil(y - line.getLineHeight());
				float bottom = y + fontMetrics.descent * 1.1f;
				mPaint.setColor(Color.BLUE);
				canvas.drawRect(left, top, right, bottom, mPaint);
				mPaint.setColor(Color.WHITE);
			} else {
				mPaint.setColor(Color.BLACK);
			}

			if (mDebugMode) {
				mDebugPaint.setColor(Color.GREEN);
				canvas.drawRect(x, (float) Math.ceil(y - line.getLineHeight()), (float) Math.ceil(x + box.getWidth()), y, mDebugPaint);
				d(box.getText());
			}

			canvas.drawText(box.getText(), x, y, mPaint);
			x += (line.getSpaceWidth() + box.getWidth());
		}

		if (mDebugMode) {
			float startX = 0;
			float startY = y + lineSpace;
			Rect rect = new Rect();
			String ratio = String.valueOf(line.getRatio());
			mDebugPaint.getTextBounds(ratio, 0, ratio.length(), rect);
			mDebugPaint.setColor(Color.BLUE);
			rect.offset((int) startX, (int) startY);
			canvas.drawRect(rect, mDebugPaint);
			mDebugPaint.setColor(Color.RED);
			canvas.drawText(ratio, startX, startY, mDebugPaint);
		}
	}

	private boolean handleClicked(float x, float y) {
		if (mParagraph == null || mParagraph.getLines() == null) {
			return false;
		}

		LineAttributes lineAttributes = mParagraph.getLineAttributes();
		List<Line> lines = mParagraph.getLines();
		int size = lines.size();
		Line targetLine = null;
		float offsetY = getPaddingTop();
		int lineNumber = 0;
		for (; lineNumber < size; ++lineNumber) {
			Line line = lines.get(lineNumber);
			float nextOffsetY = offsetY + line.getLineHeight();
			if (offsetY <= y && y <= nextOffsetY) {
				targetLine = line;
				break;
			}

			offsetY = (nextOffsetY + lineAttributes.get(lineNumber).getLineVerticalSpace());
		}

		if (targetLine == null) {
			return false;
		}

		List<Box> boxes = targetLine.getBoxes();

		int boxSize = boxes.size();
		float offsetX = getPaddingLeft();
		Box target = null;
		for (int i = 0; i < boxSize; ++i) {
			Box box = boxes.get(i);

			float nextOffsetX = offsetX + box.getWidth();
			if (offsetX <= x && x <= nextOffsetX) {
				target = box;
				break;
			}

			offsetX = (nextOffsetX + targetLine.getSpaceWidth());
		}

		if (target == null) {
			return false;
		}

		if (target.isPenalty()) {
			return handleClickedPenaltyBox(target, lines, lineNumber + 1);
		}

		mSelectedBox = target;
		mSelectedSuffix = null;
		if (mOnTextSelectedListener != null) {
			mOnTextSelectedListener.onTextSelected(this, target, null);
		}
		invalidate();
		return true;
	}

	private boolean handleClickedPenaltyBox(Box current, List<Line> lines, int nextLineNumber) {
		if (nextLineNumber < 0 || nextLineNumber >= lines.size()) {
			return false;
		}

		List<Box> boxes = lines.get(nextLineNumber).getBoxes();
		if (boxes == null || boxes.isEmpty()) {
			return false;
		}

		Box suffix = boxes.get(0);
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
		void onTextSelected(TexTextView view, Box box, @Nullable Box suffix);
	}

	private static void d(String msg) {
		Log.d("TeTextView", msg);
	}
}
