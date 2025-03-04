package me.chan.texas;

import android.app.Application;
import android.graphics.Typeface;

public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
//		HiddenApiCompat.fix(this);
		Texas.MemoryOption memoryOption = new Texas.MemoryOption();
		memoryOption.setTextBufferSize(51200)
				.setLineBufferSize(4096)
				.setParagraphBufferSize(4096);
		Texas.init(this, memoryOption);
		Texas.setTDMSEnable(true);
		Texas.setDefaultTypeface(Typeface.createFromAsset(getAssets(), "opposans_r.ttf"));
	}
}
