package me.chan.te.data;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;

import me.chan.te.annotations.Hidden;

public final class Box implements Element, Cloneable {

	private static final int FLAG_NONE = 0;
	public static final int FLAG_SPILT = 1;
	public static final int FLAG_PENALTY = 2;

	@NonNull
	private CharSequence mText;
	private BoxStyle mBoxStyle;
	private int mFlag;
	private float mWidth = -1;
	private float mHeight = -1;
	private int mStart;
	private int mEnd;
	private Measurer mMeasurer;

	/**
	 * @param other other box
	 */
	public void copy(@NonNull Box other) {
		mText = other.mText;
		mBoxStyle = other.mBoxStyle;
		mFlag = other.mFlag;
		mWidth = other.mWidth;
		mHeight = other.mHeight;
		mStart = other.mStart;
		mEnd = other.mEnd;
		mMeasurer = other.mMeasurer;
	}

	@Override
	public Object clone() {
		Box copy = new Box(mMeasurer);
		copy.copy(this);
		return copy;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Box box = (Box) o;
		return mFlag == box.mFlag &&
				Float.compare(box.mWidth, mWidth) == 0 &&
				Float.compare(box.mHeight, mHeight) == 0 &&
				mStart == box.mStart &&
				mEnd == box.mEnd &&
				mText.equals(box.mText) &&
				mBoxStyle == box.mBoxStyle &&
				mMeasurer == box.mMeasurer;
	}

	Box(Measurer measurer) {
		mMeasurer = measurer;
	}

	void reset(@NonNull CharSequence text, int start, int end, @Nullable BoxStyle boxStyle) {
		mText = text;
		mBoxStyle = boxStyle;
		mFlag = FLAG_NONE;
		mWidth = mHeight = -1;
		mStart = start;
		mEnd = end;
	}

	@Hidden
	public void append(Box other) {
		append(other.mText.subSequence(other.mStart, other.mEnd));
	}

	@Hidden
	public void append(CharSequence s) {
		// mark as dirty
		mWidth = mHeight = -1;
		mText = mText.subSequence(mStart, mEnd) + String.valueOf(s);
		mStart = 0;
		mEnd = mText.length();
	}

	public float getWidth(TextPaint textPaint) {
		if (mWidth <= 0) {
			updateTextPaint(textPaint);
			mWidth = mMeasurer.getDesiredWidth(mText, mStart, mEnd, textPaint);
		}

		return mWidth;
	}

	public float getHeight(TextPaint textPaint) {
		if (mHeight <= 0 && mMeasurer != null) {
			mHeight = mMeasurer.getDesiredHeight(mText, mStart, mEnd, textPaint);
		}

		return mHeight;
	}

	private void updateTextPaint(TextPaint textPaint) {
		if (mBoxStyle != null) {
			mBoxStyle.update(textPaint);
		}
	}

	@Hidden
	public boolean canMerge(Box other) {
		return true;
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

	public void draw(Canvas canvas, TextPaint paint, float x, float y) {
		updateTextPaint(paint);
		canvas.drawText(String.valueOf(mText), mStart, mEnd, x, y, paint);
	}

	@Override
	public String toString() {
		return String.valueOf(mText.subSequence(mStart, mEnd));
	}

	@Nullable
	public Box[] spilt(TextPaint textPaint, float limitWidth) {
		if (limitWidth <= 0) {
			return null;
		}

		float width = getWidth(textPaint);
		if (limitWidth > width) {
			return null;
		}

		int last = (int) ((limitWidth / width) * (mEnd - mStart)) + mStart;
		while (last > mStart && last < mEnd &&
				mMeasurer.getDesiredWidth(mText, mStart, last, textPaint) > limitWidth) {
			--last;
		}

		if (last <= mStart || last >= mEnd) {
			return null;
		}

		Box prefix = new Box(mMeasurer);
		prefix.copy(this);

		Box suffix = new Box(mMeasurer);
		suffix.copy(this);

		prefix.mWidth = prefix.mHeight = -1;
		suffix.mWidth = suffix.mHeight = -1;

		prefix.mEnd = last;
		suffix.mStart = last;

		Box[] boxes = new Box[2];
		boxes[0] = prefix;
		boxes[1] = suffix;
		return boxes;
	}

	public interface Measurer {
		float getDesiredWidth(CharSequence charSequence, int start, int end, TextPaint textPaint);

		float getDesiredHeight(CharSequence charSequence, int start, int end, TextPaint textPaint);
	}
}