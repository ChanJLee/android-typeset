package me.chan.te.misc;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class ObjectFactory<T> {

	private Queue<T> mQueue;
	private int mBufferSize;

	public ObjectFactory(int bufferSize) {
		if (bufferSize <= 0) {
			throw new IllegalArgumentException("buffer size must be large than 0");
		}

		mQueue = new LinkedBlockingQueue<>();
		mBufferSize = bufferSize;
	}

	@Nullable
	public synchronized T acquire() {
		return mQueue.poll();
	}

	public synchronized boolean release(@NonNull T t) {
		if (mQueue.size() >= mBufferSize) {
			return false;
		}
		return mQueue.offer(t);
	}
}