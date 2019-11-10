package me.chan.te.renderer;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextPaint;

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
import me.chan.te.text.Line;
import me.chan.te.text.Page;
import me.chan.te.text.Paragraph;
import me.chan.te.text.Segment;
import me.chan.te.typesetter.ParagraphTypesetterImpl;

class TextEngineCore {
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
	private int mHeight = 0;
	private ExecutorService mExecutor = Executors.newSingleThreadExecutor();

	private Document mDocument;
	private Future<?> mTask;
	private BreakStrategy mBreakStrategy = BreakStrategy.BALANCED;
	private Renderer mRenderer;
	private ParagraphTypesetterImpl mTypesetter;
	private RenderOption mRenderOption;


	TextEngineCore(Renderer renderer, RenderOption renderOption) {
		mTextPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
		mTextPaint.setTextSize(renderOption.getTextSize());
		mMeasurer = new AndroidMeasurer(mTextPaint);
		mOption = new Option(mMeasurer);
		mHandler = new H(Looper.getMainLooper());
		mParser = new TextParser();
		mTypesetter = new ParagraphTypesetterImpl();
		mRenderer = renderer;
		mRenderOption = renderOption;
	}

	TextPaint getTextPaint() {
		return mTextPaint;
	}

	void setParser(Parser<?> parser) {
		mParser = parser;
		reload();
	}

	/**
	 * typeset content
	 *
	 * @param source source
	 * @param width  width, must be > 0
	 */
	void typeset(final Source source, final int width, final int height) {
		if (width <= 0 || height <= 0) {
			sendMsg(MSG_FAILURE, new IllegalArgumentException("width and height must be large than 0"));
			return;
		}
		cancel();

		mTask = mExecutor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					sendMsg(MSG_START, null);
					Object content = source.open();
					typeset(content, width, height);
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
		i("typeset, width: " + width +
				"height: " + height +
				" strategy: " + mBreakStrategy +
				" text size: " + mTextPaint.getTextSize());

		mWidth = width;
		mHeight = height;
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

		for (int i = 0; i < size && !thread.isInterrupted(); ++i) {
			Segment segment = mDocument.getSegment(i);
			if (segment instanceof Figure) {
				typesetFigure((Figure) segment, mWidth);
			} else if (segment instanceof Paragraph) {
				mTypesetter.typeset((Paragraph) segment, lineAttributes, mBreakStrategy);
			} else {
				continue;
			}

			typesetPage(mDocument, segment, width, height);
		}

		i("typeset used time: " + (SystemClock.elapsedRealtime() - timestamp));
		i("is thread interrupt: " + thread.isInterrupted());

		// call listener
		sendMsg(MSG_FINISHED, mDocument);
	}

	private void typesetPage(Document document, Segment segment, float width, float height) {
		int pageSize = document.getPageCount();
		Page currentPage;
		if (pageSize == 0) {
			currentPage = Page.obtain();
			document.addPage(currentPage);
		} else {
			currentPage = document.getPage(pageSize - 1);
		}

		if (mRenderOption.getRendererMode() == RendererMode.SLIDING) {
			currentPage.addSegment(segment);
			currentPage.setWidth(width);
			currentPage.setHeight(-1);
			return;
		}

		float nextPageHeight = currentPage.getHeight();
		if (nextPageHeight != 0) {
			// 这里可以区分不同类型 选择不同的垂直方向偏移
			nextPageHeight += mRenderOption.getSegmentSpace();
		}

		float currentSegmentHeight = getSegmentHeight(segment);
		nextPageHeight += currentSegmentHeight;

		// 当前页排不下
		// 但是要注意当前页啥东西都没有都排不下的情况，这时候强行塞到当前页
		if (nextPageHeight > height && currentPage.getSegmentCount() != 0) {
			currentPage = Page.obtain();
			document.addPage(currentPage);
			currentPage.setWidth(width);
			currentPage.setHeight(currentSegmentHeight);
			currentPage.addSegment(segment);
		} else {
			currentPage.addSegment(segment);
			currentPage.setWidth(width);
			currentPage.setHeight(nextPageHeight);
		}
	}

	private float getSegmentHeight(Segment segment) {
		if (segment instanceof Figure) {
			Figure figure = (Figure) segment;
			return figure.getHeight();
		}

		if (segment instanceof Paragraph) {
			float height = 0;
			Paragraph paragraph = (Paragraph) segment;
			int size = paragraph.getLineCount();
			if (size != 0) {
				height += ((size - 1) * mRenderOption.getLineSpace());
			}
			for (int i = 0; i < size; ++i) {
				Line line = paragraph.getLine(i);
				height += line.getLineHeight();
			}

			return height;
		}

		throw new IllegalArgumentException("unknown segment type");
	}

	private void typesetFigure(Figure figure, float lineWidth) {
		float width = figure.getWidth();
		float height = figure.getHeight();

		float ratio = Figure.DEFAULT_RATIO;
		if (width > 0 && height > 0) {
			ratio = width / height;
		}

		figure.setWidth(lineWidth);
		figure.setHeight(lineWidth / ratio);
	}

	private LineAttributes createLineAttributes(float width) {
		LineAttributes.Attribute defaultAttribute = new LineAttributes.Attribute(width, Gravity.LEFT, mOption.getSpaceWidth());
		LineAttributes lineAttributes = new LineAttributes(defaultAttribute);

		if (mRenderOption.isIndentEnable()) {
			lineAttributes.add(0, new LineAttributes.Attribute(
					width - mOption.getIndentWidth(),
					Gravity.RIGHT, mOption.getSpaceWidth()
			));
		}

		return lineAttributes;
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

	void release() {
		cancel();
		mRenderer = null;
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

	void reload(RenderOption renderOption) {
		mTextPaint.setColor(renderOption.getTextColor());
		mTextPaint.setTypeface(renderOption.getTypeface());
		mTextPaint.setTextSize(renderOption.getTextSize());

		mMeasurer.refresh(mTextPaint);
		mOption.refresh(mMeasurer);
		mRenderOption = renderOption;

		reload();
	}

	private void reload() {
		if (mContent == null || mWidth < 0 || mHeight < 0) {
			return;
		}

		cancel();
		mTask = mExecutor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					sendMsg(MSG_START, null);
					typeset(mContent, mWidth, mHeight);
				} catch (Throwable throwable) {
					sendMsg(MSG_FAILURE, throwable);
				}
			}
		});
		d("reload: " + mTask);
	}

	private class H extends Handler {
		public H(Looper mainLooper) {
			super(mainLooper);
		}

		@Override
		public void handleMessage(Message msg) {
			d("typeset paragraphs");
			if (mRenderer == null) {
				return;
			}

			if (msg.what == MSG_FINISHED) {
				mRenderer.render((Document) msg.obj);
			} else if (msg.what == MSG_FAILURE) {
				mRenderer.error((Throwable) msg.obj);
			} else if (msg.what == MSG_START) {
				mRenderer.clear();
			}
		}
	}

	private static void d(String msg) {
		Log.d("TextEngineCore", msg);
	}

	private static void i(String msg) {
		Log.i("TextEngineCore", msg);
	}
}
