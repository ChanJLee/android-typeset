package me.chan.texas.text.tokenizer;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import java.util.Objects;

import me.chan.texas.misc.BitBucket32;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;

public class Token extends DefaultRecyclable {
	private static final ObjectPool<Token> POOL = new ObjectPool<>(128);

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public boolean checkSymbolAttribute(int attribute) {
		return mMask.get(Token.TYPE_SYMBOL) && mMask.get(attribute);
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public boolean hasSymbolTypefaceAttributes() {
		if (getType() != Token.TYPE_SYMBOL) {
			return false;
		}

		int attributes = mMask.getRange(BIT_SYMBOL_TYPEFACE_START, BIT_SYMBOL_TYPEFACE_END);
		return attributes != 0;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public int getCategoryBits() {
		return mMask.getRange(BIT_CATEGORY_START, BIT_CATEGORY_END);
	}

	@IntDef({SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER,
			SYMBOL_ATTRIBUTE_KINSOKU_AVOID_TAIL,
			SYMBOL_ATTRIBUTE_SQUISH_LEFT,
			SYMBOL_ATTRIBUTE_SQUISH_RIGHT,
			SYMBOL_ATTRIBUTE_STRETCH_LEFT,
			SYMBOL_ATTRIBUTE_STRETCH_RIGHT})
	public @interface SymbolTokenAttribute {

	}

	// mask bit field
	// 0...8 type
	// 8...31 mask
	// - 8...16 category
	// - 16...31 attributes
	// 31...32 direction

	// bit field
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int BIT_TYPE_START = 0;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int BIT_TYPE_END = 8;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int BIT_CATEGORY_START = 8;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int BIT_CATEGORY_END = 16;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int BIT_ATTRIBUTES_START = 16;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int BIT_ATTRIBUTES_END = 31;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int BIT_SYMBOL_TYPEFACE_START = 18;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int BIT_SYMBOL_TYPEFACE_END = 21;

	// type
	public static final byte TYPE_NONE = 0; /* 什么也不是 */
	public static final byte TYPE_SYMBOL = 1; /* 符号+标点符号 */
	public static final byte TYPE_CONTROL = 2; /* 空格、制表符等 */
	public static final byte TYPE_WORD = 3; /* 单词 */

	// category
	public static final byte SYMBOL_CATEGORY_SYMBOL = 8; /* 符号 emoji */
	public static final byte SYMBOL_CATEGORY_PUNCTUATION = 9; /* 标点符号 */
	public static final byte WORD_CATEGORY_UNKNOWN_LETTER = 10; /* 未知字符 */
	public static final byte WORD_CATEGORY_NORMAL = 11; /* 正常的单词 [a-z]... */
	public static final byte WORD_CATEGORY_NUMBER = 12; /* 数字 */
	public static final byte WORD_CATEGORY_CJK = 13; /* CJK */
	public static final byte CATEGORY_UNKNOWN = 14; /* 未知分类 */

	// symbol attributes
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER = 16;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int SYMBOL_ATTRIBUTE_KINSOKU_AVOID_TAIL = 17;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int SYMBOL_ATTRIBUTE_SQUISH_LEFT = 18;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int SYMBOL_ATTRIBUTE_SQUISH_RIGHT = 19;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int SYMBOL_ATTRIBUTE_STRETCH_LEFT = 20;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int SYMBOL_ATTRIBUTE_STRETCH_RIGHT = 21;

	// direction
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final byte DIRECTION_RTL = 31;

	@IntDef({TYPE_NONE,
			TYPE_SYMBOL,
			TYPE_CONTROL,
			TYPE_WORD,})
	public @interface TokenType {

	}

	@IntDef({CATEGORY_UNKNOWN,
			SYMBOL_CATEGORY_SYMBOL,
			SYMBOL_CATEGORY_PUNCTUATION,
			WORD_CATEGORY_UNKNOWN_LETTER,
			WORD_CATEGORY_NORMAL,
			WORD_CATEGORY_NUMBER,
			WORD_CATEGORY_CJK})
	public @interface CategoryType {

	}

	CharSequence mCharSequence;
	int mStart;
	int mEnd;
	final BitBucket32 mMask = new BitBucket32();

	private Token() {

	}

	public boolean isRtl() {
		return mMask.get(DIRECTION_RTL);
	}

	@TokenType
	public byte getType() {
		int v = mMask.getRange(BIT_TYPE_START, BIT_TYPE_END);
		if (v == 0) {
			return TYPE_NONE;
		}
		return (byte) numberOfTrailingZeros(v << BIT_TYPE_START);
	}

	@CategoryType
	public byte getCategory() {
		int v = mMask.getRange(BIT_CATEGORY_START, BIT_CATEGORY_END);
		if (v == 0) {
			return CATEGORY_UNKNOWN;
		}
		return (byte) numberOfTrailingZeros(v << BIT_CATEGORY_START);
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

	public int size() {
		return mEnd - mStart;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public String getSemantics() {
		int type = getType();
		if (type == TYPE_NONE) {
			return "none";
		}

		if (type == TYPE_CONTROL) {
			return "空格";
		}

		if (type == TYPE_SYMBOL) {
			return getSymbolSemantics();
		}

		if (type == TYPE_WORD) {
			int category = getCategory();
			if (category == WORD_CATEGORY_NORMAL) {
				return "英文";
			}

			if (category == WORD_CATEGORY_CJK) {
				return "CJK";
			}

			if (category == WORD_CATEGORY_NUMBER) {
				return "数字";
			}

			if (category == WORD_CATEGORY_UNKNOWN_LETTER) {
				return "其它";
			}

			throw new IllegalStateException("unknown word category");
		}

		return "未知";
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public boolean check(int index) {
		return mMask.get(index);
	}

	private String getSymbolSemantics() {
		String kinsoku = "   ";
		if (mMask.get(SYMBOL_ATTRIBUTE_KINSOKU_AVOID_TAIL) || mMask.get(SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER)) {
			kinsoku = mMask.get(SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER) ? "避头" : "避尾";
		}

		String typeface = "";
		if (mMask.get(SYMBOL_ATTRIBUTE_SQUISH_LEFT) || mMask.get(SYMBOL_ATTRIBUTE_SQUISH_RIGHT)) {
			typeface = mMask.get(SYMBOL_ATTRIBUTE_SQUISH_LEFT) ? "挤压左" : "挤压右";
		}

		if (mMask.get(SYMBOL_ATTRIBUTE_STRETCH_LEFT) || mMask.get(SYMBOL_ATTRIBUTE_STRETCH_RIGHT)) {
			if (!typeface.isEmpty()) {
				typeface += "&";
			}
			typeface += mMask.get(SYMBOL_ATTRIBUTE_STRETCH_LEFT) ? "拉伸左" : "拉伸右";
		}

		return String.format("符号,%-2s,%-3s", kinsoku, typeface);
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
	protected void onRecycle() {
		mCharSequence = null;
		mStart = mEnd = 0;
		mMask.clear();
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

		return String.format("%s[%s <%s>]", isRtl() ? "<<" : ">>", getSemantics(), mCharSequence.subSequence(mStart, mEnd));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Token token = (Token) o;
		if (!(mMask.equals(token.mMask))) {
			return false;
		}

		if (mCharSequence != null && token.mCharSequence != null) {
			if ((mEnd - mStart) != (token.mEnd - token.mStart)) {
				return false;
			}

			for (int i = 0; i < mCharSequence.length(); ++i) {
				if (mCharSequence.charAt(mStart + i) != token.mCharSequence.charAt(token.mStart + i)) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		int result = mMask.getBits();
		result = 31 * result + Objects.hashCode(mCharSequence);
		result = 31 * result + mStart;
		result = 31 * result + mEnd;
		return result;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static Token obtain() {
		Token token = POOL.acquire();
		if (token == null) {
			return new Token();
		}

		token.reuse();
		return token;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static Token obtainOtherWord() {
		Token token = POOL.acquire();
		if (token == null) {
			token = new Token();
		}

		token.reuse();
		token.mMask.reset(TYPE_WORD);
		return token;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static Token copy(Token other) {
		Token copy = obtain();
		copy.mMask.reset(other.mMask.getBits());
		copy.mCharSequence = other.mCharSequence;
		copy.mStart = other.mStart;
		copy.mEnd = other.mEnd;
		return copy;
	}

	@VisibleForTesting
	static int numberOfTrailingZeros(int i) {
		// HD, Count trailing 0's
		i = ~i & (i - 1);
		if (i <= 0) return i & 32;
		int n = 1;
		if (i > 1 << 16) {
			n += 16;
			i >>>= 16;
		}
		if (i > 1 << 8) {
			n += 8;
			i >>>= 8;
		}
		if (i > 1 << 4) {
			n += 4;
			i >>>= 4;
		}
		if (i > 1 << 2) {
			n += 2;
			i >>>= 2;
		}
		return n + (i >>> 1);
	}
}