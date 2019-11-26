package me.chan.te.renderer;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.chan.te.R;
import me.chan.te.image.ImageLoader;
import me.chan.te.text.Figure;
import me.chan.te.text.Page;
import me.chan.te.text.Paragraph;
import me.chan.te.text.Segment;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.Renderer> {
	private static final int TYPE_PARAGRAPH = 1;
	private static final int TYPE_FIGURE = 2;

	private Page mPage;
	private LayoutInflater mLayoutInflater;
	private ImageLoader mImageLoader;
	private TextPaint mTextPaint;
	private RenderOption mRenderOption;
	private Selection mSelection;

	public PageAdapter(LayoutInflater layoutInflater, ImageLoader imageLoader) {
		mLayoutInflater = layoutInflater;
		mImageLoader = imageLoader;
	}

	public void setSelection(Selection selection) {
		mSelection = selection;
	}

	protected void onHandleSelectionCreated(Selection selection) {
		mSelection = selection;
	}

	@NonNull
	@Override
	public Renderer onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
		if (type == TYPE_PARAGRAPH) {
			return new ParagraphRenderer(mLayoutInflater.inflate(R.layout.me_chan_te_text, viewGroup, false));
		}

		return new FigureRenderer(mLayoutInflater.inflate(R.layout.me_chan_te_figure, viewGroup, false));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onBindViewHolder(@NonNull Renderer renderer, int position) {
		renderer.render(mPage.getSegment(position));
	}

	@Override
	public int getItemViewType(int position) {
		Segment segment = mPage.getSegment(position);
		return segment instanceof Paragraph ? TYPE_PARAGRAPH : TYPE_FIGURE;
	}

	@Override
	public int getItemCount() {
		return mPage == null ? 0 : mPage.getSegmentCount();
	}

	public void clear() {
		mPage = null;
		notifyDataSetChanged();
	}

	public void render(Page page, TextPaint textPaint, RenderOption renderOption) {
		mPage = page;
		mTextPaint = textPaint;
		mRenderOption = renderOption;
		notifyDataSetChanged();
	}

	public void update(TextPaint textPaint, RenderOption renderOption) {
		mTextPaint = textPaint;
		mRenderOption = renderOption;
		notifyDataSetChanged();
	}

	static abstract class Renderer<T> extends RecyclerView.ViewHolder {

		Renderer(@NonNull View itemView) {
			super(itemView);
			onCreate(itemView);
		}

		protected abstract void onCreate(View view);

		public abstract void render(T data);

		protected Context getContext() {
			return itemView.getContext();
		}

		protected final <T extends View> T findViewById(@IdRes int id) {
			return itemView.findViewById(id);
		}
	}

	private class ParagraphRenderer extends Renderer<Paragraph> {

		private ParagraphView mParagraphView;

		ParagraphRenderer(@NonNull View itemView) {
			super(itemView);
		}

		@Override
		protected void onCreate(View view) {
			mParagraphView = (ParagraphView) itemView;
			mParagraphView.setSelectionCreateListener(new ParagraphView.OnSelectionCreateListener() {
				@Override
				public void onSelectionCreated(Selection selection) {
					onHandleSelectionCreated(selection);
				}
			});
		}

		@Override
		public void render(Paragraph data) {
			mParagraphView.render(
					data,
					mTextPaint,
					mRenderOption,
					mSelection);
		}
	}

	class FigureRenderer extends Renderer<Figure> {
		private FigureView mFigureView;
		private TextView mTvDesc;

		FigureRenderer(View view) {
			super(view);
		}

		@Override
		protected void onCreate(View view) {
			mFigureView = findViewById(R.id.image);
			mTvDesc = findViewById(R.id.desc);
		}

		@Override
		public void render(Figure figure) {
			mFigureView.render(mImageLoader, figure);
			String description = figure.getDescription();
			if (TextUtils.isEmpty(description)) {
				mTvDesc.setVisibility(View.GONE);
				return;
			}
			mTvDesc.setText(description);
		}
	}
}