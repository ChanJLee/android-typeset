package me.chan.texas.measurer;

import androidx.annotation.RestrictTo;

import me.chan.texas.misc.PaintSet;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class AndroidMeasureFactory implements MeasureFactory {

	private static class Holder {
		private static final AndroidMeasureFactory INSTANCE = new AndroidMeasureFactory();
	}

	@Override
	public Measurer create(PaintSet paintSet) {
		return new AndroidMeasurer(paintSet);
	}

	public static AndroidMeasureFactory getInstance() {
		return Holder.INSTANCE;
	}
}
