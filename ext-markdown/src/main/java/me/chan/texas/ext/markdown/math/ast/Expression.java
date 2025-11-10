package me.chan.texas.ext.markdown.math.ast;

import java.util.List;

/**
 * Expression节点：表示由term和二元运算符组成的表达式
 * <expression> ::= <term> { <binary_op> <term> }
 */
public class Expression implements Ast {
	private final List<Ast> elements;

	public Expression(List<Ast> elements) {
		this.elements = elements;
	}

	public List<Ast> getElements() {
		return elements;
	}

	@Override
	public String toString() {
		return "Expression(" + elements + ")";
	}
}