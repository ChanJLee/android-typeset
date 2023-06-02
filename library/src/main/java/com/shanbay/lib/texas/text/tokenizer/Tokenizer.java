package com.shanbay.lib.texas.text.tokenizer;

import android.content.Context;

import androidx.annotation.VisibleForTesting;

import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.tokenize.TokenizerModel;

public class Tokenizer {
	private volatile static Tokenizer sTokenizer;
	private final TokenizerInternal mTokenizerInternal;

	private Tokenizer(TokenizerInternal tokenizer) {
		mTokenizerInternal = tokenizer;
	}

	public synchronized SpanStream tokenize(CharSequence text, int start, int end) {
		return mTokenizerInternal.tokenize(String.valueOf(text), start, end);
	}

	@VisibleForTesting
	public static void setup(TokenizerModel model) {
		sTokenizer = new Tokenizer(new TokenizerInternal(model));
	}

	public static synchronized Tokenizer getInstance(Context context) throws IOException {
		if (sTokenizer != null) {
			return sTokenizer;
		}

		if (context == null) {
			return null;
		}

		InputStream inputStream = context.getAssets().open("texas/opennlp-en-ud-ewt-tokens-1.0-1.9.3.bin");
		try {
			TokenizerModel model = new TokenizerModel(inputStream);
			sTokenizer = new Tokenizer(new TokenizerInternal(model));
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Throwable throwable) {
				throwable.printStackTrace();
			}
		}
		return sTokenizer;
	}
}
