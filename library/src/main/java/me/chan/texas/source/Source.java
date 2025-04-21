package me.chan.texas.source;

import androidx.annotation.AnyThread;
import androidx.annotation.Nullable;

/**
 * 设置texas数据源的读取来源
 *
 * @param <T> 返回的类型
 */
public abstract class Source<T> {

	/**
	 * 打开开始读取内容
	 *
	 * @return 数据 返回空则代表已经没有根多数据
	 */
	@AnyThread
	@Nullable
	public final synchronized T read() {
		return onRead();
	}

	/**
	 * @return 数据
	 */
	@Nullable
	protected abstract T onRead();
}
