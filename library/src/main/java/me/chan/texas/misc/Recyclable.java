package me.chan.texas.misc;

/**
 * 可回收的资源标记
 */
public interface Recyclable {
	/**
	 * 回收
	 */
	void recycle();

	/**
	 * @return 是否被回收
	 */
	boolean isRecycled();

	/**
	 * 重用
	 */
	void reuse();
}
