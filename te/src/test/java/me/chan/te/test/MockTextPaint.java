package me.chan.te.test;

import android.graphics.Rect;
import android.text.TextPaint;

public class MockTextPaint extends TextPaint {
	private static final int MOCK_TEXT_SIZE = 13;

	private float mMockTextSize = MOCK_TEXT_SIZE;

	public float getMockTextSize() {
		return mMockTextSize;
	}

	public void setMockTextSize(float mockTextSize) {
		mMockTextSize = mockTextSize;
	}

	public float getMockTextHeight() {
		return mMockTextSize / 2;
	}

	@Override
	public void getTextBounds(String text, int start, int end, Rect bounds) {
		bounds.left = bounds.top = 0;
		bounds.right = (int) ((end - start) * mMockTextSize);
		bounds.bottom = (int) getMockTextHeight();
	}
}
