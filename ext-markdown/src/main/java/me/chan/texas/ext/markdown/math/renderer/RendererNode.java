package me.chan.texas.ext.markdown.math.renderer;

import android.util.Log;

import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;

public abstract class RendererNode {
	public static final boolean DEBUG = false;

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
		canvas.scale(mScale, mScale);

		if (DEBUG) {
			Log.d("MathRenderer", "translate(" + mLeft + "," + mTop + "), " + toPretty());
			canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
		}

		onDraw(canvas, paint);
		canvas.restore();
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

	protected abstract String toPretty();

	@Override
	public String toString() {
		return "RendererNode{" +
				", mWidth=" + mWidth +
				", mHeight=" + mHeight +
				", mLeft=" + mLeft +
				", mTop=" + mTop +
				'}';
	}
}
