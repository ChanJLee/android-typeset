package me.chan.texas.ext.markdown.math.ast;

public class GroupAtom implements Atom {
	private final MathList content;

	public GroupAtom(MathList content) {
		this.content = content;
	}

	@Override
	public String toLatex() {
		return "{" + content.toLatex() + "}";
	}

	public MathList getContent() {
		return content;
	}
}