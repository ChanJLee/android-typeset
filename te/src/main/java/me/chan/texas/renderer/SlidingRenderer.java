package me.chan.texas.renderer;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import me.chan.texas.measurer.Measurer;
import me.chan.texas.text.Document;
import me.chan.texas.text.OnClickedListener;
import me.chan.texas.text.Paragraph;

public class SlidingRenderer extends Renderer {

	private final PageAdapter mAdapter;
	private RecyclerViewInternal mImpl;
	private SpaceItemDecoration mSpaceItemDecoration;
	private LinearLayoutManager mLinearLayoutManager;
	private TexasView mTexasView;

	public SlidingRenderer(TexasView viewGroup, RenderOption renderOption) {
		super(viewGroup, renderOption);
		mTexasView = viewGroup;
		Context context = viewGroup.getContext();
		mImpl = new RecyclerViewInternal(context);
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
		mLinearLayoutManager = new LinearLayoutManager(context);
		mImpl.setLayoutManager(mLinearLayoutManager);
		viewGroup.addView(mImpl, new TexasView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		mAdapter = new PageAdapter(getLayoutInflater(), getImageLoader());
		mAdapter.setOnTextSelectedListener(new PageAdapter.OnTextSelectedListener() {
			@Override
			public void onTextSelected(Paragraph paragraph) {
				handleTextSelected(paragraph);
			}
		});
		mImpl.setAdapter(mAdapter);
	}

	@Override
	protected void onStart() {
		mAdapter.clear();
	}

	@Override
	protected void onRenderer(Document document, Measurer measurer) {
		mAdapter.render(document, getTextPaint(), getRenderOption(), measurer);
		mImpl.scrollToPostion(document.getFocusIndex());
	}

	@Override
	protected void onError(Throwable throwable) {
		Toast.makeText(getContext(), "渲染异常", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onRefresh(RenderOption renderOption) {
		mSpaceItemDecoration.setSegmentSpace(renderOption.getSegmentSpace());
		mAdapter.update(getTextPaint(), renderOption);
	}

	@Override
	protected void invalidate() {
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public int getFirstVisibleSegmentIndex() {
		return mLinearLayoutManager.findFirstVisibleItemPosition();
	}
}
