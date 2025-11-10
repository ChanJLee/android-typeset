package me.chan.texas.ext.markdown.math.ast;

public class SizeUnit implements Atom {
	public final String unit;

	public SizeUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public String toString() {
		return unit;
	}
}
