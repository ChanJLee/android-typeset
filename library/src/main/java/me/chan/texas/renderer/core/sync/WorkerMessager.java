package me.chan.texas.renderer.core.sync;


import androidx.annotation.Nullable;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.utils.concurrency.TaskQueue;

import java.util.ArrayList;
import java.util.List;

public abstract class WorkerMessager {

	protected List<Listener> mListeners = new ArrayList<>();

	public void addListener(Listener listener) {
		mListeners.add(listener);
	}

	/**
	 * 发送消息
	 *
	 * @param token      id
	 * @param message 消息附带的值
	 */
	public abstract void send(TaskQueue.Token token, WorkerMessage message);

	/**
	 * 清空消息
	 */
	public abstract void clear(TaskQueue.Token token);

	public interface Listener {
		/**
		 * 处理消息
		 *
		 * @param token
		 * @param value
		 */
		boolean handleMessage(TaskQueue.Token token, WorkerMessage value);
	}

	public static class WorkerMessage extends DefaultRecyclable {
		private static final ObjectPool<WorkerMessage> POOL = new ObjectPool<>(32);

		private int mType;
		private Object mArg;
		private Object mValue;

		private WorkerMessage() {
		}

		public int type() {
			return mType;
		}

		@Nullable
		@SuppressWarnings("unchecked")
		public <A> A asArg(Class<A> clazz) {
			if (clazz.isInstance(mArg)) {
				return (A) mArg;
			}
			return null;
		}

		@SuppressWarnings("unchecked")
		public <V> V value() {
			if (hasError()) {
				throw new IllegalStateException("try to read value with exception");
			}

			if (isRecycled()) {
				throw new IllegalStateException("try to read value with recycled message");
			}

			return (V) mValue;
		}

		public Throwable error() {
			if (isRecycled()) {
				throw new IllegalStateException("try to read value with recycled message");
			}

			return (Throwable) mValue;
		}

		public boolean hasError() {
			return mValue instanceof Throwable;
		}

		@Override
		public void recycle() {
			if (isRecycled()) {
				return;
			}

			mArg = mValue = null;
			super.recycle();
			POOL.release(this);
		}


		public static WorkerMessage obtain(int type, Object arg, Object value) {
			WorkerMessage message = POOL.acquire();
			if (message == null) {
				message = new WorkerMessage();
			}

			message.mType = type;
			message.mArg = arg;
			message.mValue = value;
			message.reuse();
			return message;
		}

	}
}
