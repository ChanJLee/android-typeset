package me.chan.texas.ext.markdown.math.ast;

public class Spacing implements Atom {
	public final String cmd;
	public final Ast ast;

	public Spacing(String cmd, Ast ast) {
		this.cmd = cmd;
		this.ast = ast;
	}

	@Override
	public String toString() {
		return "";
	}
}
