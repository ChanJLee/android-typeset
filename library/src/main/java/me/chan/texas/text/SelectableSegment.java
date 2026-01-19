package me.chan.texas.text;

import me.chan.texas.renderer.SpanTouchEventHandler;
import me.chan.texas.renderer.ui.text.OnSelectedChangedListener;

/**
 * 可选中的segment
 */
public interface SelectableSegment {
	Segment getSegment();

	Paragraph getParagraph();

	void onConfigureTouchEvent(SpanTouchEventHandler spanTouchEventHandler, OnSelectedChangedListener onSelectedChangedListener);
}
