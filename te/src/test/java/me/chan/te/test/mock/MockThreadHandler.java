package me.chan.te.test.mock;

import me.chan.te.renderer.ThreadHandler;

public abstract class MockThreadHandler implements ThreadHandler {
	@Override
	public void sendMessage(int what, Object value) {
		handleMessage(what, value);
	}

	@Override
	public void clear() {

	}
}
