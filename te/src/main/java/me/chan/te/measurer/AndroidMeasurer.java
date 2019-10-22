package me.chan.te.measurer;

import android.graphics.Paint;
import android.text.Layout;
import android.text.TextPaint;

public class AndroidMeasurer implements Measurer {

	private TextPaint mTextPaint;
	private float mFontSpacing;
	private float mDesiredHeight;

	public AndroidMeasurer(TextPaint textPaint) {
		refresh(textPaint);
	}

	public void refresh(TextPaint textPaint) {
		mTextPaint = textPaint;
		Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
		mDesiredHeight = fontMetrics.bottom - fontMetrics.top;
		mFontSpacing = mTextPaint.getFontSpacing();
	}

	@Override
	public float getDesiredWidth(CharSequence charSequence, int start, int end) {
		return Layout.getDesiredWidth(charSequence, start, end, mTextPaint);
	}

	@Override
	public float getDesiredHeight(CharSequence charSequence, int start, int end) {
		return mFontSpacing;
	}

	@Override
	public float getFontSpacing() {
		return mDesiredHeight;
	}
}
