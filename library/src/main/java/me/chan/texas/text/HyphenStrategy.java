package me.chan.texas.text;

/**
 * 断字策略
 */
public enum HyphenStrategy {
	/**
	 * 根据美英断
	 */
	US,
	/**
	 * 根据英音断
	 */
	UK,
	/**
	 * 没有断字策略
	 * */
	NONE,
}
