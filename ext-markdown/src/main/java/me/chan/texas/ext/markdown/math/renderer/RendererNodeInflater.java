package me.chan.texas.ext.markdown.math.renderer;

import android.util.Log;

import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

	public static RendererNode mockDecor() {
		DecorGroupNode.Builder builder = new DecorGroupNode.Builder(3, new TextNode(3, "∫"));
		builder.top(new TextNode(1, "x + y"));
		builder.bottom(new TextNode(1, "y + z"));
		builder.leftTop(new TextNode(1, "z + y"));
		builder.leftBottom(new TextNode(1, "y + x"));
		builder.rightTop(new TextNode(1, "x + z"));
		builder.rightBottom(new TextNode(1, "z + x"));
		return builder.build();
	}

	public static RendererNode mockList() {
		List<RendererNode> list = new ArrayList<>();
		for (Map.Entry<String, String> entry : MathFontOptions.toMap().entrySet()) {
			list.add(new TextNode(1, entry.getKey()));
			list.add(new TextNode(1, entry.getValue()));
			Log.d("chan_debug", "key: " + entry.getKey() + " value: " + entry.getValue());
		}
		return new LinearGroupNode(1, list, LinearGroupNode.Gravity.HORIZONTAL);
	}

	@VisibleForTesting
	public static SqrtNode mockSqrt(float scale, RendererNode content) {
		return new SqrtNode(scale, content, mockText(scale * 0.3f, "x + y + z"));
	}
}
