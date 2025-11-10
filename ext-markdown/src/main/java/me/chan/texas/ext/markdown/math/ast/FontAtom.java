package me.chan.texas.ext.markdown.math.ast;

public class FontAtom implements Atom {
	public final String styles;
	public final Ast ast;

	public FontAtom(String styles, Ast ast) {
		this.styles = styles;
		this.ast = ast;
	}

	@Override
	public String toString() {
		return "\\" + styles + " { " + ast.toString() + " }";
	}
}
