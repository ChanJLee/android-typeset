package com.shanbay.lib.texas.di;

import com.shanbay.lib.texas.di.core.TextEngineCoreComponent;

import dagger.Component;

@Component(modules = {SubModule.class, ConcurrencyModule.class})
public interface TexasComponent {

	TextEngineCoreComponent.Factory coreComponent();
}
