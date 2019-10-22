package me.chan.te.data;

import java.util.ArrayList;
import java.util.List;

import me.chan.te.config.Option;
import me.chan.te.hypher.Hypher;
import me.chan.te.typesetter.Typesetter;

public final class Segment {
	private CharSequence mText;
	private int mStart;
	private int mEnd;
	private List<? extends Element> mElements;

	private Segment(CharSequence text, int start, int end, List<? extends Element> elements) {
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
		return String.valueOf(mText.subSequence(mStart, mEnd));
	}

	public static class Builder {
		private static final int MIN_HYPER_LEN = 4;

		private CharSequence mText;
		private int mStart;
		private int mEnd;
		private ElementFactory mElementFactory;
		private List<Element> mElements = new ArrayList<>();
		private List<String> mHyphenated = new ArrayList<>(10);

		public Builder(CharSequence text, int start, int end, ElementFactory factory) {
			mText = text;
			mStart = start;
			mEnd = end;
			mElementFactory = factory;
		}

		public Builder text(Hypher hypher, Option option, CharSequence text, int start, int end) {
			int len = end - start;
			hypher.hyphenate(String.valueOf(text), start, end - start, mHyphenated);
			int size = mHyphenated.size();
			if (size == 0 || len < MIN_HYPER_LEN) {
				mElements.add(mElementFactory.obtainTextBox(text, start, end));
			} else {
				for (int j = 0; j < size; ++j) {
					String item = mHyphenated.get(j);
					mElements.add(mElementFactory.obtainTextBox(item));
					if (j != size - 1 && !item.isEmpty() && item.charAt(item.length() - 1) != '-') {
						mElements.add(mElementFactory.obtainPenalty(option.getHyphenWidth(), option.getHyphenHeight(), Typesetter.HYPHEN_PENALTY, true));
					}
				}
			}
			mHyphenated.clear();
			mElements.add(mElementFactory.obtainGlue(option.getSpaceWidth(), option.getSpaceStretch(), option.getSpaceShrink()));
			return this;
		}

		public Segment build() {
			if (!mElements.isEmpty()) {
				mElements.remove(mElements.size() - 1);
			}

			mElements.add(new Glue(0, Typesetter.INFINITY, 0));
			mElements.add(new Penalty(0, 0, -Typesetter.INFINITY, true));

			return new Segment(mText, mStart, mEnd, mElements);
		}
	}
}
