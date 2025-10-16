package me.chan.texas.ext.markdown.math.renderer;

import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;

public abstract class RendererNode {
	private final float mScale;

	public RendererNode(float scale) {
		mScale = scale;
	}

	public final void measure(TexasPaint paint) {
		onMeasure(paint);
	}

	public final void layout(RectF bounds) {
		onLayout(bounds);
	}

	public final void draw(TexasCanvas canvas) {
		onDraw(canvas);
	}

	protected abstract void onMeasure(TexasPaint paint);

	protected abstract void onLayout(RectF bounds);

	protected abstract void onDraw(TexasCanvas canvas);

	public static final RendererNode EMPTY = new RendererNode(1) {
		@Override
		protected void onMeasure(TexasPaint paint) {

		}

		@Override
		protected void onLayout(RectF bounds) {

		}

		@Override
		protected void onDraw(TexasCanvas canvas) {

		}
	};
}
