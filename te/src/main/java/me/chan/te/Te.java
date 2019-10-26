package me.chan.te;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;

import me.chan.te.log.Log;

public class Te {
	public static void init(Application application) {
		application.registerComponentCallbacks(new ComponentCallbacks() {
			@Override
			public void onConfigurationChanged(Configuration newConfig) {

			}

			@Override
			public void onLowMemory() {
				clean();
			}
		});
	}

	/**
	 * do clean
	 */
	public static void clean() {
		Log.i("Te", "clean text engine memory");
		// do clean
		// add engine clean code
	}
}
