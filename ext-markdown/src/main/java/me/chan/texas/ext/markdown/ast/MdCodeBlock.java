package me.chan.texas.ext.markdown.ast;

public final class MdCodeBlock implements MdBlock {
	public static final int KIND_FENCED = 0;
	public static final int KIND_INDENTED = 1;

	public final int kind;
	/**
	 * fenced code block 的语言标识，indented 时为空串
	 */
	public final String infoString;
	public final String content;

	public MdCodeBlock(int kind, String infoString, String content) {
		this.kind = kind;
		this.infoString = infoString;
		this.content = content;
	}
}
