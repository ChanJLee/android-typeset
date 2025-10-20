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
		// Greek letters (lowercase)
		GLYPHS.put("alpha", "α");
		GLYPHS.put("beta", "β");
		GLYPHS.put("gamma", "γ");
		GLYPHS.put("delta", "δ");
		GLYPHS.put("epsilon", "ϵ");
		GLYPHS.put("varepsilon", "ε");
		GLYPHS.put("zeta", "ζ");
		GLYPHS.put("eta", "η");
		GLYPHS.put("theta", "θ");
		GLYPHS.put("vartheta", "ϑ");
		GLYPHS.put("iota", "ι");
		GLYPHS.put("kappa", "κ");
		GLYPHS.put("lambda", "λ");
		GLYPHS.put("mu", "μ");
		GLYPHS.put("nu", "ν");
		GLYPHS.put("xi", "ξ");
		GLYPHS.put("pi", "π");
		GLYPHS.put("varpi", "ϖ");
		GLYPHS.put("rho", "ρ");
		GLYPHS.put("varrho", "ϱ");
		GLYPHS.put("sigma", "σ");
		GLYPHS.put("varsigma", "ς");
		GLYPHS.put("tau", "τ");
		GLYPHS.put("upsilon", "υ");
		GLYPHS.put("phi", "ϕ");
		GLYPHS.put("varphi", "φ");
		GLYPHS.put("chi", "χ");
		GLYPHS.put("psi", "ψ");
		GLYPHS.put("omega", "ω");

		// Greek letters (uppercase)
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

		// Binary operators
		GLYPHS.put("times", "×");
		GLYPHS.put("div", "÷");
		GLYPHS.put("cdot", "⋅");
		GLYPHS.put("pm", "±");
		GLYPHS.put("mp", "∓");
		GLYPHS.put("ast", "∗");
		GLYPHS.put("star", "⋆");
		GLYPHS.put("circ", "∘");
		GLYPHS.put("bullet", "∙");
		GLYPHS.put("cap", "∩");
		GLYPHS.put("cup", "∪");
		GLYPHS.put("vee", "∨");
		GLYPHS.put("wedge", "∧");
		GLYPHS.put("oplus", "⊕");
		GLYPHS.put("otimes", "⊗");
		GLYPHS.put("odot", "⊙");

		// Relation operators
		GLYPHS.put("leq", "≤");
		GLYPHS.put("geq", "≥");
		GLYPHS.put("neq", "≠");
		GLYPHS.put("approx", "≈");
		GLYPHS.put("equiv", "≡");
		GLYPHS.put("cong", "≅");
		GLYPHS.put("sim", "∼");
		GLYPHS.put("propto", "∝");
		GLYPHS.put("in", "∈");
		GLYPHS.put("notin", "∉");
		GLYPHS.put("ni", "∋");
		GLYPHS.put("subset", "⊂");
		GLYPHS.put("supset", "⊃");
		GLYPHS.put("subseteq", "⊆");
		GLYPHS.put("supseteq", "⊇");

		// Arrows
		GLYPHS.put("to", "→");
		GLYPHS.put("rightarrow", "→");
		GLYPHS.put("leftarrow", "←");
		GLYPHS.put("leftrightarrow", "↔");
		GLYPHS.put("Rightarrow", "⇒");
		GLYPHS.put("Leftarrow", "⇐");
		GLYPHS.put("Leftrightarrow", "⇔");
		GLYPHS.put("mapsto", "↦");
		GLYPHS.put("uparrow", "↑");
		GLYPHS.put("downarrow", "↓");
		GLYPHS.put("updownarrow", "↕");

		// Delimiters and misc
		GLYPHS.put("forall", "∀");
		GLYPHS.put("exists", "∃");
		GLYPHS.put("partial", "∂");
		GLYPHS.put("nabla", "∇");
		GLYPHS.put("infty", "∞");
		GLYPHS.put("angle", "∠");
		GLYPHS.put("ldots", "…");
		GLYPHS.put("cdots", "⋯");
		GLYPHS.put("vdots", "⋮");
		GLYPHS.put("ddots", "⋱");
		GLYPHS.put("prime", "′");
		GLYPHS.put("hbar", "ħ");
		GLYPHS.put("ell", "ℓ");
		GLYPHS.put("wp", "℘");
		GLYPHS.put("Re", "ℜ");
		GLYPHS.put("Im", "ℑ");
		GLYPHS.put("surd", "√");

		// Brackets
		GLYPHS.put("langle", "⟨");
		GLYPHS.put("rangle", "⟩");
		GLYPHS.put("lfloor", "⌊");
		GLYPHS.put("rfloor", "⌋");
		GLYPHS.put("lceil", "⌈");
		GLYPHS.put("rceil", "⌉");
	}

	@Nullable
	public static String getGlyph(String name) {
		return GLYPHS.get(name);
	}

	public static String toList() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : GLYPHS.entrySet()) {
			sb.append(entry.getKey()).append("=").append(entry.getValue()).append(" ");
		}
		return sb.toString();
	}
}
