package com.shanbay.lib.texas.source;

/**
 * 可以缓存内容的source
 *
 * @param <T> 输入类型
 */
public class CacheSource<T> implements Source<T> {

	private final Source<T> mSource;
	private T mValue;
	private boolean mNeedClose = false;

	/**
	 * @param source 真正提供内容的source
	 */
	public CacheSource(Source<T> source) {
		mSource = source;
	}

	@Override
	public T open() throws SourceOpenException {
		if (mValue == null) {
			mValue = mSource.open();
			mNeedClose = true;
		}
		return mValue;
	}

	@Override
	public void close() throws SourceCloseException {
		if (!mNeedClose) {
			return;
		}

		// 保证只执行一次
		try {
			mSource.close();
		} finally {
			mNeedClose = false;
		}
	}

	/**
	 * 清除缓存
	 */
	public void cleanCache() {
		mValue = null;
		mNeedClose = false;
	}
}
