package me.chan.te.config;

import android.annotation.SuppressLint;
import android.support.v4.util.SparseArrayCompat;

import me.chan.te.text.Gravity;

/**
 * 用来获取每一行的属性
 */
public class LineAttributes {
	private Attribute mDefaultAttribute;
	@SuppressLint("UseSparseArrays")
	private SparseArrayCompat<Attribute> mMap = new SparseArrayCompat<>();

	public LineAttributes(Attribute defaultAttribute) {
		mDefaultAttribute = defaultAttribute;
	}

	public LineAttributes add(int lineNumber, Attribute attribute) {
		mMap.put(lineNumber, attribute);
		return this;
	}

	public void remove(int lineNumber) {
		mMap.remove(lineNumber);
	}

	public Attribute get(int lineNumber) {
		Attribute attribute = mMap.get(lineNumber);
		if (attribute != null) {
			return attribute;
		}
		return mDefaultAttribute;
	}

	public static class Attribute {
		private float mLineWidth;
		private Gravity mGravity;
		private int mLineVerticalSpace;

		public Attribute(float lineWidth) {
			this(lineWidth, Gravity.LEFT, 20);
		}

		public Attribute(float lineWidth, Gravity gravity, int lineVerticalSpace) {
			mLineWidth = lineWidth;
			mGravity = gravity;
			mLineVerticalSpace = lineVerticalSpace;
		}

		public float getLineWidth() {
			return mLineWidth;
		}

		public Gravity getGravity() {
			return mGravity;
		}

		public int getLineVerticalSpace() {
			return mLineVerticalSpace;
		}
	}
}
