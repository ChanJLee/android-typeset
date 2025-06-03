package me.chan.texas.renderer.core.sync;

import android.os.Handler;
import android.os.Looper;

import me.chan.texas.utils.concurrency.TaskQueue;

public class AndroidMsgHandler extends MsgHandler {
	private final Handler mHandler;

	public AndroidMsgHandler() {
		mHandler = new Handler(Looper.getMainLooper(), msg -> {
			int count = mListeners.size();
			for (int i = 0; i < count; ++i) {
				Listener listener = mListeners.get(i);
				Msg message = (Msg) msg.obj;
				if (listener.handle(message.getToken(), message)) {
					break;
				}
			}
			return true;
		});
	}

	@Override
	public void send(TaskQueue.Token token, Msg message) {
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
