package me.chan.texas.text.tokenizer;

import java.util.ArrayList;
import java.util.List;

class SpanStream {
	private final List<Span> mSpans = new ArrayList<>();
	private int mSize;
	private int mIndex;

	void reset() {
		mSize = 0;
		mIndex = -1;
		if (mSpans.size() >= 32) {
			mSpans.clear();
		}
	}

	public boolean hasNext() {
		return mIndex + 1 < mSize;
	}

	void append(int start, int end) {
		if (mSize >= mSpans.size()) {
			mSpans.add(new Span(start, end));
			++mSize;
			return;
		}

		Span span = mSpans.get(mSize++);
		span.mStart = start;
		span.mEnd = end;
	}

	public Span next() {
		return mSpans.get(++mIndex);
	}

	public static class Span {
		private int mStart;
		private int mEnd;

		public Span(int start, int end) {
			mStart = start;
			mEnd = end;
		}

		public int getStart() {
			return mStart;
		}

		public int getEnd() {
			return mEnd;
		}
	}
}