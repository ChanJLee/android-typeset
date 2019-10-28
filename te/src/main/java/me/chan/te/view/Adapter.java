package me.chan.te.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import me.chan.te.R;
import me.chan.te.core.TextEngineCore;
import me.chan.te.data.Paragraph;
import me.chan.te.parser.Parser;
import me.chan.te.source.Source;
import me.chan.te.text.BreakStrategy;

public class Adapter extends RecyclerView.Adapter<TexViewHolder> {
	private static final int ACTION_REDRAW = 1;
	private static final int ACTION_ENABLE_DEBUG = 2;

	private LayoutInflater mLayoutInflater;
	private boolean mDebugMode;
	private TextEngineCore mTextEngineCore;
	private List<Paragraph> mParagraphs;

	public Adapter(final Context context) {
		mLayoutInflater = LayoutInflater.from(context);
		mTextEngineCore = new TextEngineCore(context);
		mTextEngineCore.setListener(new TextEngineCore.Listener() {
			@Override
			public void onStart() {
				mParagraphs = null;
				notifyDataSetChanged();
			}

			@Override
			public void onSuccess(List<Paragraph> paragraphs) {
				mParagraphs = paragraphs;
				notifyDataSetChanged();
			}

			@Override
			public void onFailure(Throwable throwable) {
				/* do nothing */
				Toast.makeText(context, String.valueOf(throwable.getMessage()), Toast.LENGTH_SHORT).show();
			}
		});
	}

	@NonNull
	@Override
	public TexViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
		return new TexViewHolder(mLayoutInflater.inflate(R.layout.me_chan_te_text, viewGroup, false));
	}

	@Override
	public void onBindViewHolder(@NonNull TexViewHolder texViewHolder, int position) {
		texViewHolder.mParagraphView.setDebugMode(mDebugMode);
		texViewHolder.mParagraphView.render(mParagraphs.get(position), mTextEngineCore.getTextPaint(), mTextEngineCore.getOption());
	}

	@Override
	public void onBindViewHolder(@NonNull TexViewHolder holder, int position, @NonNull List<Object> payloads) {
		if (payloads == null || payloads.isEmpty()) {
			super.onBindViewHolder(holder, position, payloads);
			return;
		}

		Object o = payloads.get(0);
		if (!(o instanceof Integer)) {
			super.onBindViewHolder(holder, position, payloads);
			return;
		}

		int action = (int) o;
		if (action == ACTION_ENABLE_DEBUG) {
			holder.mParagraphView.setDebugMode(mDebugMode);
		} else if (action == ACTION_REDRAW) {
			holder.mParagraphView.invalidate();
		}
	}

	@Override
	public int getItemCount() {
		return mParagraphs == null ? 0 : mParagraphs.size();
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
}