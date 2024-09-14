package me.chan.texas.utils.concurrency;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;

public class AndroidTaskQueue implements TaskQueue {
	private volatile H mHandler;

	private final String mName;

	public AndroidTaskQueue(String name) {
		mName = name;
	}

	private synchronized H getHandler(boolean create) {
		if (mHandler != null) {
			return mHandler;
		}

		if (!create) {
			return null;
		}

		HandlerThread thread = new HandlerThread(mName);
		thread.start();

		mHandler = new H(thread.getLooper());
		return mHandler;
	}

	@Override
	public <A, R> void submit(Token token, @NonNull A args, @NonNull Task<A, R> task, @NonNull Listener<A, R> listener) {
		Message message = Message.obtain();
		message.what = token.getId();
		message.obj = Args.obtain(token, args, task, listener);
		Handler handler = getHandler(true);
		handler.sendMessage(message);
	}

	@Override
	public <A, R> R submitSync(Token token, @NonNull A args, @NonNull Task<A, R> task) throws Throwable {
		return task.run(token, args);
	}

	@Override
	public synchronized void cancel(Token token) {
		Handler handler = getHandler(false);
		if (handler == null) {
			return;
		}
		handler.removeMessages(token.getId());
	}

	@Override
	public String toString() {
		return "AndroidMessageQueue{" +
				", mHandler=" + mHandler +
				'}';
	}

	private class H extends Handler {

		public H(@NonNull Looper looper) {
			super(looper);
		}

		@Override
		@SuppressWarnings("unchecked")
		public void handleMessage(@NonNull Message msg) {
			Args obj = (Args) msg.obj;
			Token token = obj.token;

			Listener listener = obj.listener;
			if (listener != null) {
				listener.onStart(token, obj.args);
			}

			Task task = obj.task;
			Object args = obj.args;
			try {
				Object ret = submitSync(token, args, task);
				if (listener != null) {
					listener.onSuccess(token, args, ret);
				}
			} catch (Throwable throwable) {
				if (listener != null) {
					listener.onError(token, args, throwable);
				}
			}
		}
	}

	public static class Args extends DefaultRecyclable {
		private static final ObjectPool<Args> POOL = new ObjectPool<>(32);

		private Task task;
		private Listener listener;

		private Object args;

		private Token token;

		private Args() {
		}

		@Override
		protected void onRecycle() {
			task = null;
			listener = null;
			args = null;
			token = null;
			POOL.release(this);
		}

		@SuppressWarnings("unchecked")
		public static Args obtain(Token token, Object args, @NonNull Task task, @NonNull Listener listener) {
			Args obj = POOL.acquire();
			if (obj == null) {
				obj = new Args();
			}

			obj.token = token;
			obj.task = task;
			obj.listener = listener;
			obj.args = args;
			obj.reuse();
			return obj;
		}
	}
}
