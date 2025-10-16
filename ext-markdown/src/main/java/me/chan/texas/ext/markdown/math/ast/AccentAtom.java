package me.chan.texas.ext.markdown.math.ast;

public class AccentAtom implements Atom {
	String accentCmd;  // "hat", "bar", "vec"
	MathNode content;

	public AccentAtom(String accentCmd, MathNode content) {
		this.accentCmd = accentCmd;
		this.content = content;
	}

	@Override
	public String toLatex() {
		return "\\" + accentCmd + "{" + content.toLatex() + "}";
	}
}