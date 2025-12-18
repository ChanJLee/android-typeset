package me.chan.texas.ext.markdown.math.ast;

public class SqrtAtom implements Atom {
	public final MathList content;
	public final MathList root;  // 可选，n次根

	public SqrtAtom(MathList content, MathList root) {
		this.content = content;
		this.root = root;
	}

	@Override
	public String toString() {
		String result = "\\sqrt";
		if (root != null) {
			result += "[" + root.toString() + "]";
		}
		result += "{" + content.toString() + "}";
		return result;
	}
}