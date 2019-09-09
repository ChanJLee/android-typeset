package me.chan.te.data;

import android.text.Layout;
import android.text.TextPaint;

public class Option {
	public float infinity = 1000;
	public int hyphenPenalty = 100;
	public float demeritsLine = 10;
	public float demeritsFlagged = 100;
	public float demeritsFitness = 3000;
	public int maxRelayoutTimes = 3;
	public int minHyperLen = 4;
	public float hyphenWidth;
	public float spaceWidth;
	public float spaceStretch;
	public float spaceShrink;

	public Option(TextPaint paint) {
		hyphenWidth = paint.measureText("-");
		spaceWidth = Layout.getDesiredWidth(" ", paint);
		spaceStretch = (spaceWidth * 3) / 6;
		spaceShrink =  (spaceWidth * 3) / 9;
	}
}
