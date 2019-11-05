package me.chan.te.data;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.TextPaint;

import me.chan.te.misc.ObjectFactory;
import me.chan.te.misc.Recyclable;

public class DrawableBox extends Box implements Recyclable {
	private static final ObjectFactory<DrawableBox> POOL = new ObjectFactory<>(512);

	private Drawable mDrawable;

	public DrawableBox(@NonNull Drawable drawable, float width, float height) {
		super(width, height);
		mDrawable = drawable;
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
	public void recycle() {
		mDrawable = null;
		mWidth = -1;
		mHeight = -1;
		POOL.release(this);
	}

	public static DrawableBox obtain(Drawable drawable, float width, float height) {
		DrawableBox drawableBox = POOL.acquire();
		if (drawableBox == null) {
			return new DrawableBox(drawable, width, height);
		}

		drawableBox.mWidth = width;
		drawableBox.mHeight = height;
		drawableBox.mDrawable = drawable;
		return drawableBox;
	}

	public static void clean() {
		POOL.clean();
	}
}
