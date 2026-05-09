package me.chan.texas.ext.markdown.ast;

public final class MdAutolink implements MdInline {
	public final String url;

	public MdAutolink(String url) {
		this.url = url;
	}
}
