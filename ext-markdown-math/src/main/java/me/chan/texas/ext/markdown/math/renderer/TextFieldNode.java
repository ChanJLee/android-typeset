package me.chan.texas.ext.markdown.math.renderer;

import android.graphics.Paint;

import me.chan.texas.annotations.Internal;
import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.utils.IntArray;

@Internal
public class TextFieldNode extends RendererNode {
	private final String mText;
	private final IntArray mBreakPoints = new IntArray();
	private final Paint.FontMetrics mFontMetrics = new Paint.FontMetrics();
	private float mBaselineOffset;
	private int mLineHeight;

	public TextFieldNode(MathPaint.Styles styles, String text) {
		super(styles);
		mText = text;

		mBreakPoints.add(0);

		for (int i = 0; i < text.length(); ++i) {
			char c = text.charAt(i);
			if (c == '\n') {
				mBreakPoints.add(i + 1);
			}
		}

		mBreakPoints.add(text.length());
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		paint.getFontMetrics(mFontMetrics);
		Paint.FontMetrics fontMetrics = mFontMetrics;
		mLineHeight = (int) Math.ceil(fontMetrics.descent - fontMetrics.ascent);
		mBaselineOffset = (float) Math.ceil(fontMetrics.descent);

		float width = 0;
		int start = mBreakPoints.get(0);
		for (int i = 1; i < mBreakPoints.size(); ++i) {
			int end = mBreakPoints.get(i);
			width = Math.max(width,
					paint.getRunAdvance(mText, start, end, start, end, false, end - start)
			);
			start = end;
		}

		setMeasuredSize((int) Math.ceil(width), mLineHeight * (mBreakPoints.size() - 1));
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		int start = mBreakPoints.get(0);
		for (int i = 1; i < mBreakPoints.size(); ++i) {
			int end = mBreakPoints.get(i);
			canvas.drawText(mText, start, end, 0, mLineHeight * i - mBaselineOffset, paint);
			start = end;
		}
	}

	@Override
	protected String toPretty() {
		return mText;
	}
}
