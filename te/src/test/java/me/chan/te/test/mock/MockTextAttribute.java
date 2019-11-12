package me.chan.te.test.mock;

import me.chan.te.text.TextAttribute;

public class MockTextAttribute extends TextAttribute {
	private MockTextPaint mTextPaint;

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

	@Override
	public float getIndentWidth() {
		return getSpaceWidth() * 4;
	}
}
