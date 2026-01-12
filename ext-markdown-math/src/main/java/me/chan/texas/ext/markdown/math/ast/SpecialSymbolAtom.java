package me.chan.texas.ext.markdown.math.ast;

public class SpecialSymbolAtom implements Atom {
	public final String symbol;

	public SpecialSymbolAtom(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		return "\\" + symbol;
	}
}
