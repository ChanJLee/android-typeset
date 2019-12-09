package me.chan.texas.text;

import android.graphics.Canvas;
import android.text.TextPaint;

import me.chan.texas.annotations.Hidden;
import me.chan.texas.misc.DefaultRecyclable;

/**
 * 外观，用于内容绘制
 */
@Hidden
abstract class Appearance extends DefaultRecyclable {
	/**
	 * @param canvas    canvas
	 * @param textPaint paint
	 * @param left      left
	 * @param top       top
	 * @param right     right
	 * @param bottom    bottom
	 */
	public abstract void draw(Canvas canvas, TextPaint textPaint, float left, float top, float right, float bottom);
}
