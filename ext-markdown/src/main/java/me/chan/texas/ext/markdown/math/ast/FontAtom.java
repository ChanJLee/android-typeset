package me.chan.texas.ext.markdown.math.ast;

public class FontAtom implements Atom {
	public final String cmd;
	public final MathList ast;

	public FontAtom(String cmd, MathList ast) {
		this.cmd = cmd;
		this.ast = ast;
	}

	@Override
	public String toString() {
		return "\\" + cmd + "{" + ast.toString() + "}";
	}
}
