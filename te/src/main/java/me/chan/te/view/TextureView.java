package me.chan.te.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

import me.chan.te.annotations.Hidden;
import me.chan.te.data.Box;
import me.chan.te.data.Element;
import me.chan.te.data.Gravity;
import me.chan.te.data.Line;
import me.chan.te.data.LineAttribute;
import me.chan.te.data.LineAttributes;
import me.chan.te.data.Paragraph;

@Hidden
public class TextureView extends View {
	private Paragraph mParagraph;
	private LineAttributes mLineAttributes;
	private Paint mPaint;
	private Paint mDebugPaint;

	public TextureView(Context context) {
		super(context);
	}

	public TextureView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TextureView(Context context, AttributeSet attrs, int defStyleAttr) {
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
		}
		invalidate();
	}

	public boolean isDebugMode() {
		return mDebugMode;
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
				height += mLineAttributes.get(i).getLineSpace();
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

			draw(canvas, line, x, y);
			y += lineAttribute.getLineSpace();
		}
	}

	private void draw(Canvas canvas, Line line, float x, float y) {
		List<? extends Element> elements = line.getElements();
		for (int i = 0; i < elements.size(); ++i) {
			Element element = elements.get(i);
			if (!(element instanceof Box)) {
				continue;
			}

			Box<?> box = (Box<?>) element;
			if (box.isDoNotDraw()) {
				continue;
			}

			if (mDebugMode) {
				canvas.drawRect(x, (float) Math.ceil(y - line.getLineHeight()), (float) Math.ceil(x + box.getWidth()), y, mDebugPaint);
			}

			canvas.drawText(box.getText(), x, y, mPaint);
			x += (line.getSpaceWidth() + box.getWidth());
		}
	}
}
