package me.chan.texas.di;

import com.shanbay.lib.texas.test.mock.MockTextPaint;

import me.chan.texas.measurer.MeasureFactory;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.measurer.MockMeasurer;
import me.chan.texas.misc.PaintSet;

public class FakeMeasureFactory implements MeasureFactory {

	private final MockTextPaint mMockTextPaint = new MockTextPaint();

	private static class Holder {
		private static final FakeMeasureFactory INSTANCE = new FakeMeasureFactory();
	}

	public static FakeMeasureFactory getInstance() {
		return Holder.INSTANCE;
	}

	public MockTextPaint getMockTextPaint() {
		return mMockTextPaint;
	}

	@Override
	public Measurer create(PaintSet paintSet) {
		return new MockMeasurer(mMockTextPaint);
	}
}
