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

	/**
	 * @return 保存当前状态
	 */
	public int save() {
		return mIndex;
	}

	/**
	 * @param id 恢复到对应的状态 {@link #save()}
	 */
	public void restore(int id) {
		mIndex = id;
	}

	/**
	 * @return 是否结尾
	 */
	public boolean eof() {
		return mIndex >= mEnd;
	}

	/**
	 * @return 返回当前单词并移动指针
	 */
	public int eat() {
		return mText.charAt(mIndex++);
	}

	/**
	 * @param offset 相对于当前位置偏移多少
	 */
	public void adjust(int offset) {
		mIndex += offset;
	}

	/**
	 * @param offset    相对于当前位置偏移多少
	 * @param codePoint 期望的字符
	 * @return 对应便宜下的字符是否是期望的
	 */
	public boolean tryCheck(int offset, int codePoint) {
		int index = offset + mIndex;
		if (index < mStart || index >= mEnd) {
			return false;
		}

		return mText.charAt(index) == codePoint;
	}

	/**
	 * @param index 下标
	 * @return 获取对应下标字符，但并不移动指针
	 */
	public int peek(int index) {
		return mText.charAt(index);
	}

	/**
	 * @return 获取当前字符，但并不移动指针
	 */
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

	/**
	 * 回退一步
	 */
	public void back() {
		adjust(-1);
	}
}
