package me.chan.texas.text;

import android.graphics.Canvas;
import android.graphics.Paint;

import me.chan.texas.misc.RectF;

import me.chan.texas.renderer.RendererContext;


public class UnderLine extends Appearance {
	private static final int DEFAULT_WIDTH = 6;

	private int mColor;
	private int mWidth;

	public UnderLine(int color) {
		this(color, DEFAULT_WIDTH);
	}

	public UnderLine(int color, int width) {
		mColor = color;
		mWidth = width;
	}

	public int getColor() {
		return mColor;
	}

	public void setColor(int color) {
		mColor = color;
	}

	public int getWidth() {
		return mWidth;
	}

	public void setWidth(int width) {
		mWidth = width;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		UnderLine dotUnderLine = (UnderLine) o;

		return mColor == dotUnderLine.mColor && mWidth == dotUnderLine.mWidth;
	}

	@Override
	public void draw(Canvas canvas, Paint paint, RectF inner, RectF outer, RendererContext context) {
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(mWidth);
		paint.setColor(mColor);

		canvas.drawLine(inner.left, inner.bottom, inner.right, inner.bottom, paint);
	}
}
