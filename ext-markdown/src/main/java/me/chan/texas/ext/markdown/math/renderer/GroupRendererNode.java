package me.chan.texas.ext.markdown.math.renderer;

import androidx.annotation.Nullable;

import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

public abstract class GroupRendererNode extends RendererNode {

	public GroupRendererNode(MathPaint.Styles styles) {
		super(styles);
	}

	@Nullable
	public abstract RendererNode optimize();
}
