package me.chan.texas.typesetter.utils;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import java.util.ArrayList;
import java.util.List;

import me.chan.texas.Texas;
import me.chan.texas.text.layout.Element;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class ElementStream {
	private final List<Element> mElements;
	private int mIndex = 0;

	public ElementStream() {
		Texas.MemoryOption memoryOption = Texas.getMemoryOption();
		mElements = new ArrayList<>(memoryOption.getParagraphElementInitialCapacity());
	}

	public void push(Element element) {
		mElements.add(element);
	}

	public boolean isEmpty() {
		return mElements.isEmpty();
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
		return next(mElements.size());
	}

	@Nullable
	public Element next(int end) {
		if (mIndex + 1 > end) {
			return null;
		}

		return mElements.get(mIndex++);
	}

	@Nullable
	public Element tryGet(int offset) {
		return tryGet(mIndex, offset);
	}

	@Nullable
	public Element tryGet(int state, int offset) {
		int index = state + offset;
		if (index >= mElements.size() || index < 0) {
			return null;
		}

		return mElements.get(index);
	}


	public boolean eof() {
		return mIndex >= mElements.size();
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder(32);
		stringBuilder.append("ElementStream{0..");
		stringBuilder.append(mElements.size());
		stringBuilder.append("}, state: ");
		stringBuilder.append(mIndex);
		return stringBuilder.toString();
	}

	public static int index2State(int index) {
		return index;
	}

	public int size() {
		return mElements.size();
	}

	public Element get(int index) {
		return mElements.get(index);
	}
}