package me.chan.te.data;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import me.chan.te.annotations.Hidden;

public class Box implements Element {

	@NonNull
	private String mText;
	private float mWidth;
	private float mHeight;
	private boolean mPenalty = false;

	public Box(@NonNull String text, float width, float height) {
		mText = text;
		mWidth = width;
		mHeight = height;
	}

	public void setWidth(float width) {
		mWidth = width;
	}

	@NonNull
	public String getText() {
		return mText;
	}

	@Hidden
	public void append(Box other) {
		mText = mText + other.mText;
	}

	@Hidden
	public void append(String s) {
		mText = mText + s;
	}

	@Override
	public float getWidth() {
		return mWidth;
	}

	public float getHeight() {
		return mHeight;
	}

	public void setHeight(float height) {
		mHeight = height;
	}

	@Hidden
	public boolean canMerge(Box other) {
		return true;
	}

	@Hidden
	public boolean isPenalty() {
		return mPenalty;
	}

	public void setPenalty(boolean penalty) {
		mPenalty = penalty;
	}

	@Override
	public String toString() {
		return "Box{" +
				"mText='" + mText + '\'' +
				", mWidth=" + mWidth +
				", mHeight=" + mHeight +
				", mPenalty=" + mPenalty +
				'}';
	}

	public void draw(Canvas canvas, Paint paint, float x, float y) {
		canvas.drawText(mText, x, y, paint);
	}
}