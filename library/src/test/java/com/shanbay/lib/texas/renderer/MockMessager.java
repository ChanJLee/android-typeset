package com.shanbay.lib.texas.renderer;

import com.shanbay.lib.texas.concurrency.Messager;

public class MockMessager extends Messager {

	private final Messager.HandleListener mMyListener;

	public MockMessager(HandleListener myListener) {
		mMyListener = myListener;
	}

	@Override
	public void send(int what, Object value) {
		mMyListener.handleMessage(what, value);
	}

	@Override
	public void clear() {

	}
}
