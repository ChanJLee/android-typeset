package com.shanbay.lib.texas.renderer.selection.magnifier;

import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.widget.Magnifier;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.shanbay.lib.texas.R;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class MagnifierViewApi29 implements MagnifierView {

	private final Magnifier mMagnifier;

	public MagnifierViewApi29(@NonNull View view) {
		Resources resources = view.getResources();
		int size = (int) resources.getDimension(R.dimen.com_shanbay_lib_texas_default_magnifier_size);
		mMagnifier = new Magnifier.Builder(view)
				.setSize(size, size)
				.setDefaultSourceToMagnifierOffset(0, (int) (size * -0.66f))
				.setCornerRadius(size)
				.build();
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
