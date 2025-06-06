package me.chan.texas.di;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import me.chan.texas.renderer.core.sync.MsgHandler;
import me.chan.texas.utils.concurrency.MockTaskQueue;
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
	@Named("RendererWorker")
	public Worker provideRendererTaskQueue() {
		return new MockTaskQueue();
	}

	@Provides
	@Named("BackgroundWorker")
	public Worker provideComputeQueue() {
		return new MockTaskQueue();
	}
}