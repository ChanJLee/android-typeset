package com.shanbay.lib.texas.renderer.selection.magnifier;

import android.os.Build;
import android.view.View;
import android.widget.Magnifier;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.P)
public class MagnifierViewApi28 implements MagnifierView {

	private final Magnifier mMagnifier;

	public MagnifierViewApi28(@NonNull View view) {
		mMagnifier = new Magnifier(view);
	}

	@Override
	public void show(float x, float y) {
		mMagnifier.show(x, y);
	}

	@Override
	public void dismiss() {
		mMagnifier.dismiss();
	}
}
