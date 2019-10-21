package me.chan.te.data;

import java.util.List;

public class Line {
	private List<Box> mBoxes;
	private float mLineHeight;
	private float mLineWidth;
	private float mRatio;

	public Line(List<Box> boxes, float lineHeight, float lineWidth, float ratio) {
		mBoxes = boxes;
		mLineHeight = lineHeight;
		mRatio = ratio;
		mLineWidth = lineWidth;
	}

	public List<Box> getBoxes() {
		return mBoxes;
	}

	public float getLineHeight() {
		return mLineHeight;
	}

	public float getRatio() {
		return mRatio;
	}

	public float getLineWidth() {
		return mLineWidth;
	}
}
