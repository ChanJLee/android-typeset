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

	public void getBound(TextPaint textPaint, Rect bound) {
		if (mDirty) {
			updateTextPaint(textPaint);
			mWidth = Layout.getDesiredWidth(mText, mStart, mEnd, textPaint);
			textPaint.getTextBounds(String.valueOf(mText), mStart, mEnd, bound);
			mHeight = bound.height();
		}

		bound.set(0, 0, (int) Math.ceil(mWidth), (int) Math.ceil(mHeight));
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

	@Hidden
	public CharSequence getContentForDebug() {
		return mText.subSequence(mStart, mEnd);
	}

	@Override
	public String toString() {
		return "Box{" +
				"mText=" + getContentForDebug() +
				", mBoxStyle=" + mBoxStyle +
				", mDirty=" + mDirty +
				", mPenalty=" + mPenalty +
				", mWidth=" + mWidth +
				", mHeight=" + mHeight +
				", mStart=" + mStart +
				", mEnd=" + mEnd +
				'}';
	}
}