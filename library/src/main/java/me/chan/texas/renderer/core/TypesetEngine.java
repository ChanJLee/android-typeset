package me.chan.texas.renderer.core;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;
import static me.chan.texas.renderer.core.worker.MixTask.TYPESET_ACTION_DEFAULT;

import android.util.Log;

import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import javax.inject.Inject;
import javax.inject.Named;

import me.chan.texas.Texas;
import me.chan.texas.di.TexasComponent;
import me.chan.texas.di.core.TextEngineCoreComponent;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.Renderer;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.renderer.core.worker.MixTask;
import me.chan.texas.text.Document;
import me.chan.texas.utils.concurrency.TaskQueue;

/**
 * 排版核心
 */
@RestrictTo(LIBRARY)
public class TypesetEngine {
	public static final boolean DEBUG = false;

	private volatile TexasView.Adapter<?> mAdapter;
	private int mWidth = 0;
	private Document mDocument;
	private Renderer mRenderer;
	private RenderOption mRenderOption;
	private TexasView.SegmentDecoration mSegmentDecoration;

	@Inject
	@Named("ComputeTask")
	TaskQueue mComputeQueue;

	private final TaskQueue.Token mToken = TaskQueue.Token.newInstance();

	public TypesetEngine(Renderer renderer,
						 RenderOption renderOption) {
		mRenderer = renderer;
		mRenderOption = renderOption;

		TexasComponent texasComponent = Texas.getTexasComponent();
		TextEngineCoreComponent textEngineCoreComponent = texasComponent.coreComponent().create();
		textEngineCoreComponent.inject(this);

		if (DEBUG) {
			d("typeset engine created: " + mToken);
		}
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
				if (throwable instanceof TaskQueue.TokenExpiredException) {
					if (DEBUG) {
						w(throwable);
					}
					return;
				}

				if (mRenderer != null) {
					mRenderer.error(throwable);
				}
			}

			@Override
			public void onSuccess(TypesetResult result) {
				if (mDocument != result.doc) {
					releaseDocument(mDocument);
				}
				mDocument = result.doc;
				if (mRenderer != null) {
					mRenderer.render(mDocument, result.paintSet);
				}

			}
		}, mAdapter, mSegmentDecoration);
		WorkerScheduler.mix().submit(mToken, args);
	}

	public static class TypesetResult {
		PaintSet paintSet;
		Document doc;

		public TypesetResult(PaintSet paintSet, Document doc) {
			this.paintSet = paintSet;
			this.doc = doc;
		}
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
		if (document != null) {
			releaseDocument(document);
		}
	}

	private void releaseDocument(Document document) {
		if (document == null) {
			return;
		}

		WorkerScheduler.odd().submit(mToken, mComputeQueue, () -> {
			// 回收可能是一个耗时操作
			document.release();
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
		int action = MixTask.TYPESET_ACTION_REMEASURE;

		// 看下是不是只修改了断行策略，只修改了行高
		// 大概可以提升70%左右的性能
		if (mRenderOption != null && prevRenderOption != null) {
			if (prevRenderOption.getBreakStrategy() != mRenderOption.getBreakStrategy()) {
				RenderOption copy = new RenderOption(prevRenderOption);
				copy.setBreakStrategy(mRenderOption.getBreakStrategy());
				if (copy.equals(mRenderOption)) {
					action = MixTask.TYPESET_ACTION_TYPESET_ONLY;
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
		typeset(mWidth, MixTask.TYPESET_ACTION_TYPESET_ONLY);
	}

	@VisibleForTesting
	public Object getTypesetterInternalState() {
		return WorkerScheduler.typeset().getTypesetterInternalState();
	}

	private static void d(String msg) {
		Log.d("TypesetEngine", msg);
	}

	private static void i(String msg) {
		Log.i("TypesetEngine", msg);
	}

	private static void w(String msg) {
		Log.w("TypesetEngine", msg);
	}

	private static void w(Throwable throwable) {
		Log.w("TypesetEngine", throwable);
	}

	private static void e(String msg, Throwable throwable) {
		Log.e("TypesetEngine", msg, throwable);
	}
}
