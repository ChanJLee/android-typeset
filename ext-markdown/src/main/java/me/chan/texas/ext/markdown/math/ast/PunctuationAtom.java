package me.chan.texas.ext.markdown.math.ast;

public class PunctuationAtom implements Atom {
    public final String symbol;

    public PunctuationAtom(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}