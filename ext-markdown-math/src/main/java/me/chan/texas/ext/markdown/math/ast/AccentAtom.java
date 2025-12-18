package me.chan.texas.ext.markdown.math.ast;

public class AccentAtom implements Atom {
	public final String cmd;  // "hat", "bar", "vec"
	public final Ast content;

	public AccentAtom(String cmd, Ast content) {
		this.cmd = cmd;
		this.content = content;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\\");
		stringBuilder.append(cmd);
		if (content instanceof MathList) {
			stringBuilder.append("{");
		} else {
			stringBuilder.append(" ");
		}
		stringBuilder.append(content.toString());
		if (content instanceof MathList) {
			stringBuilder.append("}");
		}

		return stringBuilder.toString();
	}
}