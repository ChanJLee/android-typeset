package com.shanbay.lib.texas.renderer;

import android.content.Context;
import android.text.TextPaint;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shanbay.lib.texas.annotations.Hidden;
import com.shanbay.lib.log.Log;
import com.shanbay.lib.texas.measurer.Measurer;
import com.shanbay.lib.texas.text.Document;
import com.shanbay.lib.texas.text.OnClickedListener;

@Hidden
class SlidingRenderer extends Renderer {

	private final TexasAdapter mAdapter;
	private TexasRecyclerView mImpl;
	private SpaceItemDecoration mSpaceItemDecoration;
	private LinearLayoutManager mLinearLayoutManager;
	private TexasView mTexasView;

	public SlidingRenderer(final TexasView viewGroup, RenderOption renderOption) {
		super(viewGroup, renderOption);
		mTexasView = viewGroup;
		Context context = viewGroup.getContext();
		mImpl = new TexasRecyclerView(context);
		mImpl.setClipToPadding(false);
		mImpl.setClipChildren(false);
		mImpl.setOnClickedListener(new OnClickedListener() {
			@Override
			public void onClicked(float x, float y) {
				mTexasView.notifyClicked(x, y);
			}
		});
		mSpaceItemDecoration = new SpaceItemDecoration(renderOption.getSegmentSpace());
		mImpl.addItemDecoration(mSpaceItemDecoration);
		mImpl.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
				mTexasView.notifyScrolled(dx, dy);
			}
		});
		mLinearLayoutManager = new TexasLinearLayoutManager(context);
		mImpl.setLayoutManager(mLinearLayoutManager);
		viewGroup.addView(mImpl,
				new TexasView.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT)
		);
		mAdapter = new TexasAdapter(getLayoutInflater(), getImageLoader());
		mImpl.setAdapter(mAdapter);
	}

	@Override
	protected void onStart() {
		d("on start");
		mAdapter.clear();
	}

	@Override
	protected void onRenderer(Document document,
							  Measurer measurer,
							  TextPaint textPaint,
							  RenderOption renderOption) {
		mAdapter.render(
				document,
				textPaint,
				renderOption,
				measurer
		);
		int index = document.getFocusSegmentIndex();
		d("render scroll to: " + index);
		if (index > 0) {
			mImpl.scrollToPosition(document.getFocusSegmentIndex());
		}
	}

	@Override
	protected void onError(Throwable throwable) {
		Log.w("SlidingTexasRenderer", throwable);
	}

	@Override
	protected void onRefresh(TextPaint textPaint, RenderOption renderOption) {
		d("refresh");
		mSpaceItemDecoration.setSegmentSpace(renderOption.getSegmentSpace());
		mAdapter.update(textPaint, renderOption);
	}

	@Override
	Selection getSelection() {
		return mAdapter.getCurrentSelection();
	}

	@Override
	int getFirstVisibleSegmentIndex() {
		return mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
	}

	private static void d(String msg) {
		Log.d("SlidingTexasRenderer", msg);
	}
}
