package me.chan.texas.ext.markdown.math.ast;

public class SupSubSuffix implements Ast {
	public final ScriptArg superscript;
	public final ScriptArg subscript;
	private final boolean reserve;

	public SupSubSuffix(ScriptArg superscript, ScriptArg subscript, boolean reserve) {
		this.superscript = superscript;
		this.subscript = subscript;
		this.reserve = reserve;
	}

	@Override
	public String toString() {
		String result = "";
		if (reserve) {
			if (subscript != null) {
				result += "_" + subscript;
			}
			if (superscript != null) {
				result += "^" + superscript;
			}
		} else {
			if (superscript != null) {
				result += "^" + superscript;
			}
			if (subscript != null) {
				result += "_" + subscript;
			}
		}
		return result;
	}
}
