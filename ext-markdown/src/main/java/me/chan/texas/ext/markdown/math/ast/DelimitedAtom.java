package me.chan.texas.ext.markdown.math.ast;

public class DelimitedAtom implements Atom {
	String leftDelim;
	MathList content;
	String rightDelim;

	public DelimitedAtom(String leftDelim, MathList content, String rightDelim) {
		this.leftDelim = leftDelim;
		this.content = content;
		this.rightDelim = rightDelim;
	}

	@Override
	public String toLatex() {
		return "\\left" + leftDelim + content.toLatex() + "\\right" + rightDelim;
	}
}