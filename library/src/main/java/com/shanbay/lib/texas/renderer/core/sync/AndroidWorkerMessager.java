package com.shanbay.lib.texas.renderer.core.sync;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

public class AndroidWorkerMessager extends WorkerMessager {
	private final Handler mHandler;

	public AndroidWorkerMessager() {
		mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
			@Override
			public boolean handleMessage(@NonNull Message msg) {
				int count = mListeners.size();
				for (int i = 0; i < count; ++i) {
					Listener listener = mListeners.get(i);
					if (listener.handleMessage(msg.what, (WorkerMessage) msg.obj)) {
						break;
					}
				}
				return true;
			}
		});
	}

	@Override
	public void send(int id, WorkerMessage message) {
		android.os.Message msg = android.os.Message.obtain();
		msg.what = id;
		msg.obj = message;
		mHandler.sendMessage(msg);
	}

	@Override
	public void clear(int id) {
		mHandler.removeMessages(id);
	}
}
