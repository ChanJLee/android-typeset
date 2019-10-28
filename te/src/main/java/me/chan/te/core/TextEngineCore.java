package me.chan.te.core;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import me.chan.te.typesetter.Typesetter;

public class TextEngineCore {
	private static final int DEFAULT_TEXT_SIZE = 18;
	private static final int MSG_FINISHED = 1;

	private TextPaint mTextPaint;
	private Option mOption;
	private AndroidMeasurer mMeasurer;
	private Handler mHandler;
	private Parser mParser;
	private CharSequence mContent;
	private int mWidth = 0;
	private ExecutorService mExecutor = Executors.newSingleThreadExecutor();

	private List<Paragraph> mParagraphs;
	private List<Segment> mSegments;
	private Future<?> mTask;
	private BreakStrategy mBreakStrategy = BreakStrategy.BALANCED;
	private Listener mListener;
	private Typesetter mTypesetter;

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
		mHandler = new H(Looper.getMainLooper());
		mParser = new TextParser();
		mTypesetter = new CoreTypesetter();
	}

	public Option getOption() {
		return mOption;
	}

	public TextPaint getTextPaint() {
		return mTextPaint;
	}

	public void setListener(Listener listener) {
		mListener = listener;
	}

	public void setParser(Parser parser) {
		mParser = parser;
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

		cancel();

		if (mListener != null) {
			mListener.onStart();
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

	// TODO 异常安全保障
	private void typeset(final CharSequence content, final int width) {
		d("typeset, width, " + width +
				" strategy: " + mBreakStrategy +
				" text size: " + mTextPaint.getTextSize());

		mWidth = width;
		mContent = content;

		// recycle memory
		recycle();

		// parse
		long timestamp = SystemClock.elapsedRealtime();
		mSegments = mParser.parser(content, mMeasurer, Hypher.getInstance(), mOption);
		d("parse used time: " + (SystemClock.elapsedRealtime() - timestamp) + " segments: " + mSegments.size());
		timestamp = SystemClock.elapsedRealtime();

		mParagraphs = new ArrayList<>();

		// typeset
		int size = mSegments.size();
		final Thread thread = Thread.currentThread();
		LineAttributes lineAttributes = createLineAttributes(width);
		for (int i = 0; i < size && !thread.isInterrupted(); ++i) {
			Segment segment = mSegments.get(i);
			Paragraph paragraph = mTypesetter.typeset(segment, lineAttributes, mBreakStrategy);
			mParagraphs.add(paragraph);
		}

		d("typeset used time: " + (SystemClock.elapsedRealtime() - timestamp) + " paragraph size:" + mParagraphs.size());
		d("is thread interrupt: " + thread.isInterrupted());

		// call listener
		mHandler.sendEmptyMessage(MSG_FINISHED);
	}

	private LineAttributes createLineAttributes(float width) {
		LineAttributes.Attribute defaultAttribute = new LineAttributes.Attribute(width, Gravity.LEFT,
				(int) mOption.getLineSpacing(), mOption.getSpaceWidth());
		LineAttributes lineAttributes = new LineAttributes(defaultAttribute);
		lineAttributes.add(0, new LineAttributes.Attribute(
				width - mOption.getIndentWidth(),
				Gravity.RIGHT,
				(int) mOption.getLineSpacing(),
				mOption.getSpaceWidth()
		));
		return lineAttributes;
	}

	private void recycle() {
		for (int i = 0; mParagraphs != null && i < mParagraphs.size(); ++i) {
			mParagraphs.get(i).recycle();
		}

		for (int i = 0; mSegments != null && i < mSegments.size(); ++i) {
			mSegments.get(i).recycle();
		}
	}

	private void refresh() {
		if (mContent == null || mWidth < 0) {
			return;
		}

		cancel();

		if (mListener != null) {
			mListener.onStart();
		}

		mTask = mExecutor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					typeset(mContent, mWidth);
				} catch (Throwable throwable) {
					handleError(throwable);
				}
			}
		});
		d("refresh: " + mTask);
	}

	private void handleError(Throwable throwable) {
		if (mListener != null) {
			mListener.onFailure(throwable);
		}
	}

	public void release() {
		mListener = null;
		mParagraphs = null;
		mSegments = null;
		mHandler.removeCallbacksAndMessages(null);
		cancel();
	}

	private void cancel() {
		if (mTask != null) {
			d("cancel task: " + mTask);
			mTask.cancel(true);
		}
	}

	public void setTextSize(float textSize) {
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
		Log.d("TextEngineCore", msg);
	}

	public void setBreakStrategy(BreakStrategy breakStrategy) {
		d("set break strategy: " + breakStrategy);
		mBreakStrategy = breakStrategy;
		refresh();
	}

	public void setTypeface(Typeface typeface) {
		mTextPaint.setTypeface(typeface);
		mMeasurer.refresh(mTextPaint);
		mOption.refresh(mMeasurer);
		refresh();
	}

	public void setTextColor(int color) {
		mTextPaint.setColor(color);
	}

	private class H extends Handler {
		public H(Looper mainLooper) {
			super(mainLooper);
		}

		@Override
		public void handleMessage(Message msg) {
			d("typeset paragraphs");
			if (mListener != null) {
				mListener.onSuccess(mParagraphs);
			}
		}
	}

	public interface Listener {
		/**
		 * 加载前调用，这时候需要清空视图
		 */
		void onStart();

		/**
		 * 成功的时候调用
		 *
		 * @param paragraphs 当前的文本
		 */
		void onSuccess(List<Paragraph> paragraphs);

		/**
		 * @param throwable 异常信息
		 */
		void onFailure(Throwable throwable);
	}
}
