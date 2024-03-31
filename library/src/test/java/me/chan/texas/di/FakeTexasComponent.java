package me.chan.texas.di;


import dagger.Component;

@Component(modules = {SubModule.class, FakeConcurrencyModule.class, FakePlatformModule.class})
public interface FakeTexasComponent extends TexasComponent {

	@Component.Factory
	interface Factory {
		FakeTexasComponent create();
	}
}
