package me.chan.texas.text;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

public class DrawContext {
	/**
	 * {@link Paragraph.SpanBuilder#tag(Object)}
	 */
	@Nullable
	private Object mTag;

	/**
	 * {@link Paragraph.SpanBuilder#tag(Object)}
	 */
	@Nullable
	private Object mPrevTag;

	/**
	 * {@link Paragraph.SpanBuilder#tag(Object)}
	 */
	@Nullable
	private Object mNextTag;

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void reset() {
		mTag = mPrevTag = mNextTag = null;
	}

	/**
	 * @return 获得当前元素的tag
	 */
	@Nullable
	public Object getTag() {
		return mTag;
	}

	/**
	 * @return 获得前一个绘制元素的tag
	 */
	@Nullable
	public Object getPrevTag() {
		return mPrevTag;
	}

	/**
	 * @return 获得后一个绘制元素的tag
	 */
	@Nullable
	public Object getNextTag() {
		return mNextTag;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void setTag(@Nullable Object tag) {
		mTag = tag;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void setPrevTag(@Nullable Object prevTag) {
		mPrevTag = prevTag;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public void setNextTag(@Nullable Object nextTag) {
		mNextTag = nextTag;
	}
}
