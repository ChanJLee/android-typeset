package me.chan.texas.renderer.core.worker;

import androidx.annotation.RestrictTo;

import me.chan.texas.utils.concurrency.Worker;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class OddWorker {

	private final Worker.Task<Runnable, Void> mTask = new Worker.Task<Runnable, Void>() {

		@Override
		protected Void onExec(Worker.Token token, Runnable args) {
			try {
				args.run();
			} catch (Throwable ignore) {
				/* do nothing */
			}
			return null;
		}
	};

	public void submit(Worker.Token token, Worker taskQueue, Runnable runnable) {
		taskQueue.async(token, runnable, mTask);
	}
}
