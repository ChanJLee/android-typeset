package me.chan.texas.renderer;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import me.chan.texas.annotations.Hidden;
import me.chan.texas.text.Segment;

@Hidden
class SpaceItemDecoration extends RecyclerView.ItemDecoration {
	private float mSegmentSpace;

	public SpaceItemDecoration(float segmentSpace) {
		mSegmentSpace = segmentSpace;
	}

	public void setSegmentSpace(float segmentSpace) {
		mSegmentSpace = segmentSpace;
	}

	@Override
	public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
		final int currentPosition = parent.getChildLayoutPosition(view);
		int count = state.getItemCount();
		if (currentPosition < 0 || currentPosition >= count) {
			return;
		}

		if (currentPosition == count - 1) {
			return;
		}

		TexasAdapter adapter = (TexasAdapter) parent.getAdapter();
		if (adapter == null) {
			return;
		}

		Segment segment = adapter.getItem(currentPosition);

		float segmentSpace = segment.getVerticalSpace(mSegmentSpace);
		if (segmentSpace < 0) {
			segmentSpace = mSegmentSpace;
		}

		outRect.set(0, 0, 0, (int) segmentSpace);
	}
}
