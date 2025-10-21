package me.chan.texas.ext.markdown.math.ast;

public class AccentAtom implements Atom {
	String accentCmd;  // "hat", "bar", "vec"
	Ast content;

	public AccentAtom(String accentCmd, Ast content) {
		this.accentCmd = accentCmd;
		this.content = content;
	}

	@Override
	public String toLatex() {
		return "\\" + accentCmd + "{" + content.toLatex() + "}";
	}

	public String getAccentCmd() {
		return accentCmd;
	}

	public Ast getContent() {
		return content;
	}
}