package me.chan.texas.ext.markdown.math.ast;

public class LargeOperatorAtom implements Atom {
	public final String op;  // "sum", "int", "prod"
	public final SupSubSuffix supSubSuffix;

	public LargeOperatorAtom(String op, SupSubSuffix supSubSuffix) {
		this.op = op;
		this.supSubSuffix = supSubSuffix;
	}

	@Override
	public String toString() {
		String result = "\\" + op;
		if (supSubSuffix != null) {
			result += supSubSuffix;
		}
		return result;
	}
}
