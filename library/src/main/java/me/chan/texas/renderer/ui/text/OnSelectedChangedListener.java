package me.chan.texas.renderer.ui.text;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import me.chan.texas.renderer.TouchEvent;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Box;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface OnSelectedChangedListener {
	int EVENT_CLICKED = 1;
	int EVENT_LONG_CLICKED = 2;
	int EVENT_DOUBLE_CLICKED = 3;

	@IntDef({EVENT_CLICKED, EVENT_LONG_CLICKED, EVENT_DOUBLE_CLICKED})
	@interface EventType {
	}

	boolean onParagraphSelected(@NonNull TouchEvent event, @NonNull Paragraph paragraph, @EventType int eventType);

	boolean onBoxSelected(@NonNull TouchEvent event, @NonNull Paragraph paragraph, @EventType int eventType, @NonNull Box box);
}