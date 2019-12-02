package me.chan.texas.renderer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import me.chan.texas.measurer.Measurer;
import me.chan.texas.text.Document;
import me.chan.texas.text.OnClickedListener;

class PagingRenderer extends Renderer {

	private Document mDocument;
	private Adapter mAdapter;
	private Measurer mMeasurer;

	PagingRenderer(TexasView viewGroup, RenderOption renderOption) {
		super(viewGroup.getContext(), renderOption);
		ViewPager viewPager = new ViewPager(viewGroup.getContext());
		viewGroup.addView(viewPager, new TexasView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		mAdapter = new Adapter();
		viewPager.setAdapter(mAdapter);
	}

	@Override
	protected void onClear() {
		mDocument = null;
		mAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onRenderer(Document document, Measurer measurer) {
		mDocument = document;
		mMeasurer = measurer;
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

	@Override
	protected void invalidate() {
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
			RenderOption renderOption = getRenderOption();
			Context context = getContext();
			RecyclerViewInternal impl = new RecyclerViewInternal(context);
			impl.setNestedScrollingEnabled(false);
			impl.setClipToPadding(false);
			impl.setClipChildren(false);
			impl.setOnClickedListener(new OnClickedListener() {
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
			LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context) {
				@Override
				public boolean canScrollVertically() {
					return false;
				}
			};
			impl.setLayoutManager(linearLayoutManager);
			impl.addItemDecoration(new SpaceItemDecoration(renderOption.getSegmentSpace()));
			container.addView(impl);
			PageAdapter pageAdapter = new PageAdapter(getLayoutInflater(), getImageLoader());
			pageAdapter.setOnTextSelectedListener(new PageAdapter.OnTextSelectedListener() {
				@Override
				public void onTextSelected() {
					invalidate();
				}
			});
			impl.setAdapter(pageAdapter);
			pageAdapter.render(mDocument.getPage(position), getTextPaint(), renderOption, mMeasurer);
			return impl;
		}

		@Override
		public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getItemPosition(@NonNull Object object) {
			return POSITION_NONE;
		}
	}
}
