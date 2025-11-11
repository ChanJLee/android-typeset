package me.chan.texas.ext.markdown.math.ast;

public class GroupAtom implements Atom {
	public final MathList content;

	public GroupAtom(MathList content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "{" + content.toString() + "}";
	}
}