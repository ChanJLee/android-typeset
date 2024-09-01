package me.chan.texas.text.tokenizer;

import com.ibm.icu.text.BreakIterator;

import java.text.CharacterIterator;

import me.chan.texas.utils.LongArray;

class WordStream {
	private final CharacterIterator0 mIterator0 = new CharacterIterator0();
	private final LongArray mBrk = new LongArray(128);
	private int mIndex = 0;

	public void setText(CharSequence text, int start, int end) {
		BreakIterator boundary = BreakIterator.getWordInstance();
		boundary.setText(mIterator0.reset(text, start, end));

		mBrk.clear();
		mIndex = 0;

		addBrk(boundary.getRuleStatus(), boundary.first() + start);
		for (int brk = boundary.next();
			 brk != BreakIterator.DONE; brk = boundary.next()) {
			addBrk(boundary.getRuleStatus(), brk + start);
		}
	}

	private void addBrk(int reason, int index) {
		long v = reason;
		v <<= 32;
		v += index;
		mBrk.add(v);
	}

	public boolean next(Listener listener) {
		if (mIndex + 1 >= mBrk.size()) {
			return false;
		}

		long start = (mBrk.get(mIndex));
		int end = (int) mBrk.get(++mIndex);

		listener.onValue(mIterator0.seq, (int) start, end, (int) (start >> 32));
		return true;
	}

	public boolean prev(Listener listener) {
		if (mIndex - 1 < 0) {
			return false;
		}

		int end = (int) mBrk.get(mIndex);
		long start = mBrk.get(--mIndex);
		listener.onValue(mIterator0.seq, (int) start, end, (int) (start >> 32));
		return true;
	}

	public int save() {
		return mIndex;
	}

	public void restore(int status) {
		mIndex = status;
	}

	public interface Listener {
		void onValue(CharSequence text, int start, int end, int reason);
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
			copy.size = this.size;
			return copy;
		}

		@Override
		public String toString() {
			return seq.subSequence(start, start + size).toString();
		}
	}
}
