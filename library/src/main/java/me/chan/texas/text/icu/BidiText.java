package me.chan.texas.text.icu;

import me.chan.texas.misc.DefaultRecyclable;

public class BidiText extends DefaultRecyclable {

	private CharSequence mText;
	private int mStart;
	private int mEnd;

	private BidiText() {}


	public static BidiText obtain(CharSequence text, int start, int end) {
		throw new RuntimeException("Stub");
	}
}
