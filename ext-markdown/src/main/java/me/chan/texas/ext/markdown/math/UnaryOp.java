package me.chan.texas.ext.markdown.math;

import me.chan.texas.ext.markdown.math.ast.Ast;

public class UnaryOp implements Ast {
	public final String op;

	public UnaryOp(String op) {
		this.op = op;
	}

	@Override
	public String toString() {
		return op;
	}
}
