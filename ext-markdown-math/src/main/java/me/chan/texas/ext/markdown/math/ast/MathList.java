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
		Ast last = null;
		for (int i = 0; i < elements.size(); i++) {
			Ast ast = elements.get(i);
			if (!(last instanceof Spacing) && last != null) {
				sb.append(" ");
			}
			sb.append(ast);
			last = ast;
		}
		return sb.toString();
	}
}
