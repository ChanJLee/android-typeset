package me.chan.texas.renderer.core.worker;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import me.chan.texas.Texas;
import me.chan.texas.TexasOption;
import me.chan.texas.adapter.ParseException;
import me.chan.texas.di.TexasComponent;
import me.chan.texas.di.core.TextEngineCoreComponent;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.hyphenation.HyphenationPattern;
import me.chan.texas.measurer.AndroidMeasurer;
import me.chan.texas.measurer.MeasureFactory;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.renderer.core.TypesetEngine;
import me.chan.texas.renderer.core.WorkerScheduler;
import me.chan.texas.renderer.core.sync.WorkerMessager;
import me.chan.texas.source.SourceOpenException;
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
import me.chan.texas.utils.concurrency.TaskQueue;

public class MixTask implements TaskQueue.Listener<MixTask.Args, TypesetEngine.TypesetResult>, TaskQueue.Task<MixTask.Args, TypesetEngine.TypesetResult> {
	private static final int TYPE_SUCCESS = 1;

	private static final int TYPE_ERROR = 2;

	private static final int TYPE_START = 3;

	public static final boolean DEBUG = false;

	/**
	 * 默认排版动作，排版的时候需要先解析document后排版
	 */
	public static final int TYPESET_ACTION_DEFAULT = 0;
	/**
	 * 排版的时候只需要重新测量document内容就可以，不需要解析document
	 */
	public static final int TYPESET_ACTION_REMEASURE = 1;
	/**
	 * 排版的时候只需要排版就好了，document不需要做任何改动
	 */
	public static final int TYPESET_ACTION_TYPESET_ONLY = 2;

	private final TaskQueue mTaskQueue;
	private final WorkerMessager mMessager;

	@Inject
	MeasureFactory mMeasureFactory;

	public MixTask(TaskQueue taskQueue, WorkerMessager messager) {
		mTaskQueue = taskQueue;
		mMessager = messager;
		mMessager.addListener((id, value) -> {
			Args args = value.asArg(Args.class);
			if (args == null) {
				return false;
			}

			if (value.type() == TYPE_START) {
				args.listener.onStart();
			} else if (value.type() == TYPE_SUCCESS) {
				args.listener.onSuccess(value.value());
			} else if (value.type() == TYPE_ERROR) {
				args.listener.onFailure(value.error());
			} else {
				throw new IllegalStateException("unknown mix's message type");
			}

			return true;
		});

		TexasComponent texasComponent = Texas.getTexasComponent();
		TextEngineCoreComponent textEngineCoreComponent = texasComponent.coreComponent().create();
		textEngineCoreComponent.inject(this);
	}

	public void submit(TaskQueue.Token token, Args args) {
		mTaskQueue.submit(token, args, this, this);
	}

	@Override
	public void onStart(TaskQueue.Token token, Args args) {
		WorkerMessager.WorkerMessage message = WorkerMessager.WorkerMessage.obtain(TYPE_START, args, null);
		mMessager.send(token, message);
	}

	@Override
	public void onSuccess(TaskQueue.Token token, Args args, TypesetEngine.TypesetResult ret) {
		WorkerMessager.WorkerMessage message = WorkerMessager.WorkerMessage.obtain(TYPE_SUCCESS, args, ret);
		mMessager.send(token, message);
	}

	@Override
	public void onError(TaskQueue.Token token, Args args, Throwable throwable) {
		WorkerMessager.WorkerMessage message = WorkerMessager.WorkerMessage.obtain(TYPE_ERROR, args, throwable);
		mMessager.send(token, message);
	}

