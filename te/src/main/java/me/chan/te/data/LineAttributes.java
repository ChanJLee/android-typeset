package me.chan.te.data;

import android.annotation.SuppressLint;

import java.util.HashMap;
import java.util.Map;

/**
 * line options
 */
public class LineAttributes {
	private int mNormalWidth;
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Integer> mMap = new HashMap<>();

	public LineAttributes(int normalWidth) {
		mNormalWidth = normalWidth;
	}

	public void setNormalWidth(int normalWidth) {
		mNormalWidth = normalWidth;
	}

	public LineAttributes addSpecialWidth(int lineNumber, int width) {
		mMap.put(lineNumber, width);
		return this;
	}

	public LineAttributes removeSpecialWidth(int lineNumber) {
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
