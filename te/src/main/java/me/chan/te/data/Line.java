package me.chan.te.data;

import java.util.List;

public class Line {
	private List<? extends Element> mElements;
	private float mLineHeight;
	private float mSpaceWidth;
	private float mRatio;

	public Line(List<? extends Element> elements, float lineHeight, float spaceWidth, float ratio) {
		mElements = elements;
		mLineHeight = lineHeight;
		mSpaceWidth = spaceWidth;
		mRatio = ratio;
	}

	public List<? extends Element> getElements() {
		return mElements;
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
