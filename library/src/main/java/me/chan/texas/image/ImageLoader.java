package me.chan.texas.image;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;


@RestrictTo(LIBRARY)
public class ImageLoader {
	private final RequestManager mRequestManager;
	private final Handler mHandler;

	public ImageLoader(@NonNull Context context) {
		mRequestManager = Glide.with(context);
		mHandler = new Handler(Looper.getMainLooper());
	}

	
	public Request file(String path) {
		return file(new File(path));
	}

	
	public Request drawable(@DrawableRes int id) {
		return new Request(id);
	}

	
	public Request file(File file) {
		return uri(Uri.fromFile(file));
	}

	
	public Request uri(String uri) {
		return uri(Uri.parse(uri));
	}

	
	public Request uri(Uri uri) {
		return new Request(uri);
	}

	
	public class Request {
		private final RequestOptions mRequestOptions;
		
		private volatile boolean mDead = false;
		private final Object mSources;

		private Request(Object source) {
			mSources = source;
			mRequestOptions = new RequestOptions();
		}

		
		@SuppressLint("CheckResult")
		public Request preview(@DrawableRes int resId) {
			mRequestOptions.placeholder(resId);
			return this;
		}

		
		@SuppressLint("CheckResult")
		public Request preview(@NonNull Drawable drawable) {
			mRequestOptions.placeholder(drawable);
			return this;
		}

		
		@SuppressLint("CheckResult")
		public Request error(@DrawableRes int resId) {
			mRequestOptions.error(resId);
			return this;
		}

		
		@SuppressLint("CheckResult")
		public Request error(@NonNull Drawable drawable) {
			mRequestOptions.error(drawable);
			return this;
		}

		
		public Request size(int size) {
			return size(size, size);
		}

		
		@SuppressLint("CheckResult")
		public Request size(int width, int height) {
			mRequestOptions.override(width, height);
			return this;
		}

		
		@SuppressLint("CheckResult")
		public Request dontAnimate() {
			mRequestOptions.dontAnimate();
			return this;
		}

		
		@SuppressLint("CheckResult")
		public Request id(@NonNull final String id) {
			if (!TextUtils.isEmpty(id)) {
				mRequestOptions.signature(new Key() {
					@Override
					public void updateDiskCacheKey(MessageDigest messageDigest) {
						messageDigest.update(id.getBytes(StandardCharsets.UTF_8));
					}
				});
			}

			return this;
		}

		
		@SuppressLint("CheckResult")
		public void into(@NonNull ImageView imageView) {
			if (mDead) {
				throw new IllegalStateException("call one request twice");
			}
			mDead = true;
			mRequestManager.clear(imageView);
			RequestBuilder<Drawable> glideRequest = mRequestManager.load(mSources);
			glideRequest.apply(mRequestOptions);

			ViewTarget<?, ?> target = glideRequest.into(imageView);
			target.waitForLayout();
		}

		
		public void asDrawable(@NonNull Listener<Drawable> listener) {
			as(mRequestManager.asDrawable(), listener);
		}

		
		public void asBitmap(@NonNull Listener<Bitmap> listener) {
			as(mRequestManager.asBitmap(), listener);
		}

		public void asBytes(Listener<byte[]> listener) {
			as(mRequestManager.as(byte[].class), listener);
		}

		
		public void asFile(Listener<File> listener) {
			as(mRequestManager.asFile(), listener);
		}

		@SuppressLint("CheckResult")
		private <T> void as(RequestBuilder<T> requestBuilder, final Listener<T> listener) {
			final RequestBuilder<T> glideRequest = requestBuilder.load(mSources);
			glideRequest.apply(mRequestOptions);
			if (Looper.myLooper() != Looper.getMainLooper()) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						doAs(glideRequest, listener);
					}
				});
				return;
			}
			doAs(glideRequest, listener);
		}

		private <T> void doAs(RequestBuilder<T> requestBuilder, final Listener<T> listener) {
			if (mDead) {
				throw new IllegalStateException("call one request twice");
			}
			mDead = true;
			requestBuilder.into(new SimpleTarget<T>() {
				@Override
				public void onResourceReady(T resource, Transition<? super T> transition) {
					listener.onLoadSuccess(ImageLoader.this, resource);
				}

				@Override
				public void onLoadFailed(@Nullable Drawable errorDrawable) {
					listener.onLoadFailed(ImageLoader.this);
				}

				@Override
				public void onLoadCleared(@Nullable Drawable placeholder) {
					listener.onLoadCleared(ImageLoader.this);
				}

				@Override
				public void onLoadStarted(@Nullable Drawable placeholder) {
					listener.onLoadStarted(ImageLoader.this);
				}
			});
		}
	}
}
