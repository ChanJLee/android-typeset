package me.chan.texas.text.tokenizer;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.ibm.icu.text.BreakIterator;

import me.chan.texas.text.icu.UnicodeUtils;
import me.chan.texas.utils.CharStream;
import me.chan.texas.utils.LongArray;

class WordStream {
	private final CharacterSequenceIterator mIterator = new CharacterSequenceIterator();
	private final CharStream mStream = new CharStream();
	private final BrkArray mBrk = new BrkArray(128);
	private int mIndex = 0;

	@VisibleForTesting
	void setText(CharSequence text, int start, int end) {
		synchronized (WordStream.class /* 粒度必须要粗 */) {
			setText0(text, start, end);
		}
	}

	private void setText0(CharSequence text, int start, int end) {
		mBrk.clear();
		mIndex = 0;
		mStream.reset(text, start, end);

		BreakIterator boundary = WordBreaker.getWordBreakIterator();
		boundary.setText(mIterator.reset(text, start, end));

		addBrk(mBrk, Token.CATEGORY_NONE, boundary.first() + start);
		for (int brk = boundary.next();
			 brk != BreakIterator.DONE; brk = boundary.next()) {
			int reason = boundary.getRuleStatus();
			if (reason >= BreakIterator.WORD_LETTER && reason < BreakIterator.WORD_LETTER_LIMIT
					|| reason >= BreakIterator.WORD_NONE && reason < BreakIterator.WORD_NONE_LIMIT) {
				append(mBrk, text, brk + start);
				continue;
			}

			if (reason >= BreakIterator.WORD_NUMBER && reason < BreakIterator.WORD_NUMBER_LIMIT) {
				addBrk(mBrk, Token.CATEGORY_NUMBER, brk + start);
				continue;
			}

			if (reason >= BreakIterator.WORD_KANA && reason < BreakIterator.WORD_KANA_LIMIT ||
					reason >= BreakIterator.WORD_IDEO && reason < BreakIterator.WORD_IDEO_LIMIT) {
				appendCJK(mBrk, brk + start);
				continue;
			}

			throw new IllegalStateException("unknown token type");
		}
	}

	private void append(BrkArray brk, CharSequence text, int end) {
		final int start = (int) brk.last();
		boolean simpleWord = true;
		for (int i = start; i < end; ++i) {
			int codePoint = text.charAt(i);
			if (!isSimpleWord(codePoint)) {
				simpleWord = false;
				break;
			}
		}

		if (simpleWord) {
			addBrk(brk, Token.CATEGORY_NORMAL, end);
			return;
		}

		// todo test 多个空格
		int codePoint = text.charAt(start);
		if (UnicodeUtils.isControlCharacter(codePoint)) {
			addBrk(brk, Token.CATEGORY_CONTROL, end);
			return;
		}

		int type = Character.getType(codePoint);
		if (type == Character.MATH_SYMBOL
				/* https://www.compart.com/en/unicode/category/Sc */
				|| type == Character.CURRENCY_SYMBOL
				/* https://www.compart.com/en/unicode/category/Sk */
				|| type == Character.MODIFIER_SYMBOL
				/* https://www.compart.com/en/unicode/category/So */
				|| type == Character.OTHER_SYMBOL) {
			addBrk(brk, Token.CATEGORY_SYMBOL, end);
			return;
		}

		if (type == Character.DASH_PUNCTUATION
				|| type == Character.END_PUNCTUATION
				|| type == Character.FINAL_QUOTE_PUNCTUATION
				|| type == Character.INITIAL_QUOTE_PUNCTUATION
				|| type == Character.OTHER_PUNCTUATION
				|| type == Character.START_PUNCTUATION) {
			addBrk(brk, Token.CATEGORY_PUNCTUATION, end);
			return;
		}

		if (UnicodeUtils.isCJKExtends(codePoint)) {
			appendCJK(brk, end);
			return;
		}

		addBrk(brk, Token.CATEGORY_UNKNOWN_LETTER, end);
	}

	private static void appendCJK(BrkArray brk, int index) {
		int lastCategory = (int) brk.last() >>> 32;
		if (lastCategory == Token.CATEGORY_CJK) {
			brk.removeLast();
		}
		addBrk(brk, Token.CATEGORY_CJK, index);
	}

	private static boolean isSimpleWord(int codePoint) {
		return (codePoint >= 'a' && codePoint <= 'z') || (codePoint >= 'A' && codePoint <= 'Z');
	}

	private static void addBrk(BrkArray buffer, int category, int index) {
		long v = category;
		v <<= 32;
		v += index;
		buffer.add(v);
	}

	@Nullable
	public Token next() {
		Token token = get(mIndex);
		if (token != null) {
			++mIndex;
		}
		return token;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = mIndex; i < mBrk.size(); ++i) {
			long v = mBrk.get(i);
			builder.append("(");
			builder.append((int) v);
			builder.append(",");
			builder.append(v >>> 32);
			builder.append(")");
		}
		return builder.toString();
	}

	private static final class BrkArray extends LongArray {
		public BrkArray(int size) {
			super(size);
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < size(); ++i) {
				long v = get(i);
				builder.append("(");
				builder.append((int) v);
				builder.append(",");
				builder.append(v >>> 32);
				builder.append(")");
			}
			return builder.toString();
		}
	}

	@Nullable
	private Token get(int index) {
		return get0(mBrk, mIterator.getSeq(), index);
	}

	private static Token get0(LongArray brk, CharSequence text, int index) {
		if (index + 1 >= brk.size() || index < 0) {
			return null;
		}

		long start = brk.get(index);
		long end = brk.get(index + 1);
		Token token = Token.obtain();
		token.mCharSequence = text;
		token.mStart = (int) start;
		token.mEnd = (int) end;
		token.mCategory = (int) (end >>> 32);
		return token;
	}

	public int save() {
		return mIndex;
	}

	public void restore(int status) {
		mIndex = status;
	}

	public Token tryGet(int state, int offset) {
		int index = state + offset;
		return get(index);
	}

	public boolean hasNext() {
		return mIndex + 1 < mBrk.size();
	}

	public void reset() {
		restore(0);
	}
}
