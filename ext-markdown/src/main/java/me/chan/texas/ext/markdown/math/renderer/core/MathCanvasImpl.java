package me.chan.texas.ext.markdown.math.renderer.core;

import android.graphics.Canvas;

import androidx.annotation.NonNull;

import me.chan.texas.renderer.core.graphics.TexasCanvas;

public class MathCanvasImpl implements MathCanvas {
	private final TexasCanvas mImpl;

	public MathCanvasImpl(TexasCanvas impl) {
		mImpl = impl;
	}

	@Override
	public void save() {
		mImpl.save();
	}

	@Override
	public void translate(float dx, float dy) {
		mImpl.translate(dx, dy);
	}

	@Override
	public void drawRect(int left, int top, int right, int bottom, MathPaint paint) {
		mImpl.drawRect(left, top, right, bottom, paint.getCore());
	}

	@Override
	public void restore() {
		mImpl.restore();
	}

	@Override
	public void drawLine(float startX, float startY, float stopX, float stopY, @NonNull MathPaint paint) {
		mImpl.drawLine(startX, startY, stopX, stopY, paint.getCore());
	}

	@Override
	public void drawText(@NonNull String text, float x, float y, MathPaint paint) {
		mImpl.drawText(text, x, y, paint.getCore());
	}

	@Override
	public void reset(Canvas canvas) {
		mImpl.reset(canvas);
	}

	@Override
	public void rotate(float degrees, float x, float y) {
		mImpl.rotate(degrees, x, y);
	}

	@Override
	public void scale(float sx, float sy) {
		mImpl.scale(sx, sy);
	}

	@Override
	public void scale(float sx, float sy, float px, float py) {
		mImpl.scale(sx, sy, px, py);
	}
}
