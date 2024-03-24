package me.chan.texas.text.layout;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import me.chan.texas.BuildConfig;
import me.chan.texas.Texas;
import me.chan.texas.annotations.Internal;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.text.Appearance;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.TextStyle;
import me.chan.texas.utils.CharArrayPool;

/**
 * 文本元素
 */
@RestrictTo(LIBRARY)
public final class TextBox extends Box {
	/**
	 * 什么属性都没有
	 */
	public static final int ATTRIBUTE_NONE = 0;
	/**
	 * 是否是被追加了 -
	 */
	public static final int ATTRIBUTE_PENALTY = 1;
	/**
	 * 是否需要缩放
	 */
	public static final int ATTRIBUTE_ZOOM_OUT = 2;
	/**
	 * 是否需要缩小左边的距离
	 */
	public static final int ATTRIBUTE_SQUISH_LEFT = 4;
	/**
	 * 是否需要缩小右边的距离
	 */
	public static final int ATTRIBUTE_SQUISH_RIGHT = 8;

	static final float ZOOM_OUT_FACTOR = 0.8333f;

	static final int SQUISH_FACTOR = 2;

	private final static ObjectPool<TextBox> POOL = new ObjectPool<>(Texas.getMemoryOption().getTextBufferSize());
	private final static CharArrayPool CHAR_ARRAY_POOL = new CharArrayPool();

	@VisibleForTesting
	static boolean hasBuffered() {
		return !POOL.isEmpty();
	}

	/**
	 * 修改内容关注
	 * {@link #copy(TextBox)}
	 * {@link #equals(Object)}
	 */
	private CharSequence mText;
	private int mStart;
	private int mEnd;
	private TextStyle mTextStyle;
	private float mTopPadding;
	private float mBottomPadding;
	private float mBaselineOffset;

	@Internal
	private int mAttribute = ATTRIBUTE_NONE;

	private int mGroupId = Hyphenation.NONE_GROUP_ID;

	private TextBox(float width, float height) {
		super(width, height);
	}

	private TextBox(@NonNull CharSequence text, int start, int end,
					float width, float height,
					TextStyle textStyle) {
		super(width, height);
		mText = text;
		mStart = start;
		mEnd = end;
		mTextStyle = textStyle;
	}

	public void copy(@NonNull TextBox other) {
		if (other.isRecycled() || isRecycled()) {
			throw new IllegalStateException("other is recycled or current is recycled");
		}

		mWidth = other.mWidth;
		mHeight = other.mHeight;
		mTag = other.mTag;
		mBackground = other.mBackground;
		mForeground = other.mForeground;

		mText = other.mText;
		mStart = other.mStart;
		mEnd = other.mEnd;
		mTextStyle = other.mTextStyle;

		// internal data
		mAttribute = other.mAttribute;

		mTopPadding = other.mTopPadding;
		mBottomPadding = other.mBottomPadding;
		mBaselineOffset = other.mBaselineOffset;

		mGroupId = other.mGroupId;
	}

	// todo fix unit test
	public boolean merge(@NonNull TextBox box) {
		if (this.mGroupId != box.mGroupId) {
			if (BuildConfig.DEBUG) {
				throw new IllegalStateException("can't merge text box with difference group id");
			}
			return false;
		}

		// 目前因为符号问题不能合并的case大概占比 1%不到
		// 但是能提高 30% 后续遍历的性能
		// TODO 优化下
		if (mAttribute != box.mAttribute) {
			return false;
		}

		this.mWidth += box.mWidth;
		this.mHeight = Math.max(this.mHeight, box.mHeight);
		this.mEnd = box.mEnd;
		this.mAttribute |= box.mAttribute;
		this.mTopPadding = Math.max(this.mTopPadding, box.mTopPadding);
		this.mBottomPadding = Math.max(this.mBottomPadding, box.mBottomPadding);
		this.mBaselineOffset = Math.max(this.mBaselineOffset, box.mBaselineOffset);
		return true;
	}

