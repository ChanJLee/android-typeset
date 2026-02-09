package me.chan.texas.text;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.annotations.Idempotent;
import me.chan.texas.renderer.SpanTouchEventHandler;
import me.chan.texas.renderer.ui.text.OnSelectedChangedListener;

/**
 * 选中协议
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface SelectionProvider {

	@Nullable
	@Idempotent
	SpanTouchEventHandler getSpanTouchEventHandler();

	@Nullable
	@Idempotent
	OnSelectedChangedListener getOnSelectedChangedListener();
}
