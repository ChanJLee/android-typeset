package me.chan.texas.text.icu;

import androidx.annotation.RestrictTo;

public class UnicodeUtils {
	
	public static boolean isIdeographic(int codePoint) {
		

		return Character.isIdeographic(codePoint);
	}






	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static boolean couldAffectRtl(int c) {
		return (0x0590 <= c && c <= 0x08FF) ||  
				c == 0x200E ||  
				c == 0x200F ||  
				(0x202A <= c && c <= 0x202E) ||  
				(0x2066 <= c && c <= 0x2069) ||  
				(0xD800 <= c && c <= 0xDFFF) ||  
				(0xFB1D <= c && c <= 0xFDFF) ||  
				(0xFE70 <= c && c <= 0xFEFE);  
	}

	public static boolean isCJKExtends(int codePoint) {

















		return (codePoint >= 0x4e00 && codePoint <= 0x9fff) ||
				(codePoint >= 0x3400 && codePoint <= 0x4dbf) ||
				(codePoint >= 0x20000 && codePoint <= 0x2a6df) ||
				(codePoint >= 0x2a700 && codePoint <= 0x2b73f) ||
				(codePoint >= 0x2b740 && codePoint <= 0x2b81f) ||
				(codePoint >= 0x2b820 && codePoint <= 0x2ceaf) ||
				(codePoint >= 0x2ceb0 && codePoint <= 0x2ebef) ||
				(codePoint >= 0xf900 && codePoint <= 0xfaff) ||
				(codePoint >= 0x2e80 && codePoint <= 0x2eff) ||
				(codePoint >= 0x2f00 && codePoint <= 0x2fdf) ||
				(codePoint >= 0x2ff0 && codePoint <= 0x2fff) ||
				(codePoint >= 0x3040 && codePoint <= 0x309f) ||
				(codePoint >= 0x30a0 && codePoint <= 0x30ff) ||
				(codePoint >= 0xff60 && codePoint <= 0xff9f) ||
				(codePoint >= 0x31f0 && codePoint <= 0x31ff) ||
				(codePoint >= 0xac00 && codePoint <= 0xd7af) ||
				(codePoint >= 0x1100 && codePoint <= 0x11ff);
	}


	
	@Deprecated
	public static boolean isLetter(int codePoint) {
		return isLatinLetter(codePoint);
	}

	
	public static boolean isLatinLetter(int codePoint) {
		
		return (codePoint >= 'a' && codePoint <= 'z') ||
				(codePoint >= 'A' && codePoint <= 'Z') ||
				isLetterLatin0(Character.getType(codePoint));
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static boolean isLetterLatin0(int type) {
		return type == Character.UPPERCASE_LETTER ||
				type == Character.LOWERCASE_LETTER;
	}

	
	public static boolean isDigit(int codePoint) {

		return (0x30 <= codePoint && codePoint <= 0x39);
	}

	
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
				
				|| type == Character.MATH_SYMBOL
				
				|| type == Character.CURRENCY_SYMBOL
				
				|| type == Character.MODIFIER_SYMBOL
				
				|| type == Character.OTHER_SYMBOL
				
				|| type == Character.MODIFIER_LETTER);
	}

	public static boolean isSpace(int codePoint) {




		return Character.isSpaceChar(codePoint);
	}

	public static boolean isWhitespace(int codePoint) {
		return Character.isWhitespace(codePoint);
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static boolean isHyphen(int codePoint) {
		return codePoint == '-';
	}


	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static boolean isRTLCharacter(int c) {
		byte directionality = Character.getDirectionality(c);
		return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
				directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
	}
}
