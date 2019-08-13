package me.chan.typeset.core;

import android.graphics.Paint;

public class Option {
	private float mHyphenWidth = 0;
	private float mSpaceWidth = 0;
	private float mSpaceStretch = 0;
	private float mSpaceShrink = 0;
	private int mTolerance;

	public Option(Paint paint) {
		reset(paint);
	}

	public void reset(Paint paint) {
		mHyphenWidth = paint.measureText("-");
		mSpaceWidth = paint.measureText(" ");
		mSpaceStretch = (mSpaceWidth * 3) / 6;
		mSpaceShrink = (mSpaceWidth * 3) / 9;
	}

	public int getInfinity() {
		return 1000;
	}

	public int getHyphenPenalty() {
		return 100;
	}

	public float getHyphenWidth() {
		return mHyphenWidth;
	}

	public float getSpaceWidth() {
		return mSpaceWidth;
	}

	public float getSpaceStretch() {
		return mSpaceStretch;
	}

	public float getSpaceShrink() {
		return mSpaceShrink;
	}

	public int getMinHyperLength() {
		return 6;
	}

	public int getTolerance() {
		return mTolerance;
	}

	public void setTolerance(int tolerance) {
		mTolerance = tolerance;
	}

	public int getDemeritsLine() {
		return 10;
	}

	public int getDemeritsFlagged() {
		return 100;
	}

	public int getDemeritsFitness() {
		return 3000;
	}
}
