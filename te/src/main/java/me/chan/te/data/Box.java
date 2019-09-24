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

	Box() {
	}

	void reset(@NonNull CharSequence text, @Nullable BoxStyle boxStyle) {
		mText = text;
		mBoxStyle = boxStyle;
		mDirty = true;
		mPenalty = false;
		mWidth = mHeight = 0;
	}

	@Hidden
	public void append(Box other) {
		append(other.mText);
	}

	@Hidden
	public void append(CharSequence s) {
		// mark as dirty
		mDirty = true;
		mText = String.valueOf(mText) + s;
	}

	public void getBound(TextPaint textPaint, Rect bound) {
		if (mDirty) {
			updateTextPaint(textPaint);
			mWidth = Layout.getDesiredWidth(mText, textPaint);
			textPaint.getTextBounds(String.valueOf(mText), 0, mText.length(), bound);
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
		canvas.drawText(String.valueOf(mText), x, y, paint);
	}
}