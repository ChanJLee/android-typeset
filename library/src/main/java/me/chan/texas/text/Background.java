package me.chan.texas.text;

import android.graphics.Canvas;
import android.text.TextPaint;

import me.chan.texas.misc.ObjectFactory;

/**
 * 背景
 */
public class Background extends Appearance {
	private static final ObjectFactory<Background> POOL = new ObjectFactory<>(512);

	private int mColor;

	private Background(int color) {
		mColor = color;
	}

	public int getColor() {
		return mColor;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Background that = (Background) o;
		return mColor == that.mColor;
	}

	@Override
	public void draw(Canvas canvas, TextPaint textPaint, float left, float top, float right, float bottom) {
		textPaint.setColor(mColor);
		canvas.drawRect(left, top, right, bottom, textPaint);
	}

	@Override
	public void recycle() {
		if (isRecycled()) {
			return;
		}

		super.recycle();
		mColor = -1;
		POOL.release(this);
	}

	public static Background obtain(int color) {
		Background background = POOL.acquire();
		if (background == null) {
			return new Background(color);
		}
		background.mColor = color;
		background.reuse();
		return background;
	}

	public static void clean() {
		POOL.clean();
	}
}
