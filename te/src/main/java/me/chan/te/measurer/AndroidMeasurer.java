package me.chan.te.measurer;

import android.graphics.Paint;
import android.text.Layout;
import android.text.TextPaint;

public class AndroidMeasurer implements Measurer {

	@Override
	public float getDesiredWidth(CharSequence charSequence, int start, int end, TextPaint textPaint) {
		return Layout.getDesiredWidth(charSequence, start, end, textPaint);
	}

	@Override
	public float getDesiredHeight(CharSequence charSequence, int start, int end, TextPaint textPaint) {
		Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
		return fontMetrics.bottom - fontMetrics.top;
	}

	@Override
	public float getFontSpacing(TextPaint textPaint) {
		return textPaint.getFontSpacing();
	}
}
