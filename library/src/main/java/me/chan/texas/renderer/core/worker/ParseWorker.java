package me.chan.texas.renderer.core.worker;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.shanbay.lib.log.Log;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
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
		mMessager.addListener(new WorkerMessager.Listener() {
			@Override
			public boolean handleMessage(int id, WorkerMessager.WorkerMessage value) {
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
			}
		});
	}

	public <T> void submit(int id, Args args) {
		mTaskQueue.submit(id, args, this, this);
	}

	@Override
	public void onStart(int id, Args args) {
		/* do nothing */
	}

	@Override
	public void onSuccess(int id, Args args, Paragraph ret) {
		WorkerMessager.WorkerMessage message = WorkerMessager.WorkerMessage.obtain(TYPE_SUCCESS, args, ret);
		mMessager.send(id, message);
	}

	@Override
	public void onError(int id, Args args, Throwable throwable) {
		Log.w("ParseWorker", throwable);
		WorkerMessager.WorkerMessage message = WorkerMessager.WorkerMessage.obtain(TYPE_ERROR, args, throwable);
		mMessager.send(id, message);
	}

	@Override
	public Paragraph run(int id, Args args) throws Throwable {
		try {
			return args.source.open();
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

		private Args() {
		}

		@Override
		public void recycle() {
			if (isRecycled()) {
				return;
			}

			listener = null;
			source = null;
			super.recycle();
			POOL.release(this);
		}

		public static Args obtain(@NonNull Source<Paragraph> source,
								  @NonNull Listener listener) {
			Args args = POOL.acquire();
			if (args == null) {
				args = new Args();
			}

			args.source = source;
			args.listener = listener;
			args.reuse();
			return args;
		}
	}
}
