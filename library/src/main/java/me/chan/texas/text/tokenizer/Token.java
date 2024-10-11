package me.chan.texas.text.tokenizer;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import java.util.Objects;

import me.chan.texas.misc.BitBucket32;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class Token extends DefaultRecyclable {
	private static final ObjectPool<Token> POOL = new ObjectPool<>(128);

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public byte getWordCategory() {
		if (mMask.get(WORD_CATEGORY_NORMAL)) {
			return WORD_CATEGORY_NORMAL;
		}

		if (mMask.get(WORD_CATEGORY_CJK)) {
			return WORD_CATEGORY_CJK;
		}

		if (mMask.get(WORD_CATEGORY_NUMBER)) {
			return WORD_CATEGORY_NUMBER;
		}

		return WORD_CATEGORY_UNKNOWN_LETTER;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public boolean checkSymbolAttribute(int attribute) {
		return mMask.get(Token.TYPE_SYMBOL) && mMask.get(attribute);
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public boolean isRtl() {
		return mMask.get(BIT_DIRECTION);
	}

	public boolean hasSymbolTypefaceAttributes() {
		if (getType() != Token.TYPE_SYMBOL) {
			return false;
		}

		int attributes = mMask.getRange(BIT_SYMBOL_TYPEFACE_START, BIT_SYMBOL_TYPEFACE_END);
		return attributes != 0;
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
	public static final byte TYPE_NONE = 0; /* 什么也不是 */

	// 8...16 category
	// 16...31 attributes

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
	public static final int BIT_DIRECTION = 31;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int BIT_SYMBOL_TYPEFACE_START = 18;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int BIT_SYMBOL_TYPEFACE_END = 21;

	public static final byte TYPE_SYMBOL = 1; /* 符号+标点符号 */
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final byte SYMBOL_CATEGORY_SYMBOL = 8; /* 符号 */
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final byte SYMBOL_CATEGORY_PUNCTUATION = 9; /* 标点符号 */
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


	public static final byte TYPE_CONTROL = 2; /* 空格、制表符等 */

	public static final byte TYPE_WORD = 3; /* 单词 */
	public static final byte WORD_CATEGORY_UNKNOWN_LETTER = 8; /* 未知字符 */
	public static final byte WORD_CATEGORY_NORMAL = 9; /* 正常的单词 [a-z]... */
	public static final byte WORD_CATEGORY_NUMBER = 10; /* 数字 */
	public static final byte WORD_CATEGORY_CJK = 11; /* CJK */

	public static final byte DIRECTION_RTL = 31;

	@IntDef({TYPE_NONE,
			TYPE_SYMBOL,
			TYPE_CONTROL,
			TYPE_WORD,})
	public @interface TokenType {

	}

	CharSequence mCharSequence;
	int mStart;
	int mEnd;
	final BitBucket32 mMask = new BitBucket32();

	private Token() {

	}

	@TokenType
	public byte getType() {
		if (mMask.get(TYPE_WORD)) {
			return TYPE_WORD;
		}

		if (mMask.get(TYPE_CONTROL)) {
			return TYPE_CONTROL;
		}

		return mMask.get(TYPE_SYMBOL) ? TYPE_SYMBOL : TYPE_NONE;
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
			byte category = getWordCategory();
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
}