package me.chan.texas.source;

public class ObjectSource implements Source {
	private Object mObject;

	public ObjectSource(Object object) {
		mObject = object;
	}

	@Override
	public Object open() throws SourceOpenException {
		return mObject;
	}

	@Override
	public void close() throws SourceCloseException {
		mObject = null;
	}
}
