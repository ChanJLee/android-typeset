package me.chan.texas.renderer;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import me.chan.texas.annotations.Hidden;

@Hidden
class TexasLinearLayoutManager extends LinearLayoutManager {

	public TexasLinearLayoutManager(Context context) {
		super(context, LinearLayoutManager.VERTICAL, false);
	}

	@Override
	public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
		SmoothScrollerImpl smoothScroller = new SmoothScrollerImpl(recyclerView.getContext());
		smoothScroller.setTargetPosition(position);
		startSmoothScroll(smoothScroller);
	}

	private static class SmoothScrollerImpl extends LinearSmoothScroller {

		SmoothScrollerImpl(Context context) {
			super(context);
		}

		@Override
		protected int getVerticalSnapPreference() {
			return SNAP_TO_START;
		}
	}
}
