package me.chan.te.data;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.TextPaint;

import me.chan.te.annotations.ForUnitTest;
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

	public void getBound(TextPaint textPaint, Bound bound) {
		if (mDirty) {
			updateTextPaint(textPaint);
			mWidth = Layout.getDesiredWidth(mText, mStart, mEnd, textPaint);
			textPaint.getTextBounds(String.valueOf(mText), mStart, mEnd, bound.mBound);
			mHeight = bound.mBound.height();
		}

		bound.mWidth = mWidth;
		bound.mHeight = mHeight;
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

	/**
	 * 只是作为测试用，不要外部调用
	 *
	 * @return 当前box内容
	 */
	@Hidden
	@ForUnitTest
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