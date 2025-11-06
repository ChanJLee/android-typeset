package me.chan.texas.ext.markdown.math.renderer.fonts;

public class Symbol {
	public final String unicode;
	public final int xMin;
	public final int ascent;
	public final int xMax;
	public final int descent;

	public Symbol(String unicode, int xMin, int ascent, int xMax, int descent) {
		this.unicode = unicode;
		this.xMin = xMin;
		this.ascent = ascent;
		this.xMax = xMax;
		this.descent = descent;
	}
}