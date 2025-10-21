package me.chan.texas.ext.markdown.math.ast;

public class Term implements Ast {
	String unaryOp;       // 可选，一元运算符 "+" 或 "-"
	Atom atom;
	SupSubSuffix suffix;  // 可选

	public Term(String unaryOp, Atom atom, SupSubSuffix suffix) {
		this.unaryOp = unaryOp;
		this.atom = atom;
		this.suffix = suffix;
	}

	@Override
	public String toLatex() {
		String result = "";
		if (unaryOp != null) {
			result += unaryOp;
		}
		result += atom.toLatex();
		if (suffix != null) {
			result += suffix.toLatex();
		}
		return result;
	}

	public String getUnaryOp() {
		return unaryOp;
	}

	public Atom getAtom() {
		return atom;
	}

	public SupSubSuffix getSuffix() {
		return suffix;
	}
}