	@Override
	public TypesetEngine.TypesetResult run(TaskQueue.Token token, Args args) throws Throwable {
		PaintSet paintSet = new PaintSet(args.option);
		Measurer measurer = mMeasureFactory.create(paintSet);
		TextAttribute textAttribute = new TextAttribute(measurer);

		long startTimestamp = 0;
		if (DEBUG) {
			startTimestamp = SystemClock.elapsedRealtime();
		}

		Document document = args.document;
		// 当之前是有document并且本次不需要解析document，我们只要简单的刷新下Document就好了
		if (document != null && args.action != TYPESET_ACTION_DEFAULT) {
			refreshDocument(document, textAttribute, args.action, measurer);
		} else {
			document = parse(token, textAttribute, measurer, args.option, args.adapter);
		}

		long parseTimestamp = 0;
		if (DEBUG) {
			parseTimestamp = SystemClock.elapsedRealtime();
			d("parse or refresh used time: " + (parseTimestamp - startTimestamp));
		}

		typesetDocument(token, args.outWidth, document, args.option, args.segmentDecoration);

		if (DEBUG) {
			d("typeset used time: " + (SystemClock.elapsedRealtime() - parseTimestamp));
			if (measurer instanceof AndroidMeasurer) {
				AndroidMeasurer androidMeasurer = (AndroidMeasurer) measurer;
				d("measure stats: " + androidMeasurer.stats());
			}
			d("status: " + WorkerScheduler.typeset().stats());
		}

		return new TypesetEngine.TypesetResult(paintSet, document);
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

	private void typesetDocument(TaskQueue.Token token, final int outWidth, Document document, RenderOption option, TexasView.SegmentDecoration segmentDecoration) throws Throwable {
		int size = document.getSegmentCount();

		// typeset
		for (int i = 0; i < size && !token.isExpired(); ++i) {
			Segment segment = document.getSegment(i);
			int width = outWidth;

			// avoid recreate
			Rect outRect = segment.getRect();
			if (outRect == null) {
				outRect = new Rect();
			}

			if (segmentDecoration != null) {
				segmentDecoration.onDecorateSegment(i, size, segment, document, outRect);
				width = outWidth - outRect.left - outRect.right;
			}

			segment.setRect(outRect);
			if (segment instanceof Figure) {
				typesetFigure((Figure) segment, width);
			} else if (segment instanceof Paragraph) {
				Paragraph paragraph = (Paragraph) segment;
				typesetParagraph(token, paragraph, width, option);
			} else if (segment instanceof ViewSegment) {
				typesetViewSegment((ViewSegment) segment);
			} else {
				throw new RuntimeException("unknown segment type");
			}
		}

		if (token.isExpired()) {
			throw new TaskQueue.TokenExpiredException("stop typeset document, reason: token expired", token);
		}

		if (option.isEnableDebug()) {
			analyzeDocument(document);
		}
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

	private Document parse(TaskQueue.Token token, TextAttribute textAttribute, Measurer measurer, RenderOption option, TexasView.Adapter<?> adapter) throws TaskQueue.TokenExpiredException, SourceOpenException, ParseException {
		// 选择断字策略
		Hyphenation hyphenation = null;
		HyphenStrategy hyphenStrategy = option.getHyphenStrategy();
		if (hyphenStrategy == HyphenStrategy.US) {
			hyphenation = Hyphenation.getInstance(HyphenationPattern.EN_US);
		} else if (hyphenStrategy == HyphenStrategy.UK) {
			hyphenation = Hyphenation.getInstance(HyphenationPattern.EN_GB);
		} else if (hyphenStrategy == HyphenStrategy.NONE) {
			hyphenation = Hyphenation.getInstance(HyphenationPattern.NONE);
		} else {
			throw new IllegalArgumentException("unknown hyphen strategy");
		}

		// 已经发生了中断，那么直接返回
		if (token.isExpired()) {
			throw new TaskQueue.TokenExpiredException("stop parse, token expired", token);
		}

		TexasOption texasOption = new TexasOption(hyphenation, measurer, textAttribute, option);
		return adapter.getDocument(texasOption);
	}

	private void typesetParagraph(TaskQueue.Token token, Paragraph paragraph, int width, RenderOption option) throws Throwable {
		ParagraphTypesetWorker.Args args = ParagraphTypesetWorker.Args.obtain(paragraph, option, width);
		WorkerScheduler.typeset().submitSync(token, args);
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

	public void cancel(TaskQueue.Token token) {
		mTaskQueue.cancel(token);
		mMessager.clear(token);
	}

	public interface Listener {
		void onStart();

		void onFailure(Throwable throwable);

		void onSuccess(TypesetEngine.TypesetResult result);
	}

	public static class Args extends DefaultRecyclable {
		private static final ObjectPool<Args> POOL = new ObjectPool<>(32);

		private int outWidth;
		private int action;
		private RenderOption option;
		private Document document;
		private Listener listener;

		private TexasView.Adapter<?> adapter;

		TexasView.SegmentDecoration segmentDecoration;

		private Args() {
		}

		@Override
		public void recycle() {
			if (isRecycled()) {
				return;
			}

			outWidth = 0;
			action = 0;
			option = null;
			document = null;
			listener = null;
			adapter = null;
			segmentDecoration = null;
			super.recycle();
			POOL.release(this);
		}

		public static Args obtain(@NonNull int outWidth, int action, RenderOption option,
								  Document document, Listener listener, TexasView.Adapter<?> adapter,
								  TexasView.SegmentDecoration segmentDecoration) {
			Args args = POOL.acquire();
			if (args == null) {
				args = new Args();
			}

			args.outWidth = outWidth;
			args.action = action;
			args.option = option;
			args.document = document;
			args.listener = listener;
			args.adapter = adapter;
			args.segmentDecoration = segmentDecoration;
			args.reuse();
			return args;
		}
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
		Log.d("MixTask", msg);
	}

	private static void i(String msg) {
		Log.i("MixTask", msg);
	}

	private static void w(String msg) {
		Log.w("MixTask", msg);
	}
}