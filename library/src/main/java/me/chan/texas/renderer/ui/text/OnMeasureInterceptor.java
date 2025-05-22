package me.chan.texas.renderer.ui.text;

import androidx.annotation.RestrictTo;

/**
 * 测量拦截器
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public interface OnMeasureInterceptor {
	/**
	 * @param specs 测量的规格
	 * @return true 表示拦截测量，false 表示不拦截测量
	 */
	boolean onMeasure(MeasureSpecs specs);

	class MeasureSpecs {
		int widthSpec;
		int heightSpec;
	}
}