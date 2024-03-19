package me.chan.texas.text;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;

/**
 * 点下划线
 */
public class DotUnderLine extends Appearance {
	private static final PathEffect DASH_EFFECT = new DashPathEffect(new float[]{12, 6, 12, 6}, 0);
	private static final int DEFAULT_WIDTH = 4;
	private static final int DEFAULT_BOTTOM_OFFSET = DEFAULT_WIDTH / 2;

	private int mColor;

	private int mWidth;
	private final Path mPath = new Path();

	public DotUnderLine(int color) {
		this(color, DEFAULT_WIDTH);
	}

	public DotUnderLine(int color, int width) {
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

		DotUnderLine dotUnderLine = (DotUnderLine) o;

		return mColor == dotUnderLine.mColor && mWidth == dotUnderLine.mWidth;
	}

	@Override
	public void draw(Canvas canvas, Paint paint, RectF inner, RectF outer, TypesetContext context) {
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(mWidth);
		paint.setColor(mColor);
		paint.setPathEffect(DASH_EFFECT);

		float bottom = inner.bottom - DEFAULT_BOTTOM_OFFSET;
		mPath.moveTo(inner.left, bottom);
		mPath.lineTo(inner.right, bottom);
		canvas.drawPath(mPath, paint);
		mPath.reset();
	}
}
