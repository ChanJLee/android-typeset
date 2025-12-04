package me.chan.texas.ext.markdown.math.ast;

public class Term implements Ast {
	public final UnaryOp unaryOp;
	public final Atom atom;
	public final SupSubSuffix suffix;
	public final PostfixOp postfixOp;  // 新增

	public Term(UnaryOp unaryOp, Atom atom, SupSubSuffix suffix) {
		this(unaryOp, atom, suffix, null);
	}

	public Term(UnaryOp unaryOp, Atom atom, SupSubSuffix suffix, PostfixOp postfixOp) {
		this.unaryOp = unaryOp;
		this.atom = atom;
		this.suffix = suffix;
		this.postfixOp = postfixOp;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		if (unaryOp != null) {
			stringBuilder.append(unaryOp.op);
			if (unaryOp.op.startsWith("\\")) {
				stringBuilder.append(" ");
			}
		}
		stringBuilder.append(atom);
		if (suffix != null) {
			stringBuilder.append(suffix);
		}
		if (postfixOp != null) {
			stringBuilder.append(postfixOp);
		}
		return stringBuilder.toString();
	}
}