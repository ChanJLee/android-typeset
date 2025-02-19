package me.chan.texas.source;

import androidx.annotation.AnyThread;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.TexasOption;

/**
 * 设置texas数据源的读取来源
 *
 * @param <T> 返回的类型
 */
public abstract class Source<T> {

	private TexasOptionLoader mLoader;

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public final void setLoader(TexasOptionLoader loader) {
		mLoader = loader;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public TexasOptionLoader getLoader() {
		return mLoader;
	}

	/**
	 * 打开开始读取内容
	 *
	 * @return 数据 返回空则代表已经没有根多数据
	 */
	@AnyThread
	@Nullable
	public final synchronized T read(TexasOption option) {
		if (mLoader == null) {
			return null;
		}

		return onRead(option);
	}

	/**
	 * @return 数据
	 */
	protected abstract T onRead(TexasOption option);

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public interface TexasOptionLoader {
		TexasOption load();
	}
}
