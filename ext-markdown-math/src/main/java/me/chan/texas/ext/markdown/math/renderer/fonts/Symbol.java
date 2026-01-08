package me.chan.texas.ext.markdown.math.renderer.fonts;

public class Symbol {
	public final String unicode;
	public final float xMin;
	public final float ascent;
	public final float xMax;
	public final float descent;

	public Symbol(String unicode, float xMin, float ascent, float xMax, float descent) {
		this.unicode = unicode;
		this.xMin = xMin;
		this.ascent = ascent;
		this.xMax = xMax;
		this.descent = descent;
	}

	@Override
	public String toString() {
		return unicode;
	}
}