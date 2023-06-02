package me.chan.texas.source;

/**
 * 直接返回已经读好的对象
 */
public class ObjectSource<T> implements Source<T> {
	private T mObject;

	public ObjectSource(T object) {
		mObject = object;
	}

	@Override
	public T open() throws SourceOpenException {
		return mObject;
	}

	@Override
	public void close() throws SourceCloseException {
		/* do nothing */
	}

	public void setObject(T object) {
		mObject = object;
	}
}
