package me.chan.te.measurer;

import android.graphics.Paint;
import android.text.BoringLayout;
import android.text.TextPaint;

import me.chan.te.text.TextStyle;

/**
 * android的文本测量器
 */
public class AndroidMeasurer implements Measurer {

	private TextPaint mTextPaint;
	private float mDesiredHeight;
	private float mTopPadding = 0;
	private float mBottomPadding = 0;
	private TextPaint mWorkPaint = new TextPaint();

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
		mBottomPadding = fontMetrics.bottom;
		mTopPadding = fontMetrics.ascent - fontMetrics.top;
	}

	@Override
	public float getFontTopPadding() {
		return mTopPadding;
	}

	@Override
	public float getFontBottomPadding() {
		return mBottomPadding;
	}

	@Override
	public float getDesiredWidth(CharSequence charSequence, int start, int end, TextStyle textStyle) {
		TextPaint textPaint = mTextPaint;
		if (textStyle != null) {
			textPaint = mWorkPaint;
			textPaint.set(mTextPaint);
			textStyle.update(textPaint);
		}

		// 不能使用 TextPaint getTextBounds
		// vivo 手机使用这个方法慢的出奇
		// BoringLayout 是用来测量单行文本的
		return BoringLayout.getDesiredWidth(charSequence, start, end, textPaint);
	}

	@Override
	public float getDesiredHeight(CharSequence charSequence, int start, int end, TextStyle textStyle) {
		return mDesiredHeight;
	}
}
