package com.shanbay.lib.texas.test.mock;

import android.text.TextPaint;

import com.shanbay.lib.texas.measurer.Measurer;
import com.shanbay.lib.texas.text.TextStyle;

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
