package me.chan.texas.measurer;

import me.chan.texas.misc.PaintSet;

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
