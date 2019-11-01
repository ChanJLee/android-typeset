package me.chan.te.text;

import android.text.TextPaint;

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

	private TextStyle() {
	}

	/**
	 * 更新text paint的样式
	 *
	 * @param textPaint text paint
	 */
	public abstract void update(TextPaint textPaint);

	/**
	 * 检查样式是否冲突
	 *
	 * @param other 另外一个style
	 * @return 是否和Other元素style冲突，如果不冲突，表示其对应的Box可以被合并
	 */
	public boolean isConflict(TextStyle other) {
		return !equals(other);
	}
}