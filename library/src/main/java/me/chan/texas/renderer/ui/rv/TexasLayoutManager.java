package me.chan.texas.renderer.ui.rv;


import android.view.View;

import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface TexasLayoutManager {

	int findFirstVisibleItemPosition();

	int findLastVisibleItemPosition();

	int findFirstCompletelyVisibleItemPosition();

	int findLastCompletelyVisibleItemPosition();
}
