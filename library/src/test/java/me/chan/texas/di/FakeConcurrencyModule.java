package me.chan.texas.di;

import androidx.annotation.NonNull;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import me.chan.texas.renderer.core.sync.MsgHandler;
import me.chan.texas.utils.concurrency.Worker;

@Module
public class FakeConcurrencyModule {

	@Provides
	public MsgHandler provideWorkerMessager() {
		return new MsgHandler() {
			@Override
			public void send(Worker.Token token, Msg message) {
				for (Listener listener : mListeners) {
					if (listener.handle(token, message)) {
						return;
					}
				}
			}

			@Override
			public void clear(Worker.Token token) {

			}
		};
	}

	@Provides
	@Named("MiscTask")
	public Worker provideMiscTaskQueue() {
		return new MockTaskQueue();
	}

	@Provides
	@Named("RendererTask")
	public Worker provideRendererTaskQueue() {
		return new MockTaskQueue();
	}

	@Provides
	@Named("ComputeTask")
	public Worker provideComputeQueue() {
		return new MockTaskQueue();
	}

	private static class MockTaskQueue implements Worker {

		@Override
		public <A, R> void async(Token token, @NonNull A args, @NonNull Task<A, R> task, @NonNull Listener<A, R> listener) {
			try {
				listener.onStart(token, args);
				listener.onSuccess(token, args, task.run(token, args));
			} catch (Throwable throwable) {
				listener.onError(token, args, throwable);
			}
		}

		@Override
		public <A, R> R sync(Token token, @NonNull A args, @NonNull Task<A, R> task) throws Throwable {
			return task.run(token, args);
		}

		@Override
		public void cancel(Token token) {

		}
	}
}