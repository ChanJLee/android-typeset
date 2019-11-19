package me.chan.te.text;

import android.graphics.Canvas;
import android.text.TextPaint;

import me.chan.te.annotations.Hidden;
import me.chan.te.renderer.Touchable;

/**
 * 一个box为排版中的绘制单元
 * <p>
 * 比如一个单词，一张图片
 */
public abstract class Box extends Paragraph.Element implements Touchable {
	protected float mWidth;
	protected float mHeight;
	protected TouchListener mTouchListener;

	public Box(float width, float height) {
		mWidth = width;
		mHeight = height;
	}

	@Override
	public void setTouchListener(TouchListener listener) {
		mTouchListener = listener;
	}

	@Override
	public TouchListener getTouchListener() {
		return mTouchListener;
	}

	public float getWidth() {
		return mWidth;
	}

	public float getHeight() {
		return mHeight;
	}

	@Hidden
	public abstract void draw(Canvas canvas, TextPaint paint, float x, float y);

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Box box = (Box) o;
		return Float.compare(box.mWidth, mWidth) == 0 &&
				Float.compare(box.mHeight, mHeight) == 0 &&
				mTouchListener == box.mTouchListener;
	}
}
