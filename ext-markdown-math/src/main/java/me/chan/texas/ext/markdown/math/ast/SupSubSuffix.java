package me.chan.texas.ext.markdown.math.ast;

import androidx.annotation.VisibleForTesting;

public class SupSubSuffix implements Ast {
	public final ScriptArg superscript;
	public final ScriptArg subscript;
	@VisibleForTesting
	final boolean reverse;

	public SupSubSuffix(ScriptArg superscript, ScriptArg subscript, boolean reverse) {
		this.superscript = superscript;
		this.subscript = subscript;
		this.reverse = reverse;
	}

	@Override
	public String toString() {
		String result = "";
		if (reverse) {
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
