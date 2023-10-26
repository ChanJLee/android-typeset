package me.chan.texas.di;

import me.chan.texas.di.core.TextEngineCoreComponent;

import dagger.Component;

@Component(modules = {SubModule.class, ConcurrencyModule.class, PlatformModule.class})
public interface TexasComponent {

	TextEngineCoreComponent.Factory coreComponent();
}
