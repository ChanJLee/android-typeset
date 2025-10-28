package me.chan.texas.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.GetChars;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;

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
	private static boolean sIsDebug = true;

	private static float sDensity = 1.0f;

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static void init(Context context) {
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
	public static boolean isDebug() {
		return sIsDebug;
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
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static float getLineSpace(Paragraph paragraph) {
		Layout layout = paragraph.getLayout();
		Layout.Advise advise = layout.getAdvise();
		float lineSpace = advise.getLineSpacingExtra();
		if (lineSpace < 0) {
			lineSpace = layout.getLineSpacingExtra();
		}
		return lineSpace;
	}

	@IntDef({CmpType.CMP_DRAW, CmpType.CMP_TYPESET, CmpType.CMP_LOAD, CmpType.CMP_IGNORE})
	public @interface CmpType {
		/**
		 * 需要重新排版
		 */
		int CMP_TYPESET = 1;
		/**
		 * 需要重新加载
		 */
		int CMP_LOAD = 2;
		/**
		 * 需要重新绘制
		 */
		int CMP_DRAW = 3;
		/**
		 * 忽略
		 */
		int CMP_IGNORE = 0;
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
			case CmpType.CMP_IGNORE:
				return "CMP_IGNORE";
			default:
				return "UNKNOWN";
		}
	}

	/**
	 * 1. 字体变化了需要重新reload
	 * 2. 字体大小变化了需要重新reload
	 * 3. 更改了断字策略
	 * 4. 渲染模式发生改变
	 * 5. 断字策略发生变化
	 * 6. 全角符号优化变化
	 *
	 * @param prev    之前的render option
	 * @param current 现在的render option
	 * @return 是否需要重新reload
	 */
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	@CmpType
	public static int cmp(RenderOption prev, RenderOption current) {
		if (prev.isCompatMode() != current.isCompatMode()) {
			throw new RuntimeException("compat mode can not be changed at runtime");
		}

		if (prev.getBreakStrategy() != current.getBreakStrategy() ||
				prev.getHyphenStrategy() != current.getHyphenStrategy() ||
				prev.isBidiEnable() != current.isBidiEnable()) {
			return CmpType.CMP_LOAD;
		}

		if (prev.getTextSize() != current.getTextSize() ||
				!TexasUtils.equals(prev.getTypeface(), current.getTypeface()) ||
				prev.isFullWithSymbolOptimizationEnable() != current.isFullWithSymbolOptimizationEnable() ||
				Float.compare(prev.getLineSpacingExtra(), current.getLineSpacingExtra()) != 0) {
			return CmpType.CMP_TYPESET;
		}

		if (prev.equals(current)) {
			return CmpType.CMP_IGNORE;
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

	/**
	 * Returns true iff the two specified rectangles intersect. In no event are
	 * either of the rectangles modified. To record the intersection,
	 * use intersect() or setIntersect().
	 *
	 * @param a The first rectangle being tested for intersection
	 * @param b The second rectangle being tested for intersection
	 * @return true iff the two specified rectangles intersect. In no event are
	 * either of the rectangles modified.
	 */
	public static boolean intersects(@NonNull RectF a, @NonNull RectF b) {
		return a.left < b.right && b.left < a.right
				&& a.top < b.bottom && b.top < a.bottom;
	}
}
