package me.chan.te.data;

import android.annotation.SuppressLint;

import java.util.HashMap;
import java.util.Map;

/**
 * line options
 */
public class LineAttributes {
	private LineAttribute mDefaultAttribute;
	@SuppressLint("UseSparseArrays")
	private Map<Integer, LineAttribute> mMap = new HashMap<>();

	public LineAttributes(LineAttribute defaultAttribute) {
		mDefaultAttribute = defaultAttribute;
	}

	public LineAttributes add(int lineNumber, LineAttribute lineAttribute) {
		mMap.put(lineNumber, lineAttribute);
		return this;
	}

	public void remove(int lineNumber) {
		mMap.remove(lineNumber);
	}

	public LineAttribute get(int lineNumber) {
		if (mMap.containsKey(lineNumber)) {
			return mMap.get(lineNumber);
		}
		return mDefaultAttribute;
	}
}
