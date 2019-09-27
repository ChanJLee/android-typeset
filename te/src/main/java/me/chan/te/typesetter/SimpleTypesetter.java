package me.chan.te.typesetter;

import android.text.TextPaint;

import me.chan.te.config.LineAttributes;
import me.chan.te.config.Option;
import me.chan.te.data.Box;
import me.chan.te.data.ElementFactory;
import me.chan.te.data.Paragraph;
import me.chan.te.data.Segment;

public class SimpleTypesetter {
	private Option mOption;
	private TextPaint mPaint;
	private TextPaint mWorkPaint;
	private Box.Bound mBound;
	private ElementFactory mElementFactory;

	public SimpleTypesetter(Option option, TextPaint paint, TextPaint workPaint, Box.Bound bound, ElementFactory elementFactory) {
		mOption = option;
		mPaint = paint;
		mWorkPaint = workPaint;
		mBound = bound;
		mElementFactory = elementFactory;
	}

	public void typeset(Paragraph paragraph, Segment segment, LineAttributes lineAttributes) {

	}
}
