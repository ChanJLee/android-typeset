package me.chan.texas.text.tokenizer;

import androidx.annotation.Nullable;

import com.ibm.icu.text.BreakIterator;

import java.text.CharacterIterator;

import me.chan.texas.utils.LongArray;

class WordStream {
	/**
	 * Tag value for "words" that do not fit into any of other categories.
	 * Includes spaces and most punctuation.
	 *
	 * @stable ICU 53
	 */
	public static final int WORD_NONE = BreakIterator.WORD_NONE;

	/**
	 * Upper bound for tags for uncategorized words.
	 *
	 * @stable ICU 53
	 */
	public static final int WORD_NONE_LIMIT = BreakIterator.WORD_NONE_LIMIT;

	/**
	 * Tag value for words that appear to be numbers, lower limit.
	 *
	 * @stable ICU 53
	 */
	public static final int WORD_NUMBER = BreakIterator.WORD_NUMBER;

	/**
	 * Tag value for words that appear to be numbers, upper limit.
	 *
	 * @stable ICU 53
	 */
	public static final int WORD_NUMBER_LIMIT = BreakIterator.WORD_NUMBER_LIMIT;

	/**
	 * Tag value for words that contain letters, excluding
	 * hiragana, katakana or ideographic characters, lower limit.
	 *
	 * @stable ICU 53
	 */
	public static final int WORD_LETTER = BreakIterator.WORD_LETTER;

	/**
	 * Tag value for words containing letters, upper limit
	 *
	 * @stable ICU 53
	 */
	public static final int WORD_LETTER_LIMIT = BreakIterator.WORD_LETTER_LIMIT;

	/**
	 * Tag value for words containing kana characters, lower limit
	 *
	 * @stable ICU 53
	 */
	public static final int WORD_KANA = BreakIterator.WORD_KANA;

	/**
	 * Tag value for words containing kana characters, upper limit
	 *
	 * @stable ICU 53
	 */
	public static final int WORD_KANA_LIMIT = BreakIterator.WORD_KANA_LIMIT;

	/**
	 * Tag value for words containing ideographic characters, lower limit
	 *
	 * @stable ICU 53
	 */
	public static final int WORD_IDEO = BreakIterator.WORD_IDEO;

	/**
	 * Tag value for words containing ideographic characters, upper limit
	 *
	 * @stable ICU 53
	 */
	public static final int WORD_IDEO_LIMIT = BreakIterator.WORD_IDEO_LIMIT;


	private final CharacterIterator0 mIterator0 = new CharacterIterator0();
	private final LongArray mBrk = new LongArray(128);
	private int mIndex = 0;

	public void setText(CharSequence text, int start, int end) {
		BreakIterator boundary = BreakIterator.getWordInstance();
		boundary.setText(mIterator0.reset(text, start, end));

		mBrk.clear();
		mIndex = 0;

		addBrk(boundary.getRuleStatus(), boundary.first() + start);
		for (int brk = boundary.next();
			 brk != BreakIterator.DONE; brk = boundary.next()) {
			addBrk(boundary.getRuleStatus(), brk + start);
		}
	}

	private void addBrk(int reason, int index) {
		long v = reason;
		v <<= 32;
		v += index;
		mBrk.add(v);
	}

	@Nullable
	public Token next() {
		if (mIndex + 1 >= mBrk.size()) {
			return null;
		}

		long start = (mBrk.get(mIndex));
		int end = (int) mBrk.get(++mIndex);
		Token token = Token.obtain();
		token.mCharSequence = mIterator0.seq;
		token.mStart = (int) start;
		token.mEnd = end;
		token.mReason = (int) (start >> 32);
		return token;
	}

	@Nullable
	public Token prev() {
		if (mIndex - 1 < 0) {
			return null;
		}

		int end = (int) mBrk.get(mIndex);
		long start = mBrk.get(--mIndex);
		Token token = Token.obtain();
		token.mCharSequence = mIterator0.seq;
		token.mStart = (int) start;
		token.mEnd = end;
		token.mReason = (int) (start >> 32);
		return token;
	}

	public int save() {
		return mIndex;
	}

	public void restore(int status) {
		mIndex = status;
	}

	private static class CharacterIterator0 implements CharacterIterator {
		private int index;
		private CharSequence seq;
		private int start;
		private int size;

		public CharacterIterator reset(CharSequence text, int start, int end) {
			seq = text;
			this.start = start;
			size = end - start;
			index = 0;
			return this;
		}

		@Override
		public char first() {
			index = 0;
			return current();
		}

		@Override
		public char last() {
			index = size;
			return previous();
		}

		@Override
		public char current() {
			if (index == size) {
				return DONE;
			}
			return seq.charAt(index + start);
		}

		@Override
		public char next() {
			if (index < size) {
				++index;
			}
			return current();
		}

		@Override
		public char previous() {
			if (index == 0) {
				return DONE;
			}
			--index;
			return current();
		}

		@Override
		public char setIndex(int position) {
			if (position < 0 || position > size) {
				throw new IllegalArgumentException();
			}
			index = position;
			return current();
		}

		@Override
		public int getBeginIndex() {
			return 0;
		}

		@Override
		public int getEndIndex() {
			return size;
		}

		@Override
		public int getIndex() {
			return index;
		}

		@Override
		public Object clone() {
			CharacterIterator0 copy = new CharacterIterator0();
			copy.start = this.start;
			copy.seq = this.seq;
			copy.index = this.index;
			copy.size = this.size;
			return copy;
		}

		@Override
		public String toString() {
			return seq.subSequence(start, start + size).toString();
		}
	}
}
