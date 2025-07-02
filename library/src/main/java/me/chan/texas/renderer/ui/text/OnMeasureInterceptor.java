package me.chan.texas.renderer.ui.text;

import androidx.annotation.RestrictTo;


@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface OnMeasureInterceptor {
	
	boolean onMeasure(MeasureSpecs specs);

	class MeasureSpecs {
		int widthSpec;
		int heightSpec;
	}
}
