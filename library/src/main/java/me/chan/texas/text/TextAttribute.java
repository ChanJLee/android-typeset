package me.chan.texas.text;

import androidx.annotation.RestrictTo;

import me.chan.texas.Texas;
import me.chan.texas.measurer.Measurer;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.util.Log;

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

	public TextAttribute(Measurer measurer) {
		refresh(measurer);
	}

	public void refresh(Measurer measurer) {
		Measurer.CharSequenceSpec spec = measurer.measure("-", 0, 1, null, null);
		mHyphenWidth = spec.getWidth();
		mHyphenHeight = spec.getHeight();

		Texas.TypesetFactor factor = Texas.getTypesetFactor();
		mSpaceWidth = (float) Math.ceil(mHyphenWidth * factor.spaceWidthFactor);
		mSpaceStretch = (float) Math.ceil(mHyphenWidth * factor.spaceStretchFactor);
		mSpaceShrink = (float) Math.ceil(mHyphenWidth * factor.spaceShrinkFactor);

		i(toString());
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

	private static void i(String msg) {
		Log.i("TexasText", msg);
	}

	@Override
	public String toString() {
		return new StringBuilder(64)
				.append("TextAttribute{")
				.append("mHyphenWidth=")
				.append(mHyphenWidth)
				.append(", mSpaceWidth=")
				.append(mSpaceWidth)
				.append(", mSpaceStretch=")
				.append(mSpaceStretch)
				.append(", mSpaceShrink=")
				.append(mSpaceShrink)
				.append(", mHyphenHeight=")
				.append(mHyphenHeight)
				.append('}')
				.toString();
	}
}
