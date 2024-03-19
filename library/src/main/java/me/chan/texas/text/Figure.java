package me.chan.texas.text;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.graphics.Rect;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.Texas;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;

/**
 * 插图
 */
public final class Figure extends DefaultRecyclable implements Segment {
	private static final ObjectPool<Figure> POOL = new ObjectPool<>(Texas.getMemoryOption().getFigureBufferSize());

	private String mUrl;

	private float mWidth;
	private float mHeight;
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	private Object mTag;

	private Rect mRect;
	private int mId;

	private Figure(String url, float width, float height, Object tag) {
		mTag = tag;
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

	@RestrictTo(LIBRARY)
	public void resize(float width, float height) {
		mWidth = width;
		mHeight = height;
	}

	@Override
	@RestrictTo(LIBRARY)
	public void recycle() {
		if (isRecycled()) {
			return;
		}

		mId = 0;
		mWidth = mHeight = 0;
		mUrl = null;
		mTag = null;
		mRect = null;
		super.recycle();
		POOL.release(this);
	}

	@Override
	public int getId() {
		return mId;
	}

	public static Figure obtain(String url, float width, float height) {
		return obtain(url, width, height, null);
	}

	public static Figure obtain(String url, float width, float height, Object tag) {
		Figure figure = POOL.acquire();
		if (figure == null) {
			return new Figure(url, width, height, tag);
		}

		figure.mUrl = url;
		figure.mWidth = width;
		figure.mHeight = height;
		figure.mTag = tag;
		figure.mId = Segment.UUID.getAndIncrement();
		figure.reuse();
		return figure;
	}

	public static void clean() {
		POOL.clean();
	}

	@Nullable
	@Override
	public Object getTag() {
		return mTag;
	}

	@Nullable
	@Override
	public void getRect(Rect rect) {
		rect.set(mRect);
	}

	@Nullable
	@Override
	public Rect getRect() {
		return mRect;
	}

	@Override
	public void setRect(Rect rect) {
		mRect = rect;
	}
}
