package me.chan.texas.ext.markdown.math.renderer.fonts;

import java.util.HashMap;
import java.util.Map;

import me.chan.texas.ext.markdown.math.ast.ExtensibleArrowAtom;
import me.chan.texas.ext.markdown.math.ast.SpecialLetterVariableAtom;
import me.chan.texas.ext.markdown.math.ast.SpecialSymbolAtom;
import me.chan.texas.ext.markdown.math.ast.SymbolAtom;
import me.chan.texas.ext.markdown.math.ast.GreekLetterVariableAtom;
import me.chan.texas.ext.markdown.math.ast.LargeOperatorAtom;

public class MathFontOptions {
	public static final float UNITS_PER_EM = 1000F;
	public static final float RADICAL_VERTICAL_GAP = 50;
	public static final float RADICAL_RULE_THICKNESS = 40;
	public static final float RADICAL_EXTRA_ASCENDER = 40;
	public static final float RADICAL_KERN_BEFORE_DEGREE = 278;
	public static final float RADICAL_DEGREE_BOTTOM_RAISE_PERCENT = 60;
	public static final float FRACTION_NUMERATOR_GAP_MIN = 40;
	public static final float FRACTION_RULE_THICKNESS = 40;
	public static final float FRACTION_DENOMINATOR_GAP_MIN = 40;

	private static final SymbolOptions SYMBOL = new SymbolOptions();
	private static final Map<String, String> AST = new HashMap<>();
	private static final Map<String, String> TEXT_OPERATORS = new HashMap<>();

