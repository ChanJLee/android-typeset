package me.chan.texas.ext.markdown.math.ast;

public class GreekLetterAtom implements Atom {
	public final String name;  // 如 "alpha", "beta"

	public GreekLetterAtom(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "\\" + name;
	}
}