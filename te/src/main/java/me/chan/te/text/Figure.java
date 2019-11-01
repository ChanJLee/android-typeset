package me.chan.te.text;

import me.chan.te.misc.Recyclable;
import me.chan.te.misc.ObjectFactory;

/**
 * 插图
 */
public class Figure implements Segment, Recyclable {
	private static final ObjectFactory<Figure> POOL = new ObjectFactory<>(16);

	private String mUrl;

	private float mWidth;
	private float mHeight;
	private Object mExtra;

	private Figure(String url, float width, float height, Object extra) {
		mUrl = url;
		mWidth = width;
		mHeight = height;
		mExtra = extra;
	}

	public Object getExtra() {
		return mExtra;
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

	@Override
	public void recycle() {
		mWidth = mHeight = -1;
		mUrl = null;
		mExtra = null;
		POOL.release(this);
	}

	public static Figure obtain(String url) {
		return obtain(url, -1, -1, null);
	}

	public static Figure obtain(String url, float width, float height, Object extra) {
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
