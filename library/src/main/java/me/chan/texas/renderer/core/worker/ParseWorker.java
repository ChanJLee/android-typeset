package me.chan.texas.renderer.core.worker;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.renderer.LoadingStrategy;
import me.chan.texas.renderer.core.sync.WorkerMessager;
import me.chan.texas.source.Source;
import me.chan.texas.text.Paragraph;
import me.chan.texas.utils.concurrency.TaskQueue;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class ParseWorker implements TaskQueue.Task<ParseWorker.Args, Paragraph>, TaskQueue.Listener<ParseWorker.Args, Paragraph> {
	private static final int TYPE_SUCCESS = 1;
	private static final int TYPE_ERROR = 2;

	private final TaskQueue mTaskQueue;
	private final WorkerMessager mMessager;

	public ParseWorker(TaskQueue taskQueue, WorkerMessager messager) {
		mTaskQueue = taskQueue;
		mMessager = messager;
		mMessager.addListener((token, value) -> {
			Args args = value.asArg(Args.class);
			if (args == null) {
				return false;
			}
			switch (value.type()) {
				case TYPE_SUCCESS:
					if (args.listener != null) {
						args.listener.onParseSuccess(value.value());
					}
					break;
				case TYPE_ERROR:
					if (args.listener != null) {
						args.listener.onParseFailure(value.error());
					}
					break;
			}
			args.recycle();
			return true;
		});
	}

	public void submit(TaskQueue.Token token, Args args) {
		mTaskQueue.submit(token, args, this, this);
	}

	public Paragraph submitSync(TaskQueue.Token token, Args args) throws Throwable {
		return mTaskQueue.submitSync(token, args, this);
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
		Log.w("ParseWorker", throwable);
		WorkerMessager.WorkerMessage message = WorkerMessager.WorkerMessage.obtain(TYPE_ERROR, args, throwable);
		mMessager.send(token, message);
	}

	@Override
	public Paragraph run(TaskQueue.Token token, Args args) throws Throwable {
		try {
			return args.source.open(args.strategy);
		} finally {
			try {
				args.source.close();
			} catch (Throwable throwable) {
				Log.w("ParseWorker", throwable);
			}
		}
	}

	public interface Listener {
		void onParseSuccess(Paragraph paragraph);

		void onParseFailure(Throwable throwable);
	}

	public static class Args extends DefaultRecyclable {
		private static final ObjectPool<Args> POOL = new ObjectPool<>(32);
		private Source<Paragraph> source;
		private Listener listener;

		private LoadingStrategy strategy;

		private Args() {
		}

		@Override
		protected void onRecycle() {
			listener = null;
			source = null;
			strategy = null;
			POOL.release(this);
		}

		public static Args obtain(@NonNull Source<Paragraph> source,
								  LoadingStrategy strategy,
								  @NonNull Listener listener) {
			Args args = POOL.acquire();
			if (args == null) {
				args = new Args();
			}

			args.source = source;
			args.strategy = strategy;
			args.listener = listener;
			args.reuse();
			return args;
		}
	}
}
