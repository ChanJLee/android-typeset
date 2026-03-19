package me.chan.texas.text;

import androidx.annotation.RestrictTo;

import me.chan.texas.measurer.Measurer;
import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.text.layout.DrawableBox;
import me.chan.texas.text.layout.StateList;

/**
 * 超文字
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
public abstract class HyperSpan extends DrawableBox implements Measurable {

	public HyperSpan() {
		super(0, 0);
	}

	public HyperSpan(float width, float height) {
		super(width, height);
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
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
}
