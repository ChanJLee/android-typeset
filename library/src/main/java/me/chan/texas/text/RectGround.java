package me.chan.texas.text;

import android.graphics.Canvas;
import android.graphics.Paint;

import me.chan.texas.misc.RectF;

import me.chan.texas.renderer.RendererContext;


public class RectGround extends Appearance {
	private int mColor;
	private int mRadius;

	
	public RectGround(int color) {
		this(color, 0);
	}

	
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
	public void draw(Canvas canvas, Paint paint, RectF inner, RectF outer, RendererContext context) {
		paint.setColor(mColor);
		if (mRadius > 0) {
			canvas.drawRoundRect(inner.left, inner.top, inner.right, inner.bottom, mRadius, mRadius, paint);
		} else {
			canvas.drawRect(inner.left, inner.top, inner.right, inner.bottom, paint);
		}
	}
}
