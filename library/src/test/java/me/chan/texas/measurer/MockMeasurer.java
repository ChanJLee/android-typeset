package me.chan.texas.measurer;

import me.chan.texas.misc.PaintSet;
import me.chan.texas.test.mock.MockTextPaint;
import me.chan.texas.text.TextStyle;

public class MockMeasurer implements Measurer {
	private final MockTextPaint mMockTextPaint;

	public MockMeasurer(MockTextPaint paint) {
		mMockTextPaint = paint;
	}

	@Override
	public CharSequenceSpec getBaseSpec() {
		CharSequenceSpec spec = new CharSequenceSpec();
		measure("-", 0, 1, null, null, spec);
		return spec;
	}

	@Override
	public void measure(CharSequence charSequence, int start, int end, TextStyle textStyle, Object tag, CharSequenceSpec spec) {
		spec.reset((end - start) * mMockTextPaint.getMockTextSize(),
				mMockTextPaint.getMockTextHeight(), 0);
	}

	@Override
	public void refresh(PaintSet paintSet) {
		// NOOP
	}
}
