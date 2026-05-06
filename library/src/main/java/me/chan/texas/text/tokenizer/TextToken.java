package me.chan.texas.text.tokenizer;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import java.util.Objects;

import me.chan.texas.misc.ObjectPool;

/**
 * 由文本词法解析产生的 token。
 * <p>
 * 之前所有写在 {@link Token} 上的字段、常量和方法都迁移到了这里，
 * 让超文字之类的非文本 token 可以共享 {@link Token} 这个抽象。
 */
public class TextToken extends Token {
	private static final ObjectPool<TextToken> POOL = new ObjectPool<>(128);

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

	// category
	public static final byte CATEGORY_SYMBOL = 8; /* 符号 emoji */
	public static final byte CATEGORY_PUNCTUATION = 9; /* 标点符号 */
	public static final byte CATEGORY_UNKNOWN_LETTER = 10; /* 未知字符 */
	public static final byte CATEGORY_NORMAL = 11; /* 正常的单词 [a-z]... */
	public static final byte CATEGORY_NUMBER = 12; /* 数字 */
	public static final byte CATEGORY_CJK = 13; /* CJK */

	@IntDef({CATEGORY_SYMBOL,
			CATEGORY_PUNCTUATION,
			CATEGORY_UNKNOWN_LETTER,
			CATEGORY_NORMAL,
			CATEGORY_NUMBER,
			CATEGORY_CJK})
	public @interface CategoryType {
	}

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

	@IntDef({SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER,
			SYMBOL_ATTRIBUTE_KINSOKU_AVOID_TAIL,
			SYMBOL_ATTRIBUTE_SQUISH_LEFT,
			SYMBOL_ATTRIBUTE_SQUISH_RIGHT,
			SYMBOL_ATTRIBUTE_STRETCH_LEFT,
			SYMBOL_ATTRIBUTE_STRETCH_RIGHT})
	public @interface SymbolTokenAttribute {
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int CONTROL_ATTRIBUTE_SPACE = 16;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int CONTROL_ATTRIBUTE_TAB_HORIZONTAL = 17;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int CONTROL_ATTRIBUTE_NEW_LINE = 18;

	// direction
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final byte DIRECTION_RTL = 31;

	CharSequence mCharSequence;
	int mStart;
	int mEnd;
	byte mType;
	byte mCategory;
	byte mAttributes;
	boolean mRtl;

	private TextToken() {

	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public boolean checkAttribute(int attribute) {
		return ((mAttributes << BIT_ATTRIBUTES_START) & (1 << attribute)) != 0;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public boolean hasSymbolTypefaceAttributes() {
		if (getType() != Token.TYPE_SYMBOL) {
			return false;
		}

		return mAttributes >> 2 != 0;
	}

	public boolean isRtl() {
		return mRtl;
	}

	@TokenType
	@Override
	public byte getType() {
		return mType;
	}

	@CategoryType
	public byte getCategory() {
		return mCategory;
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
	@Override
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
			if (category == CATEGORY_NORMAL) {
				return "英文";
			}

			if (category == CATEGORY_CJK) {
				return "CJK";
			}

			if (category == CATEGORY_NUMBER) {
				return "数字";
			}

			if (category == CATEGORY_UNKNOWN_LETTER) {
				return "其它";
			}

			throw new IllegalStateException("unknown word category");
		}

		return "未知";
	}

	private String getSymbolSemantics() {
		String kinsoku = "   ";
		if (checkAttribute(SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER)) {
			kinsoku = "避头";
		}

		if (checkAttribute(SYMBOL_ATTRIBUTE_KINSOKU_AVOID_TAIL)) {
			kinsoku += "避尾";
		}

		String typeface = "";
		if (checkAttribute(SYMBOL_ATTRIBUTE_SQUISH_LEFT) || checkAttribute(SYMBOL_ATTRIBUTE_SQUISH_RIGHT)) {
			typeface = checkAttribute(SYMBOL_ATTRIBUTE_SQUISH_LEFT) ? "挤压左" : "挤压右";
		}

		if (checkAttribute(SYMBOL_ATTRIBUTE_STRETCH_LEFT) || checkAttribute(SYMBOL_ATTRIBUTE_STRETCH_RIGHT)) {
			if (!typeface.isEmpty()) {
				typeface += "&";
			}
			typeface += checkAttribute(SYMBOL_ATTRIBUTE_STRETCH_LEFT) ? "拉伸左" : "拉伸右";
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
		mType = TYPE_NONE;
		mCategory = 0;
		mAttributes = 0;
		mRtl = false;
		POOL.release(this);
	}

	@NonNull
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

		TextToken token = (TextToken) o;
		if (mType != token.mType) return false;
		if (mCategory != token.mCategory) return false;
		if (mAttributes != token.mAttributes) return false;
		if (mRtl != token.mRtl) return false;

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
		return Objects.hash(mCharSequence, mStart, mEnd, mType, mCategory, mAttributes, mRtl);
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static TextToken obtain() {
		TextToken token = POOL.acquire();
		if (token == null) {
			return new TextToken();
		}

		token.reuse();
		return token;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static TextToken obtainOtherWord() {
		TextToken token = POOL.acquire();
		if (token == null) {
			token = new TextToken();
		}

		token.reuse();
		token.mType = TYPE_WORD;
		return token;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static TextToken copy(TextToken other) {
		TextToken copy = obtain();
		copy.mType = other.mType;
		copy.mCategory = other.mCategory;
		copy.mAttributes = other.mAttributes;
		copy.mRtl = other.mRtl;
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