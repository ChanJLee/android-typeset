package me.chan.texas.renderer;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.misc.RectF;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.layout.TextBox;

public final class RendererContext {
	private static final Box NONVALUE = TextBox.obtain("x", 0, 1, null, null, null, null);

	private Box mBox;
	private int mIndex;
	private Line mLine;
	private Box mPrev;
	private Box mNext;

	/**
	 * @return 获得当前元素的tag
	 */
	@Nullable
	public Object getTag() {
		return mBox.getTag();
	}

	/**
	 * @return 获得前一个绘制元素的tag
	 */
	@Nullable
	public Object getPrevTag() {
		if (mIndex == 0) {
			return null;
		}

		if (mPrev == NONVALUE) {
			return null;
		}

		if (mPrev != null) {
			return mPrev.getTag();
		}

		Box prev = (Box) mLine.getElement(mIndex - 1);
		RectF prevBounds = prev.getInnerBounds();
		if (Float.compare(prevBounds.right, mBox.getInnerBounds().left) == 0) {
			mPrev = prev;
			return prev.getTag();
		}

		mPrev = NONVALUE;
		return null;
	}

	/**
	 * @return 获得后一个绘制元素的tag
	 */
	@Nullable
	public Object getNextTag() {
		if (mIndex == mLine.getCount() - 1) {
			return null;
		}

		if (mNext == NONVALUE) {
			return null;
		}

		if (mNext != null) {
			return mNext.getTag();
		}

		Box next = (Box) mLine.getElement(mIndex + 1);
		RectF nextBounds = next.getInnerBounds();
		if (Float.compare(nextBounds.left, mBox.getInnerBounds().right) == 0) {
			mNext = next;
			return next.getTag();
		}

		mNext = NONVALUE;
		return null;
	}

	/**
	 * 行首
	 */
	public static final int LOCATION_LINE_START = 1;

	/**
	 * 行尾
	 */
	public static final int LOCATION_LINE_END = 2;

	/**
	 * 行中
	 */
	public static final int LOCATION_LINE_MIDDLE = 4;

	/**
	 * 段首
	 */
	public static final int LOCATION_PARAGRAPH_START = 8;

	/**
	 * 段尾
	 */
	public static final int LOCATION_PARAGRAPH_END = 16;

	/**
	 * 段中
	 */
	public static final int LOCATION_PARAGRAPH_MIDDLE = 32;

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	void clear() {
		mParagraphLocationAttribute = 0;
		mBox = null;
		mLine = null;
		mIndex = 0;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	void setBoxLocationAttribute(Line line, Box box, int index) {
		mLine = line;
		mBox = box;
		mIndex = index;
	}

	@IntDef({LOCATION_LINE_START, LOCATION_LINE_END, LOCATION_LINE_MIDDLE,
			LOCATION_PARAGRAPH_START, LOCATION_PARAGRAPH_END, LOCATION_PARAGRAPH_MIDDLE})
	public @interface LocationType {
	}

	private int mParagraphLocationAttribute = 0;

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void setParagraphLocationAttribute(int location, boolean enable) {
		if (enable) {
			mParagraphLocationAttribute |= location;
		} else {
			mParagraphLocationAttribute &= ~location;
		}
	}

	/**
	 * @param location {@link #LOCATION_LINE_START} or {@link #LOCATION_LINE_END}
	 * @return 是否包含指定位置的属性
	 */
	public boolean checkLocation(@LocationType int location) {
		if (location == LOCATION_LINE_START) {
			return mIndex == 0;
		} else if (location == LOCATION_LINE_END) {
			return mIndex == mLine.getCount();
		} else if (location == LOCATION_LINE_MIDDLE) {
			return mIndex > 0 && mIndex < mLine.getCount();
		} else if (location == LOCATION_PARAGRAPH_START) {
			return (mParagraphLocationAttribute & LOCATION_PARAGRAPH_START) != 0;
		} else if (location == LOCATION_PARAGRAPH_END) {
			return (mParagraphLocationAttribute & LOCATION_PARAGRAPH_END) != 0;
		} else if (location == LOCATION_PARAGRAPH_MIDDLE) {
			return !checkLocation(LOCATION_PARAGRAPH_START) &&
					!checkLocation(LOCATION_PARAGRAPH_END);
		}

		throw new IllegalArgumentException("unknown location type: " + location);
	}
}
