package me.chan.texas.ext.markdown.math.ast;

public class FontAtom implements Atom {
	public final String command;
	public final MathList content;

	public FontAtom(String command, MathList content) {
		this.command = command;
		this.content = content;
	}

	@Override
	public String toString() {
		return "\\" + command + "{" + content.toString() + "}";
	}
}
