package me.chan.texas.ext.markdown.math.ast;

public class LargeOperatorAtom implements Atom {
	public final String name;  // "sum", "int", "prod"

	public LargeOperatorAtom(String name) {
		this.name = "\\" + name;
	}

	@Override
	public String toString() {
		return name;
	}
}
