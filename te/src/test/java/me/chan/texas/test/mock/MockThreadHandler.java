package me.chan.texas.test.mock;

import me.chan.texas.renderer.ThreadHandler;

public abstract class MockThreadHandler implements ThreadHandler {
	@Override
	public void sendMessage(int what, Object value) {
		handleMessage(what, value);
	}

	@Override
	public void clear() {

	}
}
