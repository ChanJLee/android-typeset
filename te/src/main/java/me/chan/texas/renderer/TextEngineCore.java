package me.chan.texas.renderer;

import android.os.SystemClock;
import android.text.TextPaint;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import me.chan.texas.annotations.Hidden;
import me.chan.texas.hypher.HyphenationPattern;
import me.chan.texas.hypher.Hypher;
import me.chan.texas.log.Log;
import me.chan.texas.measurer.AndroidMeasurer;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.parser.Parser;
import me.chan.texas.parser.TextParser;
import me.chan.texas.source.Source;
import me.chan.texas.source.SourceCloseException;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Document;
import me.chan.texas.text.Figure;
import me.chan.texas.text.Foot;
import me.chan.texas.text.Gravity;
import me.chan.texas.text.HyphenStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.typesetter.ParagraphTypesetterImpl;

public class TextEngineCore {
	private static final int MSG_FINISHED = 2;
	private static final int MSG_FAILURE = 3;

	private TextPaint mTextPaint;
	private TextAttribute mTextAttribute;
	private Measurer mMeasurer;
	private ThreadHandler mHandler;
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
						final int width,
						final int height) {
		if (width <= 0 || height <= 0) {
			mHandler.sendMessage(MSG_FAILURE, new IllegalArgumentException("width and height must be large than 0"));
			return;
		}
		cancel();

		final Document document = mDocument;
		mDocument = null;
		if (mRenderer != null) {
			mRenderer.clear();
		}
		mTask = mExecutor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					Object content = source.open();
					typeset(content, width, height, document);
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

	// TODO 异常安全保障
	@SuppressWarnings("unchecked")
	private void typeset(final Object content,
						 final int width,
						 int height,
						 Document document) {
		i("typeset, width: " + width +
				" height: " + height +
				" break strategy: " + mBreakStrategy +
				" text size: " + mTextPaint.getTextSize());

		mWidth = width;
		mHeight = height;

		// recycle memory
		if (document != null) {
			document.recycle();
		}

		updateLineAttribute(width);

		// 选择断字策略
		Hypher hypher = null;
		HyphenStrategy hyphenStrategy = mRenderOption.getHyphenStrategy();
		if (hyphenStrategy == HyphenStrategy.US) {
			hypher = Hypher.getInstance(HyphenationPattern.EN_US);
		} else if (hyphenStrategy == HyphenStrategy.UK) {
			hypher = Hypher.getInstance(HyphenationPattern.EN_GB);
		} else {
			throw new IllegalArgumentException("unknown hyphen strategy");
		}

		// parse
		long timestamp = SystemClock.elapsedRealtime();
		try {
			document = mParser.parse(content, mMeasurer, hypher, mTextAttribute, mRenderOption);
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
			} else if (segment instanceof Foot) {
				typesetFoot((Foot) segment);
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

	private void typesetFoot(Foot foot) {
		/* do nothing */
		d("typeset foot, " + foot);
	}

	private void typesetFigure(Figure figure, float lineWidth) {
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

	public void reload(RenderOption renderOption) {
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
		final Document document = mDocument;
		mDocument = null;
		if (mRenderer != null) {
			mRenderer.clear();
		}
		mTask = mExecutor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					typeset(document.getRaw(), mWidth, mHeight, document);
				} catch (Throwable throwable) {
					mHandler.sendMessage(MSG_FAILURE, throwable);
				}
			}
		});
		d("reload: " + mTask);
	}

	public Document getDocument() {
		return mDocument;
	}

	private static void d(String msg) {
		Log.d("TexasCore", msg);
	}

	private static void i(String msg) {
		Log.i("TexasCore", msg);
	}
}
