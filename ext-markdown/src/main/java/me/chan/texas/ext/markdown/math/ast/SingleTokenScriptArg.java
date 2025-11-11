package me.chan.texas.ext.markdown.math.ast;

public class SingleTokenScriptArg implements ScriptArg {
	public String token;

	public SingleTokenScriptArg(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return token;
	}
}