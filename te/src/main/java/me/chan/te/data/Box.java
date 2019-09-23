package me.chan.te.data;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
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

	public Box(@NonNull CharSequence text) {
		this(text, null);
	}

	public Box(@NonNull CharSequence text, @Nullable BoxStyle boxStyle) {
		mText = text;
		mBoxStyle = boxStyle;
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

	public void getBound(TextPaint textPaint, RectF bound) {
		if (mDirty) {
			updateTextPaint(textPaint);
			mWidth = Layout.getDesiredWidth(mText, textPaint);
			Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
			mHeight = fontMetrics.bottom - fontMetrics.top;
		}

		bound.set(0, 0, mWidth, mHeight);
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