	static {
		// 希腊字母 - 小写
		AST.put("\\alpha", "α");
		AST.put("\\beta", "β");
		AST.put("\\gamma", "γ");
		AST.put("\\delta", "δ");
		AST.put("\\epsilon", "ε");
		AST.put("\\varepsilon", "ϵ");
		AST.put("\\zeta", "ζ");
		AST.put("\\eta", "η");
		AST.put("\\theta", "θ");
		AST.put("\\vartheta", "ϑ");
		AST.put("\\iota", "ι");
		AST.put("\\kappa", "κ");
		AST.put("\\lambda", "λ");
		AST.put("\\mu", "μ");
		AST.put("\\nu", "ν");
		AST.put("\\xi", "ξ");
		AST.put("\\pi", "π");
		AST.put("\\varpi", "ϖ");
		AST.put("\\rho", "ρ");
		AST.put("\\varrho", "ϱ");
		AST.put("\\sigma", "σ");
		AST.put("\\varsigma", "ς");
		AST.put("\\tau", "τ");
		AST.put("\\upsilon", "υ");
		AST.put("\\phi", "φ");
		AST.put("\\varphi", "ϕ");
		AST.put("\\chi", "χ");
		AST.put("\\psi", "ψ");
		AST.put("\\omega", "ω");

		// 希腊字母 - 大写
		AST.put("\\Gamma", "Γ");
		AST.put("\\Delta", "Δ");
		AST.put("\\Theta", "Θ");
		AST.put("\\Lambda", "Λ");
		AST.put("\\Xi", "Ξ");
		AST.put("\\Pi", "Π");
		AST.put("\\Sigma", "Σ");
		AST.put("\\Upsilon", "Υ");
		AST.put("\\Phi", "Φ");
		AST.put("\\Psi", "Ψ");
		AST.put("\\Omega", "Ω");

		// 无穷大
		AST.put("\\infty", "∞");

		// 运算符号
		AST.put("\\times", "×");
		AST.put("\\cdot", "⋅");
		AST.put("\\div", "÷");
		AST.put("\\pm", "±");
		AST.put("\\mp", "∓");
		AST.put("\\setminus", "∖");
		AST.put("\\circ", "∘");

		// 二元运算符
		AST.put("\\oplus", "⊕");
		AST.put("\\ominus", "⊖");
		AST.put("\\otimes", "⊗");
		AST.put("\\oslash", "⊘");
		AST.put("\\odot", "⊙");
		AST.put("\\bullet", "•");
		AST.put("\\star", "⋆");
		AST.put("\\dagger", "†");
		AST.put("\\ddagger", "‡");

		// 关系符号
		AST.put("\\neq", "≠");
		AST.put("\\equiv", "≡");
		AST.put("\\approx", "≈");
		AST.put("\\cong", "≅");
		AST.put("\\sim", "∼");
		AST.put("\\simeq", "≃");
		AST.put("\\asymp", "≍");
		AST.put("\\propto", "∝");

		// 不等号
		AST.put("\\le", "≤");
		AST.put("\\ge", "≥");
		AST.put("\\leq", "≤");
		AST.put("\\geq", "≥");
		AST.put("\\ll", "≪");
		AST.put("\\gg", "≫");
		AST.put("\\prec", "≺");
		AST.put("\\succ", "≻");
		AST.put("\\preceq", "⪯");
		AST.put("\\succeq", "⪰");

		// 集合符号
		AST.put("\\in", "∈");
		AST.put("\\notin", "∉");
		AST.put("\\subset", "⊂");
		AST.put("\\supset", "⊃");
		AST.put("\\subseteq", "⊆");
		AST.put("\\supseteq", "⊇");
		AST.put("\\cup", "∪");
		AST.put("\\cap", "∩");
		AST.put("\\wedge", "∧");
		AST.put("\\vee", "∨");

		// 箭头
		AST.put("\\to", "→");
		AST.put("\\rightarrow", "→");
		AST.put("\\leftarrow", "←");
		AST.put("\\leftrightarrow", "↔");
		AST.put("\\Rightarrow", "⇒");
		AST.put("\\Leftarrow", "⇐");
		AST.put("\\Leftrightarrow", "⇔");
		AST.put("\\implies", "⟹");
		AST.put("\\iff", "⟺");

		// 几何符号
		AST.put("\\perp", "⊥");
		AST.put("\\parallel", "∥");
		AST.put("\\mid", "∣");
		AST.put("\\nmid", "∤");
		AST.put("\\triangleleft", "◁");
		AST.put("\\triangleright", "▷");
		AST.put("\\angle", "∠");

		// 其他符号
		AST.put("\\bowtie", "⋈");
		AST.put("\\models", "⊨");
		AST.put("\\neg", "¬");

		// 省略号
		AST.put("\\dots", "…");
		AST.put("\\ldots", "…");
		AST.put("\\cdots", "⋯");
		AST.put("\\vdots", "⋮");
		AST.put("\\ddots", "⋱");

		// 逻辑符号
		AST.put("\\therefore", "∴");
		AST.put("\\because", "∵");
		AST.put("\\forall", "∀");
		AST.put("\\exists", "∃");
		AST.put("\\nexists", "∄");

		// 空集
		AST.put("\\emptyset", "∅");
		AST.put("\\varnothing", "∅");

		// 特殊字母和符号
		AST.put("\\hbar", "ℏ");
		AST.put("\\nabla", "∇");
		AST.put("\\partial", "∂");
		AST.put("\\ell", "ℓ");
		AST.put("\\wp", "℘");
		AST.put("\\Re", "ℜ");
		AST.put("\\Im", "ℑ");
		AST.put("\\aleph", "ℵ");

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

	public static Symbol symbol(String c) {
		return SYMBOL.all.get(c);
	}

	public static Symbol ast(SpecialLetterVariableAtom atom) {
		return ref(atom.name);
	}

	public static Symbol ast(LargeOperatorAtom atom) {
		return ref(atom.name);
	}

	public static String ast(GreekLetterVariableAtom atom) {
		return AST.get(atom.name);
	}

	public static Symbol ast(SpecialSymbolAtom atom) {
		return ref(atom.symbol);
	}

	public static Symbol ast(SymbolAtom atom) {
		String symbol = atom.symbol;
		if (symbol.startsWith("\\")) {
			return ref(atom.symbol);
		}

		return symbol(symbol);
	}

	private static Symbol ref(String symbol) {
		String ref = AST.get(symbol);
		if (ref == null) {
			throw new RuntimeException("Unknown symbol: " + symbol);
		}
		return symbol(ref);
	}

	public static Symbol ast(ExtensibleArrowAtom atom) {
		String command = atom.command;
		switch (command) {
			case "xrightarrow":
				return MathFontOptions.symbol("arrowright");
			case "xleftarrow":
				return MathFontOptions.symbol("arrowleft");
			case "xleftrightarrow":
				return MathFontOptions.symbol("arrowboth");
			case "xRightarrow":
				return MathFontOptions.symbol("arrowdblright");
			case "xLeftarrow":
				return MathFontOptions.symbol("arrowdblleft");
			case "xLeftrightarrow":
				return MathFontOptions.symbol("arrowdblboth");
			case "xhookrightarrow":
				return MathFontOptions.symbol("uni21AA");
			case "xhookleftarrow":
				return MathFontOptions.symbol("uni21A9");
			case "xtwoheadrightarrow":
				return MathFontOptions.symbol("uni21A0");
			case "xtwoheadleftarrow":
				return MathFontOptions.symbol("uni219E");
			case "xmapsto":
				return MathFontOptions.symbol("uni21A6");
			case "xtofrom":
				return MathFontOptions.symbol("uni21C4");
			default:
				throw new IllegalArgumentException("Unknown extensible arrow command: " + command);
		}
	}

	public static String textOp(String name) {
		return TEXT_OPERATORS.get(name);
	}
}