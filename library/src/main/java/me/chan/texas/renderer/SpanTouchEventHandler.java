package me.chan.texas.renderer;

import androidx.annotation.NonNull;

import me.chan.texas.text.layout.Span;

/**
 * 点击事件监听器
 * <p>
 * 当点击事件发起时，首先通过 {@link #isSpanClickable(Span)} 判断是否可以点击，如果可以点击
 * 则调用 {@link #acceptSpan(EventType, Span)} 判断是否接受点击事件，如果接受则调用，其中EventType表示点击类型
 * {@link #applySpanClicked(Span, Span)} 或者 {@link #applySpanLongClicked(Span, Span)} 处理点击事件
 * <p>
 * 谓词接收两个参数，第一个参数为点击的box，第二个参数为其它box的box
 * <p>
 * 谓词返回true表示其它box被选中
 */
public interface SpanTouchEventHandler {

	/**
	 * @param box 点击的box {@link Span#getTag()}
	 * @return true 表示可以点击
	 */
	boolean isSpanClickable(@NonNull Span box);

	/**
	 * @param type    点击类型
	 * @param clicked 点击的box {@link Span#getTag()}
	 * @return 是否表示接受当前点击事件，返回true则表示接受，则调用 {@link #applySpanClicked(Span, Span)} 或者 {@link #applySpanLongClicked(Span, Span)}
	 */
	default boolean acceptSpan(EventType type, @NonNull Span clicked) {
		return true;
	}

	/**
	 * @param clicked 点击的box {@link Span#getTag()} ()}
	 * @param other   其它box的box
	 * @return true 表示其它box被选中
	 */
	boolean applySpanClicked(@NonNull Span clicked, @NonNull Span other);

	/**
	 * @param clicked 点击的box {@link Span#getTag()}
	 * @param other   其它box的box
	 * @return true 表示其它box被选中
	 */
	boolean applySpanLongClicked(@NonNull Span clicked, @NonNull Span other);

	enum EventType {
		CLICK, LONG_CLICK
	}
}
