package me.chan.texas.renderer;

import android.content.Context;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.chan.texas.R;
import me.chan.texas.image.ImageLoader;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.text.Figure;
import me.chan.texas.text.Foot;
import me.chan.texas.text.OnClickedListener;
import me.chan.texas.text.Page;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.Renderer> {
	private static final int TYPE_PARAGRAPH = 1;
	private static final int TYPE_FIGURE = 2;
	private static final int TYPE_FOOT = 3;

	private Page mPage;
	private LayoutInflater mLayoutInflater;
	private ImageLoader mImageLoader;
	private TextPaint mTextPaint;
	private RenderOption mRenderOption;
	private OnTextSelectedListener mOnTextSelectedListener;
	private Measurer mMeasurer;

	public PageAdapter(LayoutInflater layoutInflater, ImageLoader imageLoader) {
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
			return new FootRenderer(mLayoutInflater.inflate(R.layout.me_chan_te_foot, viewGroup, false));
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onBindViewHolder(@NonNull Renderer renderer, int position) {
		renderer.render(mPage.getSegment(position));
	}

	public void setOnTextSelectedListener(OnTextSelectedListener onTextSelectedListener) {
		mOnTextSelectedListener = onTextSelectedListener;
	}

	@Override
	public int getItemViewType(int position) {
		Segment segment = mPage.getSegment(position);
		if (segment instanceof Paragraph) {
			return TYPE_PARAGRAPH;
		} else if (segment instanceof Figure) {
			return TYPE_FIGURE;
		} else if (segment instanceof Foot) {
			return TYPE_FOOT;
		}

		throw new RuntimeException("unknown segment type");
	}

	@Override
	public int getItemCount() {
		return mPage == null ? 0 : mPage.getSegmentCount();
	}

	public void clear() {
		mPage = null;
		notifyDataSetChanged();
	}

	public void render(Page page, TextPaint textPaint, RenderOption renderOption, Measurer measurer) {
		mPage = page;
		mTextPaint = textPaint;
		mRenderOption = renderOption;
		mMeasurer = measurer;
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
			mParagraphView.setOnTextSelectedListener(new ParagraphView.OnTextSelectedListener() {
				@Override
				public void onTextSelected() {
					if (mOnTextSelectedListener != null) {
						mOnTextSelectedListener.onTextSelected();
					}
				}
			});
		}

		@Override
		public void render(Paragraph data) {
			mParagraphView.render(
					data,
					mTextPaint,
					mRenderOption,
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

	class FootRenderer extends Renderer<Foot> {

		private View mBtnFoot;
		private Foot mData;

		FootRenderer(@NonNull View itemView) {
			super(itemView);
		}

		@Override
		protected void onCreate(View view) {
			mBtnFoot = view.findViewById(R.id.finish);
			mBtnFoot.setOnTouchListener(new SingleClickOnTouchListener(view.getContext()) {
				@Override
				protected void onClicked(float x, float y) {
					if (mData == null) {
						return;
					}

					OnClickedListener onClickedListener = mData.getOnClickedListener();
					if (onClickedListener != null) {
						onClickedListener.onClicked(x, y);
					}
				}
			});
		}

		@Override
		public void render(final Foot data) {
			mData = data;
		}
	}

	public interface OnTextSelectedListener {
		void onTextSelected();
	}
}