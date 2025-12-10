package me.chan.texas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.chan.texas.debug.R;

public class MathDemoActivity extends AppCompatActivity {
	private MathView mMathView;
	private TextView mTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_math_demo);

		RecyclerView recyclerView = findViewById(R.id.rv);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(new MathAdapter(getFormulaCategories(), this::render));

		mMathView = findViewById(R.id.math);
		mTextView = findViewById(R.id.console);
	}

	private void render(String formula) {
		mMathView.render("afg \\, " + formula + " \\, afg");
		mTextView.setText(formula);
	}

	private List<FormulaCategory> getFormulaCategories() {
		List<FormulaCategory> categories = new ArrayList<>();

		// ============================================================
		// <number> 数字
		// ============================================================
		categories.add(new FormulaCategory("<number> 数字", Arrays.asList(
				new MathFormula("整数", "123"),
				new MathFormula("小数", "3.14"),
				new MathFormula("长整数", "1234567890"),
				new MathFormula("长小数", "3.14159265")
		)));

		// ============================================================
		// <variable> 变量
		// ============================================================
		categories.add(new FormulaCategory("<variable> 变量", Arrays.asList(
				new MathFormula("单字母变量", "x"),
				new MathFormula("多字母变量", "abc"),
				new MathFormula("带撇变量 '", "f'"),
				new MathFormula("双撇变量 ''", "g''"),
				new MathFormula("三撇变量 '''", "h'''")
		)));

		// ============================================================
		// <greek_letter_variable> 希腊字母 - 小写
		// ============================================================
		categories.add(new FormulaCategory("<greek_letter_variable> 希腊字母-小写", Arrays.asList(
				new MathFormula("α (alpha)", "\\alpha"),
				new MathFormula("β (beta)", "\\beta"),
				new MathFormula("γ (gamma)", "\\gamma"),
				new MathFormula("δ (delta)", "\\delta"),
				new MathFormula("ε (epsilon)", "\\epsilon"),
				new MathFormula("ϵ (varepsilon)", "\\varepsilon"),
				new MathFormula("ζ (zeta)", "\\zeta"),
				new MathFormula("η (eta)", "\\eta"),
				new MathFormula("θ (theta)", "\\theta"),
				new MathFormula("ϑ (vartheta)", "\\vartheta"),
				new MathFormula("ι (iota)", "\\iota"),
				new MathFormula("κ (kappa)", "\\kappa"),
				new MathFormula("λ (lambda)", "\\lambda"),
				new MathFormula("μ (mu)", "\\mu"),
				new MathFormula("ν (nu)", "\\nu"),
				new MathFormula("ξ (xi)", "\\xi"),
				new MathFormula("π (pi)", "\\pi"),
				new MathFormula("ϖ (varpi)", "\\varpi"),
				new MathFormula("ρ (rho)", "\\rho"),
				new MathFormula("ϱ (varrho)", "\\varrho"),
				new MathFormula("σ (sigma)", "\\sigma"),
				new MathFormula("ς (varsigma)", "\\varsigma"),
				new MathFormula("τ (tau)", "\\tau"),
				new MathFormula("υ (upsilon)", "\\upsilon"),
				new MathFormula("φ (phi)", "\\phi"),
				new MathFormula("ϕ (varphi)", "\\varphi"),
				new MathFormula("χ (chi)", "\\chi"),
				new MathFormula("ψ (psi)", "\\psi"),
				new MathFormula("ω (omega)", "\\omega")
		)));

		// ============================================================
		// <greek_letter_variable> 希腊字母 - 大写
		// ============================================================
		categories.add(new FormulaCategory("<greek_letter_variable> 希腊字母-大写", Arrays.asList(
				new MathFormula("Γ (Gamma)", "\\Gamma"),
				new MathFormula("Δ (Delta)", "\\Delta"),
				new MathFormula("Θ (Theta)", "\\Theta"),
				new MathFormula("Λ (Lambda)", "\\Lambda"),
				new MathFormula("Ξ (Xi)", "\\Xi"),
				new MathFormula("Π (Pi)", "\\Pi"),
				new MathFormula("Σ (Sigma)", "\\Sigma"),
				new MathFormula("Υ (Upsilon)", "\\Upsilon"),
				new MathFormula("Φ (Phi)", "\\Phi"),
				new MathFormula("Ψ (Psi)", "\\Psi"),
				new MathFormula("Ω (Omega)", "\\Omega")
		)));

		// ============================================================
		// <greek_letter_variable> 希腊字母 - 其他
		// ============================================================
		categories.add(new FormulaCategory("<greek_letter_variable> 希腊字母-其他", Arrays.asList(
				new MathFormula("∞ (infty)", "\\infty"),
				new MathFormula("α' (alpha 带撇)", "\\alpha'"),
				new MathFormula("β'' (beta 双撇)", "\\beta''")
		)));

		// ============================================================
		// <special_letter_variable> 特殊字母变量
		// ============================================================
		categories.add(new FormulaCategory("<special_letter_variable> 特殊字母变量", Arrays.asList(
				new MathFormula("ℏ (hbar)", "\\hbar"),
				new MathFormula("∇ (nabla)", "\\nabla"),
				new MathFormula("∂ (partial)", "\\partial"),
				new MathFormula("ℓ (ell)", "\\ell"),
				new MathFormula("℘ (wp)", "\\wp"),
				new MathFormula("ℜ (Re)", "\\Re"),
				new MathFormula("ℑ (Im)", "\\Im"),
				new MathFormula("ℵ (aleph)", "\\aleph")
		)));

		// ============================================================
		// <special_symbol> 特殊符号 - 省略号
		// ============================================================
		categories.add(new FormulaCategory("<special_symbol> 特殊符号-省略号", Arrays.asList(
				new MathFormula("… (dots)", "\\dots"),
				new MathFormula("… (ldots)", "\\ldots"),
				new MathFormula("⋯ (cdots)", "\\cdots"),
				new MathFormula("⋮ (vdots)", "\\vdots"),
				new MathFormula("⋱ (ddots)", "\\ddots")
		)));

		// ============================================================
		// <special_symbol> 特殊符号 - 其他
		// ============================================================
		categories.add(new FormulaCategory("<special_symbol> 特殊符号-其他", Arrays.asList(
				new MathFormula("∠ (angle)", "\\angle"),
				new MathFormula("∴ (therefore)", "\\therefore"),
				new MathFormula("∵ (because)", "\\because"),
				new MathFormula("∀ (forall)", "\\forall"),
				new MathFormula("∃ (exists)", "\\exists"),
				new MathFormula("∄ (nexists)", "\\nexists"),
				new MathFormula("∅ (emptyset)", "\\emptyset"),
				new MathFormula("∅ (varnothing)", "\\varnothing")
		)));

		// ============================================================
		// <unary_op> 一元运算符
		// ============================================================
		categories.add(new FormulaCategory("<unary_op> 一元运算符", Arrays.asList(
				new MathFormula("正号 +x", "+x"),
				new MathFormula("负号 -x", "-x"),
				new MathFormula("± (pm)", "\\pm x"),
				new MathFormula("∓ (mp)", "\\mp x")
		)));

		// ============================================================
		// <postfix_op> 后缀运算符
		// ============================================================
		categories.add(new FormulaCategory("<postfix_op> 后缀运算符", Arrays.asList(
				new MathFormula("阶乘 n!", "n!"),
				new MathFormula("阶乘 5!", "5!"),
				new MathFormula("阶乘表达式", "(n-1)!")
		)));

		// ============================================================
		// <punctuation> 标点符号
		// ============================================================
		categories.add(new FormulaCategory("<punctuation> 标点符号", Arrays.asList(
				new MathFormula("逗号 ,", "a, b, c"),
				new MathFormula("分号 ;", "a; b; c")
		)));

		// ============================================================
		// <binary_op> 二元运算符 - 基础算术
		// ============================================================
		categories.add(new FormulaCategory("<binary_op> 二元运算符-基础算术", Arrays.asList(
				new MathFormula("加法 +", "a + b"),
				new MathFormula("减法 -", "a - b"),
				new MathFormula("乘法 *", "a * b"),
				new MathFormula("除法 /", "a / b"),
				new MathFormula("竖线 |", "a | b"),
				new MathFormula("× (times)", "a \\times b"),
				new MathFormula("⋅ (cdot)", "a \\cdot b"),
				new MathFormula("÷ (div)", "a \\div b"),
				new MathFormula("± (pm)", "a \\pm b"),
				new MathFormula("∓ (mp)", "a \\mp b")
		)));

		// ============================================================
		// <binary_op> 二元运算符 - 集合/圈运算
		// ============================================================
		categories.add(new FormulaCategory("<binary_op> 二元运算符-集合圈运算", Arrays.asList(
				new MathFormula("∖ (setminus)", "A \\setminus B"),
				new MathFormula("∘ (circ)", "f \\circ g"),
				new MathFormula("⊕ (oplus)", "a \\oplus b"),
				new MathFormula("⊖ (ominus)", "a \\ominus b"),
				new MathFormula("⊗ (otimes)", "a \\otimes b"),
				new MathFormula("⊘ (oslash)", "a \\oslash b"),
				new MathFormula("⊙ (odot)", "a \\odot b"),
				new MathFormula("• (bullet)", "a \\bullet b"),
				new MathFormula("⋆ (star)", "a \\star b"),
				new MathFormula("† (dagger)", "a \\dagger b"),
				new MathFormula("‡ (ddagger)", "a \\ddagger b")
		)));

		// ============================================================
		// <binary_op> 二元运算符 - 关系
		// ============================================================
		categories.add(new FormulaCategory("<binary_op> 二元运算符-关系", Arrays.asList(
				new MathFormula("等于 =", "a = b"),
				new MathFormula("≠ (neq)", "a \\neq b"),
				new MathFormula("≡ (equiv)", "a \\equiv b"),
				new MathFormula("≈ (approx)", "a \\approx b"),
				new MathFormula("≅ (cong)", "a \\cong b"),
				new MathFormula("∼ (sim)", "a \\sim b"),
				new MathFormula("≃ (simeq)", "a \\simeq b"),
				new MathFormula("≍ (asymp)", "a \\asymp b"),
				new MathFormula("∝ (propto)", "a \\propto b")
		)));

		// ============================================================
		// <binary_op> 二元运算符 - 比较
		// ============================================================
		categories.add(new FormulaCategory("<binary_op> 二元运算符-比较", Arrays.asList(
				new MathFormula("小于 <", "a < b"),
				new MathFormula("大于 >", "a > b"),
				new MathFormula("≤ (le)", "a \\le b"),
				new MathFormula("≥ (ge)", "a \\ge b"),
				new MathFormula("≤ (leq)", "a \\leq b"),
				new MathFormula("≥ (geq)", "a \\geq b"),
				new MathFormula("≪ (ll)", "a \\ll b"),
				new MathFormula("≫ (gg)", "a \\gg b"),
				new MathFormula("≺ (prec)", "a \\prec b"),
				new MathFormula("≻ (succ)", "a \\succ b"),
				new MathFormula("⪯ (preceq)", "a \\preceq b"),
				new MathFormula("⪰ (succeq)", "a \\succeq b")
		)));

		// ============================================================
		// <binary_op> 二元运算符 - 集合关系
		// ============================================================
		categories.add(new FormulaCategory("<binary_op> 二元运算符-集合关系", Arrays.asList(
				new MathFormula("∈ (in)", "x \\in A"),
				new MathFormula("∉ (notin)", "x \\notin A"),
				new MathFormula("⊂ (subset)", "A \\subset B"),
				new MathFormula("⊃ (supset)", "A \\supset B"),
				new MathFormula("⊆ (subseteq)", "A \\subseteq B"),
				new MathFormula("⊇ (supseteq)", "A \\supseteq B"),
				new MathFormula("∪ (cup)", "A \\cup B"),
				new MathFormula("∩ (cap)", "A \\cap B"),
				new MathFormula("∧ (wedge)", "p \\wedge q"),
				new MathFormula("∨ (vee)", "p \\vee q")
		)));

		// ============================================================
		// <binary_op> 二元运算符 - 箭头
		// ============================================================
		categories.add(new FormulaCategory("<binary_op> 二元运算符-箭头", Arrays.asList(
				new MathFormula("→ (to)", "a \\to b"),
				new MathFormula("→ (rightarrow)", "a \\rightarrow b"),
				new MathFormula("← (leftarrow)", "a \\leftarrow b"),
				new MathFormula("↔ (leftrightarrow)", "a \\leftrightarrow b"),
				new MathFormula("⇒ (Rightarrow)", "A \\Rightarrow B"),
				new MathFormula("⇐ (Leftarrow)", "A \\Leftarrow B"),
				new MathFormula("⇔ (Leftrightarrow)", "A \\Leftrightarrow B"),
				new MathFormula("⇒ (implies)", "A \\implies B"),
				new MathFormula("⇔ (iff)", "A \\iff B")
		)));

		// ============================================================
		// <binary_op> 二元运算符 - 几何/其他
		// ============================================================
		categories.add(new FormulaCategory("<binary_op> 二元运算符-几何其他", Arrays.asList(
				new MathFormula("⟂ (perp)", "a \\perp b"),
				new MathFormula("∥ (parallel)", "a \\parallel b"),
				new MathFormula("| (mid)", "a \\mid b"),
				new MathFormula("∤ (nmid)", "a \\nmid b"),
				new MathFormula("◁ (triangleleft)", "a \\triangleleft b"),
				new MathFormula("▷ (triangleright)", "a \\triangleright b"),
				new MathFormula("⋈ (bowtie)", "a \\bowtie b"),
				new MathFormula("⊨ (models)", "\\Gamma \\models \\phi")
		)));

		// ============================================================
		// <sup_sub_suffix> 上下标
		// ============================================================
		categories.add(new FormulaCategory("<sup_sub_suffix> 上下标", Arrays.asList(
				new MathFormula("上标 ^", "x^2"),
				new MathFormula("下标 _", "x_i"),
				new MathFormula("上下标 ^_", "x^2_1"),
				new MathFormula("下上标 _^", "x_1^2"),
				new MathFormula("复杂上标", "x^{n+1}"),
				new MathFormula("复杂下标", "a_{i,j}"),
				new MathFormula("嵌套上标", "2^{2^n}"),
				new MathFormula("度数 circ", "90^\\circ"),
				new MathFormula("正号上标", "x^{+}"),
				new MathFormula("负号上标", "x^{-}")
		)));

		// ============================================================
		// <group> 分组
		// ============================================================
		categories.add(new FormulaCategory("<group> 分组", Arrays.asList(
				new MathFormula("分组 {}", "{a+b}"),
				new MathFormula("嵌套分组", "{{a+b}+c}")
		)));

		// ============================================================
		// <frac> 分式
		// ============================================================
		categories.add(new FormulaCategory("<frac> 分式", Arrays.asList(
				new MathFormula("frac 简单", "\\frac{1}{2}"),
				new MathFormula("frac 复杂", "\\frac{x+y}{x-y}"),
				new MathFormula("frac 嵌套", "\\frac{\\frac{1}{2}}{\\frac{3}{4}}"),
				new MathFormula("dfrac (显示样式)", "\\dfrac{1}{2}"),
				new MathFormula("tfrac (文本样式)", "\\tfrac{1}{2}"),
				new MathFormula("cfrac (连分式)", "\\cfrac{1}{1+\\cfrac{1}{2}}")
		)));

		// ============================================================
		// <binom> 二项式系数
		// ============================================================
		categories.add(new FormulaCategory("<binom> 二项式系数", Arrays.asList(
				new MathFormula("binom", "\\binom{n}{k}"),
				new MathFormula("dbinom", "\\dbinom{n}{k}"),
				new MathFormula("tbinom", "\\tbinom{n}{k}")
		)));

		// ============================================================
		// <sqrt> 根式
		// ============================================================
		categories.add(new FormulaCategory("<sqrt> 根式", Arrays.asList(
				new MathFormula("sqrt 平方根", "\\sqrt{x}"),
				new MathFormula("sqrt 复杂", "\\sqrt{x^2+y^2}"),
				new MathFormula("sqrt n次根", "\\sqrt[3]{27}"),
				new MathFormula("sqrt 通用n次根", "\\sqrt[n]{x}")
		)));

		// ============================================================
		// <extensible_arrow> 可扩展箭头
		// ============================================================
		categories.add(new FormulaCategory("<extensible_arrow> 可扩展箭头", Arrays.asList(
				new MathFormula("xrightarrow", "a \\xrightarrow{text} b"),
				new MathFormula("xleftarrow", "a \\xleftarrow{text} b"),
				new MathFormula("xrightarrow 带下标", "a \\xrightarrow[below]{above} b"),
				new MathFormula("xleftarrow 带下标", "a \\xleftarrow[below]{above} b"),
				new MathFormula("xleftrightarrow", "a \\xleftrightarrow{text} b"),
				new MathFormula("xRightarrow", "a \\xRightarrow{text} b"),
				new MathFormula("xLeftarrow", "a \\xLeftarrow{text} b"),
				new MathFormula("xLeftrightarrow", "a \\xLeftrightarrow{text} b"),
				new MathFormula("xhookrightarrow", "a \\xhookrightarrow{text} b"),
				new MathFormula("xhookleftarrow", "a \\xhookleftarrow{text} b"),
				new MathFormula("xtwoheadrightarrow", "a \\xtwoheadrightarrow{text} b"),
				new MathFormula("xtwoheadleftarrow", "a \\xtwoheadleftarrow{text} b"),
				new MathFormula("xmapsto", "a \\xmapsto{text} b"),
				new MathFormula("xtofrom", "a \\xtofrom{text} b")
		)));

		// ============================================================
		// <delimited> 定界符 - left/right
		// ============================================================
		categories.add(new FormulaCategory("<delimited> 定界符-left/right", Arrays.asList(
				new MathFormula("left/right ()", "\\left(x\\right)"),
				new MathFormula("left/right []", "\\left[x\\right]"),
				new MathFormula("left/right {}", "\\left\\{x\\right\\}"),
				new MathFormula("left/right ||", "\\left|x\\right|"),
				new MathFormula("left/right ‖‖", "\\left\\|x\\right\\|"),
				new MathFormula("left/right ⟨⟩", "\\left\\langle x\\right\\rangle"),
				new MathFormula("left/right ⌊⌋", "\\left\\lfloor x\\right\\rfloor"),
				new MathFormula("left/right ⌈⌉", "\\left\\lceil x\\right\\rceil"),
				new MathFormula("left/right lvert", "\\left\\lvert x\\right\\rvert"),
				new MathFormula("left/right lVert", "\\left\\lVert x\\right\\rVert"),
				new MathFormula("left/right 空定界符", "\\left. \\frac{df}{dx} \\right|_{x=0}")
		)));

		// ============================================================
		// <delimited> 定界符 - big系列
		// ============================================================
		categories.add(new FormulaCategory("<delimited> 定界符-big系列", Arrays.asList(
				new MathFormula("bigl/bigr", "\\bigl(x\\bigr)"),
				new MathFormula("Bigl/Bigr", "\\Bigl(x\\Bigr)"),
				new MathFormula("biggl/biggr", "\\biggl(x\\biggr)"),
				new MathFormula("Biggl/Biggr", "\\Biggl(x\\Biggr)")
		)));

		// ============================================================
		// <function_call> 函数 - 三角函数
		// ============================================================
		categories.add(new FormulaCategory("<function_call> 函数-三角", Arrays.asList(
				new MathFormula("sin", "\\sin x"),
				new MathFormula("cos", "\\cos x"),
				new MathFormula("tan", "\\tan x"),
				new MathFormula("cot", "\\cot x"),
				new MathFormula("sec", "\\sec x"),
				new MathFormula("csc", "\\csc x"),
				new MathFormula("arcsin", "\\arcsin x"),
				new MathFormula("arccos", "\\arccos x"),
				new MathFormula("arctan", "\\arctan x")
		)));

		// ============================================================
		// <function_call> 函数 - 双曲函数
		// ============================================================
		categories.add(new FormulaCategory("<function_call> 函数-双曲", Arrays.asList(
				new MathFormula("sinh", "\\sinh x"),
				new MathFormula("cosh", "\\cosh x"),
				new MathFormula("tanh", "\\tanh x"),
				new MathFormula("coth", "\\coth x")
		)));

		// ============================================================
		// <function_call> 函数 - 对数/指数
		// ============================================================
		categories.add(new FormulaCategory("<function_call> 函数-对数指数", Arrays.asList(
				new MathFormula("log", "\\log x"),
				new MathFormula("log 带底数", "\\log_2 x"),
				new MathFormula("ln", "\\ln x"),
				new MathFormula("lg", "\\lg x"),
				new MathFormula("exp", "\\exp x")
		)));

		// ============================================================
		// <function_call> 函数 - 其他
		// ============================================================
		categories.add(new FormulaCategory("<function_call> 函数-其他", Arrays.asList(
				new MathFormula("max", "\\max\\{a,b\\}"),
				new MathFormula("min", "\\min\\{a,b\\}"),
				new MathFormula("sup", "\\sup A"),
				new MathFormula("inf", "\\inf A"),
				new MathFormula("arg", "\\arg z"),
				new MathFormula("deg", "\\deg p"),
				new MathFormula("det", "\\det A"),
				new MathFormula("dim", "\\dim V"),
				new MathFormula("gcd", "\\gcd\\left(a,b\\right)"),
				new MathFormula("hom", "\\hom(A,B)"),
				new MathFormula("ker", "\\ker f"),
				new MathFormula("Pr", "\\Pr(X=x)"),
				new MathFormula("bmod", "a \\bmod b"),
				new MathFormula("pmod", "a \\equiv b \\pmod{n}")
		)));

		// ============================================================
		// <large_operator> 大型运算符 - 求和/积
		// ============================================================
		categories.add(new FormulaCategory("<large_operator> 大型运算符-求和积", Arrays.asList(
				new MathFormula("∑ (sum)", "\\sum_{i=1}^{n}"),
				new MathFormula("∏ (prod)", "\\prod_{i=1}^{n}"),
				new MathFormula("∐ (coprod)", "\\coprod_{i=1}^{n}")
		)));

		// ============================================================
		// <large_operator> 大型运算符 - 积分
		// ============================================================
		categories.add(new FormulaCategory("<large_operator> 大型运算符-积分", Arrays.asList(
				new MathFormula("∫ (int)", "\\int_0^1"),
				new MathFormula("∬ (iint)", "\\iint_D"),
				new MathFormula("∭ (iiint)", "\\iiint_V"),
				new MathFormula("∮ (oint)", "\\oint_C"),
				new MathFormula("∯ (oiint)", "\\oiint_S"),
				new MathFormula("∰ (oiiint)", "\\oiiint_V")
		)));

		// ============================================================
		// <large_operator> 大型运算符 - 集合
		// ============================================================
		categories.add(new FormulaCategory("<large_operator> 大型运算符-集合", Arrays.asList(
				new MathFormula("⋃ (bigcup)", "\\bigcup_{i=1}^{n}"),
				new MathFormula("⋂ (bigcap)", "\\bigcap_{i=1}^{n}"),
				new MathFormula("⋁ (bigvee)", "\\bigvee_{i=1}^{n}"),
				new MathFormula("⋀ (bigwedge)", "\\bigwedge_{i=1}^{n}"),
				new MathFormula("⨁ (bigoplus)", "\\bigoplus_{i=1}^{n}"),
				new MathFormula("⨂ (bigotimes)", "\\bigotimes_{i=1}^{n}"),
				new MathFormula("⨀ (bigodot)", "\\bigodot_{i=1}^{n}"),
				new MathFormula("⨄ (biguplus)", "\\biguplus_{i=1}^{n}"),
				new MathFormula("⨆ (bigsqcup)", "\\bigsqcup_{i=1}^{n}")
		)));

		// ============================================================
		// <large_operator> 大型运算符 - 极限
		// ============================================================
		categories.add(new FormulaCategory("<large_operator> 大型运算符-极限", Arrays.asList(
				new MathFormula("lim", "\\lim_{x\\to 0}"),
				new MathFormula("limsup", "\\limsup_{n\\to\\infty}"),
				new MathFormula("liminf", "\\liminf_{n\\to\\infty}")
		)));

		// ============================================================
		// <matrix> 矩阵
		// ============================================================
		categories.add(new FormulaCategory("<matrix> 矩阵", Arrays.asList(
				new MathFormula("matrix", "\\begin{matrix}a&b\\\\c&d\\end{matrix}"),
				new MathFormula("pmatrix (圆括号)", "\\begin{pmatrix}1&2\\\\3&4\\end{pmatrix}"),
				new MathFormula("bmatrix (方括号)", "\\begin{bmatrix}1&2\\\\3&4\\end{bmatrix}"),
				new MathFormula("Bmatrix (花括号)", "\\begin{Bmatrix}1&2\\\\3&4\\end{Bmatrix}"),
				new MathFormula("vmatrix (行列式)", "\\begin{vmatrix}1&2\\\\3&4\\end{vmatrix}"),
				new MathFormula("Vmatrix (双竖线)", "\\begin{Vmatrix}1&2\\\\3&4\\end{Vmatrix}"),
				new MathFormula("smallmatrix", "\\left(\\begin{smallmatrix}a&b\\\\c&d\\end{smallmatrix}\\right)"),
				new MathFormula("array", "\\begin{array}{cc}a&b\\\\c&d\\end{array}"),
				new MathFormula("array 带对齐", "\\begin{array}{lcr}l&c&r\\end{array}"),
				new MathFormula("cases (分段函数)", "\\begin{cases}x,&x\\ge 0\\\\-x,&x<0\\end{cases}")
		)));

		// ============================================================
		// <text> 文本命令
		// ============================================================
		categories.add(new FormulaCategory("<text> 文本命令", Arrays.asList(
				new MathFormula("text", "x \\text{ if } y"),
				new MathFormula("mbox", "x \\mbox{ where } y"),
				new MathFormula("textrm", "\\textrm{text}"),
				new MathFormula("textit", "\\textit{italic}"),
				new MathFormula("textbf", "\\textbf{bold}")
		)));

		// ============================================================
		// <font_command> 字体命令
		// ============================================================
		categories.add(new FormulaCategory("<font_command> 字体命令", Arrays.asList(
				new MathFormula("mathrm (罗马体)", "\\mathrm{d}x"),
				new MathFormula("mathit (斜体)", "\\mathit{diff}"),
				new MathFormula("mathbf (粗体)", "\\mathbf{x}"),
				new MathFormula("mathsf (无衬线)", "\\mathsf{x}"),
				new MathFormula("mathtt (打字机)", "\\mathtt{x}"),
				new MathFormula("mathcal (花体)", "\\mathcal{L}"),
				new MathFormula("mathbb (黑板粗体)", "\\mathbb{R}"),
				new MathFormula("mathfrak (哥特体)", "\\mathfrak{g}"),
				new MathFormula("mathscr (手写体)", "\\mathscr{L}"),
				new MathFormula("boldsymbol", "\\boldsymbol{\\alpha}"),
				new MathFormula("bm", "\\bm{x}")
		)));

		// ============================================================
		// <accent> 重音符号 - 帽子/波浪/横线
		// ============================================================
		categories.add(new FormulaCategory("<accent> 重音符号-帽子波浪横线", Arrays.asList(
				new MathFormula("hat", "\\hat{x}"),
				new MathFormula("widehat", "\\widehat{xyz}"),
				new MathFormula("tilde", "\\tilde{x}"),
				new MathFormula("widetilde", "\\widetilde{xyz}"),
				new MathFormula("bar", "\\bar{x}"),
				new MathFormula("overline", "\\overline{xyz}"),
				new MathFormula("underline", "\\underline{xyz}")
		)));

		// ============================================================
		// <accent> 重音符号 - 向量/箭头
		// ============================================================
		categories.add(new FormulaCategory("<accent> 重音符号-向量箭头", Arrays.asList(
				new MathFormula("vec", "\\vec{v}"),
				new MathFormula("overrightarrow", "\\overrightarrow{AB}"),
				new MathFormula("overleftarrow", "\\overleftarrow{AB}")
		)));

		// ============================================================
		// <accent> 重音符号 - 点
		// ============================================================
		categories.add(new FormulaCategory("<accent> 重音符号-点", Arrays.asList(
				new MathFormula("dot", "\\dot{x}"),
				new MathFormula("ddot", "\\ddot{x}"),
				new MathFormula("dddot", "\\dddot{x}")
		)));

		// ============================================================
		// <accent> 重音符号 - 其他
		// ============================================================
		categories.add(new FormulaCategory("<accent> 重音符号-其他", Arrays.asList(
				new MathFormula("acute", "\\acute{x}"),
				new MathFormula("grave", "\\grave{x}"),
				new MathFormula("breve", "\\breve{x}"),
				new MathFormula("check", "\\check{x}"),
				new MathFormula("mathring", "\\mathring{x}")
		)));

		// ============================================================
		// <accent> 重音符号 - 括号
		// ============================================================
		categories.add(new FormulaCategory("<accent> 重音符号-括号", Arrays.asList(
				new MathFormula("overbrace", "\\overbrace{a+b+c}"),
				new MathFormula("underbrace", "\\underbrace{a+b+c}"),
				new MathFormula("overbrace 带标注", "\\overbrace{a+b+c}^{n}"),
				new MathFormula("underbrace 带标注", "\\underbrace{a+b+c}_{n}")
		)));

		// ============================================================
		// <spacing> 空格命令
		// ============================================================
		categories.add(new FormulaCategory("<spacing> 空格命令", Arrays.asList(
				new MathFormula("细空格 \\,", "a\\,b"),
				new MathFormula("中空格 \\:", "a\\:b"),
				new MathFormula("粗空格 \\;", "a\\;b"),
				new MathFormula("负空格 \\!", "a\\!b"),
				new MathFormula("quad", "a\\quad b"),
				new MathFormula("qquad", "a\\qquad b"),
				new MathFormula("hspace em", "a\\hspace{1em}b"),
				new MathFormula("hspace pt", "a\\hspace{10pt}b"),
				new MathFormula("phantom", "a\\phantom{xyz}b"),
				new MathFormula("hphantom", "a\\hphantom{xyz}b"),
				new MathFormula("vphantom", "a\\vphantom{xyz}b")
		)));

		// ============================================================
		// 世界公式 - 基础
		// ============================================================
		categories.add(new FormulaCategory("世界公式 - 基础", Arrays.asList(
				new MathFormula("二次公式", "\\frac{-b\\pm\\sqrt{b^2-4ac}}{2a}"),
				new MathFormula("欧拉公式", "e^{i\\pi}+1=0"),
				new MathFormula("勾股定理", "a^2+b^2=c^2"),
				new MathFormula("极限定义", "\\lim_{x\\to 0}\\frac{\\sin x}{x}=1"),
				new MathFormula("求和公式", "\\sum_{n=1}^{\\infty}\\frac{1}{n^2}=\\frac{\\pi^2}{6}")
		)));

		// ============================================================
		// 世界公式 - 微积分
		// ============================================================
		categories.add(new FormulaCategory("世界公式 - 微积分", Arrays.asList(
				new MathFormula("高斯积分", "\\int_0^{\\infty}e^{-x^2}dx=\\frac{\\sqrt{\\pi}}{2}"),
				new MathFormula("导数定义", "f'\\left(x\\right)=\\lim_{h\\to 0}\\frac{f\\left(x+h\\right)-f\\left(x\\right)}{h}"),
				new MathFormula("泰勒级数", "f\\left(x\\right)=\\sum_{n=0}^{\\infty}\\frac{f^{\\left(n\\right)}\\left(a\\right)}{n!}\\left(x-a\\right)^n")
		)));

		// ============================================================
		// 世界公式 - 物理
		// ============================================================
		categories.add(new FormulaCategory("世界公式 - 物理", Arrays.asList(
				new MathFormula("薛定谔方程", "i\\hbar\\frac{\\partial}{\\partial t}\\Psi=\\left[-\\frac{\\hbar^2}{2m}\\nabla^2+V\\right]\\Psi"),
				new MathFormula("麦克斯韦方程", "\\nabla\\cdot\\mathbf{E}=\\frac{\\rho}{\\epsilon_0}"),
				new MathFormula("海森堡不确定性", "\\Delta x\\cdot\\Delta p\\ge\\frac{\\hbar}{2}"),
				new MathFormula("玻尔兹曼熵", "S=k_B\\ln\\Omega"),
				new MathFormula("洛伦兹变换", "x'=\\gamma\\left(x-vt\\right)")
		)));

		// ============================================================
		// 世界公式 - 概率统计
		// ============================================================
		categories.add(new FormulaCategory("世界公式 - 概率统计", Arrays.asList(
				new MathFormula("正态分布", "f\\left(x\\right)=\\frac{1}{\\sigma\\sqrt{2\\pi}}e^{-\\frac{\\left(x-\\mu\\right)^2}{2\\sigma^2}}"),
				new MathFormula("二项式定理", "\\left(x+y\\right)^n=\\sum_{k=0}^{n}\\binom{n}{k}x^{n-k}y^k"),
				new MathFormula("柯西-施瓦茨不等式", "\\left(\\sum_{i=1}^{n}a_i b_i\\right)^2\\le\\left(\\sum_{i=1}^{n}a_i^2\\right)\\left(\\sum_{i=1}^{n}b_i^2\\right)")
		)));

		// ============================================================
		// 世界公式 - 变换
		// ============================================================
		categories.add(new FormulaCategory("世界公式 - 变换", Arrays.asList(
				new MathFormula("傅里叶变换", "F\\left(\\omega\\right)=\\int_{-\\infty}^{\\infty}f\\left(t\\right)e^{-i\\omega t}dt"),
				new MathFormula("拉普拉斯变换", "\\mathcal{L}\\left\\{f\\left(t\\right)\\right\\}=\\int_0^{\\infty}f\\left(t\\right)e^{-st}dt")
		)));

		// ============================================================
		// 世界公式 - 向量微积分
		// ============================================================
		categories.add(new FormulaCategory("世界公式 - 向量微积分", Arrays.asList(
				new MathFormula("散度定理", "\\iiint_V\\left(\\nabla\\cdot\\mathbf{F}\\right)dV=\\iint_{\\partial V}\\mathbf{F}\\cdot d\\mathbf{S}"),
				new MathFormula("斯托克斯定理", "\\int_S\\left(\\nabla\\times\\mathbf{F}\\right)\\cdot d\\mathbf{S}=\\oint_{\\partial S}\\mathbf{F}\\cdot d\\mathbf{r}")
		)));

		return categories;
	}

	// 公式分类（一级）
	private static class FormulaCategory {
		final String name;
		final List<MathFormula> formulas;
		boolean expanded = false;  // 控制展开/折叠

		FormulaCategory(String name, List<MathFormula> formulas) {
			this.name = name;
			this.formulas = formulas;
		}
	}

	// 单个公式（二级）
	private static class MathFormula {
		final String name;
		final String formula;

		MathFormula(String name, String formula) {
			this.name = name;
			this.formula = formula;
		}
	}

	// 点击回调接口
	interface OnFormulaClickListener {
		void onClick(String formula);
	}

	// 列表项类型
	private static final int TYPE_CATEGORY = 0;
	private static final int TYPE_FORMULA = 1;

	// 列表项包装类
	private static class ListItem {
		final int type;
		final FormulaCategory category;  // TYPE_CATEGORY 时使用
		final MathFormula formula;       // TYPE_FORMULA 时使用

		ListItem(FormulaCategory category) {
			this.type = TYPE_CATEGORY;
			this.category = category;
			this.formula = null;
		}

		ListItem(MathFormula formula) {
			this.type = TYPE_FORMULA;
			this.category = null;
			this.formula = formula;
		}
	}

	// Adapter
	private class MathAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
		private final List<FormulaCategory> mCategories;
		private final List<ListItem> mItems = new ArrayList<>();
		private final OnFormulaClickListener mListener;

		MathAdapter(List<FormulaCategory> categories, OnFormulaClickListener listener) {
			mCategories = categories;
			mListener = listener;
			buildItems();
		}

		private void buildItems() {
			mItems.clear();
			for (FormulaCategory category : mCategories) {
				mItems.add(new ListItem(category));
				if (category.expanded) {
					for (MathFormula formula : category.formulas) {
						mItems.add(new ListItem(formula));
					}
				}
			}
		}

		@Override
		public int getItemViewType(int position) {
			return mItems.get(position).type;
		}

		@NonNull
		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			if (viewType == TYPE_CATEGORY) {
				View view = inflater.inflate(R.layout.item_formula_category, parent, false);
				return new CategoryViewHolder(view);
			} else {
				View view = inflater.inflate(R.layout.item_math_formula, parent, false);
				return new FormulaViewHolder(view);
			}
		}

		@Override
		public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
			ListItem item = mItems.get(position);
			if (holder instanceof CategoryViewHolder) {
				CategoryViewHolder vh = (CategoryViewHolder) holder;
				FormulaCategory category = item.category;
				vh.tvName.setText(category.name + " (" + category.formulas.size() + ")");
				vh.ivArrow.setRotation(category.expanded ? 90 : 0);
				vh.itemView.setOnClickListener(v -> {
					category.expanded = !category.expanded;
					buildItems();
					notifyDataSetChanged();
				});
			} else {
				FormulaViewHolder vh = (FormulaViewHolder) holder;
				MathFormula formula = item.formula;
				vh.tvName.setText(formula.name);
				vh.tvFormula.setText(formula.formula);
				vh.itemView.setOnClickListener(v -> mListener.onClick(formula.formula));
			}
		}

		@Override
		public int getItemCount() {
			return mItems.size();
		}
	}

	static class CategoryViewHolder extends RecyclerView.ViewHolder {
		final TextView tvName;
		final TextView ivArrow;

		CategoryViewHolder(View itemView) {
			super(itemView);
			tvName = itemView.findViewById(R.id.tv_category_name);
			ivArrow = itemView.findViewById(R.id.iv_arrow);
		}
	}

	static class FormulaViewHolder extends RecyclerView.ViewHolder {
		final TextView tvName;
		final TextView tvFormula;

		FormulaViewHolder(View itemView) {
			super(itemView);
			tvName = itemView.findViewById(R.id.tv_name);
			tvFormula = itemView.findViewById(R.id.tv_formula);
		}
	}
}