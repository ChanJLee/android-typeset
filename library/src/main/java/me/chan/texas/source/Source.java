package me.chan.texas.source;

import androidx.annotation.AnyThread;
import androidx.annotation.Nullable;

import me.chan.texas.BuildConfig;
import me.chan.texas.misc.ResourceManager;
import me.chan.texas.renderer.LoadingStrategy;

/**
 * 设置texas数据源的读取来源
 *
 * @param <T> 返回的类型
 */
public abstract class Source<T> {

	private volatile boolean mClosed = false;

	public Source() {
		if (BuildConfig.DEBUG) {
			ResourceManager.hold(this, new ResourceManager.Listener<Source<T>>() {
				@Override
				public boolean isReleased(Source<T> o) {
					return o.isClosed();
				}
			});
		}
	}

	/**
	 * 打开开始读取内容
	 *
	 * @return 数据 返回空则代表已经没有根多数据
	 * @throws Throwable 异常的时候抛出
	 */
	@AnyThread
	@Nullable
	public synchronized T open(LoadingStrategy strategy) throws SourceOpenException {
		if (mClosed) {
			throw new SourceOpenException(-1, "source has been closed", null);
		}

		return onOpen(strategy);
	}

	protected abstract T onOpen(LoadingStrategy strategy) throws SourceOpenException;

	/**
	 * 读取完毕，关闭source
	 *
	 * @throws SourceCloseException
	 */
	@AnyThread
	public synchronized final void close() {
		if (mClosed) {
			return;
		}

		try {
			onClose();
		} catch (Throwable ignore) {
			/* noop */
		} finally {
			mClosed = true;
		}
	}

	protected abstract void onClose() throws SourceCloseException;

	public boolean isClosed() {
		return mClosed;
	}
}
