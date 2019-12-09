package com.shanbay.lib.texas.renderer;

public abstract class MockThreadHandler implements ThreadHandler {
	@Override
	public void sendMessage(int what, Object value) {
		handleMessage(what, value);
	}

	@Override
	public void clear() {

	}
}
