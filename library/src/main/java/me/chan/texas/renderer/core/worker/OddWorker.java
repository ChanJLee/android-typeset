package me.chan.texas.renderer.core.worker;

import androidx.annotation.RestrictTo;

import me.chan.texas.utils.concurrency.Worker;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class OddWorker {

	private final Worker.Listener<Runnable, Void> mListener = new Worker.Listener<Runnable, Void>() {
		@Override
		public void onStart(Worker.Token token, Runnable args) {

		}

		@Override
		public void onSuccess(Worker.Token token, Runnable args, Void ret) {

		}

		@Override
		public void onError(Worker.Token token, Runnable args, Throwable error) {

		}
	};
	private final Worker.Task<Runnable, Void> mTask = (token, args) -> {
		try {
			args.run();
		} catch (Throwable ignore) {
			/* do nothing */
		}
		return null;
	};

	public void submit(Worker.Token token, Worker taskQueue, Runnable runnable) {
		taskQueue.async(token, runnable, mTask, mListener);
	}
}
