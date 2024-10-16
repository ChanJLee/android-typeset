package me.chan.texas.renderer.ui;

import androidx.annotation.RestrictTo;

import me.chan.texas.renderer.RenderOption;
import me.chan.texas.text.Document;
import me.chan.texas.text.Segment;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface TexasRendererAdapter {
	int getItemCount();

	Segment getItem(int position);

	Document getDocument();

	RenderOption getRenderOption();

	int sendSignal(Segment segment, Object sig);

	int sendSignal(int index, Object sig);

	Object SIG_SELECTION_CHANGED = new Object();

	int indexOf(Segment segment);
}
