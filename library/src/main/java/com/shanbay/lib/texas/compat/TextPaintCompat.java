package com.shanbay.lib.texas.compat;

import android.graphics.Typeface;
import android.graphics.fonts.FontFamily;
import android.text.TextPaint;

import androidx.annotation.RestrictTo;

import com.shanbay.lib.log.Log;
import com.shanbay.lib.texas.renderer.TexasView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class TextPaintCompat extends TextPaint {
	private static boolean sIsMiUi = false;
	private static Field sField;

	static {
		try {
			ClassLoader classLoader = TexasView.class.getClassLoader();
			classLoader.loadClass("miui.util.TypefaceHelper");
			sIsMiUi = true;

			sField = Typeface.class.getDeclaredField("fontFamilies");
		} catch (Throwable throwable) {
			Log.w("CompatTextPaint", throwable);
		}
	}

	private TextPaintCompat(int flags) {
		super(flags);
	}

	private TextPaintCompat() {
	}

	@Override
	public Typeface setTypeface(Typeface typeface) {
		fixMiuiOpt(typeface);
		return super.setTypeface(typeface);
	}

	// save mem
	private static final List<FontFamily> EMPTY_LIST = new ArrayList<>(0);

	/* fix miui 的负优化 */
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static void fixMiuiOpt(Typeface typeface) {
		if (!sIsMiUi || sField == null) {
			return;
		}

		if (typeface == Typeface.SERIF || typeface == Typeface.SANS_SERIF ||
				typeface == Typeface.DEFAULT || typeface == Typeface.DEFAULT_BOLD ||
				typeface == Typeface.MONOSPACE) {
			return;
		}

		try {
			List<FontFamily> list = (List<FontFamily>) sField.get(typeface);
			if (list == null || list.isEmpty()) {
				return;
			}

			Log.d("TextPaintCompat", "fix miui opt");
			sField.set(typeface, EMPTY_LIST);
		} catch (Throwable e) {
			Log.w("TextPaintCompat", e);
		}
	}

	public static TextPaint create(int flag) {
		return sIsMiUi ? new TextPaintCompat(flag) : new TextPaint(flag);
	}

	public static TextPaint create() {
		return sIsMiUi ? new TextPaintCompat() : new TextPaint();
	}
}
