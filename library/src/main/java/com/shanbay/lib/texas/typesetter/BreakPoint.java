package com.shanbay.lib.texas.typesetter;

import com.shanbay.lib.texas.Texas;
import com.shanbay.lib.texas.annotations.Hidden;
import com.shanbay.lib.texas.misc.DefaultRecyclable;
import com.shanbay.lib.texas.misc.ObjectFactory;

@Hidden
class BreakPoint extends DefaultRecyclable {
	private static final ObjectFactory<BreakPoint> POOL = new ObjectFactory<>(512);
	static {
		Texas.registerLifecycleCallback(new Texas.LifecycleCallback() {
			@Override
			public void onClean() {
				POOL.clean();
			}
		});
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
