package me.chan.texas.ext.markdown.math.renderer;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

public class LargeOpNode extends RendererNode {

	public LargeOpNode(float scale) {
		super(scale);
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
