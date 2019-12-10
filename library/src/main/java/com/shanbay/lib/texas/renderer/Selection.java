package com.shanbay.lib.texas.renderer;

public class Selection {
	/**
	 * 获取选中内容高亮的top边界，坐标相对于整个屏幕
	 *
	 * @return top边界
	 */
	public float getSelectedTopEdgeOnScreen() {
		return -1;
	}

	/**
	 * 获取选中内容高亮的bottom边界，坐标相对于整个屏幕
	 *
	 * @return bottom边界
	 */
	public float getSelectedBottomEdgeOnScreen() {
		return -1;
	}

	public void highlightIf() {

	}

	public interface Function {
		boolean action(Object object);
	}
}
