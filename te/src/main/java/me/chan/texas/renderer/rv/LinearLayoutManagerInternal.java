package me.chan.texas.renderer.rv;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

public class LinearLayoutManagerInternal extends LinearLayoutManager {

	public LinearLayoutManagerInternal(Context context) {
		super(context, LinearLayoutManager.VERTICAL, false);
	}

	@Override
	public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
		SmoothScrollerImpl smoothScroller = new SmoothScrollerImpl(recyclerView.getContext());
		smoothScroller.setTargetPosition(position);
		startSmoothScroll(smoothScroller);
	}

	private class SmoothScrollerImpl extends LinearSmoothScroller {

		public SmoothScrollerImpl(Context context) {
			super(context);
		}

		@Override
		protected int getVerticalSnapPreference() {
			return SNAP_TO_START;
		}
	}
}
