package me.chan.texas.source;

import androidx.annotation.AnyThread;
import androidx.annotation.Nullable;


public abstract class Source<T> {

	
	@AnyThread
	@Nullable
	public final synchronized T read() {
		return onRead();
	}

	
	@Nullable
	protected abstract T onRead();
}
