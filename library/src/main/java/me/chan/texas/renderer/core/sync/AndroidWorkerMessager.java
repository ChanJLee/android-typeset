package me.chan.texas.renderer.core.sync;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import me.chan.texas.utils.concurrency.TaskQueue;

public class AndroidWorkerMessager extends WorkerMessager {
	private final Handler mHandler;

	public AndroidWorkerMessager() {
		mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
			@Override
			public boolean handleMessage(@NonNull Message msg) {
				int count = mListeners.size();
				for (int i = 0; i < count; ++i) {
					Listener listener = mListeners.get(i);
					WorkerMessage message = (WorkerMessage) msg.obj;
					if (listener.handleMessage(message.getToken(), message)) {
						break;
					}
				}
				return true;
			}
		});
	}

	@Override
	public void send(TaskQueue.Token token, WorkerMessage message) {
		android.os.Message msg = android.os.Message.obtain();
		msg.what = token.getId();
		msg.obj = message;
		message.setToken(token);
		mHandler.sendMessage(msg);
	}

	@Override
	public void clear(TaskQueue.Token token) {
		mHandler.removeMessages(token.getId());
	}
}
