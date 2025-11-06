package me.chan.texas.ext.markdown.math.ast;

public class VariableAtom implements Atom {
	public char name;

	public VariableAtom(char name) {
		this.name = name;
	}

	@Override
	public String toLatex() {
		return String.valueOf(name);
	}
}