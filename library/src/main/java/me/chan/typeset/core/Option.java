package me.chan.typeset.core;

import android.graphics.Paint;

public class Option {
	public int tolerance;
	public int infinity = 1000;
	public int minHyperLength = 4;

	public float spaceWidth = 0;
	public float spaceStretch = 0;
	public float spaceShrink = 0;

	public float hyphenWidth = 0;
	public int hyphenPenalty = 100;

	public float demeritsLine = 10;
	public float demeritsFlagged = 100;
	public float demeritsFitness = 3000;

	public Option(Paint paint) {
		reset(paint);
	}

	public void reset(Paint paint) {
		hyphenWidth = paint.measureText("-");
		spaceWidth = paint.measureText(" ");
		spaceStretch = (spaceWidth * 3) / 6;
		spaceShrink = (spaceWidth * 3) / 9;
	}
}
