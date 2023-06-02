package me.chan.texas.di.core;

import me.chan.texas.renderer.core.TypesetEngine;
import me.chan.texas.renderer.core.WorkerScheduler;

import dagger.Subcomponent;

@Subcomponent
public interface TextEngineCoreComponent {
	@Subcomponent.Factory
	interface Factory {
		TextEngineCoreComponent create();
	}

	void inject(TypesetEngine core);

	void inject(WorkerScheduler core);
}
