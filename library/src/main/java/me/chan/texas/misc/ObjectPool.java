package me.chan.texas.misc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * 对象工厂，用于存储频繁创建与销毁的类对象
 *
 * @param <T> 存储类型
 */
public class ObjectPool<T> {

	private final Queue<T> mQueue;
	private final int mBufferSize;

	/**
	 * 创建一个对象工厂
	 *
	 * @param bufferSize 当前缓存大小
	 */
	public ObjectPool(int bufferSize) {
		if (bufferSize <= 0) {
			throw new IllegalArgumentException("buffer size must be large than 0");
		}

		mQueue = new ArrayDeque<>(bufferSize);
		mBufferSize = bufferSize;
	}

	/**
	 * 获取一个对象
	 *
	 * @return 返回一个对象，当当前对象工厂没有对象存储时，返回null
	 */
	@Nullable
	public synchronized T acquire() {
		return mQueue.poll();
	}

	/**
	 * 回收一个对象
	 *
	 * @param obj 对象
	 */
	public synchronized boolean release(@NonNull T obj) {
		if (mQueue.size() >= mBufferSize) {
			return false;
		}
		return mQueue.offer(obj);
	}

	/**
	 * 清除内容
	 */
	public synchronized void clean() {
		mQueue.clear();
	}

	public synchronized boolean isEmpty() {
		return mQueue.isEmpty();
	}
}