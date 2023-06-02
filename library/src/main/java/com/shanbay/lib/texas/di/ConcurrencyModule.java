package com.shanbay.lib.texas.di;

import com.shanbay.lib.texas.concurrency.Messager;
import com.shanbay.lib.texas.concurrency.AndroidMessager;
import com.shanbay.lib.texas.renderer.core.sync.AndroidWorkerMessager;
import com.shanbay.lib.texas.renderer.core.sync.WorkerMessager;
import com.shanbay.lib.texas.utils.concurrency.AndroidTaskQueue;
import com.shanbay.lib.texas.utils.concurrency.TaskQueue;

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
}
