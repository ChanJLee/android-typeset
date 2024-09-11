package me.chan.texas.text.tokenizer;

import java.text.CharacterIterator;

class CharacterSequenceIterator implements CharacterIterator {
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
		CharacterSequenceIterator copy = new CharacterSequenceIterator();
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

	public CharSequence getSeq() {
		return seq;
	}
}