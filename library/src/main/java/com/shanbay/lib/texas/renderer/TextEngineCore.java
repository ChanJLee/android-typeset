package com.shanbay.lib.texas.renderer;

import android.os.SystemClock;
import android.text.TextPaint;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.shanbay.lib.texas.annotations.Hidden;
import com.shanbay.lib.texas.hyphenation.HyphenationPattern;
import com.shanbay.lib.texas.hyphenation.Hyphenation;
import com.shanbay.lib.log.Log;
import com.shanbay.lib.texas.measurer.AndroidMeasurer;
import com.shanbay.lib.texas.measurer.Measurer;
import com.shanbay.lib.texas.parser.Parser;
import com.shanbay.lib.texas.parser.TextParser;
import com.shanbay.lib.texas.source.Source;
import com.shanbay.lib.texas.source.SourceCloseException;
import com.shanbay.lib.texas.text.BreakStrategy;
import com.shanbay.lib.texas.text.Document;
import com.shanbay.lib.texas.text.Figure;
import com.shanbay.lib.texas.text.Gravity;
import com.shanbay.lib.texas.text.HyphenStrategy;
import com.shanbay.lib.texas.text.Paragraph;
import com.shanbay.lib.texas.text.Segment;
import com.shanbay.lib.texas.text.TextAttribute;
import com.shanbay.lib.texas.text.ViewSegment;
import com.shanbay.lib.texas.typesetter.ParagraphTypesetterImpl;

/**
 * 排版核心
 */
@Hidden
class TextEngineCore {
	private static final int MSG_FINISHED = 2;
	private static final int MSG_FAILURE = 3;

	private TextPaint mTextPaint;
	private TextAttribute mTextAttribute;
	private Measurer mMeasurer;
	private ThreadHandler mHandler;
	private Parser mParser;
	private int mWidth = 0;
	private ExecutorService mExecutor = Executors.newSingleThreadExecutor();

	private Document mDocument;
	private Future<?> mTask;
	private Renderer mRenderer;
	private ParagraphTypesetterImpl mTypesetter;
	private RenderOption mRenderOption;

	public TextEngineCore(Renderer renderer,
						  RenderOption renderOption) {
		this(renderer,
				renderOption,
				new TextPaint(TextPaint.ANTI_ALIAS_FLAG)
		);
	}

	@Hidden
	public TextEngineCore(Renderer renderer,
						  RenderOption renderOption,
						  TextPaint textPaint) {
		mTextPaint = textPaint;
		updateTextPaint(renderOption);

		mMeasurer = new AndroidMeasurer(mTextPaint);
		mTextAttribute = new TextAttribute(mMeasurer);
		mHandler = new AndroidThreadHandler() {

			@Override
			public void handleMessage(int what, Object value) {
				d("typeset paragraphs, msg what: " + what);
				if (mRenderer == null) {
					return;
				}

				if (what == MSG_FINISHED) {
					mDocument = (Document) value;
					mRenderer.render(mDocument, mMeasurer);
				} else if (what == MSG_FAILURE) {
					mRenderer.error((Throwable) value);
				}
			}
		};
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
	public void typeset(final Source source,
						final int width) {
		if (width <= 0) {
			mHandler.sendMessage(MSG_FAILURE, new IllegalArgumentException("width and height must be large than 0"));
			return;
		}
		cancel();

		final Document document = mDocument;
		mDocument = null;
		if (mRenderer != null) {
			mRenderer.start();
		}
		mTask = mExecutor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					Object content = source.open();
					typeset(content, width, document);
				} catch (Throwable throwable) {
					mHandler.sendMessage(MSG_FAILURE, throwable);
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

	@SuppressWarnings("unchecked")
	private void typeset(final Object content,
						 final int width,
						 Document document) {
		BreakStrategy breakStrategy = mRenderOption.getBreakStrategy();
		i("typeset, width: " + width +
				" break strategy: " + breakStrategy +
				" text size: " + mTextPaint.getTextSize());

		mWidth = width;

		// recycle memory
		if (document != null) {
			document.recycle();
		}

		updateLineAttribute(width);

		// 选择断字策略
		Hyphenation hyphenation = null;
		HyphenStrategy hyphenStrategy = mRenderOption.getHyphenStrategy();
		if (hyphenStrategy == HyphenStrategy.US) {
			hyphenation = Hyphenation.getInstance(HyphenationPattern.EN_US);
		} else if (hyphenStrategy == HyphenStrategy.UK) {
			hyphenation = Hyphenation.getInstance(HyphenationPattern.EN_GB);
		} else {
			throw new IllegalArgumentException("unknown hyphen strategy");
		}

		// parse
		long timestamp = SystemClock.elapsedRealtime();
		try {
			document = mParser.parse(content, mMeasurer, hyphenation, mTextAttribute, mRenderOption);
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
				mTypesetter.typeset((Paragraph) segment, mTextAttribute, breakStrategy);
			} else if (segment instanceof ViewSegment) {
				typesetViewSegment((ViewSegment) segment);
			} else {
				throw new RuntimeException("unknown segment type");
			}
		}

		i("typeset used time: " + (SystemClock.elapsedRealtime() - timestamp));
		boolean isInterrupted = thread.isInterrupted();
		i("is thread interrupt when typeset: " + isInterrupted);

		// call listener
		if (!isInterrupted) {
			mHandler.sendMessage(MSG_FINISHED, document);
		}
	}

	private void typesetViewSegment(ViewSegment viewSegment) {
		/* do nothing */
		d("typeset view segment");
	}

	private void typesetFigure(Figure figure, float lineWidth) {
		d("typeset figure");
		float width = figure.getWidth();
		float height = figure.getHeight();

		float ratio = Figure.DEFAULT_RATIO;
		if (width > 0 && height > 0) {
			ratio = width / height;
		}

		figure.resize(lineWidth, lineWidth / ratio);
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

	void release() {
		i("release");
		cancel();
		mRenderer = null;
		mDocument = null;
		mHandler.clear();
		mHandler = null;
	}

	private void cancel() {
		if (mTask != null) {
			i("cancel task: " + mTask);
			mTask.cancel(true);
			mTask = null;
		}
	}

	private void updateTextPaint(RenderOption renderOption) {
		mTextPaint.setColor(renderOption.getTextColor());
		mTextPaint.setTypeface(renderOption.getTypeface());
		mTextPaint.setTextSize(renderOption.getTextSize());
	}

	public void reload(RenderOption renderOption) {
		mRenderOption = renderOption;
		updateTextPaint(renderOption);

		mMeasurer.refresh(mTextPaint);
		mTextAttribute.refresh(mMeasurer);

		reload();
	}

	private void reload() {
		if (mDocument == null || mWidth < 0) {
			i("reload ignore");
			return;
		}

		final Object raw = mDocument.getRaw();
		if (raw == null) {
			i("reload ignore, get raw failed");
			return;
		}

		cancel();
		final Document document = mDocument;
		mDocument = null;
		if (mRenderer != null) {
			mRenderer.start();
		}
		mTask = mExecutor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					typeset(raw, mWidth, document);
				} catch (Throwable throwable) {
					mHandler.sendMessage(MSG_FAILURE, throwable);
				}
			}
		});
		i("reload: " + mTask);
	}

	public Document getDocument() {
		return mDocument;
	}

	public float getWidth() {
		return mWidth;
	}

	private static void d(String msg) {
		Log.d("TexasCore", msg);
	}

	private static void i(String msg) {
		Log.i("TexasCore", msg);
	}
}
