package me.chan.texas.ext.markdown.math.renderer;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

public class SpaceNode extends RendererNode {

	private final int mWidth;
	private final int mHeight;
	
	public SpaceNode(MathPaint.Styles styles, int width, int height) {
		super(styles);
		mWidth = width;
		mHeight = height;
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		setMeasuredSize(mWidth, mHeight);
	}

	@Override
	public float getBaseline() {
		return getCenterX();
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		/* NOOP */
	}

	@Override
	protected String toPretty() {
		return "SPACE";
	}

	@Override
	protected void onDrawDebug(MathCanvas canvas, MathPaint paint) {
		/* NOOP */
	}
}
