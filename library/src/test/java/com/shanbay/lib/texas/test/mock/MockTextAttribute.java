package com.shanbay.lib.texas.test.mock;

import com.shanbay.lib.texas.measurer.MockMeasurer;
import com.shanbay.lib.texas.text.TextAttribute;

public class MockTextAttribute extends TextAttribute {
	private final MockTextPaint mTextPaint;

	public MockTextAttribute(MockTextPaint textPaint) {
		super(new MockMeasurer(textPaint));
		mTextPaint = textPaint;
	}

	@Override
	public float getHyphenWidth() {
		return mTextPaint.getMockTextSize();
	}

	@Override
	public float getSpaceWidth() {
		return getHyphenWidth();
	}

	@Override
	public float getSpaceStretch() {
		return getSpaceWidth() * 1.1f;
	}

	@Override
	public float getSpaceShrink() {
		return getSpaceWidth() * 0.9f;
	}
}
