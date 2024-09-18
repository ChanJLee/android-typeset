package me.chan.texas.renderer;

import androidx.annotation.Nullable;

/**
 * 点击事件监听器
 * <p>
 * 当点击事件发起时，首先通过 {@link #isSpanClickable(Object)} 判断是否可以点击，如果可以点击，再判断点击是否为单击或者长按来获取谓词
 * {@link #applySpanClicked} 和 {@link #applySpanLongClicked}
 * <p>
 * 谓词接收两个参数，第一个参数为点击的tag，第二个参数为其它box的tag
 * <p>
 * 谓词返回true表示其它box被选中
 */
public interface SpanTouchEventHandler {

	/**
	 * @param tag 点击的tag {@link me.chan.texas.text.layout.Box#getTag()}
	 * @return true 表示可以点击
	 */
	boolean isSpanClickable(@Nullable Object tag);

	/**
	 * @param clickedTag 点击的tag {@link me.chan.texas.text.layout.Box#getTag()}
	 * @param otherTag   其它box的tag
	 * @return true 表示其它box被选中
	 */
	boolean applySpanClicked(@Nullable Object clickedTag, @Nullable Object otherTag);

	/**
	 * @param clickedTag 点击的tag {@link me.chan.texas.text.layout.Box#getTag()}
	 * @param otherTag   其它box的tag
	 * @return true 表示其它box被选中
	 */
	boolean applySpanLongClicked(@Nullable Object clickedTag, @Nullable Object otherTag);
}
