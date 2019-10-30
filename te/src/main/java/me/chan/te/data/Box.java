package me.chan.te.data;

import android.graphics.Canvas;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;

import me.chan.te.annotations.Hidden;

/**
 * 一个box为排版中的绘制单元
 * <p>
 * 比如一个单词，一张图片
 */
public abstract class Box implements Cloneable, Element {
	static final int FLAG_NONE = 0;
	static final int FLAG_PENALTY = 2;
	static final int FLAG_SPILT = 1;

	// 检查 equals
	protected float mWidth;
	protected float mHeight;
	private int mFlag = FLAG_NONE;
	protected Object mExtra;

	public Box(float width, float height, Object extra) {
		mWidth = width;
		mHeight = height;
		mExtra = extra;
	}

	public Object getExtra() {
		return mExtra;
	}

	public float getWidth() {
		return mWidth;
	}

	public float getHeight() {
		return mHeight;
	}

	public abstract Object clone();

	@Hidden
	public abstract void append(Box other);

	@Hidden
	public abstract void append(Penalty penalty);

	@Hidden
	public abstract boolean canMerge(Box other);

	protected void clearFlag() {
		setFlag(FLAG_NONE);
	}

	@Hidden
	public boolean isPenalty() {
		return mFlag == FLAG_PENALTY;
	}

	@Hidden
	public boolean isSplit() {
		return mFlag == FLAG_SPILT;
	}

	@Hidden
	public void setFlag(int flag) {
		mFlag = flag;
	}

	@Hidden
	public abstract void draw(Canvas canvas, TextPaint paint, float x, float y);

	public abstract String toString();

	public abstract boolean canSpilt();

	@Nullable
	public abstract Box spilt(float limitWidth);

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Box box = (Box) o;
		return Float.compare(box.mWidth, mWidth) == 0 &&
				Float.compare(box.mHeight, mHeight) == 0 &&
				mFlag == box.mFlag &&
				mExtra == box.mExtra;
	}

	@CallSuper
	public void copy(@NonNull Box other) {
		this.mFlag = other.mFlag;
		this.mWidth = other.mWidth;
		this.mHeight = other.mHeight;
		this.mExtra = other.mExtra;
	}
}