	public TextStyle getTextStyle() {
		return mTextStyle;
	}

	@Override
	public void recycle() {
		if (isRecycled()) {
			return;
		}

		super.recycle();
		mText = null;
		mStart = mEnd = 0;
		mTextStyle = null;
		mAttribute = ATTRIBUTE_NONE;
		mTopPadding = mBottomPadding = mBaselineOffset = 0;
		mGroupId = Hyphenation.NONE_GROUP_ID;
		POOL.release(this);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		TextBox textBox = (TextBox) o;

		if (mStart != textBox.mStart) return false;
		if (mEnd != textBox.mEnd) return false;
		if (Float.compare(textBox.mTopPadding, mTopPadding) != 0) return false;
		if (Float.compare(textBox.mBottomPadding, mBottomPadding) != 0) return false;
		if (Float.compare(textBox.mBaselineOffset, mBaselineOffset) != 0) return false;
		if (mAttribute != textBox.mAttribute) return false;
		if (mText != null ? !mText.equals(textBox.mText) : textBox.mText != null) return false;
		if (mGroupId != textBox.mGroupId) return false;
		return mTextStyle != null ? mTextStyle.equals(textBox.mTextStyle) : textBox.mTextStyle == null;
	}

	@Override
	public int hashCode() {
		int result = mText != null ? mText.hashCode() : 0;
		result = 31 * result + mStart;
		result = 31 * result + mEnd;
		result = 31 * result + (mTextStyle != null ? mTextStyle.hashCode() : 0);
		result = 31 * result + (mTopPadding != +0.0f ? Float.floatToIntBits(mTopPadding) : 0);
		result = 31 * result + (mBottomPadding != +0.0f ? Float.floatToIntBits(mBottomPadding) : 0);
		result = 31 * result + (mBaselineOffset != +0.0f ? Float.floatToIntBits(mBaselineOffset) : 0);
		result = 31 * result + mAttribute;
		result = 31 * result + mGroupId;
		return result;
	}

	public int getGroupId() {
		return mGroupId;
	}

	/**
	 * @param penalty 累加另外一个元素的文本值
	 */
	public void merge(Penalty penalty) {
		// check tag ?
		if (isPenalty()) {
			throw new IllegalStateException("set text box penalty twice");
		}

		if (penalty.getGroupId() != mGroupId) {
			if (BuildConfig.DEBUG) {
				throw new IllegalStateException("can't merge penalty with difference group id");
			}
		}

		if (penalty.getWidth() == 0) {
			return;
		}

		addAttribute(ATTRIBUTE_PENALTY);
		mWidth += penalty.getWidth();
		mHeight = Math.max(mHeight, penalty.getHeight());

		mText = mText.subSequence(mStart, mEnd) + "-";
		mStart = 0;
		mEnd = mText.length();
	}

	@Override
	public void draw(Canvas canvas, Paint paint, float x, float y, boolean isSelected) {
		if (mAttribute != ATTRIBUTE_NONE) {
			if (hasAttribute(ATTRIBUTE_ZOOM_OUT)) {
				paint.setTextSize(paint.getTextSize() * ZOOM_OUT_FACTOR);
			}

			if (hasAttribute(ATTRIBUTE_SQUISH_LEFT)) {
				x -= (mWidth - getWidth());
			}
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			int size = mEnd - mStart;
			char[] buf = CHAR_ARRAY_POOL.obtain(size);
			TextUtils.getChars(mText, mStart, mEnd, buf, 0);
			canvas.drawTextRun(buf, 0, size, 0, size, x, y, false, paint);
			CHAR_ARRAY_POOL.release(buf);
			return;
		}

		canvas.drawText(mText, mStart, mEnd, x, y, paint);
	}

	@Override
	public String toString() {
		return String.valueOf(mText.subSequence(mStart, mEnd));
	}

	public static void clean() {
		POOL.clean();
	}

	@RestrictTo(LIBRARY)
	public CharSequence getText() {
		return mText;
	}

