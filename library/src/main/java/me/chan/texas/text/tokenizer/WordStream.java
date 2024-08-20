package me.chan.texas.text.tokenizer;

import com.ibm.icu.text.BreakIterator;

import java.text.CharacterIterator;

import me.chan.texas.utils.IntArray;

class WordStream {
	private final CharacterIterator0 mIterator0 = new CharacterIterator0();
	private final IntArray mBrk = new IntArray(128);
	private int mIndex = 0;

	public void setText(CharSequence text, int start, int end) {
		BreakIterator boundary = BreakIterator.getWordInstance();
		boundary.setText(mIterator0.reset(text, start, end));

		mBrk.clear();
		mIndex = 0;

		mBrk.add(boundary.first() + start);
		for (int brk = boundary.next();
			 brk != BreakIterator.DONE; brk = boundary.next()) {
			mBrk.add(brk + start);
		}
	}

	public boolean next(Listener listener) {
		if (mIndex + 1 >= mBrk.size()) {
			return false;
		}

		int start = mIndex;
		int end = ++mIndex;

		listener.onNext(mIterator0.seq, start, end);
		return true;
	}

	public interface Listener {
		void onNext(CharSequence text, int start, int end);
	}

	private static class CharacterIterator0 implements CharacterIterator {
		private int index;
		private CharSequence seq;
		private int start;
		private int size;

		public CharacterIterator reset(CharSequence text, int start, int end) {
			seq = text;
			this.start = start;
			size = end - start;
			index = 0;
			return this;
		}

		@Override
		public char first() {
			index = 0;
			return current();
		}

		@Override
		public char last() {
			index = size;
			return previous();
		}

		@Override
		public char current() {
			if (index == size) {
				return DONE;
			}
			return seq.charAt(index + start);
		}

		@Override
		public char next() {
			if (index < size) {
				++index;
			}
			return current();
		}

		@Override
		public char previous() {
			if (index == 0) {
				return DONE;
			}
			--index;
			return current();
		}

		@Override
		public char setIndex(int position) {
			if (position < 0 || position > size) {
				throw new IllegalArgumentException();
			}
			index = position;
			return current();
		}

		@Override
		public int getBeginIndex() {
			return 0;
		}

		@Override
		public int getEndIndex() {
			return size;
		}

		@Override
		public int getIndex() {
			return index;
		}

		@Override
		public Object clone() {
			CharacterIterator0 copy = new CharacterIterator0();
			copy.start = this.start;
			copy.seq = this.seq;
			copy.index = this.index;
			return copy;
		}
	}
}
