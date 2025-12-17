package me.chan.texas.ext.markdown.math.renderer.fonts;

import java.util.HashMap;
import java.util.Map;

import me.chan.texas.ext.markdown.math.ast.ExtensibleArrowAtom;
import me.chan.texas.ext.markdown.math.ast.OperatorSymbolAtom;
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
		// 基本运算符
		AST.put("+", "plus");
		AST.put("-", "minus");
		AST.put("*", "multiply");
		AST.put("/", "divide");
		AST.put(",", "comma");                 // , (逗号) 新增

		// LaTeX 运算符
		AST.put("times", "multiply");
		AST.put("cdot", "uni22C5");
		AST.put("div", "divide");
		AST.put("pm", "plusminus");
		AST.put("mp", "minusplus");

		// 关系运算符
		AST.put("=", "equal");                 // = (等号) 新增
		AST.put("neq", "notequal");
		AST.put("equiv", "equivalence");
		AST.put("approx", "approxequal");
		AST.put("cong", "uni2245");            // ≅
		AST.put("sim", "similar");
		AST.put("<", "less");                  // < (小于) 新增
		AST.put(">", "greater");               // > (大于) 新增
		AST.put("le", "lessequal");
		AST.put("ge", "greaterequal");
		AST.put("leq", "lessequal");
		AST.put("geq", "greaterequal");
		AST.put("ll", "lessmuch");
		AST.put("gg", "greatermuch");

		// 集合运算符
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

		// 箭头运算符
		AST.put("to", "arrowright");
		AST.put("rightarrow", "arrowright");
		AST.put("leftarrow", "arrowleft");
		AST.put("leftrightarrow", "arrowboth");
		AST.put("Rightarrow", "arrowdblright");
		AST.put("Leftarrow", "arrowdblleft");
		AST.put("Leftrightarrow", "arrowdblboth");
		AST.put("implies", "arrowdblright");   // ⇒ (与 Rightarrow 相同)
		AST.put("iff", "arrowdblboth");        // ⇔ (与 Leftrightarrow 相同)

		// 几何关系运算符
		AST.put("perp", "uni27C2");            // ⟂ (垂直)
		AST.put("parallel", "parallel");       // ∥ (平行)

		// 集合差、函数复合
		AST.put("setminus", "uni2216");        // ∖ (集合差)
		AST.put("circ", "uni2218");            // ∘ (函数复合)

		// 圈运算符
		AST.put("oplus", "circleplus");        // ⊕ (直和)
		AST.put("ominus", "uni2296");          // ⊖ (减号圈)
		AST.put("otimes", "circlemultiply");   // ⊗ (张量积)
		AST.put("oslash", "circledivide");     // ⊘ (斜线圈)
		AST.put("odot", "circledot");          // ⊙ (点积)

		// 其他二元运算符
		AST.put("bullet", "bullet");           // • (实心圆点)
		AST.put("star", "uni22C6");            // ⋆ (星号)
		AST.put("dagger", "dagger");           // † (剑号)
		AST.put("ddagger", "daggerdbl");       // ‡ (双剑号)

		// 更多关系运算符
		AST.put("simeq", "similar_equal");     // ≃ (相似等于)
		AST.put("asymp", "uni224D");           // ≍ (渐近相等)
		AST.put("propto", "proportional");     // ∝ (正比于)

		// 序关系
		AST.put("prec", "uni227A");            // ≺ (前驱)
		AST.put("succ", "uni227B");            // ≻ (后继)
		AST.put("preceq", "uni227C");          // ≼ (前驱等于)
		AST.put("succeq", "uni227D");          // ≽ (后继等于)

		// 整除/不整除
		AST.put("nmid", "uni2224");            // ∤ (不整除)

		// 三角运算符
		AST.put("triangleleft", "uni25C1");    // ◁ (左三角)
		AST.put("triangleright", "uni25B7");   // ▷ (右三角)

		// 其他关系运算符
		AST.put("bowtie", "uni22C8");          // ⋈ (自然连接)
		AST.put("models", "uni22A8");          // ⊨ (满足)

		// 其他常用符号
		AST.put("infty", "infinity");
		AST.put("partial", "partialdiff");     // ∂ (偏导数) - 已存在
		AST.put("nabla", "nabla");             // ∇ (梯度算子) - 已存在
		AST.put("emptyset", "emptyset");       // ∅ (空集) - 已存在
		AST.put("varnothing", "emptyset");     // ∅ (空集变体) - 新增
		AST.put("forall", "universal");        // ∀ (全称量词) - 已存在
		AST.put("exists", "existential");      // ∃ (存在量词) - 已存在
		AST.put("nexists", "uni2204");         // ∄ (不存在) - 新增
		AST.put("neg", "logicalnot");
		AST.put("sqrt", "radical");

		// 物理和数学特殊符号 - 新增
		AST.put("hbar", "uni210F");            // ℏ (约化普朗克常数)
		AST.put("ell", "uni2113");             // ℓ (脚本小写L)
		AST.put("wp", "weierstrass");          // ℘ (魏尔斯特拉斯函数)
		AST.put("Re", "Rfraktur");             // ℜ (实部)
		AST.put("Im", "Ifraktur");             // ℑ (虚部)
		AST.put("aleph", "aleph");             // ℵ (阿列夫数)

		// 定界符 (Delimiters)
		AST.put("langle", "angleleft");
		AST.put("rangle", "angleright");
		AST.put("lfloor", "uni230A");
		AST.put("rfloor", "uni230B");
		AST.put("lceil", "uni2308");
		AST.put("rceil", "uni2309");

		// 特殊符号 (Special Symbols)
		AST.put("dots", "ellipsis");           // … (水平省略号)
		AST.put("ldots", "ellipsis");          // … (同 dots)
		AST.put("cdots", "uni22EF");           // ⋯ (居中点)
		AST.put("vdots", "uni22EE");           // ⋮ (垂直点)
		AST.put("ddots", "uni22F1");           // ⋱ (对角点)
		AST.put("therefore", "therefore");     // ∴ (因此)
		AST.put("because", "because");         // ∵ (因为)
		AST.put("angle", "uni2220");           // ∠ (角)

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

		// 特殊符号（定义在 GREEK_LETTERS 中但不是真正的希腊字母）
		TEXT_OPERATORS.put("infty", "\u221E");      // ∞ (无穷大)

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

		// 在 static 块中添加
		AST.put("|", "divides");           // | (竖线/条件/整除)
		AST.put("mid", "divides");         // \mid (与 | 相同)
	}

	public static Symbol symbol(String ref) {
		return SYMBOL.all.get(ref);
	}

	public static Symbol ast(SpecialLetterVariableAtom atom) {
		String ref = AST.get(atom.name);
		if (ref == null) {
			return null;
		}
		return symbol(ref);
	}

	public static Symbol ast(LargeOperatorAtom atom) {
		String ref = AST.get(atom.name);
		if (ref == null) {
			return null;
		}
		return symbol(ref);
	}

	public static String textOp(String name) {
		return TEXT_OPERATORS.get(name);
	}

	public static String ast(GreekLetterVariableAtom atom) {
		return textOp(atom.name);
	}

	public static Symbol ast(SpecialSymbolAtom atom) {
		String ref = AST.get(atom.symbol);
		if (ref == null) {
			throw new IllegalArgumentException("Unknown ast op: " + atom.symbol);
		}
		return symbol(ref);
	}

	public static Symbol ast(SymbolAtom atom) {
		String op = atom.symbol;
		if (op.startsWith("\\")) {
			op = atom.symbol.substring(1);
		}
		String ref = AST.get(op);
		if (ref == null) {
			return null;
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
			default:
				// 如果没有找到对应的符号，使用 rightarrow 作为默认
				Symbol symbol = MathFontOptions.symbol("arrowright");
				if (symbol == null) {
					// 如果连默认箭头都找不到，抛出异常
					throw new IllegalArgumentException("Unknown extensible arrow command: " + command);
				}
				return symbol;
		}
	}

	public static Symbol ast(OperatorSymbolAtom atom) {
		String op = atom.op;
		if (op.startsWith("\\")) {
			op = atom.op.substring(1);
		}
		String ref = AST.get(op);
		if (ref == null) {
			return null;
		}

		return symbol(ref);
	}
}