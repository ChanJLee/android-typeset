package me.chan.te.text;

import android.graphics.Canvas;
import android.text.TextPaint;

import me.chan.te.annotations.Hidden;

/**
 * 一个box为排版中的绘制单元
 * <p>
 * 比如一个单词，一张图片
 */
public abstract class Box extends Paragraph.Element {
	protected float mWidth;
	protected float mHeight;
	protected OnClickedListener mOnClickedListener;

	public Box(float width, float height) {
		mWidth = width;
		mHeight = height;
	}

	public void setOnClickedListener(OnClickedListener listener) {
		mOnClickedListener = listener;
	}

	public OnClickedListener getOnClickedListener() {
		return mOnClickedListener;
	}

	public float getWidth() {
		return mWidth;
	}

	public float getHeight() {
		return mHeight;
	}

	@Override
	public void recycle() {
		super.recycle();
		mWidth = -1;
		mHeight = -1;
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
				mOnClickedListener == box.mOnClickedListener;
	}
}
