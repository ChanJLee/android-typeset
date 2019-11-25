package me.chan.te.text;

import java.util.HashSet;
import java.util.Set;

public class Selection {
	private Set<Box> mBoxes = new HashSet<>();

	public boolean contains(Box box) {
		return mBoxes.contains(box);
	}

	public void add(Box box) {
		mBoxes.add(box);
	}

	public void clear() {
		for (Box box : mBoxes) {
			box.setSelected(false);
		}
		mBoxes = null;
	}
}
