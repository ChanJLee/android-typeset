package me.chan.texas.ext.markdown.math.renderer.fonts;

public class Symbol {
	public final String unicode;
	public final int flags;

	public Symbol(String unicode) {
		this(unicode, 0);
	}

	public Symbol(String unicode, int flags) {
		this.unicode = unicode;
		this.flags = flags;
	}
}