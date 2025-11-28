package me.chan.texas.ext.markdown.math.ast;

public class SupSubSuffix implements Ast {
	public final ScriptArg superscript;
	public final ScriptArg subscript;

	public SupSubSuffix(ScriptArg superscript, ScriptArg subscript) {
		this.superscript = superscript;
		this.subscript = subscript;
	}

	@Override
	public String toString() {
		String result = "";
		if (superscript != null) {
			result += "^" + superscript;
		}
		if (subscript != null) {
			result += "_" + subscript;
		}
		return result;
	}
}
