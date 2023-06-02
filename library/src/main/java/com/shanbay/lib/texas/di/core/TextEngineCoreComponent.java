package com.shanbay.lib.texas.di.core;

import com.shanbay.lib.texas.renderer.core.TypesetEngine;
import com.shanbay.lib.texas.renderer.core.WorkerScheduler;

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
