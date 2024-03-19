package me.chan.texas.renderer.selection.magnifier;

import android.os.Build;
import android.view.View;

public class MagnifierViewFactory {
	public static MagnifierView newInstance(View view) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			return new MagnifierViewApi29(view);
		}

		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
			return new MagnifierViewApi28(view);
		}

		return new MagnifierViewNoop(view);
	}
}
