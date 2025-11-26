package me.chan.texas.ext.markdown.math.renderer.fonts;

import java.util.HashMap;
import java.util.Map;

import me.chan.texas.ext.markdown.math.ast.SpecialSymbolAtom;
import me.chan.texas.ext.markdown.math.ast.UnaryOp;
import me.chan.texas.ext.markdown.math.ast.GreekLetterAtom;
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
		AST.put("+", "plus");
		AST.put("-", "minus");
		AST.put("*", "multiply");
		AST.put("/", "divide");
		AST.put("times", "multiply");
		AST.put("cdot", "uni22C5");
		AST.put("div", "divide");
		AST.put("pm", "plusminus");
		AST.put("mp", "minusplus");
		AST.put("neq", "notequal");
		AST.put("equiv", "equivalence");
		AST.put("approx", "approxequal");
		AST.put("cong", "uni2245");           // ≅ 新增
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
		AST.put("implies", "arrowdblright");   // ⇒ 新增（与 Rightarrow 相同）
		AST.put("iff", "arrowdblboth");        // ⇔ 新增（与 Leftrightarrow 相同）

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

		// 特殊符号 (Special Symbols) - 新增
		AST.put("dots", "ellipsis");           // … (水平省略号)
		AST.put("ldots", "ellipsis");          // … (同 dots)
		AST.put("cdots", "uni22EF");           // ⋯ (居中点)
		AST.put("vdots", "uni22EE");           // ⋮ (垂直点)
		AST.put("ddots", "uni22F1");           // ⋱ (对角点)
		AST.put("therefore", "therefore");     // ∴ (因此)
		AST.put("because", "because");         // ∵ (因为)
		AST.put("angle", "uni2220");           // ∠ (角)
		AST.put("perp", "uni27C2");            // ⟂ (垂直)
		AST.put("parallel", "parallel");       // ∥ (平行)

		// 希腊字母 - 小写 (Greek Letters - Lowercase)
		TEXT_OPERATORS.put("alpha", "\u03B1");      // α
		TEXT_OPERATORS.put("beta", "\u03B2");       // β
		TEXT_OPERATORS.put("gamma", "\u03B3");      // γ
		TEXT_OPERATORS.put("delta", "\u03B4");      // δ
		TEXT_OPERATORS.put("epsilon", "\u03B5");    // ε
		TEXT_OPERATORS.put("varepsilon", "\u03F5"); // ϵ
		TEXT_OPERATORS.put("zeta", "\u03B6");       // ζ
		TEXT_OPERATORS.put("eta", "\u03B7");        // η
		TEXT_OPERATORS.put("theta", "\u03B8");      // θ
		TEXT_OPERATORS.put("vartheta", "\u03D1");   // ϑ
		TEXT_OPERATORS.put("iota", "\u03B9");       // ι
		TEXT_OPERATORS.put("kappa", "\u03BA");      // κ
		TEXT_OPERATORS.put("lambda", "\u03BB");     // λ
		TEXT_OPERATORS.put("mu", "\u03BC");         // μ
		TEXT_OPERATORS.put("nu", "\u03BD");         // ν
		TEXT_OPERATORS.put("xi", "\u03BE");         // ξ
		TEXT_OPERATORS.put("pi", "\u03C0");         // π
		TEXT_OPERATORS.put("varpi", "\u03D6");      // ϖ
		TEXT_OPERATORS.put("rho", "\u03C1");        // ρ
		TEXT_OPERATORS.put("varrho", "\u03F1");     // ϱ
		TEXT_OPERATORS.put("sigma", "\u03C3");      // σ
		TEXT_OPERATORS.put("varsigma", "\u03C2");   // ς
		TEXT_OPERATORS.put("tau", "\u03C4");        // τ
		TEXT_OPERATORS.put("upsilon", "\u03C5");    // υ
		TEXT_OPERATORS.put("phi", "\u03C6");        // φ
		TEXT_OPERATORS.put("varphi", "\u03D5");     // ϕ
		TEXT_OPERATORS.put("chi", "\u03C7");        // χ
		TEXT_OPERATORS.put("psi", "\u03C8");        // ψ
		TEXT_OPERATORS.put("omega", "\u03C9");      // ω

		// 希腊字母 - 大写 (Greek Letters - Uppercase)
		TEXT_OPERATORS.put("Gamma", "\u0393");      // Γ
		TEXT_OPERATORS.put("Delta", "\u0394");      // Δ
		TEXT_OPERATORS.put("Theta", "\u0398");      // Θ
		TEXT_OPERATORS.put("Lambda", "\u039B");     // Λ
		TEXT_OPERATORS.put("Xi", "\u039E");         // Ξ
		TEXT_OPERATORS.put("Pi", "\u03A0");         // Π
		TEXT_OPERATORS.put("Sigma", "\u03A3");      // Σ
		TEXT_OPERATORS.put("Upsilon", "\u03A5");    // Υ
		TEXT_OPERATORS.put("Phi", "\u03A6");        // Φ
		TEXT_OPERATORS.put("Psi", "\u03A8");        // Ψ
		TEXT_OPERATORS.put("Omega", "\u03A9");      // Ω

		// 文本运算符（以罗马字体渲染）
		TEXT_OPERATORS.put("lim", "lim");
		TEXT_OPERATORS.put("limsup", "lim sup");
		TEXT_OPERATORS.put("liminf", "lim inf");
		TEXT_OPERATORS.put("sin", "sin");
		TEXT_OPERATORS.put("cos", "cos");
		TEXT_OPERATORS.put("tan", "tan");
		TEXT_OPERATORS.put("cot", "cot");
		TEXT_OPERATORS.put("sec", "sec");
		TEXT_OPERATORS.put("csc", "csc");
		TEXT_OPERATORS.put("log", "log");
		TEXT_OPERATORS.put("ln", "ln");
		TEXT_OPERATORS.put("lg", "lg");
		TEXT_OPERATORS.put("exp", "exp");
		TEXT_OPERATORS.put("max", "max");
		TEXT_OPERATORS.put("min", "min");
		TEXT_OPERATORS.put("sup", "sup");
		TEXT_OPERATORS.put("inf", "inf");
		TEXT_OPERATORS.put("det", "det");
		TEXT_OPERATORS.put("dim", "dim");
		TEXT_OPERATORS.put("gcd", "gcd");
		TEXT_OPERATORS.put("ker", "ker");
	}

	public static Symbol symbol(String ref) {
		return SYMBOL.all.get(ref);
	}

	public static Symbol ast(UnaryOp atom) {
		String ref = AST.get(atom.op);
		if (ref == null) {
			throw new IllegalArgumentException("Unknown ast op: " + atom.op);
		}
		return symbol(ref);
	}

	public static Symbol ast(LargeOperatorAtom atom) {
		String ref = AST.get(atom.op);
		if (ref == null) {
			return null;
		}
		return symbol(ref);
	}

	public static String textOp(String name) {
		return TEXT_OPERATORS.get(name);
	}

	public static String ast(GreekLetterAtom atom) {
		return textOp(atom.symbol);
	}

	public static Symbol ast(SpecialSymbolAtom atom) {
		String ref = AST.get(atom.symbol);
		if (ref == null) {
			throw new IllegalArgumentException("Unknown ast op: " + atom.symbol);
		}
		return symbol(ref);
	}
}
