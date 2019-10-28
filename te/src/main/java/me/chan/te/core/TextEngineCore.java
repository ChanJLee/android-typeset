package me.chan.te.core;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextPaint;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import me.chan.te.config.LineAttributes;
import me.chan.te.config.Option;
import me.chan.te.data.Paragraph;
import me.chan.te.data.Segment;
import me.chan.te.hypher.Hypher;
import me.chan.te.log.Log;
import me.chan.te.measurer.AndroidMeasurer;
import me.chan.te.parser.Parser;
import me.chan.te.parser.TextParser;
import me.chan.te.source.Source;
import me.chan.te.source.SourceCloseException;
import me.chan.te.text.BreakStrategy;
import me.chan.te.text.Gravity;
import me.chan.te.typesetter.CoreTypesetter;

public class TextEngineCore {
	private static final int DEFAULT_TEXT_SIZE = 18;
	private static final int ACTION_REDRAW = 1;
	private static final int ACTION_ENABLE_DEBUG = 2;

	private TextPaint mTextPaint;
	private Option mOption;
	private AndroidMeasurer mMeasurer;
	private Handler mHandler;
	private Parser mParser;
	private CharSequence mContent;
	private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
	private ExecutorService mGcExecuter = Executors.newSingleThreadExecutor();

	private List<Paragraph> mParagraphs;
	private List<Segment> mSegments;
	private Future<?> mTask;
	private BreakStrategy mBreakStrategy = BreakStrategy.BALANCED;
	private Listener mListener;

	public TextEngineCore(Context context) {
		this(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP,
				DEFAULT_TEXT_SIZE,
				context.getResources().getDisplayMetrics()));
	}

	public TextEngineCore(float textSize) {
		mTextPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
		mTextPaint.setTextSize(textSize);
		mMeasurer = new AndroidMeasurer(mTextPaint);
		mOption = new Option(mMeasurer);
		mHandler = new Handler(Looper.getMainLooper());
		mParser = new TextParser();
	}

	/**
	 * typeset content
	 *
	 * @param source source
	 * @param width  width, must be > 0
	 */
	public void typeset(final Source source, final int width) {
		if (width <= 0) {
			handleError(new IllegalArgumentException("width must be more than 0"));
			return;
		}

		mTask = mExecutor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					CharSequence content = source.open();
					typeset(content, width);
				} catch (Throwable throwable) {
					handleError(throwable);
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

	private void typeset(final CharSequence content, final int width) {
		// todo 外层可能因Paragraph已经被回收发生异常
		CoreTypesetter texTypesetter = new CoreTypesetter();
		long timestamp = SystemClock.elapsedRealtime();
		final List<Segment> segments = mParser.parser(content, mMeasurer, Hypher.getInstance(), mOption);
		d("parse used time: " + (SystemClock.elapsedRealtime() - timestamp) + " segments: " + segments.size());
		timestamp = SystemClock.elapsedRealtime();

		final List<Paragraph> paragraphs = new ArrayList<>();
		int size = segments.size();
		final Thread thread = Thread.currentThread();
		for (int i = 0; i < size && !thread.isInterrupted(); ++i) {
			Segment segment = segments.get(i);
			LineAttributes.Attribute defaultAttribute = new LineAttributes.Attribute(width, Gravity.LEFT, (int) mOption.getLineSpacing());
			LineAttributes lineAttributes = new LineAttributes(defaultAttribute);
			lineAttributes.add(0, new LineAttributes.Attribute(
					width - mOption.getIndentWidth(),
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
				mContent = content;
				release(mParagraphs, mSegments);
				mParagraphs = paragraphs;
				mSegments = segments;
				if (mListener != null) {
					mListener.onSuccess(mParagraphs);
				}
			}
		});
	}

	private void release(final List<Paragraph> paragraphs, final List<Segment> segments) {
		mGcExecuter.submit(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; paragraphs != null && i < paragraphs.size(); ++i) {
					paragraphs.get(i).recycle();
				}

				for (int i = 0; segments != null && i < segments.size(); ++i) {
					segments.get(i).recycle();
				}
			}
		});
	}

	private void handleError(Throwable throwable) {
		if (mListener != null) {
			mListener.onFailure(throwable);
		}
	}

	private static void d(String msg) {
		Log.d("TextEngineCore", msg);
	}

	public interface Listener {
		void onSuccess(List<Paragraph> paragraphs);

		void onFailure(Throwable throwable);
	}
}
