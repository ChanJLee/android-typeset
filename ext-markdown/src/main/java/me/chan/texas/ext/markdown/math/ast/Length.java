package me.chan.texas.ext.markdown.math.ast;

public class Length implements Ast {
	public final NumberAtom size;
	public final SizeUnit unit;

	public Length(NumberAtom size, SizeUnit unit) {
		this.size = size;
		this.unit = unit;
	}

	@Override
	public String toString() {
		return size.toString() + unit.toString();
	}
}