	@VisibleForTesting
	public int getStart() {
		return mStart;
	}

	@RestrictTo(LIBRARY)
	public int getEnd() {
		return mEnd;
	}

	@VisibleForTesting
	void setStart(int start) {
		mStart = start;
	}

	float getTopPadding() {
		return mTopPadding;
	}

	float getBottomPadding() {
		return mBottomPadding;
	}

	public static TextBox obtain(@NonNull CharSequence charSequence, int start, int end,
								 Measurer measurer,
								 TextStyle textStyle,
								 Object tag,
								 Appearance background,
								 Appearance foreground) {
		return obtain(charSequence, start, end, measurer, textStyle, tag, background, foreground, Hyphenation.NONE_GROUP_ID);
	}

	public static TextBox obtain(@NonNull CharSequence charSequence, int start, int end,
								 Measurer measurer,
								 TextStyle textStyle,
								 Object tag,
								 Appearance background,
								 Appearance foreground, int groupId) {
		TextBox textBox = obtain(
				charSequence, start, end, 0, 0,
				textStyle, tag, background, foreground, groupId
		);
		textBox.measure(measurer, null);
		return textBox;
	}

	private static TextBox obtain(@NonNull CharSequence charSequence, int start, int end,
								  float width, float height,
								  TextStyle textStyle,
								  Object tag,
								  Appearance background,
								  Appearance foreground,
								  int groupId) {
		TextBox box = POOL.acquire();
		if (box == null) {
			box = new TextBox(charSequence, start, end, width, height, textStyle);
		}
		box.mWidth = width;
		box.mHeight = height;
		box.mTag = tag;
		box.mBackground = background;
		box.mForeground = foreground;

		box.mText = charSequence;
		box.mStart = start;
		box.mEnd = end;
		box.mTextStyle = textStyle;

		box.mGroupId = groupId;

		box.reuse();
		return box;
	}

	@Override
	public void measure(Measurer measurer, TextAttribute textAttribute) {
		Measurer.CharSequenceSpec spec = Measurer.CharSequenceSpec.obtain();
		measurer.measure(mText, mStart, mEnd, getTextStyle(), getTag(), spec);
		mWidth = spec.getWidth();
		mHeight = spec.getHeight();
		mBottomPadding = spec.getFontBottomPadding();
		mTopPadding = spec.getFontTopPadding();
		mBaselineOffset = spec.getBaselineOffset();
		spec.recycle();
	}

	public static TextBox obtain(TextBox raw) {
		TextBox box = POOL.acquire();
		if (box == null) {
			box = new TextBox(0, 0);
		}
		box.reuse();
		box.copy(raw);
		return box;
	}

	public boolean isPenalty() {
		return hasAttribute(ATTRIBUTE_PENALTY);
	}

	public void clearAttribute(int flag) {
		mAttribute &= ~flag;
	}

	public int getAttribute() {
		return mAttribute;
	}

	public void addAttribute(int flag) {
		mAttribute |= flag;
	}

	public boolean hasAttribute(int flag) {
		return (mAttribute & flag) != 0;
	}

	@Override
	public float getWidth() {
		float width = super.getWidth();

		if (mAttribute != ATTRIBUTE_NONE) {
			if (hasAttribute(ATTRIBUTE_ZOOM_OUT)) {
				width *= ZOOM_OUT_FACTOR;
			}

			if (hasAttribute(ATTRIBUTE_SQUISH_LEFT) ||
					hasAttribute(ATTRIBUTE_SQUISH_RIGHT)) {
				width /= SQUISH_FACTOR;
			}
		}

		return width;
	}

	@Override
	public float getHeight() {
		float height = super.getHeight();

		if (mAttribute != ATTRIBUTE_NONE) {
			if (hasAttribute(ATTRIBUTE_ZOOM_OUT)) {
				height *= ZOOM_OUT_FACTOR;
			}
		}

		return height;
	}

	public float getBaselineOffset() {
		return mBaselineOffset;
	}
}
