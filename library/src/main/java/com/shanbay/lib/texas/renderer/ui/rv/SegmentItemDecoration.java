package com.shanbay.lib.texas.renderer.ui.rv;

import android.graphics.Rect;
import android.view.View;

import com.shanbay.lib.texas.renderer.ui.TexasAdapter;
import com.shanbay.lib.texas.text.Paragraph;
import com.shanbay.lib.texas.text.Segment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SegmentItemDecoration extends RecyclerView.ItemDecoration {
	private final TexasAdapter mAdapter;

	public SegmentItemDecoration(TexasAdapter adapter) {
		mAdapter = adapter;
	}

	@Override
	public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
		if (view.getVisibility() == View.GONE) {
			outRect.set(0, 0, 0, 0);
			return;
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
			segment.getRect(outRect);
		}
	}
}
