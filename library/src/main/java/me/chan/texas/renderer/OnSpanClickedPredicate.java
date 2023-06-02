package me.chan.texas.renderer;

import androidx.annotation.Nullable;

import me.chan.texas.text.layout.Box;

/**
 * 点击谓词
 */
public interface OnSpanClickedPredicate {
	/**
	 * @param clickedTag 点击的tag {@link  Box#getTag()}
	 * @param tag        box tag {@link  Box#getTag()}
	 * @return true 表示选中
	 */
	boolean apply(@Nullable Object clickedTag, @Nullable Object tag);
}