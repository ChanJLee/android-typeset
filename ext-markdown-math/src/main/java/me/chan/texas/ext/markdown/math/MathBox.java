package me.chan.texas.ext.markdown.math;

import me.chan.texas.ext.markdown.math.renderer.RendererNode;
import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathCanvasImpl;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaintImpl;
import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.text.HyperSpan;
import me.chan.texas.text.layout.StateList;

public class MathBox extends HyperSpan {
	private MathCanvas mCanvas;
	private MathPaint mPaint;

	private final RendererNode mRendererNode;

	public MathBox(RendererNode rendererNode) {
		mRendererNode = rendererNode;
	}

	@Override
	protected void onDraw(TexasCanvas canvas, TexasPaint paint, RectF inner, RectF outer, float baselineOffset, StateList states) {
		mPaint = new MathPaintImpl(paint);
		mCanvas = new MathCanvasImpl(canvas);
	}

	@Override
	protected void onMeasure(float lineHeight, float baselineOffset) {
		// todo
	}
}
