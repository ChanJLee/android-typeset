package me.chan.texas.text.util;

import androidx.annotation.Nullable;

public interface TexasIterator<T> {
	/**
	 * @return 返回下一个元素，如果没有则返回null
	 */
	@Nullable
	T next();

	/**
	 * @return 返回前一个元素，如果没有则返回null
	 */
	@Nullable
	T prev();

	/**
	 * @return 返回当前元素，如果没有则返回null
	 */
	@Nullable
	T current();

	/**
	 * @param state {@link #save()}
	 * @return 跳转到对应的state，并返回对应元素，如果跳转失败则不改变任何状态并返回null
	 */
	@Nullable
	T restore(int state);

	int save();
}
