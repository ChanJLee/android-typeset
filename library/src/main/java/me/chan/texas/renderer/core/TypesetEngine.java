package me.chan.texas.renderer.core;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.TexasOption;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.renderer.core.worker.LoadingWorker;
import me.chan.texas.renderer.core.worker.MixWorker;
import me.chan.texas.text.Document;
import me.chan.texas.utils.concurrency.Worker;

/**
 * 排版核心
 */
@RestrictTo(LIBRARY)
public class TypesetEngine {
	private static final int EVENT_START = 1;
	private static final int EVENT_SUCCESS = 2;
	private static final int EVENT_FAILURE = 3;

	private static final int EVENT_NONE = 0;

	public static final boolean DEBUG = false;
	private final Worker.Token mToken;
	private int mWidth = 0;
	private Document mDocument = null;

	private TexasView.SegmentDecoration mSegmentDecoration;

	public TypesetEngine(
			Worker.Token token) {
		mToken = token;

		if (DEBUG) {
			d("typeset engine created: " + mToken);
		}
	}

	public void resize(String reason, TexasOption option, Listener listener) {
		resize0(reason, option, mWidth, listener);
	}

	public void resize(String reason, TexasOption option, int width, Listener listener) {
		resize0(reason, option, width, listener);
	}

	private void resize0(String reason,
						 TexasOption option, int width, Listener listener) {
		if (width > 0) {
			mWidth = width;
		}

		if (mDocument == null || width <= 0) {
			return;
		}
		typeset0(reason, option, null /* 需要的是全量更新 */, mDocument, listener, EVENT_SUCCESS);
	}

	/**
	 * typeset content
	 *
	 * @param document document
	 */
	private void typeset0(String reason,
						  TexasOption option,
						  @Nullable Document prev /* 传空就是全量更新 */,
						  Document document,
						  Listener listener,
						  int focusEvents) {
		d("typeset, reason: " + reason);

		if (document == null) {
			w("typeset, document is null");
			if (listener != null && (focusEvents & EVENT_FAILURE) != 0) {
				listener.onFailure(new IllegalArgumentException("document is null"));
			}
			return;
		}

		// 先取消之前已经提交的排版任务
		WorkerScheduler.mix().cancel(mToken);

		mDocument = document;
		MixWorker.Args args = new MixWorker.Args(mWidth, option, prev, document,
				new MixWorker.Listener() {
					@Override
					public void onStart() {
						if (listener != null && (focusEvents & EVENT_START) != 0) {
							listener.onStart();
						}
					}

					@Override
					public void onFailure(Throwable throwable) {
						if (throwable instanceof Worker.TokenExpiredException) {
							if (DEBUG) {
								w(throwable);
							}
							return;
						}

						if (listener != null && (focusEvents & EVENT_FAILURE) != 0) {
							listener.onFailure(throwable);
						}
					}

					@Override
					public void onSuccess(MixWorker.TypesetResult result) {
						if (listener != null && (focusEvents & EVENT_SUCCESS) != 0) {
							listener.onSuccess(result);
						}
					}
				}, mSegmentDecoration);
		WorkerScheduler.mix().submit(mToken, args);
	}

	public int getWidth() {
		return mWidth;
	}

	public void reset() {
		mDocument = null;
	}

	public Document getDocument() {
		return mDocument;
	}

	public void load(String reason, int width, TexasView.DocumentSource source, Listener listener) {
		if (source == null) {
			return;
		}

		// 非增量的加载，都需要取消之前的任务
		cancel();

		mWidth = width;
		LoadingWorker.Args args = new LoadingWorker.Args(source, new LoadingWorker.Listener() {
			@Override
			public void onStart() {
				d("try loading doc, width: " + width + ", reason: " + reason);
				if (listener != null) {
					listener.onStart();
				}
			}

			@Override
			public void onFailure(Throwable throwable) {
				d("loading doc failure, width: " + width + ", reason: " + reason);
				if (throwable instanceof Worker.TokenExpiredException) {
					if (DEBUG) {
						w(throwable);
					}
					return;
				}

				if (listener != null) {
					listener.onFailure(throwable);
				}
			}

			@Override
			public void onSuccess(TexasOption option, Document prev, Document document) {
				d("loading doc success, width: " + width + ", reason: " + reason);
				typeset0(reason, option, prev, document, listener, EVENT_FAILURE | EVENT_SUCCESS);
			}
		});
		WorkerScheduler.loading().submit(mToken, args);
	}

	public void release() {
		i("release");
		cancel();

		mToken.destroy();
	}

	private void cancel() {
		// 取消准备发送的消息
		WorkerScheduler.loading().cancel(mToken);
		WorkerScheduler.mix().cancel(mToken);
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
		void onStart();

		void onFailure(Throwable throwable);

		void onSuccess(MixWorker.TypesetResult result);
	}
}
