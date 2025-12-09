package me.chan.texas.ext.markdown.math.ast;

public class Group implements Atom {
	public final MathList content;
	public final char s;
	private final char e;

	public Group(char s, char e, MathList content) {
		this.content = content;
		this.s = s;
		this.e = e;
	}

	@Override
	public String toString() {
		return s + content.toString() + e;
	}
}