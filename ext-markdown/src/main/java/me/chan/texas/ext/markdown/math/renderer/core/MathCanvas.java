package me.chan.texas.ext.markdown.math.renderer.core;

import android.graphics.Canvas;

import androidx.annotation.NonNull;

public interface MathCanvas {
	void save();

	void translate(float dx, float dy);

	void drawRect(int left, int top, int right, int bottom, MathPaint paint);

	void restore();

	void drawLine(float startX, float startY, float stopX, float stopY, @NonNull MathPaint paint);

	void drawText(@NonNull String text, float x, float y, MathPaint paint);

	void reset(Canvas canvas);

	void rotate(float degrees, float x, float y);

	void scale(float sx, float sy);
}
