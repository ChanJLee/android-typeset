package me.chan.texas.text.tokenizer;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import me.chan.texas.misc.BitBucket;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class Token extends DefaultRecyclable {
	private static final ObjectPool<Token> POOL = new ObjectPool<>(128);

	// 避头尾
	public static final int SYMBOL_KINSOKU_AVOID_HEADER = 1;
	public static final int SYMBOL_KINSOKU_AVOID_TAIL = 2;
	public static final int SYMBOL_KINSOKU_MASK = SYMBOL_KINSOKU_AVOID_HEADER | SYMBOL_KINSOKU_AVOID_TAIL;

	// 挤压
	public static final int SYMBOL_SQUISH_LEFT = 4;
	public static final int SYMBOL_SQUISH_RIGHT = 8;

	public static final int SYMBOL_SQUISH_MASK = SYMBOL_SQUISH_LEFT | SYMBOL_SQUISH_RIGHT;

	// 拉伸
	public static final int SYMBOL_STRETCH_LEFT = 16;
	public static final int SYMBOL_STRETCH_RIGHT = 32;

	public static final int SYMBOL_STRETCH_MASK = SYMBOL_STRETCH_LEFT | SYMBOL_STRETCH_RIGHT;

	public static final int SYMBOL_TYPEFACE_MASK = SYMBOL_SQUISH_MASK | SYMBOL_STRETCH_MASK;

	// TODO 这个地方有些许混乱了，需要重新整理
	public static final int WORD_TYPE_CN = 64;
	public static final int WORD_TYPE_LATIN = 128;
	public static final int WORD_TYPE_CONTEXT_SENSITIVE = 256;
	public static final int WORD_TYPE_MASK = WORD_TYPE_LATIN | WORD_TYPE_CN | WORD_TYPE_CONTEXT_SENSITIVE;

	public int getAttributes() {
		return mAttributes;
	}

	@IntDef({SYMBOL_KINSOKU_MASK,
			SYMBOL_SQUISH_MASK,
			SYMBOL_STRETCH_MASK,
			SYMBOL_TYPEFACE_MASK,
			WORD_TYPE_MASK})
	public @interface TokenMask {

	}

	@IntDef({SYMBOL_KINSOKU_AVOID_HEADER,
			SYMBOL_KINSOKU_AVOID_TAIL,
			SYMBOL_SQUISH_LEFT,
			SYMBOL_SQUISH_RIGHT,
			SYMBOL_STRETCH_LEFT,
			SYMBOL_STRETCH_RIGHT,
			WORD_TYPE_LATIN,
			WORD_TYPE_CN,
			WORD_TYPE_CONTEXT_SENSITIVE})
	public @interface TokenAttribute {

	}

	public static final int TYPE_NONE = 0;
	public static final int TYPE_SYMBOL = 1;
	public static final int TYPE_BLANK = 2;
	public static final int TYPE_WORD = 3;
	public static final int TYPE_PUNCTUATION = 4;

	public static final int SYMBOL_CATEGORY_MATH = Character.MATH_SYMBOL;
	public static final int SYMBOL_CATEGORY_CURRENCY = Character.CURRENCY_SYMBOL;
	public static final int SYMBOL_CATEGORY_MODIFIER = Character.MODIFIER_SYMBOL;
	public static final int SYMBOL_CATEGORY_OTHER = Character.OTHER_SYMBOL;

	public static final int PUNCTUATION_CATEGORY_DASH = Character.DASH_PUNCTUATION;
	public static final int PUNCTUATION_CATEGORY_END = Character.END_PUNCTUATION;
	public static final int PUNCTUATION_CATEGORY_FINAL_QUOTE = Character.FINAL_QUOTE_PUNCTUATION;
	public static final int PUNCTUATION_CATEGORY_INITIAL_QUOTE = Character.INITIAL_QUOTE_PUNCTUATION;
	public static final int PUNCTUATION_CATEGORY_OTHER = Character.OTHER_PUNCTUATION;
	public static final int PUNCTUATION_CATEGORY_START = Character.START_PUNCTUATION;

	public static final int WORD_CATEGORY_NUMBER = 1;
	public static final int WORD_CATEGORY_ASCII = 2;
	public static final int WORD_CATEGORY_CJK = 3;
	public static final int WORD_CATEGORY_OTHER = 4;


	@IntDef({TYPE_NONE,
			TYPE_SYMBOL,
			TYPE_BLANK,
			TYPE_WORD,
			TYPE_PUNCTUATION})
	public @interface TokenType {

	}

	@TokenType
	int mType = TYPE_NONE;
	CharSequence mCharSequence;
	int mStart;
	int mEnd;

	int mAttributes = 0;
	int mReason;

	int mCategory;

	// 添加删除要顺带修改 copy 函数

	private Token() {

	}

	private String getSymbolSemantics() {
		String kinsoku = "   ";
		if (checkMask(SYMBOL_KINSOKU_MASK)) {
			kinsoku = checkAttribute(SYMBOL_KINSOKU_MASK, SYMBOL_KINSOKU_AVOID_HEADER) ? "避头" : "避尾";
		}

		String typeface = "     ";
		if (checkMask(SYMBOL_TYPEFACE_MASK)) {
			if (checkMask(SYMBOL_SQUISH_MASK)) {
				typeface = checkAttribute(SYMBOL_SQUISH_MASK, SYMBOL_SQUISH_LEFT) ? "挤压左" : "挤压右";
			} else if (checkMask(SYMBOL_STRETCH_MASK)) {
				typeface = checkAttribute(SYMBOL_STRETCH_MASK, SYMBOL_STRETCH_LEFT) ? "拉伸左" : "拉伸右";
			} else {
				typeface = "fuck typeface";
			}
		}

		return String.format("符号,%-2s,%-3s", kinsoku, typeface);
	}

	@TokenType
	public int getType() {
		return mType;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public String getSemantics() {
		if (mType == TYPE_NONE) {
			return "none";
		}

		if (mType == TYPE_BLANK) {
			return "空格";
		}

		if (mType == TYPE_SYMBOL) {
			return getSymbolSemantics();
		}

		if (mType == TYPE_WORD) {
			return mAttributes == WORD_TYPE_LATIN ? "英文" : "中文";
		}

		return "未知";
	}

	public boolean checkMask(@TokenMask int mask) {
		return (mAttributes & mask) != 0;
	}

	public boolean checkAttribute(@TokenMask int mask, @TokenAttribute int flag) {
		return (mAttributes & mask & flag) == flag;
	}

	public CharSequence getCharSequence() {
		return mCharSequence;
	}

	public int getStart() {
		return mStart;
	}

	public int getEnd() {
		return mEnd;
	}

	public boolean equals(String s) {
		if (mCharSequence == null || s == null) {
			return true;
		}

		if (s.length() != mEnd - mStart) {
			return false;
		}

		for (int i = 0; i < s.length(); ++i) {
			if (s.charAt(i) != mCharSequence.charAt(mStart + i)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void recycle() {
		if (isRecycled()) {
			return;
		}

		super.recycle();
		mCharSequence = null;
		mStart = mEnd = 0;
		mAttributes = 0;
		mType = TYPE_NONE;
		POOL.release(this);
	}

	@Override
	public String toString() {
		if (isRecycled()) {
			return "<recycled>";
		}

		if (mCharSequence == null || mCharSequence.length() == 0) {
			return "";
		}

		return String.format("%s <%s>", getSemantics(), mCharSequence.subSequence(mStart, mEnd));
	}

	public static Token obtain() {
		Token token = POOL.acquire();
		if (token == null) {
			return new Token();
		}

		token.reuse();
		return token;
	}

	public static Token obtainBlank() {
		Token token = POOL.acquire();
		if (token == null) {
			token = new Token();
		}

		token.reuse();
		token.mType = TYPE_BLANK;
		return token;
	}

	public static Token obtainNone() {
		Token token = POOL.acquire();
		if (token == null) {
			token = new Token();
		}

		token.reuse();
		token.mType = TYPE_UNKNOWN;
		return token;
	}

	public static Token copy(Token other) {
		Token copy = obtain();
		copy.mType = other.mType;
		copy.mAttributes = other.mAttributes;
		copy.mCharSequence = other.mCharSequence;
		copy.mStart = other.mStart;
		copy.mEnd = other.mEnd;
		return copy;
	}
}