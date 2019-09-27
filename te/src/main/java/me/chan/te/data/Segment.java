package me.chan.te.data;

import java.util.List;

public final class Segment {
	private CharSequence mText;
	private int mStart;
	private int mEnd;
	private List<? extends Element> mElements;

	public Segment(CharSequence text, int start, int end, List<? extends Element> elements) {
		mText = text;
		mStart = start;
		mEnd = end;
		mElements = elements;
	}

	public List<? extends Element> getElements() {
		return mElements;
	}

	@Override
	public String toString() {
		return "Segment{" +
				"mText=" + mText.subSequence(mStart, mEnd) +
				", mElements=" + mElements +
				'}';
	}
}
