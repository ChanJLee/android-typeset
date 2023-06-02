package me.chan.texas.di;

import androidx.annotation.NonNull;

import me.chan.texas.concurrency.Messager;
import me.chan.texas.renderer.MockMessager;
import me.chan.texas.renderer.core.sync.WorkerMessager;
import me.chan.texas.utils.concurrency.TaskQueue;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class FakeConcurrencyModule {

	@Provides
	public Messager provideThreadHandler(Messager.HandleListener listener) {
		return new MockMessager(listener);
	}

	@Provides
	public WorkerMessager provideWorkerMessager() {
		return new WorkerMessager() {
			@Override
			public void send(int id, WorkerMessage message) {
				for (Listener listener : mListeners) {
					if (listener.handleMessage(id, message)) {
						return;
					}
				}
			}

			@Override
			public void clear(int id) {

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

	private static class MockTaskQueue implements TaskQueue {
		@Override
		public <A, R> void submit(int id, @NonNull A args, @NonNull Task<A, R> task, @NonNull Listener<A, R> listener) {
			try {
				listener.onStart(id, args);
				listener.onSuccess(id, args, task.run(id, args));
			} catch (Throwable throwable) {
				listener.onError(id, args, throwable);
			}
		}

		@Override
		public <A, R> R submitSync(int id, @NonNull A args, @NonNull Task<A, R> task) throws Throwable {
			return task.run(id, args);
		}

		@Override
		public void cancel(int id) {

		}
	}
}