package me.chan.texas.text;

import androidx.annotation.RestrictTo;

import me.chan.texas.renderer.SpanTouchEventHandler;
import me.chan.texas.renderer.ui.text.OnSelectedChangedListener;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface SelectionProvider {
	SpanTouchEventHandler getSpanTouchEventHandler();

	OnSelectedChangedListener getOnSelectedChangedListener();
}
