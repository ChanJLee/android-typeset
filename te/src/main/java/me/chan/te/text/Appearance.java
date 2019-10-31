package me.chan.te.text;

import android.graphics.Canvas;
import android.text.TextPaint;

import me.chan.te.misc.Recyclable;

public interface Appearance extends Recyclable {
	/**
	 * 检查样式是否冲突
	 *
	 * @param other 另外一个background
	 * @return 是否元素Appearance冲突，如果不冲突，表示其对应的Box可以被合并
	 */
	boolean isConflict(Appearance other);

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
