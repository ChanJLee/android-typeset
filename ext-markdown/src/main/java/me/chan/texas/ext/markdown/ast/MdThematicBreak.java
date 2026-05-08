package me.chan.texas.ext.markdown.ast;

public final class MdThematicBreak implements MdBlock {
	public final char marker;

	public MdThematicBreak(char marker) {
		this.marker = marker;
	}
}
