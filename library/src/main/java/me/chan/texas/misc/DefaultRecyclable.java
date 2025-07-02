package me.chan.texas.misc;

import me.chan.texas.annotations.TexasUnitTest;


public abstract class DefaultRecyclable implements Recyclable {

	@TexasUnitTest("true")
	private volatile boolean mRecycled = false;

	@Override
	public final void recycle() {
		if (mRecycled) {
			return;
		}

		mRecycled = true;
		onRecycle();
	}

	protected abstract void onRecycle();

	@Override
	public final boolean isRecycled() {
		return mRecycled;
	}

	@Override
	public final void reuse() {
		mRecycled = false;
		onReuse();
	}

	protected void onReuse() {

	}
}
