package me.chan.te.config;

import me.chan.te.measurer.Measurer;

// TODO
// 对于不同的text size, 空格的参数是一样的
// 但是 - hyphen不一样
public class Option {

	/**
	 * 跟随box的text size变化
	 */
	private float mHyphenWidth;
	private float mSpaceWidth;
	private float mSpaceStretch;
	private float mSpaceShrink;
	private float mIndentWidth;
	private float mLineSpacing;
	private float mHyphenHeight;

	public Option(Measurer measurer) {
		refresh(measurer);
	}

	public void refresh(Measurer measurer) {
		mHyphenWidth = measurer.getDesiredWidth("-", 0, 1);
		mHyphenHeight = measurer.getDesiredHeight("-", 0, 1);
		mSpaceWidth = mHyphenWidth;
		mSpaceStretch = mSpaceWidth * 1.1f;
		mSpaceShrink = mSpaceWidth * 0.9f;

		// 首行缩进四个空格
		mIndentWidth = mSpaceWidth * 4;
		// 1.0 倍行间距
		mLineSpacing = (int) (measurer.getFontSpacing());
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

	public float getHyphenHeight() {
		return mHyphenHeight;
	}
}
