package me.chan.texas.misc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.Queue;


public class ObjectPool<T> {

	private final Queue<T> mQueue;
	private final int mBufferSize;

	
	public ObjectPool(int bufferSize) {
		if (bufferSize <= 0) {
			throw new IllegalArgumentException("buffer size must be large than 0");
		}

		mQueue = new ArrayDeque<>(bufferSize);
		mBufferSize = bufferSize;
	}

	
	@Nullable
	public synchronized T acquire() {
		return mQueue.poll();
	}

	
	public synchronized boolean release(@NonNull T obj) {
		if (mQueue.size() >= mBufferSize) {
			return false;
		}
		return mQueue.offer(obj);
	}

	
	public synchronized void clean() {
		mQueue.clear();
	}

	public synchronized boolean isEmpty() {
		return mQueue.isEmpty();
	}
}
