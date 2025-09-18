package me.chan.texas.text.util;

import androidx.annotation.Nullable;

public interface TexasIterator<T> {
	@Nullable
	T next();

	@Nullable
	T prev();

	@Nullable
	T current();

	@Nullable
	T seek(int state);

	int save();

	void restore(int state);
}
