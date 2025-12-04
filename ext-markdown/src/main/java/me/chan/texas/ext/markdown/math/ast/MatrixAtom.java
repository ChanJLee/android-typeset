package me.chan.texas.ext.markdown.math.ast;

import java.util.List;

public class MatrixAtom implements Atom {
	public final String env;
	public final String gravity;
	public final List<MatrixRow> rows;

	public MatrixAtom(String env, String gravity, List<MatrixRow> rows) {
		this.env = env;
		this.gravity = gravity;
		this.rows = rows;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\\begin{").append(env).append("}\n");
		for (MatrixRow row : rows) {
			stringBuilder.append(row.toString());
			stringBuilder.append("\n");
		}
		stringBuilder.append("\\end{").append(env).append("}\n");
		return stringBuilder.toString();
	}
}
