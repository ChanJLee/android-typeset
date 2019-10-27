package me.chan.te.typesetter;

import me.chan.te.annotations.Hidden;
import me.chan.te.data.Recyclable;
import me.chan.te.misc.ObjectFactory;

@Hidden
public class BreakPoint implements Recyclable {
	private static final ObjectFactory<BreakPoint> POOL = new ObjectFactory<>(5000);

	public int position;
	public float ratio;

	private BreakPoint(int position, float ratio) {
		reset(position, ratio);
	}

	private void reset(int position, float ratio) {
		this.position = position;
		this.ratio = ratio;
	}

	@Override
	public void recycle() {
		reset(-1, -1);
		POOL.release(this);
	}

	public static void clean() {
		POOL.clean();
	}

	public static BreakPoint obtain(int position, float ratio) {
		BreakPoint breakPoint = POOL.acquire();
		if (breakPoint == null) {
			return new BreakPoint(position, ratio);
		}
		breakPoint.reset(position, ratio);
		return breakPoint;
	}
}
