package me.chan.texas.ext.markdown.math.ast;

import androidx.annotation.NonNull;

public class SymbolAtom implements Atom {
	public final String symbol;

	public SymbolAtom(String symbol) {
		this.symbol = symbol;
	}

	@NonNull
	@Override
	public String toString() {
		return symbol;
	}
}
