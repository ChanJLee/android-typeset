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
		setLayoutBounds(left, top, mLeft + getWidth(), top + getHeight());
		onLayout(left, top, right, bottom);
	}

	public final void draw(TexasCanvas canvas, TexasPaint paint) {
		canvas.save();
		canvas.translate(mLeft, mTop);
		canvas.scale(mScale, mScale);
		onDraw(canvas, paint);
		canvas.restore();
	}

	protected abstract void onMeasure(TexasPaint paint);

	protected void onLayout(float left, float top, float right, float bottom) {
	}

	protected final void setLayoutBounds(float left, float top, float right, float bottom) {
		mLeft = left;
		mTop = top;
		mRight = right;
		mBottom = bottom;
	}

	protected abstract void onDraw(TexasCanvas canvas, TexasPaint paint);

	public final int getWidth() {
		return (int) (mWidth * mScale);
	}

	public final int getHeight() {
		return (int) (mHeight * mScale);
	}

	@Override
	public String toString() {
		return "RendererNode{" +
				"mScale=" + mScale +
				", mWidth=" + mWidth +
				", mHeight=" + mHeight +
				", mLeft=" + mLeft +
				", mTop=" + mTop +
				", mRight=" + mRight +
				", mBottom=" + mBottom +
				'}';
	}
}
