package me.chan.texas.ext.markdown.math.ast;

public class ScriptArg implements Ast {
	public final Ast content;

	public ScriptArg(Ast content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return content.toString();
	}
}
