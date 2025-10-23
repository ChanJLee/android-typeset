package me.chan.texas.ext.markdown.math.ast;

public class BinOpAtom implements Atom {
	private final String op;

	public BinOpAtom(String op) {
		this.op = op;
	}

	public String getOp() {
		return op;
	}

	@Override
	public String toLatex() {
		return op;
	}
}
