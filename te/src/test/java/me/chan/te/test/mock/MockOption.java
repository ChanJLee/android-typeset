package me.chan.te.test.mock;

import me.chan.te.config.Option;

public class MockOption extends Option {
	private MockTextPaint mTextPaint;

	public MockOption(MockTextPaint textPaint) {
		super(new MockMeasurer(textPaint), textPaint);
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

	@Override
	public float getLineSpacing() {
		return mTextPaint.getMockTextHeight();
	}
}
