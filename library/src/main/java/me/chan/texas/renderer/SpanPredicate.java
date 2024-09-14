package me.chan.texas.renderer;

import androidx.annotation.Nullable;

/**
 * Span谓词
 */
public interface SpanPredicate {
	/**
	 * @param thiz  当前box的tag
	 * @param other 其它box的tag
	 * @return true 表示其它box被选中
	 */
	boolean apply(@Nullable Object thiz, @Nullable Object other);
}