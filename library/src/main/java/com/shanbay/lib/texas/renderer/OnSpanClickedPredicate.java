package com.shanbay.lib.texas.renderer;

import androidx.annotation.Nullable;

/**
 * 点击谓词
 */
public interface OnSpanClickedPredicate {
	/**
	 * @param clickedTag 点击的tag {@link  com.shanbay.lib.texas.text.layout.Box#getTag()}
	 * @param tag        box tag {@link  com.shanbay.lib.texas.text.layout.Box#getTag()}
	 * @return true 表示选中
	 */
	boolean apply(@Nullable Object clickedTag, @Nullable Object tag);
}