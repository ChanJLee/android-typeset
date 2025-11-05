package me.chan.texas.ext.markdown.math.renderer.fonts;

import java.util.HashMap;
import java.util.Map;

// AUTO GEN BY TOOLS, DO NOT MODIFY IT!
public class SymbolOptions {
	public final Map<String, Symbol> Ps = new HashMap<>();
	public final Map<String, Symbol> Sc = new HashMap<>();
	public final Map<String, Symbol> Pc = new HashMap<>();
	public final Map<String, Symbol> Pd = new HashMap<>();
	public final Map<String, Symbol> Pe = new HashMap<>();
	public final Map<String, Symbol> Pf = new HashMap<>();
	public final Map<String, Symbol> Sk = new HashMap<>();
	public final Map<String, Symbol> Pi = new HashMap<>();
	public final Map<String, Symbol> Sm = new HashMap<>();
	public final Map<String, Symbol> So = new HashMap<>();
	public final Map<String, Symbol> Po = new HashMap<>();
	public final Map<String, Symbol> all = new HashMap<>();
	public SymbolOptions() {
		// LEFT PARENTHESIS (
		Ps.put("parenleft", new Symbol("\u0028"));
		// LEFT SQUARE BRACKET [
		Ps.put("bracketleft", new Symbol("\u005B"));
		// LEFT CURLY BRACKET {
		Ps.put("braceleft", new Symbol("\u007B"));
		// SINGLE LOW-9 QUOTATION MARK ‚
		Ps.put("quotesinglbase", new Symbol("\u201A"));
		// DOUBLE LOW-9 QUOTATION MARK „
		Ps.put("quotedblbase", new Symbol("\u201E"));
		// LEFT CEILING ⌈
		Ps.put("uni2308", new Symbol("\u2308"));
		// LEFT FLOOR ⌊
		Ps.put("uni230A", new Symbol("\u230A"));
		// LEFT-POINTING ANGLE BRACKET 〈
		Ps.put("angleleft", new Symbol("\u2329"));
		// MATHEMATICAL LEFT WHITE SQUARE BRACKET ⟦
		Ps.put("uni27E6", new Symbol("\u27E6"));
		// MATHEMATICAL LEFT ANGLE BRACKET ⟨
		Ps.put("uni27E8", new Symbol("\u27E8"));
		// MATHEMATICAL LEFT DOUBLE ANGLE BRACKET ⟪
		Ps.put("uni27EA", new Symbol("\u27EA"));
		// MATHEMATICAL LEFT FLATTENED PARENTHESIS ⟮
		Ps.put("uni27EE", new Symbol("\u27EE"));
		// LEFT WHITE LENTICULAR BRACKET 〖
		Ps.put("whitelenticularbracketleft", new Symbol("\u3016"));

		all.putAll(Ps);


		// DOLLAR SIGN $
		Sc.put("dollar", new Symbol("\u0024"));
		// CENT SIGN ¢
		Sc.put("cent", new Symbol("\u00A2"));
		// POUND SIGN £
		Sc.put("sterling", new Symbol("\u00A3"));
		// CURRENCY SIGN ¤
		Sc.put("currency", new Symbol("\u00A4"));
		// YEN SIGN ¥
		Sc.put("yen", new Symbol("\u00A5"));
		// COLON SIGN ₡
		Sc.put("colonmonetary", new Symbol("\u20A1"));
		// EURO SIGN €
		Sc.put("Euro", new Symbol("\u20AC"));

		all.putAll(Sc);


		// LOW LINE _
		Pc.put("underscore", new Symbol("\u005F"));

		all.putAll(Pc);


		// HYPHEN-MINUS -
		Pd.put("hyphen", new Symbol("\u002D"));
		// HYPHEN ‐
		Pd.put("hyphentwo", new Symbol("\u2010"));
		// FIGURE DASH ‒
		Pd.put("figuredash", new Symbol("\u2012"));
		// EN DASH –
		Pd.put("endash", new Symbol("\u2013"));
		// EM DASH —
		Pd.put("emdash", new Symbol("\u2014"));
		// HORIZONTAL BAR ―
		Pd.put("uni2015", new Symbol("\u2015"));

		all.putAll(Pd);


		// RIGHT PARENTHESIS )
		Pe.put("parenright", new Symbol("\u0029"));
		// RIGHT SQUARE BRACKET ]
		Pe.put("bracketright", new Symbol("\u005D"));
		// RIGHT CURLY BRACKET }
		Pe.put("braceright", new Symbol("\u007D"));
		// RIGHT CEILING ⌉
		Pe.put("uni2309", new Symbol("\u2309"));
		// RIGHT FLOOR ⌋
		Pe.put("uni230B", new Symbol("\u230B"));
		// RIGHT-POINTING ANGLE BRACKET 〉
		Pe.put("angleright", new Symbol("\u232A"));
		// MATHEMATICAL RIGHT WHITE SQUARE BRACKET ⟧
		Pe.put("uni27E7", new Symbol("\u27E7"));
		// MATHEMATICAL RIGHT ANGLE BRACKET ⟩
		Pe.put("uni27E9", new Symbol("\u27E9"));
		// MATHEMATICAL RIGHT DOUBLE ANGLE BRACKET ⟫
		Pe.put("uni27EB", new Symbol("\u27EB"));
		// MATHEMATICAL RIGHT FLATTENED PARENTHESIS ⟯
		Pe.put("uni27EF", new Symbol("\u27EF"));
		// RIGHT WHITE LENTICULAR BRACKET 〗
		Pe.put("whitelenticularbracketright", new Symbol("\u3017"));

		all.putAll(Pe);


		// RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK »
		Pf.put("guillemotright", new Symbol("\u00BB"));
		// RIGHT SINGLE QUOTATION MARK ’
		Pf.put("quoteright", new Symbol("\u2019"));
		// RIGHT DOUBLE QUOTATION MARK ”
		Pf.put("quotedblright", new Symbol("\u201D"));
		// SINGLE RIGHT-POINTING ANGLE QUOTATION MARK ›
		Pf.put("guilsinglright", new Symbol("\u203A"));

		all.putAll(Pf);


		// CIRCUMFLEX ACCENT ^
		Sk.put("asciicircum", new Symbol("\u005E"));
		// GRAVE ACCENT `
		Sk.put("grave", new Symbol("\u0060"));
		// DIAERESIS ¨
		Sk.put("dieresis", new Symbol("\u00A8"));
		// MACRON ¯
		Sk.put("macron", new Symbol("\u00AF"));
		// ACUTE ACCENT ´
		Sk.put("acute", new Symbol("\u00B4"));
		// CEDILLA ¸
		Sk.put("cedilla", new Symbol("\u00B8"));
		// BREVE ˘
		Sk.put("breve", new Symbol("\u02D8"));
		// DOT ABOVE ˙
		Sk.put("dotaccent", new Symbol("\u02D9"));
		// RING ABOVE ˚
		Sk.put("ring", new Symbol("\u02DA"));
		// OGONEK ˛
		Sk.put("ogonek", new Symbol("\u02DB"));
		// SMALL TILDE ˜
		Sk.put("tilde", new Symbol("\u02DC"));
		// DOUBLE ACUTE ACCENT ˝
		Sk.put("hungarumlaut", new Symbol("\u02DD"));

		all.putAll(Sk);


		// LEFT-POINTING DOUBLE ANGLE QUOTATION MARK «
		Pi.put("guillemotleft", new Symbol("\u00AB"));
		// LEFT SINGLE QUOTATION MARK ‘
		Pi.put("quoteleft", new Symbol("\u2018"));
		// LEFT DOUBLE QUOTATION MARK “
		Pi.put("quotedblleft", new Symbol("\u201C"));
		// SINGLE LEFT-POINTING ANGLE QUOTATION MARK ‹
		Pi.put("guilsinglleft", new Symbol("\u2039"));

		all.putAll(Pi);


		// PLUS SIGN +
		Sm.put("plus", new Symbol("\u002B"));
		// LESS-THAN SIGN <
		Sm.put("less", new Symbol("\u003C"));
		// EQUALS SIGN =
		Sm.put("equal", new Symbol("\u003D"));
		// GREATER-THAN SIGN >
		Sm.put("greater", new Symbol("\u003E"));
		// VERTICAL LINE |
		Sm.put("bar", new Symbol("\u007C"));
		// TILDE ~
		Sm.put("asciitilde", new Symbol("\u007E"));
		// NOT SIGN ¬
		Sm.put("logicalnot", new Symbol("\u00AC"));
		// PLUS-MINUS SIGN ±
		Sm.put("plusminus", new Symbol("\u00B1"));
		// MULTIPLICATION SIGN ×
		Sm.put("multiply", new Symbol("\u00D7"));
		// DIVISION SIGN ÷
		Sm.put("divide", new Symbol("\u00F7"));
		// FRACTION SLASH ⁄
		Sm.put("fraction", new Symbol("\u2044"));
		// COMMERCIAL MINUS SIGN ⁒
		Sm.put("discount", new Symbol("\u2052"));
		// SCRIPT CAPITAL P ℘
		Sm.put("weierstrass", new Symbol("\u2118"));
		// DOUBLE-STRUCK N-ARY SUMMATION ⅀
		Sm.put("uni2140", new Symbol("\u2140"));
		// LEFTWARDS ARROW ←
		Sm.put("arrowleft", new Symbol("\u2190"));
		// UPWARDS ARROW ↑
		Sm.put("arrowup", new Symbol("\u2191"));
		// RIGHTWARDS ARROW →
		Sm.put("arrowright", new Symbol("\u2192"));
		// DOWNWARDS ARROW ↓
		Sm.put("arrowdown", new Symbol("\u2193"));
		// LEFT RIGHT ARROW ↔
		Sm.put("arrowboth", new Symbol("\u2194"));
		// LEFTWARDS ARROW WITH STROKE ↚
		Sm.put("uni219A", new Symbol("\u219A"));
		// RIGHTWARDS ARROW WITH STROKE ↛
		Sm.put("uni219B", new Symbol("\u219B"));
		// RIGHTWARDS TWO HEADED ARROW ↠
		Sm.put("uni21A0", new Symbol("\u21A0"));
		// RIGHTWARDS ARROW WITH TAIL ↣
		Sm.put("uni21A3", new Symbol("\u21A3"));
		// RIGHTWARDS ARROW FROM BAR ↦
		Sm.put("uni21A6", new Symbol("\u21A6"));
		// LEFT RIGHT ARROW WITH STROKE ↮
		Sm.put("uni21AE", new Symbol("\u21AE"));
		// LEFT RIGHT DOUBLE ARROW WITH STROKE ⇎
		Sm.put("uni21CE", new Symbol("\u21CE"));
		// RIGHTWARDS DOUBLE ARROW WITH STROKE ⇏
		Sm.put("uni21CF", new Symbol("\u21CF"));
		// RIGHTWARDS DOUBLE ARROW ⇒
		Sm.put("arrowdblright", new Symbol("\u21D2"));
		// LEFT RIGHT DOUBLE ARROW ⇔
		Sm.put("arrowdblboth", new Symbol("\u21D4"));
		// DOWNWARDS ARROW LEFTWARDS OF UPWARDS ARROW ⇵
		Sm.put("uni21F5", new Symbol("\u21F5"));
		// THREE RIGHTWARDS ARROWS ⇶
		Sm.put("uni21F6", new Symbol("\u21F6"));
		// FOR ALL ∀
		Sm.put("universal", new Symbol("\u2200"));
		// COMPLEMENT ∁
		Sm.put("uni2201", new Symbol("\u2201"));
		// PARTIAL DIFFERENTIAL ∂
		Sm.put("partialdiff", new Symbol("\u2202"));
		// THERE EXISTS ∃
		Sm.put("existential", new Symbol("\u2203"));
		// THERE DOES NOT EXIST ∄
		Sm.put("uni2204", new Symbol("\u2204"));
		// EMPTY SET ∅
		Sm.put("emptyset", new Symbol("\u2205"));
		// INCREMENT ∆
		Sm.put("uni2206", new Symbol("\u2206"));
		// NABLA ∇
		Sm.put("nabla", new Symbol("\u2207"));
		// ELEMENT OF ∈
		Sm.put("element", new Symbol("\u2208"));
		// NOT AN ELEMENT OF ∉
		Sm.put("uni2209", new Symbol("\u2209"));
		// SMALL ELEMENT OF ∊
		Sm.put("uni220A", new Symbol("\u220A"));
		// CONTAINS AS MEMBER ∋
		Sm.put("suchthat", new Symbol("\u220B"));
		// DOES NOT CONTAIN AS MEMBER ∌
		Sm.put("uni220C", new Symbol("\u220C"));
		// SMALL CONTAINS AS MEMBER ∍
		Sm.put("uni220D", new Symbol("\u220D"));
		// END OF PROOF ∎
		Sm.put("uni220E", new Symbol("\u220E"));
		// N-ARY PRODUCT ∏
		Sm.put("product", new Symbol("\u220F"));
		// N-ARY COPRODUCT ∐
		Sm.put("uni2210", new Symbol("\u2210"));
		// N-ARY SUMMATION ∑
		Sm.put("summation", new Symbol("\u2211"));
		// MINUS SIGN −
		Sm.put("minus", new Symbol("\u2212"));
		// MINUS-OR-PLUS SIGN ∓
		Sm.put("minusplus", new Symbol("\u2213"));
		// DOT PLUS ∔
		Sm.put("uni2214", new Symbol("\u2214"));
		// DIVISION SLASH ∕
		Sm.put("uni2215", new Symbol("\u2215"));
		// SET MINUS ∖
		Sm.put("uni2216", new Symbol("\u2216"));
		// ASTERISK OPERATOR ∗
		Sm.put("asteriskmath", new Symbol("\u2217"));
		// RING OPERATOR ∘
		Sm.put("uni2218", new Symbol("\u2218"));
		// BULLET OPERATOR ∙
		Sm.put("bulletoperator", new Symbol("\u2219"));
		// SQUARE ROOT √
		Sm.put("radical", new Symbol("\u221A"));
		// PROPORTIONAL TO ∝
		Sm.put("proportional", new Symbol("\u221D"));
		// INFINITY ∞
		Sm.put("infinity", new Symbol("\u221E"));
		// RIGHT ANGLE ∟
		Sm.put("uni221F", new Symbol("\u221F"));
		// ANGLE ∠
		Sm.put("uni2220", new Symbol("\u2220"));
		// MEASURED ANGLE ∡
		Sm.put("uni2221", new Symbol("\u2221"));
		// SPHERICAL ANGLE ∢
		Sm.put("uni2222", new Symbol("\u2222"));
		// DIVIDES ∣
		Sm.put("divides", new Symbol("\u2223"));
		// DOES NOT DIVIDE ∤
		Sm.put("uni2224", new Symbol("\u2224"));
		// PARALLEL TO ∥
		Sm.put("parallel", new Symbol("\u2225"));
		// NOT PARALLEL TO ∦
		Sm.put("uni2226", new Symbol("\u2226"));
		// LOGICAL AND ∧
		Sm.put("logicaland", new Symbol("\u2227"));
		// LOGICAL OR ∨
		Sm.put("logicalor", new Symbol("\u2228"));
		// INTERSECTION ∩
		Sm.put("intersection", new Symbol("\u2229"));
		// UNION ∪
		Sm.put("union", new Symbol("\u222A"));
		// INTEGRAL ∫
		Sm.put("integral", new Symbol("\u222B"));
		// DOUBLE INTEGRAL ∬
		Sm.put("uni222C", new Symbol("\u222C"));
		// TRIPLE INTEGRAL ∭
		Sm.put("uni222D", new Symbol("\u222D"));
		// CONTOUR INTEGRAL ∮
		Sm.put("contourintegral", new Symbol("\u222E"));
		// SURFACE INTEGRAL ∯
		Sm.put("uni222F", new Symbol("\u222F"));
		// VOLUME INTEGRAL ∰
		Sm.put("uni2230", new Symbol("\u2230"));
		// CLOCKWISE INTEGRAL ∱
		Sm.put("uni2231", new Symbol("\u2231"));
		// CLOCKWISE CONTOUR INTEGRAL ∲
		Sm.put("uni2232", new Symbol("\u2232"));
		// ANTICLOCKWISE CONTOUR INTEGRAL ∳
		Sm.put("uni2233", new Symbol("\u2233"));
		// THEREFORE ∴
		Sm.put("therefore", new Symbol("\u2234"));
		// BECAUSE ∵
		Sm.put("because", new Symbol("\u2235"));
		// RATIO ∶
		Sm.put("ratio", new Symbol("\u2236"));
		// PROPORTION ∷
		Sm.put("proportion", new Symbol("\u2237"));
		// DOT MINUS ∸
		Sm.put("uni2238", new Symbol("\u2238"));
		// EXCESS ∹
		Sm.put("uni2239", new Symbol("\u2239"));
		// GEOMETRIC PROPORTION ∺
		Sm.put("uni223A", new Symbol("\u223A"));
		// HOMOTHETIC ∻
		Sm.put("uni223B", new Symbol("\u223B"));
		// TILDE OPERATOR ∼
		Sm.put("similar", new Symbol("\u223C"));
		// REVERSED TILDE ∽
		Sm.put("uni223D", new Symbol("\u223D"));
		// INVERTED LAZY S ∾
		Sm.put("uni223E", new Symbol("\u223E"));
		// SINE WAVE ∿
		Sm.put("uni223F", new Symbol("\u223F"));
		// WREATH PRODUCT ≀
		Sm.put("uni2240", new Symbol("\u2240"));
		// NOT TILDE ≁
		Sm.put("uni2241", new Symbol("\u2241"));
		// MINUS TILDE ≂
		Sm.put("uni2242", new Symbol("\u2242"));
		// ASYMPTOTICALLY EQUAL TO ≃
		Sm.put("similar_equal", new Symbol("\u2243"));
		// NOT ASYMPTOTICALLY EQUAL TO ≄
		Sm.put("uni2244", new Symbol("\u2244"));
		// APPROXIMATELY EQUAL TO ≅
		Sm.put("uni2245", new Symbol("\u2245"));
		// APPROXIMATELY BUT NOT ACTUALLY EQUAL TO ≆
		Sm.put("uni2246", new Symbol("\u2246"));
		// NEITHER APPROXIMATELY NOR ACTUALLY EQUAL TO ≇
		Sm.put("uni2247", new Symbol("\u2247"));
		// ALMOST EQUAL TO ≈
		Sm.put("approxequal", new Symbol("\u2248"));
		// NOT ALMOST EQUAL TO ≉
		Sm.put("uni2249", new Symbol("\u2249"));
		// ALMOST EQUAL OR EQUAL TO ≊
		Sm.put("uni224A", new Symbol("\u224A"));
		// TRIPLE TILDE ≋
		Sm.put("uni224B", new Symbol("\u224B"));
		// ALL EQUAL TO ≌
		Sm.put("uni224C", new Symbol("\u224C"));
		// EQUIVALENT TO ≍
		Sm.put("uni224D", new Symbol("\u224D"));
		// GEOMETRICALLY EQUIVALENT TO ≎
		Sm.put("uni224E", new Symbol("\u224E"));
		// DIFFERENCE BETWEEN ≏
		Sm.put("uni224F", new Symbol("\u224F"));
		// APPROACHES THE LIMIT ≐
		Sm.put("uni2250", new Symbol("\u2250"));
		// GEOMETRICALLY EQUAL TO ≑
		Sm.put("uni2251", new Symbol("\u2251"));
		// APPROXIMATELY EQUAL TO OR THE IMAGE OF ≒
		Sm.put("uni2252", new Symbol("\u2252"));
		// IMAGE OF OR APPROXIMATELY EQUAL TO ≓
		Sm.put("uni2253", new Symbol("\u2253"));
		// COLON EQUALS ≔
		Sm.put("uni2254", new Symbol("\u2254"));
		// EQUALS COLON ≕
		Sm.put("uni2255", new Symbol("\u2255"));
		// RING IN EQUAL TO ≖
		Sm.put("uni2256", new Symbol("\u2256"));
		// RING EQUAL TO ≗
		Sm.put("uni2257", new Symbol("\u2257"));
		// CORRESPONDS TO ≘
		Sm.put("uni2258", new Symbol("\u2258"));
		// ESTIMATES ≙
		Sm.put("uni2259", new Symbol("\u2259"));
		// EQUIANGULAR TO ≚
		Sm.put("uni225A", new Symbol("\u225A"));
		// STAR EQUALS ≛
		Sm.put("uni225B", new Symbol("\u225B"));
		// DELTA EQUAL TO ≜
		Sm.put("uni225C", new Symbol("\u225C"));
		// EQUAL TO BY DEFINITION ≝
		Sm.put("uni225D", new Symbol("\u225D"));
		// MEASURED BY ≞
		Sm.put("uni225E", new Symbol("\u225E"));
		// QUESTIONED EQUAL TO ≟
		Sm.put("uni225F", new Symbol("\u225F"));
		// NOT EQUAL TO ≠
		Sm.put("notequal", new Symbol("\u2260"));
		// IDENTICAL TO ≡
		Sm.put("equivalence", new Symbol("\u2261"));
		// NOT IDENTICAL TO ≢
		Sm.put("uni2262", new Symbol("\u2262"));
		// STRICTLY EQUIVALENT TO ≣
		Sm.put("uni2263", new Symbol("\u2263"));
		// LESS-THAN OR EQUAL TO ≤
		Sm.put("lessequal", new Symbol("\u2264"));
		// GREATER-THAN OR EQUAL TO ≥
		Sm.put("greaterequal", new Symbol("\u2265"));
		// LESS-THAN OVER EQUAL TO ≦
		Sm.put("uni2266", new Symbol("\u2266"));
		// GREATER-THAN OVER EQUAL TO ≧
		Sm.put("uni2267", new Symbol("\u2267"));
		// LESS-THAN BUT NOT EQUAL TO ≨
		Sm.put("uni2268", new Symbol("\u2268"));
		// GREATER-THAN BUT NOT EQUAL TO ≩
		Sm.put("uni2269", new Symbol("\u2269"));
		// MUCH LESS-THAN ≪
		Sm.put("lessmuch", new Symbol("\u226A"));
		// MUCH GREATER-THAN ≫
		Sm.put("greatermuch", new Symbol("\u226B"));
		// BETWEEN ≬
		Sm.put("uni226C", new Symbol("\u226C"));
		// NOT EQUIVALENT TO ≭
		Sm.put("uni226D", new Symbol("\u226D"));
		// NOT LESS-THAN ≮
		Sm.put("uni226E", new Symbol("\u226E"));
		// NOT GREATER-THAN ≯
		Sm.put("uni226F", new Symbol("\u226F"));
		// NEITHER LESS-THAN NOR EQUAL TO ≰
		Sm.put("uni2270", new Symbol("\u2270"));
		// NEITHER GREATER-THAN NOR EQUAL TO ≱
		Sm.put("uni2271", new Symbol("\u2271"));
		// LESS-THAN OR EQUIVALENT TO ≲
		Sm.put("uni2272", new Symbol("\u2272"));
		// GREATER-THAN OR EQUIVALENT TO ≳
		Sm.put("uni2273", new Symbol("\u2273"));
		// NEITHER LESS-THAN NOR EQUIVALENT TO ≴
		Sm.put("uni2274", new Symbol("\u2274"));
		// NEITHER GREATER-THAN NOR EQUIVALENT TO ≵
		Sm.put("uni2275", new Symbol("\u2275"));
		// LESS-THAN OR GREATER-THAN ≶
		Sm.put("uni2276", new Symbol("\u2276"));
		// GREATER-THAN OR LESS-THAN ≷
		Sm.put("uni2277", new Symbol("\u2277"));
		// NEITHER LESS-THAN NOR GREATER-THAN ≸
		Sm.put("uni2278", new Symbol("\u2278"));
		// NEITHER GREATER-THAN NOR LESS-THAN ≹
		Sm.put("uni2279", new Symbol("\u2279"));
		// PRECEDES ≺
		Sm.put("uni227A", new Symbol("\u227A"));
		// SUCCEEDS ≻
		Sm.put("uni227B", new Symbol("\u227B"));
		// PRECEDES OR EQUAL TO ≼
		Sm.put("uni227C", new Symbol("\u227C"));
		// SUCCEEDS OR EQUAL TO ≽
		Sm.put("uni227D", new Symbol("\u227D"));
		// PRECEDES OR EQUIVALENT TO ≾
		Sm.put("uni227E", new Symbol("\u227E"));
		// SUCCEEDS OR EQUIVALENT TO ≿
		Sm.put("uni227F", new Symbol("\u227F"));
		// DOES NOT PRECEDE ⊀
		Sm.put("uni2280", new Symbol("\u2280"));
		// DOES NOT SUCCEED ⊁
		Sm.put("uni2281", new Symbol("\u2281"));
		// SUBSET OF ⊂
		Sm.put("propersubset", new Symbol("\u2282"));
		// SUPERSET OF ⊃
		Sm.put("propersuperset", new Symbol("\u2283"));
		// NOT A SUBSET OF ⊄
		Sm.put("uni2284", new Symbol("\u2284"));
		// NOT A SUPERSET OF ⊅
		Sm.put("uni2285", new Symbol("\u2285"));
		// SUBSET OF OR EQUAL TO ⊆
		Sm.put("reflexsubset", new Symbol("\u2286"));
		// SUPERSET OF OR EQUAL TO ⊇
		Sm.put("reflexsuperset", new Symbol("\u2287"));
		// NEITHER A SUBSET OF NOR EQUAL TO ⊈
		Sm.put("uni2288", new Symbol("\u2288"));
		// NEITHER A SUPERSET OF NOR EQUAL TO ⊉
		Sm.put("uni2289", new Symbol("\u2289"));
		// SUBSET OF WITH NOT EQUAL TO ⊊
		Sm.put("uni228A", new Symbol("\u228A"));
		// SUPERSET OF WITH NOT EQUAL TO ⊋
		Sm.put("uni228B", new Symbol("\u228B"));
		// MULTISET ⊌
		Sm.put("uni228C", new Symbol("\u228C"));
		// MULTISET MULTIPLICATION ⊍
		Sm.put("uni228D", new Symbol("\u228D"));
		// MULTISET UNION ⊎
		Sm.put("uni228E", new Symbol("\u228E"));
		// SQUARE IMAGE OF ⊏
		Sm.put("uni228F", new Symbol("\u228F"));
		// SQUARE ORIGINAL OF ⊐
		Sm.put("uni2290", new Symbol("\u2290"));
		// SQUARE IMAGE OF OR EQUAL TO ⊑
		Sm.put("uni2291", new Symbol("\u2291"));
		// SQUARE ORIGINAL OF OR EQUAL TO ⊒
		Sm.put("uni2292", new Symbol("\u2292"));
		// SQUARE CAP ⊓
		Sm.put("uni2293", new Symbol("\u2293"));
		// SQUARE CUP ⊔
		Sm.put("uni2294", new Symbol("\u2294"));
		// CIRCLED PLUS ⊕
		Sm.put("circleplus", new Symbol("\u2295"));
		// CIRCLED MINUS ⊖
		Sm.put("uni2296", new Symbol("\u2296"));
		// CIRCLED TIMES ⊗
		Sm.put("circlemultiply", new Symbol("\u2297"));
		// CIRCLED DIVISION SLASH ⊘
		Sm.put("circledivide", new Symbol("\u2298"));
		// CIRCLED DOT OPERATOR ⊙
		Sm.put("circledot", new Symbol("\u2299"));
		// CIRCLED RING OPERATOR ⊚
		Sm.put("uni229A", new Symbol("\u229A"));
		// CIRCLED ASTERISK OPERATOR ⊛
		Sm.put("uni229B", new Symbol("\u229B"));
		// CIRCLED EQUALS ⊜
		Sm.put("uni229C", new Symbol("\u229C"));
		// CIRCLED DASH ⊝
		Sm.put("uni229D", new Symbol("\u229D"));
		// SQUARED PLUS ⊞
		Sm.put("uni229E", new Symbol("\u229E"));
		// SQUARED MINUS ⊟
		Sm.put("uni229F", new Symbol("\u229F"));
		// SQUARED TIMES ⊠
		Sm.put("uni22A0", new Symbol("\u22A0"));
		// SQUARED DOT OPERATOR ⊡
		Sm.put("uni22A1", new Symbol("\u22A1"));
		// RIGHT TACK ⊢
		Sm.put("uni22A2", new Symbol("\u22A2"));
		// LEFT TACK ⊣
		Sm.put("uni22A3", new Symbol("\u22A3"));
		// DOWN TACK ⊤
		Sm.put("uni22A4", new Symbol("\u22A4"));
		// UP TACK ⊥
		Sm.put("uni22A5", new Symbol("\u22A5"));
		// ASSERTION ⊦
		Sm.put("uni22A6", new Symbol("\u22A6"));
		// MODELS ⊧
		Sm.put("uni22A7", new Symbol("\u22A7"));
		// TRUE ⊨
		Sm.put("uni22A8", new Symbol("\u22A8"));
		// FORCES ⊩
		Sm.put("uni22A9", new Symbol("\u22A9"));
		// TRIPLE VERTICAL BAR RIGHT TURNSTILE ⊪
		Sm.put("uni22AA", new Symbol("\u22AA"));
		// DOUBLE VERTICAL BAR DOUBLE RIGHT TURNSTILE ⊫
		Sm.put("uni22AB", new Symbol("\u22AB"));
		// DOES NOT PROVE ⊬
		Sm.put("uni22AC", new Symbol("\u22AC"));
		// NOT TRUE ⊭
		Sm.put("uni22AD", new Symbol("\u22AD"));
		// DOES NOT FORCE ⊮
		Sm.put("uni22AE", new Symbol("\u22AE"));
		// NEGATED DOUBLE VERTICAL BAR DOUBLE RIGHT TURNSTILE ⊯
		Sm.put("uni22AF", new Symbol("\u22AF"));
		// NORMAL SUBGROUP OF ⊲
		Sm.put("uni22B2", new Symbol("\u22B2"));
		// CONTAINS AS NORMAL SUBGROUP ⊳
		Sm.put("uni22B3", new Symbol("\u22B3"));
		// NORMAL SUBGROUP OF OR EQUAL TO ⊴
		Sm.put("uni22B4", new Symbol("\u22B4"));
		// CONTAINS AS NORMAL SUBGROUP OR EQUAL TO ⊵
		Sm.put("uni22B5", new Symbol("\u22B5"));
		// ORIGINAL OF ⊶
		Sm.put("uni22B6", new Symbol("\u22B6"));
		// IMAGE OF ⊷
		Sm.put("uni22B7", new Symbol("\u22B7"));
		// MULTIMAP ⊸
		Sm.put("uni22B8", new Symbol("\u22B8"));
		// HERMITIAN CONJUGATE MATRIX ⊹
		Sm.put("uni22B9", new Symbol("\u22B9"));
		// INTERCALATE ⊺
		Sm.put("uni22BA", new Symbol("\u22BA"));
		// XOR ⊻
		Sm.put("uni22BB", new Symbol("\u22BB"));
		// NAND ⊼
		Sm.put("uni22BC", new Symbol("\u22BC"));
		// NOR ⊽
		Sm.put("uni22BD", new Symbol("\u22BD"));
		// RIGHT ANGLE WITH ARC ⊾
		Sm.put("uni22BE", new Symbol("\u22BE"));
		// RIGHT TRIANGLE ⊿
		Sm.put("uni22BF", new Symbol("\u22BF"));
		// N-ARY LOGICAL AND ⋀
		Sm.put("uni22C0", new Symbol("\u22C0"));
		// N-ARY LOGICAL OR ⋁
		Sm.put("uni22C1", new Symbol("\u22C1"));
		// N-ARY INTERSECTION ⋂
		Sm.put("uni22C2", new Symbol("\u22C2"));
		// N-ARY UNION ⋃
		Sm.put("uni22C3", new Symbol("\u22C3"));
		// DIAMOND OPERATOR ⋄
		Sm.put("uni22C4", new Symbol("\u22C4"));
		// DOT OPERATOR ⋅
		Sm.put("uni22C5", new Symbol("\u22C5"));
		// STAR OPERATOR ⋆
		Sm.put("uni22C6", new Symbol("\u22C6"));
		// DIVISION TIMES ⋇
		Sm.put("uni22C7", new Symbol("\u22C7"));
		// BOWTIE ⋈
		Sm.put("uni22C8", new Symbol("\u22C8"));
		// LEFT NORMAL FACTOR SEMIDIRECT PRODUCT ⋉
		Sm.put("uni22C9", new Symbol("\u22C9"));
		// RIGHT NORMAL FACTOR SEMIDIRECT PRODUCT ⋊
		Sm.put("uni22CA", new Symbol("\u22CA"));
		// LEFT SEMIDIRECT PRODUCT ⋋
		Sm.put("uni22CB", new Symbol("\u22CB"));
		// RIGHT SEMIDIRECT PRODUCT ⋌
		Sm.put("uni22CC", new Symbol("\u22CC"));
		// REVERSED TILDE EQUALS ⋍
		Sm.put("uni22CD", new Symbol("\u22CD"));
		// CURLY LOGICAL OR ⋎
		Sm.put("uni22CE", new Symbol("\u22CE"));
		// CURLY LOGICAL AND ⋏
		Sm.put("uni22CF", new Symbol("\u22CF"));
		// DOUBLE SUBSET ⋐
		Sm.put("uni22D0", new Symbol("\u22D0"));
		// DOUBLE SUPERSET ⋑
		Sm.put("uni22D1", new Symbol("\u22D1"));
		// DOUBLE INTERSECTION ⋒
		Sm.put("uni22D2", new Symbol("\u22D2"));
		// DOUBLE UNION ⋓
		Sm.put("uni22D3", new Symbol("\u22D3"));
		// EQUAL AND PARALLEL TO ⋕
		Sm.put("uni22D5", new Symbol("\u22D5"));
		// LESS-THAN WITH DOT ⋖
		Sm.put("uni22D6", new Symbol("\u22D6"));
		// GREATER-THAN WITH DOT ⋗
		Sm.put("uni22D7", new Symbol("\u22D7"));
		// VERY MUCH LESS-THAN ⋘
		Sm.put("uni22D8", new Symbol("\u22D8"));
		// VERY MUCH GREATER-THAN ⋙
		Sm.put("uni22D9", new Symbol("\u22D9"));
		// LESS-THAN EQUAL TO OR GREATER-THAN ⋚
		Sm.put("uni22DA", new Symbol("\u22DA"));
		// GREATER-THAN EQUAL TO OR LESS-THAN ⋛
		Sm.put("uni22DB", new Symbol("\u22DB"));
		// EQUAL TO OR LESS-THAN ⋜
		Sm.put("uni22DC", new Symbol("\u22DC"));
		// EQUAL TO OR GREATER-THAN ⋝
		Sm.put("uni22DD", new Symbol("\u22DD"));
		// EQUAL TO OR PRECEDES ⋞
		Sm.put("uni22DE", new Symbol("\u22DE"));
		// EQUAL TO OR SUCCEEDS ⋟
		Sm.put("uni22DF", new Symbol("\u22DF"));
		// DOES NOT PRECEDE OR EQUAL ⋠
		Sm.put("uni22E0", new Symbol("\u22E0"));
		// DOES NOT SUCCEED OR EQUAL ⋡
		Sm.put("uni22E1", new Symbol("\u22E1"));
		// NOT SQUARE IMAGE OF OR EQUAL TO ⋢
		Sm.put("uni22E2", new Symbol("\u22E2"));
		// NOT SQUARE ORIGINAL OF OR EQUAL TO ⋣
		Sm.put("uni22E3", new Symbol("\u22E3"));
		// SQUARE IMAGE OF OR NOT EQUAL TO ⋤
		Sm.put("uni22E4", new Symbol("\u22E4"));
		// SQUARE ORIGINAL OF OR NOT EQUAL TO ⋥
		Sm.put("uni22E5", new Symbol("\u22E5"));
		// LESS-THAN BUT NOT EQUIVALENT TO ⋦
		Sm.put("uni22E6", new Symbol("\u22E6"));
		// GREATER-THAN BUT NOT EQUIVALENT TO ⋧
		Sm.put("uni22E7", new Symbol("\u22E7"));
		// PRECEDES BUT NOT EQUIVALENT TO ⋨
		Sm.put("uni22E8", new Symbol("\u22E8"));
		// SUCCEEDS BUT NOT EQUIVALENT TO ⋩
		Sm.put("uni22E9", new Symbol("\u22E9"));
		// NOT NORMAL SUBGROUP OF ⋪
		Sm.put("uni22EA", new Symbol("\u22EA"));
		// DOES NOT CONTAIN AS NORMAL SUBGROUP ⋫
		Sm.put("uni22EB", new Symbol("\u22EB"));
		// NOT NORMAL SUBGROUP OF OR EQUAL TO ⋬
		Sm.put("uni22EC", new Symbol("\u22EC"));
		// DOES NOT CONTAIN AS NORMAL SUBGROUP OR EQUAL ⋭
		Sm.put("uni22ED", new Symbol("\u22ED"));
		// VERTICAL ELLIPSIS ⋮
		Sm.put("uni22EE", new Symbol("\u22EE"));
		// MIDLINE HORIZONTAL ELLIPSIS ⋯
		Sm.put("uni22EF", new Symbol("\u22EF"));
		// UP RIGHT DIAGONAL ELLIPSIS ⋰
		Sm.put("uni22F0", new Symbol("\u22F0"));
		// DOWN RIGHT DIAGONAL ELLIPSIS ⋱
		Sm.put("uni22F1", new Symbol("\u22F1"));
		// TOP HALF INTEGRAL ⌠
		Sm.put("integraltp", new Symbol("\u2320"));
		// BOTTOM HALF INTEGRAL ⌡
		Sm.put("integralbt", new Symbol("\u2321"));
		// LEFT PARENTHESIS UPPER HOOK ⎛
		Sm.put("uni239B", new Symbol("\u239B"));
		// LEFT PARENTHESIS EXTENSION ⎜
		Sm.put("uni239C", new Symbol("\u239C"));
		// LEFT PARENTHESIS LOWER HOOK ⎝
		Sm.put("uni239D", new Symbol("\u239D"));
		// RIGHT PARENTHESIS UPPER HOOK ⎞
		Sm.put("uni239E", new Symbol("\u239E"));
		// RIGHT PARENTHESIS EXTENSION ⎟
		Sm.put("uni239F", new Symbol("\u239F"));
		// RIGHT PARENTHESIS LOWER HOOK ⎠
		Sm.put("uni23A0", new Symbol("\u23A0"));
		// LEFT SQUARE BRACKET UPPER CORNER ⎡
		Sm.put("uni23A1", new Symbol("\u23A1"));
		// LEFT SQUARE BRACKET EXTENSION ⎢
		Sm.put("uni23A2", new Symbol("\u23A2"));
		// LEFT SQUARE BRACKET LOWER CORNER ⎣
		Sm.put("uni23A3", new Symbol("\u23A3"));
		// RIGHT SQUARE BRACKET UPPER CORNER ⎤
		Sm.put("uni23A4", new Symbol("\u23A4"));
		// RIGHT SQUARE BRACKET EXTENSION ⎥
		Sm.put("uni23A5", new Symbol("\u23A5"));
		// RIGHT SQUARE BRACKET LOWER CORNER ⎦
		Sm.put("uni23A6", new Symbol("\u23A6"));
		// LEFT CURLY BRACKET UPPER HOOK ⎧
		Sm.put("uni23A7", new Symbol("\u23A7"));
		// LEFT CURLY BRACKET MIDDLE PIECE ⎨
		Sm.put("uni23A8", new Symbol("\u23A8"));
		// LEFT CURLY BRACKET LOWER HOOK ⎩
		Sm.put("uni23A9", new Symbol("\u23A9"));
		// CURLY BRACKET EXTENSION ⎪
		Sm.put("uni23AA", new Symbol("\u23AA"));
		// RIGHT CURLY BRACKET UPPER HOOK ⎫
		Sm.put("uni23AB", new Symbol("\u23AB"));
		// RIGHT CURLY BRACKET MIDDLE PIECE ⎬
		Sm.put("uni23AC", new Symbol("\u23AC"));
		// RIGHT CURLY BRACKET LOWER HOOK ⎭
		Sm.put("uni23AD", new Symbol("\u23AD"));
		// SUMMATION TOP ⎲
		Sm.put("uni23B2", new Symbol("\u23B2"));
		// SUMMATION BOTTOM ⎳
		Sm.put("uni23B3", new Symbol("\u23B3"));
		// TOP PARENTHESIS ⏜
		Sm.put("uni23DC", new Symbol("\u23DC"));
		// BOTTOM PARENTHESIS ⏝
		Sm.put("uni23DD", new Symbol("\u23DD"));
		// TOP CURLY BRACKET ⏞
		Sm.put("uni23DE", new Symbol("\u23DE"));
		// BOTTOM CURLY BRACKET ⏟
		Sm.put("uni23DF", new Symbol("\u23DF"));
		// TOP TORTOISE SHELL BRACKET ⏠
		Sm.put("uni23E0", new Symbol("\u23E0"));
		// BOTTOM TORTOISE SHELL BRACKET ⏡
		Sm.put("uni23E1", new Symbol("\u23E1"));
		// WHITE RIGHT-POINTING TRIANGLE ▷
		Sm.put("uni25B7", new Symbol("\u25B7"));
		// WHITE LEFT-POINTING TRIANGLE ◁
		Sm.put("uni25C1", new Symbol("\u25C1"));
		// MUSIC SHARP SIGN ♯
		Sm.put("musicsharpsign", new Symbol("\u266F"));
		// PERPENDICULAR ⟂
		Sm.put("uni27C2", new Symbol("\u27C2"));
		// LARGE UP TACK ⟘
		Sm.put("uni27D8", new Symbol("\u27D8"));
		// LARGE DOWN TACK ⟙
		Sm.put("uni27D9", new Symbol("\u27D9"));
		// LEFT AND RIGHT DOUBLE TURNSTILE ⟚
		Sm.put("uni27DA", new Symbol("\u27DA"));
		// LEFT AND RIGHT TACK ⟛
		Sm.put("uni27DB", new Symbol("\u27DB"));
		// LEFT MULTIMAP ⟜
		Sm.put("uni27DC", new Symbol("\u27DC"));
		// LONG RIGHT TACK ⟝
		Sm.put("uni27DD", new Symbol("\u27DD"));
		// LONG LEFT TACK ⟞
		Sm.put("uni27DE", new Symbol("\u27DE"));
		// LOZENGE DIVIDED BY HORIZONTAL RULE ⟠
		Sm.put("uni27E0", new Symbol("\u27E0"));
		// WHITE CONCAVE-SIDED DIAMOND ⟡
		Sm.put("uni27E1", new Symbol("\u27E1"));
		// WHITE CONCAVE-SIDED DIAMOND WITH LEFTWARDS TICK ⟢
		Sm.put("uni27E2", new Symbol("\u27E2"));
		// WHITE CONCAVE-SIDED DIAMOND WITH RIGHTWARDS TICK ⟣
		Sm.put("uni27E3", new Symbol("\u27E3"));
		// RIGHT ARROW WITH CIRCLED PLUS ⟴
		Sm.put("uni27F4", new Symbol("\u27F4"));
		// LONG LEFTWARDS ARROW ⟵
		Sm.put("uni27F5", new Symbol("\u27F5"));
		// LONG RIGHTWARDS ARROW ⟶
		Sm.put("uni27F6", new Symbol("\u27F6"));
		// LONG LEFT RIGHT ARROW ⟷
		Sm.put("uni27F7", new Symbol("\u27F7"));
		// LONG LEFTWARDS DOUBLE ARROW ⟸
		Sm.put("uni27F8", new Symbol("\u27F8"));
		// LONG RIGHTWARDS DOUBLE ARROW ⟹
		Sm.put("uni27F9", new Symbol("\u27F9"));
		// LONG LEFT RIGHT DOUBLE ARROW ⟺
		Sm.put("uni27FA", new Symbol("\u27FA"));
		// LONG LEFTWARDS ARROW FROM BAR ⟻
		Sm.put("uni27FB", new Symbol("\u27FB"));
		// LONG RIGHTWARDS ARROW FROM BAR ⟼
		Sm.put("uni27FC", new Symbol("\u27FC"));
		// LONG LEFTWARDS DOUBLE ARROW FROM BAR ⟽
		Sm.put("uni27FD", new Symbol("\u27FD"));
		// LONG RIGHTWARDS DOUBLE ARROW FROM BAR ⟾
		Sm.put("uni27FE", new Symbol("\u27FE"));
		// LONG RIGHTWARDS SQUIGGLE ARROW ⟿
		Sm.put("uni27FF", new Symbol("\u27FF"));
		// LEFTWARDS DOUBLE ARROW FROM BAR ⤆
		Sm.put("uni2906", new Symbol("\u2906"));
		// RIGHTWARDS DOUBLE ARROW FROM BAR ⤇
		Sm.put("uni2907", new Symbol("\u2907"));
		// N-ARY CIRCLED DOT OPERATOR ⨀
		Sm.put("uni2A00", new Symbol("\u2A00"));
		// N-ARY CIRCLED PLUS OPERATOR ⨁
		Sm.put("uni2A01", new Symbol("\u2A01"));
		// N-ARY CIRCLED TIMES OPERATOR ⨂
		Sm.put("uni2A02", new Symbol("\u2A02"));
		// N-ARY UNION OPERATOR WITH DOT ⨃
		Sm.put("uni2A03", new Symbol("\u2A03"));
		// N-ARY UNION OPERATOR WITH PLUS ⨄
		Sm.put("uni2A04", new Symbol("\u2A04"));
		// N-ARY SQUARE INTERSECTION OPERATOR ⨅
		Sm.put("uni2A05", new Symbol("\u2A05"));
		// N-ARY SQUARE UNION OPERATOR ⨆
		Sm.put("uni2A06", new Symbol("\u2A06"));
		// N-ARY TIMES OPERATOR ⨉
		Sm.put("uni2A09", new Symbol("\u2A09"));
		// QUADRUPLE INTEGRAL OPERATOR ⨌
		Sm.put("uni2A0C", new Symbol("\u2A0C"));
		// ANTICLOCKWISE INTEGRATION ⨑
		Sm.put("uni2A11", new Symbol("\u2A11"));
		// VECTOR OR CROSS PRODUCT ⨯
		Sm.put("uni2A2F", new Symbol("\u2A2F"));
		// AMALGAMATION OR COPRODUCT ⨿
		Sm.put("uni2A3F", new Symbol("\u2A3F"));
		// LESS-THAN OR SLANTED EQUAL TO ⩽
		Sm.put("uni2A7D", new Symbol("\u2A7D"));
		// GREATER-THAN OR SLANTED EQUAL TO ⩾
		Sm.put("uni2A7E", new Symbol("\u2A7E"));
		// LESS-THAN OR APPROXIMATE ⪅
		Sm.put("uni2A85", new Symbol("\u2A85"));
		// GREATER-THAN OR APPROXIMATE ⪆
		Sm.put("uni2A86", new Symbol("\u2A86"));
		// LESS-THAN AND SINGLE-LINE NOT EQUAL TO ⪇
		Sm.put("uni2A87", new Symbol("\u2A87"));
		// GREATER-THAN AND SINGLE-LINE NOT EQUAL TO ⪈
		Sm.put("uni2A88", new Symbol("\u2A88"));
		// LESS-THAN AND NOT APPROXIMATE ⪉
		Sm.put("uni2A89", new Symbol("\u2A89"));
		// GREATER-THAN AND NOT APPROXIMATE ⪊
		Sm.put("uni2A8A", new Symbol("\u2A8A"));
		// LESS-THAN ABOVE DOUBLE-LINE EQUAL ABOVE GREATER-THAN ⪋
		Sm.put("uni2A8B", new Symbol("\u2A8B"));
		// GREATER-THAN ABOVE DOUBLE-LINE EQUAL ABOVE LESS-THAN ⪌
		Sm.put("uni2A8C", new Symbol("\u2A8C"));
		// SLANTED EQUAL TO OR LESS-THAN ⪕
		Sm.put("uni2A95", new Symbol("\u2A95"));
		// SLANTED EQUAL TO OR GREATER-THAN ⪖
		Sm.put("uni2A96", new Symbol("\u2A96"));
		// PRECEDES ABOVE SINGLE-LINE EQUALS SIGN ⪯
		Sm.put("uni2AAF", new Symbol("\u2AAF"));
		// SUCCEEDS ABOVE SINGLE-LINE EQUALS SIGN ⪰
		Sm.put("uni2AB0", new Symbol("\u2AB0"));
		// THREE LEFTWARDS ARROWS ⬱
		Sm.put("uni2B31", new Symbol("\u2B31"));
		// LONG LEFTWARDS SQUIGGLE ARROW ⬳
		Sm.put("uni2B33", new Symbol("\u2B33"));
		// MATHEMATICAL BOLD NABLA 𝛁
		Sm.put("u1D6C1", new Symbol("\u1D6C1"));
		// MATHEMATICAL BOLD PARTIAL DIFFERENTIAL 𝛛
		Sm.put("u1D6DB", new Symbol("\u1D6DB"));
		// MATHEMATICAL ITALIC NABLA 𝛻
		Sm.put("u1D6FB", new Symbol("\u1D6FB"));
		// MATHEMATICAL ITALIC PARTIAL DIFFERENTIAL 𝜕
		Sm.put("u1D715", new Symbol("\u1D715"));
		// MATHEMATICAL BOLD ITALIC NABLA 𝜵
		Sm.put("u1D735", new Symbol("\u1D735"));
		// MATHEMATICAL BOLD ITALIC PARTIAL DIFFERENTIAL 𝝏
		Sm.put("u1D74F", new Symbol("\u1D74F"));
		// MATHEMATICAL SANS-SERIF BOLD NABLA 𝝯
		Sm.put("u1D76F", new Symbol("\u1D76F"));
		// MATHEMATICAL SANS-SERIF BOLD PARTIAL DIFFERENTIAL 𝞉
		Sm.put("u1D789", new Symbol("\u1D789"));
		// MATHEMATICAL SANS-SERIF BOLD ITALIC NABLA 𝞩
		Sm.put("u1D7A9", new Symbol("\u1D7A9"));
		// MATHEMATICAL SANS-SERIF BOLD ITALIC PARTIAL DIFFERENTIAL 𝟃
		Sm.put("u1D7C3", new Symbol("\u1D7C3"));

		all.putAll(Sm);


		// BROKEN BAR ¦
		So.put("brokenbar", new Symbol("\u00A6"));
		// COPYRIGHT SIGN ©
		So.put("copyright", new Symbol("\u00A9"));
		// REGISTERED SIGN ®
		So.put("registered", new Symbol("\u00AE"));
		// DEGREE SIGN °
		So.put("degree", new Symbol("\u00B0"));
		// DEGREE CELSIUS ℃
		So.put("centigrade", new Symbol("\u2103"));
		// DEGREE FAHRENHEIT ℉
		So.put("fahrenheit", new Symbol("\u2109"));
		// NUMERO SIGN №
		So.put("numero", new Symbol("\u2116"));
		// SOUND RECORDING COPYRIGHT ℗
		So.put("published", new Symbol("\u2117"));
		// PRESCRIPTION TAKE ℞
		So.put("recipe", new Symbol("\u211E"));
		// SERVICE MARK ℠
		So.put("servicemark", new Symbol("\u2120"));
		// TRADE MARK SIGN ™
		So.put("trademark", new Symbol("\u2122"));
		// INVERTED OHM SIGN ℧
		So.put("uni2127", new Symbol("\u2127"));
		// ESTIMATED SYMBOL ℮
		So.put("estimated", new Symbol("\u212E"));
		// UP DOWN ARROW ↕
		So.put("arrowupdn", new Symbol("\u2195"));
		// NORTH WEST ARROW ↖
		So.put("uni2196", new Symbol("\u2196"));
		// NORTH EAST ARROW ↗
		So.put("uni2197", new Symbol("\u2197"));
		// SOUTH EAST ARROW ↘
		So.put("uni2198", new Symbol("\u2198"));
		// SOUTH WEST ARROW ↙
		So.put("uni2199", new Symbol("\u2199"));
		// LEFTWARDS TWO HEADED ARROW ↞
		So.put("uni219E", new Symbol("\u219E"));
		// UPWARDS TWO HEADED ARROW ↟
		So.put("uni219F", new Symbol("\u219F"));
		// DOWNWARDS TWO HEADED ARROW ↡
		So.put("uni21A1", new Symbol("\u21A1"));
		// LEFTWARDS ARROW WITH TAIL ↢
		So.put("uni21A2", new Symbol("\u21A2"));
		// LEFTWARDS ARROW FROM BAR ↤
		So.put("uni21A4", new Symbol("\u21A4"));
		// UPWARDS ARROW FROM BAR ↥
		So.put("uni21A5", new Symbol("\u21A5"));
		// DOWNWARDS ARROW FROM BAR ↧
		So.put("uni21A7", new Symbol("\u21A7"));
		// LEFTWARDS ARROW WITH HOOK ↩
		So.put("uni21A9", new Symbol("\u21A9"));
		// RIGHTWARDS ARROW WITH HOOK ↪
		So.put("uni21AA", new Symbol("\u21AA"));
		// LEFTWARDS ARROW WITH LOOP ↫
		So.put("uni21AB", new Symbol("\u21AB"));
		// RIGHTWARDS ARROW WITH LOOP ↬
		So.put("uni21AC", new Symbol("\u21AC"));
		// LEFT RIGHT WAVE ARROW ↭
		So.put("uni21AD", new Symbol("\u21AD"));
		// UPWARDS ARROW WITH TIP LEFTWARDS ↰
		So.put("uni21B0", new Symbol("\u21B0"));
		// UPWARDS ARROW WITH TIP RIGHTWARDS ↱
		So.put("uni21B1", new Symbol("\u21B1"));
		// DOWNWARDS ARROW WITH TIP LEFTWARDS ↲
		So.put("uni21B2", new Symbol("\u21B2"));
		// DOWNWARDS ARROW WITH TIP RIGHTWARDS ↳
		So.put("uni21B3", new Symbol("\u21B3"));
		// RIGHTWARDS ARROW WITH CORNER DOWNWARDS ↴
		So.put("uni21B4", new Symbol("\u21B4"));
		// DOWNWARDS ARROW WITH CORNER LEFTWARDS ↵
		So.put("carriagereturn", new Symbol("\u21B5"));
		// ANTICLOCKWISE TOP SEMICIRCLE ARROW ↶
		So.put("uni21B6", new Symbol("\u21B6"));
		// CLOCKWISE TOP SEMICIRCLE ARROW ↷
		So.put("uni21B7", new Symbol("\u21B7"));
		// ANTICLOCKWISE OPEN CIRCLE ARROW ↺
		So.put("uni21BA", new Symbol("\u21BA"));
		// CLOCKWISE OPEN CIRCLE ARROW ↻
		So.put("uni21BB", new Symbol("\u21BB"));
		// LEFTWARDS HARPOON WITH BARB UPWARDS ↼
		So.put("uni21BC", new Symbol("\u21BC"));
		// LEFTWARDS HARPOON WITH BARB DOWNWARDS ↽
		So.put("uni21BD", new Symbol("\u21BD"));
		// UPWARDS HARPOON WITH BARB RIGHTWARDS ↾
		So.put("uni21BE", new Symbol("\u21BE"));
		// UPWARDS HARPOON WITH BARB LEFTWARDS ↿
		So.put("uni21BF", new Symbol("\u21BF"));
		// RIGHTWARDS HARPOON WITH BARB UPWARDS ⇀
		So.put("uni21C0", new Symbol("\u21C0"));
		// RIGHTWARDS HARPOON WITH BARB DOWNWARDS ⇁
		So.put("uni21C1", new Symbol("\u21C1"));
		// DOWNWARDS HARPOON WITH BARB RIGHTWARDS ⇂
		So.put("uni21C2", new Symbol("\u21C2"));
		// DOWNWARDS HARPOON WITH BARB LEFTWARDS ⇃
		So.put("uni21C3", new Symbol("\u21C3"));
		// RIGHTWARDS ARROW OVER LEFTWARDS ARROW ⇄
		So.put("uni21C4", new Symbol("\u21C4"));
		// UPWARDS ARROW LEFTWARDS OF DOWNWARDS ARROW ⇅
		So.put("uni21C5", new Symbol("\u21C5"));
		// LEFTWARDS ARROW OVER RIGHTWARDS ARROW ⇆
		So.put("uni21C6", new Symbol("\u21C6"));
		// LEFTWARDS PAIRED ARROWS ⇇
		So.put("uni21C7", new Symbol("\u21C7"));
		// UPWARDS PAIRED ARROWS ⇈
		So.put("uni21C8", new Symbol("\u21C8"));
		// RIGHTWARDS PAIRED ARROWS ⇉
		So.put("uni21C9", new Symbol("\u21C9"));
		// DOWNWARDS PAIRED ARROWS ⇊
		So.put("uni21CA", new Symbol("\u21CA"));
		// LEFTWARDS HARPOON OVER RIGHTWARDS HARPOON ⇋
		So.put("uni21CB", new Symbol("\u21CB"));
		// RIGHTWARDS HARPOON OVER LEFTWARDS HARPOON ⇌
		So.put("uni21CC", new Symbol("\u21CC"));
		// LEFTWARDS DOUBLE ARROW WITH STROKE ⇍
		So.put("uni21CD", new Symbol("\u21CD"));
		// LEFTWARDS DOUBLE ARROW ⇐
		So.put("arrowdblleft", new Symbol("\u21D0"));
		// UPWARDS DOUBLE ARROW ⇑
		So.put("arrowdblup", new Symbol("\u21D1"));
		// DOWNWARDS DOUBLE ARROW ⇓
		So.put("arrowdbldown", new Symbol("\u21D3"));
		// UP DOWN DOUBLE ARROW ⇕
		So.put("uni21D5", new Symbol("\u21D5"));
		// NORTH WEST DOUBLE ARROW ⇖
		So.put("uni21D6", new Symbol("\u21D6"));
		// NORTH EAST DOUBLE ARROW ⇗
		So.put("uni21D7", new Symbol("\u21D7"));
		// SOUTH EAST DOUBLE ARROW ⇘
		So.put("uni21D8", new Symbol("\u21D8"));
		// SOUTH WEST DOUBLE ARROW ⇙
		So.put("uni21D9", new Symbol("\u21D9"));
		// LEFTWARDS TRIPLE ARROW ⇚
		So.put("uni21DA", new Symbol("\u21DA"));
		// RIGHTWARDS TRIPLE ARROW ⇛
		So.put("uni21DB", new Symbol("\u21DB"));
		// LEFTWARDS SQUIGGLE ARROW ⇜
		So.put("uni21DC", new Symbol("\u21DC"));
		// RIGHTWARDS SQUIGGLE ARROW ⇝
		So.put("uni21DD", new Symbol("\u21DD"));
		// LEFTWARDS WHITE ARROW ⇦
		So.put("uni21E6", new Symbol("\u21E6"));
		// UPWARDS WHITE ARROW ⇧
		So.put("uni21E7", new Symbol("\u21E7"));
		// RIGHTWARDS WHITE ARROW ⇨
		So.put("uni21E8", new Symbol("\u21E8"));
		// DOWNWARDS WHITE ARROW ⇩
		So.put("uni21E9", new Symbol("\u21E9"));
		// UP DOWN WHITE ARROW ⇳
		So.put("uni21F3", new Symbol("\u21F3"));
		// DIAMETER SIGN ⌀
		So.put("uni2300", new Symbol("\u2300"));
		// PROJECTIVE ⌅
		So.put("uni2305", new Symbol("\u2305"));
		// PERSPECTIVE ⌆
		So.put("uni2306", new Symbol("\u2306"));
		// REVERSED NOT SIGN ⌐
		So.put("revlogicalnot", new Symbol("\u2310"));
		// TURNED NOT SIGN ⌙
		So.put("uni2319", new Symbol("\u2319"));
		// TOP LEFT CORNER ⌜
		So.put("uni231C", new Symbol("\u231C"));
		// TOP RIGHT CORNER ⌝
		So.put("uni231D", new Symbol("\u231D"));
		// BOTTOM LEFT CORNER ⌞
		So.put("uni231E", new Symbol("\u231E"));
		// BOTTOM RIGHT CORNER ⌟
		So.put("uni231F", new Symbol("\u231F"));
		// FROWN ⌢
		So.put("uni2322", new Symbol("\u2322"));
		// SMILE ⌣
		So.put("uni2323", new Symbol("\u2323"));
		// TOP SQUARE BRACKET ⎴
		So.put("uni23B4", new Symbol("\u23B4"));
		// BOTTOM SQUARE BRACKET ⎵
		So.put("uni23B5", new Symbol("\u23B5"));
		// RADICAL SYMBOL BOTTOM ⎷
		So.put("uni23B7", new Symbol("\u23B7"));
		// VERTICAL LINE EXTENSION ⏐
		So.put("uni23D0", new Symbol("\u23D0"));
		// BLANK SYMBOL ␢
		So.put("blanksymbol", new Symbol("\u2422"));
		// OPEN BOX ␣
		So.put("uni2423", new Symbol("\u2423"));
		// BOX DRAWINGS LIGHT HORIZONTAL ─
		So.put("SF100000", new Symbol("\u2500"));
		// BOX DRAWINGS LIGHT VERTICAL │
		So.put("SF110000", new Symbol("\u2502"));
		// BOX DRAWINGS LIGHT DOWN AND RIGHT ┌
		So.put("SF010000", new Symbol("\u250C"));
		// BOX DRAWINGS LIGHT DOWN AND LEFT ┐
		So.put("SF030000", new Symbol("\u2510"));
		// BOX DRAWINGS LIGHT UP AND RIGHT └
		So.put("SF020000", new Symbol("\u2514"));
		// BOX DRAWINGS LIGHT UP AND LEFT ┘
		So.put("SF040000", new Symbol("\u2518"));
		// BOX DRAWINGS LIGHT VERTICAL AND RIGHT ├
		So.put("SF080000", new Symbol("\u251C"));
		// BOX DRAWINGS LIGHT VERTICAL AND LEFT ┤
		So.put("SF090000", new Symbol("\u2524"));
		// BOX DRAWINGS LIGHT DOWN AND HORIZONTAL ┬
		So.put("SF060000", new Symbol("\u252C"));
		// BOX DRAWINGS LIGHT UP AND HORIZONTAL ┴
		So.put("SF070000", new Symbol("\u2534"));
		// BOX DRAWINGS LIGHT VERTICAL AND HORIZONTAL ┼
		So.put("SF050000", new Symbol("\u253C"));
		// LOWER ONE EIGHTH BLOCK ▁
		So.put("uni2581", new Symbol("\u2581"));
		// FULL BLOCK █
		So.put("block", new Symbol("\u2588"));
		// LIGHT SHADE ░
		So.put("ltshade", new Symbol("\u2591"));
		// MEDIUM SHADE ▒
		So.put("shade", new Symbol("\u2592"));
		// DARK SHADE ▓
		So.put("dkshade", new Symbol("\u2593"));
		// BLACK SQUARE ■
		So.put("filledbox", new Symbol("\u25A0"));
		// WHITE SQUARE □
		So.put("H22073", new Symbol("\u25A1"));
		// BLACK SMALL SQUARE ▪
		So.put("H18543", new Symbol("\u25AA"));
		// WHITE SMALL SQUARE ▫
		So.put("H18551", new Symbol("\u25AB"));
		// BLACK RECTANGLE ▬
		So.put("filledrect", new Symbol("\u25AC"));
		// WHITE RECTANGLE ▭
		So.put("uni25AD", new Symbol("\u25AD"));
		// BLACK UP-POINTING TRIANGLE ▲
		So.put("triagup", new Symbol("\u25B2"));
		// WHITE UP-POINTING TRIANGLE △
		So.put("uni25B3", new Symbol("\u25B3"));
		// BLACK RIGHT-POINTING TRIANGLE ▶
		So.put("uni25B6", new Symbol("\u25B6"));
		// BLACK DOWN-POINTING TRIANGLE ▼
		So.put("triagdn", new Symbol("\u25BC"));
		// WHITE DOWN-POINTING TRIANGLE ▽
		So.put("uni25BD", new Symbol("\u25BD"));
		// BLACK LEFT-POINTING TRIANGLE ◀
		So.put("uni25C0", new Symbol("\u25C0"));
		// LOZENGE ◊
		So.put("lozenge", new Symbol("\u25CA"));
		// WHITE CIRCLE ○
		So.put("circle", new Symbol("\u25CB"));
		// BLACK CIRCLE ●
		So.put("H18533", new Symbol("\u25CF"));
		// WHITE BULLET ◦
		So.put("openbullet", new Symbol("\u25E6"));
		// LARGE CIRCLE ◯
		So.put("uni25EF", new Symbol("\u25EF"));
		// BLACK SPADE SUIT ♠
		So.put("spade", new Symbol("\u2660"));
		// WHITE HEART SUIT ♡
		So.put("heartsuitwhite", new Symbol("\u2661"));
		// WHITE DIAMOND SUIT ♢
		So.put("diamondsuitwhite", new Symbol("\u2662"));
		// BLACK CLUB SUIT ♣
		So.put("club", new Symbol("\u2663"));
		// WHITE SPADE SUIT ♤
		So.put("spadesuitwhite", new Symbol("\u2664"));
		// BLACK HEART SUIT ♥
		So.put("heart", new Symbol("\u2665"));
		// BLACK DIAMOND SUIT ♦
		So.put("diamond", new Symbol("\u2666"));
		// WHITE CLUB SUIT ♧
		So.put("clubsuitwhite", new Symbol("\u2667"));
		// EIGHTH NOTE ♪
		So.put("musicalnote", new Symbol("\u266A"));
		// MUSIC FLAT SIGN ♭
		So.put("musicflatsign", new Symbol("\u266D"));
		// MUSIC NATURAL SIGN ♮
		So.put("uni266E", new Symbol("\u266E"));
		// MARRIAGE SYMBOL ⚭
		So.put("married", new Symbol("\u26AD"));
		// DIVORCE SYMBOL ⚮
		So.put("divorced", new Symbol("\u26AE"));
		// CHECK MARK ✓
		So.put("checkmark", new Symbol("\u2713"));
		// MALTESE CROSS ✠
		So.put("uni2720", new Symbol("\u2720"));
		// HEAVY VERTICAL BAR ❚
		So.put("uni275A", new Symbol("\u275A"));
		// BLACK RIGHTWARDS ARROW ➡
		So.put("uni27A1", new Symbol("\u27A1"));
		// LEFT RIGHT WHITE ARROW ⬄
		So.put("uni2B04", new Symbol("\u2B04"));
		// LEFTWARDS BLACK ARROW ⬅
		So.put("uni2B05", new Symbol("\u2B05"));
		// UPWARDS BLACK ARROW ⬆
		So.put("uni2B06", new Symbol("\u2B06"));
		// DOWNWARDS BLACK ARROW ⬇
		So.put("uni2B07", new Symbol("\u2B07"));
		// LEFT RIGHT BLACK ARROW ⬌
		So.put("uni2B0C", new Symbol("\u2B0C"));
		// UP DOWN BLACK ARROW ⬍
		So.put("uni2B0D", new Symbol("\u2B0D"));
		// DOTTED SQUARE ⬚
		So.put("uni2B1A", new Symbol("\u2B1A"));

		all.putAll(So);


		// EXCLAMATION MARK !
		Po.put("exclam", new Symbol("\u0021"));
		// QUOTATION MARK "
		Po.put("quotedbl", new Symbol("\""));
		// NUMBER SIGN #
		Po.put("numbersign", new Symbol("\u0023"));
		// PERCENT SIGN %
		Po.put("percent", new Symbol("\u0025"));
		// AMPERSAND &
		Po.put("ampersand", new Symbol("\u0026"));
		// APOSTROPHE '
		Po.put("quotesingle", new Symbol("\u0027"));
		// ASTERISK *
		Po.put("asterisk", new Symbol("\u002A"));
		// COMMA ,
		Po.put("comma", new Symbol("\u002C"));
		// FULL STOP .
		Po.put("period", new Symbol("\u002E"));
		// SOLIDUS /
		Po.put("slash", new Symbol("\u002F"));
		// COLON :
		Po.put("colon", new Symbol("\u003A"));
		// SEMICOLON ;
		Po.put("semicolon", new Symbol("\u003B"));
		// QUESTION MARK ?
		Po.put("question", new Symbol("\u003F"));
		// COMMERCIAL AT @
		Po.put("at", new Symbol("\u0040"));
		// REVERSE SOLIDUS \
		Po.put("backslash", new Symbol("\\"));
		// INVERTED EXCLAMATION MARK ¡
		Po.put("exclamdown", new Symbol("\u00A1", Symbol.FLAG_USE_BASELINE));
		// SECTION SIGN §
		Po.put("section", new Symbol("\u00A7", Symbol.FLAG_USE_BASELINE | Symbol.FLAG_INCLUDE_PADDING));
		// PILCROW SIGN ¶
		Po.put("paragraph", new Symbol("\u00B6", Symbol.FLAG_USE_BASELINE | Symbol.FLAG_INCLUDE_PADDING));
		// MIDDLE DOT ·
		Po.put("periodcentered", new Symbol("\u00B7"));
		// INVERTED QUESTION MARK ¿
		Po.put("questiondown", new Symbol("\u00BF", Symbol.FLAG_USE_BASELINE));
		// DOUBLE VERTICAL LINE ‖
		Po.put("dblverticalbar", new Symbol("\u2016"));
		// DOUBLE LOW LINE ‗
		Po.put("uni2017", new Symbol("\u2017", Symbol.FLAG_USE_BASELINE | Symbol.FLAG_INCLUDE_PADDING));
		// DAGGER †
		Po.put("dagger", new Symbol("\u2020"));
		// DOUBLE DAGGER ‡
		Po.put("daggerdbl", new Symbol("\u2021", Symbol.FLAG_USE_BASELINE));
		// BULLET •
		Po.put("bullet", new Symbol("\u2022"));
		// HORIZONTAL ELLIPSIS …
		Po.put("ellipsis", new Symbol("\u2026"));
		// PER MILLE SIGN ‰
		Po.put("perthousand", new Symbol("\u2030"));
		// PER TEN THOUSAND SIGN ‱
		Po.put("permyriad", new Symbol("\u2031"));
		// PRIME ′
		Po.put("minute", new Symbol("\u2032"));
		// DOUBLE PRIME ″
		Po.put("uni2033", new Symbol("\u2033"));
		// TRIPLE PRIME ‴
		Po.put("uni2034", new Symbol("\u2034"));
		// REVERSED PRIME ‵
		Po.put("primereversed", new Symbol("\u2035"));
		// REVERSED DOUBLE PRIME ‶
		Po.put("uni2036", new Symbol("\u2036"));
		// REVERSED TRIPLE PRIME ‷
		Po.put("uni2037", new Symbol("\u2037"));
		// REFERENCE MARK ※
		Po.put("referencemark", new Symbol("\u203B"));
		// INTERROBANG ‽
		Po.put("uni203D", new Symbol("\u203D"));
		// QUADRUPLE PRIME ⁗
		Po.put("uni2057", new Symbol("\u2057"));
		// INVERTED INTERROBANG ⸘
		Po.put("uni2E18", new Symbol("\u2E18"));

		all.putAll(Po);


	}
}