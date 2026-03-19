package me.chan.texas.renderer;

import androidx.annotation.NonNull;

import me.chan.texas.text.layout.Box;

/**
 * Span谓词
 */
public interface SpanPredicate {
	/**
	 * @param thiz  当前的box
	 * @param other 其它的box
	 * @return true 表示其它元素被选中
	 */
	boolean accept(@NonNull Box thiz, @NonNull Box other);
}