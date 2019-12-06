package me.chan.texas.renderer;

import android.content.Context;
import android.text.TextPaint;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import me.chan.texas.log.Log;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.renderer.rv.TexasLinearLayoutManager;
import me.chan.texas.renderer.rv.TexasRecyclerView;
import me.chan.texas.text.Document;
import me.chan.texas.text.OnClickedListener;

public class SlidingRenderer extends Renderer {

	private final PageAdapter mAdapter;
	private TexasRecyclerView mImpl;
	private SpaceItemDecoration mSpaceItemDecoration;
	private LinearLayoutManager mLinearLayoutManager;
	private TexasView mTexasView;

	public SlidingRenderer(TexasView viewGroup, RenderOption renderOption) {
		super(viewGroup, renderOption);
		mTexasView = viewGroup;
		Context context = viewGroup.getContext();
		mImpl = new TexasRecyclerView(context);
		mImpl.setClipToPadding(false);
		mImpl.setClipChildren(false);
		mImpl.setOnClickedListener(new OnClickedListener() {
			@Override
			public void onClicked(float x, float y) {
				Document document = getDocument();
				if (document == null) {
					return;
				}

				OnClickedListener onClickedListener = document.getOnClickedListener();
				if (onClickedListener != null) {
					onClickedListener.onClicked(x, y);
				}
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
		mAdapter = new PageAdapter(getLayoutInflater(), getImageLoader());
		mAdapter.setOnTextSelectedListener(new PageAdapter.OnTextSelectedListener() {
			@Override
			public void onTextSelected(ParagraphSelection paragraphSelection) {
				handleTextSelected(paragraphSelection);
			}
		});
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
		int index = document.getFocusIndex();
		d("render scroll to: " + index);
		if (index > 0) {
			mImpl.scrollToPostion(document.getFocusIndex());
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
	protected void invalidate(int position) {
		d("invalidate position: " + position);
		if (position < 0 || position >= mAdapter.getItemCount()) {
			mAdapter.notifyDataSetChanged();
			return;
		}
		mAdapter.notifyItemChanged(position);
	}

	@Override
	public int getFirstVisibleSegmentIndex() {
		return mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
	}

	private static void d(String msg) {
		Log.d("SlidingTexasRenderer", msg);
	}
}
