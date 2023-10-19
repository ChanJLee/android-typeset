package me.chan.texas.renderer.selection.overlay;

import android.graphics.RectF;

class Region {
	private RectF mTop;
	private RectF mBottom;

	public void setup(RectF top, RectF bottom) {
		mTop = top;
		mBottom = bottom;
	}

	public float getTopX() {
		return mTop.centerX();
	}

	public float getTopY() {
		return mTop.centerY();
	}

	public float getBottomX() {
		return mBottom.centerX();
	}

	public float getBottomY() {
		return mBottom.centerY();
	}

	public boolean isTop(RectF point) {
		return point == mTop;
	}
}
