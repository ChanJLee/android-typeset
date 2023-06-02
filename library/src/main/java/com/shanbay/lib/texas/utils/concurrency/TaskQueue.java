package com.shanbay.lib.texas.utils.concurrency;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

public interface TaskQueue {
	<A, R> void submit(int id, @NonNull A args, @NonNull Task<A, R> task, @NonNull Listener<A, R> listener);

	<A, R> R submitSync(int id, @NonNull A args, @NonNull Task<A, R> task) throws Throwable;

	void cancel(int id);

	interface Listener<A, R> {
		@WorkerThread
		void onStart(int id, A args);

		@WorkerThread
		void onSuccess(int id, A args, R ret);

		@WorkerThread
		void onError(int id, A args, Throwable throwable);
	}

	interface Task<A, R> {
		R run(int id, A args) throws Throwable;
	}
}
