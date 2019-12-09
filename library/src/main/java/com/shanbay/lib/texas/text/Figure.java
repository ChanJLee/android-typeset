package com.shanbay.lib.texas.text;

import com.shanbay.lib.texas.annotations.Hidden;
import com.shanbay.lib.texas.misc.ObjectFactory;

/**
 * 插图
 */
public class Figure extends Segment {
	public static final float DEFAULT_RATIO = 1.618f;

	private static final ObjectFactory<Figure> POOL = new ObjectFactory<>(16);

	private String mUrl;

	private float mWidth;
	private float mHeight;

	private Figure(String url, float width, float height) {
		mUrl = url;
		mWidth = width;
		mHeight = height;
	}

	public String getUrl() {
		return mUrl;
	}

	public float getWidth() {
		return mWidth;
	}

	public float getHeight() {
		return mHeight;
	}

	@Hidden
	public void resize(float width, float height) {
		mWidth = width;
		mHeight = height;
	}

	@Hidden
	@Override
	public void recycle() {
		if (isRecycled()) {
			return;
		}

		super.recycle();
		mWidth = mHeight = -1;
		mUrl = null;
		POOL.release(this);
	}

	public static Figure obtain(String url, float width, float height) {
		Figure figure = POOL.acquire();
		if (figure == null) {
			return new Figure(url, width, height);
		}
		figure.mUrl = url;
		figure.mWidth = width;
		figure.mHeight = height;
		figure.reuse();
		return figure;
	}

	public static void clean() {
		POOL.clean();
	}
}
