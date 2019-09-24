package me.chan.te.misc;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ObjectFactory<T> {

	private Queue<T> mQueue;
	private int mBufferSize;

	public ObjectFactory(int bufferSize) {
		mQueue = new ConcurrentLinkedQueue<>();
		mBufferSize = bufferSize;
	}

	@Nullable
	public synchronized T acquire() {
		return mQueue.poll();
	}

	public boolean release(@NonNull T t) {
		if (mQueue.size() >= mBufferSize) {
			return false;
		}
		return mQueue.offer(t);
	}
}