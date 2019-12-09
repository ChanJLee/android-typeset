package me.chan.texas.renderer;

import android.content.Context;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import me.chan.texas.R;
import me.chan.texas.image.ImageLoader;
import me.chan.texas.log.Log;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.text.Document;
import me.chan.texas.text.Figure;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.text.ViewSegment;

class TexasAdapter extends RecyclerView.Adapter<TexasAdapter.Renderer> {
	private static final int TYPE_PARAGRAPH = 1;
	private static final int TYPE_FIGURE = 2;
	private static final int TYPE_VIEW_SEGMENT = 3;

	private Document mDocument;
	private LayoutInflater mLayoutInflater;
	private ImageLoader mImageLoader;
	private TextPaint mTextPaint;
	private RenderOption mRenderOption;
	private Measurer mMeasurer;
	/*
	 * 选中效果属于编辑器内部的状态了，所有直接由adapter管理而不需要通知外部组件
	 * */
	private ParagraphSelection mParagraphSelection;

	TexasAdapter(LayoutInflater layoutInflater, ImageLoader imageLoader) {
		mLayoutInflater = layoutInflater;
		mImageLoader = imageLoader;
	}

	@NonNull
	@Override
	public Renderer onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
		if (type == TYPE_PARAGRAPH) {
			return new ParagraphRenderer(mLayoutInflater.inflate(R.layout.me_chan_te_text, viewGroup, false));
		} else if (type == TYPE_FIGURE) {
			return new FigureRenderer(mLayoutInflater.inflate(R.layout.me_chan_te_figure, viewGroup, false));
		} else {
			return new ViewSegmentRenderer(mLayoutInflater.inflate(R.layout.me_chan_te_view_segment, viewGroup, false));
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onBindViewHolder(@NonNull Renderer renderer, int position) {
		renderer.render(getItem(position));
	}

	@Override
	public int getItemViewType(int position) {
		Segment segment = getItem(position);
		if (segment instanceof Paragraph) {
			return TYPE_PARAGRAPH;
		} else if (segment instanceof Figure) {
			return TYPE_FIGURE;
		} else if (segment instanceof ViewSegment) {
			return TYPE_VIEW_SEGMENT;
		}

		throw new RuntimeException("unknown segment type");
	}

	Segment getItem(int position) {
		return mDocument.getSegment(position);
	}

	@Override
	public int getItemCount() {
		return mDocument == null ? 0 : mDocument.getSegmentCount();
	}

	public void clear() {
		d("clear");
		mDocument = null;
		mParagraphSelection = null;
		notifyDataSetChanged();
	}

	public void render(Document document,
					   TextPaint textPaint,
					   RenderOption renderOption,
					   Measurer measurer) {
		d("render");
		mDocument = document;
		mTextPaint = textPaint;
		mRenderOption = renderOption;
		mMeasurer = measurer;
		// 清空选中
		clearSelectionInternal();
		notifyDataSetChanged();
	}

	void update(TextPaint textPaint, RenderOption renderOption) {
		d("update");
		mTextPaint = textPaint;
		mRenderOption = renderOption;
		notifyDataSetChanged();
	}

	void clearSelection() {
		d("clear selection");
		if (mParagraphSelection == null) {
			return;
		}

		ParagraphSelection paragraphSelection = mParagraphSelection;
		clearSelectionInternal();
		notifySelectionChanged(paragraphSelection);
	}

	private void clearSelectionInternal() {
		if (mParagraphSelection == null) {
			return;
		}

		ParagraphSelection paragraphSelection = mParagraphSelection;
		mParagraphSelection = null;
		paragraphSelection.clearSelection();
	}

	private void notifySelectionChanged(ParagraphSelection paragraphSelection) {
		if (mDocument == null || paragraphSelection == null) {
			return;
		}

		Segment segment = paragraphSelection.getParagraph();
		if (segment == null) {
			return;
		}

		int index = mDocument.indexOf(segment);
		if (index < 0 || index >= getItemCount()) {
			return;
		}
		d("notify item changed: " + index);
		notifyItemChanged(index);
	}

	private void handleParagraphSelected(ParagraphSelection paragraphSelection) {
		// 先取消之前的内容
		clearSelectionInternal();
		notifySelectionChanged(mParagraphSelection);

		// 刷新现在的内容
		mParagraphSelection = paragraphSelection;
		notifySelectionChanged(mParagraphSelection);
	}

	float getSelectedTopEdge() {
		return mParagraphSelection == null ? -1 : mParagraphSelection.getTopEdgeInWindow();
	}

	float getSelectedBottomEdge() {
		return mParagraphSelection == null ? -1 : mParagraphSelection.getBottomEdgeInWindow();
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
			mParagraphView.setOnTextSelectedListener(new ParagraphView.OnSelectedChangedListener() {
				@Override
				public void onTextSelected(TextParagraphSelection selection) {
					handleParagraphSelected(selection);
				}

				@Override
				public void onDrawSelected(DrawableParagraphSelection selection) {
					handleParagraphSelected(selection);
				}
			});
		}

		@Override
		public void render(Paragraph data) {
			ParagraphSelection paragraphSelection = null;
			if (mParagraphSelection != null && mParagraphSelection.getParagraph() == data) {
				paragraphSelection = mParagraphSelection;
			}

			mParagraphView.render(
					data,
					mTextPaint,
					mRenderOption,
					paragraphSelection,
					mMeasurer.getFontTopPadding(),
					mMeasurer.getFontBottomPadding());
		}
	}

	class FigureRenderer extends Renderer<Figure> {
		private FigureView mFigureView;

		FigureRenderer(View view) {
			super(view);
		}

		@Override
		protected void onCreate(View view) {
			mFigureView = findViewById(R.id.image);
		}

		@Override
		public void render(Figure figure) {
			mFigureView.render(mImageLoader, figure);
		}
	}

	class ViewSegmentRenderer extends Renderer<ViewSegment> {

		private FrameLayout mRootView;

		ViewSegmentRenderer(@NonNull View itemView) {
			super(itemView);
		}

		@Override
		protected void onCreate(View view) {
			mRootView = (FrameLayout) view;
		}

		@Override
		public void render(final ViewSegment data) {
			data.attach(mLayoutInflater, mRootView);
			data.render();
		}
	}

	private static void d(String msg) {
		Log.d("TexasAdapter", msg);
	}
}