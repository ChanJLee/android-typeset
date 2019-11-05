package me.chan.te.text;

import android.graphics.Canvas;
import android.text.TextPaint;

import me.chan.te.misc.Recyclable;

public interface Appearance extends Recyclable {
	/**
	 * @param canvas    canvas
	 * @param textPaint paint
	 * @param left      left
	 * @param top       top
	 * @param right     right
	 * @param bottom    bottom
	 */
	void draw(Canvas canvas, TextPaint textPaint, float left, float top, float right, float bottom);
}
