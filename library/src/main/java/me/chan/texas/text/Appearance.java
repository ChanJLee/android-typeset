package me.chan.texas.text;

import me.chan.texas.misc.RectF;

import androidx.annotation.AnyThread;

import me.chan.texas.renderer.RendererContext;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.renderer.core.graphics.TexasCanvas;

/**
 * 外观，用于内容绘制
 */
public abstract class Appearance {
	/**
	 * <pre>
	 *       ----------- <br/>
	 * hello | |world| | I am fine. <br/>
	 *       ----------- <br/>
	 * 大的矩形是 outer 包含文字以及和前后单词的间隙
	 * 小的矩形是 inner 包裹文字
	 * </pre>
	 * * @param canvas    canvas
	 *
	 * @param paint   paint
	 * @param inner   包裹整个文字的矩形
	 * @param outer   包裹整个文字以及水平方向间隙的矩形
	 * @param context 绘制上下文，可以知道当前绘制单元前后位置
	 */
	@AnyThread
	public abstract void draw(TexasCanvas canvas, TexasPaint paint, RectF inner, RectF outer, RendererContext context);
}
