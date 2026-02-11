package me.chan.texas.ext.image;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import androidx.annotation.RestrictTo;

@RestrictTo(LIBRARY)
public class ListenerAdapter<T> implements Listener<T> {

	@Override
	public void onLoadCleared(ImageLoader loader) {

	}

	@Override
	public void onLoadStarted(ImageLoader loader) {

	}

	@Override
	public void onLoadFailed(ImageLoader loader) {

	}

	@Override
	public void onLoadSuccess(ImageLoader loader, T resources) {

	}
}
