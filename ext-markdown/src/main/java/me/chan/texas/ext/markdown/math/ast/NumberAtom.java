package me.chan.texas.ext.markdown.math.ast;

public class NumberAtom implements Atom {
	public final String value;

	public NumberAtom(String value) {
		this.value = value;
	}

	@Override
	public String toLatex() {
		return value;
	}
}