package me.chan.texas.text.tokenizer;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import java.util.Objects;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class Token extends DefaultRecyclable {
	private static final ObjectPool<Token> POOL = new ObjectPool<>(128);

	// attributes
	// 0...7 category
	// 8...31 mask

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

	public int getAttributes() {
		return mMask >>> 8;
	}

	public int getCategory() {
		return mMask & 0xff;
	}

	@IntDef({SYMBOL_KINSOKU_MASK,
			SYMBOL_SQUISH_MASK,
			SYMBOL_STRETCH_MASK,
			SYMBOL_TYPEFACE_MASK})
	public @interface TokenMask {

	}

	@IntDef({SYMBOL_KINSOKU_AVOID_HEADER,
			SYMBOL_KINSOKU_AVOID_TAIL,
			SYMBOL_SQUISH_LEFT,
			SYMBOL_SQUISH_RIGHT,
			SYMBOL_STRETCH_LEFT,
			SYMBOL_STRETCH_RIGHT})
	public @interface TokenAttribute {

	}

	public static final int TYPE_NONE = 0; /* 什么也不是 */
	public static final int TYPE_SYMBOL = 1; /* 符号+标点符号 */
	public static final int TYPE_CONTROL = 2; /* 空格、制表符等 */
	public static final int TYPE_WORD = 3; /* 单词 */

	public static final byte CATEGORY_NONE = 0;
	public static final byte CATEGORY_SYMBOL = 1; /* 符号 */
	public static final byte CATEGORY_PUNCTUATION = 2; /* 标点符号 */
	public static final byte CATEGORY_CONTROL = 3; /* 控制类字符，空格，space。。。TODO 需要扩展 */
	static final byte CATEGORY_WORD_LIMITED = 10;
	public static final byte CATEGORY_UNKNOWN_LETTER = CATEGORY_WORD_LIMITED; /* 未知字符 */
	public static final byte CATEGORY_NORMAL = 11; /* 正常的单词 [a-z]... */
	public static final byte CATEGORY_NUMBER = 12; /* 数字 */
	public static final byte CATEGORY_CJK = 13; /* CJK */
	public static final byte CATEGORY_RTL = 14; /* 从右到左的字符 TODO 保留字段 */

	@IntDef({TYPE_NONE,
			TYPE_SYMBOL,
			TYPE_CONTROL,
			TYPE_WORD,})
	public @interface TokenType {

	}

	@TokenType
	int mType = TYPE_NONE;
	CharSequence mCharSequence;
	int mStart;
	int mEnd;
	int mMask;

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

	public int size() {
		return mEnd - mStart;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public String getSemantics() {
		if (mType == TYPE_NONE) {
			return "none";
		}

		if (mType == TYPE_CONTROL) {
			return "空格";
		}

		if (mType == TYPE_SYMBOL) {
			return getSymbolSemantics();
		}

		byte category = (byte) mMask;
		if (mType == TYPE_WORD) {
			if (category == CATEGORY_NORMAL) {
				return "英文";
			}

			if (category == CATEGORY_CJK) {
				return "CJK";
			}

			if (category == CATEGORY_NUMBER) {
				return "数字";
			}

			if (category == CATEGORY_RTL) {
				return "RTL";
			}

			if (category == CATEGORY_UNKNOWN_LETTER) {
				return "其它";
			}

			throw new IllegalStateException("unknown word category");
		}

		return "未知";
	}

	public boolean checkMask(@TokenMask int mask) {
		return ((mMask >>> 8) & mask) != 0;
	}

	public boolean checkAttribute(@TokenMask int mask, @TokenAttribute int flag) {
		return ((mMask >>> 8) & mask & flag) == flag;
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
		mMask = 0;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Token token = (Token) o;
		if (!(mType == token.mType && mMask == token.mMask)) {
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
		int result = mType;
		result = 31 * result + Objects.hashCode(mCharSequence);
		result = 31 * result + mStart;
		result = 31 * result + mEnd;
		result = 31 * result + mMask;
		return result;
	}

	public static Token obtain() {
		Token token = POOL.acquire();
		if (token == null) {
			return new Token();
		}

		token.reuse();
		return token;
	}

	public static Token obtainOtherWord() {
		Token token = POOL.acquire();
		if (token == null) {
			token = new Token();
		}

		token.reuse();
		token.mType = TYPE_WORD;
		token.mMask = Token.CATEGORY_UNKNOWN_LETTER;
		return token;
	}

	public static Token copy(Token other) {
		Token copy = obtain();
		copy.mType = other.mType;
		copy.mMask = other.mMask;
		copy.mCharSequence = other.mCharSequence;
		copy.mStart = other.mStart;
		copy.mEnd = other.mEnd;
		return copy;
	}
}