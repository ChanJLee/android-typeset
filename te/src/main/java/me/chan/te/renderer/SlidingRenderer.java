package me.chan.te.renderer;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import me.chan.te.R;
import me.chan.te.text.Document;
import me.chan.te.text.Figure;
import me.chan.te.text.Page;
import me.chan.te.text.Paragraph;
import me.chan.te.text.Segment;

public class SlidingRenderer extends Renderer {

	private final Adapter mAdapter;

	public SlidingRenderer(TeView viewGroup, RenderOption renderOption) {
		super(viewGroup.getContext(), renderOption);
		Context context = viewGroup.getContext();
		RecyclerView impl = new RecyclerView(context);
		impl.setClipToPadding(false);
		impl.setClipChildren(false);
		impl.setLayoutManager(new LinearLayoutManager(context));
		viewGroup.addView(impl, new TeView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		mAdapter = new Adapter();
		impl.setAdapter(mAdapter);
	}

	@Override
	protected void onClear() {
		mAdapter.clear();
	}

	@Override
	protected void onRenderer(Document document) {
		Page page = document.getPage(0);
		mAdapter.render(page);
	}

	@Override
	protected void onError(Throwable throwable) {
		Toast.makeText(getContext(), "渲染异常", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onRefresh(RenderOption renderOption) {
		mAdapter.notifyDataSetChanged();
	}

	public class Adapter extends RecyclerView.Adapter<Renderer> {
		private static final int TYPE_PARAGRAPH = 1;
		private static final int TYPE_FIGURE = 2;

		private Page mPage;

		@NonNull
		@Override
		public Renderer onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
			if (type == TYPE_PARAGRAPH) {
				return new ParagraphRenderer(getLayoutInflater().inflate(R.layout.me_chan_te_text, viewGroup, false));
			}

			return new FigureRenderer(getLayoutInflater().inflate(R.layout.me_chan_te_figure, viewGroup, false));
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

		public void render(Page page) {
			mPage = page;
			notifyDataSetChanged();
		}
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

	// TODO 提出来
	private class ParagraphRenderer extends Renderer<Paragraph> {

		private ParagraphView mParagraphView;

		ParagraphRenderer(@NonNull View itemView) {
			super(itemView);
		}

		@Override
		protected void onCreate(View view) {
			mParagraphView = (ParagraphView) itemView;
		}

		@Override
		public void render(Paragraph data) {
			mParagraphView.render(
					data,
					getTextPaint(),
					getRenderOption());
		}
	}

	class FigureRenderer extends Renderer<Figure> {
		private ImageView mIvFigure;
		private TextView mTvDesc;

		FigureRenderer(View view) {
			super(view);
		}

		@Override
		protected void onCreate(View view) {
			mIvFigure = findViewById(R.id.image);
			mTvDesc = findViewById(R.id.desc);
		}

		@Override
		public void render(Figure figure) {
			float width = figure.getWidth();
			float height = figure.getHeight();
			mIvFigure.setMaxWidth((int) width);
			mIvFigure.setMaxHeight((int) height);

			getImageLoader().uri(figure.getUrl())
					.into(mIvFigure);

			String description = figure.getDescription();
			if (TextUtils.isEmpty(description)) {
				mTvDesc.setVisibility(View.GONE);
				return;
			}
			mTvDesc.setText(description);
		}
	}
}
