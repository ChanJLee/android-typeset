package me.chan.texas.ext.markdown.math.ast;

public class  DelimitedAtom implements Atom {
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

	@Override
	public String toString() {
		return "\\" + MathParser.DELIMITER_LEVELS[level][0] + leftDelimiter + " " +
				content.toString() +
				" \\" + MathParser.DELIMITER_LEVELS[level][1] + rightDelimiter;
	}

	public static final int LEVEL_L0 = 0;
	public static final int LEVEL_L1 = 1;
	public static final int LEVEL_L2 = 2;
	public static final int LEVEL_L3 = 3;
	public static final int LEVEL_L4 = 4;
}