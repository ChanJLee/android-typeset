package me.chan.texas.ext.markdown.math.ast;

public class TextAtom implements Atom {
	String text;
	String command;  // "text", "mbox"

	public TextAtom(String command, String text) {
		this.command = command;
		this.text = text;
	}

	@Override
	public String toLatex() {
		return "\\" + command + "{" + text + "}";
	}
}
