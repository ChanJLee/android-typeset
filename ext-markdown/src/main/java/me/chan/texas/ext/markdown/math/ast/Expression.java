package me.chan.texas.ext.markdown.math.ast;

import java.util.List;

/**
 * Expression节点：表示由term和二元运算符组成的表达式
 * <expression> ::= <term> { <binary_op> <term> }
 */
public class Expression implements Ast {
	public final List<Ast> elements;

	public Expression(List<Ast> elements) {
		this.elements = elements;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < elements.size(); i++) {
			Ast element = elements.get(i);
			stringBuilder.append(element.toString());
		}
		return stringBuilder.toString();
	}
}