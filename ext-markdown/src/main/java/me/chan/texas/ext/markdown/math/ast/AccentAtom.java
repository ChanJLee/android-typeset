package me.chan.texas.ext.markdown.math.ast;

public class AccentAtom implements Atom {
	String accentCmd;  // "hat", "bar", "vec"
	Node content;

	public AccentAtom(String accentCmd, Node content) {
		this.accentCmd = accentCmd;
		this.content = content;
	}

	@Override
	public String toLatex() {
		return "\\" + accentCmd + "{" + content.toLatex() + "}";
	}
}