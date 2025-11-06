package me.chan.texas.ext.markdown.math.renderer;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import androidx.annotation.IntRange;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

public abstract class RendererNode {
	public static final boolean DEBUG = false;

	private float mScale;
	private int mWidth;
	private int mHeight;
	private float mLeft;
	private float mTop;
	private Object mTag;

	public RendererNode(float scale) {
		mScale = scale;
	}

	public void setScale(float scale) {
		mScale = scale;
	}

	public final void measure(MathPaint paint) {
		measure(paint, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
	}

	public final void measure(MathPaint paint, int widthSpec, int heightSpec) {
		float textSize = paint.getTextSize();
		if (mScale != 1f) {
			paint.setTextSize(textSize * mScale);
		}

		onMeasure(paint, widthSpec, heightSpec);

		if (mScale != 1f) {
			paint.setTextSize(textSize);
		}
	}

	protected final void setMeasuredSize(int width, int height) {
		mWidth = width;
		mHeight = height;
	}

	public float getScale() {
		return mScale;
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

		onDraw(canvas, paint);

		onDrawDebug(canvas, paint);

		if (mScale != 1f) {
			paint.setTextSize(textSize);
		}
		canvas.restore();
	}

	protected void onDrawDebug(MathCanvas canvas, MathPaint paint) {
		if (DEBUG) {
			paint.save();
			paint.setColor(Color.BLUE);
			paint.setStyle(Paint.Style.STROKE);
			Log.d("MathRenderer", this + "->" + toPretty() + "[" + getWidth() + "," + getHeight() + "]");
			canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

			canvas.drawLine(0, 0, getWidth(), getHeight(), paint);
			canvas.drawLine(getWidth(), 0, 0, getHeight(), paint);

			paint.restore();
		}
	}

	public final void translate(float dx, float dy) {
		mLeft += dx;
		mTop += dy;
	}

	protected abstract void onMeasure(MathPaint paint, int widthSpec, int heightSpec);

	protected void onLayoutChildren() {
	}

	protected final void setLayoutBounds(float left, float top) {
		mLeft = left;
		mTop = top;
	}

	public abstract float getBaseline();

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

	public final float getCenterX() {
		return (getLeft() + getRight()) / 2;
	}

	public final float getCenterY() {
		return (getTop() + getBottom()) / 2;
	}

	public Object getTag() {
		return mTag;
	}

	public void setTag(Object tag) {
		mTag = tag;
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

	private static final int MODE_SHIFT = 30;
	private static final int MODE_MASK = 0x3 << MODE_SHIFT;

	public static final int UNSPECIFIED = 0;
	public static final int EXACTLY = 1 << MODE_SHIFT;
	public static final int AT_MOST = 2 << MODE_SHIFT;

	public static int getMode(int measureSpec) {
		//noinspection ResourceType
		return (measureSpec & MODE_MASK);
	}

	public static int getSize(int measureSpec) {
		return (measureSpec & ~MODE_MASK);
	}

	public static int makeMeasureSpec(@IntRange(from = 0, to = (1 << MODE_SHIFT) - 1) int size,
									  int mode) {
		return (size & ~MODE_MASK) | (mode & MODE_MASK);
	}
}
