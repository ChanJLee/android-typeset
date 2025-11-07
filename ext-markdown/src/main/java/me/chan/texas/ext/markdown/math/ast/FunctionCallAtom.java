package me.chan.texas.ext.markdown.math.ast;

public class FunctionCallAtom implements Atom {
	public String functionName;
	public ScriptArg subscript;     // 可选
	public ScriptArg superscript;   // 可选
	public Ast argument;       // 可选

	public FunctionCallAtom(String functionName, ScriptArg subscript,
							ScriptArg superscript, Ast argument) {
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
