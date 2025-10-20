package me.chan.texas.ext.markdown.math.renderer;

import androidx.annotation.VisibleForTesting;

import me.chan.texas.ext.markdown.math.ast.MathList;

public class RendererNodeInflater {

	public RendererNode inflate(MathList list) {
		throw new RuntimeException("Stub!");
	}

	@VisibleForTesting
	public static RendererNode mockText(String text) {
		return new TextNode(1f, text);
	}

	@VisibleForTesting
	public static RendererNode mockText() {
		return new TextNode(1f, MathFontOptions.toList());
	}

	@VisibleForTesting
	public static RendererNode mockText(float scale, String text) {
		return new TextNode(scale, text);
	}

	@VisibleForTesting
	public static RendererNode mockSqrt() {
		SqrtNode node1 = mockSqrt(1f, mockText(1f, "x1 + y1"));
		return mockSqrt(2, node1);
//		return node1;
	}

	public static RendererNode mockFractionNode() {
		return new FractionNode(2, mockSqrt(), mockSqrt());
	}

	@VisibleForTesting
	public static SqrtNode mockSqrt(float scale, RendererNode content) {
		return new SqrtNode(scale, content, mockText(scale * 0.3f, "x + y + z"));
	}
}
