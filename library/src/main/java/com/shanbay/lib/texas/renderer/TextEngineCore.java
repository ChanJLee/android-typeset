package com.shanbay.lib.texas.renderer;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextPaint;

import com.shanbay.lib.log.Log;
import com.shanbay.lib.texas.annotations.Hidden;
import com.shanbay.lib.texas.hyphenation.Hyphenation;
import com.shanbay.lib.texas.hyphenation.HyphenationPattern;
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
import com.shanbay.lib.texas.text.Line;
import com.shanbay.lib.texas.text.Paragraph;
import com.shanbay.lib.texas.text.Segment;
import com.shanbay.lib.texas.text.TextAttribute;
import com.shanbay.lib.texas.text.ViewSegment;
import com.shanbay.lib.texas.typesetter.ParagraphTypesetterImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

	TextEngineCore(Renderer renderer,
				   RenderOption renderOption) {
		this(renderer,
				renderOption,
				new TextPaint(TextPaint.ANTI_ALIAS_FLAG)
		);
	}

	@Hidden
	TextEngineCore(Renderer renderer,
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
	void typeset(final Source source,
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
		if (isInterrupted) {
			return;
		}

		// call listener
		mHandler.sendMessage(MSG_FINISHED, document);
		if (mRenderOption.isEnableDebug()) {
			analyzeDocument(document);
		}
	}

	private void typesetViewSegment(ViewSegment viewSegment) {
		/* do nothing */
		d("typeset view segment");
	}

	private void typesetFigure(Figure figure, float lineWidth) {
		d("typeset figure");
		float width = figure.getWidth();
		if (width <= 0) {
			w("width <= 0, ignore");
			return;
		}

		float height = figure.getHeight();
		if (height <= 0) {
			w("height <= 0, ignore");
			return;
		}

		float ratio = height / width;
		figure.resize(lineWidth, lineWidth * ratio);
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

	void reload(RenderOption renderOption) {
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

	private static void analyzeDocument(Document document) {
		ResultEvaluation resultEvaluation = new ResultEvaluation();
		final int segmentCount = document.getSegmentCount();
		for (int segmentIndex = 0; segmentIndex < segmentCount; ++segmentIndex) {
			Segment segment = document.getSegment(segmentIndex);
			if (!(segment instanceof Paragraph)) {
				continue;
			}

			Paragraph paragraph = (Paragraph) segment;
			final int lineCount = paragraph.getLineCount();
			for (int lineIndex = 0; lineIndex < lineCount; ++lineIndex) {
				Line line = paragraph.getLine(lineIndex);
				float ratio = line.getRatio();
				resultEvaluation.add(ratio);
			}
		}
		d("total: \n" + resultEvaluation.get());
	}

	public Document getDocument() {
		return mDocument;
	}

	private static void w(String msg) {
		Log.w("TexasCore", msg);
	}

	void updateRenderOption(RenderOption renderOption) {
		mRenderOption = renderOption;
	}

	/**
	 * 用来衡量算法质量
	 */
	private static class ResultEvaluation {
		private float mSum = 0;
		private List<Float> mSamples = new ArrayList<>();
		// 0
		private int mBestCount = 0;
		// (0, 1]
		private int mStretchLevel0Count = 0;
		// (1, 2]
		private int mStretchLevel1Count = 0;
		// (2, 3]
		private int mStretchLevel2Count = 0;
		// (3, 4]
		private int mStretchLevel3Count;
		// (4, 无穷)
		private int mStretchLevel4Count;
		// [-0.2, 0)
		private int mShrinkLevel0Count;
		// (负无穷, -0.2)
		private int mShrinkLevel1Count;

		public void add(float sample) {

			if (sample > 4) {
				++mStretchLevel4Count;
			} else if (sample > 3) {
				++mStretchLevel3Count;
			} else if (sample > 2) {
				++mStretchLevel2Count;
			} else if (sample > 1) {
				++mStretchLevel1Count;
			} else if (sample > 0) {
				++mStretchLevel0Count;
			} else if (sample == 0) {
				++mBestCount;
			} else if (sample >= -0.2) {
				++mShrinkLevel0Count;
			} else {
				++mShrinkLevel1Count;
			}

			mSamples.add(sample);
			mSum += sample;
		}

		@SuppressLint("DefaultLocale")
		public String get() {
			int count = mSamples.size();
			if (count == 0) {
				return "has not samples";
			}

			StringBuilder stringBuilder = new StringBuilder("count: ")
					.append(count)
					.append(", ");

			float avg = mSum / count;
			stringBuilder.append("avg: ")
					.append(avg)
					.append(", ");

			Collections.sort(mSamples);
			float mid = mSamples.get(count / 2);
			if (count % 2 == 0) {
				mid = (mid + mSamples.get(count / 2 - 1)) / 2;
			}
			stringBuilder.append("mid: ")
					.append(mid)
					.append(", ")
					.append("var: ")
					.append(calVar(avg, count));

			stringBuilder
					.append("\n(-∞, -0.2: ")
					.append(mShrinkLevel1Count * 1.0 / count)
					.append("\n[-0.2, 0): ")
					.append(mShrinkLevel0Count * 1.0 / count)
					.append("\n0:")
					.append(mBestCount * 1.0 / count)
					.append("\n(0, 1]: ")
					.append(mStretchLevel0Count * 1.0 / count)
					.append("\n(1, 2]: ")
					.append(mStretchLevel1Count * 1.0 / count)
					.append("\n(2, 3]: ")
					.append(mStretchLevel2Count * 1.0 / count)
					.append("\n(3, 4]: ")
					.append(mStretchLevel3Count * 1.0 / count)
					.append("\n(4, +∞): ")
					.append(mStretchLevel4Count * 1.0 / count);

			StringBuilder json = new StringBuilder("[");
			for (float sample : mSamples) {
				json.append(sample)
						.append(",");
			}
			if (json.length() > 0) {
				json.deleteCharAt(json.length() - 1);
			}
			json.append("]");
			try {
				File file = new File(Environment.getExternalStorageDirectory(), "tex.json");
				if (!file.exists()) {
					file.createNewFile();
				}
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				fileOutputStream.write(json.toString().getBytes());
				fileOutputStream.flush();
				fileOutputStream.close();
				d("EvaluationSample " + file.getAbsolutePath());
			} catch (Throwable e) {
				e.printStackTrace();
			}

			return stringBuilder.toString();
		}

		private float calVar(float avg, int count) {
			float var = 0.0f;
			for (float s : mSamples) {
				var += (s - avg) * (s - avg);
			}
			return var / count;
		}
	}

	private static void d(String msg) {
		Log.d("TexasCore", msg);
	}

	private static void i(String msg) {
		Log.i("TexasCore", msg);
	}
}
