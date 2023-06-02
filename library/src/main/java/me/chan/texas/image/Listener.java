package me.chan.texas.image;

import androidx.annotation.RestrictTo;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

@RestrictTo(LIBRARY)
public interface Listener<T> {
	void onLoadCleared(ImageLoader loader);

	void onLoadStarted(ImageLoader loader);

	void onLoadFailed(ImageLoader loader);

	void onLoadSuccess(ImageLoader loader, T resources);
}
