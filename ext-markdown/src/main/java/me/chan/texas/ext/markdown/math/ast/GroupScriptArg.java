package me.chan.texas.ext.markdown.math.ast;

public class GroupScriptArg implements ScriptArg {
	MathList content;

	public GroupScriptArg(MathList content) {
		this.content = content;
	}

	@Override
	public String toLatex() {
		return "{" + content.toLatex() + "}";
	}
}
