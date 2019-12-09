package com.shanbay.lib.texas.text;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import androidx.annotation.NonNull;

import android.text.TextPaint;

import com.shanbay.lib.texas.annotations.Hidden;
import com.shanbay.lib.texas.misc.ObjectFactory;

/**
 * drawable box
 */
@Hidden
public class DrawableBox extends Box {
	private static final ObjectFactory<DrawableBox> POOL = new ObjectFactory<>(512);
	private static final int[] STATE_PRESSED = {
			android.R.attr.state_pressed,
	};
	private static final int[] STATE_NORMAL = {
			-android.R.attr.state_pressed
	};

	private Drawable mDrawable;

	public DrawableBox(@NonNull Drawable drawable, float width, float height, OnClickedListener onClickedListener) {
		super(width, height);
		mDrawable = drawable;
		mOnClickedListener = onClickedListener;
	}

	@Override
	public void draw(Canvas canvas, TextPaint paint, float x, float y) {
		Drawable drawable = mDrawable;
		if (mDrawable instanceof StateListDrawable) {
			StateListDrawable stateListDrawable = (StateListDrawable) mDrawable;
			stateListDrawable.setState(isSelected() ? STATE_PRESSED : STATE_NORMAL);
			drawable = stateListDrawable.getCurrent();
		}

		drawable.setBounds((int) x, (int) (y - getHeight()), (int) (x + getWidth()), (int) y);
		drawable.draw(canvas);
	}

	public Drawable getDrawable() {
		return mDrawable;
	}

	@Override
	public void recycle() {
		if (isRecycled()) {
			return;
		}

		super.recycle();
		mDrawable = null;
		mWidth = -1;
		mHeight = -1;
		mOnClickedListener = null;
		POOL.release(this);
	}

	public static DrawableBox obtain(Drawable drawable, float width, float height, OnClickedListener onClickedListener) {
		DrawableBox drawableBox = POOL.acquire();
		if (drawableBox == null) {
			return new DrawableBox(drawable, width, height, onClickedListener);
		}

		drawableBox.mWidth = width;
		drawableBox.mHeight = height;
		drawableBox.mDrawable = drawable;
		drawableBox.mOnClickedListener = onClickedListener;
		drawableBox.reuse();
		return drawableBox;
	}

	public static void clean() {
		POOL.clean();
	}
}
