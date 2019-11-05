package me.chan.te.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.chan.te.R;
import me.chan.te.config.Option;
import me.chan.te.core.TextEngineCore;
import me.chan.te.image.ImageLoader;
import me.chan.te.parser.Parser;
import me.chan.te.source.Source;
import me.chan.te.text.BreakStrategy;
import me.chan.te.text.Document;
import me.chan.te.text.Figure;
import me.chan.te.text.Paragraph;
import me.chan.te.text.Segment;

public class Adapter extends RecyclerView.Adapter<Adapter.Renderer> {
	private static final int TYPE_PARAGRAPH = 1;
	private static final int TYPE_FIGURE = 2;

	private static final int ACTION_REDRAW = 1;
	private static final int ACTION_ENABLE_DEBUG = 2;

	private LayoutInflater mLayoutInflater;
	private boolean mDebugMode;
	private TextEngineCore mTextEngineCore;
	private Document mDocument;
	private ImageLoader mImageLoader;

	public Adapter(final Context context) {
		mLayoutInflater = LayoutInflater.from(context);
		mTextEngineCore = new TextEngineCore(context);
		mTextEngineCore.setListener(new TextEngineCore.Listener() {
			@Override
			public void onStart() {
				mDocument = null;
				notifyDataSetChanged();
			}

			@Override
			public void onSuccess(Document document) {
				mDocument = document;
				notifyDataSetChanged();
			}

			@Override
			public void onFailure(Throwable throwable) {
				/* do nothing */
				Toast.makeText(context, String.valueOf(throwable.getMessage()), Toast.LENGTH_SHORT).show();
			}
		});
		mImageLoader = new ImageLoader(context);
	}

	@NonNull
	@Override
	public Adapter.Renderer onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
		if (type == TYPE_PARAGRAPH) {
			return new ParagraphRenderer(mLayoutInflater.inflate(R.layout.me_chan_te_text, viewGroup, false));
		}

		return new FigureRenderer(mLayoutInflater.inflate(R.layout.me_chan_te_figure, viewGroup, false));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onBindViewHolder(@NonNull Adapter.Renderer renderer, int position) {
		renderer.render(mDocument.getSegment(position));
	}

	@Override
	public void onBindViewHolder(@NonNull Adapter.Renderer renderer, int position, @NonNull List<Object> payloads) {
		if (payloads == null || payloads.isEmpty()) {
			super.onBindViewHolder(renderer, position, payloads);
			return;
		}

		Object o = payloads.get(0);
		if (!(o instanceof Integer)) {
			super.onBindViewHolder(renderer, position, payloads);
			return;
		}

		if (!(renderer instanceof ParagraphRenderer)) {
			super.onBindViewHolder(renderer, position, payloads);
			return;
		}

		ParagraphRenderer paragraphRenderer = (ParagraphRenderer) renderer;

		int action = (int) o;
		if (action == ACTION_ENABLE_DEBUG) {
			paragraphRenderer.mParagraphView.setDebugMode(mDebugMode);
		} else if (action == ACTION_REDRAW) {
			paragraphRenderer.mParagraphView.invalidate();
		}
	}

	@Override
	public int getItemViewType(int position) {
		Segment segment = mDocument.getSegment(position);
		return segment instanceof Paragraph ? TYPE_PARAGRAPH : TYPE_FIGURE;
	}

	@Override
	public int getItemCount() {
		return mDocument == null ? 0 : mDocument.getCount();
	}

	void render(final Source source, final int width) {
		mTextEngineCore.typeset(source, width);
	}

	void setDebugMode(boolean debugMode) {
		mDebugMode = debugMode;
		redraw(ACTION_ENABLE_DEBUG);
	}

	boolean isDebugMode() {
		return mDebugMode;
	}

	void setParser(Parser parser) {
		mTextEngineCore.setParser(parser);
	}

	void setTextSize(float textSize) {
		mTextEngineCore.setTextSize(textSize);
	}

	void setBreakStrategy(BreakStrategy breakStrategy) {
		mTextEngineCore.setBreakStrategy(breakStrategy);
	}

	void setTypeface(Typeface typeface) {
		mTextEngineCore.setTypeface(typeface);
	}

	private void redraw(int action) {
		notifyItemRangeChanged(0, getItemCount(), action);
	}

	void setTextColor(int color) {
		mTextEngineCore.setTextColor(color);
		redraw(ACTION_REDRAW);
	}

	public void release() {
		mTextEngineCore.release();
	}

	static abstract class Renderer<T> extends RecyclerView.ViewHolder {

		public Renderer(@NonNull View itemView) {
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
		}

		@Override
		public void render(Paragraph data) {
			mParagraphView.setDebugMode(mDebugMode);
			mParagraphView.render(
					data,
					mTextEngineCore.getTextPaint());
		}
	}

	class FigureRenderer extends Adapter.Renderer<Figure> {
		private ImageView mIvFigure;
		private TextView mTvDesc;

		public FigureRenderer(View view) {
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

			mImageLoader.uri(figure.getUrl())
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