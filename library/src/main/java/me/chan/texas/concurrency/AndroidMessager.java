package me.chan.texas.concurrency;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

/**
 * android 线程同步器
 */
@RestrictTo(LIBRARY)
public final class AndroidMessager extends Messager {
	private final Handler mHandler;

	public AndroidMessager() {
		mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
			@Override
			public boolean handleMessage(@NonNull Message msg) {
				if (mListener != null) {
					mListener.handleMessage(msg.what, msg.obj);
				}
				return true;
			}
		});
	}

	@Override
	public void send(int what, Object value) {
		Message message = Message.obtain();
		message.what = what;
		message.obj = value;
		mHandler.sendMessage(message);
	}

	@Override
	public void clear() {
		mHandler.removeCallbacksAndMessages(null);
	}
}
