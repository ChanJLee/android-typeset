package me.chan.te.measurer;

import android.graphics.Paint;
import android.text.BoringLayout;
import android.text.TextPaint;

/**
 * android的文本测量器
 */
public class AndroidMeasurer implements Measurer {

	private TextPaint mTextPaint;
	private float mFontSpacing;
	private float mDesiredHeight;
	private float mDescent;

	public AndroidMeasurer(TextPaint textPaint) {
		refresh(textPaint);
	}

	/**
	 * 刷新当前的text paint
	 *
	 * @param textPaint text paint
	 */
	public void refresh(TextPaint textPaint) {
		mTextPaint = textPaint;
		Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
		mDesiredHeight = fontMetrics.descent - fontMetrics.ascent;
		mFontSpacing = mTextPaint.getFontSpacing();
		mDescent = fontMetrics.descent;
	}

	@Override
	public float getDesiredWidth(CharSequence charSequence, int start, int end) {
		// 不能使用 TextPaint getTextBounds
		// vivo 手机使用这个方法慢的出奇
		// BoringLayout 是用来测量单行文本的
		return BoringLayout.getDesiredWidth(charSequence, start, end, mTextPaint);
	}

	@Override
	public float getDesiredHeight(CharSequence charSequence, int start, int end) {
		return mFontSpacing;
	}

	@Override
	public float getLineSpacing() {
		return mDesiredHeight;
	}

	@Override
	public float getDescent() {
		return mDescent;
	}
}
