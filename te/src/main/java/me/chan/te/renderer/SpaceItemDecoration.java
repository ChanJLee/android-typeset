package me.chan.te.renderer;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
	private int mSegmentSpace;

	public SpaceItemDecoration(float segmentSpace) {
		mSegmentSpace = (int) segmentSpace;
	}

	@Override
	public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
		final int currentPosition = parent.getChildLayoutPosition(view);
		int count = state.getItemCount();
		if (currentPosition < 0 || currentPosition >= count) {
			return;
		}

		if (currentPosition != count - 1) {
			outRect.set(0, 0, 0, mSegmentSpace);
		}
	}
}
