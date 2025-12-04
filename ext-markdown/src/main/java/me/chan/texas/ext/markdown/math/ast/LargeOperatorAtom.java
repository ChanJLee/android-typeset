package me.chan.texas.ext.markdown.math.ast;

public class LargeOperatorAtom implements Atom {
	public final String name;  // "sum", "int", "prod"
	public final SupSubSuffix suffix;

	public LargeOperatorAtom(String name, SupSubSuffix suffix) {
		this.name = name;
		this.suffix = suffix;
	}

	@Override
	public String toString() {
		String result = "\\" + name;
		if (suffix != null) {
			result += suffix;
		}
		return result;
	}
}
