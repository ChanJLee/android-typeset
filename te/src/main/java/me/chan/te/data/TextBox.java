package me.chan.te.data;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;

import me.chan.te.annotations.Hidden;
import me.chan.te.misc.ObjectFactory;
import me.chan.te.text.TextStyle;

/**
 * 文本元素
 */
public final class TextBox extends Box implements Element, Cloneable {
	private final static ObjectFactory<TextBox> POOL = new ObjectFactory<>(51200);

	private CharSequence mText;
	@Nullable
	private TextStyle mTextStyle;
	private int mStart;
	private int mEnd;

	protected TextBox(@NonNull CharSequence text, int start, int end, float width, float height, @Nullable TextStyle textStyle, Object extra) {
		super(width, height, extra);
		reset(text, start, end, width, height, textStyle, extra);
	}

	@Override
	protected void onCopy(@NonNull Box box) {
		if (!(box instanceof TextBox)) {
			return;
		}

		TextBox other = (TextBox) box;
		mText = other.mText;
		mStart = other.mStart;
		mEnd = other.mEnd;
		mTextStyle = other.mTextStyle;
	}

	@Override
	public void recycle() {
		reset(null, -1, -1, -1, -1, null, null);
		POOL.release(this);
	}

	@Override
	public Object clone() {
		TextBox copy = TextBox.obtain(mText, mStart, mEnd, mWidth, mHeight, mTextStyle, mExtra);
		copy.copy(this);
		return copy;
	}

	@Nullable
	@Hidden
	public TextStyle getTextStyle() {
		return mTextStyle;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		TextBox textBox = (TextBox) o;
		return mStart == textBox.mStart &&
				mEnd == textBox.mEnd &&
				mText.equals(textBox.mText) &&
				mTextStyle == textBox.mTextStyle;
	}

	private void reset(CharSequence text, int start, int end, float width, float height, @Nullable TextStyle textStyle, Object extra) {
		clearFlag();
		mText = text;
		mTextStyle = textStyle;
		mWidth = width;
		mHeight = height;
		mStart = start;
		mEnd = end;
		mExtra = extra;
	}

	@Hidden
	public void append(Box box) {
		TextBox other = (TextBox) box;
		mWidth += other.mWidth;
		mHeight = Math.max(mHeight, other.mHeight);
		if (mEnd == other.mStart) {
			mEnd = other.mEnd;
			return;
		}

		mText = String.valueOf(mText.subSequence(mStart, mEnd)) + other.mText.subSequence(other.mStart, other.mEnd);
		mStart = 0;
		mEnd = mText.length();
	}

	@Hidden
	public void append(Penalty penalty) {
		if (isPenalty()) {
			throw new IllegalStateException("set text box penalty twice");
		}

		setFlag(FLAG_PENALTY);
		mWidth += penalty.getWidth();
		mHeight = Math.max(mHeight, penalty.getHeight());

		mText = mText.subSequence(mStart, mEnd) + "-";
		mStart = 0;
		mEnd = mText.length();
	}

	private void updateTextPaint(TextPaint textPaint) {
		if (mTextStyle != null) {
			mTextStyle.update(textPaint);
		}
	}

	@Hidden
	public boolean canMerge(Box box) {
		if (!(box instanceof TextBox)) {
			return false;
		}

		TextBox other = (TextBox) box;
		if (mExtra != other.mExtra) {
			return false;
		}

		if (other.mTextStyle != null && mTextStyle != null) {
			return !mTextStyle.isConflict(other.mTextStyle);
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

	@Override
	public boolean canSpilt() {
		return true;
	}

	@Nullable
	public TextBox spilt(float limitWidth) {
		if (limitWidth <= 0) {
			return null;
		}

		float width = getWidth();
		if (limitWidth > width) {
			return null;
		}

		float ratio = limitWidth / width;
		int last = (int) Math.floor((ratio * (mEnd - mStart))) + mStart;
		if (last <= mStart || last >= mEnd) {
			return null;
		}

		TextBox suffix = TextBox.obtain(mText, last, mEnd, (1 - ratio) * mWidth, mHeight, mTextStyle, mExtra);
		mEnd = last;
		mWidth = limitWidth;
		setFlag(FLAG_SPILT);
		return suffix;
	}

	public static void clean() {
		POOL.clean();
	}

	public static TextBox obtain(@NonNull CharSequence charSequence, int start, int end,
								 float width, float height, @Nullable TextStyle textStyle) {
		return obtain(charSequence, start, end, width, height, textStyle, null);
	}

	public static TextBox obtain(@NonNull CharSequence charSequence, int start, int end,
								 float width, float height, @Nullable TextStyle textStyle, Object extra) {
		TextBox box = POOL.acquire();
		if (box == null) {
			return new TextBox(charSequence, start, end, width, height, textStyle, extra);
		}
		box.reset(charSequence, start, end, width, height, textStyle, extra);
		return box;
	}
}
