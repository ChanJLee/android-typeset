package me.chan.texas.ext.markdown.ast;

public final class MdText implements MdInline {
	public final String text;

	public MdText(String text) {
		this.text = text;
	}
}
