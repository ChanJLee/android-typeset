package me.chan.texas.ext.markdown.math.ast;

public class SingleTokenScriptArg implements ScriptArg {
	String token;

	public SingleTokenScriptArg(String token) {
		this.token = token;
	}

	@Override
	public String toLatex() {
		return token;
	}

	public String getToken() {
		return token;
	}
}