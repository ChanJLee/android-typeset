package me.chan.texas.ext.markdown.math.renderer;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

public class BraceNode extends RendererNode {

	public BraceNode(MathPaint.Styles styles) {
		super(styles);
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {

	}

	@Override
	public float getBaseline() {
		return 0;
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {

	}

	@Override
	protected String toPretty() {
		return "";
	}
}
