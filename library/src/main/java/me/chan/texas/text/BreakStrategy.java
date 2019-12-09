package me.chan.texas.text;

/**
 * 断字策略
 */
public enum BreakStrategy {
	/**
	 * 简单的分行，不够显示就换行
	 */
	SIMPLE,
	/**
	 * 尽可能占满，不够显示就尝试断字
	 */
	BALANCED
}