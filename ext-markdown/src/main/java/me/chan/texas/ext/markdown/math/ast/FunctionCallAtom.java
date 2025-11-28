package me.chan.texas.ext.markdown.math.ast;

public class FunctionCallAtom implements Atom {
	public final String functionName;
	public final SupSubSuffix supSubSuffix;
	public final Ast argument;       // 可选

	public FunctionCallAtom(String functionName, SupSubSuffix supSubSuffix, Ast argument) {
		this.functionName = functionName;
		this.supSubSuffix = supSubSuffix;
		this.argument = argument;
	}

	@Override
	public String toString() {
		String result = "\\" + functionName;
		if (supSubSuffix != null) {
			result += supSubSuffix;
		}
		if (argument != null) {
			result += argument.toString();
		}
		return result;
	}
}
