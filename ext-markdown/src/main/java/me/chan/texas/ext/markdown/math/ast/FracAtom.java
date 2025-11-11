package me.chan.texas.ext.markdown.math.ast;

public class FracAtom implements Atom {
	public final MathList numerator;
	public final MathList denominator;
	public final String command;  // "frac", "dfrac", "tfrac"

	public FracAtom(String command, MathList numerator, MathList denominator) {
		this.command = command;
		this.numerator = numerator;
		this.denominator = denominator;
	}

	@Override
	public String toString() {
		return "\\" + command + "{" + numerator.toString() + "}{" + denominator.toString() + "}";
	}
}
