package me.chan.texas.ext.markdown.math.renderer.fonts;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import me.chan.texas.ext.markdown.math.ast.LargeOperatorAtom;

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
	public static final float ACCENT_BASE_HEIGHT = 450;
	public static final float SCRIPT_PERCENT_SCALE_DOWN = 70;
	public static final float SCRIPT_SCRIPT_PERCENT_SCALE_DOWN = 50;

	public static final float BRACT_BASELINE_OFFSET_FACTOR = 1.235f;

	private static final SymbolOptions SYMBOL = new SymbolOptions();
	private static final Map<String, String> AST = new HashMap<>();
	private static final Map<String, String> TEXT_OPERATORS = new HashMap<>();

	static {
		// 大型运算符 (Large Operators)
		AST.put("sum", "summation");
		AST.put("prod", "product");
		AST.put("coprod", "uni2210");
		AST.put("int", "integral");
		AST.put("iint", "uni222C");
		AST.put("iiint", "uni222D");
		AST.put("oint", "contourintegral");
		AST.put("oiint", "uni222F");
		AST.put("oiiint", "uni2230");
		AST.put("bigcup", "uni22C3");
		AST.put("bigcap", "uni22C2");
		AST.put("bigvee", "uni22C1");
		AST.put("bigwedge", "uni22C0");
		AST.put("bigoplus", "uni2A01");
		AST.put("bigotimes", "uni2A02");
		AST.put("bigodot", "uni2A00");
		AST.put("biguplus", "uni2A04");
		AST.put("bigsqcup", "uni2A06");
		
		// 二元运算符 (Binary Operators)
		AST.put("times", "multiply");
		AST.put("cdot", "uni22C5");
		AST.put("div", "divide");
		AST.put("pm", "plusminus");
		AST.put("mp", "minusplus");
		AST.put("neq", "notequal");
		AST.put("equiv", "equivalence");
		AST.put("approx", "approxequal");
		AST.put("sim", "similar");
		AST.put("le", "lessequal");
		AST.put("ge", "greaterequal");
		AST.put("leq", "lessequal");
		AST.put("geq", "greaterequal");
		AST.put("ll", "lessmuch");
		AST.put("gg", "greatermuch");
		AST.put("in", "element");
		AST.put("notin", "uni2209");
		AST.put("subset", "propersubset");
		AST.put("supset", "propersuperset");
		AST.put("subseteq", "reflexsubset");
		AST.put("supseteq", "reflexsuperset");
		AST.put("cup", "union");
		AST.put("cap", "intersection");
		AST.put("wedge", "logicaland");
		AST.put("vee", "logicalor");
		AST.put("to", "arrowright");
		AST.put("rightarrow", "arrowright");
		AST.put("leftarrow", "arrowleft");
		AST.put("leftrightarrow", "arrowboth");
		AST.put("Rightarrow", "arrowdblright");
		AST.put("Leftarrow", "arrowdblleft");
		AST.put("Leftrightarrow", "arrowdblboth");
		
		// 其他常用符号
		AST.put("infty", "infinity");
		AST.put("partial", "partialdiff");
		AST.put("nabla", "nabla");
		AST.put("emptyset", "emptyset");
		AST.put("forall", "universal");
		AST.put("exists", "existential");
		AST.put("neg", "logicalnot");
		AST.put("sqrt", "radical");
		
		// 定界符 (Delimiters)
		AST.put("langle", "angleleft");
		AST.put("rangle", "angleright");
		AST.put("lfloor", "uni230A");
		AST.put("rfloor", "uni230B");
		AST.put("lceil", "uni2308");
		AST.put("rceil", "uni2309");
		
		// 希腊字母 - 小写 (Greek Letters - Lowercase)
		// 注意：这些可能需要直接使用Unicode字符，而不是符号名称
		AST.put("alpha", "\u03B1");      // α
		AST.put("beta", "\u03B2");       // β
		AST.put("gamma", "\u03B3");      // γ
		AST.put("delta", "\u03B4");      // δ
		AST.put("epsilon", "\u03B5");    // ε
		AST.put("varepsilon", "\u03F5"); // ϵ
		AST.put("zeta", "\u03B6");       // ζ
		AST.put("eta", "\u03B7");        // η
		AST.put("theta", "\u03B8");      // θ
		AST.put("vartheta", "\u03D1");   // ϑ
		AST.put("iota", "\u03B9");       // ι
		AST.put("kappa", "\u03BA");      // κ
		AST.put("lambda", "\u03BB");     // λ
		AST.put("mu", "\u03BC");         // μ
		AST.put("nu", "\u03BD");         // ν
		AST.put("xi", "\u03BE");         // ξ
		AST.put("pi", "\u03C0");         // π
		AST.put("varpi", "\u03D6");      // ϖ
		AST.put("rho", "\u03C1");        // ρ
		AST.put("varrho", "\u03F1");     // ϱ
		AST.put("sigma", "\u03C3");      // σ
		AST.put("varsigma", "\u03C2");   // ς
		AST.put("tau", "\u03C4");        // τ
		AST.put("upsilon", "\u03C5");    // υ
		AST.put("phi", "\u03C6");        // φ
		AST.put("varphi", "\u03D5");     // ϕ
		AST.put("chi", "\u03C7");        // χ
		AST.put("psi", "\u03C8");        // ψ
		AST.put("omega", "\u03C9");      // ω
		
		// 希腊字母 - 大写 (Greek Letters - Uppercase)
		AST.put("Gamma", "\u0393");      // Γ
		AST.put("Delta", "\u0394");      // Δ
		AST.put("Theta", "\u0398");      // Θ
		AST.put("Lambda", "\u039B");     // Λ
		AST.put("Xi", "\u039E");         // Ξ
		AST.put("Pi", "\u03A0");         // Π
		AST.put("Sigma", "\u03A3");      // Σ
		AST.put("Upsilon", "\u03A5");    // Υ
		AST.put("Phi", "\u03A6");        // Φ
		AST.put("Psi", "\u03A8");        // Ψ
		AST.put("Omega", "\u03A9");      // Ω

		TEXT_OPERATORS.put("lim", "lim");
		TEXT_OPERATORS.put("limsup", "lim sup");
		TEXT_OPERATORS.put("liminf", "lim inf");
		TEXT_OPERATORS.put("sin", "sin");
		TEXT_OPERATORS.put("cos", "cos");
		TEXT_OPERATORS.put("tan", "tan");
		TEXT_OPERATORS.put("log", "log");
		TEXT_OPERATORS.put("ln", "ln");
		TEXT_OPERATORS.put("max", "max");
		TEXT_OPERATORS.put("min", "min");
	}

	public static Symbol formatByRef(String ref) {
		Symbol ret = SYMBOL.all.get(ref);
		if (ret == null) {
			throw new IllegalArgumentException("Unknown symbol: " + ref);
		}
		return ret;
	}

	public static Symbol format(LargeOperatorAtom atom) {
		throw new RuntimeException("Stub!");
	}
}
