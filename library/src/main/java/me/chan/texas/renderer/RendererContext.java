package me.chan.texas.renderer;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.text.layout.Span;
import me.chan.texas.text.layout.Line;

public final class RendererContext {

	private Span mSpan;
	private int mIndex;
	private Line mLine;

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
		mSpan = null;
		mLine = null;
		mIndex = 0;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	void setBoxLocationAttribute(Line line, Span span, int index) {
		mLine = line;
		mSpan = span;
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
			return mIndex == mLine.getSpanCount() - 1;
		} else if (location == LOCATION_LINE_MIDDLE) {
			return mIndex > 0 && mIndex < mLine.getSpanCount() - 1;
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

	/**
	 * @return 当前对应的box
	 */
	@NonNull
	public Span getSpan() {
		return mSpan;
	}

	/**
	 * @return 当前box在行中的下标
	 */
	public int getIndex() {
		return mIndex;
	}

	/**
	 * @return 当前box所在的行
	 */
	@NonNull
	public Line getLine() {
		return mLine;
	}

	/**
	 * @return 当前box对应的tag
	 */
	@Nullable
	public Object getTag() {
		return mSpan.getTag();
	}
}
