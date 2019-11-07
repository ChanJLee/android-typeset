package me.chan.te.core;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextPaint;
import android.util.TypedValue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import me.chan.te.config.LineAttributes;
import me.chan.te.config.Option;
import me.chan.te.hypher.Hypher;
import me.chan.te.log.Log;
import me.chan.te.measurer.AndroidMeasurer;
import me.chan.te.parser.Parser;
import me.chan.te.parser.TextParser;
import me.chan.te.source.Source;
import me.chan.te.source.SourceCloseException;
import me.chan.te.text.BreakStrategy;
import me.chan.te.text.Document;
import me.chan.te.text.Figure;
import me.chan.te.text.Gravity;
import me.chan.te.text.Page;
import me.chan.te.text.Paragraph;
import me.chan.te.text.Segment;
import me.chan.te.typesetter.CoreParagraphTypesetter;

public class TextEngineCore {
	private static final int DEFAULT_TEXT_SIZE = 18;
	private static final int MSG_START = 1;
	private static final int MSG_FINISHED = 2;
	private static final int MSG_FAILURE = 3;

	private TextPaint mTextPaint;
	private Option mOption;
	private AndroidMeasurer mMeasurer;
	private Handler mHandler;
	private Parser mParser;
	private Object mContent;
	private int mWidth = 0;
	private ExecutorService mExecutor = Executors.newSingleThreadExecutor();

	private Document mDocument;
	private Future<?> mTask;
	private BreakStrategy mBreakStrategy = BreakStrategy.BALANCED;
	private Listener mListener;
	private CoreParagraphTypesetter mTypesetter;
	private boolean mIndentEnable = false;

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
		mTypesetter = new CoreParagraphTypesetter();
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
					Object content = source.open();
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
	@SuppressWarnings("unchecked")
	private void typeset(final Object content, final int width, int height) {
		i("typeset, width, " + width +
				" strategy: " + mBreakStrategy +
				" text size: " + mTextPaint.getTextSize());

		mWidth = width;
		mContent = content;

		// recycle memory
		if (mDocument != null) {
			mDocument.recycle();
		}

		// parse
		long timestamp = SystemClock.elapsedRealtime();
		mDocument = mParser.parse(content, mMeasurer, Hypher.getInstance(), mOption);
		int size = mDocument.getSegmentCount();
		i("parse used time: " + (SystemClock.elapsedRealtime() - timestamp) + " segment size: " + size);
		timestamp = SystemClock.elapsedRealtime();

		// typeset
		final Thread thread = Thread.currentThread();
		LineAttributes lineAttributes = createLineAttributes(width);

		Page page = Page.obtian();
		int height = 0;
		for (int i = 0; i < size && !thread.isInterrupted(); ++i) {
			Segment segment = mDocument.getSegment(i);
			if (segment instanceof Figure) {
				typesetFigure((Figure) segment, lineAttributes);
			} else if (segment instanceof Paragraph) {
				mTypesetter.typeset((Paragraph) segment, lineAttributes, mBreakStrategy);
			} else {
				continue;
			}

			// TODO 排入 page
			page.addSegment(segment);
		}

		i("typeset used time: " + (SystemClock.elapsedRealtime() - timestamp));
		i("is thread interrupt: " + thread.isInterrupted());

		// call listener
		sendMsg(MSG_FINISHED, mDocument);
	}

	private void typesetFigure(Figure figure, LineAttributes lineAttributes) {
		LineAttributes.Attribute attribute = lineAttributes.getDefaultAttribute();

		float lineWidth = attribute.getLineWidth();

		float width = figure.getWidth();
		float height = figure.getHeight();

		if (width >= 0 && height >= 0) {
			if (width > lineWidth) {
				figure.setWidth(lineWidth);
				figure.setHeight(height / width * lineWidth);
			}
			return;
		}

		figure.setWidth(lineWidth);
		figure.setHeight(lineWidth / Figure.DEFAULT_RATIO);
	}

	private LineAttributes createLineAttributes(float width) {
		LineAttributes.Attribute defaultAttribute = new LineAttributes.Attribute(width, Gravity.LEFT, mOption.getSpaceWidth());
		LineAttributes lineAttributes = new LineAttributes(defaultAttribute);

		if (mIndentEnable) {
			lineAttributes.add(0, new LineAttributes.Attribute(
					width - mOption.getIndentWidth(),
					Gravity.RIGHT, mOption.getSpaceWidth()
			));
		}

		return lineAttributes;
	}

	public void setIndentEnable(boolean indentEnable) {
		mIndentEnable = indentEnable;
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
		mDocument = null;
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
				mListener.onSuccess((Document) msg.obj);
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
		 * @param document doc
		 */
		void onSuccess(Document document);

		/**
		 * @param throwable 异常信息
		 */
		void onFailure(Throwable throwable);
	}
}
