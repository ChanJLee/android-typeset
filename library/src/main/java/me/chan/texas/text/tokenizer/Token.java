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