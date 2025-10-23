package me.chan.texas.ext.markdown.math.ast;

import java.util.List;

public class MathList implements Ast {
	List<Ast> ast;

	public MathList(List<Ast> ast) {
		this.ast = ast;
	}

	public List<Ast> getAtoms() {
		return ast;
	}

	@Override
	public String toLatex() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ast.size(); i++) {
			sb.append(ast.get(i).toLatex());
		}
		return sb.toString();
	}
}
