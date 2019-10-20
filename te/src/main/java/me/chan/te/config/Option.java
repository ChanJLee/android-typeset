package me.chan.te.config;

import android.text.TextPaint;

import me.chan.te.measurer.Measurer;

public class Option {
	public static final float INFINITY = 1000;
	public static final int HYPHEN_PENALTY = 100;
	public static final float DEMERITS_LINE = 1;
	// 对应 α
	public static final float DEMERITS_FLAGGED = 100;
	// 对应 γ
	public static final float DEMERITS_FITNESS = 3000;
	public static final int MAX_RELAYOUT_TIMES = 30;
	public static final int MIN_HYPER_LEN = 4;
	public static final float MIN_SHRINK_RATIO = -0.2f;
	public static final float STRETCH_STEP_RATIO = 0.2f;

	/**
	 * 跟随box的text size变化
	 */
	private float mHyphenWidth;
	private float mSpaceWidth;
	private float mSpaceStretch;
	private float mSpaceShrink;
	private float mIndentWidth;
	private float mLineSpacing;

	public Option(Measurer measurer, TextPaint textPaint) {
		refresh(measurer, textPaint);
	}

	public void refresh(Measurer measurer, TextPaint textPaint) {
		mHyphenWidth = measurer.getDesiredWidth("-", 0, 1, textPaint);
		mSpaceWidth = mHyphenWidth;
		mSpaceStretch = mSpaceWidth * 1.1f;
		mSpaceShrink = mSpaceWidth * 0.9f;

		// 首行缩进四个空格
		mIndentWidth = mSpaceWidth * 4;
		// 1.0 倍行间距
		mLineSpacing = (int) (textPaint.getFontSpacing());
	}

	public float getHyphenWidth() {
		return mHyphenWidth;
	}

	public float getSpaceWidth() {
		return mSpaceWidth;
	}

	public float getSpaceStretch() {
		return mSpaceStretch;
	}

	public float getSpaceShrink() {
		return mSpaceShrink;
	}

	public float getIndentWidth() {
		return mIndentWidth;
	}

	public float getLineSpacing() {
		return mLineSpacing;
	}
}
