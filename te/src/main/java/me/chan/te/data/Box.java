package me.chan.te.data;

import android.graphics.Canvas;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;

import me.chan.te.annotations.Hidden;

public abstract class Box implements Cloneable, Element {
	private static final int FLAG_NONE = 0;
	public static final int FLAG_SPILT = 1;
	public static final int FLAG_PENALTY = 2;

	private int mFlag = FLAG_NONE;

	@CallSuper
	public void copy(@NonNull Box other) {
		this.mFlag = other.mFlag;
	}

	public abstract boolean cloneable();

	public abstract Object clone();

	@CallSuper
	public boolean equals(Object o) {
		if (!super.equals(o)) {
			return false;
		}

		Box other = (Box) o;
		return mFlag == other.mFlag;
	}

	@Hidden
	public abstract void append(Box other);

	@Hidden
	public abstract void append(CharSequence s);


	public abstract float getWidth(TextPaint textPaint);

	public abstract float getHeight(TextPaint textPaint);

	@Hidden
	public abstract boolean canMerge(Box other);

	protected void clearFlag() {
		mFlag = FLAG_NONE;
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
	public abstract Box[] spilt(TextPaint textPaint, float limitWidth);

	public interface Measurer {
		float getDesiredWidth(CharSequence charSequence, int start, int end, TextPaint textPaint);
	}
}
