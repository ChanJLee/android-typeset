package me.chan.texas.ext.markdown.math.renderer;

import android.graphics.Paint;
import android.view.View;

import androidx.annotation.IntRange;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

public abstract class RendererNode {
	public static final boolean DEBUG = false;

	protected MathPaint.Styles mStyles;
	private int mWidth;
	private int mHeight;
	private float mLeft;
	private float mTop;
	private Object mTag;
	private boolean mClipContent = false;

	public RendererNode(MathPaint.Styles styles) {
		mStyles = styles;
	}

	public void setStyles(MathPaint.Styles styles) {
		mStyles = styles;
	}

	public final void measure(MathPaint paint) {
		measure(paint, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
	}

	public void setClipContent(boolean clipContent) {
		mClipContent = clipContent;
	}

	public final void measure(MathPaint paint, int widthSpec, int heightSpec) {
		if (mStyles != null) {
			paint.save(mStyles);
		}

		onMeasure(paint, widthSpec, heightSpec);

		if (mStyles != null) {
			paint.restore();
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
		if (mClipContent) {
			canvas.clipRect(0, 0, getWidth(), getHeight());
		}

		if (mStyles != null) {
			paint.save(mStyles);
		}

		onDraw(canvas, paint);

		drawDebug(canvas, paint);

		if (mStyles != null) {
			paint.restore();
		}
		canvas.restore();
	}

	protected void drawDebug(MathCanvas canvas, MathPaint paint) {
		if (DEBUG) {
			paint.save();
			onDrawDebug(canvas, paint);
			paint.restore();
		}
	}

	protected void onDrawDebug(MathCanvas canvas, MathPaint paint) {
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
		drawDebugBounds(canvas, paint);
	}

	private void drawDebugBounds(MathCanvas canvas, MathPaint paint) {
		canvas.drawLine(0, 0, getWidth(), getHeight(), paint);
		canvas.drawLine(getWidth(), 0, 0, getHeight(), paint);
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

	public float getContentCenterY() {
		return getHeight() / 2.0f;
	}

	public Object getTag() {
		return mTag;
	}

	public void setTag(Object tag) {
		mTag = tag;
	}

	protected abstract String toPretty();

	public MathPaint.Styles getStyles() {
		return mStyles;
	}

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

	public static int makeUnspecifiedMeasureSpec() {
		return makeMeasureSpec(0, UNSPECIFIED);
	}
}
