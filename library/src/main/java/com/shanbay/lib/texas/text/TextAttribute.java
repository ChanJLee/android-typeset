package com.shanbay.lib.texas.text;

import android.annotation.SuppressLint;

import androidx.collection.SparseArrayCompat;

import com.shanbay.lib.log.Log;
import com.shanbay.lib.texas.annotations.Hidden;
import com.shanbay.lib.texas.measurer.Measurer;

/**
 * 文本属性
 */
@Hidden
public class TextAttribute {
	private LineAttribute mDefaultAttribute;
	@SuppressLint("UseSparseArrays")
	private SparseArrayCompat<LineAttribute> mMap = new SparseArrayCompat<>(2000);

	private float mHyphenWidth;
	private float mSpaceWidth;
	private float mSpaceStretch;
	private float mSpaceShrink;
	private float mIndentWidth;
	private float mHyphenHeight;

	public TextAttribute(Measurer measurer) {
		refresh(measurer);
	}

	public void refresh(Measurer measurer) {
		mHyphenWidth = measurer.getDesiredWidth("-", 0, 1, null);
		mHyphenHeight = measurer.getDesiredHeight("-", 0, 1, null);
		mSpaceWidth = mHyphenWidth * 1.2f;
		mSpaceStretch = mHyphenWidth * 0.6f;
		mSpaceShrink = mHyphenWidth * 0.2f;

		// 首行缩进四个空格
		mIndentWidth = mHyphenWidth * 4;

		i("space width: " + mSpaceWidth + " space shrink: " + mSpaceShrink + " space stretch: " + mSpaceStretch);
	}

	public TextAttribute add(int lineNumber, LineAttribute attribute) {
		mMap.put(lineNumber, attribute);
		return this;
	}

	public void remove(int lineNumber) {
		mMap.remove(lineNumber);
	}

	public void removeAllLineAttribute() {
		mMap.clear();
	}

	public LineAttribute get(int lineNumber) {
		LineAttribute attribute = mMap.get(lineNumber);
		if (attribute != null) {
			return attribute;
		}
		return mDefaultAttribute;
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

	public void setDefaultAttribute(LineAttribute defaultAttribute) {
		mDefaultAttribute = defaultAttribute;
	}

	/**
	 * 行属性
	 */
	@Hidden
	public static class LineAttribute {
		private float mLineWidth;
		private Gravity mGravity;

		public LineAttribute(float lineWidth, Gravity gravity) {
			mLineWidth = lineWidth;
			mGravity = gravity;
		}

		public float getLineWidth() {
			return mLineWidth;
		}

		public Gravity getGravity() {
			return mGravity;
		}
	}

	private static void i(String msg) {
		Log.i("TexasText", msg);
	}
}
