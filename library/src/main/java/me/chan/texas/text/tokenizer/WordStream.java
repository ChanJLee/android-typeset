package me.chan.texas.text.tokenizer;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.ibm.icu.text.BreakIterator;
import com.ibm.icu.text.RuleBasedBreakIterator;

import java.io.IOException;
import java.text.CharacterIterator;

import me.chan.texas.Texas;
import me.chan.texas.text.icu.UnicodeUtils;
import me.chan.texas.utils.CharStream;
import me.chan.texas.utils.IntArray;
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


	private final CharacterSequenceIterator mIterator = new CharacterSequenceIterator();
	private final CharStream mStream = new CharStream();
	private final LongArray mBrk = new LongArray(128);
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

		sent(mBrk, mStream);
	}

	private void sent(LongArray brk, CharStream stream) {
		while (!stream.eof()) {
			if (!unit(brk, stream)) {
				throw new IllegalStateException("parse state error");
			}
		}
	}

	private boolean unit(LongArray brk, CharStream stream) {
		return word(brk, stream) || ws(brk, stream);
	}

	private boolean word(LongArray brk, CharStream stream) {
		int save = stream.save();
		int codePoint = stream.eat();
		if (!UnicodeUtils.isBreakTokenSymbol(codePoint)) {
			stream.restore(save);
			return false;
		}

		int start = stream.save();
		boolean simple = true;
		while (!stream.eof()) {
			codePoint = stream.eat();
			if (UnicodeUtils.isBreakTokenSymbol(codePoint)) {
				stream.back();
				word0(brk, stream.getText(), start, stream.save(), simple);
				break;
			}

			if (!(codePoint >= 'a' && codePoint <= 'z') && !(codePoint >= 'A' && codePoint <= 'Z')) {
				simple = false;
			}
		}
		return true;
	}

	private final LongArray mPending = new LongArray(32);

	private void word0(LongArray brk, CharSequence text, int start, int end, boolean simple) {
		if (simple) {
			addBrk(brk, BreakIterator.WORD_LETTER, end);
			return;
		}

		BreakIterator boundary = BreakIterator.getWordInstance();
		boundary.setText(mIterator.reset(text, start, end));

		mBrk.clear();
		mIndex = 0;

		mPending.clear();
		addBrk(mPending, WORD_NONE, start);
		boundary.getRuleStatus();
		for (end = boundary.next();
			 end != BreakIterator.DONE; end = boundary.next()) {
			addBrk(mPending, boundary.getRuleStatus(), start + end);
		}

		mBrk.add(mPending.get(0));
		for (int i = 1; i < mPending.size(); ++i) {
			long v = mPending.get(i);
			int reason = (int) (v >>> 32);
			if (reason >= WORD_NUMBER && reason < WORD_NUMBER_LIMIT
					|| reason >= WORD_KANA && reason < WORD_KANA_LIMIT
					|| reason >= WORD_IDEO && reason < WORD_IDEO_LIMIT) {
				int prev = (int) mBrk.back();
				nlp(mBrk, text, prev, (int) v);
				mBrk.add(v);
			}
		}
	}

	private static boolean ws(LongArray brk, CharStream stream) {
		addBrk(brk, WORD_NONE, stream.save());
		stream.eat();
		return true;
	}

	private static void addBrk(LongArray buffer, int reason, int index) {
		long v = reason;
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

	@Nullable
	private Token get(int index) {
		return get0(mBrk, mIterator.seq, index);
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
		token.mReason = (int) (end >>> 32);
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

	private void nlp(LongArray brk, CharSequence text, int start, int end) {
		Tokenizer tokenizer = getTokenizer();
		if (tokenizer == null) {
			return;
		}

		SpanStream stream = tokenizer.tokenize(text, start, end);
		while (stream.hasNext()) {
			SpanStream.Span span = stream.next();
			// 看下首字母
			int codePoint = text.charAt(span.getStart());
			boolean isSymbolsAndPunctuation = UnicodeUtils.isSymbolsAndPunctuation(codePoint);
			addBrk(brk, isSymbolsAndPunctuation ? WORD_NONE : WORD_LETTER, span.getEnd());
		}
	}

	private static class CharacterSequenceIterator implements CharacterIterator {
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
			CharacterSequenceIterator copy = new CharacterSequenceIterator();
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

	private static final String WORD_BREAKER_US_RULE = "!!chain;\n" +
			"!!quoted_literals_only;\n" +
			"$Han=[:Han:];\n" +
			"$CR=[\\p{Word_Break=CR}];\n" +
			"$LF=[\\p{Word_Break=LF}];\n" +
			"$Newline=[\\p{Word_Break=Newline}];\n" +
			"$Extend=[\\p{Word_Break=Extend}-$Han];\n" +
			"$ZWJ=[\\p{Word_Break=ZWJ}];\n" +
			"$Regional_Indicator=[\\p{Word_Break=Regional_Indicator}];\n" +
			"$Format=[\\p{Word_Break=Format}];\n" +
			"$Katakana=[\\p{Word_Break=Katakana}];\n" +
			"$Hebrew_Letter=[\\p{Word_Break=Hebrew_Letter}];\n" +
			"$ALetter=[\\p{Word_Break=ALetter}];\n" +
			"$Single_Quote=[\\p{Word_Break=Single_Quote}];\n" +
			"$Double_Quote=[\\p{Word_Break=Double_Quote}];\n" +
			"$MidNumLet=[\\p{Word_Break=MidNumLet}];\n" +
			"$MidLetter=[\\p{Word_Break=MidLetter}-[\\:\\uFE55\\uFF1A]\\-\\u2011\\u00AD\\u2013\\u2014];\n" +
			"$MidNum=[\\p{Word_Break=MidNum}];\n" +
			"$Numeric=[\\p{Word_Break=Numeric}];\n" +
			"$ExtendNumLet=[\\p{Word_Break=ExtendNumLet}];\n" +
			"$WSegSpace=[\\p{Word_Break=WSegSpace}];\n" +
			"$Extended_Pict=[\\p{Extended_Pictographic}];\n" +
			"$Hiragana=[:Hiragana:];\n" +
			"$Ideographic=[\\p{Ideographic}];\n" +
			"$Control=[\\p{Grapheme_Cluster_Break=Control}];\n" +
			"$HangulSyllable=[\\uac00-\\ud7a3];\n" +
			"$ComplexContext=[:LineBreak=Complex_Context:];\n" +
			"$KanaKanji=[$Han$Hiragana$Katakana];\n" +
			"$dictionaryCJK=[$KanaKanji$HangulSyllable];\n" +
			"$dictionary=[$ComplexContext$dictionaryCJK];\n" +
			"$ALetterPlus=[$ALetter-$dictionaryCJK[$ComplexContext-$Extend-$Control]];\n" +
			"$CR$LF;\n" +
			"$ZWJ$Extended_Pict;\n" +
			"$WSegSpace$WSegSpace;\n" +
			"$ExFm=[$Extend$Format$ZWJ];\n" +
			"^$ExFm+;\n" +
			"[^$CR$LF$Newline$ExFm]$ExFm*;\n" +
			"$Numeric$ExFm*{100};\n" +
			"$ALetterPlus$ExFm*{200};\n" +
			"$HangulSyllable{400};\n" + /* 韩文返回400 */
			"$Hebrew_Letter$ExFm*{200};\n" +
			"$Katakana$ExFm*{400};\n" +
			"$Hiragana$ExFm*{400};\n" +
			"$Ideographic$ExFm*{400};\n" +
			"($ALetterPlus|$Hebrew_Letter)$ExFm*($ALetterPlus|$Hebrew_Letter);\n" +
			"($ALetterPlus|$Hebrew_Letter)$ExFm*($MidLetter|$MidNumLet|$Single_Quote)$ExFm*($ALetterPlus|$Hebrew_Letter){200};\n" +
			"$Hebrew_Letter$ExFm*$Single_Quote{200};\n" +
			"$Hebrew_Letter$ExFm*$Double_Quote$ExFm*$Hebrew_Letter;\n" +
			"$Numeric$ExFm*$Numeric;\n" +
			"($ALetterPlus|$Hebrew_Letter)$ExFm*$Numeric;\n" +
			"$Numeric$ExFm*($ALetterPlus|$Hebrew_Letter);\n" +
			"$Numeric$ExFm*($MidNum|$MidNumLet|$Single_Quote)$ExFm*$Numeric;\n" +
			"$Katakana$ExFm*$Katakana{400};\n" +
			"$ALetterPlus$ExFm*$ExtendNumLet{200};\n" +
			"$Hebrew_Letter$ExFm*$ExtendNumLet{200};\n" +
			"$Numeric$ExFm*$ExtendNumLet{100};\n" +
			"$Katakana$ExFm*$ExtendNumLet{400};\n" +
			"$ExtendNumLet$ExFm*$ExtendNumLet{200};\n" +
			"$ExtendNumLet$ExFm*$ALetterPlus{200};\n" +
			"$ExtendNumLet$ExFm*$Hebrew_Letter{200};\n" +
			"$ExtendNumLet$ExFm*$Numeric{100};\n" +
			"$ExtendNumLet$ExFm*$Katakana{400};\n" +
			"^$Regional_Indicator$ExFm*$Regional_Indicator;\n" +
			"$HangulSyllable$HangulSyllable{400};\n" + /* 韩文返回400 */
			"$KanaKanji$KanaKanji{400};\n" +
			".;\n";
	private static BreakIterator sWordBreakIterator;

	private static BreakIterator getWordBreakIterator() {
		if (sWordBreakIterator == null) {
			sWordBreakIterator = new RuleBasedBreakIterator(WORD_BREAKER_US_RULE);
		}
		return sWordBreakIterator;
	}

	private static Tokenizer getTokenizer() {
		Context context = Texas.getAppContext();
		try {
			return Tokenizer.getInstance(context);
		} catch (IOException e) {
			Log.w("TokenStream", e);
		}
		return null;
	}
}
