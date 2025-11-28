package me.chan.texas.ext.markdown.math.ast;

public class LargeOperatorAtom implements Atom {
	public final String name;  // "sum", "int", "prod"
	public final SupSubSuffix supSubSuffix;

	public LargeOperatorAtom(String name, SupSubSuffix supSubSuffix) {
		this.name = name;
		this.supSubSuffix = supSubSuffix;
	}

	@Override
	public String toString() {
		String result = "\\" + name;
		if (supSubSuffix != null) {
			result += supSubSuffix;
		}
		return result;
	}
}
