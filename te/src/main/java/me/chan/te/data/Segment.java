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
	private List<? extends Element> mElements = new ArrayList<>(30);

	private Segment(CharSequence text, int start, int end) {
		reset(text, start, end);
	}

	public List<? extends Element> getElements() {
		return mElements;
	}

	private void reset(CharSequence text, int start, int end) {
		mText = text;
		mStart = start;
		mEnd = end;
		if (!mElements.isEmpty()) {
			mElements.clear();
		}
	}

	@Override
	public String toString() {
		return String.valueOf(mText.subSequence(mStart, mEnd));
	}

	@Override
	public void recycle() {
		for (int i = 0; i < mElements.size(); ++i) {
			mElements.get(i).recycle();
		}
		reset(null, 0, 0);
		POOL.release(this);
	}

	@Hidden
	public static Segment obtain(CharSequence text, int start, int end) {
		Segment segment = POOL.acquire();
		if (segment == null) {
			return new Segment(text, start, end);
		}
		segment.reset(text, start, end);
		return segment;
	}

	public static class Builder {
		private static final int MIN_HYPER_LEN = 4;
		private List<Integer> mHyphenated = new ArrayList<>(10);
		private Measurer mMeasurer;
		private Hypher mHypher;
		private Option mOption;
		private Segment mSegment;

		public Builder(Measurer measurer, Hypher hypher, Option option) {
			mMeasurer = measurer;
			mHypher = hypher;
			mOption = option;
		}

		public Builder newSegment(CharSequence text, int start, int end) {
			if (mSegment != null) {
				throw new IllegalStateException("call newSegment twice");
			}

			mSegment = Segment.obtain(text, start, end);
			return this;
		}

		public Builder text(CharSequence text, int start, int end) {
			int len = end - start;
			@SuppressWarnings("unchecked")
			List<Element> elements = (List<Element>) mSegment.getElements();
			mHypher.hyphenate(text, start, end, mHyphenated);
			int size = mHyphenated.size();
			if (size == 0 || len < MIN_HYPER_LEN) {
				elements.add(TextBox.obtain(text, start, end,
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

					elements.add(TextBox.obtain(text, start, point,
							mMeasurer.getDesiredWidth(text, start, point),
							mMeasurer.getDesiredHeight(text, start, point),
							null
					));
					if (j != size - 1 && text.charAt(point - 1) != '-') {
						elements.add(Penalty.obtain(mOption.getHyphenWidth(), mOption.getHyphenHeight(), Typesetter.HYPHEN_PENALTY, true));
					}
					start = point;
				}
			}
			mHyphenated.clear();
			elements.add(Glue.obtain(mOption.getSpaceWidth(), mOption.getSpaceStretch(), mOption.getSpaceShrink()));
			return this;
		}

		public Segment build() {
			@SuppressWarnings("unchecked")
			List<Element> elements = (List<Element>) mSegment.getElements();
			if (!elements.isEmpty() && elements.get(elements.size() - 1) instanceof Glue) {
				// TODO opt
				elements.remove(elements.size() - 1);
			}

			elements.add(Glue.obtain(0, Typesetter.INFINITY, 0));
			elements.add(Penalty.obtain(0, 0, -Typesetter.INFINITY, true));
			Segment segment = mSegment;
			mSegment = null;
			return segment;
		}

		private void reset(Measurer measurer, Hypher hypher, Option option, Segment segment) {
			mMeasurer = measurer;
			mHypher = hypher;
			mOption = option;
			mSegment = segment;
			mHyphenated.clear();
		}
	}
}
