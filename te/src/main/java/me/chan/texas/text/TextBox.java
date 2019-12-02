package me.chan.texas.text;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;

import me.chan.texas.annotations.Hidden;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectFactory;

/**
 * 文本元素
 */
public final class TextBox extends Box {
	private static final short FLAG_NONE = 0;
	private static final short FLAG_PENALTY = 1;
	private final static ObjectFactory<TextBox> POOL = new ObjectFactory<>(51200);

	private CharSequence mText;
	private int mStart;
	private int mEnd;
	@Hidden
	private short mFlag = FLAG_NONE;
	private Attribute mAttribute;


	private TextBox(@NonNull CharSequence text, int start, int end,
					float width, float height,
					OnClickedListener onClickedListener, Attribute attribute) {
		super(width, height);
		reset(this, text, start, end,
				mWidth, mHeight,
				onClickedListener, attribute);
	}

	public void copy(@NonNull TextBox other) {
		if (other.isRecycled() || isRecycled()) {
			throw new IllegalStateException("other is recycled or current is recycled");
		}

		mWidth = other.mWidth;
		mHeight = other.mHeight;
		mSelected = other.mSelected;
		mOnClickedListener = other.mOnClickedListener;

		mText = other.mText;
		mAttribute = other.mAttribute;
		mStart = other.mStart;
		mEnd = other.mEnd;
		mFlag = other.mFlag;
	}

	public Background getBackground() {
		if (mAttribute == null) {
			return null;
		}
		return mAttribute.mBackground;
	}

	public Foreground getForeground() {
		if (mAttribute == null) {
			return null;
		}
		return mAttribute.mForeground;
	}

	public TextStyle getTextStyle() {
		if (mAttribute == null) {
			return null;
		}
		return mAttribute.mTextStyle;
	}

	public OnClickedListener getSpanOnClickedListener() {
		if (mAttribute == null) {
			return null;
		}
		return mAttribute.mSpanOnClickedListener;
	}

	@Override
	public void recycle() {
		if (isRecycled()) {
			return;
		}

		super.recycle();
		if (mAttribute != null) {
			mAttribute.recycle();
		}
		reset(this, null, -1, -1,
				-1, -1,
				null, null
		);
		POOL.release(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		TextBox textBox = (TextBox) o;
		return mText.equals(textBox.mText) &&
				mAttribute == textBox.mAttribute &&
				mStart == textBox.mStart &&
				mEnd == textBox.mEnd &&
				mFlag == textBox.mFlag;
	}

	@Hidden
	public void append(TextBox other) {
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

		if (penalty.getWidth() == 0) {
			return;
		}

		setFlag(FLAG_PENALTY);
		mWidth += penalty.getWidth();
		mHeight = Math.max(mHeight, penalty.getHeight());

		mText = mText.subSequence(mStart, mEnd) + "-";
		mStart = 0;
		mEnd = mText.length();
	}

	public boolean isPenalty() {
		return mFlag == FLAG_PENALTY;
	}

	public void draw(Canvas canvas, TextPaint paint, float x, float y) {
		canvas.drawText(mText, mStart, mEnd, x, y, paint);
	}

	@Override
	public String toString() {
		return String.valueOf(mText.subSequence(mStart, mEnd));
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

		TextBox suffix = TextBox.obtain(mText, last, mEnd,
				(1 - ratio) * mWidth, mHeight,
				mOnClickedListener, mAttribute
		);
		suffix.mFlag = mFlag;
		suffix.mSelected = mSelected;
		mFlag = FLAG_NONE;
		mEnd = last;
		mWidth = limitWidth;
		return suffix;
	}

	private void setFlag(short flag) {
		mFlag = flag;
	}

	public static void clean() {
		POOL.clean();
	}

	public static TextBox obtain(@NonNull CharSequence charSequence, int start, int end, float width, float height,
								 OnClickedListener onClickedListener,
								 Attribute attribute) {
		TextBox box = POOL.acquire();
		if (box == null) {
			return new TextBox(charSequence, start, end, width, height, onClickedListener, attribute);
		}
		reset(box, charSequence, start, end, width, height, onClickedListener, attribute);
		box.reuse();
		return box;
	}

	private static void reset(TextBox textBox, @NonNull CharSequence charSequence, int start, int end,
							  float width, float height,
							  OnClickedListener onClickedListener,
							  Attribute attribute) {
		textBox.mFlag = FLAG_NONE;
		textBox.mText = charSequence;
		textBox.mStart = start;
		textBox.mEnd = end;
		textBox.mWidth = width;
		textBox.mHeight = height;
		textBox.mOnClickedListener = onClickedListener;
		textBox.mAttribute = attribute;
	}

	public static class Attribute extends DefaultRecyclable {
		private final static ObjectFactory<Attribute> POOL = new ObjectFactory<>(128);

		private TextStyle mTextStyle;
		private Background mBackground;
		private Foreground mForeground;
		private OnClickedListener mSpanOnClickedListener;

		private Attribute() {
		}

		public void setTextStyle(TextStyle textStyle) {
			mTextStyle = textStyle;
		}

		public void setBackground(Background background) {
			mBackground = background;
		}

		public void setForeground(Foreground foreground) {
			mForeground = foreground;
		}

		public void setSpanOnClickedListener(OnClickedListener spanOnClickedListener) {
			mSpanOnClickedListener = spanOnClickedListener;
		}

		@Override
		public void recycle() {
			if (isRecycled()) {
				return;
			}

			super.recycle();
			mTextStyle = null;
			if (mBackground != null) {
				mBackground.recycle();
				mBackground = null;
			}
			if (mForeground != null) {
				mForeground.recycle();
				mForeground = null;
			}
			mSpanOnClickedListener = null;
			POOL.release(this);
		}

		public static Attribute obtain() {
			Attribute attribute = POOL.acquire();
			if (attribute == null) {
				return new Attribute();
			}
			attribute.reuse();
			return attribute;
		}

		public static void clean() {
			POOL.clean();
		}
	}
}
