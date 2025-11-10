package me.chan.texas.ext.markdown.math.ast;

import java.util.List;

public class MatrixAtom implements Atom {
	public final String env;
	public final List<MatrixRow> rows;

	public MatrixAtom(String env, List<MatrixRow> rows) {
		this.env = env;
		this.rows = rows;
	}

	@Override
	public String toLatex() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\\begin{").append(env).append("}\n");
		for (MatrixRow row : rows) {
			stringBuilder.append(row.toLatex());
		}
		stringBuilder.append("\\end{").append(env).append("}\n");
		return stringBuilder.toString();
	}
}
