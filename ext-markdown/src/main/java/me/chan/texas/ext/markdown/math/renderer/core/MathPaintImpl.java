package me.chan.texas.ext.markdown.math.renderer.core;

import android.graphics.Paint;

import java.util.Stack;

import me.chan.texas.renderer.core.graphics.TexasPaint;

public class MathPaintImpl implements MathPaint {
	private final TexasPaint mImpl;
	private final Stack<State> mStates = new Stack<>();

	public MathPaintImpl(TexasPaint impl) {
		mImpl = impl;
	}

	@Override
	public float getTextSize() {
		return mImpl.getTextSize();
	}

	@Override
	public Paint.Style getStyle() {
		return mImpl.getStyle();
	}

	@Override
	public void setStyle(Paint.Style style) {
		mImpl.setStyle(style);
	}

	@Override
	public void setTextSize(float textSize) {
		mImpl.setTextSize(textSize);
	}

	@Override
	public float getStrokeWidth() {
		return mImpl.getStrokeWidth();
	}

	@Override
	public void setStrokeWidth(float width) {
		mImpl.setStrokeWidth(width);
	}

	@Override
	public int getColor() {
		return mImpl.getColor();
	}

	@Override
	public void setColor(int color) {
		mImpl.setColor(color);
	}

	@Override
	public void getFontMetrics(Paint.FontMetrics fontMetrics) {
		mImpl.getFontMetrics(fontMetrics);
	}

	@Override
	public float getRunAdvance(CharSequence text, int start, int end, int contextStart, int contextEnd, boolean isRtl, int offset) {
		return mImpl.getRunAdvance(text, start, end, contextStart, contextEnd, isRtl, offset);
	}

	@Override
	public TexasPaint getCore() {
		return mImpl;
	}

	@Override
	public void save() {
		mStates.push(new State(this));
	}

	@Override
	public void restore() {
		if (mStates.empty()) {
			throw new IllegalStateException("No saved state");
		}

		State state = mStates.pop();
		state.restore(this);
	}

	private static class State {
		private final float mTextSize;
		private final Paint.Style mStyle;
		private final float mStrokeWidth;
		private final int mColor;

		public State(MathPaint paint) {
			mTextSize = paint.getTextSize();
			mStyle = paint.getStyle();
			mStrokeWidth = paint.getStrokeWidth();
			mColor = paint.getColor();
		}

		public void restore(MathPaint paint) {
			paint.setTextSize(mTextSize);
			paint.setStyle(mStyle);
			paint.setStrokeWidth(mStrokeWidth);
			paint.setColor(mColor);
		}
	}
}
