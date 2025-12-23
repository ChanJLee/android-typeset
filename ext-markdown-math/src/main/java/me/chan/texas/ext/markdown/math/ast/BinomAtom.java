package me.chan.texas.ext.markdown.math.ast;

public class BinomAtom implements Atom {
    public final MathList upper;      // 上部分（n）
    public final MathList lower;      // 下部分（k）
    public final String command;      // "binom", "dbinom", "tbinom"

    public BinomAtom(String command, MathList upper, MathList lower) {
        this.command = command;
        this.upper = upper;
        this.lower = lower;
    }

    @Override
    public String toString() {
        return "\\" + command + "{" + upper.toString() + "}{" + lower.toString() + "}";
    }
}