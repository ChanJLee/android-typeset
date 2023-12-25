package me.chan.texas.renderer.core;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.util.Log;

import androidx.annotation.RestrictTo;

import javax.inject.Inject;
import javax.inject.Named;

import me.chan.texas.Texas;
import me.chan.texas.di.TexasComponent;
import me.chan.texas.di.core.TextEngineCoreComponent;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.LoadingStrategy;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.Renderer;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.renderer.core.worker.LoadingWorker;
import me.chan.texas.renderer.core.worker.MixWorker;
import me.chan.texas.text.Document;
import me.chan.texas.utils.concurrency.TaskQueue;

/**
 * 排版核心
 */
@RestrictTo(LIBRARY)
public class TypesetEngine {
	private static final int EVENT_START = 1;
	private static final int EVENT_SUCCESS = 2;
	private static final int EVENT_FAILURE = 3;
	private static final int EVENT_ALL = EVENT_FAILURE | EVENT_START | EVENT_SUCCESS;

	public static final boolean DEBUG = false;
	private final TaskQueue.Token mToken;
	private int mWidth = 0;
	private Document mDocument = null;

	private Renderer mRenderer;
	private RenderOption mRenderOption;
	private TexasView.SegmentDecoration mSegmentDecoration;

	@Inject
	@Named("ComputeTask")
	TaskQueue mComputeQueue;

	public TypesetEngine(Renderer renderer,
						 RenderOption renderOption,
						 TaskQueue.Token token) {
		mRenderer = renderer;
		mRenderOption = renderOption;
		mToken = token;

		TexasComponent texasComponent = Texas.getTexasComponent();
		TextEngineCoreComponent textEngineCoreComponent = texasComponent.coreComponent().create();
		textEngineCoreComponent.inject(this);

		if (DEBUG) {
			d("typeset engine created: " + mToken);
		}
	}

	public void resize(String reason, LoadingStrategy strategy, Listener listener) {
		if (mDocument == null || mWidth <= 0) {
			return;
		}

		typeset0(reason, mWidth, mDocument, 0, mDocument.getSegmentCount(), strategy, listener, EVENT_ALL);
	}

	/**
	 * typeset content
	 *
	 * @param outWidth width, must be > 0
	 * @param document document
	 */
	private void typeset0(String reason, final int outWidth,
						  Document document, int start, int end,
						  LoadingStrategy strategy, Listener listener,
						  int focusEvents) {
		d("typeset, reason: " + reason + ", width: " + outWidth + ", strategy: " + strategy);
		if (outWidth <= 0) {
			w("typeset, width <= 0");
			if (listener != null && (focusEvents & EVENT_FAILURE) != 0) {
				listener.onFailure(strategy, new IllegalArgumentException("width and height must be large than 0"));
			}
			return;
		}

		if (document == null) {
			w("typeset, document is null");
			if (listener != null && (focusEvents & EVENT_FAILURE) != 0) {
				listener.onFailure(strategy, new IllegalArgumentException("document is null"));
			}
			return;
		}

		// 先取消之前已经提交的排版任务
		if (document != mDocument || strategy == LoadingStrategy.LOAD) {
			WorkerScheduler.mix().cancel(mToken);
		}

		mDocument = document;
		mWidth = outWidth;
		MixWorker.Args args = MixWorker.Args.obtain(outWidth, mRenderOption, document, strategy,
				new MixWorker.Listener() {
					@Override
					public void onStart(LoadingStrategy strategy) {
						if (listener != null && (focusEvents & EVENT_START) != 0) {
							listener.onStart(strategy);
						}
					}

					@Override
					public void onFailure(LoadingStrategy strategy, Throwable throwable) {
						if (throwable instanceof TaskQueue.TokenExpiredException) {
							if (DEBUG) {
								w(throwable);
							}
							return;
						}

						if (listener != null && (focusEvents & EVENT_FAILURE) != 0) {
							listener.onFailure(strategy, throwable);
						}
					}

					@Override
					public void onSuccess(LoadingStrategy strategy, MixWorker.TypesetResult result) {
						if (listener != null && (focusEvents & EVENT_SUCCESS) != 0) {
							listener.onSuccess(strategy, result.paintSet, result.doc, result.start, result.end);
						}
					}
				}, mSegmentDecoration, start, end);
		WorkerScheduler.mix().submit(mToken, args);
	}

	public int getWidth() {
		return mWidth;
	}

	public void reset() {
		mDocument = null;
	}

	public void load(String reason, int width, LoadingStrategy strategy, TexasView.Adapter<?> adapter, Listener listener) {
		// 非增量的加载，都需要取消之前的任务
		if (strategy == LoadingStrategy.LOAD) {
			cancel();
		}

		LoadingWorker.Args args = LoadingWorker.Args.obtain(mRenderOption, adapter, strategy, new LoadingWorker.Listener() {
			@Override
			public void onStart() {
				d("try loading doc, width: " + width + ", strategy: " + strategy);
				if (listener != null) {
					listener.onStart(strategy);
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

				if (listener != null) {
					listener.onFailure(strategy, throwable);
				}
			}

			@Override
			public void onSuccess(LoadingStrategy strategy, Document document, int start, int end) {
				if (strategy == LoadingStrategy.LOAD) {
					typeset0(reason, width, document, 0, document.getSegmentCount(), strategy, listener, EVENT_FAILURE | EVENT_SUCCESS);
					return;
				}

				typeset0(reason, width, document, start, end, strategy, listener, EVENT_FAILURE | EVENT_SUCCESS);
			}
		});
		WorkerScheduler.loading().submit(mToken, args);
	}

	public void release() {
		i("release");
		cancel();

		mToken.destroy();

		// 断开渲染
		mRenderer = null;
	}

	private void cancel() {
		// 取消准备发送的消息
		WorkerScheduler.loading().cancel(mToken);
		WorkerScheduler.mix().cancel(mToken);
	}

	public Document getDocument() {
		return mDocument;
	}

	public void updateRenderOption(RenderOption renderOption) {
		mRenderOption = renderOption;
	}

	public void setSegmentDecoration(TexasView.SegmentDecoration segmentDecoration) {
		mSegmentDecoration = segmentDecoration;
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

	public interface Listener {
		void onStart(LoadingStrategy strategy);

		void onFailure(LoadingStrategy strategy, Throwable throwable);

		void onSuccess(LoadingStrategy strategy, PaintSet paintSet, Document doc, int start, int end);
	}
}
