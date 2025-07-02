package me.chan.texas.utils;

import java.util.concurrent.atomic.AtomicInteger;


public class ReferenceCountingPointer<T> {
	private final T mPointer;
	private final AtomicInteger mRefCount;
	private final Listener<T> mListener;

	public ReferenceCountingPointer(T pointer, Listener<T> listener) {
		mPointer = pointer;
		mRefCount = new AtomicInteger(1);
		mListener = listener;
	}

	public ReferenceCountingPointer(ReferenceCountingPointer<T> other) {
		mRefCount = other.mRefCount;
		mRefCount.incrementAndGet();
		mPointer = onAcquire(other.mPointer);
		mListener = other.mListener;
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
			mListener.onRelease(mPointer);
		}
	}

	protected T onAcquire(T value) {
		return value;
	}

	public int getRefCount() {
		return mRefCount.get();
	}

	public interface Listener<T> {
		void onRelease(T v);
	}
}
