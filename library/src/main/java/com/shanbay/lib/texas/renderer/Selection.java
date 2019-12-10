package com.shanbay.lib.texas.renderer;

public interface Selection {
	/**
	 * 获取选中内容高亮的top边界，坐标相对于整个屏幕
	 *
	 * @return top边界
	 */
	float getSelectedTopEdgeOnScreen();

	/**
	 * 获取选中内容高亮的bottom边界，坐标相对于整个屏幕
	 *
	 * @return bottom边界
	 */
	float getSelectedBottomEdgeOnScreen();

	/**
	 * @param tags 重新高亮
	 */
	void selectedByTags(Object... tags);
}
