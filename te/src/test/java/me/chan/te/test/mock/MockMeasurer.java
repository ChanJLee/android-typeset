package me.chan.te.test.mock;

import android.text.TextPaint;

import me.chan.te.data.Box;

public class MockMeasurer implements Box.Measurer {
	private MockTextPaint mMockTextPaint;

	public MockMeasurer(MockTextPaint paint) {
		mMockTextPaint = paint;
	}

	@Override
	public float getDesiredWidth(CharSequence charSequence, int start, int end, TextPaint textPaint) {
		return (end - start) * mMockTextPaint.getMockTextSize();
	}

	@Override
	public float getDesiredHeight(CharSequence charSequence, int start, int end, TextPaint textPaint) {
		return mMockTextPaint.getMockTextHeight();
	}
}
