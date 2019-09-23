package me.chan.te.data;

import android.text.TextPaint;

public interface BoxStyle {
	/**
	 * 更新text paint的样式
	 *
	 * @param textPaint text paint
	 */
	void update(TextPaint textPaint);

	/**
	 * 检查样式是否冲突
	 *
	 * @param other 另外一个style
	 * @return 是否和Other元素style冲突，如果不冲突，表示其对应的Box可以被合并
	 */
	boolean isCoflict(BoxStyle other);
}
