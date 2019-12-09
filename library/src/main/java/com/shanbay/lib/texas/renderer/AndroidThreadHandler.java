package com.shanbay.lib.texas.renderer;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.shanbay.lib.texas.annotations.Hidden;

/**
 * android 线程同步器
 */
@Hidden
abstract class AndroidThreadHandler implements ThreadHandler {
	private Handler mHandler;

	public AndroidThreadHandler() {
		mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				AndroidThreadHandler.this.handleMessage(msg.what, msg.obj);
				return true;
			}
		});
	}

	@Override
	public void sendMessage(int what, Object value) {
		Message message = Message.obtain();
		message.what = what;
		message.obj = value;
		mHandler.sendMessage(message);
	}

	@Override
	public abstract void handleMessage(int what, Object value);

	@Override
	public void clear() {
		mHandler.removeCallbacksAndMessages(null);
	}
}
