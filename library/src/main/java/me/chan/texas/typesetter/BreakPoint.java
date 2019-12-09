package me.chan.texas.typesetter;

import androidx.annotation.Keep;

import me.chan.texas.Texas;
import me.chan.texas.annotations.Hidden;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectFactory;

@Hidden
class BreakPoint extends DefaultRecyclable {
	private static final ObjectFactory<BreakPoint> POOL = new ObjectFactory<>(512);
	static {
		Texas.register(BreakPoint.class);
	}

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
		if (isRecycled()) {
			return;
		}
		super.recycle();
		reset(-1, -1);
		POOL.release(this);
	}

	@Keep
	public static void clean() {
		POOL.clean();
	}

	public static BreakPoint obtain(int position, float ratio) {
		BreakPoint breakPoint = POOL.acquire();
		if (breakPoint == null) {
			return new BreakPoint(position, ratio);
		}
		breakPoint.reset(position, ratio);
		breakPoint.reuse();
		return breakPoint;
	}
}
