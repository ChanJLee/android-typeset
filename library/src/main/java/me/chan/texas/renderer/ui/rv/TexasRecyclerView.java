package me.chan.texas.renderer.ui.rv;

import android.view.View;

import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.RecyclerView;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface TexasRecyclerView {
	void addOnScrollListener(RecyclerView.OnScrollListener onScrollListener);

	void allowHandleTouchEvent();

	void disallowHandleTouchEvent();

	RecyclerView.LayoutManager getLayoutManager();

	void getChildLocations(View child, int[] locations);

	void scrollBy(int x, int y);

	int getHeight();
}
