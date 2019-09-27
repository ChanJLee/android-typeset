package me.chan.te.config;

import android.annotation.SuppressLint;

import java.util.HashMap;
import java.util.Map;

import me.chan.te.data.Gravity;

/**
 * line options
 */
public class LineAttributes {
	private Attribute mDefaultAttribute;
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Attribute> mMap = new HashMap<>();

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
		if (mMap.containsKey(lineNumber)) {
			return mMap.get(lineNumber);
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
