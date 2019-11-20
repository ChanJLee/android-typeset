package me.chan.te.text;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;

import me.chan.te.annotations.Hidden;
import me.chan.te.misc.DefaultRecyclable;
import me.chan.te.misc.ObjectFactory;

/**
 * 文本元素
 */
public final class TextBox extends Box {
	private static final int FLAG_NONE = 0;
	private static final int FLAG_PENALTY = 2;
	private static final int FLAG_SPILT = 1;
	private final static ObjectFactory<TextBox> POOL = new ObjectFactory<>(51200);

	private CharSequence mText;
	private int mStart;
	private int mEnd;
	private int mFlag = FLAG_NONE;
	private boolean mSelected = false;
	private RichTextAttribute mRichTextAttribute;


	private TextBox(@NonNull CharSequence text, int start, int end,
					float width, float height,
					OnClickedListener onClickedListener, RichTextAttribute richTextAttribute) {
		super(width, height);
		reset(this, text, start, end,
				mWidth, mHeight,
				onClickedListener, richTextAttribute);
	}

	public void copy(@NonNull TextBox other) {
		if (other.isRecycled() || isRecycled()) {
			throw new IllegalStateException("other is recycled or current is recycled");
		}

		mWidth = other.mWidth;
		mHeight = other.mHeight;
		mText = other.mText;
		mRichTextAttribute = other.mRichTextAttribute;
		mStart = other.mStart;
		mEnd = other.mEnd;
		mFlag = other.mFlag;
		mSelected = other.mSelected;
		mOnClickedListener = other.mOnClickedListener;
	}

	public boolean isSelected() {
		return mSelected;
	}

	public void setSelected(boolean selected) {
		mSelected = selected;
	}

	@Override
	public void recycle() {
		if (isRecycled()) {
			return;
		}

		super.recycle();
		if (mRichTextAttribute != null) {
			mRichTextAttribute.recycle();
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
				mRichTextAttribute == textBox.mRichTextAttribute &&
				mStart == textBox.mStart &&
				mEnd == textBox.mEnd &&
				mOnClickedListener == textBox.mOnClickedListener &&
				mFlag == textBox.mFlag &&
				mSelected == textBox.mSelected;
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

	public boolean isSplit() {
		return mFlag == FLAG_SPILT;
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
				mOnClickedListener, mRichTextAttribute
		);
		mEnd = last;
		mWidth = limitWidth;
		setFlag(FLAG_SPILT);
		return suffix;
	}

	private void setFlag(int flag) {
		mFlag = flag;
	}

	public static void clean() {
		POOL.clean();
	}

	public static TextBox obtain(@NonNull CharSequence charSequence, int start, int end, float width, float height,
								 OnClickedListener onClickedListener,
								 RichTextAttribute richTextAttribute) {
		TextBox box = POOL.acquire();
		if (box == null) {
			return new TextBox(charSequence, start, end, width, height, onClickedListener, richTextAttribute);
		}
		reset(box, charSequence, start, end, width, height, onClickedListener, richTextAttribute);
		box.reuse();
		return box;
	}

	private static void reset(TextBox textBox, @NonNull CharSequence charSequence, int start, int end,
							  float width, float height,
							  OnClickedListener onClickedListener,
							  RichTextAttribute richTextAttribute) {
		textBox.mFlag = FLAG_NONE;
		textBox.mSelected = false;
		textBox.mText = charSequence;
		textBox.mStart = start;
		textBox.mEnd = end;
		textBox.mWidth = width;
		textBox.mHeight = height;
		textBox.mOnClickedListener = onClickedListener;
		textBox.mRichTextAttribute = richTextAttribute;
	}

	public static class RichTextAttribute extends DefaultRecyclable {
		private final static ObjectFactory<RichTextAttribute> POOL = new ObjectFactory<>(128);

		private TextStyle mTextStyle;
		private Background mBackground;
		private Foreground mForeground;
		private Object mExtra;

		private RichTextAttribute() {
		}

		public TextStyle getTextStyle() {
			return mTextStyle;
		}

		public void setTextStyle(TextStyle textStyle) {
			mTextStyle = textStyle;
		}

		public Background getBackground() {
			return mBackground;
		}

		public void setBackground(Background background) {
			mBackground = background;
		}

		public Foreground getForeground() {
			return mForeground;
		}

		public void setForeground(Foreground foreground) {
			mForeground = foreground;
		}

		public Object getExtra() {
			return mExtra;
		}

		public void setExtra(Object extra) {
			mExtra = extra;
		}

		@Override
		public void recycle() {
			if (isRecycled()) {
				return;
			}

			mTextStyle = null;
			if (mBackground != null) {
				mBackground.recycle();
				mBackground = null;
			}
			if (mForeground != null) {
				mForeground.recycle();
				mForeground = null;
			}
			mExtra = null;
			super.recycle();
		}

		public static RichTextAttribute obtain() {
			RichTextAttribute richTextAttribute = POOL.acquire();
			if (richTextAttribute == null) {
				return new RichTextAttribute();
			}
			richTextAttribute.reuse();
			return richTextAttribute;
		}

		public static void clean() {
			POOL.clean();
		}
	}
}
