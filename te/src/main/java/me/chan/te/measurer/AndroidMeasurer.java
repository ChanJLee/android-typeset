package me.chan.te.measurer;

import android.graphics.Paint;
import android.text.Layout;
import android.text.TextPaint;

public class AndroidMeasurer implements Measurer {

	private TextPaint mTextPaint;

	public AndroidMeasurer(TextPaint textPaint) {
		mTextPaint = textPaint;
	}

	@Override
	public float getDesiredWidth(CharSequence charSequence, int start, int end) {
		return Layout.getDesiredWidth(charSequence, start, end, mTextPaint);
	}

	@Override
	public float getDesiredHeight(CharSequence charSequence, int start, int end) {
		Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
		return fontMetrics.bottom - fontMetrics.top;
	}

	@Override
	public float getFontSpacing() {
		return mTextPaint.getFontSpacing();
	}
}
