package me.chan.texas.utils.concurrency;


import androidx.annotation.NonNull;

public class MockTaskQueue implements Worker {

	@Override
	public <A, R> void async(Token token, @NonNull A args, @NonNull Task<A, R> task) {
		try {
			task.onStart(token, args);
			task.onSuccess(token, args, task.exec(token, args));
		} catch (Throwable throwable) {
			task.onError(token, args, throwable);
		}
	}

	@Override
	public <A, R> R sync(Token token, @NonNull A args, @NonNull Task<A, R> task) throws Throwable {
		return task.exec(token, args);
	}

	@Override
	public void cancel(Token token) {

	}
}