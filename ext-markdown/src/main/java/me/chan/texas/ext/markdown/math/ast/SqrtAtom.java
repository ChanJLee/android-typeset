package me.chan.texas.ext.markdown.math.ast;

public class SqrtAtom implements Atom {
	MathList content;
	MathList root;  // 可选，n次根

	public SqrtAtom(MathList content, MathList root) {
		this.content = content;
		this.root = root;
	}

	@Override
	public String toLatex() {
		String result = "\\sqrt";
		if (root != null) {
			result += "[" + root.toLatex() + "]";
		}
		result += "{" + content.toLatex() + "}";
		return result;
	}
}