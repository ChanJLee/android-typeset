package me.chan.texas.renderer.core.worker;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import me.chan.texas.Texas;
import me.chan.texas.TexasOption;
import me.chan.texas.di.TexasComponent;
import me.chan.texas.di.core.TextEngineCoreComponent;
import me.chan.texas.measurer.AndroidMeasurer;
import me.chan.texas.measurer.MeasureFactory;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.renderer.core.WorkerScheduler;
import me.chan.texas.renderer.core.sync.WorkerMessager;
import me.chan.texas.text.Document;
import me.chan.texas.text.Figure;
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

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class MixWorker implements TaskQueue.Listener<MixWorker.Args, MixWorker.TypesetResult>, TaskQueue.Task<MixWorker.Args, MixWorker.TypesetResult> {
	private static final int TYPE_SUCCESS = 1;

	private static final int TYPE_ERROR = 2;

	private static final int TYPE_START = 3;

	public static final boolean DEBUG = false;

	private final TaskQueue mTaskQueue;
	private final WorkerMessager mMessager;

	@Inject
	MeasureFactory mMeasureFactory;

	public MixWorker(TaskQueue taskQueue, WorkerMessager messager) {
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
	public void onSuccess(TaskQueue.Token token, Args args, TypesetResult ret) {
		WorkerMessager.WorkerMessage message = WorkerMessager.WorkerMessage.obtain(TYPE_SUCCESS, args, ret);
		mMessager.send(token, message);
	}

	@Override
	public void onError(TaskQueue.Token token, Args args, Throwable throwable) {
		WorkerMessager.WorkerMessage message = WorkerMessager.WorkerMessage.obtain(TYPE_ERROR, args, throwable);
		mMessager.send(token, message);
	}

	@Override
	public TypesetResult run(TaskQueue.Token token, Args args) throws Throwable {
		long startTimestamp = 0;
		if (DEBUG) {
			startTimestamp = SystemClock.elapsedRealtime();
		}

		long parseTimestamp = 0;
		if (DEBUG) {
			parseTimestamp = SystemClock.elapsedRealtime();
			d("parse or refresh used time: " + (parseTimestamp - startTimestamp));
		}

		final Document prev = args.prev == null ? new Document.Builder().build() : args.prev;
		DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new DiffUtil.Callback() {
			@Override
			public int getOldListSize() {
				return prev.getSegmentCount();
			}

			@Override
			public int getNewListSize() {
				return args.document.getSegmentCount();
			}

			@Override
			public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
				return prev.getSegment(oldItemPosition) == args.document.getSegment(newItemPosition);
			}

			@Override
			public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
				return areItemsTheSame(oldItemPosition, newItemPosition);
			}
		}, true);

		typesetDocument(token, args.outWidth, args.document, args.option, args.segmentDecoration, diff);

		if (DEBUG) {
			d("typeset used time: " + (SystemClock.elapsedRealtime() - parseTimestamp));
			Measurer measurer = args.option.getMeasurer();
			if (measurer instanceof AndroidMeasurer) {
				AndroidMeasurer androidMeasurer = (AndroidMeasurer) measurer;
				d("measure stats: " + androidMeasurer.stats());
			}
			d("status: " + WorkerScheduler.typeset().stats());
		}

		return new TypesetResult(args.option, args.document, diff);
	}

	private void typesetDocument(TaskQueue.Token token,
								 final int outWidth,
								 Document document,
								 TexasOption option,
								 TexasView.SegmentDecoration segmentDecoration,
								 DiffUtil.DiffResult diff) throws Throwable {
		RenderOption renderOption = option.getRenderOption();

		diff.dispatchUpdatesTo(new ListUpdateCallback() {
			@Override
			public void onInserted(int position, int count) {
				if (token.isExpired()) {
					return;
				}

				try {
					typesetSegments(token, outWidth, document, option, segmentDecoration, position, position + count);
				} catch (Throwable ignore) {
					/* NOOP */
				}
			}

			@Override
			public void onRemoved(int position, int count) {
				/* NOOP */
			}

			@Override
			public void onMoved(int fromPosition, int toPosition) {
				/* NOOP */
			}

			@Override
			public void onChanged(int position, int count, @Nullable Object payload) {
				if (token.isExpired()) {
					return;
				}

				try {
					typesetSegments(token, outWidth, document, option, segmentDecoration, position, position + count);
				} catch (Throwable ignore) {
					/* NOOP */
				}
			}
		});

		if (token.isExpired()) {
			throw new TaskQueue.TokenExpiredException("stop typeset document, reason: token expired", token);
		}

		if (renderOption.isEnableDebug()) {
			analyzeDocument(document);
		}
	}

	private void typesetSegments(
			TaskQueue.Token token,
			final int outWidth,
			Document document,
			TexasOption option,
			TexasView.SegmentDecoration segmentDecoration,
			int start, int end) throws Throwable {
		int size = document.getSegmentCount();
		RenderOption renderOption = option.getRenderOption();
		Measurer measurer = option.getMeasurer();
		TextAttribute textAttribute = option.getTextAttribute();

		// typeset
		for (int i = start; i < end && !token.isExpired(); ++i) {
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
				typesetParagraph(token, paragraph, width, renderOption, measurer, textAttribute);
			} else if (segment instanceof ViewSegment) {
				typesetViewSegment((ViewSegment) segment);
			} else {
				throw new RuntimeException("unknown segment type");
			}
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

	private void typesetParagraph(TaskQueue.Token token,
								  Paragraph paragraph,
								  int width,
								  RenderOption option,
								  Measurer measurer,
								  TextAttribute textAttribute) throws Throwable {
		measureParagraph(token, paragraph, measurer, textAttribute);
		ParagraphTypesetWorker.Args args = ParagraphTypesetWorker.Args.obtain(paragraph, option, width);
		WorkerScheduler.typeset().submitSync(token, args);
	}

	private void measureParagraph(TaskQueue.Token token,
								  Paragraph paragraph,
								  Measurer measurer,
								  TextAttribute textAttribute) throws TaskQueue.TokenExpiredException {
		Layout layout = paragraph.getLayout();
		layout.clear();

		int elementSize = paragraph.getElementCount();
		for (int j = 0; j < elementSize && !token.isExpired(); ++j) {
			Element element = paragraph.getElement(j);
			if (element == Glue.TERMINAL ||
					element == Penalty.FORCE_BREAK ||
					element == Penalty.ADVISE_BREAK ||
					element == Penalty.FORBIDDEN_BREAK) {
				continue;
			}

			element.measure(measurer, textAttribute);
		}

		if (token.isExpired()) {
			throw new TaskQueue.TokenExpiredException("stop typeset paragraph, reason: token is expired", token);
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

	public void cancel(TaskQueue.Token token) {
		mTaskQueue.cancel(token);
		mMessager.clear(token);
	}

	public interface Listener {
		void onStart();

		void onFailure(Throwable throwable);

		void onSuccess(TypesetResult result);
	}

	public static class Args {
		private final int outWidth;
		private final TexasOption option;
		private final Document prev;
		private final Document document;
		private final Listener listener;
		private final TexasView.SegmentDecoration segmentDecoration;

		public Args(int outWidth, TexasOption option, Document prev, Document document, Listener listener, TexasView.SegmentDecoration segmentDecoration) {
			this.outWidth = outWidth;
			this.option = option;
			this.prev = prev;
			this.document = document;
			this.listener = listener;
			this.segmentDecoration = segmentDecoration;
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

	public static class TypesetResult {
		/**
		 * 运行的参数
		 */
		public final TexasOption texasOption;
		/**
		 * 现在的文档
		 */
		public final Document doc;
		/**
		 * 变更diff
		 */
		public final DiffUtil.DiffResult diff;

		public TypesetResult(TexasOption texasOption, Document doc, DiffUtil.DiffResult diff) {
			this.texasOption = texasOption;
			this.doc = doc;
			this.diff = diff;
		}
	}
}