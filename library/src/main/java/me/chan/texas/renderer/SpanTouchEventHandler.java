package me.chan.texas.renderer;

import androidx.annotation.NonNull;

import me.chan.texas.text.layout.Box;

/**
 * 点击事件监听器
 * <p>
 * 当点击事件发起时，首先通过 {@link #isSpanClickable(Box)} 判断是否可以点击，如果可以点击
 * 则调用 {@link #acceptSpan(EventType, Box)} 判断是否接受点击事件，如果接受则调用，其中EventType表示点击类型
 * {@link #applySpanClicked(Box, Box)} 或者 {@link #applySpanLongClicked(Box, Box)} 处理点击事件
 * <p>
 * 谓词接收两个参数，第一个参数为点击的box，第二个参数为其它box的box
 * <p>
 * 谓词返回true表示其它box被选中
 */
public interface SpanTouchEventHandler {

	/**
	 * @param box 点击的box {@link me.chan.texas.text.layout.Box#getTag()}
	 * @return true 表示可以点击
	 */
	boolean isSpanClickable(@NonNull Box box);

	/**
	 * @param type    点击类型
	 * @param clicked 点击的box {@link me.chan.texas.text.layout.Box#getTag()}
	 * @return 是否表示接受当前点击事件，返回true则表示接受，则调用 {@link #applySpanClicked(Box, Box)} 或者 {@link #applySpanLongClicked(Box, Box)}
	 */
	default boolean acceptSpan(EventType type, @NonNull Box clicked) {
		return true;
	}

	/**
	 * @param clicked 点击的box {@link Box#getTag()} ()}
	 * @param other   其它box的box
	 * @return true 表示其它box被选中
	 */
	boolean applySpanClicked(@NonNull Box clicked, @NonNull Box other);

	/**
	 * @param clicked 点击的box {@link me.chan.texas.text.layout.Box#getTag()}
	 * @param other   其它box的box
	 * @return true 表示其它box被选中
	 */
	boolean applySpanLongClicked(@NonNull Box clicked, @NonNull Box other);

	enum EventType {
		CLICK, LONG_CLICK
	}
}
