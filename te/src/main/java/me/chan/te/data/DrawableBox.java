package me.chan.te.data;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;

import me.chan.te.misc.ObjectFactory;

public class DrawableBox extends Box implements Recyclable {
	private static final ObjectFactory<DrawableBox> POOL = new ObjectFactory<>(512);

	private Drawable mDrawable;

	public DrawableBox(@NonNull Drawable drawable, Object extra) {
		super(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), extra);
		mDrawable = drawable;
	}

	@Override
	public Object clone() {
		return DrawableBox.obtain(mDrawable, mExtra);
	}

	@Override
	public void append(Box other) {
		// do nothing
	}

	@Override
	public void append(Penalty penalty) {
		// do nothing
	}

	@Override
	public boolean canMerge(Box other) {
		return false;
	}

	@Override
	public void draw(Canvas canvas, TextPaint paint, float x, float y) {
		mDrawable.setBounds((int) x, (int) (y - getHeight()), (int) (x + getWidth()), (int) y);
		mDrawable.draw(canvas);
	}

	@Override
	public String toString() {
		return mDrawable.toString();
	}

	@Override
	public boolean canSpilt() {
		return false;
	}

	@Nullable
	@Override
	public Box spilt(float limitWidth) {
		return null;
	}

	@Override
	protected void onCopy(@NonNull Box other) {
		if (!(other instanceof DrawableBox)) {
			return;
		}
		DrawableBox drawableBox = (DrawableBox) other;
		mDrawable = drawableBox.mDrawable;
		mExtra = other.mExtra;
	}

	@Override
	public void recycle() {
		mDrawable = null;
		POOL.release(this);
	}

	public static DrawableBox obtain(Drawable drawable) {
		return obtain(drawable, null);
	}

	public static DrawableBox obtain(Drawable drawable, Object extra) {
		DrawableBox drawableBox = POOL.acquire();
		if (drawableBox == null) {
			return new DrawableBox(drawable, extra);
		}
		drawableBox.mExtra = drawable;
		drawableBox.mDrawable = drawable;
		return drawableBox;
	}
}
