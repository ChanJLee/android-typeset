package me.chan.te.config;

import me.chan.te.measurer.Measurer;

/**
 * 用于获取算法的通用选项
 * TODO
 * 对于不同的text size, 空格的参数是一样的
 * 但是 - hyphen不一样
 */
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
		mLineSpacing = measurer.getLineSpacing();
	}

	/**
	 * @return 返回字符'-'的宽度
	 */
	public float getHyphenWidth() {
		return mHyphenWidth;
	}

	/**
	 * @return 获取字符'-'的高度
	 */
	public float getHyphenHeight() {
		return mHyphenHeight;
	}

	/**
	 * @return 返回空格宽度
	 */
	public float getSpaceWidth() {
		return mSpaceWidth;
	}

	/**
	 * @return 返回空格拉伸后的宽度
	 */
	public float getSpaceStretch() {
		return mSpaceStretch;
	}

	/**
	 * @return 返回空格拉压缩后的宽度
	 */
	public float getSpaceShrink() {
		return mSpaceShrink;
	}

	/**
	 * @return 获取首行缩进的宽度
	 */
	public float getIndentWidth() {
		return mIndentWidth;
	}

	/**
	 * @return 获取每行建议间隔高度
	 */
	public float getLineSpacing() {
		return mLineSpacing;
	}
}
