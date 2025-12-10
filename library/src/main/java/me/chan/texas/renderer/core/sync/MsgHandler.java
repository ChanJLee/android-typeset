package me.chan.texas.renderer.core.sync;


import androidx.annotation.Nullable;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.utils.concurrency.Worker;

import java.util.ArrayList;
import java.util.List;

/**
 * 跟主ui线程通信的消息处理器
 */
public abstract class MsgHandler {

	protected List<Listener> mListeners = new ArrayList<>();

	public void addListener(Listener listener) {
		mListeners.add(listener);
	}

	public void removeListener(Listener listener) {
		mListeners.remove(listener);
	}

	/**
	 * 发送消息
	 *
	 * @param token   id
	 * @param message 消息附带的值
	 */
	public abstract void send(Worker.Token token, Msg message);

	/**
	 * 清空token对应的消息
	 */
	public abstract void clear(Worker.Token token);

	public interface Listener {
		/**
		 * 处理消息
		 *
		 * @param token token
		 * @param msg   消息
		 */
		boolean handle(Worker.Token token, Msg msg);
	}

	public static class Msg extends DefaultRecyclable {
		private static final ObjectPool<Msg> POOL = new ObjectPool<>(32);

		private int mType;
		private Object mArg;
		private Object mValue;

		private Worker.Token mToken;

		private Msg() {
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

		public Object arg() {
			return mArg;
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
		protected void onRecycle() {
			mArg = mValue = null;
			mToken = null;
			POOL.release(this);
		}

		public Worker.Token getToken() {
			return mToken;
		}

		public void setToken(Worker.Token token) {
			mToken = token;
		}

		public static Msg obtain(int type, Object arg, Object value) {
			Msg message = POOL.acquire();
			if (message == null) {
				message = new Msg();
			}

			message.mType = type;
			message.mArg = arg;
			message.mValue = value;
			message.reuse();
			return message;
		}
	}
}
