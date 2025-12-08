package me.chan.texas.ext.markdown.math.renderer;

import android.graphics.Color;

import androidx.annotation.VisibleForTesting;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.fonts.MathFontOptions;
import me.chan.texas.ext.markdown.math.renderer.fonts.Symbol;

public class SymbolNode extends RendererNode {
	@VisibleForTesting
	final Symbol mSymbol;
	private float mOffsetY;
	private float mOffsetX;

	public SymbolNode(MathPaint.Styles styles, Symbol symbol) {
		super(styles);
		mSymbol = symbol;
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		float textSize = paint.getTextSize();
		mOffsetX = -mSymbol.xMin / MathFontOptions.UNITS_PER_EM * textSize;
		mOffsetY = mSymbol.ascent / MathFontOptions.UNITS_PER_EM * textSize;
		int width = (int) Math.ceil((mSymbol.xMax - mSymbol.xMin) / MathFontOptions.UNITS_PER_EM * textSize);
		int height = (int) Math.ceil((mSymbol.descent - mSymbol.ascent) / MathFontOptions.UNITS_PER_EM * textSize);

		setMeasuredSize(width, height);
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		float y = getHeight() + mOffsetY;
		canvas.drawText(mSymbol.unicode, mOffsetX, y, paint);
	}

	public final void layout(float top) {
		setLayoutBounds(-mOffsetX, top);
		onLayoutChildren();
	}

	@Override
	protected void onDrawDebug(MathCanvas canvas, MathPaint paint) {
		paint.setColor(Color.GREEN);
		super.onDrawDebug(canvas, paint);
		drawDebugBounds(canvas, paint);
	}

	@Override
	protected String toPretty() {
		return "symbol: " + mSymbol.unicode;
	}
}
