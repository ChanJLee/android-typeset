package me.chan.texas.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.annotation.RestrictTo;

import com.shanbay.lib.log.Log;
import me.chan.texas.compat.TextPaintCompat;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Layout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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

	private static void initDebugInfo(Context context) {
		try {
			ApplicationInfo applicationInfo = context.getApplicationInfo();
			sIsDebug = (applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
		} catch (Throwable throwable) {
			Log.w("TexasUtils", throwable);
		}
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static boolean isIsDebug() {
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

	/**
	 * @param lhs lhs
	 * @param rhs rhs
	 * @return 返回两个对象是否相同
	 */
	public static boolean equals(Object lhs, Object rhs) {
		return (lhs == rhs) || (lhs != null && lhs.equals(rhs));
	}

	public static Typeface createTypefaceFromAsset(Context context, String path) {
		Typeface typeface = Typeface.createFromAsset(context.getAssets(), path);
		TextPaintCompat.fixMiuiOpt(typeface);
		return typeface;
	}

	/**
	 * Parses the string argument as a signed integer in the radix
	 * specified by the second argument. The characters in the string
	 * must all be digits of the specified radix (as determined by
	 * whether {@link java.lang.Character#digit(char, int)} returns a
	 * nonnegative value), except that the first character may be an
	 * ASCII minus sign {@code '-'} ({@code '\u005Cu002D'}) to
	 * indicate a negative value or an ASCII plus sign {@code '+'}
	 * ({@code '\u005Cu002B'}) to indicate a positive value. The
	 * resulting integer value is returned.
	 *
	 * <p>An exception of type {@code NumberFormatException} is
	 * thrown if any of the following situations occurs:
	 * <ul>
	 * <li>The first argument is {@code null} or is a string of
	 * length zero.
	 *
	 * <li>The radix is either smaller than
	 * {@link java.lang.Character#MIN_RADIX} or
	 * larger than {@link java.lang.Character#MAX_RADIX}.
	 *
	 * <li>Any character of the string is not a digit of the specified
	 * radix, except that the first character may be a minus sign
	 * {@code '-'} ({@code '\u005Cu002D'}) or plus sign
	 * {@code '+'} ({@code '\u005Cu002B'}) provided that the
	 * string is longer than length 1.
	 *
	 * <li>The value represented by the string is not a value of type
	 * {@code int}.
	 * </ul>
	 *
	 * <p>Examples:
	 * <blockquote><pre>
	 * parseInt("0", 10) returns 0
	 * parseInt("473", 10) returns 473
	 * parseInt("+42", 10) returns 42
	 * parseInt("-0", 10) returns 0
	 * parseInt("-FF", 16) returns -255
	 * parseInt("1100110", 2) returns 102
	 * parseInt("2147483647", 10) returns 2147483647
	 * parseInt("-2147483648", 10) returns -2147483648
	 * parseInt("2147483648", 10) throws a NumberFormatException
	 * parseInt("99", 8) throws a NumberFormatException
	 * parseInt("Kona", 10) throws a NumberFormatException
	 * parseInt("Kona", 27) returns 411787
	 * </pre></blockquote>
	 *
	 * @param s     string
	 * @param start start
	 * @param end   end
	 * @return the integer represented by the string argument in the
	 * specified radix.
	 * @throws NumberFormatException if the {@code String}
	 *                               does not contain a parsable {@code int}.
	 */
	public static int parseInt(CharSequence s, int start, int end) {
		return parseInt(s, start, end, 10);
	}

	/**
	 * Parses the string argument as a signed integer in the radix
	 * specified by the second argument. The characters in the string
	 * must all be digits of the specified radix (as determined by
	 * whether {@link java.lang.Character#digit(char, int)} returns a
	 * nonnegative value), except that the first character may be an
	 * ASCII minus sign {@code '-'} ({@code '\u005Cu002D'}) to
	 * indicate a negative value or an ASCII plus sign {@code '+'}
	 * ({@code '\u005Cu002B'}) to indicate a positive value. The
	 * resulting integer value is returned.
	 *
	 * <p>An exception of type {@code NumberFormatException} is
	 * thrown if any of the following situations occurs:
	 * <ul>
	 * <li>The first argument is {@code null} or is a string of
	 * length zero.
	 *
	 * <li>The radix is either smaller than
	 * {@link java.lang.Character#MIN_RADIX} or
	 * larger than {@link java.lang.Character#MAX_RADIX}.
	 *
	 * <li>Any character of the string is not a digit of the specified
	 * radix, except that the first character may be a minus sign
	 * {@code '-'} ({@code '\u005Cu002D'}) or plus sign
	 * {@code '+'} ({@code '\u005Cu002B'}) provided that the
	 * string is longer than length 1.
	 *
	 * <li>The value represented by the string is not a value of type
	 * {@code int}.
	 * </ul>
	 *
	 * <p>Examples:
	 * <blockquote><pre>
	 * parseInt("0", 10) returns 0
	 * parseInt("473", 10) returns 473
	 * parseInt("+42", 10) returns 42
	 * parseInt("-0", 10) returns 0
	 * parseInt("-FF", 16) returns -255
	 * parseInt("1100110", 2) returns 102
	 * parseInt("2147483647", 10) returns 2147483647
	 * parseInt("-2147483648", 10) returns -2147483648
	 * parseInt("2147483648", 10) throws a NumberFormatException
	 * parseInt("99", 8) throws a NumberFormatException
	 * parseInt("Kona", 10) throws a NumberFormatException
	 * parseInt("Kona", 27) returns 411787
	 * </pre></blockquote>
	 *
	 * @param s     string
	 * @param start start
	 * @param end   end
	 * @param radix radix
	 * @return the integer represented by the string argument in the
	 * specified radix.
	 * @throws NumberFormatException if the {@code String}
	 *                               does not contain a parsable {@code int}.
	 */
	public static int parseInt(CharSequence s, int start, int end, int radix) {
		/*
		 * WARNING: This method may be invoked early during VM initialization
		 * before IntegerCache is initialized. Care must be taken to not use
		 * the valueOf method.
		 */

		if (s == null) {
			// Android-changed: Improve exception message for parseInt.
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
			if (firstChar < '0') { // Possible leading "+" or "-"
				if (firstChar == '-') {
					negative = true;
					limit = Integer.MIN_VALUE;
				} else if (firstChar != '+')
					throw forInputString(s);

				if (len == 1) // Cannot have lone "+" or "-"
					throw forInputString(s);
				i++;
			}
			multmin = limit / radix;
			while (i < end) {
				// Accumulating negatively avoids surprises near MAX_VALUE
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
		setBidiFlags(paint);
	}

	private static void setBidiFlags(Paint paint) {
		if (BIDI_INVOKE_STUB.state == BidiInvokeStub.STATE_IDLE) {
			initBidiInvokeStub();
		}

		if (BIDI_INVOKE_STUB.state != BidiInvokeStub.STATE_OK) {
			return;
		}

		try {
			BIDI_INVOKE_STUB.method.invoke(paint, BIDI_INVOKE_STUB.flag);
		} catch (Throwable e) {
			BIDI_INVOKE_STUB.state = BidiInvokeStub.STATE_FAILED;
		}
	}

	private static void initBidiInvokeStub() {
		try {
			@SuppressLint("SoonBlockedPrivateApi") Method method = Paint.class.getDeclaredMethod("setBidiFlags", int.class);
			method.setAccessible(true);

			@SuppressLint("SoonBlockedPrivateApi") Field flag = Paint.class.getDeclaredField("BIDI_FORCE_LTR");
			flag.setAccessible(true);

			BIDI_INVOKE_STUB.method = method;
			BIDI_INVOKE_STUB.flag = flag.getInt(null);

			BIDI_INVOKE_STUB.state = BidiInvokeStub.STATE_OK;
		} catch (Throwable throwable) {
			BIDI_INVOKE_STUB.state = BidiInvokeStub.STATE_FAILED;
		}
	}

	private static class BidiInvokeStub {
		private static final int STATE_IDLE = 1;
		private static final int STATE_OK = 0;
		private static final int STATE_FAILED = -1;

		public int state = 1;
		public Method method;
		public int flag;
	}

	private static final BidiInvokeStub BIDI_INVOKE_STUB = new BidiInvokeStub();

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static float getLineSpace(Paragraph paragraph) {
		Layout layout = paragraph.getLayout();
		Layout.Advise advise = layout.getAdvise();
		float lineSpace = advise.getLineSpace();
		if (lineSpace < 0) {
			lineSpace = layout.getLineSpace();
		}
		return lineSpace;
	}

	/**
	 * 1. 字体变化了需要重新reload
	 * 2. 字体大小变化了需要重新reload
	 * 3. 更改了断字策略
	 * 4. 渲染模式发生改变
	 * 5. 断字策略发生变化
	 *
	 * @param prev    之前的render option
	 * @param current 现在的render option
	 * @return 是否需要重新reload
	 */
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static boolean diff(RenderOption prev, RenderOption current) {
		if (prev.isCompatMode() != current.isCompatMode()) {
			throw new RuntimeException("compat mode can not be changed at runtime");
		}

		if (!TexasUtils.equals(prev.getTypeface(), current.getTypeface())) {
			return true;
		}

		if (prev.getTextSize() != current.getTextSize()) {
			return true;
		}

		if (prev.getBreakStrategy() != current.getBreakStrategy()) {
			return true;
		}

		return prev.getHyphenStrategy() != current.getHyphenStrategy();
	}
}
