package me.chan.te.data;

import android.graphics.Canvas;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;

import me.chan.te.annotations.Hidden;

public abstract class Box implements Cloneable, Element {
	private static final int FLAG_NONE = 0;
	static final int FLAG_PENALTY = 2;
	public static final int FLAG_SPILT = 1;

	// 检查 equals
	protected float mWidth;
	protected float mHeight;

	private int mFlag = FLAG_NONE;

	public Box(float width, float height) {
		mWidth = width;
		mHeight = height;
	}

	public float getWidth() {
		return mWidth;
	}

	public float getHeight() {
		return mHeight;
	}

	@CallSuper
	public void copy(@NonNull Box other) {
		this.mFlag = other.mFlag;
		this.mWidth = other.mWidth;
		this.mHeight = other.mHeight;
	}

	public abstract Object clone();

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Box box = (Box) o;
		return Float.compare(box.mWidth, mWidth) == 0 &&
				Float.compare(box.mHeight, mHeight) == 0 &&
				mFlag == box.mFlag;
	}

	public abstract void append(Box other);

	public abstract void append(Penalty penalty);

	public abstract boolean canMerge(Box other);

	protected void clearFlag() {
		setFlag(FLAG_NONE);
	}

	@Hidden
	public boolean isPenalty() {
		return mFlag == FLAG_PENALTY;
	}

	public boolean isSplit() {
		return mFlag == FLAG_SPILT;
	}

	public void setFlag(int flag) {
		mFlag = flag;
	}

	public abstract void draw(Canvas canvas, TextPaint paint, float x, float y);

	public abstract String toString();

	@Nullable
	public abstract Box[] spilt(float limitWidth);
}
