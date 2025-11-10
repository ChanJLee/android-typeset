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
		return "\\" + cmd + "{" + content.toString() + "}";
	}

	public String getCmd() {
		return cmd;
	}

	public Ast getContent() {
		return content;
	}
}