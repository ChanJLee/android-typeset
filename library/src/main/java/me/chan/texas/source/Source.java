package me.chan.texas.source;

import androidx.annotation.AnyThread;

import me.chan.texas.renderer.LoadingStrategy;

/**
 * 设置texas数据源的读取来源
 *
 * @param <T> 返回的类型
 */
public interface Source<T> {
	/**
	 * 打开开始读取内容
	 *
	 * @return 数据
	 * @throws SourceOpenException 异常的时候抛出
	 */
	@AnyThread
	T open(LoadingStrategy strategy) throws SourceOpenException;

	/**
	 * 读取完毕，关闭source
	 *
	 * @throws SourceCloseException
	 */
	@AnyThread
	void close() throws SourceCloseException;
}
