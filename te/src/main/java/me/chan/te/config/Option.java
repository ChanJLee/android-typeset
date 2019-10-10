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
	public float hyphenWidth;
	public float spaceWidth;
	public float spaceStretch;
	public float spaceShrink;
	public float indentWidth;
	public float lineSpacing;

	public Option(TextPaint textPaint) {
		refresh(textPaint);
	}

	public void refresh(TextPaint textPaint) {
		hyphenWidth = Layout.getDesiredWidth("-", textPaint);
		spaceWidth = hyphenWidth;
		spaceStretch = spaceWidth * 1.1f;
		spaceShrink = spaceWidth * 0.9f;

		// 首行缩进四个空格
		indentWidth = spaceWidth * 4;
		// 1.0 倍行间距
		lineSpacing = (int) (textPaint.getFontSpacing());
	}

	// TODO call get
	public float getHyphenWidth() {
		return hyphenWidth;
	}

	public float getSpaceWidth() {
		return spaceWidth;
	}

	public float getSpaceStretch() {
		return spaceStretch;
	}

	public float getSpaceShrink() {
		return spaceShrink;
	}

	public float getIndentWidth() {
		return indentWidth;
	}

	public float getLineSpacing() {
		return lineSpacing;
	}
}
