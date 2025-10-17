package me.chan.texas.ext.markdown.math.renderer;

import androidx.annotation.VisibleForTesting;

import me.chan.texas.ext.markdown.math.ast.MathList;

public class RendererNodeInflater {

	public RendererNode inflate(MathList list) {
		throw new RuntimeException("Stub!");
	}

	@VisibleForTesting
	public static RendererNode mockText(String text) {
		return new TextNode(text);
	}

	@VisibleForTesting
	public static RendererNode mockSqrt() {
		return new SqrtNode(mockText("x + y"), mockText("x + y + z"));
	}
}
