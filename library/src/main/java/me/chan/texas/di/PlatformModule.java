package me.chan.texas.di;

import dagger.Module;
import dagger.Provides;
import me.chan.texas.measurer.AndroidMeasureFactory;
import me.chan.texas.measurer.MeasureFactory;

@Module
public class PlatformModule {

	@Provides
	public MeasureFactory provideMeasureFactory() {
		return AndroidMeasureFactory.getInstance();
	}
}
