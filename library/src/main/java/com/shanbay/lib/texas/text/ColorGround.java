package com.shanbay.lib.texas.text;

import android.graphics.Canvas;
import android.text.TextPaint;

import com.shanbay.lib.texas.misc.ObjectFactory;

/**
 * 前景/背景颜色
 */
public class ColorGround extends Appearance {
	private static final ObjectFactory<ColorGround> POOL = new ObjectFactory<>(512);

	private int mColor;

	private ColorGround(int color) {
		mColor = color;
	}

	public int getColor() {
		return mColor;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ColorGround that = (ColorGround) o;
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

	public static ColorGround obtain(int color) {
		ColorGround ground = POOL.acquire();
		if (ground == null) {
			return new ColorGround(color);
		}
		ground.mColor = color;
		ground.reuse();
		return ground;
	}

	public static void clean() {
		POOL.clean();
	}
}
