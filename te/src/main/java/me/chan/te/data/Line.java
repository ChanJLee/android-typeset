package me.chan.te.data;

import java.util.List;

public class Line {
	private List<? extends Element> mElements;
	private float mLineHeight;
	private float mSpaceWidth;

	public Line(List<? extends Element> elements, float lineHeight, float spaceWidth) {
		mElements = elements;
		mLineHeight = lineHeight;
		mSpaceWidth = spaceWidth;
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
}
