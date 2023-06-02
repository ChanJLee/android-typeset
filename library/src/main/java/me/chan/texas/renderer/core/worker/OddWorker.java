package me.chan.texas.renderer.core.worker;

import androidx.annotation.RestrictTo;

import me.chan.texas.utils.concurrency.TaskQueue;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class OddWorker implements TaskQueue.Listener<Runnable, Void>, TaskQueue.Task<Runnable, Void> {

	public void submit(int id, TaskQueue taskQueue, Runnable runnable) {
		taskQueue.submit(id, runnable, this, this);
	}

	@Override
	public void onStart(int id, Runnable args) {

	}

	@Override
	public void onSuccess(int id, Runnable args, Void ret) {

	}

	@Override
	public void onError(int id, Runnable args, Throwable throwable) {

	}

	@Override
	public Void run(int id, Runnable args) throws Throwable {
		try {
			args.run();
		} catch (Throwable ignore) {
			/* do nothing */
		}
		return null;
	}
}
