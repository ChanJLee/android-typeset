package me.chan.te.renderer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import me.chan.te.text.Document;

class PagingRenderer extends Renderer {

	private Document mDocument;
	private Adapter mAdapter;

	PagingRenderer(TeView viewGroup, RenderOption renderOption) {
		super(viewGroup.getContext(), renderOption);
		ViewPager viewPager = new ViewPager(viewGroup.getContext());
		viewGroup.addView(viewPager, new TeView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		mAdapter = new Adapter();
		viewPager.setAdapter(mAdapter);
	}

	@Override
	protected void onClear() {
		mDocument = null;
		mAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onRenderer(Document document) {
		mDocument = document;
		mAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onError(Throwable throwable) {
		Toast.makeText(getContext(), "渲染异常", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onRefresh(RenderOption renderOption) {
		mAdapter.notifyDataSetChanged();
	}

	private class Adapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mDocument == null ? 0 : mDocument.getPageCount();
		}

		@Override
		public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
			return view == o;
		}

		@NonNull
		@Override
		public Object instantiateItem(@NonNull ViewGroup container, int position) {
			Context context = getContext();
			RecyclerView impl = new RecyclerView(context);
			impl.setClipToPadding(false);
			impl.setClipChildren(false);
			impl.setLayoutManager(new LinearLayoutManager(context));
			container.addView(impl, new TeView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			PageAdapter pageAdapter = new PageAdapter(getLayoutInflater(), getImageLoader());
			impl.setAdapter(pageAdapter);
			pageAdapter.render(mDocument.getPage(position), getTextPaint(), getRenderOption());
			return impl;
		}

		@Override
		public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
			container.removeView((View) object);
		}
	}
}
