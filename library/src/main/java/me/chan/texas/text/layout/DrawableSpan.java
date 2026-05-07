package me.chan.texas.text.layout;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import androidx.annotation.RestrictTo;

import me.chan.texas.text.tokenizer.TextToken;

/**
 * 可绘制box，可以是图片，表情
 * <p>
 * 复用 {@link TextToken.SymbolTokenAttribute} 的属性集合，让超文字也可以
 * 像 symbol token 那样描述前后的断行 / 间距诉求。
 */
public abstract class DrawableSpan extends Span {

	private int mSymbolAttributes;

	/**
	 * @param width  宽度
	 * @param height 高度
	 */
	protected DrawableSpan(float width, float height) {
		super(width, height);
	}

	/**
	 * 设置一个排版属性，常量直接复用 {@link TextToken} 的 {@code SYMBOL_ATTRIBUTE_*}。
	 */
	public final void addAttribute(@TextToken.SymbolTokenAttribute int attribute) {
		mSymbolAttributes |= 1 << attribute;
	}

	/**
	 * 移除一个排版属性。
	 */
	public final void removeAttribute(@TextToken.SymbolTokenAttribute int attribute) {
		mSymbolAttributes &= ~(1 << attribute);
	}

	/**
	 * @return 是否带有指定的属性
	 */
	public final boolean checkAttribute(@TextToken.SymbolTokenAttribute int attribute) {
		return (mSymbolAttributes & (1 << attribute)) != 0;
	}

	/**
	 * @return 是否设置过任何属性
	 */
	@RestrictTo(LIBRARY)
	public final boolean hasSymbolAttributes() {
		return mSymbolAttributes != 0;
	}

	@RestrictTo(LIBRARY)
	public final int getSymbolAttributes() {
		return mSymbolAttributes;
	}

	/**
	 * @return 是否带有 squish/stretch 这类影响周围排版的字形属性，
	 * 与 {@link TextToken#hasSymbolTypefaceAttributes()} 保持一致。
	 */
	@RestrictTo(LIBRARY)
	public final boolean hasSymbolTypefaceAttributes() {
		final int typefaceMask = (1 << TextToken.SYMBOL_ATTRIBUTE_SQUISH_LEFT)
				| (1 << TextToken.SYMBOL_ATTRIBUTE_SQUISH_RIGHT)
				| (1 << TextToken.SYMBOL_ATTRIBUTE_STRETCH_LEFT)
				| (1 << TextToken.SYMBOL_ATTRIBUTE_STRETCH_RIGHT);
		return (mSymbolAttributes & typefaceMask) != 0;
	}

	@Override
	protected void onRecycle() {
		super.onRecycle();
		mSymbolAttributes = 0;
	}
}