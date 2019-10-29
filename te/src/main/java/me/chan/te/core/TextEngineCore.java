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
	private static final int MSG_START = 1;
	private static final int MSG_FINISHED = 2;
	private static final int MSG_FAILURE = 2;

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
			sendMsg(MSG_FAILURE, new IllegalArgumentException("width must be more than 0"));
			return;
		}
		cancel();

		mTask = mExecutor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					sendMsg(MSG_START, null);
					CharSequence content = source.open();
					typeset(content, width);
				} catch (Throwable throwable) {
					sendMsg(MSG_FAILURE, throwable);
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
		i("typeset, width, " + width +
				" strategy: " + mBreakStrategy +
				" text size: " + mTextPaint.getTextSize());

		mWidth = width;
		mContent = content;

		// recycle memory
		recycle();

		// parse
		long timestamp = SystemClock.elapsedRealtime();
		mSegments = mParser.parse(content, mMeasurer, Hypher.getInstance(), mOption);
		i("parse used time: " + (SystemClock.elapsedRealtime() - timestamp) + " segments: " + mSegments.size());
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

		i("typeset used time: " + (SystemClock.elapsedRealtime() - timestamp) + " paragraph size:" + mParagraphs.size());
		i("is thread interrupt: " + thread.isInterrupted());

		// call listener
		sendMsg(MSG_FINISHED, mParagraphs);
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

		mTask = mExecutor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					sendMsg(MSG_START, null);
					typeset(mContent, mWidth);
				} catch (Throwable throwable) {
					sendMsg(MSG_FAILURE, throwable);
				}
			}
		});
		d("refresh: " + mTask);
	}

	private void sendMsg(int what, Object o) {
		if (mHandler == null) {
			return;
		}

		if (o == null) {
			mHandler.sendEmptyMessage(what);
			return;
		}

		Message message = Message.obtain();
		message.what = what;
		message.obj = o;
		mHandler.sendMessage(message);
	}

	public void release() {
		cancel();
		mListener = null;
		mParagraphs = null;
		mSegments = null;
		mHandler.removeCallbacksAndMessages(null);
		mHandler = null;
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

	private static void i(String msg) {
		Log.i("TextEngineCore", msg);
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
			if (mListener == null) {
				return;
			}

			if (msg.what == MSG_FINISHED) {
				mListener.onSuccess((List<Paragraph>) msg.obj);
			} else if (msg.what == MSG_FAILURE) {
				mListener.onFailure((Throwable) msg.obj);
			} else if (msg.what == MSG_START) {
				mListener.onStart();
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
