package me.chan.texas.ext.markdown.math.ast;

public class SupSubSuffix implements Ast {
	ScriptArg superscript;
	ScriptArg subscript;

	public SupSubSuffix(ScriptArg superscript, ScriptArg subscript) {
		this.superscript = superscript;
		this.subscript = subscript;
	}

	@Override
	public String toString() {
		String result = "";
		if (superscript != null) {
			result += "^" + superscript.toString();
		}
		if (subscript != null) {
			result += "_" + subscript.toString();
		}
		return result;
	}

	public ScriptArg getSuperscript() {
		return superscript;
	}

	public ScriptArg getSubscript() {
		return subscript;
	}
}
