package me.chan.te.data;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;

import me.chan.te.annotations.Hidden;

public final class TextBox extends Box implements Element, Cloneable {

	@NonNull
	private CharSequence mText;
	private BoxStyle mBoxStyle;
	private float mWidth = -1;
	private float mHeight = -1;
	private int mStart;
	private int mEnd;
	private Measurer mMeasurer;

	/**
	 * @param box other box
	 */
	public void copy(@NonNull Box box) {
		super.copy(box);
		TextBox other = (TextBox) box;
		mText = other.mText;
		mBoxStyle = other.mBoxStyle;
		mWidth = other.mWidth;
		mHeight = other.mHeight;
		mStart = other.mStart;
		mEnd = other.mEnd;
		mMeasurer = other.mMeasurer;
	}

	@Override
	public boolean cloneable() {
		return true;
	}

	@Override
	public Object clone() {
		TextBox copy = new TextBox(mMeasurer);
		copy.copy(this);
		return copy;
	}

	@Override
	public boolean equals(Object o) {
		if (!super.equals(o)) {
			return false;
		}

		TextBox box = (TextBox) o;
		return Float.compare(box.mWidth, mWidth) == 0 &&
				Float.compare(box.mHeight, mHeight) == 0 &&
				mStart == box.mStart &&
				mEnd == box.mEnd &&
				mText.equals(box.mText) &&
				mBoxStyle == box.mBoxStyle &&
				mMeasurer == box.mMeasurer;
	}

	TextBox(Measurer measurer) {
		mMeasurer = measurer;
	}

	void reset(@NonNull CharSequence text, int start, int end, @Nullable BoxStyle boxStyle) {
		mText = text;
		mBoxStyle = boxStyle;
		clearFlag();
		mWidth = mHeight = -1;
		mStart = start;
		mEnd = end;
	}

	@Hidden
	public void append(Box box) {
		TextBox other = (TextBox) box;
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
	public boolean canMerge(Box box) {
		if (!(box instanceof TextBox)) {
			return false;
		}

		TextBox other = (TextBox) box;
		if (other.mBoxStyle != null && mBoxStyle != null) {
			return !mBoxStyle.isConflict(other.mBoxStyle);
		}
		return true;
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
	public TextBox[] spilt(TextPaint textPaint, float limitWidth) {
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

		TextBox prefix = new TextBox(mMeasurer);
		prefix.copy(this);

		TextBox suffix = new TextBox(mMeasurer);
		suffix.copy(this);

		prefix.mWidth = prefix.mHeight = -1;
		suffix.mWidth = suffix.mHeight = -1;

		prefix.mEnd = last;
		suffix.mStart = last;

		TextBox[] boxes = new TextBox[2];
		boxes[0] = prefix;
		boxes[1] = suffix;
		return boxes;
	}
}