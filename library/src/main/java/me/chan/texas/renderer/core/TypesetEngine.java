package me.chan.texas.renderer.core;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import static me.chan.texas.renderer.core.worker.MixTask.TYPESET_ACTION_DEFAULT;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import me.chan.texas.TexasOption;
import me.chan.texas.adapter.ParseException;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.hyphenation.HyphenationPattern;
import me.chan.texas.issue.IssueSystem;
import me.chan.texas.measurer.AndroidMeasurer;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.Renderer;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.renderer.core.worker.MixTask;
import me.chan.texas.renderer.core.worker.ParagraphTypesetWorker;
import me.chan.texas.renderer.core.worker.ParseWorker;
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
import me.chan.texas.utils.concurrency.TaskQueue;

/**
 * 排版核心
 */
@RestrictTo(LIBRARY)
public class TypesetEngine {

	private volatile TexasView.Adapter<?> mAdapter;
	private int mWidth = 0;
	private Document mDocument;
	private Renderer mRenderer;
	private RenderOption mRenderOption;
	private TexasView.SegmentDecoration mSegmentDecoration;

	private final TaskQueue.Token mToken = TaskQueue.Token.newInstance();

	public TypesetEngine(Renderer renderer,
						 RenderOption renderOption) {
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
			mRenderer.error(new IllegalArgumentException("width and height must be large than 0"));
			return;
		}

		if (mAdapter == null) {
			w("typeset, adapter is null");
			return;
		}

		cancel();

		MixTask.Args args = MixTask.Args.obtain(outWidth, action, mRenderOption, mDocument, new MixTask.Listener() {
			@Override
			public void onStart() {
				if (mRenderer != null) {
					mRenderer.start();
				}
			}

			@Override
			public void onFailure(Throwable throwable) {
				// todo
			}

			@Override
			public void onSuccess(TypesetResult result) {
				if (mDocument != result.doc) {
					// todo release previous document
				}
				mDocument = result.doc;
			}
		}, mAdapter, mSegmentDecoration);
		WorkerScheduler.mix().submit(mId, args);
	}

	protected Measurer createMeasure(PaintSet paintSet) {
		return new AndroidMeasurer(paintSet);
	}

	public static class TypesetResult {
		PaintSet paintSet;
		Document doc;

		public TypesetResult(PaintSet paintSet, Document doc) {
			this.paintSet = paintSet;
			this.doc = doc;
		}
	}

	private void typesetParagraph(Paragraph paragraph, int width) throws Throwable {
		ParagraphTypesetWorker.Args args = ParagraphTypesetWorker.Args.obtain(paragraph, mRenderOption, width);
		WorkerScheduler.typeset().submitSync(mToken, args);
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

		mToken.destroy();

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

		// todo odd
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
		WorkerScheduler.mix().cancel(mToken);
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
