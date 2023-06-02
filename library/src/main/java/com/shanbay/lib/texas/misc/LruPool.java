package com.shanbay.lib.texas.misc;

import androidx.annotation.NonNull;

public class LruPool<T> {

	private final ObjectPool<T> mPool;
	private final InstanceInstrument<T> mInstrument;

	public LruPool(int bufferSize, InstanceInstrument<T> instrument) {
		mPool = new ObjectPool<>(bufferSize);
		mInstrument = instrument;
	}

	@NonNull
	public T acquire() {
		T obj = mPool.acquire();
		if (obj == null) {
			obj = mInstrument.create();
		}
		return obj;
	}

	public void release(@NonNull T obj) {
		if (!mPool.release(obj)) {
			mInstrument.release(obj);
		}
	}

	public void clean() {
		T obj;
		while ((obj = mPool.acquire()) != null) {
			mInstrument.release(obj);
		}
	}

	public interface InstanceInstrument<T> {
		T create();

		void release(T obj);
	}
}
