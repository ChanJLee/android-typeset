package me.chan.texas.ext.markdown.math.ast;

public class SingleToken implements Ast {
	public Atom content;

	public SingleToken(Atom token) {
		this.content = token;
	}

	@Override
	public String toString() {
		return content.toString();
	}
}