package me.chan.texas.di;

import me.chan.texas.renderer.core.sync.AndroidMsgHandler;
import me.chan.texas.renderer.core.sync.MsgHandler;
import me.chan.texas.utils.concurrency.AndroidWorker;
import me.chan.texas.utils.concurrency.Worker;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ConcurrencyModule {

	@Provides
	public MsgHandler provideMsgHandler() {
		return new AndroidMsgHandler();
	}

	@Provides
	@Named("RendererWorker")
	public Worker provideRendererWorker() {
		return new AndroidWorker("RendererWorker");
	}

	@Provides
	@Named("BackgroundWorker")
	public Worker provideBackgroundWorker() {
		return new AndroidWorker("BackgroundWorker");
	}
}
