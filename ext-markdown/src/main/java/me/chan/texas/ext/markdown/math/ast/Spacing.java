package me.chan.texas.ext.markdown.math.ast;

public class Spacing implements Atom {
	public final String command;
	public final Ast content;

	public Spacing(String command, Ast content) {
		this.command = command;
		this.content = content;
	}

	@Override
	public String toString() {
		if (content == null) {
			// 简单的空格命令，如 \quad, \qquad, \,, \:, \;, \!
			return "\\" + command;
		} else {
			// 带参数的命令：\hspace{10em}, \phantom{xyz} 等
			return "\\" + command + "{" + content.toString() + "}";
		}
	}
}