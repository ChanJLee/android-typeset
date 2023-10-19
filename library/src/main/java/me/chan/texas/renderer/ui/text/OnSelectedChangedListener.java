package me.chan.texas.renderer.ui.text;

import android.view.MotionEvent;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Box;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface OnSelectedChangedListener {
	int EVENT_CLICKED = 1;
	int EVENT_LONG_CLICKED = 2;
	int EVENT_DOUBLE_CLICKED = 3;

	boolean onSegmentClicked(MotionEvent e, Paragraph paragraph, int eventType);

	@IntDef({EVENT_CLICKED, EVENT_LONG_CLICKED, EVENT_DOUBLE_CLICKED})
	@interface EventType {
	}

	boolean onBoxSelected(MotionEvent e, Paragraph paragraph, @EventType int eventType, Box box);
}