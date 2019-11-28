package me.chan.te.test.mock;

import android.text.TextPaint;

import me.chan.te.measurer.Measurer;
import me.chan.te.text.TextStyle;

public class MockMeasurer implements Measurer {
	private MockTextPaint mMockTextPaint;

	public MockMeasurer(MockTextPaint paint) {
		mMockTextPaint = paint;
	}

	@Override
	public float getDesiredWidth(CharSequence charSequence, int start, int end, TextStyle textStyle) {
		return (end - start) * mMockTextPaint.getMockTextSize();
	}

	@Override
	public float getDesiredHeight(CharSequence charSequence, int start, int end, TextStyle textStyle) {
		return mMockTextPaint.getMockTextHeight();
	}

	@Override
	public void refresh(TextPaint textPaint) {

	}

	@Override
	public float getFontTopPadding() {
		return 0;
	}

	@Override
	public float getFontBottomPadding() {
		return 0;
	}
}
