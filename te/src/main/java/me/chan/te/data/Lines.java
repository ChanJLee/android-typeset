package me.chan.te.data;

import java.util.HashMap;
import java.util.Map;

public class Lines {
	private int mNormalWidth;
	private Map<Integer, Integer> mMap = new HashMap<>();

	public Lines(int normalWidth) {
		mNormalWidth = normalWidth;
	}

	public Lines add(int lineNumber, int width) {
		mMap.put(lineNumber, width);
		return this;
	}

	public Lines remove(int lineNumber) {
		mMap.remove(lineNumber);
		return this;
	}

	public int get(int lineNumber) {
		if (mMap.containsKey(lineNumber)) {
			return mMap.get(lineNumber);
		}
		return mNormalWidth;
	}
}
