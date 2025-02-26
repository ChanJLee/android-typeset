package me.chan.texas.renderer.ui.rv;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.RecyclerView;

import me.chan.texas.renderer.ui.text.TextureParagraph;
import me.chan.texas.text.Document;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface TexasRecyclerView {
	void addOnScrollListener(RecyclerView.OnScrollListener onScrollListener);

	void allowHandleTouchEvent();

	void disallowHandleTouchEvent();

	TexasLayoutManager getTexasLayoutManager();

	void getChildLocations(TextureParagraph textureParagraph, int[] locations);

	void scrollBy(int x, int y);

	int getHeight();

	@Nullable
	Document getDocument();
}
