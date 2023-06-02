package com.shanbay.lib.texas.text.layout;

import androidx.annotation.RestrictTo;

import com.shanbay.lib.texas.measurer.Measurer;
import com.shanbay.lib.texas.misc.DefaultRecyclable;
import com.shanbay.lib.texas.text.TextAttribute;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

/**
 * 排版算法中基本元素的接口
 */
@RestrictTo(LIBRARY)
public abstract class Element extends DefaultRecyclable {
	public abstract void measure(Measurer measurer, TextAttribute textAttribute);
}
