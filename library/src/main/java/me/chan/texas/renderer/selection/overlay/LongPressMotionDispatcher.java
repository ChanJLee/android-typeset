package me.chan.texas.renderer.selection.overlay;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import java.util.Arrays;

import me.chan.texas.BuildConfig;

public abstract class LongPressMotionDispatcher {
	private static final int LONG_PRESS_TIMEOUT = 500;
	private static final int DIRECTION_NONE = 0;
	public static final int DIRECTION_UP = 1;
	public static final int DIRECTION_DOWN = 2;

	@IntDef({DIRECTION_NONE, DIRECTION_UP, DIRECTION_DOWN})
	public @interface DirectionType {
	}

	private final boolean[] mPendingMsgBucket = {false, false, false};

	private final Handler mPendingMotionHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(@NonNull Message msg) {
			onMotionReceived(msg.what);
			mPendingMsgBucket[msg.what] = false;
		}
	};
	private int mLastDirection = DIRECTION_NONE;

	public void dispatch(@DirectionType int direction) {
		if (direction != mLastDirection && mLastDirection != DIRECTION_NONE) {
			cancel("direction changed");
		}

		if (mPendingMsgBucket[direction]) {
			return;
		}

		mPendingMsgBucket[direction] = true;
		mLastDirection = direction;
		Message message = mPendingMotionHandler.obtainMessage(direction);
		mPendingMotionHandler.sendMessageDelayed(message, LONG_PRESS_TIMEOUT);
	}

	public void cancel(String reason) {
		if (BuildConfig.DEBUG) {
			Log.d("MotionDispatcher", "cancel: " + reason);
		}

		mPendingMotionHandler.removeCallbacksAndMessages(null);
		Arrays.fill(mPendingMsgBucket, false);
		mLastDirection = DIRECTION_NONE;
	}

	protected abstract void onMotionReceived(int direction);
}
