package me.chan.texas.text.tokenizer;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import me.chan.texas.misc.DefaultRecyclable;

/**
 * 词法引擎产生的最小语义单元。
 * <p>
 * 子类约定：
 * <ul>
 *     <li>{@link TextToken} 承载普通文本相关的所有信息（字符串、起止位置、word/symbol/control 类型、bidi 等）。</li>
 *     <li>{@link HyperSpanToken} 承载一个 {@link me.chan.texas.text.HyperSpan}，对外报告 {@link #TYPE_SYMBOL}，
 *     这样可以与 {@link TextToken} 共用同一套排版规则。</li>
 * </ul>
 * <p>
 * 这里集中维护 token 的"语义"常量（type / category / 各类 attribute 位），它们是面向上层
 * 暴露的 API；具体的字段编码（{@code BIT_*} / {@code DIRECTION_RTL}）属于 {@link TextToken}
 * 的实现细节，留在子类里。
 */
public abstract class Token extends DefaultRecyclable {

	// type
	public static final byte TYPE_NONE = 0; /* 什么也不是 */
	public static final byte TYPE_SYMBOL = 1; /* 符号+标点符号，超文字也归为这一类 */
	public static final byte TYPE_CONTROL = 2; /* 空格、制表符等 */
	public static final byte TYPE_WORD = 3; /* 单词 */

	@IntDef({TYPE_NONE,
			TYPE_SYMBOL,
			TYPE_CONTROL,
			TYPE_WORD})
	public @interface TokenType {
	}

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

	// control attributes
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int CONTROL_ATTRIBUTE_SPACE = 16;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int CONTROL_ATTRIBUTE_TAB_HORIZONTAL = 17;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int CONTROL_ATTRIBUTE_NEW_LINE = 18;

	@TokenType
	public abstract byte getType();

	/**
	 * 默认所有 token 都不带任何属性。子类按需重写。
	 * <p>
	 * 参数同时承载 symbol/control 两类位（它们在 bit 编码上是错开的）。
	 */
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public boolean checkAttribute(int attribute) {
		return false;
	}

	/**
	 * 默认所有 token 都不带 squish/stretch 字形属性。子类按需重写。
	 */
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public boolean hasSymbolTypefaceAttributes() {
		return false;
	}

	/**
	 * 默认所有 token 都是 LTR。子类按需重写。
	 */
	public boolean isRtl() {
		return false;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public String getSemantics() {
		return "未知";
	}

	@NonNull
	@Override
	public String toString() {
		if (isRecycled()) {
			return "<recycled>";
		}
		return getSemantics();
	}
}