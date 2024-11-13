package me.chan.texas.renderer.ui.rv;

import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.RecyclerView;

import me.chan.texas.renderer.ui.text.TextureParagraph;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface TexasRecyclerView {
	void addOnScrollListener(RecyclerView.OnScrollListener onScrollListener);

	void allowHandleTouchEvent();

	void disallowHandleTouchEvent();

	TexasLayoutManager getTexasLayoutManager();

	void getChildLocations(TextureParagraph textureParagraph, int[] locations);

	void scrollBy(int x, int y);

	int getHeight();

	RecyclerView.Adapter<?> getAdapter();
}
