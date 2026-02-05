package me.chan.texas.renderer.ui.rv;


import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface TexasLayoutManager {

	int findFirstVisibleItemPosition();

	int findLastVisibleItemPosition();

	int findFirstCompletelyVisibleItemPosition();

	int findLastCompletelyVisibleItemPosition();
}
