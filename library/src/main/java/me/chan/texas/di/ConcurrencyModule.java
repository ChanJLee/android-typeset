package me.chan.texas.di;

import me.chan.texas.concurrency.Messager;
import me.chan.texas.concurrency.AndroidMessager;
import me.chan.texas.renderer.core.sync.AndroidWorkerMessager;
import me.chan.texas.renderer.core.sync.WorkerMessager;
import me.chan.texas.utils.concurrency.AndroidTaskQueue;
import me.chan.texas.utils.concurrency.TaskQueue;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ConcurrencyModule {

	@Provides
	public Messager provideThreadHandler() {
		return new AndroidMessager();
	}

	@Provides
	public WorkerMessager provideWorkerMessager() {
		return new AndroidWorkerMessager();
	}

	@Provides
	@Named("MiscTask")
	public TaskQueue provideMiscTaskQueue() {
		return new AndroidTaskQueue("MiscTask");
	}

	@Provides
	@Named("RendererTask")
	public TaskQueue provideRendererTaskQueue() {
		return new AndroidTaskQueue("RendererTask");
	}

	@Provides
	@Named("MixTask")
	public TaskQueue provideTypesetQueue() {
		return new AndroidTaskQueue("MixTask");
	}
}
