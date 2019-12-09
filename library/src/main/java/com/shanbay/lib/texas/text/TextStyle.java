package com.shanbay.lib.texas.text;

import android.text.TextPaint;

/**
 * 文字样式
 */
public abstract class TextStyle {

	public static final TextStyle NONE = new TextStyle() {
		@Override
		public void update(TextPaint textPaint) {
			/* do nothing */
		}
	};
	public static final TextStyle BOLD = new TextStyle() {

		@Override
		public void update(TextPaint textPaint) {
			textPaint.setFakeBoldText(true);
		}
	};
	public static final TextStyle ITALIC = new TextStyle() {
		@Override
		public void update(TextPaint textPaint) {
			textPaint.setTextSkewX(-0.25f);
		}
	};

	public static final TextStyle BOLD_ITALIC = new TextStyle() {
		@Override
		public void update(TextPaint textPaint) {
			textPaint.setFakeBoldText(true);
			textPaint.setTextSkewX(-0.25f);
		}
	};

	/**
	 * 更新text paint的样式
	 *
	 * @param textPaint text paint
	 */
	public abstract void update(TextPaint textPaint);
}