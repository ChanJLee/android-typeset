package me.chan.texas.renderer.core.worker;

import androidx.annotation.RestrictTo;

import me.chan.texas.utils.concurrency.TaskQueue;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class OddWorker implements TaskQueue.Listener<Runnable, Void>, TaskQueue.Task<Runnable, Void> {

	public void submit(TaskQueue.Token token, TaskQueue taskQueue, Runnable runnable) {
		taskQueue.submit(token, runnable, this, this);
	}

	@Override
	public void onStart(TaskQueue.Token token, Runnable args) {

	}

	@Override
	public void onSuccess(TaskQueue.Token token, Runnable args, Void ret) {

	}

	@Override
	public void onError(TaskQueue.Token token, Runnable args, Throwable throwable) {

	}

	@Override
	public Void run(TaskQueue.Token token, Runnable args) throws Throwable {
		try {
			args.run();
		} catch (Throwable ignore) {
			/* do nothing */
		}
		return null;
	}
}
