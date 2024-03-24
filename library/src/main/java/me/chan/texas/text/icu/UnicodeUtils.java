package me.chan.texas.text.icu;

import androidx.annotation.RestrictTo;

public class UnicodeUtils {
	/**
	 * cjkv字符
	 *
	 * @param codePoint unicode
	 * @return 是否是全角
	 */
	public static boolean isIdeographic(int codePoint) {
		/* 大部分的cjkv字符 */
		// https://www.compart.com/en/unicode/block/U+FF00
		return Character.isIdeographic(codePoint);
	}

	/**
	 * 是否是中文 (简体、繁体)
	 *
	 * @param codePoint unicode
	 * @return 是否是中文字符
	 */
	public static boolean isCn(int codePoint) {
		// https://www.compart.com/en/unicode/block/U+4E00
		// https://www.compart.com/en/unicode/block/U+F900
		return (codePoint >= 0x4e00 && codePoint <= 0x9fff) ||
				(codePoint >= 0xf900 && codePoint <= 0xfaff);
	}

	/**
	 * use {@link #isLatinLetter(int)} instead
	 *
	 * @param codePoint unicode
	 * @return 是否是空白字符
	 */
	@Deprecated
	public static boolean isLetter(int codePoint) {
		return isLatinLetter(codePoint);
	}

	/**
	 * @param codePoint unicode
	 * @return 是否是拉丁字母 a-z A-Z
	 */
	public static boolean isLatinLetter(int codePoint) {
		/*
		 * 优先自主判断
		 * */
		return (codePoint >= 'a' && codePoint <= 'z') ||
				(codePoint >= 'A' && codePoint <= 'Z') ||
				isLetterLatin0(Character.getType(codePoint));
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static boolean isLetterLatin0(int type) {
		return type == Character.UPPERCASE_LETTER ||
				type == Character.LOWERCASE_LETTER;
	}

	/**
	 * @param codePoint unicode
	 * @return 是否是数字
	 */
	public static boolean isDigit(int codePoint) {
		// https://www.compart.com/en/unicode/block/U+0000
		return (0x30 <= codePoint && codePoint <= 0x39);
	}

	/**
	 * @param codePoint unicode
	 * @return 是否是符号
	 */
	public static boolean isSymbolsAndPunctuation(int codePoint) {
		final int type = Character.getType(codePoint);
		return isSymbolsAndPunctuation0(type);
	}

	private static boolean isSymbolsAndPunctuation0(int type) {
		return (type == Character.CONNECTOR_PUNCTUATION
				|| type == Character.DASH_PUNCTUATION
				|| type == Character.END_PUNCTUATION
				|| type == Character.FINAL_QUOTE_PUNCTUATION
				|| type == Character.INITIAL_QUOTE_PUNCTUATION
				|| type == Character.OTHER_PUNCTUATION
				|| type == Character.START_PUNCTUATION
				/* https://www.compart.com/en/unicode/category/Sm */
				|| type == Character.MATH_SYMBOL
				/* https://www.compart.com/en/unicode/category/Sc */
				|| type == Character.CURRENCY_SYMBOL
				/* https://www.compart.com/en/unicode/category/Sk */
				|| type == Character.MODIFIER_SYMBOL
				/* https://www.compart.com/en/unicode/category/So */
				|| type == Character.OTHER_SYMBOL
				/* https://www.compart.com/en/unicode/category/Lm */
				|| type == Character.MODIFIER_LETTER);
	}

	public static boolean isSpace(int codePoint) {
		// https://www.compart.com/en/unicode/category/Zs
		// https://www.compart.com/en/unicode/category/Zp
		// https://www.compart.com/en/unicode/category/Zl
		// etc
		return Character.isSpaceChar(codePoint);
	}

	public static boolean isWhitespace(int codePoint) {
		return Character.isWhitespace(codePoint);
	}

	/**
	 * @param codePoint unicode
	 * @return 是否是内部分割符
	 */
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static boolean isBreakTokenSymbol(int codePoint) {
		// white 和 space 是有交叉的 0x20 就是
		/* 优先自主判断 */
		return codePoint == ' ' ||
				codePoint == '\n' ||
				codePoint == '\t' ||
				codePoint == '\r' ||
				isSpace(codePoint) ||
				isWhitespace(codePoint);
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static boolean isHyphen(int codePoint) {
		return codePoint == '-';
	}
}
