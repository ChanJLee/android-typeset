package me.chan.texas.ext.markdown.math.renderer;

import android.graphics.Paint;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

public class TextNode extends RendererNode {

	private final String mContent;
	private final Paint.FontMetrics mFontMetrics = new Paint.FontMetrics();
	private float mBaselineOffset;

	public TextNode(float scale, String content) {
		super(scale);
		mContent = content;
	}

	@Override
	protected void onMeasure(MathPaint paint) {
		paint.getFontMetrics(mFontMetrics);
		Paint.FontMetrics fontMetrics = mFontMetrics;
		int height = (int) Math.ceil(fontMetrics.descent - fontMetrics.ascent);
		mBaselineOffset = (float) Math.ceil(fontMetrics.descent);

		int size = mContent.length();
		int width = (int) Math.ceil(paint.getRunAdvance(mContent, 0, size, 0, size, false, size));

		setMeasuredSize(width, height);
	}

	public void setBaselineOffset(float baselineOffset) {
		mBaselineOffset = baselineOffset;
	}

	public float getBaselineOffset() {
		return mBaselineOffset;
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		canvas.drawText(mContent, 0, getHeight() - mBaselineOffset, paint);
	}

	@Override
	protected String toPretty() {
		return "text: " + mContent;
	}
}
