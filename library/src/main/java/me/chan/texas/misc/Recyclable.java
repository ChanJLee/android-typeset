package me.chan.texas.misc;

import androidx.annotation.RestrictTo;


@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface Recyclable {
	
	void recycle();

	
	boolean isRecycled();

	
	void reuse();
}
