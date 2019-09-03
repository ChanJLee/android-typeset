package me.chan.te.data;

import java.util.List;

public class Line {
	public List<? extends Element> mElements;
	public float mLineHeight;

	public Line(List<? extends Element> elements, float lineHeight) {
		mElements = elements;
		mLineHeight = lineHeight;
	}

	public List<? extends Element> getElements() {
		return mElements;
	}

	public float getLineHeight() {
		return mLineHeight;
	}
}
