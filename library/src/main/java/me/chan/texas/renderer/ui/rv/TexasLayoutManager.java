package me.chan.texas.renderer.ui.rv;


import android.view.View;

import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface TexasLayoutManager {

	int findFirstVisibleItemPosition();

	int findLastVisibleItemPosition();

	View findViewByPosition(int index);

	int findFirstCompletelyVisibleItemPosition();

	int findLastCompletelyVisibleItemPosition();
}
