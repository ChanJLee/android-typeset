package me.chan.androidtex;

import android.app.Application;

import com.shanbay.lib.hiddenapi.HiddenApiCompat;
import com.shanbay.lib.log.Log;
import me.chan.texas.Texas;

public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		Log.init(this, "/sdcard/shanbay/texas/log");
		HiddenApiCompat.fix(this);
		Texas.MemoryOption memoryOption = new Texas.MemoryOption();
		memoryOption.setTextBufferSize(51200)
				.setLineBufferSize(4096)
				.setParagraphBufferSize(4096);
		Texas.init(this, memoryOption);
	}
}
