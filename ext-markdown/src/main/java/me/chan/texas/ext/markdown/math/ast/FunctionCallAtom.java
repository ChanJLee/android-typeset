package me.chan.texas.ext.markdown.math.ast;

public class FunctionCallAtom implements Atom {
	public final String functionName;
	public final ScriptArg subscript;     // 可选
	public final ScriptArg superscript;   // 可选
	public final Ast argument;       // 可选

	public FunctionCallAtom(String functionName, ScriptArg subscript,
							ScriptArg superscript, Ast argument) {
		this.functionName = functionName;
		this.subscript = subscript;
		this.superscript = superscript;
		this.argument = argument;
	}

	@Override
	public String toString() {
		String result = "\\" + functionName;
		if (subscript != null) {
			result += "_" + subscript.toString();
		}
		if (superscript != null) {
			result += "^" + superscript.toString();
		}
		if (argument != null) {
			result += argument.toString();
		}
		return result;
	}
}
