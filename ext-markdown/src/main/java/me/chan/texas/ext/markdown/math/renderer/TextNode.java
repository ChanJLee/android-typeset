package me.chan.texas.ext.markdown.math.renderer;

import android.graphics.Paint;

import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;

public class TextNode extends RendererNode {

	private final String mContent;
	private final Paint.FontMetrics mFontMetrics = new Paint.FontMetrics();
	private float mBaselineOffset;

	public TextNode(float scale, String content) {
		super(scale);
		mContent = content;
	}

	@Override
	protected void onMeasure(TexasPaint paint) {
		paint.getFontMetrics(mFontMetrics);
		Paint.FontMetrics fontMetrics = mFontMetrics;
		int height = (int) Math.ceil(fontMetrics.descent - fontMetrics.ascent + fontMetrics.leading);
		mBaselineOffset = (float) Math.ceil(fontMetrics.descent);

		int size = mContent.length();
		int width = (int) Math.ceil(paint.getRunAdvance(mContent, 0, size, 0, size, false, size));

		setMeasuredSize(width, height);
	}

	@Override
	protected void onDraw(TexasCanvas canvas, TexasPaint paint) {
		canvas.drawText(mContent, 0, getHeight() - mBaselineOffset, paint);
	}

	@Override
	protected String toPretty() {
		return "text: " + mContent;
	}
}
