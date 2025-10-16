package me.chan.texas.ext.markdown.math.renderer;

import androidx.annotation.Nullable;

import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;

public class SqrtNode extends RendererNode {
	private final RendererNode mContent;

	@Nullable
	private final RendererNode mRoot;

	public SqrtNode(float scale, RendererNode content, @Nullable RendererNode root) {
		super(scale);
		mContent = content;
		mRoot = root;
	}

	@Override
	protected void onMeasure(TexasPaint paint) {

	}

	@Override
	protected void onLayout(RectF bounds) {

	}

	@Override
	protected void onDraw(TexasCanvas canvas) {

	}
}
