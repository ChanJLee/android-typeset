package me.chan.te.test;

import android.text.TextPaint;

import me.chan.te.data.Box;

public class MockMeasurer implements Box.Measurer {

	@Override
	public float getDesiredWidth(CharSequence charSequence, int start, int end, TextPaint textPaint) {
		return (end - start) * ((MockTextPaint) textPaint).getMockTextSize();
	}
}
