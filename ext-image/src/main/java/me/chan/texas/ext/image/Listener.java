package me.chan.texas.ext.image;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import androidx.annotation.RestrictTo;

@RestrictTo(LIBRARY)
public interface Listener<T> {
	void onLoadCleared(ImageLoader loader);

	void onLoadStarted(ImageLoader loader);

	void onLoadFailed(ImageLoader loader);

	void onLoadSuccess(ImageLoader loader, T resources);
}
