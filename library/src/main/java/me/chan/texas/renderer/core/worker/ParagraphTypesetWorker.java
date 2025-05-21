package me.chan.texas.renderer.core.worker;

import android.util.Log;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.renderer.core.WorkerScheduler;
import me.chan.texas.renderer.core.sync.WorkerMessager;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.typesetter.ParagraphTypesetter;
import me.chan.texas.utils.concurrency.TaskQueue;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class ParagraphTypesetWorker implements TaskQueue.Task<ParagraphTypesetWorker.Args, Paragraph>,
		TaskQueue.Listener<ParagraphTypesetWorker.Args, Paragraph> {
	private static final int TYPE_SUCCESS = 1;
	private static final int TYPE_ERROR = 2;

	private final ParagraphTypesetter mTypesetter;
	private final TaskQueue mTaskQueue;
	private final WorkerMessager mMessager;

	public ParagraphTypesetWorker(TaskQueue taskQueue, WorkerMessager messager) {
		mTaskQueue = taskQueue;
		mMessager = messager;
		mTypesetter = new ParagraphTypesetter();
		mMessager.addListener((id, message) -> {
			Args args = message.asArg(Args.class);
			if (args == null) {
				return false;
			}

			switch (message.type()) {
				case TYPE_SUCCESS:
					if (args.listener != null) {
						args.listener.onTypesetSuccess(message.value());
					}
					break;
				case TYPE_ERROR:
					if (args.listener != null) {
						args.listener.onTypesetFailure(message.error());
					}
					break;
			}
			args.recycle();
			return true;
		});
	}

	public String stats() {
		return mTypesetter.stats();
	}

	public void submit(TaskQueue.Token token, Args args) {
		mTaskQueue.submit(token, args, this, this);
	}

	public Paragraph submitSync(TaskQueue.Token token, Args args) throws Throwable {
		return mTaskQueue.submitSync(token, args, this);
	}

	public void cancel(TaskQueue.Token token) {
		mTaskQueue.cancel(token);
	}

	@VisibleForTesting
	public Object getTypesetterInternalState() {
		return mTypesetter.getInternalState();
	}

	@Override
	public void onStart(TaskQueue.Token token, Args args) {
		/* do nothing */
	}

	@Override
	public void onSuccess(TaskQueue.Token token, Args args, Paragraph ret) {
		WorkerMessager.WorkerMessage message = WorkerMessager.WorkerMessage.obtain(TYPE_SUCCESS, args, ret);
		mMessager.send(token, message);
	}

	@Override
	public void onError(TaskQueue.Token token, Args args, Throwable throwable) {
		Log.w("TypesetWorker", throwable);
		WorkerMessager.WorkerMessage message = WorkerMessager.WorkerMessage.obtain(TYPE_ERROR, args, throwable);
		mMessager.send(token, message);
	}

	@Override
	public Paragraph run(TaskQueue.Token token, Args args) throws Throwable {
		Paragraph paragraph = args.paragraph;
		Layout layout = paragraph.getLayout();

		Layout.Advise advise = layout.getAdvise();
		BreakStrategy breakStrategy = advise.getBreakStrategy();
		if (!mTypesetter.typeset(paragraph, breakStrategy, args.width)) {
			throw new RuntimeException("typeset failed");
		}
		return paragraph;
	}

	/**
	 * 预测宽高
	 *
	 * @param paragraph     段落
	 * @param token         令牌
	 * @param expectedWidth 期望宽度
	 * @return true表示成功
	 */
	public boolean desire(@NonNull Paragraph paragraph, TaskQueue.Token token, int expectedWidth) {
		if (!paragraph.hasContent()) {
			return false;
		}

		ParagraphTypesetWorker worker = WorkerScheduler.typeset();
		ParagraphTypesetWorker.Args args = ParagraphTypesetWorker.Args.obtain(paragraph, expectedWidth);
		try {
			worker.submitSync(token, args);
		} catch (Throwable e) {
			return false;
		}
		return true;
	}

	/**
	 * 预测宽高
	 *
	 * @param paragraph 段落
	 * @param token     令牌
	 * @return true表示成功
	 */
	public boolean desire(@NonNull Paragraph paragraph, TaskQueue.Token token) {
		return desire(paragraph, token, Integer.MAX_VALUE);
	}

	public interface Listener {

		void onTypesetSuccess(Paragraph paragraph);

		void onTypesetFailure(Throwable throwable);
	}

	public static class Args extends DefaultRecyclable {
		private static final ObjectPool<Args> POOL = new ObjectPool<>(32);
		private Paragraph paragraph;
		private int width;

		private Listener listener;

		private Args() {
		}

		@Override
		protected void onRecycle() {
			paragraph = null;
			listener = null;
			width = 0;
			POOL.release(this);
		}

		public static Args obtain(@NonNull Paragraph paragraph,
								  @IntRange(from = 1) int width) {
			return obtain(paragraph, width, null);
		}

		public static Args obtain(@NonNull Paragraph paragraph,
								  @IntRange(from = 1) int width,
								  @Nullable Listener listener) {
			Args args = POOL.acquire();
			if (args == null) {
				args = new Args();
			}

			args.paragraph = paragraph;
			args.width = width;
			args.listener = listener;
			args.reuse();
			return args;
		}
	}
}
