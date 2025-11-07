package me.chan.texas.ext.markdown.math.ast;

public class Length implements Ast {
	public final NumberAtom size;
	public final Unit unit;

	public Length(NumberAtom size, Unit unit) {
		this.size = size;
		this.unit = unit;
	}

	@Override
	public String toLatex() {
		return size.toLatex() + unit.toLatex();
	}
}
