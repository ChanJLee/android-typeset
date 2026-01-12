package me.chan.texas.ext.markdown.math.ast;

public class GreekLetterVariableAtom implements Atom {
	public final String name;  // 如 "alpha", "beta"
	public final String primeSuffix;

	public GreekLetterVariableAtom(String name) {
		this(name, "");
	}

	public GreekLetterVariableAtom(String name, String primeSuffix) {
		this.name = "\\" +name;
		this.primeSuffix = primeSuffix;
	}

	@Override
	public String toString() {
		return name + primeSuffix;
	}
}