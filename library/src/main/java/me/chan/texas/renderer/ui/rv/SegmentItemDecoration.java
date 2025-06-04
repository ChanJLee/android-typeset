package me.chan.texas.renderer.ui.rv;

import android.graphics.Rect;
import android.view.View;

import me.chan.texas.renderer.ui.TexasRendererAdapter;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.utils.TexasUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SegmentItemDecoration extends RecyclerView.ItemDecoration {
	private final TexasRendererAdapter mAdapter;
	private final me.chan.texas.misc.Rect mRect = new me.chan.texas.misc.Rect();

	public SegmentItemDecoration(TexasRendererAdapter adapter) {
		mAdapter = adapter;
	}

	@Override
	public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
		if (view instanceof SegmentItemFragmentLayout) {
			SegmentItemFragmentLayout layout = (SegmentItemFragmentLayout) view;
			if (layout.isDisableDecoration()) {
				return;
			}
		}

		int position = parent.getChildAdapterPosition(view);
		if (position < 0 || position >= mAdapter.getItemCount()) {
			return;
		}

		Segment segment = mAdapter.getItem(position);
		if (segment == null) {
			return;
		}

		if (!(segment instanceof Paragraph)) {
			segment.getRect(mRect);
			TexasUtils.copyRect(outRect, mRect);
		}
	}
}
