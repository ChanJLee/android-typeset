package me.chan.texas.text;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Line;

public class TypesetContext {
	/**
	 * {@link Paragraph.SpanBuilder#tag(Object)}
	 */
	private Box mBox;

	/**
	 * {@link Paragraph.SpanBuilder#tag(Object)}
	 */
	@Nullable
	private Box mPrevBox;

	/**
	 * {@link Paragraph.SpanBuilder#tag(Object)}
	 */
	@Nullable
	private Box mNextBox;

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
		return mPrevBox == null ? null : mPrevBox.getTag();
	}

	/**
	 * @return 获得后一个绘制元素的tag
	 */
	@Nullable
	public Object getNextTag() {
		return mNextBox == null ? null : mNextBox.getTag();
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void reset(Box prev, Box current, Box next) {
		mBox = current;
		mPrevBox = prev;
		mNextBox = next;
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

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int LOCATION_PARAGRAPH_START = 8;

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int LOCATION_PARAGRAPH_END = 16;

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int LOCATION_PARAGRAPH_MIDDLE = 32;

	public void clear() {
		mBox = mNextBox = mPrevBox = null;
		mParagraphLocationAttribute = 0;
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
			return mPrevBox == null;
		} else if (location == LOCATION_LINE_END) {
			return mNextBox == null;
		} else if (location == LOCATION_LINE_MIDDLE) {
			return mPrevBox != null && mNextBox != null;
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
