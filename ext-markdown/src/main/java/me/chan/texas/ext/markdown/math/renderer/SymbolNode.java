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
		float textSize = paint.getTextSize();
		mAxisHeight = MathFontOptions.AXIS_HEIGHT / MathFontOptions.UNITS_PER_EM * textSize;

		int height;
		if ((mSymbol.flags & Symbol.FLAG_USE_CONST_TEXT_HEIGHT) == 0) {
			paint.getFontMetrics(mFontMetrics);
			height = (int) Math.ceil(mFontMetrics.descent - mFontMetrics.ascent);
		} else {
			height = (int) Math.ceil(MathFontOptions.ACCENT_BASE_HEIGHT / MathFontOptions.UNITS_PER_EM * textSize);
		}

		if ((mSymbol.flags & Symbol.FLAG_INCLUDE_PADDING) != 0) {
			height += (int) Math.ceil(mAxisHeight * 2);
		} else {
			height -= (int) Math.ceil(mAxisHeight);
		}

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
		float y = getHeight();
		if ((mSymbol.flags & Symbol.FLAG_USE_BASELINE) != 0) {
			y -= mAxisHeight;
		}
		canvas.drawText(mSymbol.unicode, 0, y, paint);
	}

	@Override
	protected String toPretty() {
		return "symbol: " + mSymbol.unicode;
	}
}
