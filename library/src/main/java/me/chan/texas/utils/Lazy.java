package me.chan.texas.utils;

import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public abstract class Lazy<T> {
	private volatile T mValue = null;

	public synchronized final T value() {
		if (mValue == null) {
			mValue = onCreate();
		}
		return mValue;
	}

	protected abstract T onCreate();
}
