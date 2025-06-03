package me.chan.texas.renderer.core.worker;

import androidx.annotation.RestrictTo;

import me.chan.texas.utils.concurrency.Worker;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class OddWorker implements Worker.Listener<Runnable, Void>, Worker.Task<Runnable, Void> {

	public void submit(Worker.Token token, Worker taskQueue, Runnable runnable) {
		taskQueue.async(token, runnable, this, this);
	}

	@Override
	public void onStart(Worker.Token token, Runnable args) {

	}

	@Override
	public void onSuccess(Worker.Token token, Runnable args, Void ret) {

	}

	@Override
	public void onError(Worker.Token token, Runnable args, Throwable error) {

	}

	@Override
	public Void run(Worker.Token token, Runnable args) throws Throwable {
		try {
			args.run();
		} catch (Throwable ignore) {
			/* do nothing */
		}
		return null;
	}
}
