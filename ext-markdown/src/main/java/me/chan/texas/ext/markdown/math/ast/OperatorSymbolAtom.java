package me.chan.texas.ext.markdown.math.ast;

public class OperatorSymbolAtom implements Atom {
	public final String op;

	public OperatorSymbolAtom(String op) {
		this.op = op;
	}

	@Override
	public String toString() {
		return op;
	}
}
