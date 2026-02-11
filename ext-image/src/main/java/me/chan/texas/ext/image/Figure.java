package me.chan.texas.ext.image;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.RecyclerView;

import me.chan.texas.misc.Rect;
import me.chan.texas.renderer.ui.RendererHost;
import me.chan.texas.text.Segment;

/**
 * 插图
 */
public final class Figure implements Segment {

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
	public void recycle() {
		mId = 0;
		mWidth = mHeight = 0;
		mUrl = null;
		mTag = null;
		mRect = null;
		mHost = null;
		mHolder = null;
	}

	@Override
	public boolean isRecycled() {
		return mId == 0;
	}

	@Override
	public int getId() {
		return mId;
	}

	public static Figure obtain(String url, float width, float height) {
		return obtain(url, width, height, null);
	}

	public static Figure obtain(String url, float width, float height, Object tag) {
		Figure figure = new Figure(url, width, height, tag);
		figure.mId = Segment.nextId();
		return figure;
	}

	@Nullable
	@Override
	public Object getTag() {
		return mTag;
	}

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
	public void setPadding(Rect rect) {
		mRect = rect;
	}

	private RecyclerView.ViewHolder mHolder;
	private RendererHost mHost;

	@Override
	public void attachToWindow(RecyclerView.ViewHolder holder) {
		mHolder = holder;
	}

	@Override
	public void detachFromWindow(RecyclerView.ViewHolder holder) {
		mHolder = null;
	}

	@Override
	public void bind(RendererHost host) {
		mHost = host;
	}

	@Override
	public void requestRedraw() {
		if (mHost == null) {
			return;
		}

		mHost.updateSegment(mHolder, this);
	}

	@Override
	public int getIndex() {
		return mHost == null ? -1 : mHost.indexOf(this);
	}
}
