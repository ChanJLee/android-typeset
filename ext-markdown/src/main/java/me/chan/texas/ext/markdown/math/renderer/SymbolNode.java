package me.chan.texas.ext.markdown.math.renderer;

import android.graphics.Paint;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.fonts.MathFontOptions;
import me.chan.texas.ext.markdown.math.renderer.fonts.Symbol;

public class SymbolNode extends RendererNode {
	private final Symbol mSymbol;
	private final Paint.FontMetrics mFontMetrics = new Paint.FontMetrics();
	private float mAxisHeight;

	public SymbolNode(float scale, Symbol symbol) {
		super(scale);
		mSymbol = symbol;
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		paint.getFontMetrics(mFontMetrics);
		Paint.FontMetrics fontMetrics = mFontMetrics;
		mAxisHeight = MathFontOptions.AXIS_HEIGHT / MathFontOptions.UNITS_PER_EM * paint.getTextSize();
		int height = (int) Math.ceil(fontMetrics.descent - fontMetrics.ascent);
//		if (mIncludePadding) {
//			height += (int) Math.ceil(baselineOffset * 2);
//		} else {
//			height -= (int) Math.ceil(baselineOffset);
//		}

		int size = mSymbol.unicode.length();
		int width = (int) Math.ceil(paint.getRunAdvance(mSymbol.unicode, 0, size, 0, size, false, size));

		setMeasuredSize(width, height);
	}

	@Override
	public float getBaseline() {
		return getCenterY();
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		canvas.drawText(mSymbol.unicode, 0, getHeight(), paint);
	}

	@Override
	protected String toPretty() {
		return "symbol: " + mSymbol.unicode;
	}
}
