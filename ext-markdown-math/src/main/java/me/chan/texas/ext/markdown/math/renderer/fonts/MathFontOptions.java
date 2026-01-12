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
		AST.put("rceil", "⌉");
		AST.put("supseteq", "⊇");
		AST.put("wedge", "∧");
		AST.put("rightarrow", "→");
		AST.put("bowtie", "⋈");
		AST.put("cong", "≅");
		AST.put("bigcup", "⋃");
		AST.put("oint", "∮");
		AST.put("prec", "≺");
		AST.put("sqrt", "√");
		AST.put("leq", "≤");
		AST.put("bigotimes", "⨂");
		AST.put("triangleright", "▷");
		AST.put("because", "∵");
		AST.put("ddots", "⋱");
		AST.put("models", "⊨");
		AST.put("Leftarrow", "⇐");
		AST.put("in", "∈");
		AST.put("cdot", "⋅");
		AST.put("nmid", "∤");
		AST.put("langle", "〈");
		AST.put("iiint", "∭");
		AST.put("circ", "∘");
		AST.put("lfloor", "⌊");
		AST.put("bigoplus", "⨁");
		AST.put("partial", "∂");
		AST.put("cup", "∪");
		AST.put("varnothing", "∅");
		AST.put("Im", "ℑ");
		AST.put("simeq", "≃");
		AST.put("odot", "⊙");
		AST.put("mid", "∣");
		AST.put("sum", "∑");
		AST.put("subseteq", "⊆");
		AST.put("lceil", "⌈");
		AST.put("geq", "≥");
		AST.put("Re", "ℜ");
		AST.put("bigodot", "⨀");
		AST.put("preceq", "⪯");
		AST.put("nexists", "∄");
		AST.put("angle", "∠");
		AST.put("prod", "∏");
		AST.put("star", "⋆");
		AST.put("dots", "…");
		AST.put("forall", "∀");
		AST.put("emptyset", "∅");
		AST.put("vdots", "⋮");
		AST.put("setminus", "∖");
		AST.put("succ", "≻");
		AST.put("le", "≤");
		AST.put("exists", "∃");
		AST.put("notin", "∉");
		AST.put("to", "→");
		AST.put("implies", "⇒");
		AST.put("ll", "≪");
		AST.put("rfloor", "⌋");
		AST.put("coprod", "∐");
		AST.put("Leftrightarrow", "⇔");
		AST.put("biguplus", "⨄");
		AST.put("propto", "∝");
		AST.put("leftarrow", "←");
		AST.put("cdots", "⋯");
		AST.put("div", "÷");
		AST.put("neg", "¬");
		AST.put("bigwedge", "⋀");
		AST.put("vee", "∨");
		AST.put("ldots", "…");
		AST.put("oiint", "∯");
		AST.put("neq", "≠");
		AST.put("hbar", "ℏ");
		AST.put("aleph", "ℵ");
		AST.put("oslash", "⊘");
		AST.put("dagger", "†");
		AST.put("oiiint", "∰");
		AST.put("mp", "∓");
		AST.put("ell", "ℓ");
		AST.put("bigvee", "⋁");
		AST.put("leftrightarrow", "↔");
		AST.put("succeq", "⪰");
		AST.put("rangle", "〉");
		AST.put("supset", "⊃");
		AST.put("therefore", "∴");
		AST.put("Rightarrow", "⇒");
		AST.put("perp", "⟂");
		AST.put("otimes", "⊗");
		AST.put("bigsqcup", "⨆");
		AST.put("times", "×");
		AST.put("cap", "∩");
		AST.put("parallel", "∥");
		AST.put("sim", "∼");
		AST.put("iint", "∬");
		AST.put("oplus", "⊕");
		AST.put("wp", "℘");
		AST.put("triangleleft", "◁");
		AST.put("bullet", "•");
		AST.put("infty", "∞");
		AST.put("ge", "≥");
		AST.put("bigcap", "⋂");
		AST.put("gg", "≫");
		AST.put("asymp", "≍");
		AST.put("iff", "⇔");
		AST.put("int", "∫");
		AST.put("equiv", "≡");
		AST.put("ddagger", "‡");
		AST.put("ominus", "⊖");
		AST.put("pm", "±");
		AST.put("subset", "⊂");
		AST.put("approx", "≈");
		AST.put("nabla", "∇");

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