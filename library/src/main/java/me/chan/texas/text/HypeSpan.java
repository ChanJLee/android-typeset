package me.chan.texas.text;

import androidx.annotation.RestrictTo;

import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.text.layout.DrawableBox;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.StateList;

/**
 * 超文字
 * <pre><code>
 * class MyHypeSpan extends HypeSpan {
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
public abstract class HypeSpan implements Measurable {
	private final DrawableBox mDrawableBox;

	public HypeSpan() {
		this(0, 0);
	}

	public HypeSpan(float width, float height) {
		mDrawableBox = DrawableBox.obtain(this, width, height);
	}

	/**
	 * @return 超文字的宽
	 */
	public final float getWidth() {
		return mDrawableBox.getWidth();
	}

	/**
	 * @return 超文字的高
	 */
	public final float getHeight() {
		return mDrawableBox.getHeight();
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
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

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public final void measure(float lineHeight, float baselineOffset) {
		onMeasure(lineHeight, baselineOffset);
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
		mDrawableBox.resize(width, height);
	}

	/**
	 * 设置唯一标识
	 *
	 * @param tag tag
	 */
	public final void setTag(Object tag) {
		mDrawableBox.setTag(tag);
	}

	public final void setBackground(Appearance background) {
		mDrawableBox.setBackground(background);
	}

	public final void setForeground(Appearance foreground) {
		mDrawableBox.setForeground(foreground);
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public final Element getDrawableBox() {
		return mDrawableBox;
	}
}
