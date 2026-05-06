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
 *     <li>{@link HyperSpanToken} 承载一个 {@link me.chan.texas.text.HyperSpan}，让超文字也能进入排版规则。</li>
 * </ul>
 */
public abstract class Token extends DefaultRecyclable {

	// type
	public static final byte TYPE_NONE = 0; /* 什么也不是 */
	public static final byte TYPE_SYMBOL = 1; /* 符号+标点符号 */
	public static final byte TYPE_CONTROL = 2; /* 空格、制表符等 */
	public static final byte TYPE_WORD = 3; /* 单词 */
	public static final byte TYPE_HYPER_SPAN = 4; /* 超文字 */

	@IntDef({TYPE_NONE,
			TYPE_SYMBOL,
			TYPE_CONTROL,
			TYPE_WORD,
			TYPE_HYPER_SPAN})
	public @interface TokenType {
	}

	@TokenType
	public abstract byte getType();

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