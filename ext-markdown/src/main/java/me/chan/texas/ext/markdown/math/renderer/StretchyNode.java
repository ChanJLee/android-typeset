package me.chan.texas.ext.markdown.math.renderer;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.fonts.Symbol;

public class StretchyNode extends RendererNode {
	private final SymbolNode mSymbol;
	private int mActualWidth;

	public StretchyNode(MathPaint.Styles styles, Symbol symbol) {
		super(styles);
		mSymbol = new SymbolNode(styles, symbol);
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		mSymbol.measure(paint);

		float height = RendererNode.getSize(heightSpec);
		float width = RendererNode.getSize(widthSpec);

		if (RendererNode.getMode(heightSpec) != RendererNode.EXACTLY) {
			height = mSymbol.getHeight();
		}
		mSymbol.layout(0);

		float left = mSymbol.getLeft();
		float right = mSymbol.getRight();

		mActualWidth = (int) Math.ceil(right - left);
		if (RendererNode.getMode(widthSpec) == RendererNode.UNSPECIFIED) {
			width = mActualWidth;
		}

		float dx = -left;
		mSymbol.translate(dx, 0);
		setMeasuredSize((int) Math.ceil(width), (int) Math.ceil(height));
	}

	@Override
	public float getBaseline() {
		return getCenterY();
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		int width = getWidth();

		float scaleX = 1f;
		float scaleY = getHeight() * 1.0f / mSymbol.getHeight();

		canvas.save();
		canvas.scale(width * 1.0f / mActualWidth, scaleY);

		mSymbol.draw(canvas, paint);

		canvas.restore();
	}

	@Override
	protected String toPretty() {
		return "stretchy";
	}
}
