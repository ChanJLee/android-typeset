package me.chan.texas.ext.markdown.math.renderer.fonts;

public class Symbol {
	public final String unicode;
	public int flags;

	public Symbol(String unicode) {
		this(unicode, 0);
	}

	public Symbol(String unicode, int flags) {
		this.unicode = unicode;
		this.flags = flags;
	}

	public static final int FLAG_INCLUDE_PADDING = 1;
	public static final int FLAG_USE_BASELINE = 2;
	public static final int FLAG_USE_CONST_TEXT_HEIGHT = 4;
}