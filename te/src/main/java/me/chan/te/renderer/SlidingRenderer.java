package me.chan.te.renderer;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Toast;

import me.chan.te.text.Document;
import me.chan.te.text.Page;

public class SlidingRenderer extends Renderer {

	private final PageAdapter mAdapter;

	public SlidingRenderer(TeView viewGroup, RenderOption renderOption) {
		super(viewGroup.getContext(), renderOption);
		Context context = viewGroup.getContext();
		RecyclerView impl = new RecyclerView(context);
		impl.setClipToPadding(false);
		impl.setClipChildren(false);
		impl.addItemDecoration(new SpaceItemDecoration(renderOption.getSegmentSpace()));
		impl.setLayoutManager(new LinearLayoutManager(context));
		viewGroup.addView(impl, new TeView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		mAdapter = new PageAdapter(getLayoutInflater(), getImageLoader());
		impl.setAdapter(mAdapter);
	}

	@Override
	protected void onClear() {
		mAdapter.clear();
	}

	@Override
	protected void onRenderer(Document document) {
		Page page = document.getPage(0);
		mAdapter.render(page, getTextPaint(), getRenderOption());
	}

	@Override
	protected void onError(Throwable throwable) {
		Toast.makeText(getContext(), "渲染异常", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onRefresh(RenderOption renderOption) {
		mAdapter.update(getTextPaint(), renderOption);
	}
}
