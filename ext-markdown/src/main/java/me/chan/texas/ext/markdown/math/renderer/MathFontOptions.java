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
		// Greek letters (lowercase) from JSON
		GLYPHS.put("alpha", "α");
		GLYPHS.put("beta", "β");
		GLYPHS.put("gamma", "γ");
		GLYPHS.put("delta", "δ");
		GLYPHS.put("epsilon", "ε"); // Note: JSON has a different epsilon, this keeps the existing one
		GLYPHS.put("zeta", "ζ");
		GLYPHS.put("eta", "η");
		GLYPHS.put("theta", "θ");
		GLYPHS.put("iota", "ι");
		GLYPHS.put("kappa", "κ");
		GLYPHS.put("lambda", "λ");
		GLYPHS.put("mu", "μ");
		GLYPHS.put("nu", "ν");
		GLYPHS.put("xi", "ξ");
		GLYPHS.put("pi", "π");
		GLYPHS.put("rho", "ρ");
		GLYPHS.put("sigma", "σ");
		GLYPHS.put("tau", "τ");
		GLYPHS.put("upsilon", "υ");
		GLYPHS.put("phi", "φ"); // Note: JSON has a different phi, this keeps the existing one
		GLYPHS.put("chi", "χ");
		GLYPHS.put("psi", "ψ");
		GLYPHS.put("omega", "ω");
		// Symbols from existing code not in this part of JSON
		GLYPHS.put("varepsilon", "ε");
		GLYPHS.put("vartheta", "ϑ");
		GLYPHS.put("varpi", "ϖ");
		GLYPHS.put("varrho", "ϱ");
		GLYPHS.put("varsigma", "ς");
		GLYPHS.put("varphi", "φ");


		// Greek letters (uppercase) from JSON
		GLYPHS.put("Gamma", "Γ");
		GLYPHS.put("Delta", "Δ");
		GLYPHS.put("Theta", "Θ");
		GLYPHS.put("Lambda", "Λ");
		GLYPHS.put("Xi", "Ξ");
		GLYPHS.put("Pi", "Π");
		GLYPHS.put("Sigma", "Σ");
		GLYPHS.put("Upsilon", "Υ");
		GLYPHS.put("Phi", "Φ");
		GLYPHS.put("Psi", "Ψ");
		GLYPHS.put("Omega", "Ω");

		// Basic operators from JSON
		GLYPHS.put("plus", "+");
		GLYPHS.put("minus", "-");
		GLYPHS.put("times", "×");
		GLYPHS.put("divide", "÷");
		GLYPHS.put("equals", "=");
		GLYPHS.put("plusminus", "±");
		GLYPHS.put("minusplus", "∓");
		GLYPHS.put("cdot", "⋅");
		GLYPHS.put("ast", "∗");
		GLYPHS.put("star", "⋆");
		// Symbols from existing code not in this part of JSON
		GLYPHS.put("div", "÷");
		GLYPHS.put("pm", "±");
		GLYPHS.put("mp", "∓");
		GLYPHS.put("circ", "∘");
		GLYPHS.put("bullet", "∙");

		// Relations from JSON
		GLYPHS.put("lt", "<");
		GLYPHS.put("gt", ">");
		GLYPHS.put("le", "≤");
		GLYPHS.put("ge", "≥");
		GLYPHS.put("leq", "≤");
		GLYPHS.put("geq", "≥");
		GLYPHS.put("equiv", "≡");
		GLYPHS.put("approx", "≈");
		GLYPHS.put("neq", "≠");
		GLYPHS.put("sim", "∼");
		GLYPHS.put("cong", "≅");
		GLYPHS.put("propto", "∝");

		// Set Operators from JSON
		GLYPHS.put("in", "∈");
		GLYPHS.put("notin", "∉");
		GLYPHS.put("subset", "⊂");
		GLYPHS.put("supset", "⊃");
		GLYPHS.put("subseteq", "⊆");
		GLYPHS.put("supseteq", "⊇");
		GLYPHS.put("cup", "∪");
		GLYPHS.put("cap", "∩");
		GLYPHS.put("emptyset", "∅");
		GLYPHS.put("exists", "∃");
		GLYPHS.put("forall", "∀");
		// Symbols from existing code not in this part of JSON
		GLYPHS.put("ni", "∋");

		// Arrows from JSON
		GLYPHS.put("leftarrow", "←");
		GLYPHS.put("rightarrow", "→");
		GLYPHS.put("uparrow", "↑");
		GLYPHS.put("downarrow", "↓");
		GLYPHS.put("leftrightarrow", "↔");
		GLYPHS.put("Leftarrow", "⇐");
		GLYPHS.put("Rightarrow", "⇒");
		GLYPHS.put("Leftrightarrow", "⇔");
		GLYPHS.put("mapsto", "↦");
		// Symbols from existing code not in this part of JSON
		GLYPHS.put("to", "→");
		GLYPHS.put("updownarrow", "↕");

		// Large Operators from JSON
		GLYPHS.put("sum", "∑");
		GLYPHS.put("prod", "∏");
		GLYPHS.put("coprod", "∐");
		GLYPHS.put("int", "∫");
		GLYPHS.put("iint", "∬");
		GLYPHS.put("iiint", "∭");
		GLYPHS.put("oint", "∮");
		GLYPHS.put("bigcup", "⋃");
		GLYPHS.put("bigcap", "⋂");
		GLYPHS.put("bigvee", "⋁");
		GLYPHS.put("bigwedge", "⋀");

		// Delimiters from JSON
		GLYPHS.put("lparen", "(");
		GLYPHS.put("rparen", ")");
		GLYPHS.put("lbracket", "[");
		GLYPHS.put("rbracket", "]");
		GLYPHS.put("lbrace", "{");
		GLYPHS.put("rbrace", "}");
		GLYPHS.put("langle", "⟨");
		GLYPHS.put("rangle", "⟩");
		GLYPHS.put("lfloor", "⌊");
		GLYPHS.put("rfloor", "⌋");
		GLYPHS.put("lceil", "⌈");
		GLYPHS.put("rceil", "⌉");
		GLYPHS.put("vert", "|");
		GLYPHS.put("Vert", "‖");

		// Misc Symbols from JSON
		GLYPHS.put("infty", "∞");
		GLYPHS.put("partial", "∂");
		GLYPHS.put("nabla", "∇");
		GLYPHS.put("angle", "∠");
		GLYPHS.put("parallel", "∥");
		GLYPHS.put("perp", "⊥");
		GLYPHS.put("prime", "′");
		GLYPHS.put("hbar", "ℏ");
		GLYPHS.put("ell", "ℓ");
		GLYPHS.put("radical", "√");
		// Symbols from existing code not in this part of JSON
		GLYPHS.put("ldots", "…");
		GLYPHS.put("cdots", "⋯");
		GLYPHS.put("vdots", "⋮");
		GLYPHS.put("ddots", "⋱");
		GLYPHS.put("wp", "℘");
		GLYPHS.put("Re", "ℜ");
		GLYPHS.put("Im", "ℑ");
		GLYPHS.put("surd", "√");

		// Logic Symbols from JSON
		GLYPHS.put("neg", "¬");
		GLYPHS.put("wedge", "∧");
		GLYPHS.put("vee", "∨");
		GLYPHS.put("implies", "⇒");
		GLYPHS.put("iff", "⇔");

		// Set operators from existing code that were under "Binary operators"
		GLYPHS.put("oplus", "⊕");
		GLYPHS.put("otimes", "⊗");
		GLYPHS.put("odot", "⊙");
	}

	@Nullable
	public static String formatSymbol(String ref) {
		if (ref.startsWith("\\")) {
			return GLYPHS.get(ref);
		}
		return ref;
	}

	public static Map<String, String> toMap() {
		return GLYPHS;
	}
}
