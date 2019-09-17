package me.chan.te.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

import me.chan.te.annotations.Hidden;
import me.chan.te.data.Box;
import me.chan.te.data.Gravity;
import me.chan.te.data.Line;
import me.chan.te.data.LineAttribute;
import me.chan.te.data.LineAttributes;
import me.chan.te.data.Paragraph;
import me.chan.te.log.Log;

@Hidden
public class TeTextView extends View implements GestureDetector.OnGestureListener {
	private Paragraph mParagraph;
	private LineAttributes mLineAttributes;
	private Paint mPaint;
	private Paint mDebugPaint;

	public TeTextView(Context context) {
		super(context);
	}

	public TeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void render(@NonNull Paragraph paragraph,
					   @NonNull LineAttributes lineAttributes,
					   @NonNull Paint paint) {
		mParagraph = paragraph;
		mLineAttributes = lineAttributes;
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

	private boolean mSelectable = false;
	private GestureDetector mGestureDetector = null;

	public boolean isSelectable() {
		return mSelectable;
	}

	public void setSelectable(boolean selectable) {
		mSelectable = selectable;
		if (mGestureDetector == null) {
			mGestureDetector = new GestureDetector(getContext(), this);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!mSelectable ||
				mParagraph == null ||
				mParagraph.getLines() == null) {
			return super.onTouchEvent(event);
		}

		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mParagraph != null && mParagraph.getLines() != null &&
				!mParagraph.getLines().isEmpty()) {
			List<Line> lines = mParagraph.getLines();
			int height = getPaddingTop() + getPaddingBottom();
			for (int i = 0; i < lines.size(); ++i) {
				Line line = lines.get(i);
				height += line.getLineHeight();
				height += mLineAttributes.get(i).getLineVerticalSpace();
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
				mLineAttributes == null ||
				mPaint == null) {
			return;
		}

		float y = getPaddingTop();
		float width = getWidth();

		List<Line> lines = mParagraph.getLines();
		for (int i = 0; i < lines.size(); ++i) {
			Line line = lines.get(i);
			y += line.getLineHeight();
			float x = getPaddingLeft();
			LineAttribute lineAttribute = mLineAttributes.get(i);
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
		List<Box<?>> boxes = line.getBoxes();

		if (mDebugMode) {
			d("=========================");
		}

		for (int i = 0; i < boxes.size(); ++i) {
			Box<?> box = boxes.get(i);

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

			offsetY = (nextOffsetY + mLineAttributes.get(lineNumber).getLineVerticalSpace());
		}

		if (targetLine == null) {
			return false;
		}

		List<Box<?>> boxes = targetLine.getBoxes();

		int boxSize = boxes.size();
		float offsetX = getPaddingLeft();
		Box<?> target = null;
		for (int i = 0; i < boxSize; ++i) {
			Box<?> box = boxes.get(i);

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

		d("on clicked: " + target.getText());
		return true;
	}

	private boolean handleClickedPenaltyBox(Box<?> current, List<Line> lines, int nextLineNumber) {
		if (nextLineNumber < 0 || nextLineNumber >= lines.size()) {
			return false;
		}

		List<Box<?>> boxes = lines.get(nextLineNumber).getBoxes();
		if (boxes == null || boxes.isEmpty()) {
			return false;
		}

		Box<?> suffix = boxes.get(0);
		String prefix = current.getText();
		if (prefix != null && prefix.length() >= 1) {
			prefix = prefix.substring(0, prefix.length() - 1);
		}

		d("on clicked: " + (prefix + suffix.getText()));
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
		return handleClicked(e.getX(), e.getY());
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		/* do nothing */
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
