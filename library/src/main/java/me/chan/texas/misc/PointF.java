package me.chan.texas.misc;

import androidx.annotation.NonNull;

/**
 * Point holds two integer coordinates
 */
public class PointF {
	public float x;
	public float y;

	public PointF() {
	}

	public PointF(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public PointF(@NonNull PointF src) {
		set(src);
	}

	/**
	 * Set the point's x and y coordinates
	 */
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Sets the point's from {@code src}'s coordinates
	 *
	 * @hide
	 */
	public void set(@NonNull PointF src) {
		this.x = src.x;
		this.y = src.y;
	}

	/**
	 * Negate the point's coordinates
	 */
	public final void negate() {
		x = -x;
		y = -y;
	}

	/**
	 * Offset the point's coordinates by dx, dy
	 */
	public final void offset(float dx, float dy) {
		x += dx;
		y += dy;
	}

	/**
	 * Returns true if the point's coordinates equal (x,y)
	 */
	public final boolean equals(float x, float y) {
		return this.x == x && this.y == y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PointF point = (PointF) o;

		if (x != point.x) return false;
		if (y != point.y) return false;

		return true;
	}

	@Override
	public String toString() {
		return "Point(" + x + ", " + y + ")";
	}

	public boolean contains(float x, float y, float size /* 宽度 */) {
		float offset = size / 2;
		float left = this.x - offset;
		float right = this.x + offset;
		float top = this.y - offset;
		float bottom = this.y + offset;
		return x >= left && x <= right && y >= top && y <= bottom;
	}
}
