package me.chan.texas.ext.markdown.math.ast;

public class VariableAtom implements Atom {
	public final String name;

	public VariableAtom(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}