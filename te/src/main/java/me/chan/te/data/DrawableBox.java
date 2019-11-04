package me.chan.te.data;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;

import me.chan.te.misc.ObjectFactory;
import me.chan.te.misc.Recyclable;

public class DrawableBox extends Box implements Recyclable {
	private static final ObjectFactory<DrawableBox> POOL = new ObjectFactory<>(512);

	private Drawable mDrawable;

	public DrawableBox(@NonNull Drawable drawable, float width, float height, Object extra) {
		super(width, height, extra);
		mDrawable = drawable;
	}

	@Override
	public Object clone() {
		return DrawableBox.obtain(mDrawable, getWidth(), getHeight(), getExtra());
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

	public Drawable getDrawable() {
		return mDrawable;
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
		mWidth = -1;
		mHeight = -1;
		mExtra = null;
		clear();
		POOL.release(this);
	}

	public static DrawableBox obtain(Drawable drawable, float width, float height, Object extra) {
		DrawableBox drawableBox = POOL.acquire();
		if (drawableBox == null) {
			return new DrawableBox(drawable, width, height, extra);
		}
		drawableBox.mWidth = width;
		drawableBox.mHeight = height;
		drawableBox.mExtra = extra;
		drawableBox.mDrawable = drawable;
		return drawableBox;
	}

	public static void clean() {
		POOL.clean();
	}
}
