package com.shanbay.lib.texas.text;

import android.graphics.Rect;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

/**
 * 渲染的最小单元
 */
public interface Segment {
	/**
	 * @return 获取当前segment的唯一标识
	 */
	@Nullable
	Object getTag();

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	void getRect(Rect rect);

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	@Nullable
	Rect getRect();

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	void setRect(Rect rect);

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	void recycle();

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	boolean isRecycled();
}
