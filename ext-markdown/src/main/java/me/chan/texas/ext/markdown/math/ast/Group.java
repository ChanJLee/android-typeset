package me.chan.texas.ext.markdown.math.ast;

public class Group implements Atom {
	public final MathList content;

	public Group(MathList content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "{" + content.toString() + "}";
	}
}