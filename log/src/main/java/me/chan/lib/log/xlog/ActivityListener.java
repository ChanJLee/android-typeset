package me.chan.lib.log.xlog;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.tencent.mars.xlog.Log;

/**
 * Created by chan on 2017/10/19.
 */

public class ActivityListener implements Application.ActivityLifecycleCallbacks {
	/**
	 * 主线程应该没有竞争条件 所以不需要加锁
	 */
	private int mCount = 0;

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
		++mCount;
	}

	@Override
	public void onActivityStarted(Activity activity) {

	}

	@Override
	public void onActivityResumed(Activity activity) {

	}

	@Override
	public void onActivityPaused(Activity activity) {

	}

	@Override
	public void onActivityStopped(Activity activity) {
		Log.appenderFlush(false);
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		--mCount;
		if (mCount <= 0) {
			mCount = 0;
			Log.appenderFlush(false);
		}
	}
}
