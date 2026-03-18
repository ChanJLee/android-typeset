package me.chan.texas.text;

import me.chan.texas.R;
import me.chan.texas.misc.Rect;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.concurrent.atomic.AtomicInteger;

import me.chan.texas.renderer.ui.RendererHost;

/**
 * 渲染的最小单元
 */
public abstract class Segment {

	static AtomicInteger SEGMENT_UUID = new AtomicInteger(0);

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	protected SparseArrayCompat<Object> mTagsKv;

	@Nullable
	public final  <T> T getTag(@IdRes int key, T defaultValue) {
		if (mTagsKv == null) {
			return defaultValue;
		}

		return (T) mTagsKv.get(key, defaultValue);
	}

	/**
	 * @return 获取当前segment的唯一标识
	 */
	@Nullable
	public final Object getTag() {
		return getTag(R.id.me_chan_texas_view_segment_tag, null);
	}

	public final void setTag(@Nullable Object tag) {
		setTag(R.id.me_chan_texas_view_segment_tag, tag);
	}

	public final void setTag(@IdRes int id, @Nullable Object tag) {
		if (mTagsKv == null) {
			mTagsKv = new SparseArrayCompat<>();
		}
		mTagsKv.put(id, tag);
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public abstract void getRect(Rect rect);

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	@Nullable
	public abstract Rect getRect();

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public abstract void setPadding(Rect rect);

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public abstract void recycle();

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public abstract boolean isRecycled();

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public abstract int getId();

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public abstract void bind(RendererHost host);

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public abstract void attachToWindow(RecyclerView.ViewHolder holder);

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public abstract void detachFromWindow(RecyclerView.ViewHolder holder);

	public abstract void requestRedraw();

	/**
	 * @return 获得最终显示内容时候的下标
	 */
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public abstract int getIndex();

	static int nextId() {
		int id = Segment.SEGMENT_UUID.incrementAndGet();
		if (id <= 0) {
			throw new RuntimeException("Segment id overflow");
		}
		return id;
	}
}
