package me.chan.texas.renderer.ui.rv;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.RecyclerView;

import me.chan.texas.misc.Rect;
import me.chan.texas.renderer.ui.text.ParagraphView;
import me.chan.texas.text.Document;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.text.ViewSegment;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface TexasRecyclerView {
	void addOnScrollListener(RecyclerView.OnScrollListener onScrollListener);

	void allowHandleTouchEvent();

	void disallowHandleTouchEvent();

	boolean getSegmentLocations(Segment segment, Rect locations);

	void scrollBy(int x, int y);

	int getHeight();

	@Nullable
	Document getDocument();

	boolean getViewSegmentParagraphLocations(ViewSegment viewSegment, ParagraphView paragraphView, Paragraph paragraph, Rect locations);
}
