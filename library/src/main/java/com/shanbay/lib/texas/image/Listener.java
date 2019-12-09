package com.shanbay.lib.texas.image;

import com.shanbay.lib.texas.annotations.Hidden;

@Hidden
public interface Listener<T> {
	void onLoadCleared(ImageLoader loader);

	void onLoadStarted(ImageLoader loader);

	void onLoadFailed(ImageLoader loader);

	void onLoadSuccess(ImageLoader loader, T resources);
}
