package me.chan.texas.ext.markdown.math.renderer;

import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;

public abstract class RendererNode {
	private final float mScale;
	private int mWidth;
	private int mHeight;
	private float mLeft;
	private float mTop;
	private float mRight;
	private float mBottom;

	public RendererNode(float scale) {
		mScale = scale;
	}

	public final void measure(TexasPaint paint) {
		onMeasure(paint);
	}

	protected final void setMeasuredSize(int width, int height) {
		mWidth = width;
		mHeight = height;
	}

	public final void layout(float left, float top, float right, float bottom) {
		mLeft = left;
		mTop = top;
		mRight = right;
		mBottom = bottom;
		onLayout(left, top, right, bottom);
	}

	public final void draw(TexasCanvas canvas) {
		onDraw(canvas);
	}

	protected abstract void onMeasure(TexasPaint paint);

	protected void onLayout(float left, float top, float right, float bottom) {
	}

	protected abstract void onDraw(TexasCanvas canvas);

	public final int getWidth() {
		return (int) (mWidth * mScale);
	}

	public final int getHeight() {
		return (int) (mHeight * mScale);
	}
}
