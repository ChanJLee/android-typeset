package me.chan.texas.ext.markdown.math.ast;

public class TextAtom implements Atom {
	public final String content;
	public final String command;  // "text", "mbox"

	public TextAtom(String command, String content) {
		this.command = command;
		this.content = content;
	}

	@Override
	public String toString() {
		return "\\" + command + "{" + content + "}";
	}
}
