package me.chan.texas.utils;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class CharStream {
	private int mIndex;
	private CharSequence mText;
	private int mStart;
	private int mEnd;

	public CharStream() {
		this(null, 0, 0);
	}

	public CharStream(CharSequence text, int start, int end) {
		reset(text, start, end);
	}

	public int save() {
		return mIndex;
	}

	public void restore(int id) {
		mIndex = id;
	}

	public boolean eof() {
		return mIndex >= mEnd;
	}

	public int eat() {
		return mText.charAt(mIndex++);
	}

	public void adjust(int offset) {
		mIndex += offset;
	}

	public boolean tryCheck(int offset, int codePoint) {
		int index = offset + mIndex;
		if (index < mStart || index >= mEnd) {
			return false;
		}

		return mText.charAt(index) == codePoint;
	}

	public boolean eatIf(int codePoint) {
		if (peek() == codePoint) {
			eat();
			return true;
		}

		return false;
	}

	public int peek(int index) {
		return mText.charAt(index);
	}

	public int peek() {
		return mText.charAt(mIndex);
	}

	public void reset(CharSequence text, int start, int end) {
		mIndex = start;
		mText = text;
		mStart = start;
		mEnd = end;
	}

	public CharSequence getText() {
		return mText;
	}

	@NonNull
	@Override
	public String toString() {
		if (mText == null || (mIndex >= mEnd)) {
			return "";
		}
		return String.valueOf(mText.subSequence(mIndex, mEnd));
	}

	public void clear() {
		reset(null, 0, 0);
	}

	public void back() {
		adjust(-1);
	}

	public void consumeCommand(String cmd) {
		// TODO
		adjust(cmd.length());
	}

	public String peekCommand() {
		throw new RuntimeException("Stub!");
	}
}
