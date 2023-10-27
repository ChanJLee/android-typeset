package me.chan.texas.di.core;

import me.chan.texas.renderer.core.TypesetEngine;
import me.chan.texas.renderer.core.WorkerScheduler;

import dagger.Subcomponent;
import me.chan.texas.renderer.core.worker.MixWorker;

@Subcomponent
public interface TextEngineCoreComponent {
	@Subcomponent.Factory
	interface Factory {
		TextEngineCoreComponent create();
	}

	void inject(TypesetEngine typesetEngine);

	void inject(WorkerScheduler workerScheduler);

	void inject(MixWorker mixTask);
}
