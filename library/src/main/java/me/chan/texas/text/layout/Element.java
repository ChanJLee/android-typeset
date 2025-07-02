package me.chan.texas.text.layout;

import androidx.annotation.RestrictTo;

import me.chan.texas.measurer.Measurer;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.text.TextAttribute;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

/**
 * 排版算法中基本元素的接口
 */
@RestrictTo(LIBRARY)
public abstract class Element extends DefaultRecyclable {
	public abstract void measure(Measurer measurer, TextAttribute textAttribute);
}
