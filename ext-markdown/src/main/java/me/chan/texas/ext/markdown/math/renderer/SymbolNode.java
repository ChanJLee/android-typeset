package me.chan.texas.ext.markdown.math.renderer;

import android.graphics.Paint;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

public class SymbolNode extends RendererNode {
	private final String mContent;
	private final Paint.FontMetrics mFontMetrics = new Paint.FontMetrics();
	private final boolean mIncludePadding;

	public SymbolNode(String content) {
		this(1, content);
	}

	public SymbolNode(float scale, String content) {
		this(scale, content, false);
	}

	public SymbolNode(float scale, String content, boolean includePadding) {
		super(scale);
		mContent = content;
		mIncludePadding = includePadding;
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		paint.getFontMetrics(mFontMetrics);
		Paint.FontMetrics fontMetrics = mFontMetrics;
		float baselineOffset = MathFontOptions.AXIS_HEIGHT / MathFontOptions.UNITS_PER_EM * paint.getTextSize();
		int height = (int) Math.ceil(fontMetrics.descent - fontMetrics.ascent);
		if (mIncludePadding) {
			height += (int) Math.ceil(baselineOffset * 2);
		} else {
			height -= (int) Math.ceil(baselineOffset);
		}

		int size = mContent.length();
		int width = (int) Math.ceil(paint.getRunAdvance(mContent, 0, size, 0, size, false, size));

		setMeasuredSize(width, height);
	}

	@Override
	public float getBaseline() {
		return getCenterY();
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		canvas.drawText(mContent, 0, getHeight(), paint);
	}

	@Override
	protected String toPretty() {
		return "symbol: " + mContent;
	}
}
