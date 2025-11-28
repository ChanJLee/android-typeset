package me.chan.texas.ext.markdown.math.ast;

public class FunctionCallAtom implements Atom {
	public final String name;
	public final SupSubSuffix supSubSuffix;
	public final Ast argument;       // 可选

	public FunctionCallAtom(String name, SupSubSuffix supSubSuffix, Ast argument) {
		this.name = name;
		this.supSubSuffix = supSubSuffix;
		this.argument = argument;
	}

	@Override
	public String toString() {
		String result = "\\" + name;
		if (supSubSuffix != null) {
			result += supSubSuffix;
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
