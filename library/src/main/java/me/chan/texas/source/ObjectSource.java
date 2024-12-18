package me.chan.texas.source;

import me.chan.texas.renderer.LoadingStrategy;

/**
 * 直接返回已经读好的对象
 */
public class ObjectSource<T> extends Source<T> {
	private T mObject;

	public ObjectSource(T object) {
		mObject = object;
	}

	@Override
	protected T onOpen(LoadingStrategy strategy) throws SourceOpenException {
		if (strategy == LoadingStrategy.INIT) {
			return mObject;
		}

		return null;
	}

	@Override
	protected void onClose() throws SourceCloseException {
		/* do nothing */
	}

	public void setObject(T object) {
		mObject = object;
	}
}
