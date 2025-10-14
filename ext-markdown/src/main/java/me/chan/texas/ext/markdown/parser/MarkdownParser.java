package me.chan.texas.ext.markdown.parser;

import me.chan.texas.ext.markdown.math.MathNode;
import me.chan.texas.utils.CharStream;

public class MarkdownParser {

	public MathNode parse(CharStream stream) {
		if (stream == null || stream.eof()) {
			return MathNode.EMPTY;
		}

		throw new RuntimeException("Stub!");
	}
}
