package me.chan.te.test.mock;

import android.graphics.Rect;
import android.text.TextPaint;

public class MockTextPaint extends TextPaint {
	private static final int MOCK_TEXT_SIZE = 13;

	private int mMockTextSize = MOCK_TEXT_SIZE;

	public MockTextPaint() {
		this(MOCK_TEXT_SIZE);
	}

	public MockTextPaint(int textSize) {
		mMockTextSize = textSize;
	}

	public int getMockTextSize() {
		return mMockTextSize;
	}

	public void setMockTextSize(int mockTextSize) {
		mMockTextSize = mockTextSize;
	}

	public int getMockTextHeight() {
		return mMockTextSize / 2;
	}

	@Override
	public void getTextBounds(String text, int start, int end, Rect bounds) {
		bounds.left = bounds.top = 0;
		bounds.right = (end - start) * getMockTextSize();
		bounds.bottom = getMockTextHeight();
	}
}
