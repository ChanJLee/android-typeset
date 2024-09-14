package me.chan.texas.text.tokenizer;

import me.chan.texas.misc.Recyclable;

public interface TokenStream extends Recyclable {

	boolean hasNext();

	int save();

	void restore(int state);

	Token next();

	Token tryGet(int state, int offset);

	Token tryGet(int offset);

	int size();

	static TokenStream obtain(CharSequence text, int start, int end) {
		return TextTokenStream.obtain(text, start, end);
	}

	static TokenStream link(TokenStream stream1, TokenStream stream2) {
		// 目前只需要支持两个
		return LinkedTokenStream.obtain(stream1, stream2);
	}
}
