package me.chan.texas.ext.markdown.math.ast;

public class FunctionCallAtom implements Atom {
	String functionName;
	ScriptArg subscript;     // 可选
	ScriptArg superscript;   // 可选
	Node argument;       // 可选

	public FunctionCallAtom(String functionName, ScriptArg subscript,
							ScriptArg superscript, Node argument) {
		this.functionName = functionName;
		this.subscript = subscript;
		this.superscript = superscript;
		this.argument = argument;
	}

	@Override
	public String toLatex() {
		String result = "\\" + functionName;
		if (subscript != null) {
			result += "_" + subscript.toLatex();
		}
		if (superscript != null) {
			result += "^" + superscript.toLatex();
		}
		if (argument != null) {
			result += argument.toLatex();
		}
		return result;
	}
}
