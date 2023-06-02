package me.chan.texas.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;

/**
 * 下划线
 */
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
	public void draw(Canvas canvas, TextPaint textPaint, RectF inner, RectF outer, DrawContext context) {
		textPaint.setStyle(Paint.Style.FILL);
		textPaint.setStrokeWidth(mWidth);
		textPaint.setColor(mColor);

		canvas.drawLine(inner.left, inner.bottom, inner.right, inner.bottom, textPaint);
	}
}
