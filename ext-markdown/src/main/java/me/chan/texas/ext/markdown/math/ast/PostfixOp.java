package me.chan.texas.ext.markdown.math.ast;

public class PostfixOp implements Ast {
	public final String op;

	public PostfixOp(String op) {
		this.op = op;
	}

	@Override
	public String toString() {
		return op;
	}
}
