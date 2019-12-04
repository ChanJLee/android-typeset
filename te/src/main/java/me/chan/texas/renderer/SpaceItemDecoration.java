package me.chan.texas.renderer;

import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.chan.texas.text.Segment;
import me.chan.texas.text.ViewSegment;

import android.view.View;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
	private int mSegmentSpace;

	public SpaceItemDecoration(int segmentSpace) {
		mSegmentSpace = segmentSpace;
	}

	public void setSegmentSpace(int segmentSpace) {
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

		PageAdapter adapter = (PageAdapter) parent.getAdapter();
		if (adapter == null) {
			return;
		}

		Segment segment = adapter.getItem(currentPosition);

		int segmentSpace = mSegmentSpace;
		if (segment instanceof ViewSegment) {
			segmentSpace = ((ViewSegment) segment).getVerticalSpace();
			if (segmentSpace < 0) {
				segmentSpace = mSegmentSpace;
			}
		}

		outRect.set(0, 0, 0, segmentSpace);
	}
}
