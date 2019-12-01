package me.chan.te.renderer;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public abstract class SingleClickOnTouchListener implements View.OnTouchListener {
	private final int mTouchSlopSquare;
	private float mLastX;
	private float mLastY;
	private boolean mIgnore = false;

	public SingleClickOnTouchListener(Context context) {
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		final int touchSlop = configuration.getScaledTouchSlop();
		mTouchSlopSquare = touchSlop * touchSlop;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (mIgnore) {
			return false;
		}

		int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			mLastX = event.getX();
			mLastY = event.getY();
			mIgnore = false;
		} else if (action == MotionEvent.ACTION_MOVE) {
			float x = event.getX();
			float y = event.getY();
			float dx = x - mLastX;
			float dy = y - mLastY;
			if (dx * dx + dy * dy >= mTouchSlopSquare) {
				mIgnore = true;
				return false;
			}
			mLastX = x;
			mLastY = y;
		} else if (action == MotionEvent.ACTION_UP) {
			onClicked(event.getRawX(), event.getRawY());
		}
		return true;
	}

	protected abstract void onClicked(float x, float y);
}
