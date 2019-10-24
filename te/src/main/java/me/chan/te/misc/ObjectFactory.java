package me.chan.te.misc;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * 对象工厂，用于存储频繁创建与销毁的类对象
 *
 * @param <T> 存储类型
 */
public class ObjectFactory<T> {

	private Queue<T> mQueue;
	private int mBufferSize;

	/**
	 * 创建一个对象工厂
	 *
	 * @param bufferSize 当前缓存大小
	 */
	public ObjectFactory(int bufferSize) {
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
	public T acquire() {
		return mQueue.poll();
	}

	/**
	 * 回收一个对象
	 *
	 * @param obj 对象
	 */
	public void release(@NonNull T obj) {
		if (mQueue.size() >= mBufferSize) {
			return;
		}
		mQueue.offer(obj);
	}
}