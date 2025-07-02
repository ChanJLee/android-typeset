package me.chan.texas.typesetter.utils;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Element;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class ElementStream {
	private final Paragraph mParagraph;
	private int mIndex;

	public ElementStream(Paragraph paragraph) {
		mParagraph = paragraph;
		mIndex = 0;
	}

	public int state() {
		return mIndex;
	}

	public void restore(int state) {
		mIndex = state;
	}

	public int pickState(int state, int offset) {
		return state + offset;
	}

	public boolean checkState(int state) {
		return mIndex == state;
	}

	@Nullable
	public Element next() {
		return next0(mParagraph.getElementCount());
	}

	@Nullable
	private Element next0(int end) {
		if (mIndex + 1 > end) {
			return null;
		}

		return mParagraph.getElement(mIndex++);
	}

	@Nullable
	public Element prev() {
		int index = mIndex - 1;
		if (index < 0 || index >= mParagraph.getElementCount()) {
			return null;
		}
		return mParagraph.getElement(--mIndex);
	}

	@Nullable
	public Element tryGet(int offset) {
		return tryGet(mIndex, offset);
	}

	@Nullable
	public Element tryGet(int state, int offset) {
		int index = state + offset;
		if (index >= mParagraph.getElementCount() || index < 0) {
			return null;
		}

		return mParagraph.getElement(index);
	}

	@Nullable
	public Element pick(int state) {
		if (state >= mParagraph.getElementCount() || state < 0) {
			return null;
		}
		return mParagraph.getElement(state);
	}

	public boolean eof() {
		return mIndex >= mParagraph.getElementCount();
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder(32);
		stringBuilder.append("ElementStream{0...");
		stringBuilder.append(mParagraph == null || mParagraph.isRecycled() ? 0 : mParagraph.getElementCount());
		stringBuilder.append("}, state: ");
		stringBuilder.append(mIndex);
		return stringBuilder.toString();
	}

	public static int index2State(int index) {
		return index;
	}

	public int distance(int startState, int endState) {
		return endState - startState;
	}
}
