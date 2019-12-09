package me.chan.androidtex;

import android.app.Application;

import com.shanbay.lib.texas.Texas;

public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		Texas.init(this);
	}
}
