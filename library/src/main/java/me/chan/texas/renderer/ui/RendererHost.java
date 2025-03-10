package me.chan.texas.renderer.ui;

import androidx.annotation.RestrictTo;

import me.chan.texas.text.Segment;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface RendererHost {
	void updateSegment(Object unit, Segment segment);

	default int indexOf(Segment segment) {
		return -1;
	}
}
