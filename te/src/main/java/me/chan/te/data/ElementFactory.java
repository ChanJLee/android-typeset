package me.chan.te.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pools;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ElementFactory {

	private Pools.Pool<Box> mBoxPool = new ElementPool<>(10000);
	private Pools.Pool<Penalty> mPenaltyPool = new ElementPool<>(4000);
	private Pools.Pool<Glue> mGluePool = new ElementPool<>(10000);

	public Box obtainBox(CharSequence charSequence) {
		return obtainBox(charSequence, null);
	}

	public Box obtainBox(CharSequence charSequence, BoxStyle boxStyle) {
		Box box = mBoxPool.acquire();
		if (box == null) {
			box = new Box();
		}
		box.reset(charSequence, boxStyle);
		return box;
	}

	public void recycle(Element element) {
		if (element instanceof Box) {
			mBoxPool.release((Box) element);
		} else if (element instanceof Penalty) {
			mPenaltyPool.release((Penalty) element);
		} else {
			mGluePool.release((Glue) element);
		}
	}

	private class ElementPool<T> implements Pools.Pool<T> {

		private Queue<T> mQueue;
		private int mBufferSize;

		ElementPool(int bufferSize) {
			mQueue = new ConcurrentLinkedQueue<>();
			mBufferSize = bufferSize;
		}

		@Nullable
		@Override
		public synchronized T acquire() {
			return mQueue.poll();
		}

		@Override
		public boolean release(@NonNull T t) {
			if (mQueue.size() >= mBufferSize) {
				return false;
			}
			return mQueue.offer(t);
		}
	}
}
