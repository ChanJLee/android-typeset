package me.chan.texas.utils.concurrency;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class AndroidWorker implements Worker {
	private volatile H mHandler;

	private final String mName;

	public AndroidWorker(String name) {
		mName = name;
	}

	private synchronized H getHandler() {
		if (mHandler != null) {
			return mHandler;
		}

		HandlerThread thread = new HandlerThread(mName);
		thread.start();

		mHandler = new H(thread.getLooper());
		return mHandler;
	}

	@Override
	public <A, R> void async(Token token, @NonNull A args, @NonNull Task<A, R> task) {
		Message message = Message.obtain();
		message.what = token.getId();
		message.obj = Args.obtain(token, args, task);
		Handler handler = getHandler();
		handler.sendMessage(message);
	}

	@Override
	public <A, R> R sync(Token token, @NonNull A args, @NonNull Task<A, R> task) throws Throwable {
		try {
			task.onStart(token, args);
			R ret = task.exec(token, args);
			task.onSuccess(token, args, ret);
			return ret;
		} catch (Throwable error) {
			task.onError(token, args, error);
			throw error;
		}
	}

	@Override
	public synchronized void cancel(Token token) {
		if (mHandler == null) {
			return;
		}

		mHandler.removeMessages(token.getId());
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

			Task task = obj.task;
			Object args = obj.args;

			try {
				sync(token, args, task);
			} catch (Throwable ignore) {
				
			}
		}
	}

	public static class Args extends DefaultRecyclable {
		private static final ObjectPool<Args> POOL = new ObjectPool<>(32);

		private Task<?, ?> task;

		private Object args;

		private Token token;

		private Args() {
		}

		@Override
		protected void onRecycle() {
			task = null;
			args = null;
			token = null;
			POOL.release(this);
		}

		@SuppressWarnings("unchecked")
		public static <A> Args obtain(Token token,
									  A args,
									  @NonNull Task<A, ?> task) {
			Args obj = POOL.acquire();
			if (obj == null) {
				obj = new Args();
			}

			obj.token = token;
			obj.task = task;
			obj.args = args;
			obj.reuse();
			return obj;
		}
	}
}
