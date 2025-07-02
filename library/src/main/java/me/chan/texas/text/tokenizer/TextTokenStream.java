package me.chan.texas.text.tokenizer;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import com.ibm.icu.text.BreakIterator;

import me.chan.texas.misc.BitBucket32;
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
	private boolean mRtl = false;
	private final BitBucket32 mBits = new BitBucket32();

	@Override
	protected void onRecycle() {
		mIndex = 0;
		mRtl = false;
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
		synchronized (TextTokenStream.class ) {
			setText0(text, start, end, rtl);
		}
	}

	private void setText0(CharSequence text, int start, int end, boolean rtl) {
		mBrk.clear();
		mIndex = 0;
		mRtl = rtl;

		BreakIterator boundary = WordBreaker.getWordBreakIterator();
		boundary.setText(mIterator.reset(text, start, end));

		addBrk(mBrk, 0, boundary.first() + start);
		for (int brk = boundary.next();
			 brk != BreakIterator.DONE; brk = boundary.next()) {
			int reason = boundary.getRuleStatus();
			if (reason >= BreakIterator.WORD_LETTER && reason < BreakIterator.WORD_LETTER_LIMIT) {
				appendLatter(mBrk, text, brk + start);
				continue;
			}

			if (reason >= BreakIterator.WORD_NUMBER && reason < BreakIterator.WORD_NUMBER_LIMIT) {
				mBits.clear();
				mBits.set(Token.TYPE_WORD);
				mBits.set(Token.CATEGORY_NUMBER);
				mBits.set(Token.DIRECTION_RTL, mRtl);
				addBrk(mBrk, mBits.getBits(), brk + start);
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
			if (codePoint > 128 && !UnicodeUtils.isSymbolsAndPunctuation(codePoint)) {
				simpleWord = false;
				break;
			}
		}

		if (simpleWord) {
			mBits.clear();
			mBits.set(Token.TYPE_WORD);
			mBits.set(Token.CATEGORY_NORMAL);
			mBits.set(Token.DIRECTION_RTL, mRtl);
			addBrk(brk, mBits.getBits(), end);
			return;
		}

		int codePoint = text.charAt(start);
		if (UnicodeUtils.isCJKExtends(codePoint)) {
			appendCJK(brk, end);
			return;
		}

		mBits.clear();
		mBits.set(Token.TYPE_WORD);
		mBits.set(Token.CATEGORY_UNKNOWN_LETTER);
		mBits.set(Token.DIRECTION_RTL, mRtl);
		addBrk(brk, mBits.getBits(), end);
	}

	private void appendUnknown(BrkArray brk, CharSequence text, int end) {

		final int start = (int) brk.last();

		int codePoint = text.charAt(start);
		if (codePoint == ' ') {
			appendControl(brk, Character.SPACE_SEPARATOR, codePoint, end);
			return;
		}

		int type = Character.getType(codePoint);
		if (type == Character.MATH_SYMBOL
				
				|| type == Character.CURRENCY_SYMBOL
				
				|| type == Character.MODIFIER_SYMBOL
				
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

		if (type == Character.CONTROL ||
				type == Character.SPACE_SEPARATOR ||
				type == Character.LINE_SEPARATOR ||
				type == Character.PARAGRAPH_SEPARATOR ||
				type == Character.COMBINING_SPACING_MARK ||
				type == Character.FORMAT) {
			appendControl(brk, (byte) type, codePoint, end);
			return;
		}

		mBits.clear();
		mBits.set(Token.TYPE_WORD);
		mBits.set(Token.CATEGORY_UNKNOWN_LETTER);
		mBits.set(Token.DIRECTION_RTL, mRtl);
		addBrk(brk, mBits.getBits(), end);
	}

	private void appendControl(BrkArray brk, byte type, int codePoint, int index) {




		mBits.clear();
		mBits.set(Token.TYPE_CONTROL);
		if (type == Character.SPACE_SEPARATOR) {
			mBits.set(Token.CONTROL_ATTRIBUTE_SPACE);
		} else if (codePoint == '\n' || type == Character.PARAGRAPH_SEPARATOR || type == Character.LINE_SEPARATOR) {
			mBits.set(Token.CONTROL_ATTRIBUTE_NEW_LINE);
		} else if (codePoint == '\t') {
			mBits.set(Token.CONTROL_ATTRIBUTE_TAB_HORIZONTAL);
		}
		mBits.set(Token.DIRECTION_RTL, mRtl);
		addBrk(brk, mBits.getBits(), index);
	}

	private void appendSymbolOrPunctuation(BrkArray brk, byte category, int type, int codePoint, int index) {
		mBits.clear();
		mBits.set(Token.TYPE_SYMBOL);
		mBits.set(category);
		setupAdvise(mBits, type, codePoint);
		mBits.set(Token.DIRECTION_RTL, mRtl);

		addBrk(brk, mBits.getBits(), index);
	}

	@VisibleForTesting
	static void setupAdvise(BitBucket32 bits, int type, int codePoint) {
		if (!setupSquishAdvise(bits, codePoint)) {
			setupStretchAdvise(bits, codePoint);
		}

		setupKinsokuAdvise(bits, codePoint, type);
	}

	private void appendCJK(BrkArray brk, int index) {
		mBits.reset((int) (brk.last() >>> 32));
		if (mBits.get(Token.TYPE_WORD) && mBits.get(Token.CATEGORY_CJK)) {
			brk.removeLast();
		}

		mBits.clear();
		mBits.set(Token.TYPE_WORD);
		mBits.set(Token.CATEGORY_CJK);
		mBits.set(Token.DIRECTION_RTL, mRtl);

		addBrk(brk, mBits.getBits(), index);
	}

	private void addBrk(BrkArray buffer, int mask, int index) {
		long v = mask;
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
		return get0(mBrk, mIterator.getSeq(), index, mRtl);
	}

	private Token get0(LongArray brk, CharSequence text, int index, boolean rtl) {
		if (rtl) {
			index = size() - index - 1;
		}

		if (index + 1 >= brk.size() || index < 0) {
			return null;
		}

		long start = brk.get(index);
		long end = brk.get(index + 1);
		Token token = Token.obtain();
		token.mCharSequence = text;
		token.mStart = (int) start;
		token.mEnd = (int) end;

		mBits.reset((int) (end >>> 32));
		token.mType = (byte) (bit2Value(mBits.getRange(Token.BIT_TYPE_START, Token.BIT_TYPE_END), Token.BIT_TYPE_START));
		if (token.mType != Token.TYPE_CONTROL) {
			token.mCategory = (byte) (bit2Value(mBits.getRange(Token.BIT_CATEGORY_START, Token.BIT_CATEGORY_END), Token.BIT_CATEGORY_START));
		}
		if (token.mType == Token.TYPE_SYMBOL || token.mType == Token.TYPE_CONTROL) {
			token.mAttributes = (byte) mBits.getRange(Token.BIT_ATTRIBUTES_START, Token.BIT_ATTRIBUTES_END);
		}
		token.mRtl = mBits.get(Token.DIRECTION_RTL);

		return token;
	}

	private static int bit2Value(int bits, int offset) {
		if (bits == 0) {
			return offset;
		}

		return Token.numberOfTrailingZeros(bits) + offset;
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
			'〉', 
			'、', 
			'。', 
			'〉', 
			'》', 
			'」', 
			'』', 
			'】', 
			'〕', 
			'〗', 
			'〙', 
			'〛', 
			'〞', 
			'〟', 
			'﹂', 
			'﹄', 
			'﹚', 
			'﹜', 
			'﹞', 
			'！', 
			'）', 
			'，', 
			'．', 
			'：', 
			'；', 
			'？', 
			'］', 
			'｠', 
	};
	@VisibleForTesting
	public static final char[] SQUISH_LEFT_MAP = {
			'〈', 
			'〈', 
			'《', 
			'「', 
			'『', 
			'【', 
			'〔', 
			'〖', 
			'〘', 
			'〚', 
			'〝', 
			'︐', 
			'︑', 
			'︒', 
			'︓', 
			'︔', 
			'︕', 
			'︖', 
			'﹁', 
			'﹃', 
			'﹙', 
			'﹛', 
			'﹝', 
			'（', 
			'［', 
			'｟', 
	};

	@VisibleForTesting
	public static final char[] STRETCH_RIGHT_MAP = {
			'!', 
			')', 
			',', 
			'.', 
			':', 
			';', 
			'>', 
			'?', 
			']', 
			'}', 
			'»', 
			';', 
			'༻', 
			'༽', 
			'᚜', 
			'’', 
			'”', 
			'›', 
			'⁆', 
			'⁾', 
			'₎', 
			'⌉', 
			'⌋', 
			'〉', 
			'❩', 
			'❫', 
			'❭', 
			'❯', 
			'❱', 
			'❳', 
			'❵', 
			'⟆', 
			'⟧', 
			'⟩', 
			'⟫', 
			'⟭', 
			'⟯', 
			'⦄', 
			'⦆', 
			'⦈', 
			'⦊', 
			'⦌', 
			'⦎', 
			'⦐', 
			'⦒', 
			'⦔', 
			'⦖', 
			'⦘', 
			'⧙', 
			'⧛', 
			'⧽', 
			'⸃', 
			'⸅', 
			'⸊', 
			'⸍', 
			'⸝', 
			'⸡', 
			'⸣', 
			'⸥', 
			'⸧', 
			'⸩', 
			'﴾', 
			'︘', 
			'︶', 
			'︸', 
			'︺', 
			'︼', 
			'︾', 
			'﹀', 
			'﹂', 
			'﹄', 
			'﹈', 
			'｝', 
			'｡', 
			'｣', 
			'､', 
	};

	@VisibleForTesting
	public static final char[] STRETCH_LEFT_MAP = {
			'(', 
			'<', 
			'[', 
			'{', 
			'«', 
			'¿', 
			'༺', 
			'༼', 
			'᚛', 
			'‘', 
			'‚', 
			'‛', 
			'“', 
			'„', 
			'‟', 
			'‹', 
			'⁅', 
			'⁽', 
			'₍', 
			'⌈', 
			'⌊', 
			'〈', 
			'❨', 
			'❪', 
			'❬', 
			'❮', 
			'❰', 
			'❲', 
			'❴', 
			'⟅', 
			'⟦', 
			'⟨', 
			'⟪', 
			'⟬', 
			'⟮', 
			'⦃', 
			'⦅', 
			'⦇', 
			'⦉', 
			'⦋', 
			'⦍', 
			'⦏', 
			'⦑', 
			'⦓', 
			'⦕', 
			'⦗', 
			'⧘', 
			'⧚', 
			'⧼', 
			'⸂', 
			'⸄', 
			'⸉', 
			'⸌', 
			'⸜', 
			'⸠', 
			'⸢', 
			'⸤', 
			'⸦', 
			'⸨', 
			'︗', 
			'︵', 
			'︷', 
			'︹', 
			'︻', 
			'︽', 
			'︿', 
			'﹇', 
			'｛', 
			'｢', 
	};

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	static boolean setupStretchAdvise(BitBucket32 bits, int codePoint) {
		int index = binarySearch(STRETCH_RIGHT_MAP, codePoint);
		if (index >= 0) {
			bits.set(Token.SYMBOL_ATTRIBUTE_STRETCH_RIGHT);
			return true;
		}

		index = binarySearch(STRETCH_LEFT_MAP, codePoint);
		if (index >= 0) {
			bits.set(Token.SYMBOL_ATTRIBUTE_STRETCH_LEFT);
			return true;
		}

		return false;
	}

	@VisibleForTesting
	static boolean setupKinsokuAdvise(BitBucket32 bits, int codePoint) {
		int type = Character.getType(codePoint);
		return setupKinsokuAdvise(bits, codePoint, type);
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	static boolean setupKinsokuAdvise(BitBucket32 bits, int codePoint, int type) {
		if (type == Character.START_PUNCTUATION ||
				type == Character.INITIAL_QUOTE_PUNCTUATION ||
				type == Character.CURRENCY_SYMBOL) {
			bits.set(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_TAIL);
			return true;
		}

		if (type == Character.FINAL_QUOTE_PUNCTUATION ||
				type == Character.END_PUNCTUATION ||
				type == Character.DASH_PUNCTUATION) {
			bits.set(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER);
			return true;
		}

		if (type == Character.OTHER_PUNCTUATION) {
			if (codePoint == '&' || codePoint == '@' || codePoint == '·' || codePoint == '/') {
				bits.set(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER);
				bits.set(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_TAIL);
				return true;
			}





			bits.set(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER);
			return true;
		}

		if (type == Character.MATH_SYMBOL || type == Character.MODIFIER_SYMBOL || type == Character.OTHER_SYMBOL) {
			bits.set(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER);
			bits.set(Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_TAIL);
			return true;
		}

		return false;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	static boolean setupSquishAdvise(BitBucket32 bits, int codePoint) {
		int index = binarySearch(SQUISH_RIGHT_MAP, codePoint);
		if (index >= 0) {
			bits.set(Token.SYMBOL_ATTRIBUTE_SQUISH_RIGHT);
			return true;
		}

		index = binarySearch(SQUISH_LEFT_MAP, codePoint);
		if (index >= 0) {
			bits.set(Token.SYMBOL_ATTRIBUTE_SQUISH_LEFT);
			return true;
		}

		return false;
	}

	private static int binarySearch(char[] array, int value) {
		if (value < array[0] || value > array[array.length - 1]) {
			return -1;
		}

		int lo = 0;
		int hi = array.length - 1;


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
				return mid;  
			}
		}

		
		return -1;
	}

	public static TextTokenStream obtain(CharSequence text, int start, int end) {
		return obtain(text, start, end, false);
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
