package me.chan.texas.text.layout;

import androidx.annotation.RestrictTo;

import me.chan.texas.measurer.Measurer;
import me.chan.texas.text.TextAttribute;

/**
 * 排版算法中基本元素的接口
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface Element {

	void measure(Measurer measurer, TextAttribute textAttribute);

	default void recycle() {}
}
