package me.chan.texas.text.tokenizer;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import com.ibm.icu.text.BreakIterator;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.text.icu.UnicodeUtils;
import me.chan.texas.trace.TraceEvent;
import me.chan.texas.utils.LongArray;

class TextTokenStream extends DefaultRecyclable implements TokenStream {
	private static final ObjectPool<TextTokenStream> POOL = new ObjectPool<>(4);

	private final CharacterSequenceIterator mIterator = new CharacterSequenceIterator();
	private final BrkArray mBrk = new BrkArray(128);
	private int mIndex = 0;

	@Override
	protected void onRecycle() {
		mIndex = 0;
		POOL.release(this);
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public CharSequence getText() {
		return mIterator.getSeq();
	}

	@VisibleForTesting
	void setText(CharSequence text, int start, int end) {
		setText(text, start, end, false);
	}

	void setText(CharSequence text, int start, int end, boolean rtl) {
		synchronized (TextTokenStream.class /* 粒度必须要粗 */) {
			setText0(text, start, end, rtl);
		}
	}

	private void setText0(CharSequence text, int start, int end, boolean rtl) {
		mBrk.clear();
		mIndex = 0;

		BreakIterator boundary = WordBreaker.getWordBreakIterator();
		boundary.setText(mIterator.reset(text, start, end));

		addBrk(mBrk, Token.CATEGORY_NONE, boundary.first() + start);
		for (int brk = boundary.next();
			 brk != BreakIterator.DONE; brk = boundary.next()) {
			int reason = boundary.getRuleStatus();
			if (reason >= BreakIterator.WORD_LETTER && reason < BreakIterator.WORD_LETTER_LIMIT) {
				appendLatter(mBrk, text, brk + start);
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

			if (reason >= BreakIterator.WORD_NONE && reason < BreakIterator.WORD_NONE_LIMIT) {
				appendUnknown(mBrk, text, brk + start);
				continue;
			}

			throw new IllegalStateException("unknown token type");
		}
	}

	private void appendLatter(BrkArray brk, CharSequence text, int end) {
		final int start = (int) brk.last();
		boolean simpleWord = true;
		for (int i = start; i < end; ++i) {
			int codePoint = text.charAt(i);
			if (codePoint > 128) {
				simpleWord = false;
				break;
			}
		}

		if (simpleWord) {
			addBrk(brk, Token.CATEGORY_NORMAL, end);
			return;
		}

		int codePoint = text.charAt(start);
		if (UnicodeUtils.isCJKExtends(codePoint)) {
			appendCJK(brk, end);
			return;
		}

		addBrk(brk, Token.CATEGORY_UNKNOWN_LETTER, end);
	}

	private void appendUnknown(BrkArray brk, CharSequence text, int end) {
		final int start = (int) brk.last();

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
			appendSymbolOrPunctuation(brk, Token.CATEGORY_SYMBOL, type, codePoint, end);
			if (end - start > 1) {
				TraceEvent.error("TokenStream, unknown symbol1: " + text.subSequence(start, end));
			}
			return;
		}

		if (type == Character.DASH_PUNCTUATION
				|| type == Character.END_PUNCTUATION
				|| type == Character.FINAL_QUOTE_PUNCTUATION
				|| type == Character.INITIAL_QUOTE_PUNCTUATION
				|| type == Character.OTHER_PUNCTUATION
				|| type == Character.START_PUNCTUATION) {
			appendSymbolOrPunctuation(brk, Token.CATEGORY_PUNCTUATION, type, codePoint, end);
			if (end - start > 1) {
				TraceEvent.error("TokenStream, unknown symbol2: " + text.subSequence(start, end));
			}
			return;
		}

		addBrk(brk, Token.CATEGORY_UNKNOWN_LETTER, end);
	}

	private static void appendSymbolOrPunctuation(BrkArray brk, byte category, int type, int codePoint, int index) {
		addBrk0(brk, category, getAdvise(codePoint, type), index);
	}

	@VisibleForTesting
	static int getAdvise(int codePoint, int type) {
		int mask = getSquishAdvise(codePoint);
		if (mask == 0) {
			mask = getStretchAdvise(codePoint);
		}
		return mask | getKinsokuAdvise(codePoint, type);
	}

	private static void appendCJK(BrkArray brk, int index) {
		byte lastCategory = (byte) (brk.last() >>> 32);
		if (lastCategory == Token.CATEGORY_CJK) {
			brk.removeLast();
		}
		addBrk(brk, Token.CATEGORY_CJK, index);
	}

	private static void addBrk(BrkArray buffer, byte category, int index) {
		addBrk0(buffer, category, 0, index);
	}

	private static void addBrk0(BrkArray buffer, byte category, long advise, int index) {
		long v = getMask(category, advise);
		v <<= 32;
		v += index;
		buffer.add(v);
	}

	@VisibleForTesting
	static int getMask(byte category, long advise) {
		return (int) (advise << 8 | category);
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
		token.mMask = (int) (end >>> 32);

		int category = token.getCategory();
		if (category >= Token.CATEGORY_WORD_LIMITED) {
			token.mType = Token.TYPE_WORD;
		} else if (category == Token.CATEGORY_CONTROL) {
			token.mType = Token.TYPE_CONTROL;
		} else if (category == Token.CATEGORY_PUNCTUATION ||
				category == Token.CATEGORY_SYMBOL) {
			token.mType = Token.TYPE_SYMBOL;
		} else if (category == Token.CATEGORY_NONE) {
			token.mType = Token.TYPE_NONE;
		} else {
			throw new IllegalStateException("unknown token type");
		}

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

	public Token tryGet(int offset) {
		return tryGet(save(), offset);
	}

	@Override
	public int size() {
		return mBrk.size() - 1;
	}

	public boolean hasNext() {
		return mIndex + 1 < mBrk.size();
	}

	public void reset() {
		restore(0);
	}

	@VisibleForTesting
	public static final char[] SQUISH_RIGHT_MAP = {
			'〉', /* \u232a */
			'、', /* \u3001 */
			'。', /* \u3002 */
			'〉', /* \u3009 */
			'》', /* \u300b */
			'」', /* \u300d */
			'』', /* \u300f */
			'】', /* \u3011 */
			'〕', /* \u3015 */
			'〗', /* \u3017 */
			'〙', /* \u3019 */
			'〛', /* \u301b */
			'〞', /* \u301e */
			'〟', /* \u301f */
			'﹂', /* \ufe42 */
			'﹄', /* \ufe44 */
			'﹚', /* \ufe5a */
			'﹜', /* \ufe5c */
			'﹞', /* \ufe5e */
			'！', /* \uff01 */
			'）', /* \uff09 */
			'，', /* \uff0c */
			'．', /* \uff0e */
			'：', /* \uff1a */
			'；', /* \uff1b */
			'？', /* \uff1f */
			'］', /* \uff3d */
			'｠', /* \uff60 */
	};
	@VisibleForTesting
	public static final char[] SQUISH_LEFT_MAP = {
			'〈', /* \u2329 */
			'〈', /* \u3008 */
			'《', /* \u300a */
			'「', /* \u300c */
			'『', /* \u300e */
			'【', /* \u3010 */
			'〔', /* \u3014 */
			'〖', /* \u3016 */
			'〘', /* \u3018 */
			'〚', /* \u301a */
			'〝', /* \u301d */
			'︐', /* \ufe10 */
			'︑', /* \ufe11 */
			'︒', /* \ufe12 */
			'︓', /* \ufe13 */
			'︔', /* \ufe14 */
			'︕', /* \ufe15 */
			'︖', /* \ufe16 */
			'﹁', /* \ufe41 */
			'﹃', /* \ufe43 */
			'﹙', /* \ufe59 */
			'﹛', /* \ufe5b */
			'﹝', /* \ufe5d */
			'（', /* \uff08 */
			'［', /* \uff3b */
			'｟', /* \uff5f */
	};

	@VisibleForTesting
	public static final char[] STRETCH_RIGHT_MAP = {
			'!', /* \u0021 */
			')', /* \u0029 */
			',', /* \u002c */
			'.', /* \u002e */
			':', /* \u003a */
			';', /* \u003b */
			'>', /* \u003e */
			'?', /* \u003f */
			']', /* \u005d */
			'}', /* \u007d */
			'»', /* \u00bb */
			';', /* \u037e */
			'༻', /* \u0f3b */
			'༽', /* \u0f3d */
			'᚜', /* \u169c */
			'’', /* \u2019 */
			'”', /* \u201d */
			'›', /* \u203a */
			'⁆', /* \u2046 */
			'⁾', /* \u207e */
			'₎', /* \u208e */
			'⌉', /* \u2309 */
			'⌋', /* \u230b */
			'〉', /* \u232a */
			'❩', /* \u2769 */
			'❫', /* \u276b */
			'❭', /* \u276d */
			'❯', /* \u276f */
			'❱', /* \u2771 */
			'❳', /* \u2773 */
			'❵', /* \u2775 */
			'⟆', /* \u27c6 */
			'⟧', /* \u27e7 */
			'⟩', /* \u27e9 */
			'⟫', /* \u27eb */
			'⟭', /* \u27ed */
			'⟯', /* \u27ef */
			'⦄', /* \u2984 */
			'⦆', /* \u2986 */
			'⦈', /* \u2988 */
			'⦊', /* \u298a */
			'⦌', /* \u298c */
			'⦎', /* \u298e */
			'⦐', /* \u2990 */
			'⦒', /* \u2992 */
			'⦔', /* \u2994 */
			'⦖', /* \u2996 */
			'⦘', /* \u2998 */
			'⧙', /* \u29d9 */
			'⧛', /* \u29db */
			'⧽', /* \u29fd */
			'⸃', /* \u2e03 */
			'⸅', /* \u2e05 */
			'⸊', /* \u2e0a */
			'⸍', /* \u2e0d */
			'⸝', /* \u2e1d */
			'⸡', /* \u2e21 */
			'⸣', /* \u2e23 */
			'⸥', /* \u2e25 */
			'⸧', /* \u2e27 */
			'⸩', /* \u2e29 */
			'﴾', /* \ufd3e */
			'︘', /* \ufe18 */
			'︶', /* \ufe36 */
			'︸', /* \ufe38 */
			'︺', /* \ufe3a */
			'︼', /* \ufe3c */
			'︾', /* \ufe3e */
			'﹀', /* \ufe40 */
			'﹂', /* \ufe42 */
			'﹄', /* \ufe44 */
			'﹈', /* \ufe48 */
			'｝', /* \uff5d */
			'｡', /* \uff61 */
			'｣', /* \uff63 */
			'､', /* \uff64 */
	};

	@VisibleForTesting
	public static final char[] STRETCH_LEFT_MAP = {
			'(', /* \u0028 */
			'<', /* \u003c */
			'[', /* \u005b */
			'{', /* \u007b */
			'«', /* \u00ab */
			'¿', /* \u00bf */
			'༺', /* \u0f3a */
			'༼', /* \u0f3c */
			'᚛', /* \u169b */
			'‘', /* \u2018 */
			'‚', /* \u201a */
			'‛', /* \u201b */
			'“', /* \u201c */
			'„', /* \u201e */
			'‟', /* \u201f */
			'‹', /* \u2039 */
			'⁅', /* \u2045 */
			'⁽', /* \u207d */
			'₍', /* \u208d */
			'⌈', /* \u2308 */
			'⌊', /* \u230a */
			'〈', /* \u2329 */
			'❨', /* \u2768 */
			'❪', /* \u276a */
			'❬', /* \u276c */
			'❮', /* \u276e */
			'❰', /* \u2770 */
			'❲', /* \u2772 */
			'❴', /* \u2774 */
			'⟅', /* \u27c5 */
			'⟦', /* \u27e6 */
			'⟨', /* \u27e8 */
			'⟪', /* \u27ea */
			'⟬', /* \u27ec */
			'⟮', /* \u27ee */
			'⦃', /* \u2983 */
			'⦅', /* \u2985 */
			'⦇', /* \u2987 */
			'⦉', /* \u2989 */
			'⦋', /* \u298b */
			'⦍', /* \u298d */
			'⦏', /* \u298f */
			'⦑', /* \u2991 */
			'⦓', /* \u2993 */
			'⦕', /* \u2995 */
			'⦗', /* \u2997 */
			'⧘', /* \u29d8 */
			'⧚', /* \u29da */
			'⧼', /* \u29fc */
			'⸂', /* \u2e02 */
			'⸄', /* \u2e04 */
			'⸉', /* \u2e09 */
			'⸌', /* \u2e0c */
			'⸜', /* \u2e1c */
			'⸠', /* \u2e20 */
			'⸢', /* \u2e22 */
			'⸤', /* \u2e24 */
			'⸦', /* \u2e26 */
			'⸨', /* \u2e28 */
			'︗', /* \ufe17 */
			'︵', /* \ufe35 */
			'︷', /* \ufe37 */
			'︹', /* \ufe39 */
			'︻', /* \ufe3b */
			'︽', /* \ufe3d */
			'︿', /* \ufe3f */
			'﹇', /* \ufe47 */
			'｛', /* \uff5b */
			'｢', /* \uff62 */
	};

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static int getStretchAdvise(int codePoint) {
		int index = binarySearch(STRETCH_RIGHT_MAP, codePoint);
		if (index >= 0) {
			return Token.SYMBOL_STRETCH_RIGHT;
		}

		index = binarySearch(STRETCH_LEFT_MAP, codePoint);
		if (index >= 0) {
			return Token.SYMBOL_STRETCH_LEFT;
		}

		return 0;
	}

	@VisibleForTesting
	public static int getKinsokuAdvise(int codePoint) {
		return getKinsokuAdvise(codePoint, Character.getType(codePoint));
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static int getKinsokuAdvise(int codePoint, int type) {
		if (type == Character.START_PUNCTUATION ||
				type == Character.INITIAL_QUOTE_PUNCTUATION ||
				type == Character.CURRENCY_SYMBOL) {
			return Token.SYMBOL_KINSOKU_AVOID_TAIL;
		}

		if (type == Character.FINAL_QUOTE_PUNCTUATION ||
				type == Character.END_PUNCTUATION ||
				type == Character.DASH_PUNCTUATION) {
			return Token.SYMBOL_KINSOKU_AVOID_HEADER;
		}

		if (type == Character.OTHER_PUNCTUATION) {
			if (codePoint == '&' || codePoint == '@' || codePoint == '·') {
				return Token.SYMBOL_KINSOKU_MASK;
			}
//
//			if (codePoint == '"' || codePoint == '\'') {
//				return Token.SYMBOL_KINSOKU_AVOID_HEADER;
//			}

			return Token.SYMBOL_KINSOKU_AVOID_HEADER;
		}

		if (type == Character.MATH_SYMBOL || type == Character.MODIFIER_SYMBOL || type == Character.OTHER_SYMBOL) {
			return Token.SYMBOL_KINSOKU_MASK;
		}

		return 0;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static int getSquishAdvise(int codePoint) {
		int index = binarySearch(SQUISH_RIGHT_MAP, codePoint);
		if (index >= 0) {
			return Token.SYMBOL_SQUISH_RIGHT;
		}

		index = binarySearch(SQUISH_LEFT_MAP, codePoint);
		if (index >= 0) {
			return Token.SYMBOL_SQUISH_LEFT;
		}

		return 0;
	}

	private static int binarySearch(char[] array, int value) {
		if (value < array[0] || value > array[array.length - 1]) {
			return -1;
		}

		int lo = 0;
		int hi = array.length - 1;

		// not found
		if (value < array[lo] || value > array[hi]) {
			return -1;
		}

		while (lo <= hi) {
			final int mid = (lo + hi) >>> 1;
			final int midVal = array[mid];

			if (midVal < value) {
				lo = mid + 1;
			} else if (midVal > value) {
				hi = mid - 1;
			} else {
				return mid;  // value found
			}
		}

		/* not found */
		return -1;
	}

	public static TextTokenStream obtain(CharSequence text, int start, int end, boolean rtl) {
		TextTokenStream tokenStream = POOL.acquire();
		if (tokenStream == null) {
			tokenStream = new TextTokenStream();
		}

		tokenStream.setText(text, start, end, rtl);
		tokenStream.reuse();
		return tokenStream;
	}
}
