package me.chan.texas.ext.markdown.math.ast;

public class Term implements Ast {
	public final Atom atom;
	public final SupSubSuffix suffix;

	public Term(Atom atom, SupSubSuffix suffix) {
		this.atom = atom;
		this.suffix = suffix;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(atom);
		if (suffix != null) {
			stringBuilder.append(suffix);
		}
		return stringBuilder.toString();
	}
}