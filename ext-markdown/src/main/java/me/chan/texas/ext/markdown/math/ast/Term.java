package me.chan.texas.ext.markdown.math.ast;

public class Term implements Ast {
	public final UnaryOp unaryOp;       // 可选，一元运算符 "+" 或 "-"
	public final Atom atom;
	public final SupSubSuffix suffix;  // 可选

	public Term(UnaryOp unaryOp, Atom atom, SupSubSuffix suffix) {
		this.unaryOp = unaryOp;
		this.atom = atom;
		this.suffix = suffix;
	}

	@Override
	public String toString() {
		String result = "";
		if (unaryOp != null) {
			result += unaryOp;
		}
		result += atom.toString();
		if (suffix != null) {
			result += suffix.toString();
		}
		return result;
	}
}