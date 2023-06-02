package com.shanbay.lib.texas.utils;

import androidx.annotation.RestrictTo;

import java.util.Arrays;
import java.util.EmptyStackException;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class IntStack {

	static final int DEFAULT_SIZE = 16;

	private int[] mStack;
	private int mTop = 0;

	public IntStack() {
		mStack = new int[DEFAULT_SIZE];
	}

	public IntStack(IntStack other) {
		mTop = other.mTop;
		mStack = Arrays.copyOf(other.mStack, other.mStack.length);
	}

	public void push(int state) {
		if (mTop + 1 > mStack.length) {
			mStack = Arrays.copyOf(mStack, mStack.length * 2);
		}
		mStack[mTop++] = state;
	}

	public int pop() {
		if (empty()) {
			throw new EmptyStackException();
		}

		return mStack[--mTop];
	}

	public boolean empty() {
		return mTop == 0;
	}

	public void clear() {
		mTop = 0;
	}

	public int top() {
		if (empty()) {
			throw new EmptyStackException();
		}

		return mStack[mTop - 1];
	}

	public int bottom() {
		if (empty()) {
			throw new EmptyStackException();
		}

		return mStack[0];
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(32);
		builder.append("{");
		for (int i = 0; i < mTop; ++i) {
			builder.append(mStack[i]);
			builder.append(',');
		}
		builder.append("}");
		return builder.toString();
	}

	public int size() {
		return mTop;
	}
}
