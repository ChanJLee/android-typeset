package me.chan.texas.ext.markdown.math.ast;

import java.util.List;

public class MathList implements Ast {
	public final List<Ast> elements;

	public MathList(List<Ast> ast) {
		this.elements = ast;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < elements.size(); i++) {
			sb.append(elements.get(i).toString());
		}
		return sb.toString();
	}
}
