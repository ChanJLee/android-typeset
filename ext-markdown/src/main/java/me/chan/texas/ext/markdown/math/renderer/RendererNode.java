package me.chan.texas.ext.markdown.math.renderer;

import android.graphics.Paint;
import android.util.Log;

import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;

public abstract class RendererNode {
	public static final boolean DEBUG = true;

	private float mScale = 1;
	private int mWidth;
	private int mHeight;
	private float mLeft;
	private float mTop;

	public final void measure(TexasPaint paint) {
		onMeasure(paint);
	}

	public void setScale(float scale) {
		mScale = scale;
	}

	protected final void setMeasuredSize(int width, int height) {
		mWidth = width;
		mHeight = height;
	}

	public final void layout(float left, float top) {
		setLayoutBounds(left, top);
		onLayoutChildren();
	}

	public final void draw(TexasCanvas canvas, TexasPaint paint) {
		canvas.save();
		canvas.translate(mLeft, mTop);
		if (DEBUG) {
			Paint.Style style = paint.getStyle();
			paint.setStyle(Paint.Style.STROKE);
			Log.d("MathRenderer", this + "->" + toPretty());
			canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
			paint.setStyle(style);
		}

		canvas.scale(mScale, mScale);

		onDraw(canvas, paint);
		canvas.restore();
	}

	public float getScale() {
		return mScale;
	}

	public final void translate(float dx, float dy) {
		mLeft += dx;
		mTop += dy;
	}

	protected abstract void onMeasure(TexasPaint paint);

	protected void onLayoutChildren() {
	}

	protected final void setLayoutBounds(float left, float top) {
		mLeft = left;
		mTop = top;
	}

	protected abstract void onDraw(TexasCanvas canvas, TexasPaint paint);

	public final int getWidth() {
		return (int) (mWidth * mScale);
	}

	public final int getHeight() {
		return (int) (mHeight * mScale);
	}

	public final float getLeft() {
		return mLeft;
	}

	public final float getTop() {
		return mTop;
	}

	public final float getRight() {
		return mLeft + getWidth();
	}

	public final float getBottom() {
		return mTop + getHeight();
	}

	protected abstract String toPretty();

	@Override
	public String toString() {
		return "(w=" + mWidth +
				", h=" + mHeight +
				",[" + mLeft +
				", " + mTop +
				", " + getRight() +
				", " + getRight() +
				"])";
	}
}
