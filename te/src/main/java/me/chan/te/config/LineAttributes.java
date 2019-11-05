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
	private SparseArrayCompat<Attribute> mMap = new SparseArrayCompat<>(2000);

	public LineAttributes(Attribute defaultAttribute) {
		mDefaultAttribute = defaultAttribute;
	}

	public LineAttributes add(int lineNumber, Attribute attribute) {
		mMap.put(lineNumber, attribute);
		return this;
	}

	public Attribute getDefaultAttribute() {
		return mDefaultAttribute;
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
		private float mWordSpace;

		public Attribute(float lineWidth, Gravity gravity, float wordSpace) {
			mLineWidth = lineWidth;
			mGravity = gravity;
			mWordSpace = wordSpace;
		}

		public float getLineWidth() {
			return mLineWidth;
		}

		public Gravity getGravity() {
			return mGravity;
		}

		public float getWordSpaceWidth() {
			return mWordSpace;
		}
	}
}
