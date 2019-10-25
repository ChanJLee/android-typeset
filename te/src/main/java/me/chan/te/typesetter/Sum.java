package me.chan.te.typesetter;

import me.chan.te.data.Recyclable;
import me.chan.te.misc.ObjectFactory;

public class Sum implements Recyclable {
	private static final ObjectFactory<Sum> POOL = new ObjectFactory<>(100);

	public float width = 0;
	public float stretch = 0;
	public float shrink = 0;

	private Sum() {
	}

	@Override
	public void recycle() {
		width = shrink = stretch = -1;
		POOL.release(this);
	}

	public static Sum obtain() {
		Sum sum = POOL.acquire();
		if (sum != null) {
			return sum;
		}
		return new Sum();
	}

	public static Sum obtain(Sum other) {
		Sum sum = obtain();
		sum.width = other.width;
		sum.shrink = other.shrink;
		sum.stretch = other.stretch;
		return sum;
	}
}