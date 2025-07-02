package me.chan.texas.renderer.ui.rv;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.content.Context;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import me.chan.texas.renderer.ui.text.TextureParagraph;

@RestrictTo(LIBRARY)
public class TexasLinearLayoutManagerImpl extends LinearLayoutManager implements TexasLayoutManager {

	private int mOffset;

	public TexasLinearLayoutManagerImpl(Context context) {
		super(context, RecyclerView.VERTICAL, false);
	}

	public void setOffset(int offset) {
		mOffset = offset;
	}

	@Override
	public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
		SmoothScrollerImpl smoothScroller = new SmoothScrollerImpl(recyclerView.getContext(), mOffset);
		smoothScroller.setTargetPosition(position);
		startSmoothScroll(smoothScroller);
	}

	@Override
	public void scrollToPosition(int position) {
		scrollToPositionWithOffset(position, mOffset);
	}

	@Override
	public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
		try {
			super.onLayoutChildren(recycler, state);
		} catch (Throwable throwable) {
			throw new RuntimeException("LayoutChildren error", throwable);
		}
	}

	@Nullable
	@Override
	public TextureParagraph findTextureParagraphByPosition(int index) {
		View child = findViewByPosition(index);
		return child instanceof TextureParagraph ? (TextureParagraph) child : null;
	}

	private static class SmoothScrollerImpl extends LinearSmoothScroller {

		private static final int MAX_DURATION = 100;
		private final int mOffset;

		public SmoothScrollerImpl(Context context, int offset) {
			super(context);
			mOffset = offset;
		}

		@Override
		protected int calculateTimeForScrolling(int dx) {
			int duration = super.calculateTimeForScrolling(dx);
			return Math.min(duration, MAX_DURATION);
		}

		@Override
		public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
			return super.calculateDtToFit(viewStart, viewEnd, boxStart, boxEnd, snapPreference) + mOffset;
		}

		@Override
		protected int getVerticalSnapPreference() {
			return SNAP_TO_START;
		}
	}
}
