package me.chan.te.renderer;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextPaint;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import me.chan.te.text.TextAttribute;
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
	private TextAttribute mTextAttribute;
	private AndroidMeasurer mMeasurer;
	private Handler mHandler;
	private Parser mParser;
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
		updateTextPaint(renderOption);

		mMeasurer = new AndroidMeasurer(mTextPaint);
		mTextAttribute = new TextAttribute(mMeasurer);
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
					Document document = mDocument;
					mDocument = null;
					typeset(content, width, height, document);
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
	private void typeset(final Object content, final int width, int height, Document document) {
		i("typeset, width: " + width +
				"height: " + height +
				" strategy: " + mBreakStrategy +
				" text size: " + mTextPaint.getTextSize());

		mWidth = width;
		mHeight = height;

		// recycle memory
		if (document != null) {
			document.recycle();
		}

		updateLineAttribute(width);

		// parse
		long timestamp = SystemClock.elapsedRealtime();
		try {
			document = mParser.parse(content, mMeasurer, Hypher.getInstance(), mTextAttribute);
		} catch (InterruptedException e) {
			i("interrupted when parse");
			return;
		}

		document.setRaw(content);

		int size = document.getSegmentCount();
		i("parse used time: " + (SystemClock.elapsedRealtime() - timestamp) + " segment size: " + size);
		timestamp = SystemClock.elapsedRealtime();

		// typeset
		final Thread thread = Thread.currentThread();
		for (int i = 0; i < size && !thread.isInterrupted(); ++i) {
			Segment segment = document.getSegment(i);
			if (segment instanceof Figure) {
				typesetFigure((Figure) segment, mWidth);
			} else if (segment instanceof Paragraph) {
				mTypesetter.typeset((Paragraph) segment, mTextAttribute, mBreakStrategy);
			} else {
				continue;
			}

			typesetPage(document, segment, width, height);
		}

		i("typeset used time: " + (SystemClock.elapsedRealtime() - timestamp));
		boolean isInterrupted = thread.isInterrupted();
		i("is thread interrupt when typeset: " + isInterrupted);

		// call listener
		if (!isInterrupted) {
			sendMsg(MSG_FINISHED, document);
		}
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

	private void updateLineAttribute(float width) {
		mTextAttribute.removeAllLineAttribute();

		TextAttribute.LineAttribute defaultAttribute = new TextAttribute.LineAttribute(width, Gravity.LEFT);
		mTextAttribute.setDefaultAttribute(defaultAttribute);

		if (mRenderOption.isIndentEnable()) {
			mTextAttribute.add(0, new TextAttribute.LineAttribute(
					width - mTextAttribute.getIndentWidth(),
					Gravity.RIGHT
			));
		}
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
		i("release");
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
			mTask = null;
		}
	}

	private void updateTextPaint(RenderOption renderOption) {
		mTextPaint.setColor(renderOption.getTextColor());
		mTextPaint.setTypeface(renderOption.getTypeface());
		mTextPaint.setTextSize(renderOption.getTextSize());
	}

	void reload(RenderOption renderOption) {
		mRenderOption = renderOption;
		updateTextPaint(renderOption);

		mMeasurer.refresh(mTextPaint);
		mTextAttribute.refresh(mMeasurer);

		reload();
	}

	private void reload() {
		if (mDocument == null || mWidth < 0 || mHeight < 0) {
			i("reload ignore");
			return;
		}

		cancel();
		mTask = mExecutor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					sendMsg(MSG_START, null);
					Document document = mDocument;
					mDocument = null;
					typeset(document.getRaw(), mWidth, mHeight, document);
				} catch (Throwable throwable) {
					sendMsg(MSG_FAILURE, throwable);
				}
			}
		});
		d("reload: " + mTask);
	}

	private class H extends Handler {
		H(Looper mainLooper) {
			super(mainLooper);
		}

		@Override
		public void handleMessage(Message msg) {
			d("typeset paragraphs, msg what: " + msg.what);
			if (mRenderer == null) {
				return;
			}

			if (msg.what == MSG_FINISHED) {
				mDocument = (Document) msg.obj;
				mRenderer.render(mDocument);
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
