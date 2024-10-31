package me.chan.texas.renderer.selection.overlay;

import me.chan.texas.misc.PointF;

class Region {
	private PointF mTop;
	private PointF mBottom;

	public void setup(PointF top, PointF bottom) {
		mTop = top;
		mBottom = bottom;
	}

	public float getTopX() {
		return mTop.x;
	}

	public float getTopY() {
		return mTop.y;
	}

	public float getBottomX() {
		return mBottom.x;
	}

	public float getBottomY() {
		return mBottom.y;
	}

	public boolean isTop(PointF point) {
		return point == mTop;
	}

	@Override
	public String toString() {
		return "Region[" + mTop + mBottom + "]";
	}
}
