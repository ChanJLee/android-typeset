package me.chan.texas.source;

import androidx.annotation.AnyThread;
import androidx.annotation.Nullable;

import me.chan.texas.TexasOption;

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
	 * @throws Throwable 异常的时候抛出
	 */
	@AnyThread
	@Nullable
	public final synchronized T open() throws SourceOpenException {
		return onOpen();
	}

	/**
	 * @return 数据
	 * @throws SourceOpenException 打开异常
	 */
	protected abstract T onOpen() throws SourceOpenException;

	/**
	 * 读取完毕，关闭source
	 *
	 * @throws SourceCloseException
	 */
	@AnyThread
	public synchronized final void close() {
		try {
			onClose();
		} catch (Throwable ignore) {
			/* noop */
		}
	}

	protected abstract void onClose() throws SourceCloseException;
}
