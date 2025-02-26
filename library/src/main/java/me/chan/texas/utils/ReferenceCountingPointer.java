package me.chan.texas.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 引用计数器
 *
 * @param <T> 指针类型
 */
public abstract class ReferenceCountingPointer<T> {
	private final T mPointer;
	private final AtomicInteger mRefCount;

	public ReferenceCountingPointer(T pointer) {
		mPointer = pointer;
		mRefCount = new AtomicInteger(1);
	}

	public ReferenceCountingPointer(ReferenceCountingPointer<T> other) {
		mRefCount = other.mRefCount;
		mRefCount.incrementAndGet();
		mPointer = onAcquire(other.mPointer);
	}

	public T get() {
		return mPointer;
	}

	public void release() {
		int v = mRefCount.decrementAndGet();
		if (v < 0) {
			throw new IllegalStateException("Reference count has been corrupted");
		}

		if (v == 0) {
			onRelease(mPointer);
		}
	}

	protected abstract void onRelease(T value);

	protected T onAcquire(T value) {
		return value;
	}

	public int getRefCount() {
		return mRefCount.get();
	}
}
