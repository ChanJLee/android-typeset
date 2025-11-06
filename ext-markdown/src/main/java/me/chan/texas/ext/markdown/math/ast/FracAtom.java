package me.chan.texas.ext.markdown.math.ast;

public class FracAtom implements Atom {
	public final MathList numerator;
	public final MathList denominator;
	String command;  // "frac", "dfrac", "tfrac"

	public FracAtom(String command, MathList numerator, MathList denominator) {
		this.command = command;
		this.numerator = numerator;
		this.denominator = denominator;
	}

	@Override
	public String toLatex() {
		return "\\" + command + "{" + numerator.toLatex() + "}{" + denominator.toLatex() + "}";
	}
}
