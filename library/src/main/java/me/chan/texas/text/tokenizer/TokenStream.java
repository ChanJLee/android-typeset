package me.chan.texas.text.tokenizer;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.text.icu.UnicodeUtils;

import java.util.Arrays;

// sent ::= unit*
// unit ::= <symbol> | <word> | <blank> | <未知字符>
// word ::= cn | latin
// cn ::= <中文字>+
// latin ::= <0-9 拉丁字母 拉丁链接符号>*
// symbol ::= single symbol | multi symbol
// single symbol ::= UnicodeUtils#isSymbolsAndPunctuation
// blank ::= UnicodeUtils#isBreakTokenSymbol
// multi symbol ::= ...

/**
 * 大致划分出英文半角 中文 全角 空格 未知字符
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class TokenStream extends DefaultRecyclable {
	private static final ObjectPool<TokenStream> POOL = new ObjectPool<>(4);

	private final WordStream mStream = new WordStream();

	private TokenStream() {
	}


	public void reset() {
		mStream.reset();
	}

	public int save() {
		return mStream.save();
	}

	public void restore(int state) {
		mStream.restore(state);
	}

	@Override
	public void recycle() {
		if (isRecycled()) {
			return;
		}

		super.recycle();
		POOL.release(this);
	}

	@Nullable
	public Token tryGet(int state, int offset) {
		return mStream.tryGet(state, offset);
	}

	@Nullable
	public Token tryGet(int step) {
		return tryGet(mStream.save(), step);
	}

	public Token next() {
		if (!hasNext()) {
			throw new IllegalStateException("empty reader stream");
		}

		return aop(mStream.next());
	}

	public boolean hasNext() {
		return mStream.hasNext();
	}

	private static Token aop(Token token) {
		if (token.mReason >= WordStream.WORD_LETTER && token.mReason < WordStream.WORD_LETTER_LIMIT) {
			return word(token);
		}

		if (token.mReason >= WordStream.WORD_IDEO && token.mReason < WordStream.WORD_IDEO_LIMIT) {
			return word(token);
		}

		if (token.mReason >= WordStream.WORD_NONE && token.mReason < WordStream.WORD_NONE_LIMIT) {
			return unit(token);
		}

		if (token.mReason >= WordStream.WORD_NUMBER && token.mReason < WordStream.WORD_NUMBER_LIMIT) {
			token.mType = Token.TYPE_WORD;
			token.mCategory = Token.WORD_CATEGORY_NUMBER;
			return token;
		}

		if (token.mReason >= WordStream.WORD_KANA && token.mReason < WordStream.WORD_KANA_LIMIT) {
			return word(token);
		}

		throw new IllegalStateException("unknown token type");
	}

	private static Token word(Token token) {
		// fast check
		token.mType = Token.TYPE_WORD;
		int codePoint = token.mCharSequence.charAt(token.mStart);
		if (codePoint <= 0xff) {
			token.mCategory = Token.WORD_CATEGORY_ASCII;
			return token;
		}
		token.mCategory = UnicodeUtils.isCJKExtends(codePoint) ? Token.WORD_CATEGORY_CJK : Token.WORD_CATEGORY_OTHER;
		return token;
	}

	private static Token unit(Token token) {
		int codePoint = token.mCharSequence.charAt(token.mStart);
		int type = Character.getType(codePoint);
		if (type == Character.MATH_SYMBOL
				/* https://www.compart.com/en/unicode/category/Sc */
				|| type == Character.CURRENCY_SYMBOL
				/* https://www.compart.com/en/unicode/category/Sk */
				|| type == Character.MODIFIER_SYMBOL
				/* https://www.compart.com/en/unicode/category/So */
				|| type == Character.OTHER_SYMBOL) {
			token.mCategory = type;
			token.mType = Token.TYPE_SYMBOL;
			token.mAttributes = type != Character.OTHER_SYMBOL ? Token.SYMBOL_KINSOKU_AVOID_TAIL : 0;
			return token;
		}

		if (type == Character.DASH_PUNCTUATION
				|| type == Character.END_PUNCTUATION
				|| type == Character.FINAL_QUOTE_PUNCTUATION
				|| type == Character.INITIAL_QUOTE_PUNCTUATION
				|| type == Character.OTHER_PUNCTUATION
				|| type == Character.START_PUNCTUATION) {
			token.mCategory = type;
			token.mType = Token.TYPE_SYMBOL;
			token.mAttributes = getStretchAdvise(codePoint) |
					getSquishAdvise(codePoint) |
					getKinsokuAdvise(codePoint);
			return token;
		}

		// 空白？
		if (UnicodeUtils.isControlCharacter(codePoint)) {
			token.mType = Token.TYPE_BLANK;
			return token;
		}

		// 未知字符，这个可能是不可见的字符
		token.mType = Token.TYPE_WORD;
		token.mCategory = Token.WORD_CATEGORY_OTHER;
		return token;
	}

	public static TokenStream obtain(CharSequence text, int start, int end) {
		TokenStream tokenStream = POOL.acquire();
		if (tokenStream == null) {
			tokenStream = new TokenStream();
		}

		tokenStream.mStream.setText(text, start, end);
		tokenStream.reuse();
		return tokenStream;
	}

	@NonNull
	@Override
	public String toString() {
		return mStream.toString();
	}

	// 符号类相关的case
	@VisibleForTesting
	public static final int[] KINSOKU_AVOID_HEADER_MAP;

	@VisibleForTesting
	public static final int[] KINSOKU_AVOID_TAIL_MAP;

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static int getKinsokuAdvise(int codePoint) {
		// 临时加的，没有放到表里面。
		// 规避在头部能最大程度减少视觉上的干扰。
		if (codePoint == '"' || codePoint == '\'') {
			return Token.SYMBOL_KINSOKU_AVOID_HEADER;
		}

		int advise = 0;
		int index = binarySearch(KINSOKU_AVOID_HEADER_MAP, codePoint);
		if (index >= 0) {
			advise |= Token.SYMBOL_KINSOKU_AVOID_HEADER;
		}

		index = binarySearch(KINSOKU_AVOID_TAIL_MAP, codePoint);
		if (index >= 0) {
			advise |= Token.SYMBOL_KINSOKU_AVOID_TAIL;
		}

		return advise;
	}

	@VisibleForTesting
	public static final int[] SQUISH_RIGHT_MAP;

	@VisibleForTesting
	public static final int[] SQUISH_LEFT_MAP;

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

	@VisibleForTesting
	public static final int[] STRETCH_RIGHT_MAP;

	@VisibleForTesting
	public static final int[] STRETCH_LEFT_MAP;

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

	static {
		Arrays.sort(KINSOKU_AVOID_HEADER_MAP = new int[]{
				0x21 /*!*/, 0x25 /*%*/, 0x27 /*'*/, 0x29 /*)*/, 0x2c /*,*/, 0x2d /*-*/, 0x2e /*.*/, 0x3a /*:*/, 0x3b /*;*/,
				0x3f /*?*/, 0x5d /*]*/, 0x7d /*}*/, 0xa8 /*¨*/, 0xb0 /*°*/, 0xb7 /*·*/, 0x2c7 /*ˇ*/, 0x2c9 /*ˉ*/, 0x2010 /*‐*/,
				0x2013 /*–*/, 0x2014 /*—*/, 0x2015 /*―*/, 0x2016 /*‖*/, 0x2019 /*’*/, 0x201d /*”*/, 0x2022 /*•*/, 0x2025 /*‥*/,
				0x2026 /*…*/, 0x2027 /*‧*/, 0x2030 /*‰*/, 0x2032 /*′*/, 0x2033 /*″*/, 0x2103 /*℃*/, 0x2109 /*℉*/, 0x2236 /*∶*/,
				0x3001 /*、*/, 0x3e /*>*/, 0x3002 /*。*/, 0x3005 /*々*/, 0x3009 /*〉*/, 0x300b /*》*/, 0x300d /*」*/, 0x300f /*』*/,
				0x3011 /*】*/, 0x3015 /*〕*/, 0x3017 /*〗*/, 0xfe30 /*︰*/, 0xfe34 /*︴*/, 0xfe36 /*︶*/, 0xfe38 /*︸*/, 0xfe3a /*︺*/,
				0xfe3c /*︼*/, 0xfe3e /*︾*/, 0xfe40 /*﹀*/, 0xfe42 /*﹂*/, 0xfe44 /*﹄*/, 0xfe4f /*﹏*/, 0xff01 /*！*/, 0xff02 /*＂*/,
				0xff05 /*％*/, 0xff07 /*＇*/, 0xff09 /*）*/, 0xff0c /*，*/, 0xff0d /*－*/, 0xff1a /*：*/, 0xff1b /*；*/, 0xff1f /*？*/,
				0xff3d /*］*/, 0xff40 /*｀*/, 0xff5c /*｜*/, 0xff5d /*｝*/, 0xff5e /*～*/, 0xffe0 /*￠*/
		});

		Arrays.sort(KINSOKU_AVOID_TAIL_MAP = new int[]{
				0x24 /*$*/, 0x28 /*(*/, 0x5b /*[*/, 0x5c /*\*/, 0x7b /*{*/, 0xa3 /*£*/, 0xa5 /*¥*/, 0xa7 /*§*/, 0xb7 /*·*/,
				0x2018 /*‘*/, 0x201c /*“*/, 0x2035 /*‵*/, 0x20ac /*€*/, 0x3008 /*〈*/, 0x300a /*《*/, 0x300c /*「*/, 0x300e /*『*/,
				0x3010 /*【*/, 0x3012 /*〒*/, 0x3016 /*〖*/, 0x3014 /*〔*/, 0xfe35 /*︵*/, 0xfe37 /*︷*/, 0xfe39 /*︹*/, 0xfe3b /*︻*/,
				0xfe3d /*︽*/, 0xfe3f /*︿*/, 0xfe41 /*﹁*/, 0xfe43 /*﹃*/, 0xfe5f /*﹟*/, 0xfe69 /*﹩*/, 0xfe6b /*﹫*/, 0xff03 /*＃*/,
				0xff04 /*＄*/, 0xff08 /*（*/, 0xff1c /*＜*/, 0xff20 /*＠*/, 0xff3b /*［*/, 0xff5b /*｛*/, 0xff5e /*～*/, 0xffe1 /*￡*/,
				0xffe5 /*￥*/, 0x3c /*<*/
		});

		Arrays.sort(SQUISH_RIGHT_MAP = new int[]{
				'，', '。', '、', '：', '；', '？', '！', '》', '）', '』', '」'
		});

		Arrays.sort(SQUISH_LEFT_MAP = new int[]{
				'《', '（', '『', '「'
		});

		Arrays.sort(STRETCH_RIGHT_MAP = new int[]{
				',', '.', ':', ';', '?', '!', ':', '>', ')', ']', '}', 0x201d /* ” */, '’' /* 0x2019 */,
		});

		Arrays.sort(STRETCH_LEFT_MAP = new int[]{
				'<', '(', '[', '{', 0x201c /* “ */, '‘' /* 0x2018 */,
		});
	}

	private static int binarySearch(int[] array, int value) {

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
}
