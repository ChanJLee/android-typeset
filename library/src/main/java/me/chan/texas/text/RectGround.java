package me.chan.texas.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * 前景/背景颜色
 */
public class RectGround extends Appearance {
	private int mColor;
	private int mRadius;

	/**
	 * @param color 颜色
	 */
	public RectGround(int color) {
		this(color, 0);
	}

	/**
	 * @param color  颜色
	 * @param radius 圆角半径
	 */
	public RectGround(int color, int radius) {
		mColor = color;
		mRadius = radius;
	}

	public int getColor() {
		return mColor;
	}

	public void setColor(int color) {
		mColor = color;
	}

	public int getRadius() {
		return mRadius;
	}

	public void setRadius(int radius) {
		mRadius = radius;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RectGround that = (RectGround) o;
		return mColor == that.mColor && mRadius == that.mRadius;
	}

	@Override
	public void draw(Canvas canvas, Paint paint, RectF inner, RectF outer, TypesetContext context) {
		paint.setColor(mColor);
		if (mRadius > 0) {
			canvas.drawRoundRect(inner, mRadius, mRadius, paint);
		} else {
			canvas.drawRect(inner, paint);
		}
	}
}
