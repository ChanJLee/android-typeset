package me.chan.texas.ext.markdown.math.ast;

public class LargeOperatorAtom implements Atom {
	public final String op;  // "sum", "int", "prod"
	public final ScriptArg subscript;
	public final ScriptArg superscript;

	public LargeOperatorAtom(String op, ScriptArg subscript, ScriptArg superscript) {
		this.op = op;
		this.subscript = subscript;
		this.superscript = superscript;
	}

	@Override
	public String toString() {
		String result = "\\" + op;
		if (subscript != null) {
			result += "_" + subscript.toString();
		}
		if (superscript != null) {
			result += "^" + superscript.toString();
		}
		return result;
	}
}
