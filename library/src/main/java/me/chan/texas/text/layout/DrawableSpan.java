package me.chan.texas.text.layout;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.chan.texas.text.tokenizer.Token;

/**
 * 可绘制box，可以是图片，表情
 * <p>
 * 复用 {@link Token.SymbolTokenAttribute} 中受支持的子集，让超文字也可以
 * 像 symbol token 那样描述前后的断行 / 间距诉求。
 * <p>
 * 仅支持以下四个属性：
 * <ul>
 *     <li>{@link Token#SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER}</li>
 *     <li>{@link Token#SYMBOL_ATTRIBUTE_KINSOKU_AVOID_TAIL}</li>
 *     <li>{@link Token#SYMBOL_ATTRIBUTE_STRETCH_LEFT}</li>
 *     <li>{@link Token#SYMBOL_ATTRIBUTE_STRETCH_RIGHT}</li>
 * </ul>
 */
public abstract class DrawableSpan extends Span {

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_HEADER,
			Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_TAIL,
			Token.SYMBOL_ATTRIBUTE_STRETCH_LEFT,
			Token.SYMBOL_ATTRIBUTE_STRETCH_RIGHT})
	public @interface DrawableSymbolAttribute {
	}

	private int mSymbolAttributes;

	/**
	 * @param width  宽度
	 * @param height 高度
	 */
	protected DrawableSpan(float width, float height) {
		super(width, height);
	}

	/**
	 * 设置一个排版属性，仅接受 {@link DrawableSymbolAttribute} 列出的常量。
	 */
	public final void addAttribute(@DrawableSymbolAttribute int attribute) {
		mSymbolAttributes |= 1 << attribute;
	}

	/**
	 * 移除一个排版属性。
	 */
	public final void removeAttribute(@DrawableSymbolAttribute int attribute) {
		mSymbolAttributes &= ~(1 << attribute);
	}

	/**
	 * @return 是否带有指定的属性
	 */
	public final boolean checkAttribute(@DrawableSymbolAttribute int attribute) {
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
	 * @return 是否带有 stretch 这类影响周围排版的字形属性，
	 * 与 {@link Token#hasSymbolTypefaceAttributes()} 保持一致。
	 * DrawableSpan 不支持 squish，所以只检测 stretch。
	 */
	@RestrictTo(LIBRARY)
	public final boolean hasSymbolTypefaceAttributes() {
		final int typefaceMask = (1 << Token.SYMBOL_ATTRIBUTE_STRETCH_LEFT)
				| (1 << Token.SYMBOL_ATTRIBUTE_STRETCH_RIGHT);
		return (mSymbolAttributes & typefaceMask) != 0;
	}

	@Override
	protected void onRecycle() {
		super.onRecycle();
		mSymbolAttributes = 0;
	}
}