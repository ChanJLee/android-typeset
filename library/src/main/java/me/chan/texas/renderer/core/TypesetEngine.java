package me.chan.texas.renderer.core;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Environment;
import android.os.SystemClock;

import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import com.shanbay.lib.log.Log;
import me.chan.texas.Texas;
import me.chan.texas.TexasOption;
import me.chan.texas.adapter.ParseException;
import me.chan.texas.concurrency.Messager;
import me.chan.texas.di.TexasComponent;
import me.chan.texas.di.core.TextEngineCoreComponent;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.hyphenation.HyphenationPattern;
import me.chan.texas.issue.IssueSystem;
import me.chan.texas.measurer.AndroidMeasurer;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.Renderer;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.renderer.core.worker.TypesetWorker;
import me.chan.texas.source.SourceOpenException;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Document;
import me.chan.texas.text.Figure;
import me.chan.texas.text.HyphenStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.ViewSegment;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.Glue;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.layout.Penalty;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;

/**
 * 排版核心
 */
@RestrictTo(LIBRARY)
public class TypesetEngine implements Messager.HandleListener {
	public static final boolean DEBUG = false;

	/**
	 * 排版消息，成功/失败
	 */
	private static final int TYPESET_MSG_FINISHED = 2;
	private static final int TYPESET_MSG_FAILURE = 3;

	/**
	 * 默认排版动作，排版的时候需要先解析document后排版
	 */
	private static final int TYPESET_ACTION_DEFAULT = 0;
	/**
	 * 排版的时候只需要重新测量document内容就可以，不需要解析document
	 */
	private static final int TYPESET_ACTION_REMEASURE = 1;
	/**
	 * 排版的时候只需要排版就好了，document不需要做任何改动
	 */
	private static final int TYPESET_ACTION_TYPESET_ONLY = 2;

	// handler需要设置线程可见性，这样一旦释放了handler，工作线程能立马看到
	// 滞后的消息就不会发到主线程
	@Inject
	volatile Messager mMessager;

	private volatile TexasView.Adapter<?> mAdapter;
	private int mWidth = 0;
	private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

	private Document mDocument;
	private Future<?> mTask;
	private Renderer mRenderer;
	private RenderOption mRenderOption;
	private TexasView.SegmentDecoration mSegmentDecoration;

	public TypesetEngine(Renderer renderer,
						 RenderOption renderOption) {
		TexasComponent texasComponent = Texas.getTexasComponent();
		TextEngineCoreComponent textEngineCoreComponent = texasComponent.coreComponent().create();
		textEngineCoreComponent.inject(this);
		mMessager.setListener(this);

		mRenderer = renderer;
		mRenderOption = renderOption;
	}

	public void setAdapter(TexasView.Adapter<?> adapter) {
		// 设置adapter之前需要取消所有的任务
		// 防止document解析错
		// adapter和对应的数据接口是有关系的
		cancel();
		mAdapter = adapter;
	}

	public void typeset(int width) {
		typeset(width, TYPESET_ACTION_DEFAULT);
	}

