package me.chan.texas;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class MyLinearLayout extends LinearLayout {
	public MyLinearLayout(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		Log.d("chan_debug", "dispatchDraw" + Build.VERSION.SDK_INT);
		super.dispatchDraw(canvas);
	}

	public void draw(Canvas canvas) {
		super.draw(canvas);
		Log.d("chan_debug", "draw");
	}
}
