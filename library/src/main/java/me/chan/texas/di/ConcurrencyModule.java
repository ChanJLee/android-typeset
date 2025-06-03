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
	@Named("MiscTask")
	public Worker provideMiscWorker() {
		return new AndroidWorker("MiscTask");
	}

	@Provides
	@Named("RendererTask")
	public Worker provideRendererWorker() {
		return new AndroidWorker("RendererTask");
	}

	@Provides
	@Named("ComputeTask")
	public Worker provideComputeWorker() {
		return new AndroidWorker("ComputeTask");
	}
}
