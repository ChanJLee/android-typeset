package me.chan.texas.ext.markdown.math.renderer;

import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.core.graphics.TexasCanvas;

public abstract class MathNode {

	public final void measure() {
		onMeasure();
	}

	public final void layout(RectF bounds) {
		onLayout(bounds);
	}

	public final void draw(TexasCanvas canvas) {
		onDraw(canvas);
	}

	protected abstract void onMeasure();

	protected abstract void onLayout(RectF bounds);

	protected abstract void onDraw(TexasCanvas canvas);

	public static final MathNode EMPTY = new MathNode() {
		@Override
		protected void onMeasure() {

		}

		@Override
		protected void onLayout(RectF bounds) {

		}

		@Override
		protected void onDraw(TexasCanvas canvas) {

		}
	};
}
