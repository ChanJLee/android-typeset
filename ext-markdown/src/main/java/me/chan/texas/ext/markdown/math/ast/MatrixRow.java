package me.chan.texas.ext.markdown.math.ast;

import java.util.List;

public class MatrixRow implements Ast {
	private final List<Ast> elements;

	public MatrixRow(List<Ast> elements) {
		this.elements = elements;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		String join = " & ";
		for (Ast element : elements) {
			builder.append(element.toString());
			builder.append(join);
		}

		builder.delete(builder.length() - join.length(), builder.length());
		return builder.toString();
	}
}
