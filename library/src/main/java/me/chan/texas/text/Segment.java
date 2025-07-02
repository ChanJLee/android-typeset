package me.chan.texas.text;

import me.chan.texas.misc.Rect;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.RecyclerView;

import java.util.concurrent.atomic.AtomicInteger;

import me.chan.texas.renderer.ui.RendererHost;
import me.chan.texas.renderer.ui.TexasRendererAdapter;


public interface Segment {

	AtomicInteger SEGMENT_UUID = new AtomicInteger(0);

	
	@Nullable
	Object getTag();

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	void getRect(Rect rect);

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	@Nullable
	Rect getRect();

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	void setRect(Rect rect);

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	void recycle();

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	boolean isRecycled();

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	int getId();

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	void bind(RendererHost host);

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	void attachToWindow(RecyclerView.ViewHolder holder);

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	void detachFromWindow(RecyclerView.ViewHolder holder);

	void requestRedraw();

	
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	int getIndex();

	static int nextId() {
		int id = SEGMENT_UUID.incrementAndGet();
		if (id <= 0) {
			throw new RuntimeException("Segment id overflow");
		}
		return id;
	}
}
