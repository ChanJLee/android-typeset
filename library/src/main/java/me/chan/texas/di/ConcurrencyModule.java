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
	public Worker provideMiscTaskQueue() {
		return new AndroidWorker("MiscTask");
	}

	@Provides
	@Named("RendererTask")
	public Worker provideRendererTaskQueue() {
		return new AndroidWorker("RendererTask");
	}

	@Provides
	@Named("ComputeTask")
	public Worker provideComputeQueue() {
		return new AndroidWorker("ComputeTask");
	}
}
