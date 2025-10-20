package me.chan.texas.ext.markdown.math.renderer;

import android.graphics.Paint;
import android.util.Log;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

public abstract class RendererNode {
	public static final boolean DEBUG = true;

	private final float mScale;
	private int mWidth;
	private int mHeight;
	private float mLeft;
	private float mTop;

	public RendererNode(float scale) {
		mScale = scale;
	}

	public final void measure(MathPaint paint) {
		float textSize = paint.getTextSize();
		if (mScale != 1f) {
			paint.setTextSize(textSize * mScale);
		}

		onMeasure(paint);

		if (mScale != 1f) {
			paint.setTextSize(textSize);
		}
	}

	protected final void setMeasuredSize(int width, int height) {
		mWidth = width;
		mHeight = height;
	}

	public final void layout(float left, float top) {
		setLayoutBounds(left, top);
		onLayoutChildren();
	}

	public final void draw(MathCanvas canvas, MathPaint paint) {
		canvas.save();
		canvas.translate(mLeft, mTop);

		float textSize = paint.getTextSize();
		if (mScale != 1f) {
			paint.setTextSize(textSize * mScale);
		}

		if (DEBUG) {
			Paint.Style style = paint.getStyle();
			paint.setStyle(Paint.Style.STROKE);
			Log.d("MathRenderer", this + "->" + toPretty());
			canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
			paint.setStyle(style);
		}

		onDraw(canvas, paint);

		if (mScale != 1f) {
			paint.setTextSize(textSize);
		}
		canvas.restore();
	}

	public final void translate(float dx, float dy) {
		mLeft += dx;
		mTop += dy;
	}

	protected abstract void onMeasure(MathPaint paint);

	protected void onLayoutChildren() {
	}

	protected final void setLayoutBounds(float left, float top) {
		mLeft = left;
		mTop = top;
	}

	protected abstract void onDraw(MathCanvas canvas, MathPaint paint);

	public final int getWidth() {
		return mWidth;
	}

	public final int getHeight() {
		return mHeight;
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
				", " + getBottom() +
				"])";
	}
}
