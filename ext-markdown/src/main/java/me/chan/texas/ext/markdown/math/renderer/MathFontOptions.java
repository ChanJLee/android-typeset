package me.chan.texas.ext.markdown.math.renderer;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class MathFontOptions {
	public static final float UNITS_PER_EM = 1000F;
	public static final float RADICAL_VERTICAL_GAP = 50;
	public static final float RADICAL_DISPLAY_STYLE_VERTICAL_GAP = 148;
	public static final float RADICAL_RULE_THICKNESS = 40;
	public static final float RADICAL_EXTRA_ASCENDER = 40;
	public static final float RADICAL_KERN_BEFORE_DEGREE = 278;
	public static final float RADICAL_KERN_AFTER_DEGREE = -556;
	public static final float RADICAL_DEGREE_BOTTOM_RAISE_PERCENT = 60;
	public static final float FRACTION_NUMERATOR_SHIFT_UP = 394;
	public static final float FRACTION_NUMERATOR_DISPLAY_STYLE_SHIFT_UP = 677;
	public static final float FRACTION_DENOMINATOR_SHIFT_DOWN = 345;
	public static final float FRACTION_DENOMINATOR_DISPLAY_STYLE_SHIFT_DOWN = 686;
	public static final float FRACTION_NUMERATOR_GAP_MIN = 40;
	public static final float FRACTION_NUM_DISPLAY_STYLE_GAP_MIN = 120;
	public static final float FRACTION_RULE_THICKNESS = 40;
	public static final float FRACTION_DENOMINATOR_GAP_MIN = 40;
	public static final float FRACTION_DENOM_DISPLAY_STYLE_GAP_MIN = 120;
	public static final float SUPERSCRIPT_SHIFT_UP = 363;
	public static final float SUPERSCRIPT_SHIFT_UP_CRAMPED = 289;
	public static final float SUBSCRIPT_SHIFT_DOWN = 247;
	public static final float SUPERSCRIPT_BASELINE_DROP_MAX = 250;
	public static final float SUBSCRIPT_BASELINE_DROP_MIN = 200;
	public static final float SUPERSCRIPT_BOTTOM_MIN = 108;
	public static final float SUBSCRIPT_TOP_MAX = 344;
	public static final float SUB_SUPERSCRIPT_GAP_MIN = 160;
	public static final float AXIS_HEIGHT = 250;
	public static final float SCRIPT_PERCENT_SCALE_DOWN = 70;
	public static final float SCRIPT_SCRIPT_PERCENT_SCALE_DOWN = 50;
	private static final Map<String, String> GLYPHS = new HashMap<>();

	static {
		// SPACE
		GLYPHS.put("space", "\u0020");
		// EXCLAMATION MARK !
		GLYPHS.put("exclam", "\u0021");
		// QUOTATION MARK "
		GLYPHS.put("quotedbl", "\"");
		// NUMBER SIGN #
		GLYPHS.put("numbersign", "\u0023");
		// DOLLAR SIGN $
		GLYPHS.put("dollar", "\u0024");
		// PERCENT SIGN %
		GLYPHS.put("percent", "\u0025");
		// AMPERSAND &
		GLYPHS.put("ampersand", "\u0026");
		// APOSTROPHE '
		GLYPHS.put("quotesingle", "\u0027");
		// LEFT PARENTHESIS (
		GLYPHS.put("parenleft", "\u0028");
		// RIGHT PARENTHESIS )
		GLYPHS.put("parenright", "\u0029");
		// ASTERISK *
		GLYPHS.put("asterisk", "\u002A");
		// PLUS SIGN +
		GLYPHS.put("plus", "\u002B");
		// COMMA ,
		GLYPHS.put("comma", "\u002C");
		// HYPHEN-MINUS -
		GLYPHS.put("hyphen", "\u002D");
		// FULL STOP .
		GLYPHS.put("period", "\u002E");
		// SOLIDUS /
		GLYPHS.put("slash", "\u002F");
		// DIGIT ZERO 0
		GLYPHS.put("zero", "\u0030");
		// DIGIT ONE 1
		GLYPHS.put("one", "\u0031");
		// DIGIT TWO 2
		GLYPHS.put("two", "\u0032");
		// DIGIT THREE 3
		GLYPHS.put("three", "\u0033");
		// DIGIT FOUR 4
		GLYPHS.put("four", "\u0034");
		// DIGIT FIVE 5
		GLYPHS.put("five", "\u0035");
		// DIGIT SIX 6
		GLYPHS.put("six", "\u0036");
		// DIGIT SEVEN 7
		GLYPHS.put("seven", "\u0037");
		// DIGIT EIGHT 8
		GLYPHS.put("eight", "\u0038");
		// DIGIT NINE 9
		GLYPHS.put("nine", "\u0039");
		// COLON :
		GLYPHS.put("colon", "\u003A");
		// SEMICOLON ;
		GLYPHS.put("semicolon", "\u003B");
		// LESS-THAN SIGN <
		GLYPHS.put("less", "\u003C");
		// EQUALS SIGN =
		GLYPHS.put("equal", "\u003D");
		// GREATER-THAN SIGN >
		GLYPHS.put("greater", "\u003E");
		// QUESTION MARK ?
		GLYPHS.put("question", "\u003F");
		// COMMERCIAL AT @
		GLYPHS.put("at", "\u0040");
		// LATIN CAPITAL LETTER A A
		GLYPHS.put("A", "\u0041");
		// LATIN CAPITAL LETTER B B
		GLYPHS.put("B", "\u0042");
		// LATIN CAPITAL LETTER C C
		GLYPHS.put("C", "\u0043");
		// LATIN CAPITAL LETTER D D
		GLYPHS.put("D", "\u0044");
		// LATIN CAPITAL LETTER E E
		GLYPHS.put("E", "\u0045");
		// LATIN CAPITAL LETTER F F
		GLYPHS.put("F", "\u0046");
		// LATIN CAPITAL LETTER G G
		GLYPHS.put("G", "\u0047");
		// LATIN CAPITAL LETTER H H
		GLYPHS.put("H", "\u0048");
		// LATIN CAPITAL LETTER I I
		GLYPHS.put("I", "\u0049");
		// LATIN CAPITAL LETTER J J
		GLYPHS.put("J", "\u004A");
		// LATIN CAPITAL LETTER K K
		GLYPHS.put("K", "\u004B");
		// LATIN CAPITAL LETTER L L
		GLYPHS.put("L", "\u004C");
		// LATIN CAPITAL LETTER M M
		GLYPHS.put("M", "\u004D");
		// LATIN CAPITAL LETTER N N
		GLYPHS.put("N", "\u004E");
		// LATIN CAPITAL LETTER O O
		GLYPHS.put("O", "\u004F");
		// LATIN CAPITAL LETTER P P
		GLYPHS.put("P", "\u0050");
		// LATIN CAPITAL LETTER Q Q
		GLYPHS.put("Q", "\u0051");
		// LATIN CAPITAL LETTER R R
		GLYPHS.put("R", "\u0052");
		// LATIN CAPITAL LETTER S S
		GLYPHS.put("S", "\u0053");
		// LATIN CAPITAL LETTER T T
		GLYPHS.put("T", "\u0054");
		// LATIN CAPITAL LETTER U U
		GLYPHS.put("U", "\u0055");
		// LATIN CAPITAL LETTER V V
		GLYPHS.put("V", "\u0056");
		// LATIN CAPITAL LETTER W W
		GLYPHS.put("W", "\u0057");
		// LATIN CAPITAL LETTER X X
		GLYPHS.put("X", "\u0058");
		// LATIN CAPITAL LETTER Y Y
		GLYPHS.put("Y", "\u0059");
		// LATIN CAPITAL LETTER Z Z
		GLYPHS.put("Z", "\u005A");
		// LEFT SQUARE BRACKET [
		GLYPHS.put("bracketleft", "\u005B");
		// REVERSE SOLIDUS \
		GLYPHS.put("backslash", "\\");
		// RIGHT SQUARE BRACKET ]
		GLYPHS.put("bracketright", "\u005D");
		// CIRCUMFLEX ACCENT ^
		GLYPHS.put("asciicircum", "\u005E");
		// LOW LINE _
		GLYPHS.put("underscore", "\u005F");
		// GRAVE ACCENT `
		GLYPHS.put("grave", "\u0060");
		// LATIN SMALL LETTER A a
		GLYPHS.put("a", "\u0061");
		// LATIN SMALL LETTER B b
		GLYPHS.put("b", "\u0062");
		// LATIN SMALL LETTER C c
		GLYPHS.put("c", "\u0063");
		// LATIN SMALL LETTER D d
		GLYPHS.put("d", "\u0064");
		// LATIN SMALL LETTER E e
		GLYPHS.put("e", "\u0065");
		// LATIN SMALL LETTER F f
		GLYPHS.put("f", "\u0066");
		// LATIN SMALL LETTER G g
		GLYPHS.put("g", "\u0067");
		// LATIN SMALL LETTER H h
		GLYPHS.put("h", "\u0068");
		// LATIN SMALL LETTER I i
		GLYPHS.put("i", "\u0069");
		// LATIN SMALL LETTER J j
		GLYPHS.put("j", "\u006A");
		// LATIN SMALL LETTER K k
		GLYPHS.put("k", "\u006B");
		// LATIN SMALL LETTER L l
		GLYPHS.put("l", "\u006C");
		// LATIN SMALL LETTER M m
		GLYPHS.put("m", "\u006D");
		// LATIN SMALL LETTER N n
		GLYPHS.put("n", "\u006E");
		// LATIN SMALL LETTER O o
		GLYPHS.put("o", "\u006F");
		// LATIN SMALL LETTER P p
		GLYPHS.put("p", "\u0070");
		// LATIN SMALL LETTER Q q
		GLYPHS.put("q", "\u0071");
		// LATIN SMALL LETTER R r
		GLYPHS.put("r", "\u0072");
		// LATIN SMALL LETTER S s
		GLYPHS.put("s", "\u0073");
		// LATIN SMALL LETTER T t
		GLYPHS.put("t", "\u0074");
		// LATIN SMALL LETTER U u
		GLYPHS.put("u", "\u0075");
		// LATIN SMALL LETTER V v
		GLYPHS.put("v", "\u0076");
		// LATIN SMALL LETTER W w
		GLYPHS.put("w", "\u0077");
		// LATIN SMALL LETTER X x
		GLYPHS.put("x", "\u0078");
		// LATIN SMALL LETTER Y y
		GLYPHS.put("y", "\u0079");
		// LATIN SMALL LETTER Z z
		GLYPHS.put("z", "\u007A");
		// LEFT CURLY BRACKET {
		GLYPHS.put("braceleft", "\u007B");
		// VERTICAL LINE |
		GLYPHS.put("bar", "\u007C");
		// RIGHT CURLY BRACKET }
		GLYPHS.put("braceright", "\u007D");
		// TILDE ~
		GLYPHS.put("asciitilde", "\u007E");
		// NO-BREAK SPACE  
		GLYPHS.put("uni00A0", "\u00A0");
		// INVERTED EXCLAMATION MARK ¡
		GLYPHS.put("exclamdown", "\u00A1");
		// CENT SIGN ¢
		GLYPHS.put("cent", "\u00A2");
		// POUND SIGN £
		GLYPHS.put("sterling", "\u00A3");
		// CURRENCY SIGN ¤
		GLYPHS.put("currency", "\u00A4");
		// YEN SIGN ¥
		GLYPHS.put("yen", "\u00A5");
		// BROKEN BAR ¦
		GLYPHS.put("brokenbar", "\u00A6");
		// SECTION SIGN §
		GLYPHS.put("section", "\u00A7");
		// DIAERESIS ¨
		GLYPHS.put("dieresis", "\u00A8");
		// COPYRIGHT SIGN ©
		GLYPHS.put("copyright", "\u00A9");
		// FEMININE ORDINAL INDICATOR ª
		GLYPHS.put("ordfeminine", "\u00AA");
		// LEFT-POINTING DOUBLE ANGLE QUOTATION MARK «
		GLYPHS.put("guillemotleft", "\u00AB");
		// NOT SIGN ¬
		GLYPHS.put("logicalnot", "\u00AC");
		// REGISTERED SIGN ®
		GLYPHS.put("registered", "\u00AE");
		// MACRON ¯
		GLYPHS.put("macron", "\u00AF");
		// DEGREE SIGN °
		GLYPHS.put("degree", "\u00B0");
		// PLUS-MINUS SIGN ±
		GLYPHS.put("plusminus", "\u00B1");
		// ACUTE ACCENT ´
		GLYPHS.put("acute", "\u00B4");
		// MICRO SIGN µ
		GLYPHS.put("uni00B5", "\u00B5");
		// PILCROW SIGN ¶
		GLYPHS.put("paragraph", "\u00B6");
		// MIDDLE DOT ·
		GLYPHS.put("periodcentered", "\u00B7");
		// CEDILLA ¸
		GLYPHS.put("cedilla", "\u00B8");
		// MASCULINE ORDINAL INDICATOR º
		GLYPHS.put("ordmasculine", "\u00BA");
		// RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK »
		GLYPHS.put("guillemotright", "\u00BB");
		// INVERTED QUESTION MARK ¿
		GLYPHS.put("questiondown", "\u00BF");
		// LATIN CAPITAL LETTER A WITH GRAVE À
		GLYPHS.put("Agrave", "\u00C0");
		// LATIN CAPITAL LETTER A WITH ACUTE Á
		GLYPHS.put("Aacute", "\u00C1");
		// LATIN CAPITAL LETTER A WITH CIRCUMFLEX Â
		GLYPHS.put("Acircumflex", "\u00C2");
		// LATIN CAPITAL LETTER A WITH TILDE Ã
		GLYPHS.put("Atilde", "\u00C3");
		// LATIN CAPITAL LETTER A WITH DIAERESIS Ä
		GLYPHS.put("Adieresis", "\u00C4");
		// LATIN CAPITAL LETTER A WITH RING ABOVE Å
		GLYPHS.put("Aring", "\u00C5");
		// LATIN CAPITAL LETTER AE Æ
		GLYPHS.put("AE", "\u00C6");
		// LATIN CAPITAL LETTER C WITH CEDILLA Ç
		GLYPHS.put("Ccedilla", "\u00C7");
		// LATIN CAPITAL LETTER E WITH GRAVE È
		GLYPHS.put("Egrave", "\u00C8");
		// LATIN CAPITAL LETTER E WITH ACUTE É
		GLYPHS.put("Eacute", "\u00C9");
		// LATIN CAPITAL LETTER E WITH CIRCUMFLEX Ê
		GLYPHS.put("Ecircumflex", "\u00CA");
		// LATIN CAPITAL LETTER E WITH DIAERESIS Ë
		GLYPHS.put("Edieresis", "\u00CB");
		// LATIN CAPITAL LETTER I WITH GRAVE Ì
		GLYPHS.put("Igrave", "\u00CC");
		// LATIN CAPITAL LETTER I WITH ACUTE Í
		GLYPHS.put("Iacute", "\u00CD");
		// LATIN CAPITAL LETTER I WITH CIRCUMFLEX Î
		GLYPHS.put("Icircumflex", "\u00CE");
		// LATIN CAPITAL LETTER I WITH DIAERESIS Ï
		GLYPHS.put("Idieresis", "\u00CF");
		// LATIN CAPITAL LETTER ETH Ð
		GLYPHS.put("Eth", "\u00D0");
		// LATIN CAPITAL LETTER N WITH TILDE Ñ
		GLYPHS.put("Ntilde", "\u00D1");
		// LATIN CAPITAL LETTER O WITH GRAVE Ò
		GLYPHS.put("Ograve", "\u00D2");
		// LATIN CAPITAL LETTER O WITH ACUTE Ó
		GLYPHS.put("Oacute", "\u00D3");
		// LATIN CAPITAL LETTER O WITH CIRCUMFLEX Ô
		GLYPHS.put("Ocircumflex", "\u00D4");
		// LATIN CAPITAL LETTER O WITH TILDE Õ
		GLYPHS.put("Otilde", "\u00D5");
		// LATIN CAPITAL LETTER O WITH DIAERESIS Ö
		GLYPHS.put("Odieresis", "\u00D6");
		// MULTIPLICATION SIGN ×
		GLYPHS.put("multiply", "\u00D7");
		// LATIN CAPITAL LETTER O WITH STROKE Ø
		GLYPHS.put("Oslash", "\u00D8");
		// LATIN CAPITAL LETTER U WITH GRAVE Ù
		GLYPHS.put("Ugrave", "\u00D9");
		// LATIN CAPITAL LETTER U WITH ACUTE Ú
		GLYPHS.put("Uacute", "\u00DA");
		// LATIN CAPITAL LETTER U WITH CIRCUMFLEX Û
		GLYPHS.put("Ucircumflex", "\u00DB");
		// LATIN CAPITAL LETTER U WITH DIAERESIS Ü
		GLYPHS.put("Udieresis", "\u00DC");
		// LATIN CAPITAL LETTER Y WITH ACUTE Ý
		GLYPHS.put("Yacute", "\u00DD");
		// LATIN CAPITAL LETTER THORN Þ
		GLYPHS.put("Thorn", "\u00DE");
		// LATIN SMALL LETTER SHARP S ß
		GLYPHS.put("germandbls", "\u00DF");
		// LATIN SMALL LETTER A WITH GRAVE à
		GLYPHS.put("agrave", "\u00E0");
		// LATIN SMALL LETTER A WITH ACUTE á
		GLYPHS.put("aacute", "\u00E1");
		// LATIN SMALL LETTER A WITH CIRCUMFLEX â
		GLYPHS.put("acircumflex", "\u00E2");
		// LATIN SMALL LETTER A WITH TILDE ã
		GLYPHS.put("atilde", "\u00E3");
		// LATIN SMALL LETTER A WITH DIAERESIS ä
		GLYPHS.put("adieresis", "\u00E4");
		// LATIN SMALL LETTER A WITH RING ABOVE å
		GLYPHS.put("aring", "\u00E5");
		// LATIN SMALL LETTER AE æ
		GLYPHS.put("ae", "\u00E6");
		// LATIN SMALL LETTER C WITH CEDILLA ç
		GLYPHS.put("ccedilla", "\u00E7");
		// LATIN SMALL LETTER E WITH GRAVE è
		GLYPHS.put("egrave", "\u00E8");
		// LATIN SMALL LETTER E WITH ACUTE é
		GLYPHS.put("eacute", "\u00E9");
		// LATIN SMALL LETTER E WITH CIRCUMFLEX ê
		GLYPHS.put("ecircumflex", "\u00EA");
		// LATIN SMALL LETTER E WITH DIAERESIS ë
		GLYPHS.put("edieresis", "\u00EB");
		// LATIN SMALL LETTER I WITH GRAVE ì
		GLYPHS.put("igrave", "\u00EC");
		// LATIN SMALL LETTER I WITH ACUTE í
		GLYPHS.put("iacute", "\u00ED");
		// LATIN SMALL LETTER I WITH CIRCUMFLEX î
		GLYPHS.put("icircumflex", "\u00EE");
		// LATIN SMALL LETTER I WITH DIAERESIS ï
		GLYPHS.put("idieresis", "\u00EF");
		// LATIN SMALL LETTER ETH ð
		GLYPHS.put("eth", "\u00F0");
		// LATIN SMALL LETTER N WITH TILDE ñ
		GLYPHS.put("ntilde", "\u00F1");
		// LATIN SMALL LETTER O WITH GRAVE ò
		GLYPHS.put("ograve", "\u00F2");
		// LATIN SMALL LETTER O WITH ACUTE ó
		GLYPHS.put("oacute", "\u00F3");
		// LATIN SMALL LETTER O WITH CIRCUMFLEX ô
		GLYPHS.put("ocircumflex", "\u00F4");
		// LATIN SMALL LETTER O WITH TILDE õ
		GLYPHS.put("otilde", "\u00F5");
		// LATIN SMALL LETTER O WITH DIAERESIS ö
		GLYPHS.put("odieresis", "\u00F6");
		// DIVISION SIGN ÷
		GLYPHS.put("divide", "\u00F7");
		// LATIN SMALL LETTER O WITH STROKE ø
		GLYPHS.put("oslash", "\u00F8");
		// LATIN SMALL LETTER U WITH GRAVE ù
		GLYPHS.put("ugrave", "\u00F9");
		// LATIN SMALL LETTER U WITH ACUTE ú
		GLYPHS.put("uacute", "\u00FA");
		// LATIN SMALL LETTER U WITH CIRCUMFLEX û
		GLYPHS.put("ucircumflex", "\u00FB");
		// LATIN SMALL LETTER U WITH DIAERESIS ü
		GLYPHS.put("udieresis", "\u00FC");
		// LATIN SMALL LETTER Y WITH ACUTE ý
		GLYPHS.put("yacute", "\u00FD");
		// LATIN SMALL LETTER THORN þ
		GLYPHS.put("thorn", "\u00FE");
		// LATIN SMALL LETTER Y WITH DIAERESIS ÿ
		GLYPHS.put("ydieresis", "\u00FF");
		// LATIN CAPITAL LETTER A WITH MACRON Ā
		GLYPHS.put("Amacron", "\u0100");
		// LATIN SMALL LETTER A WITH MACRON ā
		GLYPHS.put("amacron", "\u0101");
		// LATIN CAPITAL LETTER A WITH BREVE Ă
		GLYPHS.put("Abreve", "\u0102");
		// LATIN SMALL LETTER A WITH BREVE ă
		GLYPHS.put("abreve", "\u0103");
		// LATIN CAPITAL LETTER A WITH OGONEK Ą
		GLYPHS.put("Aogonek", "\u0104");
		// LATIN SMALL LETTER A WITH OGONEK ą
		GLYPHS.put("aogonek", "\u0105");
		// LATIN CAPITAL LETTER C WITH ACUTE Ć
		GLYPHS.put("Cacute", "\u0106");
		// LATIN SMALL LETTER C WITH ACUTE ć
		GLYPHS.put("cacute", "\u0107");
		// LATIN CAPITAL LETTER C WITH CARON Č
		GLYPHS.put("Ccaron", "\u010C");
		// LATIN SMALL LETTER C WITH CARON č
		GLYPHS.put("ccaron", "\u010D");
		// LATIN CAPITAL LETTER D WITH CARON Ď
		GLYPHS.put("Dcaron", "\u010E");
		// LATIN SMALL LETTER D WITH CARON ď
		GLYPHS.put("dcaron", "\u010F");
		// LATIN CAPITAL LETTER D WITH STROKE Đ
		GLYPHS.put("Dcroat", "\u0110");
		// LATIN CAPITAL LETTER E WITH MACRON Ē
		GLYPHS.put("Emacron", "\u0112");
		// LATIN SMALL LETTER E WITH MACRON ē
		GLYPHS.put("emacron", "\u0113");
		// LATIN CAPITAL LETTER E WITH DOT ABOVE Ė
		GLYPHS.put("Edotaccent", "\u0116");
		// LATIN SMALL LETTER E WITH DOT ABOVE ė
		GLYPHS.put("edotaccent", "\u0117");
		// LATIN CAPITAL LETTER E WITH OGONEK Ę
		GLYPHS.put("Eogonek", "\u0118");
		// LATIN SMALL LETTER E WITH OGONEK ę
		GLYPHS.put("eogonek", "\u0119");
		// LATIN CAPITAL LETTER E WITH CARON Ě
		GLYPHS.put("Ecaron", "\u011A");
		// LATIN SMALL LETTER E WITH CARON ě
		GLYPHS.put("ecaron", "\u011B");
		// LATIN CAPITAL LETTER G WITH BREVE Ğ
		GLYPHS.put("Gbreve", "\u011E");
		// LATIN SMALL LETTER G WITH BREVE ğ
		GLYPHS.put("gbreve", "\u011F");
		// LATIN CAPITAL LETTER G WITH CEDILLA Ģ
		GLYPHS.put("Gcommaaccent", "\u0122");
		// LATIN SMALL LETTER G WITH CEDILLA ģ
		GLYPHS.put("gcommaaccent", "\u0123");
		// LATIN CAPITAL LETTER I WITH TILDE Ĩ
		GLYPHS.put("Itilde", "\u0128");
		// LATIN SMALL LETTER I WITH TILDE ĩ
		GLYPHS.put("itilde", "\u0129");
		// LATIN CAPITAL LETTER I WITH MACRON Ī
		GLYPHS.put("Imacron", "\u012A");
		// LATIN SMALL LETTER I WITH MACRON ī
		GLYPHS.put("imacron", "\u012B");
		// LATIN CAPITAL LETTER I WITH OGONEK Į
		GLYPHS.put("Iogonek", "\u012E");
		// LATIN SMALL LETTER I WITH OGONEK į
		GLYPHS.put("iogonek", "\u012F");
		// LATIN CAPITAL LETTER I WITH DOT ABOVE İ
		GLYPHS.put("Idotaccent", "\u0130");
		// LATIN SMALL LETTER DOTLESS I ı
		GLYPHS.put("dotlessi", "\u0131");
		// LATIN CAPITAL LIGATURE IJ Ĳ
		GLYPHS.put("I_J", "\u0132");
		// LATIN SMALL LIGATURE IJ ĳ
		GLYPHS.put("i_j", "\u0133");
		// LATIN CAPITAL LETTER K WITH CEDILLA Ķ
		GLYPHS.put("Kcommaaccent", "\u0136");
		// LATIN SMALL LETTER K WITH CEDILLA ķ
		GLYPHS.put("kcommaaccent", "\u0137");
		// LATIN CAPITAL LETTER L WITH ACUTE Ĺ
		GLYPHS.put("Lacute", "\u0139");
		// LATIN SMALL LETTER L WITH ACUTE ĺ
		GLYPHS.put("lacute", "\u013A");
		// LATIN CAPITAL LETTER L WITH CEDILLA Ļ
		GLYPHS.put("Lcommaaccent", "\u013B");
		// LATIN SMALL LETTER L WITH CEDILLA ļ
		GLYPHS.put("lcommaaccent", "\u013C");
		// LATIN CAPITAL LETTER L WITH CARON Ľ
		GLYPHS.put("Lcaron", "\u013D");
		// LATIN SMALL LETTER L WITH CARON ľ
		GLYPHS.put("lcaron", "\u013E");
		// LATIN CAPITAL LETTER L WITH STROKE Ł
		GLYPHS.put("Lslash", "\u0141");
		// LATIN SMALL LETTER L WITH STROKE ł
		GLYPHS.put("lslash", "\u0142");
		// LATIN CAPITAL LETTER N WITH ACUTE Ń
		GLYPHS.put("Nacute", "\u0143");
		// LATIN SMALL LETTER N WITH ACUTE ń
		GLYPHS.put("nacute", "\u0144");
		// LATIN CAPITAL LETTER N WITH CEDILLA Ņ
		GLYPHS.put("Ncommaaccent", "\u0145");
		// LATIN SMALL LETTER N WITH CEDILLA ņ
		GLYPHS.put("ncommaaccent", "\u0146");
		// LATIN CAPITAL LETTER N WITH CARON Ň
		GLYPHS.put("Ncaron", "\u0147");
		// LATIN SMALL LETTER N WITH CARON ň
		GLYPHS.put("ncaron", "\u0148");
		// LATIN CAPITAL LETTER ENG Ŋ
		GLYPHS.put("Eng", "\u014A");
		// LATIN SMALL LETTER ENG ŋ
		GLYPHS.put("eng", "\u014B");
		// LATIN CAPITAL LETTER O WITH MACRON Ō
		GLYPHS.put("Omacron", "\u014C");
		// LATIN SMALL LETTER O WITH MACRON ō
		GLYPHS.put("omacron", "\u014D");
		// LATIN CAPITAL LETTER O WITH DOUBLE ACUTE Ő
		GLYPHS.put("Ohungarumlaut", "\u0150");
		// LATIN SMALL LETTER O WITH DOUBLE ACUTE ő
		GLYPHS.put("ohungarumlaut", "\u0151");
		// LATIN CAPITAL LIGATURE OE Œ
		GLYPHS.put("OE", "\u0152");
		// LATIN SMALL LIGATURE OE œ
		GLYPHS.put("oe", "\u0153");
		// LATIN CAPITAL LETTER R WITH ACUTE Ŕ
		GLYPHS.put("Racute", "\u0154");
		// LATIN SMALL LETTER R WITH ACUTE ŕ
		GLYPHS.put("racute", "\u0155");
		// LATIN CAPITAL LETTER R WITH CEDILLA Ŗ
		GLYPHS.put("Rcommaaccent", "\u0156");
		// LATIN SMALL LETTER R WITH CEDILLA ŗ
		GLYPHS.put("rcommaaccent", "\u0157");
		// LATIN CAPITAL LETTER R WITH CARON Ř
		GLYPHS.put("Rcaron", "\u0158");
		// LATIN SMALL LETTER R WITH CARON ř
		GLYPHS.put("rcaron", "\u0159");
		// LATIN CAPITAL LETTER S WITH ACUTE Ś
		GLYPHS.put("Sacute", "\u015A");
		// LATIN SMALL LETTER S WITH ACUTE ś
		GLYPHS.put("sacute", "\u015B");
		// LATIN CAPITAL LETTER S WITH CEDILLA Ş
		GLYPHS.put("Scedilla", "\u015E");
		// LATIN SMALL LETTER S WITH CEDILLA ş
		GLYPHS.put("scedilla", "\u015F");
		// LATIN CAPITAL LETTER S WITH CARON Š
		GLYPHS.put("Scaron", "\u0160");
		// LATIN SMALL LETTER S WITH CARON š
		GLYPHS.put("scaron", "\u0161");
		// LATIN CAPITAL LETTER T WITH CEDILLA Ţ
		GLYPHS.put("Tcedilla", "\u0162");
		// LATIN SMALL LETTER T WITH CEDILLA ţ
		GLYPHS.put("tcedilla", "\u0163");
		// LATIN CAPITAL LETTER T WITH CARON Ť
		GLYPHS.put("Tcaron", "\u0164");
		// LATIN SMALL LETTER T WITH CARON ť
		GLYPHS.put("tcaron", "\u0165");
		// LATIN CAPITAL LETTER U WITH TILDE Ũ
		GLYPHS.put("Utilde", "\u0168");
		// LATIN SMALL LETTER U WITH TILDE ũ
		GLYPHS.put("utilde", "\u0169");
		// LATIN CAPITAL LETTER U WITH MACRON Ū
		GLYPHS.put("Umacron", "\u016A");
		// LATIN SMALL LETTER U WITH MACRON ū
		GLYPHS.put("umacron", "\u016B");
		// LATIN CAPITAL LETTER U WITH RING ABOVE Ů
		GLYPHS.put("Uring", "\u016E");
		// LATIN SMALL LETTER U WITH RING ABOVE ů
		GLYPHS.put("uring", "\u016F");
		// LATIN CAPITAL LETTER U WITH DOUBLE ACUTE Ű
		GLYPHS.put("Uhungarumlaut", "\u0170");
		// LATIN SMALL LETTER U WITH DOUBLE ACUTE ű
		GLYPHS.put("uhungarumlaut", "\u0171");
		// LATIN CAPITAL LETTER U WITH OGONEK Ų
		GLYPHS.put("Uogonek", "\u0172");
		// LATIN SMALL LETTER U WITH OGONEK ų
		GLYPHS.put("uogonek", "\u0173");
		// LATIN CAPITAL LETTER Y WITH DIAERESIS Ÿ
		GLYPHS.put("Ydieresis", "\u0178");
		// LATIN CAPITAL LETTER Z WITH ACUTE Ź
		GLYPHS.put("Zacute", "\u0179");
		// LATIN SMALL LETTER Z WITH ACUTE ź
		GLYPHS.put("zacute", "\u017A");
		// LATIN CAPITAL LETTER Z WITH DOT ABOVE Ż
		GLYPHS.put("Zdotaccent", "\u017B");
		// LATIN SMALL LETTER Z WITH DOT ABOVE ż
		GLYPHS.put("zdotaccent", "\u017C");
		// LATIN CAPITAL LETTER Z WITH CARON Ž
		GLYPHS.put("Zcaron", "\u017D");
		// LATIN SMALL LETTER Z WITH CARON ž
		GLYPHS.put("zcaron", "\u017E");
		// LATIN SMALL LETTER LONG S ſ
		GLYPHS.put("longs", "\u017F");
		// LATIN CAPITAL LETTER O WITH HORN Ơ
		GLYPHS.put("Ohorn", "\u01A0");
		// LATIN SMALL LETTER O WITH HORN ơ
		GLYPHS.put("ohorn", "\u01A1");
		// LATIN CAPITAL LETTER U WITH HORN Ư
		GLYPHS.put("Uhorn", "\u01AF");
		// LATIN SMALL LETTER U WITH HORN ư
		GLYPHS.put("uhorn", "\u01B0");
		// LATIN CAPITAL LETTER S WITH COMMA BELOW Ș
		GLYPHS.put("uni0218", "\u0218");
		// LATIN SMALL LETTER S WITH COMMA BELOW ș
		GLYPHS.put("uni0219", "\u0219");
		// LATIN CAPITAL LETTER T WITH COMMA BELOW Ț
		GLYPHS.put("uni021A", "\u021A");
		// LATIN SMALL LETTER T WITH COMMA BELOW ț
		GLYPHS.put("uni021B", "\u021B");
		// LATIN SMALL LETTER DOTLESS J ȷ
		GLYPHS.put("dotlessj", "\u0237");
		// MODIFIER LETTER CIRCUMFLEX ACCENT ˆ
		GLYPHS.put("circumflex", "\u02C6");
		// CARON ˇ
		GLYPHS.put("caron", "\u02C7");
		// BREVE ˘
		GLYPHS.put("breve", "\u02D8");
		// DOT ABOVE ˙
		GLYPHS.put("dotaccent", "\u02D9");
		// DOT ABOVE ˙
		GLYPHS.put("ddotaccent", "\u02D9\u02D9");
		// DOT ABOVE ˙
		GLYPHS.put("dddotaccent", "\u02D9\u02D9\u02D9");
		// RING ABOVE ˚
		GLYPHS.put("ring", "\u02DA");
		// OGONEK ˛
		GLYPHS.put("ogonek", "\u02DB");
		// SMALL TILDE ˜
		GLYPHS.put("tilde", "\u02DC");
		// DOUBLE ACUTE ACCENT ˝
		GLYPHS.put("hungarumlaut", "\u02DD");
		// COMBINING GRAVE ACCENT ̀
		GLYPHS.put("uni0300", "\u0300");
		// COMBINING ACUTE ACCENT ́
		GLYPHS.put("uni0301", "\u0301");
		// COMBINING CIRCUMFLEX ACCENT ̂
		GLYPHS.put("circumflexcmb", "\u0302");
		// COMBINING TILDE ̃
		GLYPHS.put("tildecomb", "\u0303");
		// COMBINING MACRON ̄
		GLYPHS.put("uni0304", "\u0304");
		// COMBINING OVERLINE ̅
		GLYPHS.put("overlinecmb", "\u0305");
		// COMBINING BREVE ̆
		GLYPHS.put("brevecmb", "\u0306");
		// COMBINING DOT ABOVE ̇
		GLYPHS.put("uni0307", "\u0307");
		// COMBINING DIAERESIS ̈
		GLYPHS.put("uni0308", "\u0308");
		// COMBINING HOOK ABOVE ̉
		GLYPHS.put("uni0309", "\u0309");
		// COMBINING RING ABOVE ̊
		GLYPHS.put("uni030A", "\u030A");
		// COMBINING DOUBLE ACUTE ACCENT ̋
		GLYPHS.put("uni030B", "\u030B");
		// COMBINING CARON ̌
		GLYPHS.put("caroncmb", "\u030C");
		// COMBINING DOUBLE GRAVE ACCENT ̏
		GLYPHS.put("uni030F", "\u030F");
		// COMBINING INVERTED BREVE ̑
		GLYPHS.put("breveinvertedcmb", "\u0311");
		// COMBINING DOT BELOW ̣
		GLYPHS.put("uni0323", "\u0323");
		// COMBINING COMMA BELOW ̦
		GLYPHS.put("uni0326", "\u0326");
		// COMBINING CARON BELOW ̬
		GLYPHS.put("caronbelowcmb", "\u032C");
		// COMBINING CIRCUMFLEX ACCENT BELOW ̭
		GLYPHS.put("circumflexbelowcmb", "\u032D");
		// COMBINING BREVE BELOW ̮
		GLYPHS.put("brevebelowcmb", "\u032E");
		// COMBINING INVERTED BREVE BELOW ̯
		GLYPHS.put("breveinvertedbelowcmb", "\u032F");
		// COMBINING TILDE BELOW ̰
		GLYPHS.put("tildebelowcmb", "\u0330");
		// COMBINING MACRON BELOW ̱
		GLYPHS.put("uni0331", "\u0331");
		// COMBINING LOW LINE ̲
		GLYPHS.put("lowlinecmb", "\u0332");
		// COMBINING DOUBLE LOW LINE ̳
		GLYPHS.put("dbllowlinecmb", "\u0333");
		// COMBINING LONG SOLIDUS OVERLAY ̸
		GLYPHS.put("uni0338", "\u0338");
		// COMBINING DOUBLE OVERLINE ̿
		GLYPHS.put("dbloverlinecmb", "\u033F");
		// COMBINING LEFT RIGHT ARROW BELOW ͍
		GLYPHS.put("uni034D", "\u034D");
		// GREEK CAPITAL LETTER ALPHA Α
		GLYPHS.put("Alpha", "\u0391");
		// GREEK CAPITAL LETTER BETA Β
		GLYPHS.put("Beta", "\u0392");
		// GREEK CAPITAL LETTER GAMMA Γ
		GLYPHS.put("Gamma", "\u0393");
		// GREEK CAPITAL LETTER DELTA Δ
		GLYPHS.put("Delta", "\u0394");
		// GREEK CAPITAL LETTER EPSILON Ε
		GLYPHS.put("Epsilon", "\u0395");
		// GREEK CAPITAL LETTER ZETA Ζ
		GLYPHS.put("Zeta", "\u0396");
		// GREEK CAPITAL LETTER ETA Η
		GLYPHS.put("Eta", "\u0397");
		// GREEK CAPITAL LETTER THETA Θ
		GLYPHS.put("Theta", "\u0398");
		// GREEK CAPITAL LETTER IOTA Ι
		GLYPHS.put("Iota", "\u0399");
		// GREEK CAPITAL LETTER KAPPA Κ
		GLYPHS.put("Kappa", "\u039A");
		// GREEK CAPITAL LETTER LAMDA Λ
		GLYPHS.put("Lambda", "\u039B");
		// GREEK CAPITAL LETTER MU Μ
		GLYPHS.put("Mu", "\u039C");
		// GREEK CAPITAL LETTER NU Ν
		GLYPHS.put("Nu", "\u039D");
		// GREEK CAPITAL LETTER XI Ξ
		GLYPHS.put("Xi", "\u039E");
		// GREEK CAPITAL LETTER OMICRON Ο
		GLYPHS.put("Omicron", "\u039F");
		// GREEK CAPITAL LETTER PI Π
		GLYPHS.put("Pi", "\u03A0");
		// GREEK CAPITAL LETTER RHO Ρ
		GLYPHS.put("Rho", "\u03A1");
		// GREEK CAPITAL LETTER SIGMA Σ
		GLYPHS.put("Sigma", "\u03A3");
		// GREEK CAPITAL LETTER TAU Τ
		GLYPHS.put("Tau", "\u03A4");
		// GREEK CAPITAL LETTER UPSILON Υ
		GLYPHS.put("Upsilon", "\u03A5");
		// GREEK CAPITAL LETTER PHI Φ
		GLYPHS.put("Phi", "\u03A6");
		// GREEK CAPITAL LETTER CHI Χ
		GLYPHS.put("Chi", "\u03A7");
		// GREEK CAPITAL LETTER PSI Ψ
		GLYPHS.put("Psi", "\u03A8");
		// GREEK CAPITAL LETTER OMEGA Ω
		GLYPHS.put("Omega", "\u03A9");
		// GREEK SMALL LETTER ALPHA α
		GLYPHS.put("alpha", "\u03B1");
		// GREEK SMALL LETTER BETA β
		GLYPHS.put("beta", "\u03B2");
		// GREEK SMALL LETTER GAMMA γ
		GLYPHS.put("gamma", "\u03B3");
		// GREEK SMALL LETTER DELTA δ
		GLYPHS.put("delta", "\u03B4");
		// GREEK SMALL LETTER EPSILON ε
		GLYPHS.put("epsilon", "\u03B5");
		// GREEK SMALL LETTER ZETA ζ
		GLYPHS.put("zeta", "\u03B6");
		// GREEK SMALL LETTER ETA η
		GLYPHS.put("eta", "\u03B7");
		// GREEK SMALL LETTER THETA θ
		GLYPHS.put("theta", "\u03B8");
		// GREEK SMALL LETTER IOTA ι
		GLYPHS.put("iota", "\u03B9");
		// GREEK SMALL LETTER KAPPA κ
		GLYPHS.put("kappa", "\u03BA");
		// GREEK SMALL LETTER LAMDA λ
		GLYPHS.put("lambda", "\u03BB");
		// GREEK SMALL LETTER MU μ
		GLYPHS.put("mu", "\u03BC");
		// GREEK SMALL LETTER NU ν
		GLYPHS.put("nu", "\u03BD");
		// GREEK SMALL LETTER XI ξ
		GLYPHS.put("xi", "\u03BE");
		// GREEK SMALL LETTER OMICRON ο
		GLYPHS.put("omicron", "\u03BF");
		// GREEK SMALL LETTER PI π
		GLYPHS.put("pi", "\u03C0");
		// GREEK SMALL LETTER RHO ρ
		GLYPHS.put("rho", "\u03C1");
		// GREEK SMALL LETTER FINAL SIGMA ς
		GLYPHS.put("uni03C2", "\u03C2");
		// GREEK SMALL LETTER SIGMA σ
		GLYPHS.put("sigma", "\u03C3");
		// GREEK SMALL LETTER TAU τ
		GLYPHS.put("tau", "\u03C4");
		// GREEK SMALL LETTER UPSILON υ
		GLYPHS.put("upsilon", "\u03C5");
		// GREEK SMALL LETTER PHI φ
		GLYPHS.put("phi", "\u03C6");
		// GREEK SMALL LETTER CHI χ
		GLYPHS.put("chi", "\u03C7");
		// GREEK SMALL LETTER PSI ψ
		GLYPHS.put("psi", "\u03C8");
		// GREEK SMALL LETTER OMEGA ω
		GLYPHS.put("omega", "\u03C9");
		// GREEK THETA SYMBOL ϑ
		GLYPHS.put("uni03D1", "\u03D1");
		// GREEK PHI SYMBOL ϕ
		GLYPHS.put("uni03D5", "\u03D5");
		// GREEK PI SYMBOL ϖ
		GLYPHS.put("uni03D6", "\u03D6");
		// GREEK KAPPA SYMBOL ϰ
		GLYPHS.put("uni03F0", "\u03F0");
		// GREEK RHO SYMBOL ϱ
		GLYPHS.put("uni03F1", "\u03F1");
		// GREEK CAPITAL THETA SYMBOL ϴ
		GLYPHS.put("uni03F4", "\u03F4");
		// GREEK LUNATE EPSILON SYMBOL ϵ
		GLYPHS.put("uni03F5", "\u03F5");
		// LATIN CAPITAL LETTER A WITH DOT BELOW Ạ
		GLYPHS.put("Adotbelow", "\u1EA0");
		// LATIN SMALL LETTER A WITH DOT BELOW ạ
		GLYPHS.put("adotbelow", "\u1EA1");
		// LATIN CAPITAL LETTER A WITH HOOK ABOVE Ả
		GLYPHS.put("Ahookabove", "\u1EA2");
		// LATIN SMALL LETTER A WITH HOOK ABOVE ả
		GLYPHS.put("ahookabove", "\u1EA3");
		// LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND ACUTE Ấ
		GLYPHS.put("Acircumflexacute", "\u1EA4");
		// LATIN SMALL LETTER A WITH CIRCUMFLEX AND ACUTE ấ
		GLYPHS.put("acircumflexacute", "\u1EA5");
		// LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND GRAVE Ầ
		GLYPHS.put("Acircumflexgrave", "\u1EA6");
		// LATIN SMALL LETTER A WITH CIRCUMFLEX AND GRAVE ầ
		GLYPHS.put("acircumflexgrave", "\u1EA7");
		// LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND HOOK ABOVE Ẩ
		GLYPHS.put("Acircumflexhookabove", "\u1EA8");
		// LATIN SMALL LETTER A WITH CIRCUMFLEX AND HOOK ABOVE ẩ
		GLYPHS.put("acircumflexhookabove", "\u1EA9");
		// LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND TILDE Ẫ
		GLYPHS.put("Acircumflextilde", "\u1EAA");
		// LATIN SMALL LETTER A WITH CIRCUMFLEX AND TILDE ẫ
		GLYPHS.put("acircumflextilde", "\u1EAB");
		// LATIN CAPITAL LETTER A WITH CIRCUMFLEX AND DOT BELOW Ậ
		GLYPHS.put("Acircumflexdotbelow", "\u1EAC");
		// LATIN SMALL LETTER A WITH CIRCUMFLEX AND DOT BELOW ậ
		GLYPHS.put("acircumflexdotbelow", "\u1EAD");
		// LATIN CAPITAL LETTER A WITH BREVE AND ACUTE Ắ
		GLYPHS.put("Abreveacute", "\u1EAE");
		// LATIN SMALL LETTER A WITH BREVE AND ACUTE ắ
		GLYPHS.put("abreveacute", "\u1EAF");
		// LATIN CAPITAL LETTER A WITH BREVE AND GRAVE Ằ
		GLYPHS.put("Abrevegrave", "\u1EB0");
		// LATIN SMALL LETTER A WITH BREVE AND GRAVE ằ
		GLYPHS.put("abrevegrave", "\u1EB1");
		// LATIN CAPITAL LETTER A WITH BREVE AND HOOK ABOVE Ẳ
		GLYPHS.put("Abrevehookabove", "\u1EB2");
		// LATIN SMALL LETTER A WITH BREVE AND HOOK ABOVE ẳ
		GLYPHS.put("abrevehookabove", "\u1EB3");
		// LATIN CAPITAL LETTER A WITH BREVE AND TILDE Ẵ
		GLYPHS.put("Abrevetilde", "\u1EB4");
		// LATIN SMALL LETTER A WITH BREVE AND TILDE ẵ
		GLYPHS.put("abrevetilde", "\u1EB5");
		// LATIN CAPITAL LETTER A WITH BREVE AND DOT BELOW Ặ
		GLYPHS.put("Abrevedotbelow", "\u1EB6");
		// LATIN SMALL LETTER A WITH BREVE AND DOT BELOW ặ
		GLYPHS.put("abrevedotbelow", "\u1EB7");
		// LATIN CAPITAL LETTER E WITH DOT BELOW Ẹ
		GLYPHS.put("Edotbelow", "\u1EB8");
		// LATIN SMALL LETTER E WITH DOT BELOW ẹ
		GLYPHS.put("edotbelow", "\u1EB9");
		// LATIN CAPITAL LETTER E WITH HOOK ABOVE Ẻ
		GLYPHS.put("Ehookabove", "\u1EBA");
		// LATIN SMALL LETTER E WITH HOOK ABOVE ẻ
		GLYPHS.put("ehookabove", "\u1EBB");
		// LATIN CAPITAL LETTER E WITH TILDE Ẽ
		GLYPHS.put("Etilde", "\u1EBC");
		// LATIN SMALL LETTER E WITH TILDE ẽ
		GLYPHS.put("etilde", "\u1EBD");
		// LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND ACUTE Ế
		GLYPHS.put("Ecircumflexacute", "\u1EBE");
		// LATIN SMALL LETTER E WITH CIRCUMFLEX AND ACUTE ế
		GLYPHS.put("ecircumflexacute", "\u1EBF");
		// LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND GRAVE Ề
		GLYPHS.put("Ecircumflexgrave", "\u1EC0");
		// LATIN SMALL LETTER E WITH CIRCUMFLEX AND GRAVE ề
		GLYPHS.put("ecircumflexgrave", "\u1EC1");
		// LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND HOOK ABOVE Ể
		GLYPHS.put("Ecircumflexhookabove", "\u1EC2");
		// LATIN SMALL LETTER E WITH CIRCUMFLEX AND HOOK ABOVE ể
		GLYPHS.put("ecircumflexhookabove", "\u1EC3");
		// LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND TILDE Ễ
		GLYPHS.put("Ecircumflextilde", "\u1EC4");
		// LATIN SMALL LETTER E WITH CIRCUMFLEX AND TILDE ễ
		GLYPHS.put("ecircumflextilde", "\u1EC5");
		// LATIN CAPITAL LETTER E WITH CIRCUMFLEX AND DOT BELOW Ệ
		GLYPHS.put("Ecircumflexdotbelow", "\u1EC6");
		// LATIN SMALL LETTER E WITH CIRCUMFLEX AND DOT BELOW ệ
		GLYPHS.put("ecircumflexdotbelow", "\u1EC7");
		// LATIN CAPITAL LETTER I WITH HOOK ABOVE Ỉ
		GLYPHS.put("Ihookabove", "\u1EC8");
		// LATIN SMALL LETTER I WITH HOOK ABOVE ỉ
		GLYPHS.put("ihookabove", "\u1EC9");
		// LATIN CAPITAL LETTER I WITH DOT BELOW Ị
		GLYPHS.put("Idotbelow", "\u1ECA");
		// LATIN SMALL LETTER I WITH DOT BELOW ị
		GLYPHS.put("idotbelow", "\u1ECB");
		// LATIN CAPITAL LETTER O WITH DOT BELOW Ọ
		GLYPHS.put("Odotbelow", "\u1ECC");
		// LATIN SMALL LETTER O WITH DOT BELOW ọ
		GLYPHS.put("odotbelow", "\u1ECD");
		// LATIN CAPITAL LETTER O WITH HOOK ABOVE Ỏ
		GLYPHS.put("Ohookabove", "\u1ECE");
		// LATIN SMALL LETTER O WITH HOOK ABOVE ỏ
		GLYPHS.put("ohookabove", "\u1ECF");
		// LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND ACUTE Ố
		GLYPHS.put("Ocircumflexacute", "\u1ED0");
		// LATIN SMALL LETTER O WITH CIRCUMFLEX AND ACUTE ố
		GLYPHS.put("ocircumflexacute", "\u1ED1");
		// LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND GRAVE Ồ
		GLYPHS.put("Ocircumflexgrave", "\u1ED2");
		// LATIN SMALL LETTER O WITH CIRCUMFLEX AND GRAVE ồ
		GLYPHS.put("ocircumflexgrave", "\u1ED3");
		// LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND HOOK ABOVE Ổ
		GLYPHS.put("Ocircumflexhookabove", "\u1ED4");
		// LATIN SMALL LETTER O WITH CIRCUMFLEX AND HOOK ABOVE ổ
		GLYPHS.put("ocircumflexhookabove", "\u1ED5");
		// LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND TILDE Ỗ
		GLYPHS.put("Ocircumflextilde", "\u1ED6");
		// LATIN SMALL LETTER O WITH CIRCUMFLEX AND TILDE ỗ
		GLYPHS.put("ocircumflextilde", "\u1ED7");
		// LATIN CAPITAL LETTER O WITH CIRCUMFLEX AND DOT BELOW Ộ
		GLYPHS.put("Ocircumflexdotbelow", "\u1ED8");
		// LATIN SMALL LETTER O WITH CIRCUMFLEX AND DOT BELOW ộ
		GLYPHS.put("ocircumflexdotbelow", "\u1ED9");
		// LATIN CAPITAL LETTER O WITH HORN AND ACUTE Ớ
		GLYPHS.put("Ohornacute", "\u1EDA");
		// LATIN SMALL LETTER O WITH HORN AND ACUTE ớ
		GLYPHS.put("ohornacute", "\u1EDB");
		// LATIN CAPITAL LETTER O WITH HORN AND GRAVE Ờ
		GLYPHS.put("Ohorngrave", "\u1EDC");
		// LATIN SMALL LETTER O WITH HORN AND GRAVE ờ
		GLYPHS.put("ohorngrave", "\u1EDD");
		// LATIN CAPITAL LETTER O WITH HORN AND HOOK ABOVE Ở
		GLYPHS.put("Ohornhookabove", "\u1EDE");
		// LATIN SMALL LETTER O WITH HORN AND HOOK ABOVE ở
		GLYPHS.put("ohornhookabove", "\u1EDF");
		// LATIN CAPITAL LETTER O WITH HORN AND TILDE Ỡ
		GLYPHS.put("Ohorntilde", "\u1EE0");
		// LATIN SMALL LETTER O WITH HORN AND TILDE ỡ
		GLYPHS.put("ohorntilde", "\u1EE1");
		// LATIN CAPITAL LETTER O WITH HORN AND DOT BELOW Ợ
		GLYPHS.put("Ohorndotbelow", "\u1EE2");
		// LATIN SMALL LETTER O WITH HORN AND DOT BELOW ợ
		GLYPHS.put("ohorndotbelow", "\u1EE3");
		// LATIN CAPITAL LETTER U WITH DOT BELOW Ụ
		GLYPHS.put("Udotbelow", "\u1EE4");
		// LATIN SMALL LETTER U WITH DOT BELOW ụ
		GLYPHS.put("udotbelow", "\u1EE5");
		// LATIN CAPITAL LETTER U WITH HOOK ABOVE Ủ
		GLYPHS.put("Uhookabove", "\u1EE6");
		// LATIN SMALL LETTER U WITH HOOK ABOVE ủ
		GLYPHS.put("uhookabove", "\u1EE7");
		// LATIN CAPITAL LETTER U WITH HORN AND ACUTE Ứ
		GLYPHS.put("Uhornacute", "\u1EE8");
		// LATIN SMALL LETTER U WITH HORN AND ACUTE ứ
		GLYPHS.put("uhornacute", "\u1EE9");
		// LATIN CAPITAL LETTER U WITH HORN AND GRAVE Ừ
		GLYPHS.put("Uhorngrave", "\u1EEA");
		// LATIN SMALL LETTER U WITH HORN AND GRAVE ừ
		GLYPHS.put("uhorngrave", "\u1EEB");
		// LATIN CAPITAL LETTER U WITH HORN AND HOOK ABOVE Ử
		GLYPHS.put("Uhornhookabove", "\u1EEC");
		// LATIN SMALL LETTER U WITH HORN AND HOOK ABOVE ử
		GLYPHS.put("uhornhookabove", "\u1EED");
		// LATIN CAPITAL LETTER U WITH HORN AND TILDE Ữ
		GLYPHS.put("Uhorntilde", "\u1EEE");
		// LATIN SMALL LETTER U WITH HORN AND TILDE ữ
		GLYPHS.put("uhorntilde", "\u1EEF");
		// LATIN CAPITAL LETTER U WITH HORN AND DOT BELOW Ự
		GLYPHS.put("Uhorndotbelow", "\u1EF0");
		// LATIN SMALL LETTER U WITH HORN AND DOT BELOW ự
		GLYPHS.put("uhorndotbelow", "\u1EF1");
		// LATIN CAPITAL LETTER Y WITH GRAVE Ỳ
		GLYPHS.put("Ygrave", "\u1EF2");
		// LATIN SMALL LETTER Y WITH GRAVE ỳ
		GLYPHS.put("ygrave", "\u1EF3");
		// LATIN CAPITAL LETTER Y WITH DOT BELOW Ỵ
		GLYPHS.put("Ydotbelow", "\u1EF4");
		// LATIN SMALL LETTER Y WITH DOT BELOW ỵ
		GLYPHS.put("ydotbelow", "\u1EF5");
		// LATIN CAPITAL LETTER Y WITH HOOK ABOVE Ỷ
		GLYPHS.put("Yhookabove", "\u1EF6");
		// LATIN SMALL LETTER Y WITH HOOK ABOVE ỷ
		GLYPHS.put("yhookabove", "\u1EF7");
		// LATIN CAPITAL LETTER Y WITH TILDE Ỹ
		GLYPHS.put("Ytilde", "\u1EF8");
		// LATIN SMALL LETTER Y WITH TILDE ỹ
		GLYPHS.put("ytilde", "\u1EF9");
		// EN QUAD  
		GLYPHS.put("uni2000", "\u2000");
		// EM QUAD  
		GLYPHS.put("uni2001", "\u2001");
		// EN SPACE  
		GLYPHS.put("uni2002", "\u2002");
		// EM SPACE  
		GLYPHS.put("uni2003", "\u2003");
		// THREE-PER-EM SPACE  
		GLYPHS.put("uni2004", "\u2004");
		// FOUR-PER-EM SPACE  
		GLYPHS.put("uni2005", "\u2005");
		// SIX-PER-EM SPACE  
		GLYPHS.put("uni2006", "\u2006");
		// FIGURE SPACE  
		GLYPHS.put("uni2007", "\u2007");
		// PUNCTUATION SPACE  
		GLYPHS.put("uni2008", "\u2008");
		// THIN SPACE  
		GLYPHS.put("uni2009", "\u2009");
		// HAIR SPACE  
		GLYPHS.put("uni200A", "\u200A");
		// ZERO WIDTH SPACE ​
		GLYPHS.put("uni200B", "\u200B");
		// ZERO WIDTH NON-JOINER ‌
		GLYPHS.put("uni200C", "\u200C");
		// ZERO WIDTH JOINER ‍
		GLYPHS.put("uni200D", "\u200D");
		// HYPHEN ‐
		GLYPHS.put("hyphentwo", "\u2010");
		// FIGURE DASH ‒
		GLYPHS.put("figuredash", "\u2012");
		// EN DASH –
		GLYPHS.put("endash", "\u2013");
		// EM DASH —
		GLYPHS.put("emdash", "\u2014");
		// HORIZONTAL BAR ―
		GLYPHS.put("uni2015", "\u2015");
		// DOUBLE VERTICAL LINE ‖
		GLYPHS.put("dblverticalbar", "\u2016");
		// DOUBLE LOW LINE ‗
		GLYPHS.put("uni2017", "\u2017");
		// LEFT SINGLE QUOTATION MARK ‘
		GLYPHS.put("quoteleft", "\u2018");
		// RIGHT SINGLE QUOTATION MARK ’
		GLYPHS.put("quoteright", "\u2019");
		// SINGLE LOW-9 QUOTATION MARK ‚
		GLYPHS.put("quotesinglbase", "\u201A");
		// LEFT DOUBLE QUOTATION MARK “
		GLYPHS.put("quotedblleft", "\u201C");
		// RIGHT DOUBLE QUOTATION MARK ”
		GLYPHS.put("quotedblright", "\u201D");
		// DOUBLE LOW-9 QUOTATION MARK „
		GLYPHS.put("quotedblbase", "\u201E");
		// DAGGER †
		GLYPHS.put("dagger", "\u2020");
		// DOUBLE DAGGER ‡
		GLYPHS.put("daggerdbl", "\u2021");
		// BULLET •
		GLYPHS.put("bullet", "\u2022");
		// HORIZONTAL ELLIPSIS …
		GLYPHS.put("ellipsis", "\u2026");
		// NARROW NO-BREAK SPACE  
		GLYPHS.put("uni202F", "\u202F");
		// PER MILLE SIGN ‰
		GLYPHS.put("perthousand", "\u2030");
		// PER TEN THOUSAND SIGN ‱
		GLYPHS.put("permyriad", "\u2031");
		// PRIME ′
		GLYPHS.put("minute", "\u2032");
		// DOUBLE PRIME ″
		GLYPHS.put("uni2033", "\u2033");
		// TRIPLE PRIME ‴
		GLYPHS.put("uni2034", "\u2034");
		// REVERSED PRIME ‵
		GLYPHS.put("primereversed", "\u2035");
		// REVERSED DOUBLE PRIME ‶
		GLYPHS.put("uni2036", "\u2036");
		// REVERSED TRIPLE PRIME ‷
		GLYPHS.put("uni2037", "\u2037");
		// SINGLE LEFT-POINTING ANGLE QUOTATION MARK ‹
		GLYPHS.put("guilsinglleft", "\u2039");
		// SINGLE RIGHT-POINTING ANGLE QUOTATION MARK ›
		GLYPHS.put("guilsinglright", "\u203A");
		// REFERENCE MARK ※
		GLYPHS.put("referencemark", "\u203B");
		// INTERROBANG ‽
		GLYPHS.put("uni203D", "\u203D");
		// FRACTION SLASH ⁄
		GLYPHS.put("fraction", "\u2044");
		// COMMERCIAL MINUS SIGN ⁒
		GLYPHS.put("discount", "\u2052");
		// QUADRUPLE PRIME ⁗
		GLYPHS.put("uni2057", "\u2057");
		// MEDIUM MATHEMATICAL SPACE  
		GLYPHS.put("uni205F", "\u205F");
		// WORD JOINER ⁠
		GLYPHS.put("uni2060", "\u2060");
		// FUNCTION APPLICATION ⁡
		GLYPHS.put("uni2061", "\u2061");
		// INVISIBLE TIMES ⁢
		GLYPHS.put("uni2062", "\u2062");
		// INVISIBLE SEPARATOR ⁣
		GLYPHS.put("uni2063", "\u2063");
		// INVISIBLE PLUS ⁤
		GLYPHS.put("uni2064", "\u2064");
		// COLON SIGN ₡
		GLYPHS.put("colonmonetary", "\u20A1");
		// EURO SIGN €
		GLYPHS.put("Euro", "\u20AC");
		// COMBINING LEFT HARPOON ABOVE ⃐
		GLYPHS.put("uni20D0", "\u20D0");
		// COMBINING RIGHT HARPOON ABOVE ⃑
		GLYPHS.put("uni20D1", "\u20D1");
		// COMBINING LONG VERTICAL LINE OVERLAY ⃒
		GLYPHS.put("uni20D2", "\u20D2");
		// COMBINING SHORT VERTICAL LINE OVERLAY ⃓
		GLYPHS.put("uni20D3", "\u20D3");
		// COMBINING ANTICLOCKWISE ARROW ABOVE ⃔
		GLYPHS.put("uni20D4", "\u20D4");
		// COMBINING CLOCKWISE ARROW ABOVE ⃕
		GLYPHS.put("uni20D5", "\u20D5");
		// COMBINING LEFT ARROW ABOVE ⃖
		GLYPHS.put("uni20D6", "\u20D6");
		// COMBINING RIGHT ARROW ABOVE ⃗
		GLYPHS.put("uni20D7", "\u20D7");
		// COMBINING RING OVERLAY ⃘
		GLYPHS.put("uni20D8", "\u20D8");
		// COMBINING THREE DOTS ABOVE ⃛
		GLYPHS.put("uni20DB", "\u20DB");
		// COMBINING FOUR DOTS ABOVE ⃜
		GLYPHS.put("uni20DC", "\u20DC");
		// COMBINING ENCLOSING CIRCLE ⃝
		GLYPHS.put("uni20DD", "\u20DD");
		// COMBINING ENCLOSING SQUARE ⃞
		GLYPHS.put("uni20DE", "\u20DE");
		// COMBINING ENCLOSING DIAMOND ⃟
		GLYPHS.put("uni20DF", "\u20DF");
		// COMBINING LEFT RIGHT ARROW ABOVE ⃡
		GLYPHS.put("uni20E1", "\u20E1");
		// COMBINING ENCLOSING UPWARD POINTING TRIANGLE ⃤
		GLYPHS.put("uni20E4", "\u20E4");
		// COMBINING REVERSE SOLIDUS OVERLAY ⃥
		GLYPHS.put("uni20E5", "\u20E5");
		// COMBINING DOUBLE VERTICAL STROKE OVERLAY ⃦
		GLYPHS.put("uni20E6", "\u20E6");
		// COMBINING TRIPLE UNDERDOT ⃨
		GLYPHS.put("uni20E8", "\u20E8");
		// COMBINING WIDE BRIDGE ABOVE ⃩
		GLYPHS.put("uni20E9", "\u20E9");
		// COMBINING LEFTWARDS ARROW OVERLAY ⃪
		GLYPHS.put("uni20EA", "\u20EA");
		// COMBINING LONG DOUBLE SOLIDUS OVERLAY ⃫
		GLYPHS.put("uni20EB", "\u20EB");
		// COMBINING RIGHTWARDS HARPOON WITH BARB DOWNWARDS ⃬
		GLYPHS.put("uni20EC", "\u20EC");
		// COMBINING LEFTWARDS HARPOON WITH BARB DOWNWARDS ⃭
		GLYPHS.put("uni20ED", "\u20ED");
		// COMBINING LEFT ARROW BELOW ⃮
		GLYPHS.put("uni20EE", "\u20EE");
		// COMBINING RIGHT ARROW BELOW ⃯
		GLYPHS.put("uni20EF", "\u20EF");
		// COMBINING ASTERISK ABOVE ⃰
		GLYPHS.put("uni20F0", "\u20F0");
		// DOUBLE-STRUCK CAPITAL C ℂ
		GLYPHS.put("uni2102", "\u2102");
		// DEGREE CELSIUS ℃
		GLYPHS.put("centigrade", "\u2103");
		// EULER CONSTANT ℇ
		GLYPHS.put("uni2107", "\u2107");
		// DEGREE FAHRENHEIT ℉
		GLYPHS.put("fahrenheit", "\u2109");
		// SCRIPT CAPITAL H ℋ
		GLYPHS.put("uni210B", "\u210B");
		// BLACK-LETTER CAPITAL H ℌ
		GLYPHS.put("uni210C", "\u210C");
		// DOUBLE-STRUCK CAPITAL H ℍ
		GLYPHS.put("uni210D", "\u210D");
		// PLANCK CONSTANT ℎ
		GLYPHS.put("uni210E", "\u210E");
		// PLANCK CONSTANT OVER TWO PI ℏ
		GLYPHS.put("uni210F", "\u210F");
		// SCRIPT CAPITAL I ℐ
		GLYPHS.put("uni2110", "\u2110");
		// BLACK-LETTER CAPITAL I ℑ
		GLYPHS.put("uni2111", "\u2111");
		// SCRIPT CAPITAL L ℒ
		GLYPHS.put("uni2112", "\u2112");
		// SCRIPT SMALL L ℓ
		GLYPHS.put("uni2113", "\u2113");
		// DOUBLE-STRUCK CAPITAL N ℕ
		GLYPHS.put("uni2115", "\u2115");
		// NUMERO SIGN №
		GLYPHS.put("numero", "\u2116");
		// SOUND RECORDING COPYRIGHT ℗
		GLYPHS.put("published", "\u2117");
		// SCRIPT CAPITAL P ℘
		GLYPHS.put("weierstrass", "\u2118");
		// DOUBLE-STRUCK CAPITAL P ℙ
		GLYPHS.put("uni2119", "\u2119");
		// DOUBLE-STRUCK CAPITAL Q ℚ
		GLYPHS.put("uni211A", "\u211A");
		// SCRIPT CAPITAL R ℛ
		GLYPHS.put("uni211B", "\u211B");
		// BLACK-LETTER CAPITAL R ℜ
		GLYPHS.put("uni211C", "\u211C");
		// DOUBLE-STRUCK CAPITAL R ℝ
		GLYPHS.put("uni211D", "\u211D");
		// PRESCRIPTION TAKE ℞
		GLYPHS.put("recipe", "\u211E");
		// SERVICE MARK ℠
		GLYPHS.put("servicemark", "\u2120");
		// TRADE MARK SIGN ™
		GLYPHS.put("trademark", "\u2122");
		// DOUBLE-STRUCK CAPITAL Z ℤ
		GLYPHS.put("uni2124", "\u2124");
		// OHM SIGN Ω
		GLYPHS.put("uni2126", "\u2126");
		// INVERTED OHM SIGN ℧
		GLYPHS.put("uni2127", "\u2127");
		// BLACK-LETTER CAPITAL Z ℨ
		GLYPHS.put("uni2128", "\u2128");
		// KELVIN SIGN K
		GLYPHS.put("uni212A", "\u212A");
		// ANGSTROM SIGN Å
		GLYPHS.put("uni212B", "\u212B");
		// SCRIPT CAPITAL B ℬ
		GLYPHS.put("uni212C", "\u212C");
		// BLACK-LETTER CAPITAL C ℭ
		GLYPHS.put("uni212D", "\u212D");
		// ESTIMATED SYMBOL ℮
		GLYPHS.put("estimated", "\u212E");
		// SCRIPT CAPITAL E ℰ
		GLYPHS.put("uni2130", "\u2130");
		// SCRIPT CAPITAL F ℱ
		GLYPHS.put("uni2131", "\u2131");
		// SCRIPT CAPITAL M ℳ
		GLYPHS.put("uni2133", "\u2133");
		// ALEF SYMBOL ℵ
		GLYPHS.put("aleph", "\u2135");
		// BET SYMBOL ℶ
		GLYPHS.put("uni2136", "\u2136");
		// GIMEL SYMBOL ℷ
		GLYPHS.put("uni2137", "\u2137");
		// DALET SYMBOL ℸ
		GLYPHS.put("uni2138", "\u2138");
		// DOUBLE-STRUCK SMALL PI ℼ
		GLYPHS.put("uni213C", "\u213C");
		// DOUBLE-STRUCK SMALL GAMMA ℽ
		GLYPHS.put("uni213D", "\u213D");
		// DOUBLE-STRUCK CAPITAL GAMMA ℾ
		GLYPHS.put("uni213E", "\u213E");
		// DOUBLE-STRUCK CAPITAL PI ℿ
		GLYPHS.put("uni213F", "\u213F");
		// DOUBLE-STRUCK N-ARY SUMMATION ⅀
		GLYPHS.put("uni2140", "\u2140");
		// DOUBLE-STRUCK ITALIC CAPITAL D ⅅ
		GLYPHS.put("uni2145", "\u2145");
		// DOUBLE-STRUCK ITALIC SMALL D ⅆ
		GLYPHS.put("uni2146", "\u2146");
		// DOUBLE-STRUCK ITALIC SMALL E ⅇ
		GLYPHS.put("uni2147", "\u2147");
		// DOUBLE-STRUCK ITALIC SMALL I ⅈ
		GLYPHS.put("uni2148", "\u2148");
		// DOUBLE-STRUCK ITALIC SMALL J ⅉ
		GLYPHS.put("uni2149", "\u2149");
		// LEFTWARDS ARROW ←
		GLYPHS.put("arrowleft", "\u2190");
		// UPWARDS ARROW ↑
		GLYPHS.put("arrowup", "\u2191");
		// RIGHTWARDS ARROW →
		GLYPHS.put("arrowright", "\u2192");
		// DOWNWARDS ARROW ↓
		GLYPHS.put("arrowdown", "\u2193");
		// LEFT RIGHT ARROW ↔
		GLYPHS.put("arrowboth", "\u2194");
		// UP DOWN ARROW ↕
		GLYPHS.put("arrowupdn", "\u2195");
		// NORTH WEST ARROW ↖
		GLYPHS.put("uni2196", "\u2196");
		// NORTH EAST ARROW ↗
		GLYPHS.put("uni2197", "\u2197");
		// SOUTH EAST ARROW ↘
		GLYPHS.put("uni2198", "\u2198");
		// SOUTH WEST ARROW ↙
		GLYPHS.put("uni2199", "\u2199");
		// LEFTWARDS ARROW WITH STROKE ↚
		GLYPHS.put("uni219A", "\u219A");
		// RIGHTWARDS ARROW WITH STROKE ↛
		GLYPHS.put("uni219B", "\u219B");
		// LEFTWARDS TWO HEADED ARROW ↞
		GLYPHS.put("uni219E", "\u219E");
		// UPWARDS TWO HEADED ARROW ↟
		GLYPHS.put("uni219F", "\u219F");
		// RIGHTWARDS TWO HEADED ARROW ↠
		GLYPHS.put("uni21A0", "\u21A0");
		// DOWNWARDS TWO HEADED ARROW ↡
		GLYPHS.put("uni21A1", "\u21A1");
		// LEFTWARDS ARROW WITH TAIL ↢
		GLYPHS.put("uni21A2", "\u21A2");
		// RIGHTWARDS ARROW WITH TAIL ↣
		GLYPHS.put("uni21A3", "\u21A3");
		// LEFTWARDS ARROW FROM BAR ↤
		GLYPHS.put("uni21A4", "\u21A4");
		// UPWARDS ARROW FROM BAR ↥
		GLYPHS.put("uni21A5", "\u21A5");
		// RIGHTWARDS ARROW FROM BAR ↦
		GLYPHS.put("uni21A6", "\u21A6");
		// DOWNWARDS ARROW FROM BAR ↧
		GLYPHS.put("uni21A7", "\u21A7");
		// LEFTWARDS ARROW WITH HOOK ↩
		GLYPHS.put("uni21A9", "\u21A9");
		// RIGHTWARDS ARROW WITH HOOK ↪
		GLYPHS.put("uni21AA", "\u21AA");
		// LEFTWARDS ARROW WITH LOOP ↫
		GLYPHS.put("uni21AB", "\u21AB");
		// RIGHTWARDS ARROW WITH LOOP ↬
		GLYPHS.put("uni21AC", "\u21AC");
		// LEFT RIGHT WAVE ARROW ↭
		GLYPHS.put("uni21AD", "\u21AD");
		// LEFT RIGHT ARROW WITH STROKE ↮
		GLYPHS.put("uni21AE", "\u21AE");
		// UPWARDS ARROW WITH TIP LEFTWARDS ↰
		GLYPHS.put("uni21B0", "\u21B0");
		// UPWARDS ARROW WITH TIP RIGHTWARDS ↱
		GLYPHS.put("uni21B1", "\u21B1");
		// DOWNWARDS ARROW WITH TIP LEFTWARDS ↲
		GLYPHS.put("uni21B2", "\u21B2");
		// DOWNWARDS ARROW WITH TIP RIGHTWARDS ↳
		GLYPHS.put("uni21B3", "\u21B3");
		// RIGHTWARDS ARROW WITH CORNER DOWNWARDS ↴
		GLYPHS.put("uni21B4", "\u21B4");
		// DOWNWARDS ARROW WITH CORNER LEFTWARDS ↵
		GLYPHS.put("carriagereturn", "\u21B5");
		// ANTICLOCKWISE TOP SEMICIRCLE ARROW ↶
		GLYPHS.put("uni21B6", "\u21B6");
		// CLOCKWISE TOP SEMICIRCLE ARROW ↷
		GLYPHS.put("uni21B7", "\u21B7");
		// ANTICLOCKWISE OPEN CIRCLE ARROW ↺
		GLYPHS.put("uni21BA", "\u21BA");
		// CLOCKWISE OPEN CIRCLE ARROW ↻
		GLYPHS.put("uni21BB", "\u21BB");
		// LEFTWARDS HARPOON WITH BARB UPWARDS ↼
		GLYPHS.put("uni21BC", "\u21BC");
		// LEFTWARDS HARPOON WITH BARB DOWNWARDS ↽
		GLYPHS.put("uni21BD", "\u21BD");
		// UPWARDS HARPOON WITH BARB RIGHTWARDS ↾
		GLYPHS.put("uni21BE", "\u21BE");
		// UPWARDS HARPOON WITH BARB LEFTWARDS ↿
		GLYPHS.put("uni21BF", "\u21BF");
		// RIGHTWARDS HARPOON WITH BARB UPWARDS ⇀
		GLYPHS.put("uni21C0", "\u21C0");
		// RIGHTWARDS HARPOON WITH BARB DOWNWARDS ⇁
		GLYPHS.put("uni21C1", "\u21C1");
		// DOWNWARDS HARPOON WITH BARB RIGHTWARDS ⇂
		GLYPHS.put("uni21C2", "\u21C2");
		// DOWNWARDS HARPOON WITH BARB LEFTWARDS ⇃
		GLYPHS.put("uni21C3", "\u21C3");
		// RIGHTWARDS ARROW OVER LEFTWARDS ARROW ⇄
		GLYPHS.put("uni21C4", "\u21C4");
		// UPWARDS ARROW LEFTWARDS OF DOWNWARDS ARROW ⇅
		GLYPHS.put("uni21C5", "\u21C5");
		// LEFTWARDS ARROW OVER RIGHTWARDS ARROW ⇆
		GLYPHS.put("uni21C6", "\u21C6");
		// LEFTWARDS PAIRED ARROWS ⇇
		GLYPHS.put("uni21C7", "\u21C7");
		// UPWARDS PAIRED ARROWS ⇈
		GLYPHS.put("uni21C8", "\u21C8");
		// RIGHTWARDS PAIRED ARROWS ⇉
		GLYPHS.put("uni21C9", "\u21C9");
		// DOWNWARDS PAIRED ARROWS ⇊
		GLYPHS.put("uni21CA", "\u21CA");
		// LEFTWARDS HARPOON OVER RIGHTWARDS HARPOON ⇋
		GLYPHS.put("uni21CB", "\u21CB");
		// RIGHTWARDS HARPOON OVER LEFTWARDS HARPOON ⇌
		GLYPHS.put("uni21CC", "\u21CC");
		// LEFTWARDS DOUBLE ARROW WITH STROKE ⇍
		GLYPHS.put("uni21CD", "\u21CD");
		// LEFT RIGHT DOUBLE ARROW WITH STROKE ⇎
		GLYPHS.put("uni21CE", "\u21CE");
		// RIGHTWARDS DOUBLE ARROW WITH STROKE ⇏
		GLYPHS.put("uni21CF", "\u21CF");
		// LEFTWARDS DOUBLE ARROW ⇐
		GLYPHS.put("arrowdblleft", "\u21D0");
		// UPWARDS DOUBLE ARROW ⇑
		GLYPHS.put("arrowdblup", "\u21D1");
		// RIGHTWARDS DOUBLE ARROW ⇒
		GLYPHS.put("arrowdblright", "\u21D2");
		// DOWNWARDS DOUBLE ARROW ⇓
		GLYPHS.put("arrowdbldown", "\u21D3");
		// LEFT RIGHT DOUBLE ARROW ⇔
		GLYPHS.put("arrowdblboth", "\u21D4");
		// UP DOWN DOUBLE ARROW ⇕
		GLYPHS.put("uni21D5", "\u21D5");
		// NORTH WEST DOUBLE ARROW ⇖
		GLYPHS.put("uni21D6", "\u21D6");
		// NORTH EAST DOUBLE ARROW ⇗
		GLYPHS.put("uni21D7", "\u21D7");
		// SOUTH EAST DOUBLE ARROW ⇘
		GLYPHS.put("uni21D8", "\u21D8");
		// SOUTH WEST DOUBLE ARROW ⇙
		GLYPHS.put("uni21D9", "\u21D9");
		// LEFTWARDS TRIPLE ARROW ⇚
		GLYPHS.put("uni21DA", "\u21DA");
		// RIGHTWARDS TRIPLE ARROW ⇛
		GLYPHS.put("uni21DB", "\u21DB");
		// LEFTWARDS SQUIGGLE ARROW ⇜
		GLYPHS.put("uni21DC", "\u21DC");
		// RIGHTWARDS SQUIGGLE ARROW ⇝
		GLYPHS.put("uni21DD", "\u21DD");
		// LEFTWARDS WHITE ARROW ⇦
		GLYPHS.put("uni21E6", "\u21E6");
		// UPWARDS WHITE ARROW ⇧
		GLYPHS.put("uni21E7", "\u21E7");
		// RIGHTWARDS WHITE ARROW ⇨
		GLYPHS.put("uni21E8", "\u21E8");
		// DOWNWARDS WHITE ARROW ⇩
		GLYPHS.put("uni21E9", "\u21E9");
		// UP DOWN WHITE ARROW ⇳
		GLYPHS.put("uni21F3", "\u21F3");
		// DOWNWARDS ARROW LEFTWARDS OF UPWARDS ARROW ⇵
		GLYPHS.put("uni21F5", "\u21F5");
		// THREE RIGHTWARDS ARROWS ⇶
		GLYPHS.put("uni21F6", "\u21F6");
		// FOR ALL ∀
		GLYPHS.put("universal", "\u2200");
		// COMPLEMENT ∁
		GLYPHS.put("uni2201", "\u2201");
		// PARTIAL DIFFERENTIAL ∂
		GLYPHS.put("partialdiff", "\u2202");
		// THERE EXISTS ∃
		GLYPHS.put("existential", "\u2203");
		// THERE DOES NOT EXIST ∄
		GLYPHS.put("uni2204", "\u2204");
		// EMPTY SET ∅
		GLYPHS.put("emptyset", "\u2205");
		// INCREMENT ∆
		GLYPHS.put("uni2206", "\u2206");
		// NABLA ∇
		GLYPHS.put("nabla", "\u2207");
		// ELEMENT OF ∈
		GLYPHS.put("element", "\u2208");
		// NOT AN ELEMENT OF ∉
		GLYPHS.put("uni2209", "\u2209");
		// SMALL ELEMENT OF ∊
		GLYPHS.put("uni220A", "\u220A");
		// CONTAINS AS MEMBER ∋
		GLYPHS.put("suchthat", "\u220B");
		// DOES NOT CONTAIN AS MEMBER ∌
		GLYPHS.put("uni220C", "\u220C");
		// SMALL CONTAINS AS MEMBER ∍
		GLYPHS.put("uni220D", "\u220D");
		// END OF PROOF ∎
		GLYPHS.put("uni220E", "\u220E");
		// N-ARY PRODUCT ∏
		GLYPHS.put("product", "\u220F");
		// N-ARY COPRODUCT ∐
		GLYPHS.put("uni2210", "\u2210");
		// N-ARY SUMMATION ∑
		GLYPHS.put("summation", "\u2211");
		// MINUS SIGN −
		GLYPHS.put("minus", "\u2212");
		// MINUS-OR-PLUS SIGN ∓
		GLYPHS.put("minusplus", "\u2213");
		// DOT PLUS ∔
		GLYPHS.put("uni2214", "\u2214");
		// DIVISION SLASH ∕
		GLYPHS.put("uni2215", "\u2215");
		// SET MINUS ∖
		GLYPHS.put("uni2216", "\u2216");
		// ASTERISK OPERATOR ∗
		GLYPHS.put("asteriskmath", "\u2217");
		// RING OPERATOR ∘
		GLYPHS.put("uni2218", "\u2218");
		// BULLET OPERATOR ∙
		GLYPHS.put("bulletoperator", "\u2219");
		// SQUARE ROOT √
		GLYPHS.put("radical", "\u221A");
		// PROPORTIONAL TO ∝
		GLYPHS.put("proportional", "\u221D");
		// INFINITY ∞
		GLYPHS.put("infinity", "\u221E");
		// RIGHT ANGLE ∟
		GLYPHS.put("uni221F", "\u221F");
		// ANGLE ∠
		GLYPHS.put("uni2220", "\u2220");
		// MEASURED ANGLE ∡
		GLYPHS.put("uni2221", "\u2221");
		// SPHERICAL ANGLE ∢
		GLYPHS.put("uni2222", "\u2222");
		// DIVIDES ∣
		GLYPHS.put("divides", "\u2223");
		// DOES NOT DIVIDE ∤
		GLYPHS.put("uni2224", "\u2224");
		// PARALLEL TO ∥
		GLYPHS.put("parallel", "\u2225");
		// NOT PARALLEL TO ∦
		GLYPHS.put("uni2226", "\u2226");
		// LOGICAL AND ∧
		GLYPHS.put("logicaland", "\u2227");
		// LOGICAL OR ∨
		GLYPHS.put("logicalor", "\u2228");
		// INTERSECTION ∩
		GLYPHS.put("intersection", "\u2229");
		// UNION ∪
		GLYPHS.put("union", "\u222A");
		// INTEGRAL ∫
		GLYPHS.put("integral", "\u222B");
		// DOUBLE INTEGRAL ∬
		GLYPHS.put("uni222C", "\u222C");
		// TRIPLE INTEGRAL ∭
		GLYPHS.put("uni222D", "\u222D");
		// CONTOUR INTEGRAL ∮
		GLYPHS.put("contourintegral", "\u222E");
		// SURFACE INTEGRAL ∯
		GLYPHS.put("uni222F", "\u222F");
		// VOLUME INTEGRAL ∰
		GLYPHS.put("uni2230", "\u2230");
		// CLOCKWISE INTEGRAL ∱
		GLYPHS.put("uni2231", "\u2231");
		// CLOCKWISE CONTOUR INTEGRAL ∲
		GLYPHS.put("uni2232", "\u2232");
		// ANTICLOCKWISE CONTOUR INTEGRAL ∳
		GLYPHS.put("uni2233", "\u2233");
		// THEREFORE ∴
		GLYPHS.put("therefore", "\u2234");
		// BECAUSE ∵
		GLYPHS.put("because", "\u2235");
		// RATIO ∶
		GLYPHS.put("ratio", "\u2236");
		// PROPORTION ∷
		GLYPHS.put("proportion", "\u2237");
		// DOT MINUS ∸
		GLYPHS.put("uni2238", "\u2238");
		// EXCESS ∹
		GLYPHS.put("uni2239", "\u2239");
		// GEOMETRIC PROPORTION ∺
		GLYPHS.put("uni223A", "\u223A");
		// HOMOTHETIC ∻
		GLYPHS.put("uni223B", "\u223B");
		// TILDE OPERATOR ∼
		GLYPHS.put("similar", "\u223C");
		// REVERSED TILDE ∽
		GLYPHS.put("uni223D", "\u223D");
		// INVERTED LAZY S ∾
		GLYPHS.put("uni223E", "\u223E");
		// SINE WAVE ∿
		GLYPHS.put("uni223F", "\u223F");
		// WREATH PRODUCT ≀
		GLYPHS.put("uni2240", "\u2240");
		// NOT TILDE ≁
		GLYPHS.put("uni2241", "\u2241");
		// MINUS TILDE ≂
		GLYPHS.put("uni2242", "\u2242");
		// ASYMPTOTICALLY EQUAL TO ≃
		GLYPHS.put("similar_equal", "\u2243");
		// NOT ASYMPTOTICALLY EQUAL TO ≄
		GLYPHS.put("uni2244", "\u2244");
		// APPROXIMATELY EQUAL TO ≅
		GLYPHS.put("uni2245", "\u2245");
		// APPROXIMATELY BUT NOT ACTUALLY EQUAL TO ≆
		GLYPHS.put("uni2246", "\u2246");
		// NEITHER APPROXIMATELY NOR ACTUALLY EQUAL TO ≇
		GLYPHS.put("uni2247", "\u2247");
		// ALMOST EQUAL TO ≈
		GLYPHS.put("approxequal", "\u2248");
		// NOT ALMOST EQUAL TO ≉
		GLYPHS.put("uni2249", "\u2249");
		// ALMOST EQUAL OR EQUAL TO ≊
		GLYPHS.put("uni224A", "\u224A");
		// TRIPLE TILDE ≋
		GLYPHS.put("uni224B", "\u224B");
		// ALL EQUAL TO ≌
		GLYPHS.put("uni224C", "\u224C");
		// EQUIVALENT TO ≍
		GLYPHS.put("uni224D", "\u224D");
		// GEOMETRICALLY EQUIVALENT TO ≎
		GLYPHS.put("uni224E", "\u224E");
		// DIFFERENCE BETWEEN ≏
		GLYPHS.put("uni224F", "\u224F");
		// APPROACHES THE LIMIT ≐
		GLYPHS.put("uni2250", "\u2250");
		// GEOMETRICALLY EQUAL TO ≑
		GLYPHS.put("uni2251", "\u2251");
		// APPROXIMATELY EQUAL TO OR THE IMAGE OF ≒
		GLYPHS.put("uni2252", "\u2252");
		// IMAGE OF OR APPROXIMATELY EQUAL TO ≓
		GLYPHS.put("uni2253", "\u2253");
		// COLON EQUALS ≔
		GLYPHS.put("uni2254", "\u2254");
		// EQUALS COLON ≕
		GLYPHS.put("uni2255", "\u2255");
		// RING IN EQUAL TO ≖
		GLYPHS.put("uni2256", "\u2256");
		// RING EQUAL TO ≗
		GLYPHS.put("uni2257", "\u2257");
		// CORRESPONDS TO ≘
		GLYPHS.put("uni2258", "\u2258");
		// ESTIMATES ≙
		GLYPHS.put("uni2259", "\u2259");
		// EQUIANGULAR TO ≚
		GLYPHS.put("uni225A", "\u225A");
		// STAR EQUALS ≛
		GLYPHS.put("uni225B", "\u225B");
		// DELTA EQUAL TO ≜
		GLYPHS.put("uni225C", "\u225C");
		// EQUAL TO BY DEFINITION ≝
		GLYPHS.put("uni225D", "\u225D");
		// MEASURED BY ≞
		GLYPHS.put("uni225E", "\u225E");
		// QUESTIONED EQUAL TO ≟
		GLYPHS.put("uni225F", "\u225F");
		// NOT EQUAL TO ≠
		GLYPHS.put("notequal", "\u2260");
		// IDENTICAL TO ≡
		GLYPHS.put("equivalence", "\u2261");
		// NOT IDENTICAL TO ≢
		GLYPHS.put("uni2262", "\u2262");
		// STRICTLY EQUIVALENT TO ≣
		GLYPHS.put("uni2263", "\u2263");
		// LESS-THAN OR EQUAL TO ≤
		GLYPHS.put("lessequal", "\u2264");
		// GREATER-THAN OR EQUAL TO ≥
		GLYPHS.put("greaterequal", "\u2265");
		// LESS-THAN OVER EQUAL TO ≦
		GLYPHS.put("uni2266", "\u2266");
		// GREATER-THAN OVER EQUAL TO ≧
		GLYPHS.put("uni2267", "\u2267");
		// LESS-THAN BUT NOT EQUAL TO ≨
		GLYPHS.put("uni2268", "\u2268");
		// GREATER-THAN BUT NOT EQUAL TO ≩
		GLYPHS.put("uni2269", "\u2269");
		// MUCH LESS-THAN ≪
		GLYPHS.put("lessmuch", "\u226A");
		// MUCH GREATER-THAN ≫
		GLYPHS.put("greatermuch", "\u226B");
		// BETWEEN ≬
		GLYPHS.put("uni226C", "\u226C");
		// NOT EQUIVALENT TO ≭
		GLYPHS.put("uni226D", "\u226D");
		// NOT LESS-THAN ≮
		GLYPHS.put("uni226E", "\u226E");
		// NOT GREATER-THAN ≯
		GLYPHS.put("uni226F", "\u226F");
		// NEITHER LESS-THAN NOR EQUAL TO ≰
		GLYPHS.put("uni2270", "\u2270");
		// NEITHER GREATER-THAN NOR EQUAL TO ≱
		GLYPHS.put("uni2271", "\u2271");
		// LESS-THAN OR EQUIVALENT TO ≲
		GLYPHS.put("uni2272", "\u2272");
		// GREATER-THAN OR EQUIVALENT TO ≳
		GLYPHS.put("uni2273", "\u2273");
		// NEITHER LESS-THAN NOR EQUIVALENT TO ≴
		GLYPHS.put("uni2274", "\u2274");
		// NEITHER GREATER-THAN NOR EQUIVALENT TO ≵
		GLYPHS.put("uni2275", "\u2275");
		// LESS-THAN OR GREATER-THAN ≶
		GLYPHS.put("uni2276", "\u2276");
		// GREATER-THAN OR LESS-THAN ≷
		GLYPHS.put("uni2277", "\u2277");
		// NEITHER LESS-THAN NOR GREATER-THAN ≸
		GLYPHS.put("uni2278", "\u2278");
		// NEITHER GREATER-THAN NOR LESS-THAN ≹
		GLYPHS.put("uni2279", "\u2279");
		// PRECEDES ≺
		GLYPHS.put("uni227A", "\u227A");
		// SUCCEEDS ≻
		GLYPHS.put("uni227B", "\u227B");
		// PRECEDES OR EQUAL TO ≼
		GLYPHS.put("uni227C", "\u227C");
		// SUCCEEDS OR EQUAL TO ≽
		GLYPHS.put("uni227D", "\u227D");
		// PRECEDES OR EQUIVALENT TO ≾
		GLYPHS.put("uni227E", "\u227E");
		// SUCCEEDS OR EQUIVALENT TO ≿
		GLYPHS.put("uni227F", "\u227F");
		// DOES NOT PRECEDE ⊀
		GLYPHS.put("uni2280", "\u2280");
		// DOES NOT SUCCEED ⊁
		GLYPHS.put("uni2281", "\u2281");
		// SUBSET OF ⊂
		GLYPHS.put("propersubset", "\u2282");
		// SUPERSET OF ⊃
		GLYPHS.put("propersuperset", "\u2283");
		// NOT A SUBSET OF ⊄
		GLYPHS.put("uni2284", "\u2284");
		// NOT A SUPERSET OF ⊅
		GLYPHS.put("uni2285", "\u2285");
		// SUBSET OF OR EQUAL TO ⊆
		GLYPHS.put("reflexsubset", "\u2286");
		// SUPERSET OF OR EQUAL TO ⊇
		GLYPHS.put("reflexsuperset", "\u2287");
		// NEITHER A SUBSET OF NOR EQUAL TO ⊈
		GLYPHS.put("uni2288", "\u2288");
		// NEITHER A SUPERSET OF NOR EQUAL TO ⊉
		GLYPHS.put("uni2289", "\u2289");
		// SUBSET OF WITH NOT EQUAL TO ⊊
		GLYPHS.put("uni228A", "\u228A");
		// SUPERSET OF WITH NOT EQUAL TO ⊋
		GLYPHS.put("uni228B", "\u228B");
		// MULTISET ⊌
		GLYPHS.put("uni228C", "\u228C");
		// MULTISET MULTIPLICATION ⊍
		GLYPHS.put("uni228D", "\u228D");
		// MULTISET UNION ⊎
		GLYPHS.put("uni228E", "\u228E");
		// SQUARE IMAGE OF ⊏
		GLYPHS.put("uni228F", "\u228F");
		// SQUARE ORIGINAL OF ⊐
		GLYPHS.put("uni2290", "\u2290");
		// SQUARE IMAGE OF OR EQUAL TO ⊑
		GLYPHS.put("uni2291", "\u2291");
		// SQUARE ORIGINAL OF OR EQUAL TO ⊒
		GLYPHS.put("uni2292", "\u2292");
		// SQUARE CAP ⊓
		GLYPHS.put("uni2293", "\u2293");
		// SQUARE CUP ⊔
		GLYPHS.put("uni2294", "\u2294");
		// CIRCLED PLUS ⊕
		GLYPHS.put("circleplus", "\u2295");
		// CIRCLED MINUS ⊖
		GLYPHS.put("uni2296", "\u2296");
		// CIRCLED TIMES ⊗
		GLYPHS.put("circlemultiply", "\u2297");
		// CIRCLED DIVISION SLASH ⊘
		GLYPHS.put("circledivide", "\u2298");
		// CIRCLED DOT OPERATOR ⊙
		GLYPHS.put("circledot", "\u2299");
		// CIRCLED RING OPERATOR ⊚
		GLYPHS.put("uni229A", "\u229A");
		// CIRCLED ASTERISK OPERATOR ⊛
		GLYPHS.put("uni229B", "\u229B");
		// CIRCLED EQUALS ⊜
		GLYPHS.put("uni229C", "\u229C");
		// CIRCLED DASH ⊝
		GLYPHS.put("uni229D", "\u229D");
		// SQUARED PLUS ⊞
		GLYPHS.put("uni229E", "\u229E");
		// SQUARED MINUS ⊟
		GLYPHS.put("uni229F", "\u229F");
		// SQUARED TIMES ⊠
		GLYPHS.put("uni22A0", "\u22A0");
		// SQUARED DOT OPERATOR ⊡
		GLYPHS.put("uni22A1", "\u22A1");
		// RIGHT TACK ⊢
		GLYPHS.put("uni22A2", "\u22A2");
		// LEFT TACK ⊣
		GLYPHS.put("uni22A3", "\u22A3");
		// DOWN TACK ⊤
		GLYPHS.put("uni22A4", "\u22A4");
		// UP TACK ⊥
		GLYPHS.put("uni22A5", "\u22A5");
		// ASSERTION ⊦
		GLYPHS.put("uni22A6", "\u22A6");
		// MODELS ⊧
		GLYPHS.put("uni22A7", "\u22A7");
		// TRUE ⊨
		GLYPHS.put("uni22A8", "\u22A8");
		// FORCES ⊩
		GLYPHS.put("uni22A9", "\u22A9");
		// TRIPLE VERTICAL BAR RIGHT TURNSTILE ⊪
		GLYPHS.put("uni22AA", "\u22AA");
		// DOUBLE VERTICAL BAR DOUBLE RIGHT TURNSTILE ⊫
		GLYPHS.put("uni22AB", "\u22AB");
		// DOES NOT PROVE ⊬
		GLYPHS.put("uni22AC", "\u22AC");
		// NOT TRUE ⊭
		GLYPHS.put("uni22AD", "\u22AD");
		// DOES NOT FORCE ⊮
		GLYPHS.put("uni22AE", "\u22AE");
		// NEGATED DOUBLE VERTICAL BAR DOUBLE RIGHT TURNSTILE ⊯
		GLYPHS.put("uni22AF", "\u22AF");
		// NORMAL SUBGROUP OF ⊲
		GLYPHS.put("uni22B2", "\u22B2");
		// CONTAINS AS NORMAL SUBGROUP ⊳
		GLYPHS.put("uni22B3", "\u22B3");
		// NORMAL SUBGROUP OF OR EQUAL TO ⊴
		GLYPHS.put("uni22B4", "\u22B4");
		// CONTAINS AS NORMAL SUBGROUP OR EQUAL TO ⊵
		GLYPHS.put("uni22B5", "\u22B5");
		// ORIGINAL OF ⊶
		GLYPHS.put("uni22B6", "\u22B6");
		// IMAGE OF ⊷
		GLYPHS.put("uni22B7", "\u22B7");
		// MULTIMAP ⊸
		GLYPHS.put("uni22B8", "\u22B8");
		// HERMITIAN CONJUGATE MATRIX ⊹
		GLYPHS.put("uni22B9", "\u22B9");
		// INTERCALATE ⊺
		GLYPHS.put("uni22BA", "\u22BA");
		// XOR ⊻
		GLYPHS.put("uni22BB", "\u22BB");
		// NAND ⊼
		GLYPHS.put("uni22BC", "\u22BC");
		// NOR ⊽
		GLYPHS.put("uni22BD", "\u22BD");
		// RIGHT ANGLE WITH ARC ⊾
		GLYPHS.put("uni22BE", "\u22BE");
		// RIGHT TRIANGLE ⊿
		GLYPHS.put("uni22BF", "\u22BF");
		// N-ARY LOGICAL AND ⋀
		GLYPHS.put("uni22C0", "\u22C0");
		// N-ARY LOGICAL OR ⋁
		GLYPHS.put("uni22C1", "\u22C1");
		// N-ARY INTERSECTION ⋂
		GLYPHS.put("uni22C2", "\u22C2");
		// N-ARY UNION ⋃
		GLYPHS.put("uni22C3", "\u22C3");
		// DIAMOND OPERATOR ⋄
		GLYPHS.put("uni22C4", "\u22C4");
		// DOT OPERATOR ⋅
		GLYPHS.put("uni22C5", "\u22C5");
		// STAR OPERATOR ⋆
		GLYPHS.put("uni22C6", "\u22C6");
		// DIVISION TIMES ⋇
		GLYPHS.put("uni22C7", "\u22C7");
		// BOWTIE ⋈
		GLYPHS.put("uni22C8", "\u22C8");
		// LEFT NORMAL FACTOR SEMIDIRECT PRODUCT ⋉
		GLYPHS.put("uni22C9", "\u22C9");
		// RIGHT NORMAL FACTOR SEMIDIRECT PRODUCT ⋊
		GLYPHS.put("uni22CA", "\u22CA");
		// LEFT SEMIDIRECT PRODUCT ⋋
		GLYPHS.put("uni22CB", "\u22CB");
		// RIGHT SEMIDIRECT PRODUCT ⋌
		GLYPHS.put("uni22CC", "\u22CC");
		// REVERSED TILDE EQUALS ⋍
		GLYPHS.put("uni22CD", "\u22CD");
		// CURLY LOGICAL OR ⋎
		GLYPHS.put("uni22CE", "\u22CE");
		// CURLY LOGICAL AND ⋏
		GLYPHS.put("uni22CF", "\u22CF");
		// DOUBLE SUBSET ⋐
		GLYPHS.put("uni22D0", "\u22D0");
		// DOUBLE SUPERSET ⋑
		GLYPHS.put("uni22D1", "\u22D1");
		// DOUBLE INTERSECTION ⋒
		GLYPHS.put("uni22D2", "\u22D2");
		// DOUBLE UNION ⋓
		GLYPHS.put("uni22D3", "\u22D3");
		// EQUAL AND PARALLEL TO ⋕
		GLYPHS.put("uni22D5", "\u22D5");
		// LESS-THAN WITH DOT ⋖
		GLYPHS.put("uni22D6", "\u22D6");
		// GREATER-THAN WITH DOT ⋗
		GLYPHS.put("uni22D7", "\u22D7");
		// VERY MUCH LESS-THAN ⋘
		GLYPHS.put("uni22D8", "\u22D8");
		// VERY MUCH GREATER-THAN ⋙
		GLYPHS.put("uni22D9", "\u22D9");
		// LESS-THAN EQUAL TO OR GREATER-THAN ⋚
		GLYPHS.put("uni22DA", "\u22DA");
		// GREATER-THAN EQUAL TO OR LESS-THAN ⋛
		GLYPHS.put("uni22DB", "\u22DB");
		// EQUAL TO OR LESS-THAN ⋜
		GLYPHS.put("uni22DC", "\u22DC");
		// EQUAL TO OR GREATER-THAN ⋝
		GLYPHS.put("uni22DD", "\u22DD");
		// EQUAL TO OR PRECEDES ⋞
		GLYPHS.put("uni22DE", "\u22DE");
		// EQUAL TO OR SUCCEEDS ⋟
		GLYPHS.put("uni22DF", "\u22DF");
		// DOES NOT PRECEDE OR EQUAL ⋠
		GLYPHS.put("uni22E0", "\u22E0");
		// DOES NOT SUCCEED OR EQUAL ⋡
		GLYPHS.put("uni22E1", "\u22E1");
		// NOT SQUARE IMAGE OF OR EQUAL TO ⋢
		GLYPHS.put("uni22E2", "\u22E2");
		// NOT SQUARE ORIGINAL OF OR EQUAL TO ⋣
		GLYPHS.put("uni22E3", "\u22E3");
		// SQUARE IMAGE OF OR NOT EQUAL TO ⋤
		GLYPHS.put("uni22E4", "\u22E4");
		// SQUARE ORIGINAL OF OR NOT EQUAL TO ⋥
		GLYPHS.put("uni22E5", "\u22E5");
		// LESS-THAN BUT NOT EQUIVALENT TO ⋦
		GLYPHS.put("uni22E6", "\u22E6");
		// GREATER-THAN BUT NOT EQUIVALENT TO ⋧
		GLYPHS.put("uni22E7", "\u22E7");
		// PRECEDES BUT NOT EQUIVALENT TO ⋨
		GLYPHS.put("uni22E8", "\u22E8");
		// SUCCEEDS BUT NOT EQUIVALENT TO ⋩
		GLYPHS.put("uni22E9", "\u22E9");
		// NOT NORMAL SUBGROUP OF ⋪
		GLYPHS.put("uni22EA", "\u22EA");
		// DOES NOT CONTAIN AS NORMAL SUBGROUP ⋫
		GLYPHS.put("uni22EB", "\u22EB");
		// NOT NORMAL SUBGROUP OF OR EQUAL TO ⋬
		GLYPHS.put("uni22EC", "\u22EC");
		// DOES NOT CONTAIN AS NORMAL SUBGROUP OR EQUAL ⋭
		GLYPHS.put("uni22ED", "\u22ED");
		// VERTICAL ELLIPSIS ⋮
		GLYPHS.put("uni22EE", "\u22EE");
		// MIDLINE HORIZONTAL ELLIPSIS ⋯
		GLYPHS.put("uni22EF", "\u22EF");
		// UP RIGHT DIAGONAL ELLIPSIS ⋰
		GLYPHS.put("uni22F0", "\u22F0");
		// DOWN RIGHT DIAGONAL ELLIPSIS ⋱
		GLYPHS.put("uni22F1", "\u22F1");
		// DIAMETER SIGN ⌀
		GLYPHS.put("uni2300", "\u2300");
		// PROJECTIVE ⌅
		GLYPHS.put("uni2305", "\u2305");
		// PERSPECTIVE ⌆
		GLYPHS.put("uni2306", "\u2306");
		// LEFT CEILING ⌈
		GLYPHS.put("uni2308", "\u2308");
		// RIGHT CEILING ⌉
		GLYPHS.put("uni2309", "\u2309");
		// LEFT FLOOR ⌊
		GLYPHS.put("uni230A", "\u230A");
		// RIGHT FLOOR ⌋
		GLYPHS.put("uni230B", "\u230B");
		// REVERSED NOT SIGN ⌐
		GLYPHS.put("revlogicalnot", "\u2310");
		// TURNED NOT SIGN ⌙
		GLYPHS.put("uni2319", "\u2319");
		// TOP LEFT CORNER ⌜
		GLYPHS.put("uni231C", "\u231C");
		// TOP RIGHT CORNER ⌝
		GLYPHS.put("uni231D", "\u231D");
		// BOTTOM LEFT CORNER ⌞
		GLYPHS.put("uni231E", "\u231E");
		// BOTTOM RIGHT CORNER ⌟
		GLYPHS.put("uni231F", "\u231F");
		// TOP HALF INTEGRAL ⌠
		GLYPHS.put("integraltp", "\u2320");
		// BOTTOM HALF INTEGRAL ⌡
		GLYPHS.put("integralbt", "\u2321");
		// FROWN ⌢
		GLYPHS.put("uni2322", "\u2322");
		// SMILE ⌣
		GLYPHS.put("uni2323", "\u2323");
		// LEFT-POINTING ANGLE BRACKET 〈
		GLYPHS.put("angleleft", "\u2329");
		// RIGHT-POINTING ANGLE BRACKET 〉
		GLYPHS.put("angleright", "\u232A");
		// LEFT PARENTHESIS UPPER HOOK ⎛
		GLYPHS.put("uni239B", "\u239B");
		// LEFT PARENTHESIS EXTENSION ⎜
		GLYPHS.put("uni239C", "\u239C");
		// LEFT PARENTHESIS LOWER HOOK ⎝
		GLYPHS.put("uni239D", "\u239D");
		// RIGHT PARENTHESIS UPPER HOOK ⎞
		GLYPHS.put("uni239E", "\u239E");
		// RIGHT PARENTHESIS EXTENSION ⎟
		GLYPHS.put("uni239F", "\u239F");
		// RIGHT PARENTHESIS LOWER HOOK ⎠
		GLYPHS.put("uni23A0", "\u23A0");
		// LEFT SQUARE BRACKET UPPER CORNER ⎡
		GLYPHS.put("uni23A1", "\u23A1");
		// LEFT SQUARE BRACKET EXTENSION ⎢
		GLYPHS.put("uni23A2", "\u23A2");
		// LEFT SQUARE BRACKET LOWER CORNER ⎣
		GLYPHS.put("uni23A3", "\u23A3");
		// RIGHT SQUARE BRACKET UPPER CORNER ⎤
		GLYPHS.put("uni23A4", "\u23A4");
		// RIGHT SQUARE BRACKET EXTENSION ⎥
		GLYPHS.put("uni23A5", "\u23A5");
		// RIGHT SQUARE BRACKET LOWER CORNER ⎦
		GLYPHS.put("uni23A6", "\u23A6");
		// LEFT CURLY BRACKET UPPER HOOK ⎧
		GLYPHS.put("uni23A7", "\u23A7");
		// LEFT CURLY BRACKET MIDDLE PIECE ⎨
		GLYPHS.put("uni23A8", "\u23A8");
		// LEFT CURLY BRACKET LOWER HOOK ⎩
		GLYPHS.put("uni23A9", "\u23A9");
		// CURLY BRACKET EXTENSION ⎪
		GLYPHS.put("uni23AA", "\u23AA");
		// RIGHT CURLY BRACKET UPPER HOOK ⎫
		GLYPHS.put("uni23AB", "\u23AB");
		// RIGHT CURLY BRACKET MIDDLE PIECE ⎬
		GLYPHS.put("uni23AC", "\u23AC");
		// RIGHT CURLY BRACKET LOWER HOOK ⎭
		GLYPHS.put("uni23AD", "\u23AD");
		// SUMMATION TOP ⎲
		GLYPHS.put("uni23B2", "\u23B2");
		// SUMMATION BOTTOM ⎳
		GLYPHS.put("uni23B3", "\u23B3");
		// TOP SQUARE BRACKET ⎴
		GLYPHS.put("uni23B4", "\u23B4");
		// BOTTOM SQUARE BRACKET ⎵
		GLYPHS.put("uni23B5", "\u23B5");
		// RADICAL SYMBOL BOTTOM ⎷
		GLYPHS.put("uni23B7", "\u23B7");
		// VERTICAL LINE EXTENSION ⏐
		GLYPHS.put("uni23D0", "\u23D0");
		// TOP PARENTHESIS ⏜
		GLYPHS.put("uni23DC", "\u23DC");
		// BOTTOM PARENTHESIS ⏝
		GLYPHS.put("uni23DD", "\u23DD");
		// TOP CURLY BRACKET ⏞
		GLYPHS.put("uni23DE", "\u23DE");
		// BOTTOM CURLY BRACKET ⏟
		GLYPHS.put("uni23DF", "\u23DF");
		// TOP TORTOISE SHELL BRACKET ⏠
		GLYPHS.put("uni23E0", "\u23E0");
		// BOTTOM TORTOISE SHELL BRACKET ⏡
		GLYPHS.put("uni23E1", "\u23E1");
		// BLANK SYMBOL ␢
		GLYPHS.put("blanksymbol", "\u2422");
		// OPEN BOX ␣
		GLYPHS.put("uni2423", "\u2423");
		// BOX DRAWINGS LIGHT HORIZONTAL ─
		GLYPHS.put("SF100000", "\u2500");
		// BOX DRAWINGS LIGHT VERTICAL │
		GLYPHS.put("SF110000", "\u2502");
		// BOX DRAWINGS LIGHT DOWN AND RIGHT ┌
		GLYPHS.put("SF010000", "\u250C");
		// BOX DRAWINGS LIGHT DOWN AND LEFT ┐
		GLYPHS.put("SF030000", "\u2510");
		// BOX DRAWINGS LIGHT UP AND RIGHT └
		GLYPHS.put("SF020000", "\u2514");
		// BOX DRAWINGS LIGHT UP AND LEFT ┘
		GLYPHS.put("SF040000", "\u2518");
		// BOX DRAWINGS LIGHT VERTICAL AND RIGHT ├
		GLYPHS.put("SF080000", "\u251C");
		// BOX DRAWINGS LIGHT VERTICAL AND LEFT ┤
		GLYPHS.put("SF090000", "\u2524");
		// BOX DRAWINGS LIGHT DOWN AND HORIZONTAL ┬
		GLYPHS.put("SF060000", "\u252C");
		// BOX DRAWINGS LIGHT UP AND HORIZONTAL ┴
		GLYPHS.put("SF070000", "\u2534");
		// BOX DRAWINGS LIGHT VERTICAL AND HORIZONTAL ┼
		GLYPHS.put("SF050000", "\u253C");
		// LOWER ONE EIGHTH BLOCK ▁
		GLYPHS.put("uni2581", "\u2581");
		// FULL BLOCK █
		GLYPHS.put("block", "\u2588");
		// LIGHT SHADE ░
		GLYPHS.put("ltshade", "\u2591");
		// MEDIUM SHADE ▒
		GLYPHS.put("shade", "\u2592");
		// DARK SHADE ▓
		GLYPHS.put("dkshade", "\u2593");
		// BLACK SQUARE ■
		GLYPHS.put("filledbox", "\u25A0");
		// WHITE SQUARE □
		GLYPHS.put("H22073", "\u25A1");
		// BLACK SMALL SQUARE ▪
		GLYPHS.put("H18543", "\u25AA");
		// WHITE SMALL SQUARE ▫
		GLYPHS.put("H18551", "\u25AB");
		// BLACK RECTANGLE ▬
		GLYPHS.put("filledrect", "\u25AC");
		// WHITE RECTANGLE ▭
		GLYPHS.put("uni25AD", "\u25AD");
		// BLACK UP-POINTING TRIANGLE ▲
		GLYPHS.put("triagup", "\u25B2");
		// WHITE UP-POINTING TRIANGLE △
		GLYPHS.put("uni25B3", "\u25B3");
		// BLACK RIGHT-POINTING TRIANGLE ▶
		GLYPHS.put("uni25B6", "\u25B6");
		// WHITE RIGHT-POINTING TRIANGLE ▷
		GLYPHS.put("uni25B7", "\u25B7");
		// BLACK DOWN-POINTING TRIANGLE ▼
		GLYPHS.put("triagdn", "\u25BC");
		// WHITE DOWN-POINTING TRIANGLE ▽
		GLYPHS.put("uni25BD", "\u25BD");
		// BLACK LEFT-POINTING TRIANGLE ◀
		GLYPHS.put("uni25C0", "\u25C0");
		// WHITE LEFT-POINTING TRIANGLE ◁
		GLYPHS.put("uni25C1", "\u25C1");
		// LOZENGE ◊
		GLYPHS.put("lozenge", "\u25CA");
		// WHITE CIRCLE ○
		GLYPHS.put("circle", "\u25CB");
		// BLACK CIRCLE ●
		GLYPHS.put("H18533", "\u25CF");
		// WHITE BULLET ◦
		GLYPHS.put("openbullet", "\u25E6");
		// LARGE CIRCLE ◯
		GLYPHS.put("uni25EF", "\u25EF");
		// BLACK SPADE SUIT ♠
		GLYPHS.put("spade", "\u2660");
		// WHITE HEART SUIT ♡
		GLYPHS.put("heartsuitwhite", "\u2661");
		// WHITE DIAMOND SUIT ♢
		GLYPHS.put("diamondsuitwhite", "\u2662");
		// BLACK CLUB SUIT ♣
		GLYPHS.put("club", "\u2663");
		// WHITE SPADE SUIT ♤
		GLYPHS.put("spadesuitwhite", "\u2664");
		// BLACK HEART SUIT ♥
		GLYPHS.put("heart", "\u2665");
		// BLACK DIAMOND SUIT ♦
		GLYPHS.put("diamond", "\u2666");
		// WHITE CLUB SUIT ♧
		GLYPHS.put("clubsuitwhite", "\u2667");
		// EIGHTH NOTE ♪
		GLYPHS.put("musicalnote", "\u266A");
		// MUSIC FLAT SIGN ♭
		GLYPHS.put("musicflatsign", "\u266D");
		// MUSIC NATURAL SIGN ♮
		GLYPHS.put("uni266E", "\u266E");
		// MUSIC SHARP SIGN ♯
		GLYPHS.put("musicsharpsign", "\u266F");
		// MARRIAGE SYMBOL ⚭
		GLYPHS.put("married", "\u26AD");
		// DIVORCE SYMBOL ⚮
		GLYPHS.put("divorced", "\u26AE");
		// CHECK MARK ✓
		GLYPHS.put("checkmark", "\u2713");
		// MALTESE CROSS ✠
		GLYPHS.put("uni2720", "\u2720");
		// HEAVY VERTICAL BAR ❚
		GLYPHS.put("uni275A", "\u275A");
		// BLACK RIGHTWARDS ARROW ➡
		GLYPHS.put("uni27A1", "\u27A1");
		// PERPENDICULAR ⟂
		GLYPHS.put("uni27C2", "\u27C2");
		// LARGE UP TACK ⟘
		GLYPHS.put("uni27D8", "\u27D8");
		// LARGE DOWN TACK ⟙
		GLYPHS.put("uni27D9", "\u27D9");
		// LEFT AND RIGHT DOUBLE TURNSTILE ⟚
		GLYPHS.put("uni27DA", "\u27DA");
		// LEFT AND RIGHT TACK ⟛
		GLYPHS.put("uni27DB", "\u27DB");
		// LEFT MULTIMAP ⟜
		GLYPHS.put("uni27DC", "\u27DC");
		// LONG RIGHT TACK ⟝
		GLYPHS.put("uni27DD", "\u27DD");
		// LONG LEFT TACK ⟞
		GLYPHS.put("uni27DE", "\u27DE");
		// LOZENGE DIVIDED BY HORIZONTAL RULE ⟠
		GLYPHS.put("uni27E0", "\u27E0");
		// WHITE CONCAVE-SIDED DIAMOND ⟡
		GLYPHS.put("uni27E1", "\u27E1");
		// WHITE CONCAVE-SIDED DIAMOND WITH LEFTWARDS TICK ⟢
		GLYPHS.put("uni27E2", "\u27E2");
		// WHITE CONCAVE-SIDED DIAMOND WITH RIGHTWARDS TICK ⟣
		GLYPHS.put("uni27E3", "\u27E3");
		// MATHEMATICAL LEFT WHITE SQUARE BRACKET ⟦
		GLYPHS.put("uni27E6", "\u27E6");
		// MATHEMATICAL RIGHT WHITE SQUARE BRACKET ⟧
		GLYPHS.put("uni27E7", "\u27E7");
		// MATHEMATICAL LEFT ANGLE BRACKET ⟨
		GLYPHS.put("uni27E8", "\u27E8");
		// MATHEMATICAL RIGHT ANGLE BRACKET ⟩
		GLYPHS.put("uni27E9", "\u27E9");
		// MATHEMATICAL LEFT DOUBLE ANGLE BRACKET ⟪
		GLYPHS.put("uni27EA", "\u27EA");
		// MATHEMATICAL RIGHT DOUBLE ANGLE BRACKET ⟫
		GLYPHS.put("uni27EB", "\u27EB");
		// MATHEMATICAL LEFT FLATTENED PARENTHESIS ⟮
		GLYPHS.put("uni27EE", "\u27EE");
		// MATHEMATICAL RIGHT FLATTENED PARENTHESIS ⟯
		GLYPHS.put("uni27EF", "\u27EF");
		// RIGHT ARROW WITH CIRCLED PLUS ⟴
		GLYPHS.put("uni27F4", "\u27F4");
		// LONG LEFTWARDS ARROW ⟵
		GLYPHS.put("uni27F5", "\u27F5");
		// LONG RIGHTWARDS ARROW ⟶
		GLYPHS.put("uni27F6", "\u27F6");
		// LONG LEFT RIGHT ARROW ⟷
		GLYPHS.put("uni27F7", "\u27F7");
		// LONG LEFTWARDS DOUBLE ARROW ⟸
		GLYPHS.put("uni27F8", "\u27F8");
		// LONG RIGHTWARDS DOUBLE ARROW ⟹
		GLYPHS.put("uni27F9", "\u27F9");
		// LONG LEFT RIGHT DOUBLE ARROW ⟺
		GLYPHS.put("uni27FA", "\u27FA");
		// LONG LEFTWARDS ARROW FROM BAR ⟻
		GLYPHS.put("uni27FB", "\u27FB");
		// LONG RIGHTWARDS ARROW FROM BAR ⟼
		GLYPHS.put("uni27FC", "\u27FC");
		// LONG LEFTWARDS DOUBLE ARROW FROM BAR ⟽
		GLYPHS.put("uni27FD", "\u27FD");
		// LONG RIGHTWARDS DOUBLE ARROW FROM BAR ⟾
		GLYPHS.put("uni27FE", "\u27FE");
		// LONG RIGHTWARDS SQUIGGLE ARROW ⟿
		GLYPHS.put("uni27FF", "\u27FF");
		// LEFTWARDS DOUBLE ARROW FROM BAR ⤆
		GLYPHS.put("uni2906", "\u2906");
		// RIGHTWARDS DOUBLE ARROW FROM BAR ⤇
		GLYPHS.put("uni2907", "\u2907");
		// N-ARY CIRCLED DOT OPERATOR ⨀
		GLYPHS.put("uni2A00", "\u2A00");
		// N-ARY CIRCLED PLUS OPERATOR ⨁
		GLYPHS.put("uni2A01", "\u2A01");
		// N-ARY CIRCLED TIMES OPERATOR ⨂
		GLYPHS.put("uni2A02", "\u2A02");
		// N-ARY UNION OPERATOR WITH DOT ⨃
		GLYPHS.put("uni2A03", "\u2A03");
		// N-ARY UNION OPERATOR WITH PLUS ⨄
		GLYPHS.put("uni2A04", "\u2A04");
		// N-ARY SQUARE INTERSECTION OPERATOR ⨅
		GLYPHS.put("uni2A05", "\u2A05");
		// N-ARY SQUARE UNION OPERATOR ⨆
		GLYPHS.put("uni2A06", "\u2A06");
		// N-ARY TIMES OPERATOR ⨉
		GLYPHS.put("uni2A09", "\u2A09");
		// QUADRUPLE INTEGRAL OPERATOR ⨌
		GLYPHS.put("uni2A0C", "\u2A0C");
		// ANTICLOCKWISE INTEGRATION ⨑
		GLYPHS.put("uni2A11", "\u2A11");
		// VECTOR OR CROSS PRODUCT ⨯
		GLYPHS.put("uni2A2F", "\u2A2F");
		// AMALGAMATION OR COPRODUCT ⨿
		GLYPHS.put("uni2A3F", "\u2A3F");
		// LESS-THAN OR SLANTED EQUAL TO ⩽
		GLYPHS.put("uni2A7D", "\u2A7D");
		// GREATER-THAN OR SLANTED EQUAL TO ⩾
		GLYPHS.put("uni2A7E", "\u2A7E");
		// LESS-THAN OR APPROXIMATE ⪅
		GLYPHS.put("uni2A85", "\u2A85");
		// GREATER-THAN OR APPROXIMATE ⪆
		GLYPHS.put("uni2A86", "\u2A86");
		// LESS-THAN AND SINGLE-LINE NOT EQUAL TO ⪇
		GLYPHS.put("uni2A87", "\u2A87");
		// GREATER-THAN AND SINGLE-LINE NOT EQUAL TO ⪈
		GLYPHS.put("uni2A88", "\u2A88");
		// LESS-THAN AND NOT APPROXIMATE ⪉
		GLYPHS.put("uni2A89", "\u2A89");
		// GREATER-THAN AND NOT APPROXIMATE ⪊
		GLYPHS.put("uni2A8A", "\u2A8A");
		// LESS-THAN ABOVE DOUBLE-LINE EQUAL ABOVE GREATER-THAN ⪋
		GLYPHS.put("uni2A8B", "\u2A8B");
		// GREATER-THAN ABOVE DOUBLE-LINE EQUAL ABOVE LESS-THAN ⪌
		GLYPHS.put("uni2A8C", "\u2A8C");
		// SLANTED EQUAL TO OR LESS-THAN ⪕
		GLYPHS.put("uni2A95", "\u2A95");
		// SLANTED EQUAL TO OR GREATER-THAN ⪖
		GLYPHS.put("uni2A96", "\u2A96");
		// PRECEDES ABOVE SINGLE-LINE EQUALS SIGN ⪯
		GLYPHS.put("uni2AAF", "\u2AAF");
		// SUCCEEDS ABOVE SINGLE-LINE EQUALS SIGN ⪰
		GLYPHS.put("uni2AB0", "\u2AB0");
		// LEFT RIGHT WHITE ARROW ⬄
		GLYPHS.put("uni2B04", "\u2B04");
		// LEFTWARDS BLACK ARROW ⬅
		GLYPHS.put("uni2B05", "\u2B05");
		// UPWARDS BLACK ARROW ⬆
		GLYPHS.put("uni2B06", "\u2B06");
		// DOWNWARDS BLACK ARROW ⬇
		GLYPHS.put("uni2B07", "\u2B07");
		// LEFT RIGHT BLACK ARROW ⬌
		GLYPHS.put("uni2B0C", "\u2B0C");
		// UP DOWN BLACK ARROW ⬍
		GLYPHS.put("uni2B0D", "\u2B0D");
		// DOTTED SQUARE ⬚
		GLYPHS.put("uni2B1A", "\u2B1A");
		// THREE LEFTWARDS ARROWS ⬱
		GLYPHS.put("uni2B31", "\u2B31");
		// LONG LEFTWARDS SQUIGGLE ARROW ⬳
		GLYPHS.put("uni2B33", "\u2B33");
		// INVERTED INTERROBANG ⸘
		GLYPHS.put("uni2E18", "\u2E18");
		// LEFT WHITE LENTICULAR BRACKET 〖
		GLYPHS.put("whitelenticularbracketleft", "\u3016");
		// RIGHT WHITE LENTICULAR BRACKET 〗
		GLYPHS.put("whitelenticularbracketright", "\u3017");
		// LATIN SMALL LIGATURE FF ﬀ
		GLYPHS.put("f_f", "\uFB00");
		// LATIN SMALL LIGATURE FI ﬁ
		GLYPHS.put("f_i", "\uFB01");
		// LATIN SMALL LIGATURE FL ﬂ
		GLYPHS.put("f_l", "\uFB02");
		// LATIN SMALL LIGATURE FFI ﬃ
		GLYPHS.put("f_f_i", "\uFB03");
		// LATIN SMALL LIGATURE FFL ﬄ
		GLYPHS.put("f_f_l", "\uFB04");
		// ZERO WIDTH NO-BREAK SPACE ﻿
		GLYPHS.put("uniFEFF", "\uFEFF");
		// MATHEMATICAL BOLD CAPITAL A 𝐀
		GLYPHS.put("u1D400", "\u1D400");
		// MATHEMATICAL BOLD CAPITAL B 𝐁
		GLYPHS.put("u1D401", "\u1D401");
		// MATHEMATICAL BOLD CAPITAL C 𝐂
		GLYPHS.put("u1D402", "\u1D402");
		// MATHEMATICAL BOLD CAPITAL D 𝐃
		GLYPHS.put("u1D403", "\u1D403");
		// MATHEMATICAL BOLD CAPITAL E 𝐄
		GLYPHS.put("u1D404", "\u1D404");
		// MATHEMATICAL BOLD CAPITAL F 𝐅
		GLYPHS.put("u1D405", "\u1D405");
		// MATHEMATICAL BOLD CAPITAL G 𝐆
		GLYPHS.put("u1D406", "\u1D406");
		// MATHEMATICAL BOLD CAPITAL H 𝐇
		GLYPHS.put("u1D407", "\u1D407");
		// MATHEMATICAL BOLD CAPITAL I 𝐈
		GLYPHS.put("u1D408", "\u1D408");
		// MATHEMATICAL BOLD CAPITAL J 𝐉
		GLYPHS.put("u1D409", "\u1D409");
		// MATHEMATICAL BOLD CAPITAL K 𝐊
		GLYPHS.put("u1D40A", "\u1D40A");
		// MATHEMATICAL BOLD CAPITAL L 𝐋
		GLYPHS.put("u1D40B", "\u1D40B");
		// MATHEMATICAL BOLD CAPITAL M 𝐌
		GLYPHS.put("u1D40C", "\u1D40C");
		// MATHEMATICAL BOLD CAPITAL N 𝐍
		GLYPHS.put("u1D40D", "\u1D40D");
		// MATHEMATICAL BOLD CAPITAL O 𝐎
		GLYPHS.put("u1D40E", "\u1D40E");
		// MATHEMATICAL BOLD CAPITAL P 𝐏
		GLYPHS.put("u1D40F", "\u1D40F");
		// MATHEMATICAL BOLD CAPITAL Q 𝐐
		GLYPHS.put("u1D410", "\u1D410");
		// MATHEMATICAL BOLD CAPITAL R 𝐑
		GLYPHS.put("u1D411", "\u1D411");
		// MATHEMATICAL BOLD CAPITAL S 𝐒
		GLYPHS.put("u1D412", "\u1D412");
		// MATHEMATICAL BOLD CAPITAL T 𝐓
		GLYPHS.put("u1D413", "\u1D413");
		// MATHEMATICAL BOLD CAPITAL U 𝐔
		GLYPHS.put("u1D414", "\u1D414");
		// MATHEMATICAL BOLD CAPITAL V 𝐕
		GLYPHS.put("u1D415", "\u1D415");
		// MATHEMATICAL BOLD CAPITAL W 𝐖
		GLYPHS.put("u1D416", "\u1D416");
		// MATHEMATICAL BOLD CAPITAL X 𝐗
		GLYPHS.put("u1D417", "\u1D417");
		// MATHEMATICAL BOLD CAPITAL Y 𝐘
		GLYPHS.put("u1D418", "\u1D418");
		// MATHEMATICAL BOLD CAPITAL Z 𝐙
		GLYPHS.put("u1D419", "\u1D419");
		// MATHEMATICAL BOLD SMALL A 𝐚
		GLYPHS.put("u1D41A", "\u1D41A");
		// MATHEMATICAL BOLD SMALL B 𝐛
		GLYPHS.put("u1D41B", "\u1D41B");
		// MATHEMATICAL BOLD SMALL C 𝐜
		GLYPHS.put("u1D41C", "\u1D41C");
		// MATHEMATICAL BOLD SMALL D 𝐝
		GLYPHS.put("u1D41D", "\u1D41D");
		// MATHEMATICAL BOLD SMALL E 𝐞
		GLYPHS.put("u1D41E", "\u1D41E");
		// MATHEMATICAL BOLD SMALL F 𝐟
		GLYPHS.put("u1D41F", "\u1D41F");
		// MATHEMATICAL BOLD SMALL G 𝐠
		GLYPHS.put("u1D420", "\u1D420");
		// MATHEMATICAL BOLD SMALL H 𝐡
		GLYPHS.put("u1D421", "\u1D421");
		// MATHEMATICAL BOLD SMALL I 𝐢
		GLYPHS.put("u1D422", "\u1D422");
		// MATHEMATICAL BOLD SMALL J 𝐣
		GLYPHS.put("u1D423", "\u1D423");
		// MATHEMATICAL BOLD SMALL K 𝐤
		GLYPHS.put("u1D424", "\u1D424");
		// MATHEMATICAL BOLD SMALL L 𝐥
		GLYPHS.put("u1D425", "\u1D425");
		// MATHEMATICAL BOLD SMALL M 𝐦
		GLYPHS.put("u1D426", "\u1D426");
		// MATHEMATICAL BOLD SMALL N 𝐧
		GLYPHS.put("u1D427", "\u1D427");
		// MATHEMATICAL BOLD SMALL O 𝐨
		GLYPHS.put("u1D428", "\u1D428");
		// MATHEMATICAL BOLD SMALL P 𝐩
		GLYPHS.put("u1D429", "\u1D429");
		// MATHEMATICAL BOLD SMALL Q 𝐪
		GLYPHS.put("u1D42A", "\u1D42A");
		// MATHEMATICAL BOLD SMALL R 𝐫
		GLYPHS.put("u1D42B", "\u1D42B");
		// MATHEMATICAL BOLD SMALL S 𝐬
		GLYPHS.put("u1D42C", "\u1D42C");
		// MATHEMATICAL BOLD SMALL T 𝐭
		GLYPHS.put("u1D42D", "\u1D42D");
		// MATHEMATICAL BOLD SMALL U 𝐮
		GLYPHS.put("u1D42E", "\u1D42E");
		// MATHEMATICAL BOLD SMALL V 𝐯
		GLYPHS.put("u1D42F", "\u1D42F");
		// MATHEMATICAL BOLD SMALL W 𝐰
		GLYPHS.put("u1D430", "\u1D430");
		// MATHEMATICAL BOLD SMALL X 𝐱
		GLYPHS.put("u1D431", "\u1D431");
		// MATHEMATICAL BOLD SMALL Y 𝐲
		GLYPHS.put("u1D432", "\u1D432");
		// MATHEMATICAL BOLD SMALL Z 𝐳
		GLYPHS.put("u1D433", "\u1D433");
		// MATHEMATICAL ITALIC CAPITAL A 𝐴
		GLYPHS.put("u1D434", "\u1D434");
		// MATHEMATICAL ITALIC CAPITAL B 𝐵
		GLYPHS.put("u1D435", "\u1D435");
		// MATHEMATICAL ITALIC CAPITAL C 𝐶
		GLYPHS.put("u1D436", "\u1D436");
		// MATHEMATICAL ITALIC CAPITAL D 𝐷
		GLYPHS.put("u1D437", "\u1D437");
		// MATHEMATICAL ITALIC CAPITAL E 𝐸
		GLYPHS.put("u1D438", "\u1D438");
		// MATHEMATICAL ITALIC CAPITAL F 𝐹
		GLYPHS.put("u1D439", "\u1D439");
		// MATHEMATICAL ITALIC CAPITAL G 𝐺
		GLYPHS.put("u1D43A", "\u1D43A");
		// MATHEMATICAL ITALIC CAPITAL H 𝐻
		GLYPHS.put("u1D43B", "\u1D43B");
		// MATHEMATICAL ITALIC CAPITAL I 𝐼
		GLYPHS.put("u1D43C", "\u1D43C");
		// MATHEMATICAL ITALIC CAPITAL J 𝐽
		GLYPHS.put("u1D43D", "\u1D43D");
		// MATHEMATICAL ITALIC CAPITAL K 𝐾
		GLYPHS.put("u1D43E", "\u1D43E");
		// MATHEMATICAL ITALIC CAPITAL L 𝐿
		GLYPHS.put("u1D43F", "\u1D43F");
		// MATHEMATICAL ITALIC CAPITAL M 𝑀
		GLYPHS.put("u1D440", "\u1D440");
		// MATHEMATICAL ITALIC CAPITAL N 𝑁
		GLYPHS.put("u1D441", "\u1D441");
		// MATHEMATICAL ITALIC CAPITAL O 𝑂
		GLYPHS.put("u1D442", "\u1D442");
		// MATHEMATICAL ITALIC CAPITAL P 𝑃
		GLYPHS.put("u1D443", "\u1D443");
		// MATHEMATICAL ITALIC CAPITAL Q 𝑄
		GLYPHS.put("u1D444", "\u1D444");
		// MATHEMATICAL ITALIC CAPITAL R 𝑅
		GLYPHS.put("u1D445", "\u1D445");
		// MATHEMATICAL ITALIC CAPITAL S 𝑆
		GLYPHS.put("u1D446", "\u1D446");
		// MATHEMATICAL ITALIC CAPITAL T 𝑇
		GLYPHS.put("u1D447", "\u1D447");
		// MATHEMATICAL ITALIC CAPITAL U 𝑈
		GLYPHS.put("u1D448", "\u1D448");
		// MATHEMATICAL ITALIC CAPITAL V 𝑉
		GLYPHS.put("u1D449", "\u1D449");
		// MATHEMATICAL ITALIC CAPITAL W 𝑊
		GLYPHS.put("u1D44A", "\u1D44A");
		// MATHEMATICAL ITALIC CAPITAL X 𝑋
		GLYPHS.put("u1D44B", "\u1D44B");
		// MATHEMATICAL ITALIC CAPITAL Y 𝑌
		GLYPHS.put("u1D44C", "\u1D44C");
		// MATHEMATICAL ITALIC CAPITAL Z 𝑍
		GLYPHS.put("u1D44D", "\u1D44D");
		// MATHEMATICAL ITALIC SMALL A 𝑎
		GLYPHS.put("u1D44E", "\u1D44E");
		// MATHEMATICAL ITALIC SMALL B 𝑏
		GLYPHS.put("u1D44F", "\u1D44F");
		// MATHEMATICAL ITALIC SMALL C 𝑐
		GLYPHS.put("u1D450", "\u1D450");
		// MATHEMATICAL ITALIC SMALL D 𝑑
		GLYPHS.put("u1D451", "\u1D451");
		// MATHEMATICAL ITALIC SMALL E 𝑒
		GLYPHS.put("u1D452", "\u1D452");
		// MATHEMATICAL ITALIC SMALL F 𝑓
		GLYPHS.put("u1D453", "\u1D453");
		// MATHEMATICAL ITALIC SMALL G 𝑔
		GLYPHS.put("u1D454", "\u1D454");
		// MATHEMATICAL ITALIC SMALL I 𝑖
		GLYPHS.put("u1D456", "\u1D456");
		// MATHEMATICAL ITALIC SMALL J 𝑗
		GLYPHS.put("u1D457", "\u1D457");
		// MATHEMATICAL ITALIC SMALL K 𝑘
		GLYPHS.put("u1D458", "\u1D458");
		// MATHEMATICAL ITALIC SMALL L 𝑙
		GLYPHS.put("u1D459", "\u1D459");
		// MATHEMATICAL ITALIC SMALL M 𝑚
		GLYPHS.put("u1D45A", "\u1D45A");
		// MATHEMATICAL ITALIC SMALL N 𝑛
		GLYPHS.put("u1D45B", "\u1D45B");
		// MATHEMATICAL ITALIC SMALL O 𝑜
		GLYPHS.put("u1D45C", "\u1D45C");
		// MATHEMATICAL ITALIC SMALL P 𝑝
		GLYPHS.put("u1D45D", "\u1D45D");
		// MATHEMATICAL ITALIC SMALL Q 𝑞
		GLYPHS.put("u1D45E", "\u1D45E");
		// MATHEMATICAL ITALIC SMALL R 𝑟
		GLYPHS.put("u1D45F", "\u1D45F");
		// MATHEMATICAL ITALIC SMALL S 𝑠
		GLYPHS.put("u1D460", "\u1D460");
		// MATHEMATICAL ITALIC SMALL T 𝑡
		GLYPHS.put("u1D461", "\u1D461");
		// MATHEMATICAL ITALIC SMALL U 𝑢
		GLYPHS.put("u1D462", "\u1D462");
		// MATHEMATICAL ITALIC SMALL V 𝑣
		GLYPHS.put("u1D463", "\u1D463");
		// MATHEMATICAL ITALIC SMALL W 𝑤
		GLYPHS.put("u1D464", "\u1D464");
		// MATHEMATICAL ITALIC SMALL X 𝑥
		GLYPHS.put("u1D465", "\u1D465");
		// MATHEMATICAL ITALIC SMALL Y 𝑦
		GLYPHS.put("u1D466", "\u1D466");
		// MATHEMATICAL ITALIC SMALL Z 𝑧
		GLYPHS.put("u1D467", "\u1D467");
		// MATHEMATICAL BOLD ITALIC CAPITAL A 𝑨
		GLYPHS.put("u1D468", "\u1D468");
		// MATHEMATICAL BOLD ITALIC CAPITAL B 𝑩
		GLYPHS.put("u1D469", "\u1D469");
		// MATHEMATICAL BOLD ITALIC CAPITAL C 𝑪
		GLYPHS.put("u1D46A", "\u1D46A");
		// MATHEMATICAL BOLD ITALIC CAPITAL D 𝑫
		GLYPHS.put("u1D46B", "\u1D46B");
		// MATHEMATICAL BOLD ITALIC CAPITAL E 𝑬
		GLYPHS.put("u1D46C", "\u1D46C");
		// MATHEMATICAL BOLD ITALIC CAPITAL F 𝑭
		GLYPHS.put("u1D46D", "\u1D46D");
		// MATHEMATICAL BOLD ITALIC CAPITAL G 𝑮
		GLYPHS.put("u1D46E", "\u1D46E");
		// MATHEMATICAL BOLD ITALIC CAPITAL H 𝑯
		GLYPHS.put("u1D46F", "\u1D46F");
		// MATHEMATICAL BOLD ITALIC CAPITAL I 𝑰
		GLYPHS.put("u1D470", "\u1D470");
		// MATHEMATICAL BOLD ITALIC CAPITAL J 𝑱
		GLYPHS.put("u1D471", "\u1D471");
		// MATHEMATICAL BOLD ITALIC CAPITAL K 𝑲
		GLYPHS.put("u1D472", "\u1D472");
		// MATHEMATICAL BOLD ITALIC CAPITAL L 𝑳
		GLYPHS.put("u1D473", "\u1D473");
		// MATHEMATICAL BOLD ITALIC CAPITAL M 𝑴
		GLYPHS.put("u1D474", "\u1D474");
		// MATHEMATICAL BOLD ITALIC CAPITAL N 𝑵
		GLYPHS.put("u1D475", "\u1D475");
		// MATHEMATICAL BOLD ITALIC CAPITAL O 𝑶
		GLYPHS.put("u1D476", "\u1D476");
		// MATHEMATICAL BOLD ITALIC CAPITAL P 𝑷
		GLYPHS.put("u1D477", "\u1D477");
		// MATHEMATICAL BOLD ITALIC CAPITAL Q 𝑸
		GLYPHS.put("u1D478", "\u1D478");
		// MATHEMATICAL BOLD ITALIC CAPITAL R 𝑹
		GLYPHS.put("u1D479", "\u1D479");
		// MATHEMATICAL BOLD ITALIC CAPITAL S 𝑺
		GLYPHS.put("u1D47A", "\u1D47A");
		// MATHEMATICAL BOLD ITALIC CAPITAL T 𝑻
		GLYPHS.put("u1D47B", "\u1D47B");
		// MATHEMATICAL BOLD ITALIC CAPITAL U 𝑼
		GLYPHS.put("u1D47C", "\u1D47C");
		// MATHEMATICAL BOLD ITALIC CAPITAL V 𝑽
		GLYPHS.put("u1D47D", "\u1D47D");
		// MATHEMATICAL BOLD ITALIC CAPITAL W 𝑾
		GLYPHS.put("u1D47E", "\u1D47E");
		// MATHEMATICAL BOLD ITALIC CAPITAL X 𝑿
		GLYPHS.put("u1D47F", "\u1D47F");
		// MATHEMATICAL BOLD ITALIC CAPITAL Y 𝒀
		GLYPHS.put("u1D480", "\u1D480");
		// MATHEMATICAL BOLD ITALIC CAPITAL Z 𝒁
		GLYPHS.put("u1D481", "\u1D481");
		// MATHEMATICAL BOLD ITALIC SMALL A 𝒂
		GLYPHS.put("u1D482", "\u1D482");
		// MATHEMATICAL BOLD ITALIC SMALL B 𝒃
		GLYPHS.put("u1D483", "\u1D483");
		// MATHEMATICAL BOLD ITALIC SMALL C 𝒄
		GLYPHS.put("u1D484", "\u1D484");
		// MATHEMATICAL BOLD ITALIC SMALL D 𝒅
		GLYPHS.put("u1D485", "\u1D485");
		// MATHEMATICAL BOLD ITALIC SMALL E 𝒆
		GLYPHS.put("u1D486", "\u1D486");
		// MATHEMATICAL BOLD ITALIC SMALL F 𝒇
		GLYPHS.put("u1D487", "\u1D487");
		// MATHEMATICAL BOLD ITALIC SMALL G 𝒈
		GLYPHS.put("u1D488", "\u1D488");
		// MATHEMATICAL BOLD ITALIC SMALL H 𝒉
		GLYPHS.put("u1D489", "\u1D489");
		// MATHEMATICAL BOLD ITALIC SMALL I 𝒊
		GLYPHS.put("u1D48A", "\u1D48A");
		// MATHEMATICAL BOLD ITALIC SMALL J 𝒋
		GLYPHS.put("u1D48B", "\u1D48B");
		// MATHEMATICAL BOLD ITALIC SMALL K 𝒌
		GLYPHS.put("u1D48C", "\u1D48C");
		// MATHEMATICAL BOLD ITALIC SMALL L 𝒍
		GLYPHS.put("u1D48D", "\u1D48D");
		// MATHEMATICAL BOLD ITALIC SMALL M 𝒎
		GLYPHS.put("u1D48E", "\u1D48E");
		// MATHEMATICAL BOLD ITALIC SMALL N 𝒏
		GLYPHS.put("u1D48F", "\u1D48F");
		// MATHEMATICAL BOLD ITALIC SMALL O 𝒐
		GLYPHS.put("u1D490", "\u1D490");
		// MATHEMATICAL BOLD ITALIC SMALL P 𝒑
		GLYPHS.put("u1D491", "\u1D491");
		// MATHEMATICAL BOLD ITALIC SMALL Q 𝒒
		GLYPHS.put("u1D492", "\u1D492");
		// MATHEMATICAL BOLD ITALIC SMALL R 𝒓
		GLYPHS.put("u1D493", "\u1D493");
		// MATHEMATICAL BOLD ITALIC SMALL S 𝒔
		GLYPHS.put("u1D494", "\u1D494");
		// MATHEMATICAL BOLD ITALIC SMALL T 𝒕
		GLYPHS.put("u1D495", "\u1D495");
		// MATHEMATICAL BOLD ITALIC SMALL U 𝒖
		GLYPHS.put("u1D496", "\u1D496");
		// MATHEMATICAL BOLD ITALIC SMALL V 𝒗
		GLYPHS.put("u1D497", "\u1D497");
		// MATHEMATICAL BOLD ITALIC SMALL W 𝒘
		GLYPHS.put("u1D498", "\u1D498");
		// MATHEMATICAL BOLD ITALIC SMALL X 𝒙
		GLYPHS.put("u1D499", "\u1D499");
		// MATHEMATICAL BOLD ITALIC SMALL Y 𝒚
		GLYPHS.put("u1D49A", "\u1D49A");
		// MATHEMATICAL BOLD ITALIC SMALL Z 𝒛
		GLYPHS.put("u1D49B", "\u1D49B");
		// MATHEMATICAL SCRIPT CAPITAL A 𝒜
		GLYPHS.put("u1D49C", "\u1D49C");
		// MATHEMATICAL SCRIPT CAPITAL C 𝒞
		GLYPHS.put("u1D49E", "\u1D49E");
		// MATHEMATICAL SCRIPT CAPITAL D 𝒟
		GLYPHS.put("u1D49F", "\u1D49F");
		// MATHEMATICAL SCRIPT CAPITAL G 𝒢
		GLYPHS.put("u1D4A2", "\u1D4A2");
		// MATHEMATICAL SCRIPT CAPITAL J 𝒥
		GLYPHS.put("u1D4A5", "\u1D4A5");
		// MATHEMATICAL SCRIPT CAPITAL K 𝒦
		GLYPHS.put("u1D4A6", "\u1D4A6");
		// MATHEMATICAL SCRIPT CAPITAL N 𝒩
		GLYPHS.put("u1D4A9", "\u1D4A9");
		// MATHEMATICAL SCRIPT CAPITAL O 𝒪
		GLYPHS.put("u1D4AA", "\u1D4AA");
		// MATHEMATICAL SCRIPT CAPITAL P 𝒫
		GLYPHS.put("u1D4AB", "\u1D4AB");
		// MATHEMATICAL SCRIPT CAPITAL Q 𝒬
		GLYPHS.put("u1D4AC", "\u1D4AC");
		// MATHEMATICAL SCRIPT CAPITAL S 𝒮
		GLYPHS.put("u1D4AE", "\u1D4AE");
		// MATHEMATICAL SCRIPT CAPITAL T 𝒯
		GLYPHS.put("u1D4AF", "\u1D4AF");
		// MATHEMATICAL SCRIPT CAPITAL U 𝒰
		GLYPHS.put("u1D4B0", "\u1D4B0");
		// MATHEMATICAL SCRIPT CAPITAL V 𝒱
		GLYPHS.put("u1D4B1", "\u1D4B1");
		// MATHEMATICAL SCRIPT CAPITAL W 𝒲
		GLYPHS.put("u1D4B2", "\u1D4B2");
		// MATHEMATICAL SCRIPT CAPITAL X 𝒳
		GLYPHS.put("u1D4B3", "\u1D4B3");
		// MATHEMATICAL SCRIPT CAPITAL Y 𝒴
		GLYPHS.put("u1D4B4", "\u1D4B4");
		// MATHEMATICAL SCRIPT CAPITAL Z 𝒵
		GLYPHS.put("u1D4B5", "\u1D4B5");
		// MATHEMATICAL BOLD SCRIPT CAPITAL A 𝓐
		GLYPHS.put("u1D4D0", "\u1D4D0");
		// MATHEMATICAL BOLD SCRIPT CAPITAL B 𝓑
		GLYPHS.put("u1D4D1", "\u1D4D1");
		// MATHEMATICAL BOLD SCRIPT CAPITAL C 𝓒
		GLYPHS.put("u1D4D2", "\u1D4D2");
		// MATHEMATICAL BOLD SCRIPT CAPITAL D 𝓓
		GLYPHS.put("u1D4D3", "\u1D4D3");
		// MATHEMATICAL BOLD SCRIPT CAPITAL E 𝓔
		GLYPHS.put("u1D4D4", "\u1D4D4");
		// MATHEMATICAL BOLD SCRIPT CAPITAL F 𝓕
		GLYPHS.put("u1D4D5", "\u1D4D5");
		// MATHEMATICAL BOLD SCRIPT CAPITAL G 𝓖
		GLYPHS.put("u1D4D6", "\u1D4D6");
		// MATHEMATICAL BOLD SCRIPT CAPITAL H 𝓗
		GLYPHS.put("u1D4D7", "\u1D4D7");
		// MATHEMATICAL BOLD SCRIPT CAPITAL I 𝓘
		GLYPHS.put("u1D4D8", "\u1D4D8");
		// MATHEMATICAL BOLD SCRIPT CAPITAL J 𝓙
		GLYPHS.put("u1D4D9", "\u1D4D9");
		// MATHEMATICAL BOLD SCRIPT CAPITAL K 𝓚
		GLYPHS.put("u1D4DA", "\u1D4DA");
		// MATHEMATICAL BOLD SCRIPT CAPITAL L 𝓛
		GLYPHS.put("u1D4DB", "\u1D4DB");
		// MATHEMATICAL BOLD SCRIPT CAPITAL M 𝓜
		GLYPHS.put("u1D4DC", "\u1D4DC");
		// MATHEMATICAL BOLD SCRIPT CAPITAL N 𝓝
		GLYPHS.put("u1D4DD", "\u1D4DD");
		// MATHEMATICAL BOLD SCRIPT CAPITAL O 𝓞
		GLYPHS.put("u1D4DE", "\u1D4DE");
		// MATHEMATICAL BOLD SCRIPT CAPITAL P 𝓟
		GLYPHS.put("u1D4DF", "\u1D4DF");
		// MATHEMATICAL BOLD SCRIPT CAPITAL Q 𝓠
		GLYPHS.put("u1D4E0", "\u1D4E0");
		// MATHEMATICAL BOLD SCRIPT CAPITAL R 𝓡
		GLYPHS.put("u1D4E1", "\u1D4E1");
		// MATHEMATICAL BOLD SCRIPT CAPITAL S 𝓢
		GLYPHS.put("u1D4E2", "\u1D4E2");
		// MATHEMATICAL BOLD SCRIPT CAPITAL T 𝓣
		GLYPHS.put("u1D4E3", "\u1D4E3");
		// MATHEMATICAL BOLD SCRIPT CAPITAL U 𝓤
		GLYPHS.put("u1D4E4", "\u1D4E4");
		// MATHEMATICAL BOLD SCRIPT CAPITAL V 𝓥
		GLYPHS.put("u1D4E5", "\u1D4E5");
		// MATHEMATICAL BOLD SCRIPT CAPITAL W 𝓦
		GLYPHS.put("u1D4E6", "\u1D4E6");
		// MATHEMATICAL BOLD SCRIPT CAPITAL X 𝓧
		GLYPHS.put("u1D4E7", "\u1D4E7");
		// MATHEMATICAL BOLD SCRIPT CAPITAL Y 𝓨
		GLYPHS.put("u1D4E8", "\u1D4E8");
		// MATHEMATICAL BOLD SCRIPT CAPITAL Z 𝓩
		GLYPHS.put("u1D4E9", "\u1D4E9");
		// MATHEMATICAL FRAKTUR CAPITAL A 𝔄
		GLYPHS.put("u1D504", "\u1D504");
		// MATHEMATICAL FRAKTUR CAPITAL B 𝔅
		GLYPHS.put("u1D505", "\u1D505");
		// MATHEMATICAL FRAKTUR CAPITAL D 𝔇
		GLYPHS.put("u1D507", "\u1D507");
		// MATHEMATICAL FRAKTUR CAPITAL E 𝔈
		GLYPHS.put("u1D508", "\u1D508");
		// MATHEMATICAL FRAKTUR CAPITAL F 𝔉
		GLYPHS.put("u1D509", "\u1D509");
		// MATHEMATICAL FRAKTUR CAPITAL G 𝔊
		GLYPHS.put("u1D50A", "\u1D50A");
		// MATHEMATICAL FRAKTUR CAPITAL J 𝔍
		GLYPHS.put("u1D50D", "\u1D50D");
		// MATHEMATICAL FRAKTUR CAPITAL K 𝔎
		GLYPHS.put("u1D50E", "\u1D50E");
		// MATHEMATICAL FRAKTUR CAPITAL L 𝔏
		GLYPHS.put("u1D50F", "\u1D50F");
		// MATHEMATICAL FRAKTUR CAPITAL M 𝔐
		GLYPHS.put("u1D510", "\u1D510");
		// MATHEMATICAL FRAKTUR CAPITAL N 𝔑
		GLYPHS.put("u1D511", "\u1D511");
		// MATHEMATICAL FRAKTUR CAPITAL O 𝔒
		GLYPHS.put("u1D512", "\u1D512");
		// MATHEMATICAL FRAKTUR CAPITAL P 𝔓
		GLYPHS.put("u1D513", "\u1D513");
		// MATHEMATICAL FRAKTUR CAPITAL Q 𝔔
		GLYPHS.put("u1D514", "\u1D514");
		// MATHEMATICAL FRAKTUR CAPITAL S 𝔖
		GLYPHS.put("u1D516", "\u1D516");
		// MATHEMATICAL FRAKTUR CAPITAL T 𝔗
		GLYPHS.put("u1D517", "\u1D517");
		// MATHEMATICAL FRAKTUR CAPITAL U 𝔘
		GLYPHS.put("u1D518", "\u1D518");
		// MATHEMATICAL FRAKTUR CAPITAL V 𝔙
		GLYPHS.put("u1D519", "\u1D519");
		// MATHEMATICAL FRAKTUR CAPITAL W 𝔚
		GLYPHS.put("u1D51A", "\u1D51A");
		// MATHEMATICAL FRAKTUR CAPITAL X 𝔛
		GLYPHS.put("u1D51B", "\u1D51B");
		// MATHEMATICAL FRAKTUR CAPITAL Y 𝔜
		GLYPHS.put("u1D51C", "\u1D51C");
		// MATHEMATICAL FRAKTUR SMALL A 𝔞
		GLYPHS.put("u1D51E", "\u1D51E");
		// MATHEMATICAL FRAKTUR SMALL B 𝔟
		GLYPHS.put("u1D51F", "\u1D51F");
		// MATHEMATICAL FRAKTUR SMALL C 𝔠
		GLYPHS.put("u1D520", "\u1D520");
		// MATHEMATICAL FRAKTUR SMALL D 𝔡
		GLYPHS.put("u1D521", "\u1D521");
		// MATHEMATICAL FRAKTUR SMALL E 𝔢
		GLYPHS.put("u1D522", "\u1D522");
		// MATHEMATICAL FRAKTUR SMALL F 𝔣
		GLYPHS.put("u1D523", "\u1D523");
		// MATHEMATICAL FRAKTUR SMALL G 𝔤
		GLYPHS.put("u1D524", "\u1D524");
		// MATHEMATICAL FRAKTUR SMALL H 𝔥
		GLYPHS.put("u1D525", "\u1D525");
		// MATHEMATICAL FRAKTUR SMALL I 𝔦
		GLYPHS.put("u1D526", "\u1D526");
		// MATHEMATICAL FRAKTUR SMALL J 𝔧
		GLYPHS.put("u1D527", "\u1D527");
		// MATHEMATICAL FRAKTUR SMALL K 𝔨
		GLYPHS.put("u1D528", "\u1D528");
		// MATHEMATICAL FRAKTUR SMALL L 𝔩
		GLYPHS.put("u1D529", "\u1D529");
		// MATHEMATICAL FRAKTUR SMALL M 𝔪
		GLYPHS.put("u1D52A", "\u1D52A");
		// MATHEMATICAL FRAKTUR SMALL N 𝔫
		GLYPHS.put("u1D52B", "\u1D52B");
		// MATHEMATICAL FRAKTUR SMALL O 𝔬
		GLYPHS.put("u1D52C", "\u1D52C");
		// MATHEMATICAL FRAKTUR SMALL P 𝔭
		GLYPHS.put("u1D52D", "\u1D52D");
		// MATHEMATICAL FRAKTUR SMALL Q 𝔮
		GLYPHS.put("u1D52E", "\u1D52E");
		// MATHEMATICAL FRAKTUR SMALL R 𝔯
		GLYPHS.put("u1D52F", "\u1D52F");
		// MATHEMATICAL FRAKTUR SMALL S 𝔰
		GLYPHS.put("u1D530", "\u1D530");
		// MATHEMATICAL FRAKTUR SMALL T 𝔱
		GLYPHS.put("u1D531", "\u1D531");
		// MATHEMATICAL FRAKTUR SMALL U 𝔲
		GLYPHS.put("u1D532", "\u1D532");
		// MATHEMATICAL FRAKTUR SMALL V 𝔳
		GLYPHS.put("u1D533", "\u1D533");
		// MATHEMATICAL FRAKTUR SMALL W 𝔴
		GLYPHS.put("u1D534", "\u1D534");
		// MATHEMATICAL FRAKTUR SMALL X 𝔵
		GLYPHS.put("u1D535", "\u1D535");
		// MATHEMATICAL FRAKTUR SMALL Y 𝔶
		GLYPHS.put("u1D536", "\u1D536");
		// MATHEMATICAL FRAKTUR SMALL Z 𝔷
		GLYPHS.put("u1D537", "\u1D537");
		// MATHEMATICAL DOUBLE-STRUCK CAPITAL A 𝔸
		GLYPHS.put("u1D538", "\u1D538");
		// MATHEMATICAL DOUBLE-STRUCK CAPITAL B 𝔹
		GLYPHS.put("u1D539", "\u1D539");
		// MATHEMATICAL DOUBLE-STRUCK CAPITAL D 𝔻
		GLYPHS.put("u1D53B", "\u1D53B");
		// MATHEMATICAL DOUBLE-STRUCK CAPITAL E 𝔼
		GLYPHS.put("u1D53C", "\u1D53C");
		// MATHEMATICAL DOUBLE-STRUCK CAPITAL F 𝔽
		GLYPHS.put("u1D53D", "\u1D53D");
		// MATHEMATICAL DOUBLE-STRUCK CAPITAL G 𝔾
		GLYPHS.put("u1D53E", "\u1D53E");
		// MATHEMATICAL DOUBLE-STRUCK CAPITAL I 𝕀
		GLYPHS.put("u1D540", "\u1D540");
		// MATHEMATICAL DOUBLE-STRUCK CAPITAL J 𝕁
		GLYPHS.put("u1D541", "\u1D541");
		// MATHEMATICAL DOUBLE-STRUCK CAPITAL K 𝕂
		GLYPHS.put("u1D542", "\u1D542");
		// MATHEMATICAL DOUBLE-STRUCK CAPITAL L 𝕃
		GLYPHS.put("u1D543", "\u1D543");
		// MATHEMATICAL DOUBLE-STRUCK CAPITAL M 𝕄
		GLYPHS.put("u1D544", "\u1D544");
		// MATHEMATICAL DOUBLE-STRUCK CAPITAL O 𝕆
		GLYPHS.put("u1D546", "\u1D546");
		// MATHEMATICAL DOUBLE-STRUCK CAPITAL S 𝕊
		GLYPHS.put("u1D54A", "\u1D54A");
		// MATHEMATICAL DOUBLE-STRUCK CAPITAL T 𝕋
		GLYPHS.put("u1D54B", "\u1D54B");
		// MATHEMATICAL DOUBLE-STRUCK CAPITAL U 𝕌
		GLYPHS.put("u1D54C", "\u1D54C");
		// MATHEMATICAL DOUBLE-STRUCK CAPITAL V 𝕍
		GLYPHS.put("u1D54D", "\u1D54D");
		// MATHEMATICAL DOUBLE-STRUCK CAPITAL W 𝕎
		GLYPHS.put("u1D54E", "\u1D54E");
		// MATHEMATICAL DOUBLE-STRUCK CAPITAL X 𝕏
		GLYPHS.put("u1D54F", "\u1D54F");
		// MATHEMATICAL DOUBLE-STRUCK CAPITAL Y 𝕐
		GLYPHS.put("u1D550", "\u1D550");
		// MATHEMATICAL DOUBLE-STRUCK SMALL A 𝕒
		GLYPHS.put("u1D552", "\u1D552");
		// MATHEMATICAL DOUBLE-STRUCK SMALL B 𝕓
		GLYPHS.put("u1D553", "\u1D553");
		// MATHEMATICAL DOUBLE-STRUCK SMALL C 𝕔
		GLYPHS.put("u1D554", "\u1D554");
		// MATHEMATICAL DOUBLE-STRUCK SMALL D 𝕕
		GLYPHS.put("u1D555", "\u1D555");
		// MATHEMATICAL DOUBLE-STRUCK SMALL E 𝕖
		GLYPHS.put("u1D556", "\u1D556");
		// MATHEMATICAL DOUBLE-STRUCK SMALL F 𝕗
		GLYPHS.put("u1D557", "\u1D557");
		// MATHEMATICAL DOUBLE-STRUCK SMALL G 𝕘
		GLYPHS.put("u1D558", "\u1D558");
		// MATHEMATICAL DOUBLE-STRUCK SMALL H 𝕙
		GLYPHS.put("u1D559", "\u1D559");
		// MATHEMATICAL DOUBLE-STRUCK SMALL I 𝕚
		GLYPHS.put("u1D55A", "\u1D55A");
		// MATHEMATICAL DOUBLE-STRUCK SMALL J 𝕛
		GLYPHS.put("u1D55B", "\u1D55B");
		// MATHEMATICAL DOUBLE-STRUCK SMALL K 𝕜
		GLYPHS.put("u1D55C", "\u1D55C");
		// MATHEMATICAL DOUBLE-STRUCK SMALL L 𝕝
		GLYPHS.put("u1D55D", "\u1D55D");
		// MATHEMATICAL DOUBLE-STRUCK SMALL M 𝕞
		GLYPHS.put("u1D55E", "\u1D55E");
		// MATHEMATICAL DOUBLE-STRUCK SMALL N 𝕟
		GLYPHS.put("u1D55F", "\u1D55F");
		// MATHEMATICAL DOUBLE-STRUCK SMALL O 𝕠
		GLYPHS.put("u1D560", "\u1D560");
		// MATHEMATICAL DOUBLE-STRUCK SMALL P 𝕡
		GLYPHS.put("u1D561", "\u1D561");
		// MATHEMATICAL DOUBLE-STRUCK SMALL Q 𝕢
		GLYPHS.put("u1D562", "\u1D562");
		// MATHEMATICAL DOUBLE-STRUCK SMALL R 𝕣
		GLYPHS.put("u1D563", "\u1D563");
		// MATHEMATICAL DOUBLE-STRUCK SMALL S 𝕤
		GLYPHS.put("u1D564", "\u1D564");
		// MATHEMATICAL DOUBLE-STRUCK SMALL T 𝕥
		GLYPHS.put("u1D565", "\u1D565");
		// MATHEMATICAL DOUBLE-STRUCK SMALL U 𝕦
		GLYPHS.put("u1D566", "\u1D566");
		// MATHEMATICAL DOUBLE-STRUCK SMALL V 𝕧
		GLYPHS.put("u1D567", "\u1D567");
		// MATHEMATICAL DOUBLE-STRUCK SMALL W 𝕨
		GLYPHS.put("u1D568", "\u1D568");
		// MATHEMATICAL DOUBLE-STRUCK SMALL X 𝕩
		GLYPHS.put("u1D569", "\u1D569");
		// MATHEMATICAL DOUBLE-STRUCK SMALL Y 𝕪
		GLYPHS.put("u1D56A", "\u1D56A");
		// MATHEMATICAL DOUBLE-STRUCK SMALL Z 𝕫
		GLYPHS.put("u1D56B", "\u1D56B");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL A 𝕬
		GLYPHS.put("u1D56C", "\u1D56C");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL B 𝕭
		GLYPHS.put("u1D56D", "\u1D56D");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL C 𝕮
		GLYPHS.put("u1D56E", "\u1D56E");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL D 𝕯
		GLYPHS.put("u1D56F", "\u1D56F");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL E 𝕰
		GLYPHS.put("u1D570", "\u1D570");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL F 𝕱
		GLYPHS.put("u1D571", "\u1D571");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL G 𝕲
		GLYPHS.put("u1D572", "\u1D572");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL H 𝕳
		GLYPHS.put("u1D573", "\u1D573");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL I 𝕴
		GLYPHS.put("u1D574", "\u1D574");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL J 𝕵
		GLYPHS.put("u1D575", "\u1D575");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL K 𝕶
		GLYPHS.put("u1D576", "\u1D576");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL L 𝕷
		GLYPHS.put("u1D577", "\u1D577");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL M 𝕸
		GLYPHS.put("u1D578", "\u1D578");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL N 𝕹
		GLYPHS.put("u1D579", "\u1D579");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL O 𝕺
		GLYPHS.put("u1D57A", "\u1D57A");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL P 𝕻
		GLYPHS.put("u1D57B", "\u1D57B");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL Q 𝕼
		GLYPHS.put("u1D57C", "\u1D57C");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL R 𝕽
		GLYPHS.put("u1D57D", "\u1D57D");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL S 𝕾
		GLYPHS.put("u1D57E", "\u1D57E");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL T 𝕿
		GLYPHS.put("u1D57F", "\u1D57F");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL U 𝖀
		GLYPHS.put("u1D580", "\u1D580");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL V 𝖁
		GLYPHS.put("u1D581", "\u1D581");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL W 𝖂
		GLYPHS.put("u1D582", "\u1D582");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL X 𝖃
		GLYPHS.put("u1D583", "\u1D583");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL Y 𝖄
		GLYPHS.put("u1D584", "\u1D584");
		// MATHEMATICAL BOLD FRAKTUR CAPITAL Z 𝖅
		GLYPHS.put("u1D585", "\u1D585");
		// MATHEMATICAL BOLD FRAKTUR SMALL A 𝖆
		GLYPHS.put("u1D586", "\u1D586");
		// MATHEMATICAL BOLD FRAKTUR SMALL B 𝖇
		GLYPHS.put("u1D587", "\u1D587");
		// MATHEMATICAL BOLD FRAKTUR SMALL C 𝖈
		GLYPHS.put("u1D588", "\u1D588");
		// MATHEMATICAL BOLD FRAKTUR SMALL D 𝖉
		GLYPHS.put("u1D589", "\u1D589");
		// MATHEMATICAL BOLD FRAKTUR SMALL E 𝖊
		GLYPHS.put("u1D58A", "\u1D58A");
		// MATHEMATICAL BOLD FRAKTUR SMALL F 𝖋
		GLYPHS.put("u1D58B", "\u1D58B");
		// MATHEMATICAL BOLD FRAKTUR SMALL G 𝖌
		GLYPHS.put("u1D58C", "\u1D58C");
		// MATHEMATICAL BOLD FRAKTUR SMALL H 𝖍
		GLYPHS.put("u1D58D", "\u1D58D");
		// MATHEMATICAL BOLD FRAKTUR SMALL I 𝖎
		GLYPHS.put("u1D58E", "\u1D58E");
		// MATHEMATICAL BOLD FRAKTUR SMALL J 𝖏
		GLYPHS.put("u1D58F", "\u1D58F");
		// MATHEMATICAL BOLD FRAKTUR SMALL K 𝖐
		GLYPHS.put("u1D590", "\u1D590");
		// MATHEMATICAL BOLD FRAKTUR SMALL L 𝖑
		GLYPHS.put("u1D591", "\u1D591");
		// MATHEMATICAL BOLD FRAKTUR SMALL M 𝖒
		GLYPHS.put("u1D592", "\u1D592");
		// MATHEMATICAL BOLD FRAKTUR SMALL N 𝖓
		GLYPHS.put("u1D593", "\u1D593");
		// MATHEMATICAL BOLD FRAKTUR SMALL O 𝖔
		GLYPHS.put("u1D594", "\u1D594");
		// MATHEMATICAL BOLD FRAKTUR SMALL P 𝖕
		GLYPHS.put("u1D595", "\u1D595");
		// MATHEMATICAL BOLD FRAKTUR SMALL Q 𝖖
		GLYPHS.put("u1D596", "\u1D596");
		// MATHEMATICAL BOLD FRAKTUR SMALL R 𝖗
		GLYPHS.put("u1D597", "\u1D597");
		// MATHEMATICAL BOLD FRAKTUR SMALL S 𝖘
		GLYPHS.put("u1D598", "\u1D598");
		// MATHEMATICAL BOLD FRAKTUR SMALL T 𝖙
		GLYPHS.put("u1D599", "\u1D599");
		// MATHEMATICAL BOLD FRAKTUR SMALL U 𝖚
		GLYPHS.put("u1D59A", "\u1D59A");
		// MATHEMATICAL BOLD FRAKTUR SMALL V 𝖛
		GLYPHS.put("u1D59B", "\u1D59B");
		// MATHEMATICAL BOLD FRAKTUR SMALL W 𝖜
		GLYPHS.put("u1D59C", "\u1D59C");
		// MATHEMATICAL BOLD FRAKTUR SMALL X 𝖝
		GLYPHS.put("u1D59D", "\u1D59D");
		// MATHEMATICAL BOLD FRAKTUR SMALL Y 𝖞
		GLYPHS.put("u1D59E", "\u1D59E");
		// MATHEMATICAL BOLD FRAKTUR SMALL Z 𝖟
		GLYPHS.put("u1D59F", "\u1D59F");
		// MATHEMATICAL SANS-SERIF CAPITAL A 𝖠
		GLYPHS.put("u1D5A0", "\u1D5A0");
		// MATHEMATICAL SANS-SERIF CAPITAL B 𝖡
		GLYPHS.put("u1D5A1", "\u1D5A1");
		// MATHEMATICAL SANS-SERIF CAPITAL C 𝖢
		GLYPHS.put("u1D5A2", "\u1D5A2");
		// MATHEMATICAL SANS-SERIF CAPITAL D 𝖣
		GLYPHS.put("u1D5A3", "\u1D5A3");
		// MATHEMATICAL SANS-SERIF CAPITAL E 𝖤
		GLYPHS.put("u1D5A4", "\u1D5A4");
		// MATHEMATICAL SANS-SERIF CAPITAL F 𝖥
		GLYPHS.put("u1D5A5", "\u1D5A5");
		// MATHEMATICAL SANS-SERIF CAPITAL G 𝖦
		GLYPHS.put("u1D5A6", "\u1D5A6");
		// MATHEMATICAL SANS-SERIF CAPITAL H 𝖧
		GLYPHS.put("u1D5A7", "\u1D5A7");
		// MATHEMATICAL SANS-SERIF CAPITAL I 𝖨
		GLYPHS.put("u1D5A8", "\u1D5A8");
		// MATHEMATICAL SANS-SERIF CAPITAL J 𝖩
		GLYPHS.put("u1D5A9", "\u1D5A9");
		// MATHEMATICAL SANS-SERIF CAPITAL K 𝖪
		GLYPHS.put("u1D5AA", "\u1D5AA");
		// MATHEMATICAL SANS-SERIF CAPITAL L 𝖫
		GLYPHS.put("u1D5AB", "\u1D5AB");
		// MATHEMATICAL SANS-SERIF CAPITAL M 𝖬
		GLYPHS.put("u1D5AC", "\u1D5AC");
		// MATHEMATICAL SANS-SERIF CAPITAL N 𝖭
		GLYPHS.put("u1D5AD", "\u1D5AD");
		// MATHEMATICAL SANS-SERIF CAPITAL O 𝖮
		GLYPHS.put("u1D5AE", "\u1D5AE");
		// MATHEMATICAL SANS-SERIF CAPITAL P 𝖯
		GLYPHS.put("u1D5AF", "\u1D5AF");
		// MATHEMATICAL SANS-SERIF CAPITAL Q 𝖰
		GLYPHS.put("u1D5B0", "\u1D5B0");
		// MATHEMATICAL SANS-SERIF CAPITAL R 𝖱
		GLYPHS.put("u1D5B1", "\u1D5B1");
		// MATHEMATICAL SANS-SERIF CAPITAL S 𝖲
		GLYPHS.put("u1D5B2", "\u1D5B2");
		// MATHEMATICAL SANS-SERIF CAPITAL T 𝖳
		GLYPHS.put("u1D5B3", "\u1D5B3");
		// MATHEMATICAL SANS-SERIF CAPITAL U 𝖴
		GLYPHS.put("u1D5B4", "\u1D5B4");
		// MATHEMATICAL SANS-SERIF CAPITAL V 𝖵
		GLYPHS.put("u1D5B5", "\u1D5B5");
		// MATHEMATICAL SANS-SERIF CAPITAL W 𝖶
		GLYPHS.put("u1D5B6", "\u1D5B6");
		// MATHEMATICAL SANS-SERIF CAPITAL X 𝖷
		GLYPHS.put("u1D5B7", "\u1D5B7");
		// MATHEMATICAL SANS-SERIF CAPITAL Y 𝖸
		GLYPHS.put("u1D5B8", "\u1D5B8");
		// MATHEMATICAL SANS-SERIF CAPITAL Z 𝖹
		GLYPHS.put("u1D5B9", "\u1D5B9");
		// MATHEMATICAL SANS-SERIF SMALL A 𝖺
		GLYPHS.put("u1D5BA", "\u1D5BA");
		// MATHEMATICAL SANS-SERIF SMALL B 𝖻
		GLYPHS.put("u1D5BB", "\u1D5BB");
		// MATHEMATICAL SANS-SERIF SMALL C 𝖼
		GLYPHS.put("u1D5BC", "\u1D5BC");
		// MATHEMATICAL SANS-SERIF SMALL D 𝖽
		GLYPHS.put("u1D5BD", "\u1D5BD");
		// MATHEMATICAL SANS-SERIF SMALL E 𝖾
		GLYPHS.put("u1D5BE", "\u1D5BE");
		// MATHEMATICAL SANS-SERIF SMALL F 𝖿
		GLYPHS.put("u1D5BF", "\u1D5BF");
		// MATHEMATICAL SANS-SERIF SMALL G 𝗀
		GLYPHS.put("u1D5C0", "\u1D5C0");
		// MATHEMATICAL SANS-SERIF SMALL H 𝗁
		GLYPHS.put("u1D5C1", "\u1D5C1");
		// MATHEMATICAL SANS-SERIF SMALL I 𝗂
		GLYPHS.put("u1D5C2", "\u1D5C2");
		// MATHEMATICAL SANS-SERIF SMALL J 𝗃
		GLYPHS.put("u1D5C3", "\u1D5C3");
		// MATHEMATICAL SANS-SERIF SMALL K 𝗄
		GLYPHS.put("u1D5C4", "\u1D5C4");
		// MATHEMATICAL SANS-SERIF SMALL L 𝗅
		GLYPHS.put("u1D5C5", "\u1D5C5");
		// MATHEMATICAL SANS-SERIF SMALL M 𝗆
		GLYPHS.put("u1D5C6", "\u1D5C6");
		// MATHEMATICAL SANS-SERIF SMALL N 𝗇
		GLYPHS.put("u1D5C7", "\u1D5C7");
		// MATHEMATICAL SANS-SERIF SMALL O 𝗈
		GLYPHS.put("u1D5C8", "\u1D5C8");
		// MATHEMATICAL SANS-SERIF SMALL P 𝗉
		GLYPHS.put("u1D5C9", "\u1D5C9");
		// MATHEMATICAL SANS-SERIF SMALL Q 𝗊
		GLYPHS.put("u1D5CA", "\u1D5CA");
		// MATHEMATICAL SANS-SERIF SMALL R 𝗋
		GLYPHS.put("u1D5CB", "\u1D5CB");
		// MATHEMATICAL SANS-SERIF SMALL S 𝗌
		GLYPHS.put("u1D5CC", "\u1D5CC");
		// MATHEMATICAL SANS-SERIF SMALL T 𝗍
		GLYPHS.put("u1D5CD", "\u1D5CD");
		// MATHEMATICAL SANS-SERIF SMALL U 𝗎
		GLYPHS.put("u1D5CE", "\u1D5CE");
		// MATHEMATICAL SANS-SERIF SMALL V 𝗏
		GLYPHS.put("u1D5CF", "\u1D5CF");
		// MATHEMATICAL SANS-SERIF SMALL W 𝗐
		GLYPHS.put("u1D5D0", "\u1D5D0");
		// MATHEMATICAL SANS-SERIF SMALL X 𝗑
		GLYPHS.put("u1D5D1", "\u1D5D1");
		// MATHEMATICAL SANS-SERIF SMALL Y 𝗒
		GLYPHS.put("u1D5D2", "\u1D5D2");
		// MATHEMATICAL SANS-SERIF SMALL Z 𝗓
		GLYPHS.put("u1D5D3", "\u1D5D3");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL A 𝗔
		GLYPHS.put("u1D5D4", "\u1D5D4");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL B 𝗕
		GLYPHS.put("u1D5D5", "\u1D5D5");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL C 𝗖
		GLYPHS.put("u1D5D6", "\u1D5D6");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL D 𝗗
		GLYPHS.put("u1D5D7", "\u1D5D7");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL E 𝗘
		GLYPHS.put("u1D5D8", "\u1D5D8");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL F 𝗙
		GLYPHS.put("u1D5D9", "\u1D5D9");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL G 𝗚
		GLYPHS.put("u1D5DA", "\u1D5DA");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL H 𝗛
		GLYPHS.put("u1D5DB", "\u1D5DB");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL I 𝗜
		GLYPHS.put("u1D5DC", "\u1D5DC");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL J 𝗝
		GLYPHS.put("u1D5DD", "\u1D5DD");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL K 𝗞
		GLYPHS.put("u1D5DE", "\u1D5DE");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL L 𝗟
		GLYPHS.put("u1D5DF", "\u1D5DF");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL M 𝗠
		GLYPHS.put("u1D5E0", "\u1D5E0");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL N 𝗡
		GLYPHS.put("u1D5E1", "\u1D5E1");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL O 𝗢
		GLYPHS.put("u1D5E2", "\u1D5E2");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL P 𝗣
		GLYPHS.put("u1D5E3", "\u1D5E3");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL Q 𝗤
		GLYPHS.put("u1D5E4", "\u1D5E4");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL R 𝗥
		GLYPHS.put("u1D5E5", "\u1D5E5");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL S 𝗦
		GLYPHS.put("u1D5E6", "\u1D5E6");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL T 𝗧
		GLYPHS.put("u1D5E7", "\u1D5E7");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL U 𝗨
		GLYPHS.put("u1D5E8", "\u1D5E8");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL V 𝗩
		GLYPHS.put("u1D5E9", "\u1D5E9");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL W 𝗪
		GLYPHS.put("u1D5EA", "\u1D5EA");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL X 𝗫
		GLYPHS.put("u1D5EB", "\u1D5EB");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL Y 𝗬
		GLYPHS.put("u1D5EC", "\u1D5EC");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL Z 𝗭
		GLYPHS.put("u1D5ED", "\u1D5ED");
		// MATHEMATICAL SANS-SERIF BOLD SMALL A 𝗮
		GLYPHS.put("u1D5EE", "\u1D5EE");
		// MATHEMATICAL SANS-SERIF BOLD SMALL B 𝗯
		GLYPHS.put("u1D5EF", "\u1D5EF");
		// MATHEMATICAL SANS-SERIF BOLD SMALL C 𝗰
		GLYPHS.put("u1D5F0", "\u1D5F0");
		// MATHEMATICAL SANS-SERIF BOLD SMALL D 𝗱
		GLYPHS.put("u1D5F1", "\u1D5F1");
		// MATHEMATICAL SANS-SERIF BOLD SMALL E 𝗲
		GLYPHS.put("u1D5F2", "\u1D5F2");
		// MATHEMATICAL SANS-SERIF BOLD SMALL F 𝗳
		GLYPHS.put("u1D5F3", "\u1D5F3");
		// MATHEMATICAL SANS-SERIF BOLD SMALL G 𝗴
		GLYPHS.put("u1D5F4", "\u1D5F4");
		// MATHEMATICAL SANS-SERIF BOLD SMALL H 𝗵
		GLYPHS.put("u1D5F5", "\u1D5F5");
		// MATHEMATICAL SANS-SERIF BOLD SMALL I 𝗶
		GLYPHS.put("u1D5F6", "\u1D5F6");
		// MATHEMATICAL SANS-SERIF BOLD SMALL J 𝗷
		GLYPHS.put("u1D5F7", "\u1D5F7");
		// MATHEMATICAL SANS-SERIF BOLD SMALL K 𝗸
		GLYPHS.put("u1D5F8", "\u1D5F8");
		// MATHEMATICAL SANS-SERIF BOLD SMALL L 𝗹
		GLYPHS.put("u1D5F9", "\u1D5F9");
		// MATHEMATICAL SANS-SERIF BOLD SMALL M 𝗺
		GLYPHS.put("u1D5FA", "\u1D5FA");
		// MATHEMATICAL SANS-SERIF BOLD SMALL N 𝗻
		GLYPHS.put("u1D5FB", "\u1D5FB");
		// MATHEMATICAL SANS-SERIF BOLD SMALL O 𝗼
		GLYPHS.put("u1D5FC", "\u1D5FC");
		// MATHEMATICAL SANS-SERIF BOLD SMALL P 𝗽
		GLYPHS.put("u1D5FD", "\u1D5FD");
		// MATHEMATICAL SANS-SERIF BOLD SMALL Q 𝗾
		GLYPHS.put("u1D5FE", "\u1D5FE");
		// MATHEMATICAL SANS-SERIF BOLD SMALL R 𝗿
		GLYPHS.put("u1D5FF", "\u1D5FF");
		// MATHEMATICAL SANS-SERIF BOLD SMALL S 𝘀
		GLYPHS.put("u1D600", "\u1D600");
		// MATHEMATICAL SANS-SERIF BOLD SMALL T 𝘁
		GLYPHS.put("u1D601", "\u1D601");
		// MATHEMATICAL SANS-SERIF BOLD SMALL U 𝘂
		GLYPHS.put("u1D602", "\u1D602");
		// MATHEMATICAL SANS-SERIF BOLD SMALL V 𝘃
		GLYPHS.put("u1D603", "\u1D603");
		// MATHEMATICAL SANS-SERIF BOLD SMALL W 𝘄
		GLYPHS.put("u1D604", "\u1D604");
		// MATHEMATICAL SANS-SERIF BOLD SMALL X 𝘅
		GLYPHS.put("u1D605", "\u1D605");
		// MATHEMATICAL SANS-SERIF BOLD SMALL Y 𝘆
		GLYPHS.put("u1D606", "\u1D606");
		// MATHEMATICAL SANS-SERIF BOLD SMALL Z 𝘇
		GLYPHS.put("u1D607", "\u1D607");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL A 𝘈
		GLYPHS.put("u1D608", "\u1D608");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL B 𝘉
		GLYPHS.put("u1D609", "\u1D609");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL C 𝘊
		GLYPHS.put("u1D60A", "\u1D60A");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL D 𝘋
		GLYPHS.put("u1D60B", "\u1D60B");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL E 𝘌
		GLYPHS.put("u1D60C", "\u1D60C");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL F 𝘍
		GLYPHS.put("u1D60D", "\u1D60D");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL G 𝘎
		GLYPHS.put("u1D60E", "\u1D60E");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL H 𝘏
		GLYPHS.put("u1D60F", "\u1D60F");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL I 𝘐
		GLYPHS.put("u1D610", "\u1D610");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL J 𝘑
		GLYPHS.put("u1D611", "\u1D611");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL K 𝘒
		GLYPHS.put("u1D612", "\u1D612");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL L 𝘓
		GLYPHS.put("u1D613", "\u1D613");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL M 𝘔
		GLYPHS.put("u1D614", "\u1D614");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL N 𝘕
		GLYPHS.put("u1D615", "\u1D615");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL O 𝘖
		GLYPHS.put("u1D616", "\u1D616");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL P 𝘗
		GLYPHS.put("u1D617", "\u1D617");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL Q 𝘘
		GLYPHS.put("u1D618", "\u1D618");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL R 𝘙
		GLYPHS.put("u1D619", "\u1D619");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL S 𝘚
		GLYPHS.put("u1D61A", "\u1D61A");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL T 𝘛
		GLYPHS.put("u1D61B", "\u1D61B");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL U 𝘜
		GLYPHS.put("u1D61C", "\u1D61C");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL V 𝘝
		GLYPHS.put("u1D61D", "\u1D61D");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL W 𝘞
		GLYPHS.put("u1D61E", "\u1D61E");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL X 𝘟
		GLYPHS.put("u1D61F", "\u1D61F");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL Y 𝘠
		GLYPHS.put("u1D620", "\u1D620");
		// MATHEMATICAL SANS-SERIF ITALIC CAPITAL Z 𝘡
		GLYPHS.put("u1D621", "\u1D621");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL A 𝘢
		GLYPHS.put("u1D622", "\u1D622");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL B 𝘣
		GLYPHS.put("u1D623", "\u1D623");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL C 𝘤
		GLYPHS.put("u1D624", "\u1D624");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL D 𝘥
		GLYPHS.put("u1D625", "\u1D625");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL E 𝘦
		GLYPHS.put("u1D626", "\u1D626");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL F 𝘧
		GLYPHS.put("u1D627", "\u1D627");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL G 𝘨
		GLYPHS.put("u1D628", "\u1D628");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL H 𝘩
		GLYPHS.put("u1D629", "\u1D629");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL I 𝘪
		GLYPHS.put("u1D62A", "\u1D62A");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL J 𝘫
		GLYPHS.put("u1D62B", "\u1D62B");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL K 𝘬
		GLYPHS.put("u1D62C", "\u1D62C");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL L 𝘭
		GLYPHS.put("u1D62D", "\u1D62D");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL M 𝘮
		GLYPHS.put("u1D62E", "\u1D62E");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL N 𝘯
		GLYPHS.put("u1D62F", "\u1D62F");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL O 𝘰
		GLYPHS.put("u1D630", "\u1D630");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL P 𝘱
		GLYPHS.put("u1D631", "\u1D631");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL Q 𝘲
		GLYPHS.put("u1D632", "\u1D632");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL R 𝘳
		GLYPHS.put("u1D633", "\u1D633");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL S 𝘴
		GLYPHS.put("u1D634", "\u1D634");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL T 𝘵
		GLYPHS.put("u1D635", "\u1D635");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL U 𝘶
		GLYPHS.put("u1D636", "\u1D636");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL V 𝘷
		GLYPHS.put("u1D637", "\u1D637");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL W 𝘸
		GLYPHS.put("u1D638", "\u1D638");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL X 𝘹
		GLYPHS.put("u1D639", "\u1D639");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL Y 𝘺
		GLYPHS.put("u1D63A", "\u1D63A");
		// MATHEMATICAL SANS-SERIF ITALIC SMALL Z 𝘻
		GLYPHS.put("u1D63B", "\u1D63B");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL A 𝘼
		GLYPHS.put("u1D63C", "\u1D63C");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL B 𝘽
		GLYPHS.put("u1D63D", "\u1D63D");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL C 𝘾
		GLYPHS.put("u1D63E", "\u1D63E");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL D 𝘿
		GLYPHS.put("u1D63F", "\u1D63F");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL E 𝙀
		GLYPHS.put("u1D640", "\u1D640");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL F 𝙁
		GLYPHS.put("u1D641", "\u1D641");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL G 𝙂
		GLYPHS.put("u1D642", "\u1D642");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL H 𝙃
		GLYPHS.put("u1D643", "\u1D643");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL I 𝙄
		GLYPHS.put("u1D644", "\u1D644");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL J 𝙅
		GLYPHS.put("u1D645", "\u1D645");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL K 𝙆
		GLYPHS.put("u1D646", "\u1D646");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL L 𝙇
		GLYPHS.put("u1D647", "\u1D647");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL M 𝙈
		GLYPHS.put("u1D648", "\u1D648");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL N 𝙉
		GLYPHS.put("u1D649", "\u1D649");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL O 𝙊
		GLYPHS.put("u1D64A", "\u1D64A");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL P 𝙋
		GLYPHS.put("u1D64B", "\u1D64B");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL Q 𝙌
		GLYPHS.put("u1D64C", "\u1D64C");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL R 𝙍
		GLYPHS.put("u1D64D", "\u1D64D");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL S 𝙎
		GLYPHS.put("u1D64E", "\u1D64E");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL T 𝙏
		GLYPHS.put("u1D64F", "\u1D64F");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL U 𝙐
		GLYPHS.put("u1D650", "\u1D650");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL V 𝙑
		GLYPHS.put("u1D651", "\u1D651");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL W 𝙒
		GLYPHS.put("u1D652", "\u1D652");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL X 𝙓
		GLYPHS.put("u1D653", "\u1D653");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL Y 𝙔
		GLYPHS.put("u1D654", "\u1D654");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL Z 𝙕
		GLYPHS.put("u1D655", "\u1D655");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL A 𝙖
		GLYPHS.put("u1D656", "\u1D656");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL B 𝙗
		GLYPHS.put("u1D657", "\u1D657");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL C 𝙘
		GLYPHS.put("u1D658", "\u1D658");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL D 𝙙
		GLYPHS.put("u1D659", "\u1D659");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL E 𝙚
		GLYPHS.put("u1D65A", "\u1D65A");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL F 𝙛
		GLYPHS.put("u1D65B", "\u1D65B");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL G 𝙜
		GLYPHS.put("u1D65C", "\u1D65C");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL H 𝙝
		GLYPHS.put("u1D65D", "\u1D65D");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL I 𝙞
		GLYPHS.put("u1D65E", "\u1D65E");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL J 𝙟
		GLYPHS.put("u1D65F", "\u1D65F");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL K 𝙠
		GLYPHS.put("u1D660", "\u1D660");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL L 𝙡
		GLYPHS.put("u1D661", "\u1D661");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL M 𝙢
		GLYPHS.put("u1D662", "\u1D662");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL N 𝙣
		GLYPHS.put("u1D663", "\u1D663");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL O 𝙤
		GLYPHS.put("u1D664", "\u1D664");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL P 𝙥
		GLYPHS.put("u1D665", "\u1D665");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL Q 𝙦
		GLYPHS.put("u1D666", "\u1D666");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL R 𝙧
		GLYPHS.put("u1D667", "\u1D667");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL S 𝙨
		GLYPHS.put("u1D668", "\u1D668");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL T 𝙩
		GLYPHS.put("u1D669", "\u1D669");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL U 𝙪
		GLYPHS.put("u1D66A", "\u1D66A");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL V 𝙫
		GLYPHS.put("u1D66B", "\u1D66B");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL W 𝙬
		GLYPHS.put("u1D66C", "\u1D66C");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL X 𝙭
		GLYPHS.put("u1D66D", "\u1D66D");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL Y 𝙮
		GLYPHS.put("u1D66E", "\u1D66E");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL Z 𝙯
		GLYPHS.put("u1D66F", "\u1D66F");
		// MATHEMATICAL MONOSPACE CAPITAL A 𝙰
		GLYPHS.put("u1D670", "\u1D670");
		// MATHEMATICAL MONOSPACE CAPITAL B 𝙱
		GLYPHS.put("u1D671", "\u1D671");
		// MATHEMATICAL MONOSPACE CAPITAL C 𝙲
		GLYPHS.put("u1D672", "\u1D672");
		// MATHEMATICAL MONOSPACE CAPITAL D 𝙳
		GLYPHS.put("u1D673", "\u1D673");
		// MATHEMATICAL MONOSPACE CAPITAL E 𝙴
		GLYPHS.put("u1D674", "\u1D674");
		// MATHEMATICAL MONOSPACE CAPITAL F 𝙵
		GLYPHS.put("u1D675", "\u1D675");
		// MATHEMATICAL MONOSPACE CAPITAL G 𝙶
		GLYPHS.put("u1D676", "\u1D676");
		// MATHEMATICAL MONOSPACE CAPITAL H 𝙷
		GLYPHS.put("u1D677", "\u1D677");
		// MATHEMATICAL MONOSPACE CAPITAL I 𝙸
		GLYPHS.put("u1D678", "\u1D678");
		// MATHEMATICAL MONOSPACE CAPITAL J 𝙹
		GLYPHS.put("u1D679", "\u1D679");
		// MATHEMATICAL MONOSPACE CAPITAL K 𝙺
		GLYPHS.put("u1D67A", "\u1D67A");
		// MATHEMATICAL MONOSPACE CAPITAL L 𝙻
		GLYPHS.put("u1D67B", "\u1D67B");
		// MATHEMATICAL MONOSPACE CAPITAL M 𝙼
		GLYPHS.put("u1D67C", "\u1D67C");
		// MATHEMATICAL MONOSPACE CAPITAL N 𝙽
		GLYPHS.put("u1D67D", "\u1D67D");
		// MATHEMATICAL MONOSPACE CAPITAL O 𝙾
		GLYPHS.put("u1D67E", "\u1D67E");
		// MATHEMATICAL MONOSPACE CAPITAL P 𝙿
		GLYPHS.put("u1D67F", "\u1D67F");
		// MATHEMATICAL MONOSPACE CAPITAL Q 𝚀
		GLYPHS.put("u1D680", "\u1D680");
		// MATHEMATICAL MONOSPACE CAPITAL R 𝚁
		GLYPHS.put("u1D681", "\u1D681");
		// MATHEMATICAL MONOSPACE CAPITAL S 𝚂
		GLYPHS.put("u1D682", "\u1D682");
		// MATHEMATICAL MONOSPACE CAPITAL T 𝚃
		GLYPHS.put("u1D683", "\u1D683");
		// MATHEMATICAL MONOSPACE CAPITAL U 𝚄
		GLYPHS.put("u1D684", "\u1D684");
		// MATHEMATICAL MONOSPACE CAPITAL V 𝚅
		GLYPHS.put("u1D685", "\u1D685");
		// MATHEMATICAL MONOSPACE CAPITAL W 𝚆
		GLYPHS.put("u1D686", "\u1D686");
		// MATHEMATICAL MONOSPACE CAPITAL X 𝚇
		GLYPHS.put("u1D687", "\u1D687");
		// MATHEMATICAL MONOSPACE CAPITAL Y 𝚈
		GLYPHS.put("u1D688", "\u1D688");
		// MATHEMATICAL MONOSPACE CAPITAL Z 𝚉
		GLYPHS.put("u1D689", "\u1D689");
		// MATHEMATICAL MONOSPACE SMALL A 𝚊
		GLYPHS.put("u1D68A", "\u1D68A");
		// MATHEMATICAL MONOSPACE SMALL B 𝚋
		GLYPHS.put("u1D68B", "\u1D68B");
		// MATHEMATICAL MONOSPACE SMALL C 𝚌
		GLYPHS.put("u1D68C", "\u1D68C");
		// MATHEMATICAL MONOSPACE SMALL D 𝚍
		GLYPHS.put("u1D68D", "\u1D68D");
		// MATHEMATICAL MONOSPACE SMALL E 𝚎
		GLYPHS.put("u1D68E", "\u1D68E");
		// MATHEMATICAL MONOSPACE SMALL F 𝚏
		GLYPHS.put("u1D68F", "\u1D68F");
		// MATHEMATICAL MONOSPACE SMALL G 𝚐
		GLYPHS.put("u1D690", "\u1D690");
		// MATHEMATICAL MONOSPACE SMALL H 𝚑
		GLYPHS.put("u1D691", "\u1D691");
		// MATHEMATICAL MONOSPACE SMALL I 𝚒
		GLYPHS.put("u1D692", "\u1D692");
		// MATHEMATICAL MONOSPACE SMALL J 𝚓
		GLYPHS.put("u1D693", "\u1D693");
		// MATHEMATICAL MONOSPACE SMALL K 𝚔
		GLYPHS.put("u1D694", "\u1D694");
		// MATHEMATICAL MONOSPACE SMALL L 𝚕
		GLYPHS.put("u1D695", "\u1D695");
		// MATHEMATICAL MONOSPACE SMALL M 𝚖
		GLYPHS.put("u1D696", "\u1D696");
		// MATHEMATICAL MONOSPACE SMALL N 𝚗
		GLYPHS.put("u1D697", "\u1D697");
		// MATHEMATICAL MONOSPACE SMALL O 𝚘
		GLYPHS.put("u1D698", "\u1D698");
		// MATHEMATICAL MONOSPACE SMALL P 𝚙
		GLYPHS.put("u1D699", "\u1D699");
		// MATHEMATICAL MONOSPACE SMALL Q 𝚚
		GLYPHS.put("u1D69A", "\u1D69A");
		// MATHEMATICAL MONOSPACE SMALL R 𝚛
		GLYPHS.put("u1D69B", "\u1D69B");
		// MATHEMATICAL MONOSPACE SMALL S 𝚜
		GLYPHS.put("u1D69C", "\u1D69C");
		// MATHEMATICAL MONOSPACE SMALL T 𝚝
		GLYPHS.put("u1D69D", "\u1D69D");
		// MATHEMATICAL MONOSPACE SMALL U 𝚞
		GLYPHS.put("u1D69E", "\u1D69E");
		// MATHEMATICAL MONOSPACE SMALL V 𝚟
		GLYPHS.put("u1D69F", "\u1D69F");
		// MATHEMATICAL MONOSPACE SMALL W 𝚠
		GLYPHS.put("u1D6A0", "\u1D6A0");
		// MATHEMATICAL MONOSPACE SMALL X 𝚡
		GLYPHS.put("u1D6A1", "\u1D6A1");
		// MATHEMATICAL MONOSPACE SMALL Y 𝚢
		GLYPHS.put("u1D6A2", "\u1D6A2");
		// MATHEMATICAL MONOSPACE SMALL Z 𝚣
		GLYPHS.put("u1D6A3", "\u1D6A3");
		// MATHEMATICAL ITALIC SMALL DOTLESS I 𝚤
		GLYPHS.put("u1D6A4", "\u1D6A4");
		// MATHEMATICAL ITALIC SMALL DOTLESS J 𝚥
		GLYPHS.put("u1D6A5", "\u1D6A5");
		// MATHEMATICAL BOLD CAPITAL ALPHA 𝚨
		GLYPHS.put("u1D6A8", "\u1D6A8");
		// MATHEMATICAL BOLD CAPITAL BETA 𝚩
		GLYPHS.put("u1D6A9", "\u1D6A9");
		// MATHEMATICAL BOLD CAPITAL GAMMA 𝚪
		GLYPHS.put("u1D6AA", "\u1D6AA");
		// MATHEMATICAL BOLD CAPITAL DELTA 𝚫
		GLYPHS.put("u1D6AB", "\u1D6AB");
		// MATHEMATICAL BOLD CAPITAL EPSILON 𝚬
		GLYPHS.put("u1D6AC", "\u1D6AC");
		// MATHEMATICAL BOLD CAPITAL ZETA 𝚭
		GLYPHS.put("u1D6AD", "\u1D6AD");
		// MATHEMATICAL BOLD CAPITAL ETA 𝚮
		GLYPHS.put("u1D6AE", "\u1D6AE");
		// MATHEMATICAL BOLD CAPITAL THETA 𝚯
		GLYPHS.put("u1D6AF", "\u1D6AF");
		// MATHEMATICAL BOLD CAPITAL IOTA 𝚰
		GLYPHS.put("u1D6B0", "\u1D6B0");
		// MATHEMATICAL BOLD CAPITAL KAPPA 𝚱
		GLYPHS.put("u1D6B1", "\u1D6B1");
		// MATHEMATICAL BOLD CAPITAL LAMDA 𝚲
		GLYPHS.put("u1D6B2", "\u1D6B2");
		// MATHEMATICAL BOLD CAPITAL MU 𝚳
		GLYPHS.put("u1D6B3", "\u1D6B3");
		// MATHEMATICAL BOLD CAPITAL NU 𝚴
		GLYPHS.put("u1D6B4", "\u1D6B4");
		// MATHEMATICAL BOLD CAPITAL XI 𝚵
		GLYPHS.put("u1D6B5", "\u1D6B5");
		// MATHEMATICAL BOLD CAPITAL OMICRON 𝚶
		GLYPHS.put("u1D6B6", "\u1D6B6");
		// MATHEMATICAL BOLD CAPITAL PI 𝚷
		GLYPHS.put("u1D6B7", "\u1D6B7");
		// MATHEMATICAL BOLD CAPITAL RHO 𝚸
		GLYPHS.put("u1D6B8", "\u1D6B8");
		// MATHEMATICAL BOLD CAPITAL THETA SYMBOL 𝚹
		GLYPHS.put("u1D6B9", "\u1D6B9");
		// MATHEMATICAL BOLD CAPITAL SIGMA 𝚺
		GLYPHS.put("u1D6BA", "\u1D6BA");
		// MATHEMATICAL BOLD CAPITAL TAU 𝚻
		GLYPHS.put("u1D6BB", "\u1D6BB");
		// MATHEMATICAL BOLD CAPITAL UPSILON 𝚼
		GLYPHS.put("u1D6BC", "\u1D6BC");
		// MATHEMATICAL BOLD CAPITAL PHI 𝚽
		GLYPHS.put("u1D6BD", "\u1D6BD");
		// MATHEMATICAL BOLD CAPITAL CHI 𝚾
		GLYPHS.put("u1D6BE", "\u1D6BE");
		// MATHEMATICAL BOLD CAPITAL PSI 𝚿
		GLYPHS.put("u1D6BF", "\u1D6BF");
		// MATHEMATICAL BOLD CAPITAL OMEGA 𝛀
		GLYPHS.put("u1D6C0", "\u1D6C0");
		// MATHEMATICAL BOLD NABLA 𝛁
		GLYPHS.put("u1D6C1", "\u1D6C1");
		// MATHEMATICAL BOLD SMALL ALPHA 𝛂
		GLYPHS.put("u1D6C2", "\u1D6C2");
		// MATHEMATICAL BOLD SMALL BETA 𝛃
		GLYPHS.put("u1D6C3", "\u1D6C3");
		// MATHEMATICAL BOLD SMALL GAMMA 𝛄
		GLYPHS.put("u1D6C4", "\u1D6C4");
		// MATHEMATICAL BOLD SMALL DELTA 𝛅
		GLYPHS.put("u1D6C5", "\u1D6C5");
		// MATHEMATICAL BOLD SMALL EPSILON 𝛆
		GLYPHS.put("u1D6C6", "\u1D6C6");
		// MATHEMATICAL BOLD SMALL ZETA 𝛇
		GLYPHS.put("u1D6C7", "\u1D6C7");
		// MATHEMATICAL BOLD SMALL ETA 𝛈
		GLYPHS.put("u1D6C8", "\u1D6C8");
		// MATHEMATICAL BOLD SMALL THETA 𝛉
		GLYPHS.put("u1D6C9", "\u1D6C9");
		// MATHEMATICAL BOLD SMALL IOTA 𝛊
		GLYPHS.put("u1D6CA", "\u1D6CA");
		// MATHEMATICAL BOLD SMALL KAPPA 𝛋
		GLYPHS.put("u1D6CB", "\u1D6CB");
		// MATHEMATICAL BOLD SMALL LAMDA 𝛌
		GLYPHS.put("u1D6CC", "\u1D6CC");
		// MATHEMATICAL BOLD SMALL MU 𝛍
		GLYPHS.put("u1D6CD", "\u1D6CD");
		// MATHEMATICAL BOLD SMALL NU 𝛎
		GLYPHS.put("u1D6CE", "\u1D6CE");
		// MATHEMATICAL BOLD SMALL XI 𝛏
		GLYPHS.put("u1D6CF", "\u1D6CF");
		// MATHEMATICAL BOLD SMALL OMICRON 𝛐
		GLYPHS.put("u1D6D0", "\u1D6D0");
		// MATHEMATICAL BOLD SMALL PI 𝛑
		GLYPHS.put("u1D6D1", "\u1D6D1");
		// MATHEMATICAL BOLD SMALL RHO 𝛒
		GLYPHS.put("u1D6D2", "\u1D6D2");
		// MATHEMATICAL BOLD SMALL FINAL SIGMA 𝛓
		GLYPHS.put("u1D6D3", "\u1D6D3");
		// MATHEMATICAL BOLD SMALL SIGMA 𝛔
		GLYPHS.put("u1D6D4", "\u1D6D4");
		// MATHEMATICAL BOLD SMALL TAU 𝛕
		GLYPHS.put("u1D6D5", "\u1D6D5");
		// MATHEMATICAL BOLD SMALL UPSILON 𝛖
		GLYPHS.put("u1D6D6", "\u1D6D6");
		// MATHEMATICAL BOLD SMALL PHI 𝛗
		GLYPHS.put("u1D6D7", "\u1D6D7");
		// MATHEMATICAL BOLD SMALL CHI 𝛘
		GLYPHS.put("u1D6D8", "\u1D6D8");
		// MATHEMATICAL BOLD SMALL PSI 𝛙
		GLYPHS.put("u1D6D9", "\u1D6D9");
		// MATHEMATICAL BOLD SMALL OMEGA 𝛚
		GLYPHS.put("u1D6DA", "\u1D6DA");
		// MATHEMATICAL BOLD PARTIAL DIFFERENTIAL 𝛛
		GLYPHS.put("u1D6DB", "\u1D6DB");
		// MATHEMATICAL BOLD EPSILON SYMBOL 𝛜
		GLYPHS.put("u1D6DC", "\u1D6DC");
		// MATHEMATICAL BOLD THETA SYMBOL 𝛝
		GLYPHS.put("u1D6DD", "\u1D6DD");
		// MATHEMATICAL BOLD KAPPA SYMBOL 𝛞
		GLYPHS.put("u1D6DE", "\u1D6DE");
		// MATHEMATICAL BOLD PHI SYMBOL 𝛟
		GLYPHS.put("u1D6DF", "\u1D6DF");
		// MATHEMATICAL BOLD RHO SYMBOL 𝛠
		GLYPHS.put("u1D6E0", "\u1D6E0");
		// MATHEMATICAL BOLD PI SYMBOL 𝛡
		GLYPHS.put("u1D6E1", "\u1D6E1");
		// MATHEMATICAL ITALIC CAPITAL ALPHA 𝛢
		GLYPHS.put("u1D6E2", "\u1D6E2");
		// MATHEMATICAL ITALIC CAPITAL BETA 𝛣
		GLYPHS.put("u1D6E3", "\u1D6E3");
		// MATHEMATICAL ITALIC CAPITAL GAMMA 𝛤
		GLYPHS.put("u1D6E4", "\u1D6E4");
		// MATHEMATICAL ITALIC CAPITAL DELTA 𝛥
		GLYPHS.put("u1D6E5", "\u1D6E5");
		// MATHEMATICAL ITALIC CAPITAL EPSILON 𝛦
		GLYPHS.put("u1D6E6", "\u1D6E6");
		// MATHEMATICAL ITALIC CAPITAL ZETA 𝛧
		GLYPHS.put("u1D6E7", "\u1D6E7");
		// MATHEMATICAL ITALIC CAPITAL ETA 𝛨
		GLYPHS.put("u1D6E8", "\u1D6E8");
		// MATHEMATICAL ITALIC CAPITAL THETA 𝛩
		GLYPHS.put("u1D6E9", "\u1D6E9");
		// MATHEMATICAL ITALIC CAPITAL IOTA 𝛪
		GLYPHS.put("u1D6EA", "\u1D6EA");
		// MATHEMATICAL ITALIC CAPITAL KAPPA 𝛫
		GLYPHS.put("u1D6EB", "\u1D6EB");
		// MATHEMATICAL ITALIC CAPITAL LAMDA 𝛬
		GLYPHS.put("u1D6EC", "\u1D6EC");
		// MATHEMATICAL ITALIC CAPITAL MU 𝛭
		GLYPHS.put("u1D6ED", "\u1D6ED");
		// MATHEMATICAL ITALIC CAPITAL NU 𝛮
		GLYPHS.put("u1D6EE", "\u1D6EE");
		// MATHEMATICAL ITALIC CAPITAL XI 𝛯
		GLYPHS.put("u1D6EF", "\u1D6EF");
		// MATHEMATICAL ITALIC CAPITAL OMICRON 𝛰
		GLYPHS.put("u1D6F0", "\u1D6F0");
		// MATHEMATICAL ITALIC CAPITAL PI 𝛱
		GLYPHS.put("u1D6F1", "\u1D6F1");
		// MATHEMATICAL ITALIC CAPITAL RHO 𝛲
		GLYPHS.put("u1D6F2", "\u1D6F2");
		// MATHEMATICAL ITALIC CAPITAL THETA SYMBOL 𝛳
		GLYPHS.put("u1D6F3", "\u1D6F3");
		// MATHEMATICAL ITALIC CAPITAL SIGMA 𝛴
		GLYPHS.put("u1D6F4", "\u1D6F4");
		// MATHEMATICAL ITALIC CAPITAL TAU 𝛵
		GLYPHS.put("u1D6F5", "\u1D6F5");
		// MATHEMATICAL ITALIC CAPITAL UPSILON 𝛶
		GLYPHS.put("u1D6F6", "\u1D6F6");
		// MATHEMATICAL ITALIC CAPITAL PHI 𝛷
		GLYPHS.put("u1D6F7", "\u1D6F7");
		// MATHEMATICAL ITALIC CAPITAL CHI 𝛸
		GLYPHS.put("u1D6F8", "\u1D6F8");
		// MATHEMATICAL ITALIC CAPITAL PSI 𝛹
		GLYPHS.put("u1D6F9", "\u1D6F9");
		// MATHEMATICAL ITALIC CAPITAL OMEGA 𝛺
		GLYPHS.put("u1D6FA", "\u1D6FA");
		// MATHEMATICAL ITALIC NABLA 𝛻
		GLYPHS.put("u1D6FB", "\u1D6FB");
		// MATHEMATICAL ITALIC SMALL ALPHA 𝛼
		GLYPHS.put("u1D6FC", "\u1D6FC");
		// MATHEMATICAL ITALIC SMALL BETA 𝛽
		GLYPHS.put("u1D6FD", "\u1D6FD");
		// MATHEMATICAL ITALIC SMALL GAMMA 𝛾
		GLYPHS.put("u1D6FE", "\u1D6FE");
		// MATHEMATICAL ITALIC SMALL DELTA 𝛿
		GLYPHS.put("u1D6FF", "\u1D6FF");
		// MATHEMATICAL ITALIC SMALL EPSILON 𝜀
		GLYPHS.put("u1D700", "\u1D700");
		// MATHEMATICAL ITALIC SMALL ZETA 𝜁
		GLYPHS.put("u1D701", "\u1D701");
		// MATHEMATICAL ITALIC SMALL ETA 𝜂
		GLYPHS.put("u1D702", "\u1D702");
		// MATHEMATICAL ITALIC SMALL THETA 𝜃
		GLYPHS.put("u1D703", "\u1D703");
		// MATHEMATICAL ITALIC SMALL IOTA 𝜄
		GLYPHS.put("u1D704", "\u1D704");
		// MATHEMATICAL ITALIC SMALL KAPPA 𝜅
		GLYPHS.put("u1D705", "\u1D705");
		// MATHEMATICAL ITALIC SMALL LAMDA 𝜆
		GLYPHS.put("u1D706", "\u1D706");
		// MATHEMATICAL ITALIC SMALL MU 𝜇
		GLYPHS.put("u1D707", "\u1D707");
		// MATHEMATICAL ITALIC SMALL NU 𝜈
		GLYPHS.put("u1D708", "\u1D708");
		// MATHEMATICAL ITALIC SMALL XI 𝜉
		GLYPHS.put("u1D709", "\u1D709");
		// MATHEMATICAL ITALIC SMALL OMICRON 𝜊
		GLYPHS.put("u1D70A", "\u1D70A");
		// MATHEMATICAL ITALIC SMALL PI 𝜋
		GLYPHS.put("u1D70B", "\u1D70B");
		// MATHEMATICAL ITALIC SMALL RHO 𝜌
		GLYPHS.put("u1D70C", "\u1D70C");
		// MATHEMATICAL ITALIC SMALL FINAL SIGMA 𝜍
		GLYPHS.put("u1D70D", "\u1D70D");
		// MATHEMATICAL ITALIC SMALL SIGMA 𝜎
		GLYPHS.put("u1D70E", "\u1D70E");
		// MATHEMATICAL ITALIC SMALL TAU 𝜏
		GLYPHS.put("u1D70F", "\u1D70F");
		// MATHEMATICAL ITALIC SMALL UPSILON 𝜐
		GLYPHS.put("u1D710", "\u1D710");
		// MATHEMATICAL ITALIC SMALL PHI 𝜑
		GLYPHS.put("u1D711", "\u1D711");
		// MATHEMATICAL ITALIC SMALL CHI 𝜒
		GLYPHS.put("u1D712", "\u1D712");
		// MATHEMATICAL ITALIC SMALL PSI 𝜓
		GLYPHS.put("u1D713", "\u1D713");
		// MATHEMATICAL ITALIC SMALL OMEGA 𝜔
		GLYPHS.put("u1D714", "\u1D714");
		// MATHEMATICAL ITALIC PARTIAL DIFFERENTIAL 𝜕
		GLYPHS.put("u1D715", "\u1D715");
		// MATHEMATICAL ITALIC EPSILON SYMBOL 𝜖
		GLYPHS.put("u1D716", "\u1D716");
		// MATHEMATICAL ITALIC THETA SYMBOL 𝜗
		GLYPHS.put("u1D717", "\u1D717");
		// MATHEMATICAL ITALIC KAPPA SYMBOL 𝜘
		GLYPHS.put("u1D718", "\u1D718");
		// MATHEMATICAL ITALIC PHI SYMBOL 𝜙
		GLYPHS.put("u1D719", "\u1D719");
		// MATHEMATICAL ITALIC RHO SYMBOL 𝜚
		GLYPHS.put("u1D71A", "\u1D71A");
		// MATHEMATICAL ITALIC PI SYMBOL 𝜛
		GLYPHS.put("u1D71B", "\u1D71B");
		// MATHEMATICAL BOLD ITALIC CAPITAL ALPHA 𝜜
		GLYPHS.put("u1D71C", "\u1D71C");
		// MATHEMATICAL BOLD ITALIC CAPITAL BETA 𝜝
		GLYPHS.put("u1D71D", "\u1D71D");
		// MATHEMATICAL BOLD ITALIC CAPITAL GAMMA 𝜞
		GLYPHS.put("u1D71E", "\u1D71E");
		// MATHEMATICAL BOLD ITALIC CAPITAL DELTA 𝜟
		GLYPHS.put("u1D71F", "\u1D71F");
		// MATHEMATICAL BOLD ITALIC CAPITAL EPSILON 𝜠
		GLYPHS.put("u1D720", "\u1D720");
		// MATHEMATICAL BOLD ITALIC CAPITAL ZETA 𝜡
		GLYPHS.put("u1D721", "\u1D721");
		// MATHEMATICAL BOLD ITALIC CAPITAL ETA 𝜢
		GLYPHS.put("u1D722", "\u1D722");
		// MATHEMATICAL BOLD ITALIC CAPITAL THETA 𝜣
		GLYPHS.put("u1D723", "\u1D723");
		// MATHEMATICAL BOLD ITALIC CAPITAL IOTA 𝜤
		GLYPHS.put("u1D724", "\u1D724");
		// MATHEMATICAL BOLD ITALIC CAPITAL KAPPA 𝜥
		GLYPHS.put("u1D725", "\u1D725");
		// MATHEMATICAL BOLD ITALIC CAPITAL LAMDA 𝜦
		GLYPHS.put("u1D726", "\u1D726");
		// MATHEMATICAL BOLD ITALIC CAPITAL MU 𝜧
		GLYPHS.put("u1D727", "\u1D727");
		// MATHEMATICAL BOLD ITALIC CAPITAL NU 𝜨
		GLYPHS.put("u1D728", "\u1D728");
		// MATHEMATICAL BOLD ITALIC CAPITAL XI 𝜩
		GLYPHS.put("u1D729", "\u1D729");
		// MATHEMATICAL BOLD ITALIC CAPITAL OMICRON 𝜪
		GLYPHS.put("u1D72A", "\u1D72A");
		// MATHEMATICAL BOLD ITALIC CAPITAL PI 𝜫
		GLYPHS.put("u1D72B", "\u1D72B");
		// MATHEMATICAL BOLD ITALIC CAPITAL RHO 𝜬
		GLYPHS.put("u1D72C", "\u1D72C");
		// MATHEMATICAL BOLD ITALIC CAPITAL THETA SYMBOL 𝜭
		GLYPHS.put("u1D72D", "\u1D72D");
		// MATHEMATICAL BOLD ITALIC CAPITAL SIGMA 𝜮
		GLYPHS.put("u1D72E", "\u1D72E");
		// MATHEMATICAL BOLD ITALIC CAPITAL TAU 𝜯
		GLYPHS.put("u1D72F", "\u1D72F");
		// MATHEMATICAL BOLD ITALIC CAPITAL UPSILON 𝜰
		GLYPHS.put("u1D730", "\u1D730");
		// MATHEMATICAL BOLD ITALIC CAPITAL PHI 𝜱
		GLYPHS.put("u1D731", "\u1D731");
		// MATHEMATICAL BOLD ITALIC CAPITAL CHI 𝜲
		GLYPHS.put("u1D732", "\u1D732");
		// MATHEMATICAL BOLD ITALIC CAPITAL PSI 𝜳
		GLYPHS.put("u1D733", "\u1D733");
		// MATHEMATICAL BOLD ITALIC CAPITAL OMEGA 𝜴
		GLYPHS.put("u1D734", "\u1D734");
		// MATHEMATICAL BOLD ITALIC NABLA 𝜵
		GLYPHS.put("u1D735", "\u1D735");
		// MATHEMATICAL BOLD ITALIC SMALL ALPHA 𝜶
		GLYPHS.put("u1D736", "\u1D736");
		// MATHEMATICAL BOLD ITALIC SMALL BETA 𝜷
		GLYPHS.put("u1D737", "\u1D737");
		// MATHEMATICAL BOLD ITALIC SMALL GAMMA 𝜸
		GLYPHS.put("u1D738", "\u1D738");
		// MATHEMATICAL BOLD ITALIC SMALL DELTA 𝜹
		GLYPHS.put("u1D739", "\u1D739");
		// MATHEMATICAL BOLD ITALIC SMALL EPSILON 𝜺
		GLYPHS.put("u1D73A", "\u1D73A");
		// MATHEMATICAL BOLD ITALIC SMALL ZETA 𝜻
		GLYPHS.put("u1D73B", "\u1D73B");
		// MATHEMATICAL BOLD ITALIC SMALL ETA 𝜼
		GLYPHS.put("u1D73C", "\u1D73C");
		// MATHEMATICAL BOLD ITALIC SMALL THETA 𝜽
		GLYPHS.put("u1D73D", "\u1D73D");
		// MATHEMATICAL BOLD ITALIC SMALL IOTA 𝜾
		GLYPHS.put("u1D73E", "\u1D73E");
		// MATHEMATICAL BOLD ITALIC SMALL KAPPA 𝜿
		GLYPHS.put("u1D73F", "\u1D73F");
		// MATHEMATICAL BOLD ITALIC SMALL LAMDA 𝝀
		GLYPHS.put("u1D740", "\u1D740");
		// MATHEMATICAL BOLD ITALIC SMALL MU 𝝁
		GLYPHS.put("u1D741", "\u1D741");
		// MATHEMATICAL BOLD ITALIC SMALL NU 𝝂
		GLYPHS.put("u1D742", "\u1D742");
		// MATHEMATICAL BOLD ITALIC SMALL XI 𝝃
		GLYPHS.put("u1D743", "\u1D743");
		// MATHEMATICAL BOLD ITALIC SMALL OMICRON 𝝄
		GLYPHS.put("u1D744", "\u1D744");
		// MATHEMATICAL BOLD ITALIC SMALL PI 𝝅
		GLYPHS.put("u1D745", "\u1D745");
		// MATHEMATICAL BOLD ITALIC SMALL RHO 𝝆
		GLYPHS.put("u1D746", "\u1D746");
		// MATHEMATICAL BOLD ITALIC SMALL FINAL SIGMA 𝝇
		GLYPHS.put("u1D747", "\u1D747");
		// MATHEMATICAL BOLD ITALIC SMALL SIGMA 𝝈
		GLYPHS.put("u1D748", "\u1D748");
		// MATHEMATICAL BOLD ITALIC SMALL TAU 𝝉
		GLYPHS.put("u1D749", "\u1D749");
		// MATHEMATICAL BOLD ITALIC SMALL UPSILON 𝝊
		GLYPHS.put("u1D74A", "\u1D74A");
		// MATHEMATICAL BOLD ITALIC SMALL PHI 𝝋
		GLYPHS.put("u1D74B", "\u1D74B");
		// MATHEMATICAL BOLD ITALIC SMALL CHI 𝝌
		GLYPHS.put("u1D74C", "\u1D74C");
		// MATHEMATICAL BOLD ITALIC SMALL PSI 𝝍
		GLYPHS.put("u1D74D", "\u1D74D");
		// MATHEMATICAL BOLD ITALIC SMALL OMEGA 𝝎
		GLYPHS.put("u1D74E", "\u1D74E");
		// MATHEMATICAL BOLD ITALIC PARTIAL DIFFERENTIAL 𝝏
		GLYPHS.put("u1D74F", "\u1D74F");
		// MATHEMATICAL BOLD ITALIC EPSILON SYMBOL 𝝐
		GLYPHS.put("u1D750", "\u1D750");
		// MATHEMATICAL BOLD ITALIC THETA SYMBOL 𝝑
		GLYPHS.put("u1D751", "\u1D751");
		// MATHEMATICAL BOLD ITALIC KAPPA SYMBOL 𝝒
		GLYPHS.put("u1D752", "\u1D752");
		// MATHEMATICAL BOLD ITALIC PHI SYMBOL 𝝓
		GLYPHS.put("u1D753", "\u1D753");
		// MATHEMATICAL BOLD ITALIC RHO SYMBOL 𝝔
		GLYPHS.put("u1D754", "\u1D754");
		// MATHEMATICAL BOLD ITALIC PI SYMBOL 𝝕
		GLYPHS.put("u1D755", "\u1D755");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL ALPHA 𝝖
		GLYPHS.put("u1D756", "\u1D756");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL BETA 𝝗
		GLYPHS.put("u1D757", "\u1D757");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL GAMMA 𝝘
		GLYPHS.put("u1D758", "\u1D758");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL DELTA 𝝙
		GLYPHS.put("u1D759", "\u1D759");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL EPSILON 𝝚
		GLYPHS.put("u1D75A", "\u1D75A");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL ZETA 𝝛
		GLYPHS.put("u1D75B", "\u1D75B");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL ETA 𝝜
		GLYPHS.put("u1D75C", "\u1D75C");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL THETA 𝝝
		GLYPHS.put("u1D75D", "\u1D75D");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL IOTA 𝝞
		GLYPHS.put("u1D75E", "\u1D75E");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL KAPPA 𝝟
		GLYPHS.put("u1D75F", "\u1D75F");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL LAMDA 𝝠
		GLYPHS.put("u1D760", "\u1D760");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL MU 𝝡
		GLYPHS.put("u1D761", "\u1D761");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL NU 𝝢
		GLYPHS.put("u1D762", "\u1D762");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL XI 𝝣
		GLYPHS.put("u1D763", "\u1D763");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL OMICRON 𝝤
		GLYPHS.put("u1D764", "\u1D764");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL PI 𝝥
		GLYPHS.put("u1D765", "\u1D765");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL RHO 𝝦
		GLYPHS.put("u1D766", "\u1D766");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL THETA SYMBOL 𝝧
		GLYPHS.put("u1D767", "\u1D767");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL SIGMA 𝝨
		GLYPHS.put("u1D768", "\u1D768");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL TAU 𝝩
		GLYPHS.put("u1D769", "\u1D769");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL UPSILON 𝝪
		GLYPHS.put("u1D76A", "\u1D76A");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL PHI 𝝫
		GLYPHS.put("u1D76B", "\u1D76B");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL CHI 𝝬
		GLYPHS.put("u1D76C", "\u1D76C");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL PSI 𝝭
		GLYPHS.put("u1D76D", "\u1D76D");
		// MATHEMATICAL SANS-SERIF BOLD CAPITAL OMEGA 𝝮
		GLYPHS.put("u1D76E", "\u1D76E");
		// MATHEMATICAL SANS-SERIF BOLD NABLA 𝝯
		GLYPHS.put("u1D76F", "\u1D76F");
		// MATHEMATICAL SANS-SERIF BOLD SMALL ALPHA 𝝰
		GLYPHS.put("u1D770", "\u1D770");
		// MATHEMATICAL SANS-SERIF BOLD SMALL BETA 𝝱
		GLYPHS.put("u1D771", "\u1D771");
		// MATHEMATICAL SANS-SERIF BOLD SMALL GAMMA 𝝲
		GLYPHS.put("u1D772", "\u1D772");
		// MATHEMATICAL SANS-SERIF BOLD SMALL DELTA 𝝳
		GLYPHS.put("u1D773", "\u1D773");
		// MATHEMATICAL SANS-SERIF BOLD SMALL EPSILON 𝝴
		GLYPHS.put("u1D774", "\u1D774");
		// MATHEMATICAL SANS-SERIF BOLD SMALL ZETA 𝝵
		GLYPHS.put("u1D775", "\u1D775");
		// MATHEMATICAL SANS-SERIF BOLD SMALL ETA 𝝶
		GLYPHS.put("u1D776", "\u1D776");
		// MATHEMATICAL SANS-SERIF BOLD SMALL THETA 𝝷
		GLYPHS.put("u1D777", "\u1D777");
		// MATHEMATICAL SANS-SERIF BOLD SMALL IOTA 𝝸
		GLYPHS.put("u1D778", "\u1D778");
		// MATHEMATICAL SANS-SERIF BOLD SMALL KAPPA 𝝹
		GLYPHS.put("u1D779", "\u1D779");
		// MATHEMATICAL SANS-SERIF BOLD SMALL LAMDA 𝝺
		GLYPHS.put("u1D77A", "\u1D77A");
		// MATHEMATICAL SANS-SERIF BOLD SMALL MU 𝝻
		GLYPHS.put("u1D77B", "\u1D77B");
		// MATHEMATICAL SANS-SERIF BOLD SMALL NU 𝝼
		GLYPHS.put("u1D77C", "\u1D77C");
		// MATHEMATICAL SANS-SERIF BOLD SMALL XI 𝝽
		GLYPHS.put("u1D77D", "\u1D77D");
		// MATHEMATICAL SANS-SERIF BOLD SMALL OMICRON 𝝾
		GLYPHS.put("u1D77E", "\u1D77E");
		// MATHEMATICAL SANS-SERIF BOLD SMALL PI 𝝿
		GLYPHS.put("u1D77F", "\u1D77F");
		// MATHEMATICAL SANS-SERIF BOLD SMALL RHO 𝞀
		GLYPHS.put("u1D780", "\u1D780");
		// MATHEMATICAL SANS-SERIF BOLD SMALL FINAL SIGMA 𝞁
		GLYPHS.put("u1D781", "\u1D781");
		// MATHEMATICAL SANS-SERIF BOLD SMALL SIGMA 𝞂
		GLYPHS.put("u1D782", "\u1D782");
		// MATHEMATICAL SANS-SERIF BOLD SMALL TAU 𝞃
		GLYPHS.put("u1D783", "\u1D783");
		// MATHEMATICAL SANS-SERIF BOLD SMALL UPSILON 𝞄
		GLYPHS.put("u1D784", "\u1D784");
		// MATHEMATICAL SANS-SERIF BOLD SMALL PHI 𝞅
		GLYPHS.put("u1D785", "\u1D785");
		// MATHEMATICAL SANS-SERIF BOLD SMALL CHI 𝞆
		GLYPHS.put("u1D786", "\u1D786");
		// MATHEMATICAL SANS-SERIF BOLD SMALL PSI 𝞇
		GLYPHS.put("u1D787", "\u1D787");
		// MATHEMATICAL SANS-SERIF BOLD SMALL OMEGA 𝞈
		GLYPHS.put("u1D788", "\u1D788");
		// MATHEMATICAL SANS-SERIF BOLD PARTIAL DIFFERENTIAL 𝞉
		GLYPHS.put("u1D789", "\u1D789");
		// MATHEMATICAL SANS-SERIF BOLD EPSILON SYMBOL 𝞊
		GLYPHS.put("u1D78A", "\u1D78A");
		// MATHEMATICAL SANS-SERIF BOLD THETA SYMBOL 𝞋
		GLYPHS.put("u1D78B", "\u1D78B");
		// MATHEMATICAL SANS-SERIF BOLD KAPPA SYMBOL 𝞌
		GLYPHS.put("u1D78C", "\u1D78C");
		// MATHEMATICAL SANS-SERIF BOLD PHI SYMBOL 𝞍
		GLYPHS.put("u1D78D", "\u1D78D");
		// MATHEMATICAL SANS-SERIF BOLD RHO SYMBOL 𝞎
		GLYPHS.put("u1D78E", "\u1D78E");
		// MATHEMATICAL SANS-SERIF BOLD PI SYMBOL 𝞏
		GLYPHS.put("u1D78F", "\u1D78F");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL ALPHA 𝞐
		GLYPHS.put("u1D790", "\u1D790");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL BETA 𝞑
		GLYPHS.put("u1D791", "\u1D791");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL GAMMA 𝞒
		GLYPHS.put("u1D792", "\u1D792");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL DELTA 𝞓
		GLYPHS.put("u1D793", "\u1D793");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL EPSILON 𝞔
		GLYPHS.put("u1D794", "\u1D794");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL ZETA 𝞕
		GLYPHS.put("u1D795", "\u1D795");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL ETA 𝞖
		GLYPHS.put("u1D796", "\u1D796");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL THETA 𝞗
		GLYPHS.put("u1D797", "\u1D797");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL IOTA 𝞘
		GLYPHS.put("u1D798", "\u1D798");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL KAPPA 𝞙
		GLYPHS.put("u1D799", "\u1D799");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL LAMDA 𝞚
		GLYPHS.put("u1D79A", "\u1D79A");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL MU 𝞛
		GLYPHS.put("u1D79B", "\u1D79B");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL NU 𝞜
		GLYPHS.put("u1D79C", "\u1D79C");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL XI 𝞝
		GLYPHS.put("u1D79D", "\u1D79D");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL OMICRON 𝞞
		GLYPHS.put("u1D79E", "\u1D79E");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL PI 𝞟
		GLYPHS.put("u1D79F", "\u1D79F");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL RHO 𝞠
		GLYPHS.put("u1D7A0", "\u1D7A0");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL THETA SYMBOL 𝞡
		GLYPHS.put("u1D7A1", "\u1D7A1");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL SIGMA 𝞢
		GLYPHS.put("u1D7A2", "\u1D7A2");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL TAU 𝞣
		GLYPHS.put("u1D7A3", "\u1D7A3");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL UPSILON 𝞤
		GLYPHS.put("u1D7A4", "\u1D7A4");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL PHI 𝞥
		GLYPHS.put("u1D7A5", "\u1D7A5");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL CHI 𝞦
		GLYPHS.put("u1D7A6", "\u1D7A6");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL PSI 𝞧
		GLYPHS.put("u1D7A7", "\u1D7A7");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC CAPITAL OMEGA 𝞨
		GLYPHS.put("u1D7A8", "\u1D7A8");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC NABLA 𝞩
		GLYPHS.put("u1D7A9", "\u1D7A9");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL ALPHA 𝞪
		GLYPHS.put("u1D7AA", "\u1D7AA");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL BETA 𝞫
		GLYPHS.put("u1D7AB", "\u1D7AB");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL GAMMA 𝞬
		GLYPHS.put("u1D7AC", "\u1D7AC");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL DELTA 𝞭
		GLYPHS.put("u1D7AD", "\u1D7AD");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL EPSILON 𝞮
		GLYPHS.put("u1D7AE", "\u1D7AE");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL ZETA 𝞯
		GLYPHS.put("u1D7AF", "\u1D7AF");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL ETA 𝞰
		GLYPHS.put("u1D7B0", "\u1D7B0");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL THETA 𝞱
		GLYPHS.put("u1D7B1", "\u1D7B1");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL IOTA 𝞲
		GLYPHS.put("u1D7B2", "\u1D7B2");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL KAPPA 𝞳
		GLYPHS.put("u1D7B3", "\u1D7B3");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL LAMDA 𝞴
		GLYPHS.put("u1D7B4", "\u1D7B4");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL MU 𝞵
		GLYPHS.put("u1D7B5", "\u1D7B5");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL NU 𝞶
		GLYPHS.put("u1D7B6", "\u1D7B6");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL XI 𝞷
		GLYPHS.put("u1D7B7", "\u1D7B7");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL OMICRON 𝞸
		GLYPHS.put("u1D7B8", "\u1D7B8");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL PI 𝞹
		GLYPHS.put("u1D7B9", "\u1D7B9");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL RHO 𝞺
		GLYPHS.put("u1D7BA", "\u1D7BA");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL FINAL SIGMA 𝞻
		GLYPHS.put("u1D7BB", "\u1D7BB");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL SIGMA 𝞼
		GLYPHS.put("u1D7BC", "\u1D7BC");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL TAU 𝞽
		GLYPHS.put("u1D7BD", "\u1D7BD");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL UPSILON 𝞾
		GLYPHS.put("u1D7BE", "\u1D7BE");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL PHI 𝞿
		GLYPHS.put("u1D7BF", "\u1D7BF");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL CHI 𝟀
		GLYPHS.put("u1D7C0", "\u1D7C0");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL PSI 𝟁
		GLYPHS.put("u1D7C1", "\u1D7C1");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC SMALL OMEGA 𝟂
		GLYPHS.put("u1D7C2", "\u1D7C2");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC PARTIAL DIFFERENTIAL 𝟃
		GLYPHS.put("u1D7C3", "\u1D7C3");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC EPSILON SYMBOL 𝟄
		GLYPHS.put("u1D7C4", "\u1D7C4");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC THETA SYMBOL 𝟅
		GLYPHS.put("u1D7C5", "\u1D7C5");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC KAPPA SYMBOL 𝟆
		GLYPHS.put("u1D7C6", "\u1D7C6");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC PHI SYMBOL 𝟇
		GLYPHS.put("u1D7C7", "\u1D7C7");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC RHO SYMBOL 𝟈
		GLYPHS.put("u1D7C8", "\u1D7C8");
		// MATHEMATICAL SANS-SERIF BOLD ITALIC PI SYMBOL 𝟉
		GLYPHS.put("u1D7C9", "\u1D7C9");
		// MATHEMATICAL BOLD DIGIT ZERO 𝟎
		GLYPHS.put("u1D7CE", "\u1D7CE");
		// MATHEMATICAL BOLD DIGIT ONE 𝟏
		GLYPHS.put("u1D7CF", "\u1D7CF");
		// MATHEMATICAL BOLD DIGIT TWO 𝟐
		GLYPHS.put("u1D7D0", "\u1D7D0");
		// MATHEMATICAL BOLD DIGIT THREE 𝟑
		GLYPHS.put("u1D7D1", "\u1D7D1");
		// MATHEMATICAL BOLD DIGIT FOUR 𝟒
		GLYPHS.put("u1D7D2", "\u1D7D2");
		// MATHEMATICAL BOLD DIGIT FIVE 𝟓
		GLYPHS.put("u1D7D3", "\u1D7D3");
		// MATHEMATICAL BOLD DIGIT SIX 𝟔
		GLYPHS.put("u1D7D4", "\u1D7D4");
		// MATHEMATICAL BOLD DIGIT SEVEN 𝟕
		GLYPHS.put("u1D7D5", "\u1D7D5");
		// MATHEMATICAL BOLD DIGIT EIGHT 𝟖
		GLYPHS.put("u1D7D6", "\u1D7D6");
		// MATHEMATICAL BOLD DIGIT NINE 𝟗
		GLYPHS.put("u1D7D7", "\u1D7D7");
		// MATHEMATICAL DOUBLE-STRUCK DIGIT ZERO 𝟘
		GLYPHS.put("u1D7D8", "\u1D7D8");
		// MATHEMATICAL DOUBLE-STRUCK DIGIT ONE 𝟙
		GLYPHS.put("u1D7D9", "\u1D7D9");
		// MATHEMATICAL DOUBLE-STRUCK DIGIT TWO 𝟚
		GLYPHS.put("u1D7DA", "\u1D7DA");
		// MATHEMATICAL DOUBLE-STRUCK DIGIT THREE 𝟛
		GLYPHS.put("u1D7DB", "\u1D7DB");
		// MATHEMATICAL DOUBLE-STRUCK DIGIT FOUR 𝟜
		GLYPHS.put("u1D7DC", "\u1D7DC");
		// MATHEMATICAL DOUBLE-STRUCK DIGIT FIVE 𝟝
		GLYPHS.put("u1D7DD", "\u1D7DD");
		// MATHEMATICAL DOUBLE-STRUCK DIGIT SIX 𝟞
		GLYPHS.put("u1D7DE", "\u1D7DE");
		// MATHEMATICAL DOUBLE-STRUCK DIGIT SEVEN 𝟟
		GLYPHS.put("u1D7DF", "\u1D7DF");
		// MATHEMATICAL DOUBLE-STRUCK DIGIT EIGHT 𝟠
		GLYPHS.put("u1D7E0", "\u1D7E0");
		// MATHEMATICAL DOUBLE-STRUCK DIGIT NINE 𝟡
		GLYPHS.put("u1D7E1", "\u1D7E1");
		// MATHEMATICAL SANS-SERIF DIGIT ZERO 𝟢
		GLYPHS.put("u1D7E2", "\u1D7E2");
		// MATHEMATICAL SANS-SERIF DIGIT ONE 𝟣
		GLYPHS.put("u1D7E3", "\u1D7E3");
		// MATHEMATICAL SANS-SERIF DIGIT TWO 𝟤
		GLYPHS.put("u1D7E4", "\u1D7E4");
		// MATHEMATICAL SANS-SERIF DIGIT THREE 𝟥
		GLYPHS.put("u1D7E5", "\u1D7E5");
		// MATHEMATICAL SANS-SERIF DIGIT FOUR 𝟦
		GLYPHS.put("u1D7E6", "\u1D7E6");
		// MATHEMATICAL SANS-SERIF DIGIT FIVE 𝟧
		GLYPHS.put("u1D7E7", "\u1D7E7");
		// MATHEMATICAL SANS-SERIF DIGIT SIX 𝟨
		GLYPHS.put("u1D7E8", "\u1D7E8");
		// MATHEMATICAL SANS-SERIF DIGIT SEVEN 𝟩
		GLYPHS.put("u1D7E9", "\u1D7E9");
		// MATHEMATICAL SANS-SERIF DIGIT EIGHT 𝟪
		GLYPHS.put("u1D7EA", "\u1D7EA");
		// MATHEMATICAL SANS-SERIF DIGIT NINE 𝟫
		GLYPHS.put("u1D7EB", "\u1D7EB");
		// MATHEMATICAL SANS-SERIF BOLD DIGIT ZERO 𝟬
		GLYPHS.put("u1D7EC", "\u1D7EC");
		// MATHEMATICAL SANS-SERIF BOLD DIGIT ONE 𝟭
		GLYPHS.put("u1D7ED", "\u1D7ED");
		// MATHEMATICAL SANS-SERIF BOLD DIGIT TWO 𝟮
		GLYPHS.put("u1D7EE", "\u1D7EE");
		// MATHEMATICAL SANS-SERIF BOLD DIGIT THREE 𝟯
		GLYPHS.put("u1D7EF", "\u1D7EF");
		// MATHEMATICAL SANS-SERIF BOLD DIGIT FOUR 𝟰
		GLYPHS.put("u1D7F0", "\u1D7F0");
		// MATHEMATICAL SANS-SERIF BOLD DIGIT FIVE 𝟱
		GLYPHS.put("u1D7F1", "\u1D7F1");
		// MATHEMATICAL SANS-SERIF BOLD DIGIT SIX 𝟲
		GLYPHS.put("u1D7F2", "\u1D7F2");
		// MATHEMATICAL SANS-SERIF BOLD DIGIT SEVEN 𝟳
		GLYPHS.put("u1D7F3", "\u1D7F3");
		// MATHEMATICAL SANS-SERIF BOLD DIGIT EIGHT 𝟴
		GLYPHS.put("u1D7F4", "\u1D7F4");
		// MATHEMATICAL SANS-SERIF BOLD DIGIT NINE 𝟵
		GLYPHS.put("u1D7F5", "\u1D7F5");
		// MATHEMATICAL MONOSPACE DIGIT ZERO 𝟶
		GLYPHS.put("u1D7F6", "\u1D7F6");
		// MATHEMATICAL MONOSPACE DIGIT ONE 𝟷
		GLYPHS.put("u1D7F7", "\u1D7F7");
		// MATHEMATICAL MONOSPACE DIGIT TWO 𝟸
		GLYPHS.put("u1D7F8", "\u1D7F8");
		// MATHEMATICAL MONOSPACE DIGIT THREE 𝟹
		GLYPHS.put("u1D7F9", "\u1D7F9");
		// MATHEMATICAL MONOSPACE DIGIT FOUR 𝟺
		GLYPHS.put("u1D7FA", "\u1D7FA");
		// MATHEMATICAL MONOSPACE DIGIT FIVE 𝟻
		GLYPHS.put("u1D7FB", "\u1D7FB");
		// MATHEMATICAL MONOSPACE DIGIT SIX 𝟼
		GLYPHS.put("u1D7FC", "\u1D7FC");
		// MATHEMATICAL MONOSPACE DIGIT SEVEN 𝟽
		GLYPHS.put("u1D7FD", "\u1D7FD");
		// MATHEMATICAL MONOSPACE DIGIT EIGHT 𝟾
		GLYPHS.put("u1D7FE", "\u1D7FE");
		// MATHEMATICAL MONOSPACE DIGIT NINE 𝟿
		GLYPHS.put("u1D7FF", "\u1D7FF");

	}

	public static String formatSymbol(String ref) {
		String ret = formatSymbol0(ref);
		if (ret == null) {
			throw new IllegalArgumentException("Unknown symbol: " + ref);
		}
		return ret;
	}

	private static String formatSymbol0(String ref) {
		return GLYPHS.get(ref);
	}

	public static Map<String, String> toMap() {
		return GLYPHS;
	}
}
