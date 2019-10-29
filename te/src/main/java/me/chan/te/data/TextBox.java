package me.chan.te.data;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;

import me.chan.te.annotations.Hidden;
import me.chan.te.misc.ObjectFactory;

/**
 * 文本元素
 */
public final class TextBox extends Box implements Element, Cloneable {
	private final static ObjectFactory<TextBox> POOL = new ObjectFactory<>(50000);
	private CharSequence mText;
	@Nullable
	private BoxStyle mBoxStyle;
	private int mStart;
	private int mEnd;
	private Object mExtra;

	protected TextBox(@NonNull CharSequence text, int start, int end, float width, float height, @Nullable BoxStyle boxStyle, Object extra) {
		super(width, height);
		reset(text, start, end, width, height, boxStyle, extra);
	}

	public Object getExtra() {
		return mExtra;
	}

	/**
	 * @param box other box
	 */
	public void copy(@NonNull Box box) {
		TextBox other = (TextBox) box;
		reset(other.mText, other.mStart, other.mEnd, other.mWidth, other.mHeight, other.mBoxStyle, other.mExtra);
		super.copy(box);
	}

	@Override
	public void recycle() {
		reset(null, -1, -1, -1, -1, null, null);
		POOL.release(this);
	}

	@Override
	public Object clone() {
		TextBox copy = new TextBox(mText, mStart, mEnd, mWidth, mHeight, mBoxStyle, mExtra);
		copy.copy(this);
		return copy;
	}

	@Nullable
	@Hidden
	public BoxStyle getBoxStyle() {
		return mBoxStyle;
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
				mBoxStyle == textBox.mBoxStyle;
	}

	private void reset(CharSequence text, int start, int end, float width, float height, @Nullable BoxStyle boxStyle, Object extra) {
		clearFlag();
		mText = text;
		mBoxStyle = boxStyle;
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
		if (mExtra != other.mExtra) {
			return false;
		}

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
	public TextBox[] spilt(float limitWidth) {
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

		TextBox[] boxes = new TextBox[2];
		boxes[0] = new TextBox(mText, mStart, last, limitWidth, mHeight, mBoxStyle, mExtra);
		boxes[1] = new TextBox(mText, last, mEnd, (1 - ratio) * mWidth, mHeight, mBoxStyle, mExtra);
		return boxes;
	}

	public static void clean() {
		POOL.clean();
	}

	public static TextBox obtain(@NonNull CharSequence charSequence, int start, int end,
								 float width, float height, @Nullable BoxStyle boxStyle, Object extra) {
		TextBox box = POOL.acquire();
		if (box == null) {
			return new TextBox(charSequence, start, end, width, height, boxStyle, extra);
		}
		box.reset(charSequence, start, end, width, height, boxStyle, extra);
		return box;
	}
}
