package me.chan.texas.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.GetChars;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import me.chan.texas.compat.TextPaintCompat;
import me.chan.texas.misc.Rect;
import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Layout;

public class TexasUtils {
	private static int sScreenWidth = 1080;
	private static int sScreenHeight = 2000;
	private static boolean sIsDebug = true;

	private static float sDensity = 1.0f;

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static void init(Context context) {
		initWindowInfo(context);
		initDebugInfo(context);

		Resources resources = context.getResources();
		DisplayMetrics displayMetrics = resources.getDisplayMetrics();
		sDensity = displayMetrics.density;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static float getDensity() {
		return sDensity;
	}

	private static void initDebugInfo(Context context) {
		try {
			ApplicationInfo applicationInfo = context.getApplicationInfo();
			sIsDebug = (applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
		} catch (Throwable throwable) {
			Log.w("TexasUtils", throwable);
		}
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static boolean isDebug() {
		return sIsDebug;
	}

	private static void initWindowInfo(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		if (windowManager == null) {
			return;
		}

		Point point = new Point();
		windowManager.getDefaultDisplay().getSize(point);
		sScreenWidth = point.x;
		sScreenHeight = point.y;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static int getScreenHeight() {
		return sScreenHeight;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static int getScreenWidth() {
		return sScreenWidth;
	}

	
	public static boolean equals(Object lhs, Object rhs) {
		return (lhs == rhs) || (lhs != null && lhs.equals(rhs));
	}

	public static Typeface createTypefaceFromAsset(Context context, String path) {
		Typeface typeface = Typeface.createFromAsset(context.getAssets(), path);
		TextPaintCompat.fixMiuiOpt(typeface);
		return typeface;
	}

	
	public static int parseInt(CharSequence s, int start, int end) {
		return parseInt(s, start, end, 10);
	}

	
	public static int parseInt(CharSequence s, int start, int end, int radix) {
		

		if (s == null) {

			throw new NumberFormatException("s == null");
		}

		if (radix < Character.MIN_RADIX) {
			throw new NumberFormatException("radix " + radix +
					" less than Character.MIN_RADIX");
		}

		if (radix > Character.MAX_RADIX) {
			throw new NumberFormatException("radix " + radix +
					" greater than Character.MAX_RADIX");
		}

		int result = 0;
		boolean negative = false;
		int i = start, len = end - start;
		int limit = -Integer.MAX_VALUE;
		int multmin;
		int digit;

		if (len > 0) {
			char firstChar = s.charAt(start);
			if (firstChar < '0') { 
				if (firstChar == '-') {
					negative = true;
					limit = Integer.MIN_VALUE;
				} else if (firstChar != '+')
					throw forInputString(s);

				if (len == 1) 
					throw forInputString(s);
				i++;
			}
			multmin = limit / radix;
			while (i < end) {

				digit = Character.digit(s.charAt(i++), radix);
				if (digit < 0) {
					throw forInputString(s);
				}
				if (result < multmin) {
					throw forInputString(s);
				}
				result *= radix;
				if (result < limit + digit) {
					throw forInputString(s);
				}
				result -= digit;
			}
		} else {
			throw forInputString(s);
		}
		return negative ? result : -result;
	}

	private static NumberFormatException forInputString(CharSequence s) {
		return new NumberFormatException("For input string: \"" + s + "\"");
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static void setupTextPaint(TextPaint paint) {
		paint.density = sDensity;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static float getLineSpace(Paragraph paragraph) {
		Layout layout = paragraph.getLayout();
		Layout.Advise advise = layout.getAdvise();
		float lineSpace = advise.getLineSpacingExtra();
		if (lineSpace < 0) {
			lineSpace = layout.getLineSpace();
		}
		return lineSpace;
	}

	@IntDef({CmpType.CMP_DRAW, CmpType.CMP_TYPESET, CmpType.CMP_LOAD})
	public @interface CmpType {
		
		int CMP_TYPESET = 1;
		
		int CMP_LOAD = 2;
		
		int CMP_DRAW = 3;
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static String cmpType2String(@CmpType int type) {
		switch (type) {
			case CmpType.CMP_DRAW:
				return "CMP_DRAW";
			case CmpType.CMP_TYPESET:
				return "CMP_TYPESET";
			case CmpType.CMP_LOAD:
				return "CMP_LOAD";
			default:
				return "UNKNOWN";
		}
	}

	
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	@CmpType
	public static int cmp(RenderOption prev, RenderOption current) {
		if (prev.isCompatMode() != current.isCompatMode()) {
			throw new RuntimeException("compat mode can not be changed at runtime");
		}

		if (prev.getBreakStrategy() != current.getBreakStrategy() ||
				prev.getHyphenStrategy() != current.getHyphenStrategy()) {
			return CmpType.CMP_LOAD;
		}

		if (prev.getTextSize() != current.getTextSize() ||
				!TexasUtils.equals(prev.getTypeface(), current.getTypeface()) ||
				prev.isFullWithSymbolOptimizationEnable() != current.isFullWithSymbolOptimizationEnable()) {
			return CmpType.CMP_TYPESET;
		}

		return CmpType.CMP_DRAW;
	}

	public static void getChars(CharSequence s, int start, int end,
								char[] dest, int destoff) {
		Class<? extends CharSequence> c = s.getClass();

		if (c == String.class)
			((String) s).getChars(start, end, dest, destoff);
		else if (c == StringBuffer.class)
			((StringBuffer) s).getChars(start, end, dest, destoff);
		else if (c == StringBuilder.class)
			((StringBuilder) s).getChars(start, end, dest, destoff);
		else if (s instanceof GetChars)
			((GetChars) s).getChars(start, end, dest, destoff);
		else {
			for (int i = start; i < end; i++)
				dest[destoff++] = s.charAt(i);
		}
	}

	public static void copyRect(Rect dest, Rect src) {
		dest.top = src.top;
		dest.bottom = src.bottom;
		dest.left = src.left;
		dest.right = src.right;
	}

	public static void copyRect(android.graphics.Rect dest, Rect src) {
		dest.top = src.top;
		dest.bottom = src.bottom;
		dest.left = src.left;
		dest.right = src.right;
	}

	public static void copyRect(RectF dest, RectF src) {
		dest.top = src.top;
		dest.bottom = src.bottom;
		dest.left = src.left;
		dest.right = src.right;
	}

	public static void setRect(RectF dest, float left, float top, float right, float bottom) {
		dest.left = left;
		dest.top = top;
		dest.right = right;
		dest.bottom = bottom;
	}

	public static void setRect(Rect dest, int left, int top, int right, int bottom) {
		dest.left = left;
		dest.top = top;
		dest.right = right;
		dest.bottom = bottom;
	}

	
	public static boolean intersects(@NonNull RectF a, @NonNull RectF b) {
		return a.left < b.right && b.left < a.right
				&& a.top < b.bottom && b.top < a.bottom;
	}
}
