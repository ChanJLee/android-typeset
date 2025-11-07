package me.chan.texas.ext.markdown.math.ast;

public class PlainSymbolAtom implements Atom {
	public final String symbol;

	public PlainSymbolAtom(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toLatex() {
		return "";
	}
}
