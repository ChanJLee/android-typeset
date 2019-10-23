package me.chan.te.measurer;

/**
 * 文字测量器
 */
public interface Measurer {
	/**
	 * @param charSequence 文本
	 * @param start        文本开始下表
	 * @param end          文本结束下标
	 * @return 文本宽度
	 */
	float getDesiredWidth(CharSequence charSequence, int start, int end);

	/**
	 * @param charSequence 文本
	 * @param start        文本开始下表
	 * @param end          文本结束下标
	 * @return 文本高度
	 */
	float getDesiredHeight(CharSequence charSequence, int start, int end);

	/**
	 * @return 每行建议间隔
	 */
	float getFontSpacing();
}