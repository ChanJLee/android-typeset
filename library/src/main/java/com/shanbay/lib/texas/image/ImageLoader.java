package com.shanbay.lib.texas.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.Charset;
import java.security.MessageDigest;

/**
 * 图片加载器
 */
public class ImageLoader {
	/**
	 * should return {@link Bitmap.Config#ARGB_8888} for
	 * {@link Bitmap#getConfig()} when possible.
	 *
	 * <p> GIF images decoded by {@link android.graphics.BitmapFactory} currently use an internal
	 * hidden format that is returned as null from {@link Bitmap#getConfig()}. Since
	 * we cannot force {@link android.graphics.BitmapFactory} to always return our desired config,
	 * this setting is a preference, not a promise.</p>
	 */
	public static final int FORMAT_ARGB_8888 = 0x01;

	/**
	 * Bitmaps decoded from image formats that support and/or use alpha (some types of PNGs, GIFs etc)
	 * should return {@link Bitmap.Config#ARGB_8888} for
	 * {@link Bitmap#getConfig()}. Bitmaps decoded from formats that don't support or
	 * use alpha should return {@link Bitmap.Config#RGB_565} for
	 * {@link Bitmap#getConfig()}.
	 */
	public static final int FORMAT_RGB_565 = 0x02;

	@IntDef({FORMAT_ARGB_8888, FORMAT_RGB_565})
	@Retention(RetentionPolicy.SOURCE)
	public @interface ImageFormat {

	}

	/**
	 * {@link DiskCacheStrategy#NONE}
	 */
	public static final int CACHE_STRATEGY_NONE = 0x0;
	/**
	 * {@link DiskCacheStrategy#DATA}
	 */
	public static final int CACHE_STRATEGY_DATA = 0x1;
	/**
	 * {@link DiskCacheStrategy#RESOURCE}
	 */
	public static final int CACHE_STRATEGY_RESOURCE = 0x2;
	/**
	 * {@link DiskCacheStrategy#AUTOMATIC}
	 */
	public static final int CACHE_STRATEGY_AUTOMATIC = 0x4;
	/**
	 * {@link DiskCacheStrategy#ALL}
	 */
	public static final int CACHE_STRATEGY_ALL = 0x8;

	@IntDef({CACHE_STRATEGY_NONE, CACHE_STRATEGY_DATA,
			CACHE_STRATEGY_RESOURCE, CACHE_STRATEGY_AUTOMATIC,
			CACHE_STRATEGY_ALL})
	@Retention(RetentionPolicy.SOURCE)
	public @interface CacheStrategy {

	}

	private RequestManager mRequestManager;
	private Handler mHandler;

	public ImageLoader(@NonNull Context context) {
		mRequestManager = Glide.with(context);
		mHandler = new Handler(Looper.getMainLooper());
	}

	/**
	 * load image from file
	 *
	 * @param path file path
	 * @return request
	 */
	public Request file(String path) {
		return file(new File(path));
	}

	/**
	 * load image from drawable
	 *
	 * @param id resource's id
	 * @return request
	 */
	public Request drawable(@DrawableRes int id) {
		return new Request(id);
	}

	/**
	 * load image from file
	 *
	 * @param file file path
	 * @return
	 */
	public Request file(File file) {
		return uri(Uri.fromFile(file));
	}

	/**
	 * load image from uri
	 *
	 * @param uri uri
	 * @return
	 */
	public Request uri(String uri) {
		return uri(Uri.parse(uri));
	}

	/**
	 * load image from uri
	 *
	 * @param uri uri
	 * @return
	 */
	public Request uri(Uri uri) {
		return new Request(uri);
	}

	/**
	 * Image Request
	 */
	public class Request {
		private RequestOptions mRequestOptions;
		/**
		 * 记录是否调用过into
		 */
		private volatile boolean mDead = false;
		private Object mSources;

		private Request(Object source) {
			mSources = source;
			mRequestOptions = new RequestOptions();
		}

		/**
		 * 请求图片的format
		 *
		 * @param format {@link #FORMAT_ARGB_8888} {@link #FORMAT_RGB_565}
		 * @return
		 */
		public Request format(@ImageFormat int format) {
			if (format == FORMAT_ARGB_8888) {
				mRequestOptions.format(DecodeFormat.PREFER_ARGB_8888);
			} else if (format == FORMAT_RGB_565) {
				mRequestOptions.format(DecodeFormat.PREFER_RGB_565);
			}
			return this;
		}

