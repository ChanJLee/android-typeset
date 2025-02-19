package me.chan.texas.di.core;

import me.chan.texas.renderer.TexasView;
import me.chan.texas.renderer.core.TypesetEngine;
import me.chan.texas.renderer.core.WorkerScheduler;

import dagger.Subcomponent;
import me.chan.texas.renderer.core.worker.LoadingWorker;
import me.chan.texas.renderer.core.worker.MixWorker;
import me.chan.texas.renderer.ui.RendererAdapterImpl;
import me.chan.texas.renderer.ui.text.ParagraphView;

@Subcomponent
public interface TextEngineCoreComponent {
	@Subcomponent.Factory
	interface Factory {
		TextEngineCoreComponent create();
	}

	void inject(TypesetEngine engine);

	void inject(WorkerScheduler scheduler);

	void inject(MixWorker worker);

	void inject(LoadingWorker worker);

	void inject(TexasView view);

	void inject(ParagraphView view);

	void inject(RendererAdapterImpl adapter);
}
