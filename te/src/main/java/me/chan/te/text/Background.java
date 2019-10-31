package me.chan.te.text;

/**
 * 背景
 */
public interface Background {

	/**
	 * 检查样式是否冲突
	 *
	 * @param other 另外一个background
	 * @return 是否和Other元素style冲突，如果不冲突，表示其对应的Box可以被合并
	 */
	boolean isConflict(Background other);
}
