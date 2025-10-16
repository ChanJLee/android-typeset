package me.chan.texas.ext.markdown.math.ast;

public class SupSubSuffix implements Ast {
	ScriptArg superscript;
	ScriptArg subscript;

	public SupSubSuffix(ScriptArg superscript, ScriptArg subscript) {
		this.superscript = superscript;
		this.subscript = subscript;
	}

	@Override
	public String toLatex() {
		String result = "";
		if (superscript != null) {
			result += "^" + superscript.toLatex();
		}
		if (subscript != null) {
			result += "_" + subscript.toLatex();
		}
		return result;
	}
}
