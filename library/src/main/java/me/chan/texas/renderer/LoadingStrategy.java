package me.chan.texas.renderer;

/**
 * 策略
 */
public enum LoadingStrategy {
	/**
	 * 加载之前
	 */
	LOAD_PREVIOUS,
	/**
	 * 加载更多
	 */
	LOAD_MORE,
	/**
	 * 重新加载数据
	 */
	INIT,
	/**
	 * 只重新排版，不重新加载数据
	 */
	TYPESET_ONLY
}