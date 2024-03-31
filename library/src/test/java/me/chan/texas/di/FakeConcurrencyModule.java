package me.chan.texas.di;

import androidx.annotation.NonNull;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import me.chan.texas.renderer.core.sync.WorkerMessager;
import me.chan.texas.utils.concurrency.TaskQueue;

@Module
public class FakeConcurrencyModule {

	@Provides
	public WorkerMessager provideWorkerMessager() {
		return new WorkerMessager() {
			@Override
			public void send(TaskQueue.Token token, WorkerMessage message) {
				for (Listener listener : mListeners) {
					if (listener.handleMessage(token, message)) {
						return;
					}
				}
			}

			@Override
			public void clear(TaskQueue.Token token) {

			}
		};
	}

	@Provides
	@Named("MiscTask")
	public TaskQueue provideMiscTaskQueue() {
		return new MockTaskQueue();
	}

	@Provides
	@Named("RendererTask")
	public TaskQueue provideRendererTaskQueue() {
		return new MockTaskQueue();
	}

	@Provides
	@Named("ComputeTask")
	public TaskQueue provideComputeQueue() {
		return new MockTaskQueue();
	}

	private static class MockTaskQueue implements TaskQueue {

		@Override
		public <A, R> void submit(Token token, @NonNull A args, @NonNull Task<A, R> task, @NonNull Listener<A, R> listener) {
			try {
				listener.onStart(token, args);
				listener.onSuccess(token, args, task.run(token, args));
			} catch (Throwable throwable) {
				listener.onError(token, args, throwable);
			}
		}

		@Override
		public <A, R> R submitSync(Token token, @NonNull A args, @NonNull Task<A, R> task) throws Throwable {
			return task.run(token, args);
		}

		@Override
		public void cancel(Token token) {

		}
	}
}