package me.chan.texas.ext.markdown.math.ast;

public class FunctionCallAtom implements Atom {
	public final String name;
	public final SupSubSuffix suffix;
	public final Ast argument;       // 可选

	public FunctionCallAtom(String name, SupSubSuffix suffix, Ast argument) {
		this.name = name;
		this.suffix = suffix;
		this.argument = argument;
	}

	@Override
	public String toString() {
		String result = "\\" + name;
		if (suffix != null) {
			result += suffix;
		}
		if (argument != null) {
			if (!(argument instanceof Group)) {
				result += " ";
			}
			result += argument.toString();
		}
		return result;
	}
}
