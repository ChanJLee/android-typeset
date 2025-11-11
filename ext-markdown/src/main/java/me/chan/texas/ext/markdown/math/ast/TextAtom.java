package me.chan.texas.ext.markdown.math.ast;

public class TextAtom implements Atom {
	public final String text;
	public final String command;  // "text", "mbox"

	public TextAtom(String command, String text) {
		this.command = command;
		this.text = text;
	}

	@Override
	public String toString() {
		return "\\" + command + "{" + text + "}";
	}
}
