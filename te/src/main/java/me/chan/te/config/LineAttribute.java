package me.chan.te.config;

import me.chan.te.data.Gravity;

public class LineAttribute {
	private float mLineWidth;
	private Gravity mGravity;
	private int mLineVerticalSpace;

	public LineAttribute(float lineWidth) {
		this(lineWidth, Gravity.LEFT, 20);
	}

	public LineAttribute(float lineWidth, Gravity gravity, int lineVerticalSpace) {
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
