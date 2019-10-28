package me.chan.te.view;

import android.content.Context;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import me.chan.te.R;
import me.chan.te.config.LineAttributes;
import me.chan.te.data.Paragraph;
import me.chan.te.data.Segment;
import me.chan.te.hypher.Hypher;
import me.chan.te.log.Log;
import me.chan.te.parser.Parser;
import me.chan.te.source.Source;
import me.chan.te.source.SourceCloseException;
import me.chan.te.text.BreakStrategy;
import me.chan.te.text.Gravity;
import me.chan.te.typesetter.CoreTypesetter;

public class Adapter extends RecyclerView.Adapter<TexViewHolder> {
	private static final int ACTION_REDRAW = 1;
	private static final int ACTION_ENABLE_DEBUG = 2;

	private LayoutInflater mLayoutInflater;
	private int mWidth = -1;
	private boolean mDebugMode;

	public Adapter(Context context) {
		mLayoutInflater = LayoutInflater.from(context);
	}

	@NonNull
	@Override
	public TexViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
		return new TexViewHolder(mLayoutInflater.inflate(R.layout.me_chan_te_text, viewGroup, false));
	}

	@Override
	public void onBindViewHolder(@NonNull TexViewHolder texViewHolder, int position) {
		texViewHolder.mParagraphView.setDebugMode(mDebugMode);
		texViewHolder.mParagraphView.render(mParagraphs.get(position), mTextPaint, mOption);
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
		cancel();
		final List<Paragraph> prevParagraph = mParagraphs;
		final List<Segment> prevSegments = mSegments;

		mParagraphs = null;
		mSegments = null;
		notifyDataSetChanged();

		mTask = mExecutor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					mContent = source.open();
					mWidth = width;
					refreshInternal(prevParagraph, prevSegments);
				} catch (Throwable throwable) {
					w(throwable);
				} finally {
					try {
						source.close();
					} catch (SourceCloseException e) {
						e.printStackTrace();
					}
				}
			}
		});
		d("typeset: " + mTask);
	}

	private void refresh() {
		if (mContent == null || mWidth <= 0) {
			w("content or width is invalid, ignore refresh");
			return;
		}

		cancel();
		final List<Paragraph> prevParagraph = mParagraphs;
		final List<Segment> prevSegments = mSegments;

		mParagraphs = null;
		mSegments = null;
		notifyDataSetChanged();

		mTask = mExecutor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					refreshInternal(prevParagraph, prevSegments);
				} catch (Throwable throwable) {
					w(throwable);
				}
			}
		});
		d("refresh: " + mTask);
	}

	private void cancel() {
		if (mTask != null) {
			d("cancel task: " + mTask);
			mTask.cancel(true);
		}
	}

	private synchronized void refreshInternal(List<Paragraph> prevParagraph,
											  List<Segment> prevSegments) {
		for (int i = 0; prevParagraph != null && i < prevParagraph.size(); ++i) {
			prevParagraph.get(i).recycle();
		}

		for (int i = 0; prevSegments != null && i < prevSegments.size(); ++i) {
			prevSegments.get(i).recycle();
		}

		CoreTypesetter texTypesetter = new CoreTypesetter();
		long timestamp = SystemClock.elapsedRealtime();
		final List<Segment> segments = mParser.parser(mContent, mMeasurer, Hypher.getInstance(), mOption);
		d("parse used time: " + (SystemClock.elapsedRealtime() - timestamp) + " segments: " + segments.size());
		timestamp = SystemClock.elapsedRealtime();

		final List<Paragraph> paragraphs = new ArrayList<>();
		int size = segments.size();
		final Thread thread = Thread.currentThread();
		for (int i = 0; i < size && !thread.isInterrupted(); ++i) {
			Segment segment = segments.get(i);
			LineAttributes.Attribute defaultAttribute = new LineAttributes.Attribute(mWidth, Gravity.LEFT, (int) mOption.getLineSpacing());
			LineAttributes lineAttributes = new LineAttributes(defaultAttribute);
			lineAttributes.add(0, new LineAttributes.Attribute(
					mWidth - mOption.getIndentWidth(),
					Gravity.RIGHT,
					(int) mOption.getLineSpacing()
			));
			Paragraph paragraph = texTypesetter.typeset(segment, lineAttributes, mBreakStrategy);
			paragraphs.add(paragraph);
		}

		d("typeset used time: " + (SystemClock.elapsedRealtime() - timestamp) + " paragraph size:" + paragraphs.size());
		d("is thread interrupt: " + thread.isInterrupted());

		mHandler.post(new Runnable() {
			@Override
			public void run() {
				d("typeset paragraphs");
				mParagraphs = paragraphs;
				mSegments = segments;
				notifyDataSetChanged();
			}
		});
	}

	void setDebugMode(boolean debugMode) {
		mDebugMode = debugMode;
		redraw(ACTION_ENABLE_DEBUG);
	}

	boolean isDebugMode() {
		return mDebugMode;
	}

	void setParser(Parser parser) {
		mParser = parser;
		refresh();
	}

	void setTextSize(float textSize) {
		if (mTextPaint.getTextSize() == textSize) {
			d("text size do not changed, ignore set text size");
			return;
		}

		mTextPaint.setTextSize(textSize);
		mMeasurer.refresh(mTextPaint);
		mOption.refresh(mMeasurer);
		refresh();
	}

	private static void d(String msg) {
		Log.d("TeCore", msg);
	}

	private static void w(String msg) {
		Log.w("TeCore", msg);
	}

	private static void w(Throwable throwable) {
		Log.w("TeCore", throwable);
	}

	void setBreakStrategy(BreakStrategy breakStrategy) {
		mBreakStrategy = breakStrategy;
		refresh();
	}

	void setTypeface(Typeface typeface) {
		mTextPaint.setTypeface(typeface);
		mMeasurer.refresh(mTextPaint);
		mOption.refresh(mMeasurer);
		refresh();
	}

	private void redraw(int action) {
		notifyItemRangeChanged(0, getItemCount(), action);
	}

	void setTextColor(int color) {
		mTextPaint.setColor(color);
		redraw(ACTION_REDRAW);
	}

	public void release() {
		d("call release");
		final List<Paragraph> paragraphs = mParagraphs;
		final List<Segment> segments = mSegments;

		mParagraphs = null;
		mSegments = null;
		notifyDataSetChanged();

		for (int i = 0; paragraphs != null && i < paragraphs.size(); ++i) {
			paragraphs.get(i).recycle();
		}

		for (int i = 0; segments != null && i < segments.size(); ++i) {
			segments.get(i).recycle();
		}
	}
}