package me.chan.texas.ext.markdown.math.ast;

import androidx.annotation.NonNull;

public class ExtensibleArrowAtom implements Atom {
	public final String command;      // "xrightarrow", "xleftarrow" 等
	public final MathList above;      // 必需：箭头上方的内容
	public final MathList below;      // 可选：箭头下方的内容

	public ExtensibleArrowAtom(String command, MathList above, MathList below) {
		this.command = command;
		this.above = above;
		this.below = below;
	}

	@NonNull
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\\").append(command);
		if (above != null) {
			stringBuilder.append("{");
			stringBuilder.append(above);
			stringBuilder.append("}");
		}
		if (below != null) {
			stringBuilder.append("{");
			stringBuilder.append(below);
			stringBuilder.append("}");
		}
		return stringBuilder.toString();
	}
}