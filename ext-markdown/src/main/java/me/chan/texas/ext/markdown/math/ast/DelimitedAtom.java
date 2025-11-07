package me.chan.texas.ext.markdown.math.ast;

public class DelimitedAtom implements Atom {
	public final int level;
	public final String leftDelimiter;
	public final MathList content;
	public final String rightDelimiter;

	public DelimitedAtom(int level, String leftDelimiter, MathList content, String rightDelimiter) {
		this.leftDelimiter = leftDelimiter;
		this.content = content;
		this.rightDelimiter = rightDelimiter;
		this.level = level;
	}

	// TODO unit test
	@Override
	public String toLatex() {
		if (level == LEVEL_L0) {
			return "\\left" + leftDelimiter + content.toLatex() + "\\right" + rightDelimiter;
		}

		if (level == LEVEL_L1) {
			return "\\bigl" + leftDelimiter + content.toLatex() + "\\bigr" + rightDelimiter;
		}

		if (level == LEVEL_L2) {
			return "\\Bigl" + leftDelimiter + content.toLatex() + "\\Bigr" + rightDelimiter;
		}

		if (level == LEVEL_L3) {
			return "\\biggl" + leftDelimiter + content.toLatex() + "\\biggr" + rightDelimiter;
		}

		if (level == LEVEL_L4) {
			return "\\Biggl" + leftDelimiter + content.toLatex() + "\\Biggr" + rightDelimiter;
		}

		throw new IllegalArgumentException("unknown level");
	}

	public static final int LEVEL_L0 = 0;
	public static final int LEVEL_L1 = 1;
	public static final int LEVEL_L2 = 2;
	public static final int LEVEL_L3 = 3;
	public static final int LEVEL_L4 = 4;
}