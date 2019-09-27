package me.chan.te.config;

import android.annotation.SuppressLint;

import java.util.HashMap;
import java.util.Map;

/**
 * line options
 */
public class SegmentAttributes {
	private SegmentAttribute mDefaultAttribute;
	@SuppressLint("UseSparseArrays")
	private Map<Integer, SegmentAttribute> mMap = new HashMap<>();

	public SegmentAttributes(SegmentAttribute defaultAttribute) {
		mDefaultAttribute = defaultAttribute;
	}

	public SegmentAttributes add(int lineNumber, SegmentAttribute segmentAttribute) {
		mMap.put(lineNumber, segmentAttribute);
		return this;
	}

	public void remove(int lineNumber) {
		mMap.remove(lineNumber);
	}

	public SegmentAttribute get(int lineNumber) {
		if (mMap.containsKey(lineNumber)) {
			return mMap.get(lineNumber);
		}
		return mDefaultAttribute;
	}
}
