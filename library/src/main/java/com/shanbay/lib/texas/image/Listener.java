package com.shanbay.lib.texas.image;

public interface Listener<T> {
	void onLoadCleared(ImageLoader loader);

	void onLoadStarted(ImageLoader loader);

	void onLoadFailed(ImageLoader loader);

	void onLoadSuccess(ImageLoader loader, T resources);
}
