package me.chan.te.data;

import java.util.HashMap;
import java.util.Map;

/**
 * line options
 */
public class LineOptions {
	private int mNormalWidth;
	private Map<Integer, Integer> mMap = new HashMap<>();

	public LineOptions(int normalWidth) {
		mNormalWidth = normalWidth;
	}

	public LineOptions addSpecialWidth(int lineNumber, int width) {
		mMap.put(lineNumber, width);
		return this;
	}

	public LineOptions removeSpecialWidth(int lineNumber) {
		mMap.remove(lineNumber);
		return this;
	}

	public int getLineWidth(int lineNumber) {
		if (mMap.containsKey(lineNumber)) {
			return mMap.get(lineNumber);
		}
		return mNormalWidth;
	}
}
