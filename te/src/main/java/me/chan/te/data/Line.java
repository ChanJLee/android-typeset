package me.chan.te.data;

import java.util.List;

public class Line {
	private List<Box<?>> mBoxes;
	private float mLineHeight;
	private float mSpaceWidth;
	private float mRatio;

	public Line(List<Box<?>> boxes, float lineHeight, float spaceWidth, float ratio) {
		mBoxes = boxes;
		mLineHeight = lineHeight;
		mSpaceWidth = spaceWidth;
		mRatio = ratio;
	}

	public List<Box<?>> getBoxes() {
		return mBoxes;
	}

	public float getLineHeight() {
		return mLineHeight;
	}

	public float getSpaceWidth() {
		return mSpaceWidth;
	}

	public float getRatio() {
		return mRatio;
	}
}
