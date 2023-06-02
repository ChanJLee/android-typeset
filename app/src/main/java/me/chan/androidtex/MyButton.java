package me.chan.androidtex;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

public class MyButton extends Button {
	public MyButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		Log.d("chan_debug", "button dispatchDraw" + Build.VERSION.SDK_INT);
		super.dispatchDraw(canvas);
	}

	public void draw(Canvas canvas) {
		super.draw(canvas);
		Log.d("chan_debug", "button draw");
	}
}
