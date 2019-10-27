package me.chan.androidtex;

import android.app.Application;

import me.chan.te.Te;

public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		Te.init(this);
	}
}
