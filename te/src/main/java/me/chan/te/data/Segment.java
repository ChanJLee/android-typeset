package me.chan.te.data;

import java.util.ArrayList;
import java.util.List;

import me.chan.te.config.Option;
import me.chan.te.hypher.Hypher;
import me.chan.te.measurer.Measurer;
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
		private List<Element> mElements = new ArrayList<>();
		private List<String> mHyphenated = new ArrayList<>(10);
		private Measurer mMeasurer;

		public Builder(CharSequence text, int start, int end, Measurer measurer) {
			mText = text;
			mStart = start;
			mEnd = end;
			mMeasurer = measurer;
		}

		public Builder text(Hypher hypher, Option option, CharSequence text, int start, int end) {
			int len = end - start;
			hypher.hyphenate(String.valueOf(text), start, end - start, mHyphenated);
			int size = mHyphenated.size();
			if (size == 0 || len < MIN_HYPER_LEN) {
				mElements.add(TextBox.obtain(text, start, end,
						mMeasurer.getDesiredWidth(text, start, end),
						mMeasurer.getDesiredHeight(text, start, end),
						null
				));
			} else {
				for (int j = 0; j < size; ++j) {
					String item = mHyphenated.get(j);
					int itemLen = item.length();
					mElements.add(TextBox.obtain(item, 0, itemLen,
							mMeasurer.getDesiredWidth(item, 0, itemLen),
							mMeasurer.getDesiredHeight(item, 0, itemLen),
							null
					));
					if (j != size - 1 && !item.isEmpty() && item.charAt(itemLen - 1) != '-') {
						mElements.add(Penalty.obtain(option.getHyphenWidth(), option.getHyphenHeight(), Typesetter.HYPHEN_PENALTY, true));
					}
				}
			}
			mHyphenated.clear();
			mElements.add(Glue.obtain(option.getSpaceWidth(), option.getSpaceStretch(), option.getSpaceShrink()));
			return this;
		}

		public Segment build() {
			if (!mElements.isEmpty()) {
				mElements.remove(mElements.size() - 1);
			}

			mElements.add(Glue.obtain(0, Typesetter.INFINITY, 0));
			mElements.add(Penalty.obtain(0, 0, -Typesetter.INFINITY, true));

			return new Segment(mText, mStart, mEnd, mElements);
		}
	}
}
