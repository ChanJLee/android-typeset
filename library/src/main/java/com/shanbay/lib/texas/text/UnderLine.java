package com.shanbay.lib.texas.text;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.text.TextPaint;

import com.shanbay.lib.texas.misc.ObjectFactory;

/**
 * 下划线
 */
public class UnderLine extends Appearance {
	private static final PathEffect DASH_EFFECT = new DashPathEffect(new float[]{16, 8, 16, 8}, 0);
	/**
	 * 绘制都是发生在主线程的，所以不用担心这里共享对象会有线程竞争问题
	 */
	private static final Path PATH = new Path();

	private static final ObjectFactory<UnderLine> POOL = new ObjectFactory<>(256);

	private int mColor;

	private UnderLine(int color) {
		mColor = color;
	}

	public int getColor() {
		return mColor;
	}

	public void setColor(int color) {
		mColor = color;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		UnderLine underLine = (UnderLine) o;

		return mColor == underLine.mColor;
	}

	@Override
	public void draw(Canvas canvas, TextPaint textPaint, float left, float top, float right, float bottom) {
		textPaint.setStyle(Paint.Style.STROKE);
		textPaint.setColor(mColor);
		textPaint.setPathEffect(DASH_EFFECT);
		PATH.reset();
		PATH.moveTo(left, bottom);
		PATH.lineTo(right, bottom);
		canvas.drawPath(PATH, textPaint);
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

	public static UnderLine obtain(int color) {
		UnderLine underLine = POOL.acquire();
		if (underLine == null) {
			return new UnderLine(color);
		}
		underLine.mColor = color;
		underLine.reuse();
		return underLine;
	}

	public static void clean() {
		POOL.clean();
	}
}
