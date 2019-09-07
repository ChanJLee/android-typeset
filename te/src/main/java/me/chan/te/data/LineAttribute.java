package me.chan.te.data;

public class LineAttribute {
	private float mLineWidth;
	private Gravity mGravity;
	private int mLineSpace;

	public LineAttribute(float lineWidth) {
		this(lineWidth, Gravity.LEFT, 20);
	}

	public LineAttribute(float lineWidth, Gravity gravity, int lineSpace) {
		mLineWidth = lineWidth;
		mGravity = gravity;
		mLineSpace = lineSpace;
	}

	public float getLineWidth() {
		return mLineWidth;
	}

	public Gravity getGravity() {
		return mGravity;
	}

	public int getLineSpace() {
		return mLineSpace;
	}
}
