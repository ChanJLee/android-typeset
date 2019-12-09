package com.shanbay.lib.texas.misc;

import com.shanbay.lib.texas.annotations.Hidden;

/**
 * 默认可回收实现类
 */
@Hidden
public class DefaultRecyclable implements Recyclable {
	private boolean mRecycled = false;

	@Override
	public void recycle() {
		if (mRecycled) {
			throw new IllegalStateException("current object has been recycled, call recycle twice");
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
