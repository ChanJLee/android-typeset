package me.chan.texas.di;

import dagger.Module;
import dagger.Provides;
import me.chan.texas.measurer.MeasureFactory;

@Module
public class FakePlatformModule {

	@Provides
	public MeasureFactory provideMeasureFactory() {
		return FakeMeasureFactory.getInstance();
	}
}
