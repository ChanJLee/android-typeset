package me.chan.te.config;

import android.text.Layout;
import android.text.TextPaint;

public class Option {
	public final float INFINITY = 1000;
	public final int HYPHEN_PENALTY = 100;
	public final float DEMERITS_LINE = 1;
	// 对应 α
	public final float DEMERITS_FLAGGED = 100;
	// 对应 γ
	public final float DEMERITS_FITNESS = 3000;
	public final int MAX_RELAYOUT_TIMES = 30;
	public final int MIN_HYPER_LEN = 4;
	public final float MIN_SHRINK_RATIO = -0.2f;
	public final float STRETCH_STEP_RATIO = 0.2f;

	/**
	 * 跟随box的text size变化
	 */
	private float mHyphenWidth;
	private float mSpaceWidth;
	private float mSpaceStretch;
	private float mSpaceShrink;
	private float mIndentWidth;
	private float mLineSpacing;

	public Option(TextPaint textPaint) {
		refresh(textPaint);
	}

	public void refresh(TextPaint textPaint) {
		mHyphenWidth = Layout.getDesiredWidth("-", textPaint);
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
