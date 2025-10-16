package me.chan.texas.ext.markdown.math.ast;

import java.util.List;

public class MathList implements MathNode {
	List<Term> terms;
	List<String> operators;

	public MathList(List<Term> terms, List<String> operators) {
		this.terms = terms;
		this.operators = operators;
	}

	@Override
	public String toLatex() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < terms.size(); i++) {
			sb.append(terms.get(i).toLatex());
			if (i < operators.size()) {
				sb.append(operators.get(i));
			}
		}
		return sb.toString();
	}
}
