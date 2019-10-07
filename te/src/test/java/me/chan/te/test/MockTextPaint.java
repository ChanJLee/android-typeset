package me.chan.te.test;

import android.graphics.Rect;
import android.text.TextPaint;

public class MockTextPaint extends TextPaint {
	public static final int MOCK_TEXT_SIZE = 13;
	public static final int MOCK_TEXT_HEIGHT = 20;

	@Override
	public void getTextBounds(String text, int start, int end, Rect bounds) {
		bounds.left = bounds.top = 0;
		bounds.right = (end - start) * MOCK_TEXT_SIZE;
		bounds.bottom = MOCK_TEXT_HEIGHT;
	}
}
