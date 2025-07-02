package me.chan.texas.renderer.ui.rv;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.RestrictTo;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

@RestrictTo(LIBRARY)
public abstract class SingleClickOnTouchListener implements View.OnTouchListener {
	private static final int[] STATE_PRESSED = {
			android.R.attr.state_pressed,
	};
	private static final int[] STATE_NORMAL = {
			-android.R.attr.state_pressed
	};

	private final int mTouchSlopSquare;
	private float mDownX;
	private float mDownY;
	private boolean mIgnore = false;

	public SingleClickOnTouchListener(Context context) {
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		final int touchSlop = configuration.getScaledTouchSlop();
		mTouchSlopSquare = touchSlop * touchSlop;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();



		if (action == MotionEvent.ACTION_DOWN) {
			mDownX = event.getX();
			mDownY = event.getY();
			mIgnore = false;
			Drawable drawable = v.getBackground();
			if (drawable instanceof StateListDrawable) {
				StateListDrawable stateListDrawable = (StateListDrawable) drawable;
				stateListDrawable.setState(STATE_PRESSED);
				v.invalidate();
			}
			return true;
		}


		if (action == MotionEvent.ACTION_MOVE) {
			if (mIgnore) {
				return false;
			}

			float x = event.getX();
			float y = event.getY();
			float dx = x - mDownX;
			float dy = y - mDownY;
			if (dx * dx + dy * dy >= mTouchSlopSquare) {
				mIgnore = true;
				return false;
			}

			return true;
		}


		Drawable drawable = v.getBackground();
		if (drawable instanceof StateListDrawable) {
			StateListDrawable stateListDrawable = (StateListDrawable) drawable;
			stateListDrawable.setState(STATE_NORMAL);
			v.invalidate();
		}


		if (action == MotionEvent.ACTION_UP && !mIgnore) {
			onClicked(event);
		}


		return !mIgnore;
	}

	protected abstract void onClicked(MotionEvent event);
}
