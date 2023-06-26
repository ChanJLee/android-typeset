package me.chan.androidtex;

import android.app.Application;

import me.chan.texas.Texas;

public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		// HiddenApiCompat.fix(this);
		Texas.MemoryOption memoryOption = new Texas.MemoryOption();
		memoryOption.setTextBufferSize(51200)
				.setLineBufferSize(4096)
				.setParagraphBufferSize(4096);
		Texas.init(this, memoryOption);
	}
}
