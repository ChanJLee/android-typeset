package me.chan.texas.renderer;

import androidx.annotation.Nullable;


public interface SpanTouchEventHandler {

	
	boolean isSpanClickable(@Nullable Object tag);

	
	default boolean acceptSpan(EventType type, @Nullable Object clickedTag) {
		return true;
	}

	
	boolean applySpanClicked(@Nullable Object clickedTag, @Nullable Object otherTag);

	
	boolean applySpanLongClicked(@Nullable Object clickedTag, @Nullable Object otherTag);

	enum EventType {
		CLICK, LONG_CLICK
	}
}
