package me.chan.texas.ext.markdown.ast;

public final class MdInlineCode implements MdInline {
	public final String code;

	public MdInlineCode(String code) {
		this.code = code;
	}
}
