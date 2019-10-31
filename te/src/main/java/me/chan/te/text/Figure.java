package me.chan.te.text;

import me.chan.te.misc.Recyclable;
import me.chan.te.misc.ObjectFactory;

/**
 * 插图
 */
public class Figure implements Segment, Recyclable {
	private static final ObjectFactory<Figure> POOL = new ObjectFactory<>(16);

	private String mUrl;

	private int mWidth;
	private int mHeight;
	private Object mExtra;

	private Figure(String url, int width, int height, Object extra) {
		mUrl = url;
		mWidth = width;
		mHeight = height;
		mExtra = extra;
	}

	public String getUrl() {
		return mUrl;
	}

	public int getWidth() {
		return mWidth;
	}

	public int getHeight() {
		return mHeight;
	}

	@Override
	public void recycle() {
		mWidth = mHeight = -1;
		mUrl = null;
		POOL.release(this);
	}

	public static Figure obtain(String url) {
		return obtain(url, -1, -1, null);
	}

	public static Figure obtain(String url, int width, int height, Object extra) {
		Figure figure = POOL.acquire();
		if (figure == null) {
			return new Figure(url, width, height, extra);
		}
		figure.mUrl = url;
		figure.mWidth = width;
		figure.mHeight = height;
		figure.mExtra = extra;
		return figure;
	}

	public static void clean() {
		POOL.clean();
	}
}
