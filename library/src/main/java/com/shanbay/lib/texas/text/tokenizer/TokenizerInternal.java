package com.shanbay.lib.texas.text.tokenizer;

import com.shanbay.lib.texas.text.icu.UnicodeUtils;

import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.tokenize.TokenContextGenerator;
import opennlp.tools.tokenize.TokenizerFactory;
import opennlp.tools.tokenize.TokenizerModel;

class TokenizerInternal {
	/**
	 * Constant indicates a token split.
	 */
	public static final String SPLIT = "T";

	/**
	 * Constant indicates no token split.
	 */
	public static final String NO_SPLIT = "F";

	/**
	 * The maximum entropy model to use to evaluate contexts.
	 */
	private final MaxentModel model;

	/**
	 * The context generator.
	 */
	private final TokenContextGenerator cg;

	private final SpanStream mSpanStream;

	public TokenizerInternal(TokenizerModel model) {
		TokenizerFactory factory = model.getFactory();
		this.cg = factory.getContextGenerator();
		this.model = model.getMaxentModel();
		mSpanStream = new SpanStream();
	}

	public SpanStream tokenize(String text, int start, int end) {
		mSpanStream.reset();

		int size = end - start;
		if (size < 2) {
			mSpanStream.append(start, end);
			return mSpanStream;
		}

		text = text.substring(start, end);

		int offset = start;
		start = 0;
		end = size;
		for (int j = 1; j < size; j++) {
			int cp = text.charAt(j);
			if (UnicodeUtils.isDigit(cp) || UnicodeUtils.isLatinLetter(cp)) {
				continue;
			}

			String[] context = getContext(text, j);
			double[] probs =
					model.eval(context);
			String best = model.getBestOutcome(probs);
			if (best.equals(TokenizerInternal.SPLIT)) {
				mSpanStream.append(start + offset, j + offset);
				start = j;
			}
		}
		mSpanStream.append(start + offset, end + offset);

		return mSpanStream;
	}

	private String[] getContext(String charSequence, int index) {
		return cg.getContext(charSequence, index);
	}
}
