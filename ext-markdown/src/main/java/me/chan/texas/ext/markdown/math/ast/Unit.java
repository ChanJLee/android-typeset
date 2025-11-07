package me.chan.texas.ext.markdown.math.ast;

public class Unit implements Atom {
	public final String unit;

	public Unit(String unit) {
		this.unit = unit;
	}

	@Override
	public String toLatex() {
		return unit;
	}
}
