package me.chan.te.data;

import java.util.ArrayList;
import java.util.List;

import me.chan.te.annotations.Hidden;
import me.chan.te.config.Option;
import me.chan.te.hypher.Hypher;
import me.chan.te.measurer.Measurer;
import me.chan.te.misc.ObjectFactory;
import me.chan.te.typesetter.Typesetter;

/**
 * 一段文本片段
 */
public final class Segment implements Recyclable {
	private static final ObjectFactory<Segment> POOL = new ObjectFactory<>(3000);

	private CharSequence mText;
	private int mStart;
	private int mEnd;
	private List<? extends Element> mElements;

	private Segment(CharSequence text, int start, int end, List<? extends Element> elements) {
		reset(text, start, end, elements);
	}

	public List<? extends Element> getElements() {
		return mElements;
	}

	private void reset(CharSequence text, int start, int end, List<? extends Element> elements) {
		mText = text;
		mStart = start;
		mEnd = end;
		mElements = elements;
	}

	@Override
	public String toString() {
		return String.valueOf(mText.subSequence(mStart, mEnd));
	}

	@Override
	public void recycle() {
		for (int i = 0; mElements != null && i < mElements.size(); ++i) {
			mElements.get(i).recycle();
		}
		reset(null, 0, 0, null);
		POOL.release(this);
	}

	@Hidden
	public static Segment obtain(CharSequence text, int start, int end, List<? extends Element> elements) {
		Segment segment = POOL.acquire();
		if (segment == null) {
			return new Segment(text, start, end, elements);
		}
		segment.reset(text, start, end, elements);
		return segment;
	}

	public static class Builder implements Recyclable {
		private static final ObjectFactory<Builder> POOL = new ObjectFactory<>(1);

		private static final int MIN_HYPER_LEN = 4;

		private CharSequence mText;
		private int mStart;
		private int mEnd;
		private List<Element> mElements;
		private List<Integer> mHyphenated = new ArrayList<>(10);
		private Measurer mMeasurer;
		private Hypher mHypher;
		private Option mOption;

		private Builder(CharSequence text, int start, int end, Measurer measurer, Hypher hypher, Option option, List<Element> elements) {
			reset(text, start, end, measurer, hypher, option, elements);
		}

		public Builder text(CharSequence text, int start, int end) {
			int len = end - start;
			mHypher.hyphenate(text, start, end, mHyphenated);
			int size = mHyphenated.size();
			if (size == 0 || len < MIN_HYPER_LEN) {
				mElements.add(TextBox.obtain(text, start, end,
						mMeasurer.getDesiredWidth(text, start, end),
						mMeasurer.getDesiredHeight(text, start, end),
						null
				));
			} else {
				for (int j = 0; j < size; ++j) {
					int point = mHyphenated.get(j);
					if (point == start) {
						continue;
					}

					mElements.add(TextBox.obtain(text, start, point,
							mMeasurer.getDesiredWidth(text, start, point),
							mMeasurer.getDesiredHeight(text, start, point),
							null
					));
					if (j != size - 1 && text.charAt(point - 1) != '-') {
						mElements.add(Penalty.obtain(mOption.getHyphenWidth(), mOption.getHyphenHeight(), Typesetter.HYPHEN_PENALTY, true));
					}
					start = point;
				}
			}
			mHyphenated.clear();
			mElements.add(Glue.obtain(mOption.getSpaceWidth(), mOption.getSpaceStretch(), mOption.getSpaceShrink()));
			return this;
		}

		public Segment build() {
			if (!mElements.isEmpty()) {
				mElements.remove(mElements.size() - 1);
			}

			mElements.add(Glue.obtain(0, Typesetter.INFINITY, 0));
			mElements.add(Penalty.obtain(0, 0, -Typesetter.INFINITY, true));

			return Segment.obtain(mText, mStart, mEnd, mElements);
		}

		private void reset(CharSequence text, int start, int end, Measurer measurer, Hypher hypher, Option option, List<Element> elements) {
			mText = text;
			mStart = start;
			mEnd = end;
			mMeasurer = measurer;
			mHypher = hypher;
			mOption = option;
			mElements = elements;
			mHyphenated.clear();
		}

		@Override
		public void recycle() {
			reset(null, 0, 0, null, null, null, null);
			POOL.release(this);
		}

		public static Builder obtain(CharSequence text, int start, int end, Measurer measurer, Hypher hypher, Option option) {
			Builder builder = POOL.acquire();
			List<Element> elements = new ArrayList<>(30);
			if (builder == null) {
				return new Builder(text, start, end, measurer, hypher, option, elements);
			}
			builder.reset(text, start, end, measurer, hypher, option, elements);
			return builder;
		}
	}
}
