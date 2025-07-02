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

		// 按下操作，记录按下的坐标
		// 并改变背景
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

		// 移动，发现如果超过一定距离，就不响应
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

		// 变更背景为正常
		Drawable drawable = v.getBackground();
		if (drawable instanceof StateListDrawable) {
			StateListDrawable stateListDrawable = (StateListDrawable) drawable;
			stateListDrawable.setState(STATE_NORMAL);
			v.invalidate();
		}

		// 抬起手的时候看有没有滑动过度，滑动过度不判定为点击
		if (action == MotionEvent.ACTION_UP && !mIgnore) {
			onClicked(event);
		}

		// 返回值看有没有忽略滑动
		return !mIgnore;
	}

	protected abstract void onClicked(MotionEvent event);
}
