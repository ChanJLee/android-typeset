package me.chan.te.data;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.TextPaint;

import me.chan.te.annotations.Hidden;

public final class Box implements Element {

	@NonNull
	private CharSequence mText;
	private BoxStyle mBoxStyle;
	private boolean mDirty = true;
	private boolean mPenalty = false;
	private float mWidth = 0;
	private float mHeight = 0;
	private int mStart;
	private int mEnd;

	/**
	 * @param other other box
	 */
	public void copy(@NonNull Box other) {
		mText = other.mText;
		mBoxStyle = other.mBoxStyle;
		mDirty = other.mDirty;
		mPenalty = other.mDirty;
		mWidth = other.mWidth;
		mHeight = other.mHeight;
		mStart = other.mStart;
		mEnd = other.mEnd;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Box box = (Box) o;
		return mDirty == box.mDirty &&
				mPenalty == box.mPenalty &&
				Float.compare(box.mWidth, mWidth) == 0 &&
				Float.compare(box.mHeight, mHeight) == 0 &&
				mStart == box.mStart &&
				mEnd == box.mEnd &&
				mText.equals(box.mText) &&
				mBoxStyle == ((Box) o).mBoxStyle;
	}

	Box() {
	}

	void reset(@NonNull CharSequence text, int start, int end, @Nullable BoxStyle boxStyle) {
		mText = text;
		mBoxStyle = boxStyle;
		mDirty = true;
		mPenalty = false;
		mWidth = mHeight = 0;
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
		mDirty = true;
		mText = mText.subSequence(mStart, mEnd) + String.valueOf(s);
		mStart = 0;
		mEnd = mText.length();
	}

	public void getBound(TextPaint textPaint, Bound bound) {
		bound.mWidth = getWidth(textPaint);
		bound.mHeight = getHeight(textPaint, bound);
	}

	private float getWidth(TextPaint textPaint) {
		if (mDirty) {
			updateTextPaint(textPaint);
			mWidth = Layout.getDesiredWidth(mText, mStart, mEnd, textPaint);
		}

		return mWidth;
	}

	private float getHeight(TextPaint textPaint, Bound bound) {
		if (mDirty) {
			updateTextPaint(textPaint);
			textPaint.getTextBounds(String.valueOf(mText), mStart, mEnd, bound.mBound);
			mHeight = bound.mBound.height();
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
		return mPenalty;
	}

	public void setPenalty(boolean penalty) {
		mPenalty = penalty;
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

		int last = (int) (Math.floor((width / limitWidth) * (mEnd - mStart)) + mStart);
		if (last <= mStart || last >= mEnd) {
			return null;
		}

		Box prefix = new Box();
		prefix.copy(this);

		Box suffix = new Box();
		suffix.copy(this);

		prefix.mEnd = last;
		suffix.mStart = last;

		Box[] boxes = new Box[2];
		boxes[0] = prefix;
		boxes[1] = suffix;
		return boxes;
	}

	public static class Bound {
		private Rect mBound = new Rect();
		private float mHeight;
		private float mWidth;

		public float getHeight() {
			return mHeight;
		}

		public float getWidth() {
			return mWidth;
		}
	}
}