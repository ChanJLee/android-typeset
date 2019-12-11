package com.shanbay.lib.texas.renderer;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.shanbay.lib.texas.annotations.Hidden;

@Hidden
class TexasLinearLayoutManager extends LinearLayoutManager {

	TexasLinearLayoutManager(Context context) {
		super(context, LinearLayoutManager.VERTICAL, false);
	}

	@Override
	public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
		SmoothScrollerImpl smoothScroller = new SmoothScrollerImpl(recyclerView.getContext());
		smoothScroller.setTargetPosition(position);
		startSmoothScroll(smoothScroller);
	}

	private static class SmoothScrollerImpl extends LinearSmoothScroller {

		private static final int MAX_DURATION = 100;

		SmoothScrollerImpl(Context context) {
			super(context);
		}

		@Override
		protected int calculateTimeForScrolling(int dx) {
			int duration = super.calculateTimeForScrolling(dx);
			return duration > MAX_DURATION ? MAX_DURATION : duration;
		}

		@Override
		protected int getVerticalSnapPreference() {
			return SNAP_TO_START;
		}
	}
}