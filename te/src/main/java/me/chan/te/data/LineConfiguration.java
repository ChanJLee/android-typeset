package me.chan.te.data;

import java.util.HashMap;
import java.util.Map;

/**
 * line options
 */
public class LineConfiguration {
	private int mNormalWidth;
	private Map<Integer, Integer> mMap = new HashMap<>();

	public LineConfiguration(int normalWidth) {
		mNormalWidth = normalWidth;
	}

	public void setNormalWidth(int normalWidth) {
		mNormalWidth = normalWidth;
	}

	public LineConfiguration addSpecialWidth(int lineNumber, int width) {
		mMap.put(lineNumber, width);
		return this;
	}

	public LineConfiguration removeSpecialWidth(int lineNumber) {
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
