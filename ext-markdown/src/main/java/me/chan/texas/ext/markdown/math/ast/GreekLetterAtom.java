package me.chan.texas.ext.markdown.math.ast;

public class GreekLetterAtom implements Atom {
	public final String symbol;  // 如 "alpha", "beta"

	public GreekLetterAtom(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toLatex() {
		return "\\" + symbol;
	}
}