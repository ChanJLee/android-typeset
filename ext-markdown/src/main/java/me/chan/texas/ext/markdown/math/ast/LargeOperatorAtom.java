package me.chan.texas.ext.markdown.math.ast;

public class LargeOperatorAtom implements Atom {
	String operatorName;  // "sum", "int", "prod"
	ScriptArg subscript;
	ScriptArg superscript;

	public LargeOperatorAtom(String operatorName, ScriptArg subscript, ScriptArg superscript) {
		this.operatorName = operatorName;
		this.subscript = subscript;
		this.superscript = superscript;
	}

	@Override
	public String toLatex() {
		String result = "\\" + operatorName;
		if (subscript != null) {
			result += "_" + subscript.toLatex();
		}
		if (superscript != null) {
			result += "^" + superscript.toLatex();
		}
		return result;
	}
}