	/**
	 * typeset content
	 *
	 * @param outWidth width, must be > 0
	 * @param action   action
	 */
	private void typeset(final int outWidth, final int action) {
		if (outWidth <= 0) {
			w("typeset, width <= 0");
			sendMessage(TYPESET_MSG_FAILURE, new IllegalArgumentException("width and height must be large than 0"));
			return;
		}

		if (mAdapter == null) {
			w("typeset, adapter is null");
			return;
		}

		cancel();

		if (mRenderer != null) {
			mRenderer.start();
		}
		mTask = mExecutor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					PaintSet paintSet = new PaintSet(mRenderOption);
					Measurer measurer = createMeasure(paintSet);
					TextAttribute textAttribute = new TextAttribute(measurer);

					long startTimestamp = 0;
					if (DEBUG) {
						startTimestamp = SystemClock.elapsedRealtime();
					}

					Document document = mDocument;
					// 当之前是有document并且本次不需要解析document，我们只要简单的刷新下Document就好了
					if (document != null && action != TYPESET_ACTION_DEFAULT) {
						refreshDocument(document, textAttribute, action, measurer);
					} else {
						document = parse(textAttribute, measurer);
					}

					long parseTimestamp = 0;
					if (DEBUG) {
						parseTimestamp = SystemClock.elapsedRealtime();
						d("parse or refresh used time: " + (parseTimestamp - startTimestamp));
					}

					if (document == null) {
						w("parse, but document is null");
						return;
					}
					typesetDocument(outWidth, document, paintSet);

					if (DEBUG) {
						d("typeset used time: " + (SystemClock.elapsedRealtime() - parseTimestamp));
						if (measurer instanceof AndroidMeasurer) {
							AndroidMeasurer androidMeasurer = (AndroidMeasurer) measurer;
							d("measure stats: " + androidMeasurer.stats());
						}
						d("status: " + WorkerScheduler.typeset().stats());
					}
				} catch (Throwable throwable) {
					e("typeset catch exception", throwable);
					sendMessage(TYPESET_MSG_FAILURE, throwable);
					IssueSystem.submit("typeset", throwable);
				}
			}
		});
		d("typeset: " + mTask);
	}

	protected Measurer createMeasure(PaintSet paintSet) {
		return new AndroidMeasurer(paintSet);
	}

	private void refreshDocument(Document document, TextAttribute textAttribute, int action, Measurer measurer) {
		int size = document.getSegmentCount();
		for (int i = 0; i < size; ++i) {
			Segment segment = document.getSegment(i);
			if (!(segment instanceof Paragraph)) {
				continue;
			}

			Paragraph paragraph = (Paragraph) segment;
			Layout layout = paragraph.getLayout();
			layout.clear();

			if (action == TYPESET_ACTION_TYPESET_ONLY) {
				continue;
			}

			int elementSize = paragraph.getElementCount();
			for (int j = 0; j < elementSize; ++j) {
				Element element = paragraph.getElement(j);
				if (element == Glue.TERMINAL ||
						element == Penalty.FORCE_BREAK ||
						element == Penalty.ADVISE_BREAK ||
						element == Penalty.FORBIDDEN_BREAK) {
					continue;
				}

				element.measure(measurer, textAttribute);
			}
		}
	}

	private Document parse(TextAttribute textAttribute, Measurer measurer) throws SourceOpenException, ParseException {
		BreakStrategy breakStrategy = mRenderOption.getBreakStrategy();
		i("parse, break strategy: " + breakStrategy);

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

		// 已经发生了中断，那么直接返回
		final Thread thread = Thread.currentThread();
		if (thread.isInterrupted() || mAdapter == null) {
			i("interrupted before parse or adapter is null");
			return null;
		}

		TexasOption texasOption = new TexasOption(hyphenation, measurer, textAttribute, mRenderOption);
		return mAdapter.getDocument(texasOption);
	}

	private void typesetDocument(final int outWidth, Document document, PaintSet context) throws Throwable {
		Thread thread = Thread.currentThread();
		int size = document.getSegmentCount();

		// typeset
		for (int i = 0; i < size && !thread.isInterrupted(); ++i) {
			Segment segment = document.getSegment(i);
			int width = outWidth;

			// avoid recreate
			Rect outRect = segment.getRect();
			if (outRect == null) {
				outRect = new Rect();
			}

			if (mSegmentDecoration != null) {
				mSegmentDecoration.onDecorateSegment(i, size, segment, document, outRect);
				width = outWidth - outRect.left - outRect.right;
			}

			segment.setRect(outRect);
			if (segment instanceof Figure) {
				typesetFigure((Figure) segment, width);
			} else if (segment instanceof Paragraph) {
				Paragraph paragraph = (Paragraph) segment;
				typesetParagraph(paragraph, width);
			} else if (segment instanceof ViewSegment) {
				typesetViewSegment((ViewSegment) segment);
			} else {
				throw new RuntimeException("unknown segment type");
			}
		}

		boolean isInterrupted = thread.isInterrupted();
		i("is thread interrupt when typeset: " + isInterrupted);
		if (isInterrupted) {
			return;
		}

		// call listener
		Document prevDoc = mDocument;
		mDocument = document;
		mWidth = outWidth;
		sendMessage(TYPESET_MSG_FINISHED, new TypesetResult(context, document));
		if (prevDoc != null && mDocument != prevDoc) {
			prevDoc.release();
		}

		if (mRenderOption.isEnableDebug()) {
			analyzeDocument(document);
		}
	}

	static class TypesetResult {
		PaintSet paintSet;
		Document doc;

		public TypesetResult(PaintSet paintSet, Document doc) {
			this.paintSet = paintSet;
			this.doc = doc;
		}
	}

	private void typesetParagraph(Paragraph paragraph, int width) throws Throwable {
		TypesetWorker.Args args = TypesetWorker.Args.obtain(paragraph, mRenderOption, width);
		WorkerScheduler.typeset().submitSync(0, args);
	}

	private void sendMessage(int what, Object o) {
		if (mMessager != null) {
			mMessager.send(what, o);
		}
	}

	private void typesetViewSegment(ViewSegment viewSegment) {
		/* do nothing */
	}

	private void typesetFigure(Figure figure, float lineWidth) {
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

	public void release() {
		i("release");
		cancel();

		// 断开消息通知
		mMessager = null;
		// 关闭解析
		mAdapter = null;
		// 断开渲染
		mRenderer = null;

		// 释放内存
		final Document document = mDocument;
		mDocument = null;
		if (document == null) {
			return;
		}

		mExecutor.execute(new Runnable() {
			@Override
			public void run() {
				// 回收可能是一个耗时操作
				document.release();
				i("release document finished");
			}
		});
	}

	private void cancel() {
		// 取消准备发送的消息
		if (mMessager != null) {
			i("clear msg");
			mMessager.clear();
		}

		if (mTask != null) {
			i("cancel task: " + mTask);
			mTask.cancel(true);
			mTask = null;
		}
	}

	/**
	 * @param prevRenderOption 旧的渲染选项
	 */
	public void reload(RenderOption prevRenderOption) {
		// 默认只要重新测量就可以了
		int action = TYPESET_ACTION_REMEASURE;

		// 看下是不是只修改了断行策略，只修改了行高
		// 大概可以提升70%左右的性能
		if (mRenderOption != null && prevRenderOption != null) {
			if (prevRenderOption.getBreakStrategy() != mRenderOption.getBreakStrategy()) {
				RenderOption copy = new RenderOption(prevRenderOption);
				copy.setBreakStrategy(mRenderOption.getBreakStrategy());
				if (copy.equals(mRenderOption)) {
					action = TYPESET_ACTION_TYPESET_ONLY;
				}
			}
		}

		// fail-fast
		if (mWidth <= 0) {
			i("reload ignore");
			return;
		}
		typeset(mWidth, action);
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
			Layout layout = paragraph.getLayout();
			final int lineCount = layout.getLineCount();
			for (int lineIndex = 0; lineIndex < lineCount; ++lineIndex) {
				Line line = layout.getLine(lineIndex);
				float ratio = line.getRatio();
				resultEvaluation.add(ratio);
			}
		}
		d("total: \n" + resultEvaluation.get());
	}

	public Document getDocument() {
		return mDocument;
	}

	public void updateRenderOption(RenderOption renderOption) {
		mRenderOption = renderOption;
	}

	public void setSegmentDecoration(TexasView.SegmentDecoration segmentDecoration) {
		mSegmentDecoration = segmentDecoration;

		// fail-fast
		if (mWidth <= 0) {
			i("reload ignore");
			return;
		}
		typeset(mWidth, TYPESET_ACTION_TYPESET_ONLY);
	}

	@Override
	@RestrictTo(LIBRARY)
	public void handleMessage(int what, Object value) {
		d("typeset paragraphs, msg what: " + what);
		if (mRenderer == null) {
			return;
		}

		if (what == TYPESET_MSG_FINISHED) {
			TypesetResult result = (TypesetResult) value;
			mRenderer.render(mDocument, result.paintSet);
		} else if (what == TYPESET_MSG_FAILURE) {
			mRenderer.error((Throwable) value);
		}
	}

	@VisibleForTesting
	public Object getTypesetterInternalState() {
		return WorkerScheduler.typeset().getTypesetterInternalState();
	}

	/**
	 * 用来衡量算法质量
	 */
	private static class ResultEvaluation {
		private float mSum = 0;
		private final List<Float> mSamples = new ArrayList<>();
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

	private static void w(String msg) {
		Log.w("TexasCore", msg);
	}

	private static void e(String msg, Throwable throwable) {
		Log.e("TexasCore", msg, throwable);
	}
}
