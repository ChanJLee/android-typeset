package me.chan.texas.text;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.text.layout.Box;

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
	public static final int LOCATION_LINE_MIDDLE = 3;

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int LOCATION_PARAGRAPH_START = 4;

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int LOCATION_PARAGRAPH_END = 5;

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static final int LOCATION_PARAGRAPH_MIDDLE = 6;

	@IntDef({LOCATION_LINE_START, LOCATION_LINE_END, LOCATION_LINE_MIDDLE,
			LOCATION_PARAGRAPH_START, LOCATION_PARAGRAPH_END, LOCATION_PARAGRAPH_MIDDLE})
	public @interface LocationType {
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
		}

		throw new IllegalArgumentException("unknown location type: " + location);
	}
}
