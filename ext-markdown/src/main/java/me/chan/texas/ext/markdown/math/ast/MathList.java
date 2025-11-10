package me.chan.texas.ext.markdown.math.ast;

import java.util.List;

public class MathList implements Ast {
	List<Ast> ast;

	public MathList(List<Ast> ast) {
		this.ast = ast;
	}

	public List<Ast> getAst() {
		return ast;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ast.size(); i++) {
			sb.append(ast.get(i).toString());
		}
		return sb.toString();
	}
}
