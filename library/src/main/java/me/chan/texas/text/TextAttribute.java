package me.chan.texas.text;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import me.chan.texas.Texas;
import me.chan.texas.measurer.Measurer;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

/**
 * 文本属性
 */
@RestrictTo(LIBRARY)
public class TextAttribute {

	private float mHyphenWidth;
	private float mSpaceWidth;
	private float mSpaceStretch;
	private float mSpaceShrink;
	private float mHyphenHeight;
	private float mLineHeight;
	private float mBaselineOffset;

	public TextAttribute(Measurer measurer) {
		refresh(measurer);
	}

	public void refresh(Measurer measurer) {
		Measurer.CharSequenceSpec spec = measurer.getBaseSpec();
		mHyphenWidth = spec.getWidth();
		mHyphenHeight = spec.getHeight();
		mLineHeight = spec.getHeight();
		mBaselineOffset = spec.getBaselineOffset();

		Texas.TypesetFactor factor = Texas.getTypesetFactor();
		mSpaceWidth = (float) Math.ceil(mHyphenWidth * factor.spaceWidthFactor);
		mSpaceStretch = mHyphenWidth * factor.spaceStretchFactor;
		mSpaceShrink = mHyphenWidth * factor.spaceShrinkFactor;
	}

	public float getBaselineOffset() {
		return mBaselineOffset;
	}

	public float getLineHeight() {
		return mLineHeight;
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

	@NonNull
	@Override
	public String toString() {
		return "TextAttribute{" +
				"mHyphenWidth=" +
				mHyphenWidth +
				", mSpaceWidth=" +
				mSpaceWidth +
				", mSpaceStretch=" +
				mSpaceStretch +
				", mSpaceShrink=" +
				mSpaceShrink +
				", mHyphenHeight=" +
				mHyphenHeight +
				'}';
	}
}
