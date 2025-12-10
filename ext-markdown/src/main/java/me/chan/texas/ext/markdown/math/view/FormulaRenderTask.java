package me.chan.texas.ext.markdown.math.view;

import me.chan.texas.ext.markdown.math.renderer.RendererNode;
import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.utils.concurrency.Worker;

public class FormulaRenderTask extends Worker.Task<FormulaRenderTask.RendererArgs, Void> {

	@Override
	protected Void onExec(Worker.Token token, RendererArgs args) throws Throwable {
		return null;
	}

	public static class RendererArgs {
		public final RendererNode rendererNode;
		public final MathPaint paint;

		public RendererArgs(RendererNode rendererNode, MathPaint paint /* make copy */) {
			this.rendererNode = rendererNode;
			this.paint = paint;
		}
	}
}
