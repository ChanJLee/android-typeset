package me.chan.texas.text;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.chan.texas.measurer.Measurer;
import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.text.layout.Span;
import me.chan.texas.text.layout.StateList;
import me.chan.texas.text.tokenizer.Token;

/**
 * 超文字
 * <p>
 * 复用 {@link Token.SymbolTokenAttribute} 中受支持的子集，让超文字也可以
 * 像 symbol token 那样描述前后的断行 / 间距诉求。
 * <p>
 * 仅支持以下四个属性：
 * <ul>
 *     <li>{@link #AVOID_LINE_HEADER}</li>
 *     <li>{@link #AVOID_LINE_TAIL}</li>
 *     <li>{@link #STRETCH_LEFT}</li>
 *     <li>{@link #STRETCH_RIGHT}</li>
 * </ul>
 * <pre><code>
 * class MyHyperSpan extends HyperSpan {
 *
 *    &#64;Override
 *    protected void onDraw(TexasCanvas canvas, TexasPaint paint, RectF inner, RectF outer, float baselineOffset, StateList states) {
 * 		canvas.drawText("hello", inner.bottom, inner.bottom - baselineOffset, paint);
 *    }
 *
 *    &#64;Override
 *    protected void onMeasure(float lineHeight, float baselineOffset) {
 * 		setMeasuredSize(10, 20);
 *    }
 * }
 * </code></pre>
 */
public abstract class HyperSpan extends Span implements Measurable {
	/**
	 * 避免出现在行首
	 */
	public static final int AVOID_LINE_HEADER = Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_LINE_HEADER;
	/**
	 * 避免出现在行尾
	 */
	public static final int AVOID_LINE_TAIL = Token.SYMBOL_ATTRIBUTE_KINSOKU_AVOID_LINE_TAIL;
	/**
	 * 左边留出空白
	 */
	public static final int STRETCH_LEFT = Token.SYMBOL_ATTRIBUTE_STRETCH_LEFT;
	/**
	 * 右边留出空白
	 */
	public static final int STRETCH_RIGHT = Token.SYMBOL_ATTRIBUTE_STRETCH_RIGHT;

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({HyperSpan.AVOID_LINE_HEADER,
			HyperSpan.AVOID_LINE_TAIL,
			HyperSpan.STRETCH_LEFT,
			HyperSpan.STRETCH_RIGHT})
	public @interface HyperSpanSymbolAttribute {
	}

	private int mSymbolAttributes;

	public HyperSpan() {
		super(0, 0);
	}

	public HyperSpan(float width, float height) {
		super(width, height);
	}

	/**
	 * 设置一个排版属性，仅接受 {@link HyperSpanSymbolAttribute} 列出的常量。
	 */
	public final void addAttribute(@HyperSpanSymbolAttribute int attribute) {
		mSymbolAttributes |= 1 << attribute;
	}

	/**
	 * 移除一个排版属性。
	 */
	public final void removeAttribute(@HyperSpanSymbolAttribute int attribute) {
		mSymbolAttributes &= ~(1 << attribute);
	}

	/**
	 * @return 是否带有指定的属性
	 */
	public final boolean checkAttribute(@HyperSpanSymbolAttribute int attribute) {
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
	 * HyperSpan 不支持 squish，所以只检测 stretch。
	 */
	@RestrictTo(LIBRARY)
	public final boolean hasSymbolTypefaceAttributes() {
		final int typefaceMask = (1 << STRETCH_LEFT) | (1 << STRETCH_RIGHT);
		return (mSymbolAttributes & typefaceMask) != 0;
	}

	@RestrictTo(LIBRARY)
	@Override
	public final void draw(TexasCanvas canvas, TexasPaint paint, RectF inner, RectF outer, float baselineOffset, StateList states) {
		onDraw(canvas, paint, inner, outer, baselineOffset, states);
	}

	/**
	 * @param canvas         canvas
	 * @param paint          paint
	 * @param inner          包裹文字的壳子
	 * @param outer          包括文字间隔的壳子
	 * @param baselineOffset 文字绘制基准线
	 * @param states         states
	 */
	protected abstract void onDraw(TexasCanvas canvas, TexasPaint paint, RectF inner, RectF outer, float baselineOffset, StateList states);

	@Override
	protected final void onMeasure(Measurer measurer, TextAttribute textAttribute) {
		onMeasure(textAttribute.getLineHeight(), textAttribute.getBaselineOffset());
	}

	/**
	 * 开始测量的时候调用，测量完成后调用{@link #setMeasuredSize(float, float)}
	 * <p>
	 *
	 * @param lineHeight 默认的行高
	 */
	protected abstract void onMeasure(float lineHeight, float baselineOffset);

	/**
	 * 设置测量后的大小
	 *
	 * @param width  宽度
	 * @param height 高度
	 */
	public final void setMeasuredSize(float width, float height) {
		mWidth = width;
		mHeight = height;
	}

	@Override
	public float getBaselineOffset() {
		return 0f;
	}

	@Override
	public final boolean isIsolate(boolean backward) {
		return true;
	}

	@Override
	protected void onRecycle() {
		super.onRecycle();
		mSymbolAttributes = 0;
	}
}