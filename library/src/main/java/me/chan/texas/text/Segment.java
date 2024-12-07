package me.chan.texas.text;

import android.graphics.Rect;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 渲染的最小单元
 */
public interface Segment {

	AtomicInteger SEGMENT_UUID = new AtomicInteger(0);

	/**
	 * @return 获取当前segment的唯一标识
	 */
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
	default void attachToWindow() {
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	default void detachFromWindow() {
	}

	static int nextId() {
		int id = SEGMENT_UUID.incrementAndGet();
		if (id <= 0) {
			throw new RuntimeException("Segment id overflow");
		}
		return id;
	}
}
