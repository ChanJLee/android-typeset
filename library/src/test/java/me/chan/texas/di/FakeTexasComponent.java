package me.chan.texas.di;

import me.chan.texas.concurrency.Messager;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = {SubModule.class, FakeConcurrencyModule.class})
public interface FakeTexasComponent extends TexasComponent {

	@Component.Factory
	interface Factory {
		FakeTexasComponent create(@BindsInstance Messager.HandleListener listener);
	}
}
