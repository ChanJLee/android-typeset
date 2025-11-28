package me.chan.texas.ext.markdown.math.ast;

public class Spacing implements Atom {
	public final String cmd;
	public final Ast content;

	public Spacing(String cmd, Ast content) {
		this.cmd = cmd;
		this.content = content;
	}

	@Override
	public String toString() {
		return "";
	}
}
