package me.chan.texas.renderer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

public abstract class SingleClickOnTouchListener implements View.OnTouchListener {
	private static final int[] STATE_PRESSED = {
			android.R.attr.state_pressed,
	};
	private static final int[] STATE_NORMAL = {
			-android.R.attr.state_pressed
	};

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
		StateListDrawable stateListDrawable = null;
		Drawable drawable = v.getBackground();
		if (drawable instanceof StateListDrawable) {
			stateListDrawable = (StateListDrawable) drawable;
		}

		int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			mLastX = event.getX();
			mLastY = event.getY();
			mIgnore = false;
			if (stateListDrawable != null) {
				stateListDrawable.setState(STATE_PRESSED);
				v.invalidate();
			}
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
			if (stateListDrawable != null) {
				stateListDrawable.setState(STATE_NORMAL);
				v.invalidate();
			}
			if (!mIgnore) {
				onClicked(event.getRawX(), event.getRawY());
			}
			mIgnore = false;
		} else if (action == MotionEvent.ACTION_CANCEL) {
			mIgnore = false;
			if (stateListDrawable != null) {
				stateListDrawable.setState(STATE_NORMAL);
				v.invalidate();
			}
		}
		return true;
	}

	protected abstract void onClicked(float x, float y);
}
