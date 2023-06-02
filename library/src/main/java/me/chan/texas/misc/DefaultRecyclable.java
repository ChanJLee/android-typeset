package me.chan.texas.misc;

import me.chan.texas.annotations.TexasUnitTest;

/**
 * 默认可回收实现类
 */
public class DefaultRecyclable implements Recyclable {

	@TexasUnitTest("true")
	private volatile boolean mRecycled = false;

	@Override
	public void recycle() {
		if (mRecycled) {
			throw new IllegalStateException("current object has been recycled, call recycle twice: " + this);
		}
		mRecycled = true;
	}

	@Override
	public boolean isRecycled() {
		return mRecycled;
	}

	@Override
	public void reuse() {
		mRecycled = false;
	}
}