		/**
		 * 图片加载时候的预览图
		 *
		 * @param resId 预览资源
		 * @return
		 */
		public Request preview(@DrawableRes int resId) {
			mRequestOptions.placeholder(resId);
			return this;
		}

		/**
		 * 图片加载时候的预览图
		 *
		 * @param drawable 预览图
		 * @return
		 */
		public Request preview(@NonNull Drawable drawable) {
			mRequestOptions.placeholder(drawable);
			return this;
		}

		/**
		 * 图片加载失败的时候显示的图片
		 *
		 * @param resId 错误资源
		 * @return
		 */
		public Request error(@DrawableRes int resId) {
			mRequestOptions.error(resId);
			return this;
		}

		/**
		 * 图片加载失败的时候显示的图片
		 *
		 * @param drawable 错误图片
		 * @return
		 */
		public Request error(@NonNull Drawable drawable) {
			mRequestOptions.error(drawable);
			return this;
		}

		/**
		 * 设置大小
		 *
		 * @param size 大小
		 * @return
		 */
		public Request size(int size) {
			return size(size, size);
		}

		/**
		 * 设置大小
		 *
		 * @param width  宽度
		 * @param height 高度
		 * @return
		 */
		public Request size(int width, int height) {
			mRequestOptions.override(width, height);
			return this;
		}

		/**
		 * 关闭加载时候的动画效果
		 *
		 * @return request
		 */
		public Request dontAnimate() {
			mRequestOptions.dontAnimate();
			return this;
		}

		/**
		 * 用于唯一识别本次请求
		 *
		 * @param id 用于识别本次请求
		 * @return request
		 */
		public Request id(@NonNull final String id) {
			if (!TextUtils.isEmpty(id)) {
				mRequestOptions.signature(new Key() {
					@Override
					public void updateDiskCacheKey(MessageDigest messageDigest) {
						messageDigest.update(id.getBytes(Charset.forName("UTF-8")));
					}
				});
			}

			return this;
		}

		/**
		 * 图片加载时候的缓存策略
		 *
		 * @param strategy {@link #CACHE_STRATEGY_ALL} {@link #CACHE_STRATEGY_AUTOMATIC}
		 *                 {@link #CACHE_STRATEGY_DATA} {@link #CACHE_STRATEGY_RESOURCE}
		 *                 {@link #CACHE_STRATEGY_NONE}
		 * @return
		 */
		public Request cacheStrategy(@CacheStrategy int strategy) {
			if (strategy == CACHE_STRATEGY_ALL) {
				mRequestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
			} else if (strategy == CACHE_STRATEGY_AUTOMATIC) {
				mRequestOptions.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
			} else if (strategy == CACHE_STRATEGY_DATA) {
				mRequestOptions.diskCacheStrategy(DiskCacheStrategy.DATA);
			} else if (strategy == CACHE_STRATEGY_RESOURCE) {
				mRequestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
			} else if (strategy == CACHE_STRATEGY_NONE) {
				mRequestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
			}
			return this;
		}

		/**
		 * 将获取的图片渲染到ImageView里
		 *
		 * @param imageView imageView
		 * @throws IllegalStateException 每个request只可以调用一次，如果调用多次那么触发{@link IllegalStateException}异常
		 */
		public void into(@NonNull ImageView imageView) {
			if (mDead) {
				throw new IllegalStateException("call one request twice");
			}
			mDead = true;
			mRequestManager.clear(imageView);
			RequestBuilder<Drawable> glideRequest = mRequestManager.load(mSources);
			glideRequest.apply(mRequestOptions);
			glideRequest.into(imageView);
		}

		/**
		 * 异步调用
		 *
		 * @param listener listener
		 * @throws IllegalStateException 每个request只可以调用一次，如果调用多次那么触发{@link IllegalStateException}异常，回调执行在主线程
		 */
		public void asDrawable(@NonNull Listener<Drawable> listener) {
			as(mRequestManager.asDrawable(), listener);
		}

		/**
		 * 异步调用
		 *
		 * @param listener listener
		 * @throws IllegalStateException 每个request只可以调用一次，如果调用多次那么触发{@link IllegalStateException}异常，回调执行在主线程
		 */
		public void asBitmap(@NonNull Listener<Bitmap> listener) {
			as(mRequestManager.asBitmap(), listener);
		}

		public void asBytes(Listener<byte[]> listener) {
			as(mRequestManager.as(byte[].class), listener);
		}

		/**
		 * @param listener 作为文件
		 */
		public void asFile(Listener<File> listener) {
			as(mRequestManager.asFile(), listener);
		}

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
