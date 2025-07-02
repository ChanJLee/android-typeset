package me.chan.texas.text.tokenizer;

import androidx.annotation.RestrictTo;

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
		return obtain(text, start, end, false);
	}

	static TokenStream obtain(CharSequence text, int start, int end, boolean rtl) {
		return TextTokenStream.obtain(text, start, end, rtl);
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	static TokenStream link(TokenStream stream1, TokenStream stream2) {

		return LinkedTokenStream.obtain(stream1, stream2);
	}
}
