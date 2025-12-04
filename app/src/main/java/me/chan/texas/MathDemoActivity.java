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
import java.util.List;

import me.chan.texas.debug.R;

public class MathDemoActivity extends AppCompatActivity {
	private MathView mMathView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_math_demo);

		RecyclerView recyclerView = findViewById(R.id.rv);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(new MathAdapter(getMathFormulas(), this::render));

		mMathView = findViewById(R.id.math);
	}

	private void render(String math) {
		mMathView.render(math);
	}

	private List<MathFormula> getMathFormulas() {
		List<MathFormula> formulas = new ArrayList<>();

		// ============================================================
		// 基础元素
		// ============================================================
		formulas.add(new MathFormula("整数", "123"));
		formulas.add(new MathFormula("小数", "3.14"));
		formulas.add(new MathFormula("变量", "x"));
		formulas.add(new MathFormula("多字母变量", "abc"));
		formulas.add(new MathFormula("带撇变量", "f'"));
		formulas.add(new MathFormula("双撇变量", "g''"));

		// ============================================================
		// 希腊字母 - 小写
		// ============================================================
		formulas.add(new MathFormula("α (alpha)", "\\alpha"));
		formulas.add(new MathFormula("β (beta)", "\\beta"));
		formulas.add(new MathFormula("γ (gamma)", "\\gamma"));
		formulas.add(new MathFormula("δ (delta)", "\\delta"));
		formulas.add(new MathFormula("ε (epsilon)", "\\epsilon"));
		formulas.add(new MathFormula("ϵ (varepsilon)", "\\varepsilon"));
		formulas.add(new MathFormula("ζ (zeta)", "\\zeta"));
		formulas.add(new MathFormula("η (eta)", "\\eta"));
		formulas.add(new MathFormula("θ (theta)", "\\theta"));
		formulas.add(new MathFormula("ϑ (vartheta)", "\\vartheta"));
		formulas.add(new MathFormula("ι (iota)", "\\iota"));
		formulas.add(new MathFormula("κ (kappa)", "\\kappa"));
		formulas.add(new MathFormula("λ (lambda)", "\\lambda"));
		formulas.add(new MathFormula("μ (mu)", "\\mu"));
		formulas.add(new MathFormula("ν (nu)", "\\nu"));
		formulas.add(new MathFormula("ξ (xi)", "\\xi"));
		formulas.add(new MathFormula("π (pi)", "\\pi"));
		formulas.add(new MathFormula("ϖ (varpi)", "\\varpi"));
		formulas.add(new MathFormula("ρ (rho)", "\\rho"));
		formulas.add(new MathFormula("ϱ (varrho)", "\\varrho"));
		formulas.add(new MathFormula("σ (sigma)", "\\sigma"));
		formulas.add(new MathFormula("ς (varsigma)", "\\varsigma"));
		formulas.add(new MathFormula("τ (tau)", "\\tau"));
		formulas.add(new MathFormula("υ (upsilon)", "\\upsilon"));
		formulas.add(new MathFormula("φ (phi)", "\\phi"));
		formulas.add(new MathFormula("ϕ (varphi)", "\\varphi"));
		formulas.add(new MathFormula("χ (chi)", "\\chi"));
		formulas.add(new MathFormula("ψ (psi)", "\\psi"));
		formulas.add(new MathFormula("ω (omega)", "\\omega"));

		// ============================================================
		// 希腊字母 - 大写
		// ============================================================
		formulas.add(new MathFormula("Γ (Gamma)", "\\Gamma"));
		formulas.add(new MathFormula("Δ (Delta)", "\\Delta"));
		formulas.add(new MathFormula("Θ (Theta)", "\\Theta"));
		formulas.add(new MathFormula("Λ (Lambda)", "\\Lambda"));
		formulas.add(new MathFormula("Ξ (Xi)", "\\Xi"));
		formulas.add(new MathFormula("Π (Pi)", "\\Pi"));
		formulas.add(new MathFormula("Σ (Sigma)", "\\Sigma"));
		formulas.add(new MathFormula("Υ (Upsilon)", "\\Upsilon"));
		formulas.add(new MathFormula("Φ (Phi)", "\\Phi"));
		formulas.add(new MathFormula("Ψ (Psi)", "\\Psi"));
		formulas.add(new MathFormula("Ω (Omega)", "\\Omega"));

		// ============================================================
		// 特殊变量符号
		// ============================================================
		formulas.add(new MathFormula("ℏ (hbar)", "\\hbar"));
		formulas.add(new MathFormula("∇ (nabla)", "\\nabla"));
		formulas.add(new MathFormula("∂ (partial)", "\\partial"));
		formulas.add(new MathFormula("ℓ (ell)", "\\ell"));
		formulas.add(new MathFormula("℘ (wp)", "\\wp"));
		formulas.add(new MathFormula("ℜ (Re)", "\\Re"));
		formulas.add(new MathFormula("ℑ (Im)", "\\Im"));
		formulas.add(new MathFormula("ℵ (aleph)", "\\aleph"));

		// ============================================================
		// 特殊符号
		// ============================================================
		formulas.add(new MathFormula("∞ (infty)", "\\infty"));
		formulas.add(new MathFormula("… (dots)", "\\dots"));
		formulas.add(new MathFormula("… (ldots)", "\\ldots"));
		formulas.add(new MathFormula("⋯ (cdots)", "\\cdots"));
		formulas.add(new MathFormula("⋮ (vdots)", "\\vdots"));
		formulas.add(new MathFormula("⋱ (ddots)", "\\ddots"));
		formulas.add(new MathFormula("∠ (angle)", "\\angle"));
		formulas.add(new MathFormula("∴ (therefore)", "\\therefore"));
		formulas.add(new MathFormula("∵ (because)", "\\because"));
		formulas.add(new MathFormula("∀ (forall)", "\\forall"));
		formulas.add(new MathFormula("∃ (exists)", "\\exists"));
		formulas.add(new MathFormula("∄ (nexists)", "\\nexists"));
		formulas.add(new MathFormula("∅ (emptyset)", "\\emptyset"));
		formulas.add(new MathFormula("∅ (varnothing)", "\\varnothing"));

		// ============================================================
		// 一元运算符
		// ============================================================
		formulas.add(new MathFormula("负号 -x", "-x"));
		formulas.add(new MathFormula("正号 +x", "+x"));
		formulas.add(new MathFormula("± (pm)", "\\pm x"));
		formulas.add(new MathFormula("∓ (mp)", "\\mp x"));

		// ============================================================
		// 二元运算符 - 算术
		// ============================================================
		formulas.add(new MathFormula("加法 +", "a + b"));
		formulas.add(new MathFormula("减法 -", "a - b"));
		formulas.add(new MathFormula("乘法 *", "a * b"));
		formulas.add(new MathFormula("除法 /", "a / b"));
		formulas.add(new MathFormula("× (times)", "a \\times b"));
		formulas.add(new MathFormula("⋅ (cdot)", "a \\cdot b"));
		formulas.add(new MathFormula("÷ (div)", "a \\div b"));
		formulas.add(new MathFormula("± (pm)", "a \\pm b"));
		formulas.add(new MathFormula("∓ (mp)", "a \\mp b"));

		// ============================================================
		// 二元运算符 - 关系
		// ============================================================
		formulas.add(new MathFormula("等于 =", "a = b"));
		formulas.add(new MathFormula("≠ (neq)", "a \\neq b"));
		formulas.add(new MathFormula("≡ (equiv)", "a \\equiv b"));
		formulas.add(new MathFormula("≈ (approx)", "a \\approx b"));
		formulas.add(new MathFormula("≅ (cong)", "a \\cong b"));
		formulas.add(new MathFormula("∼ (sim)", "a \\sim b"));
		formulas.add(new MathFormula("小于 <", "a < b"));
		formulas.add(new MathFormula("大于 >", "a > b"));
		formulas.add(new MathFormula("≤ (le)", "a \\le b"));
		formulas.add(new MathFormula("≥ (ge)", "a \\ge b"));
		formulas.add(new MathFormula("≤ (leq)", "a \\leq b"));
		formulas.add(new MathFormula("≥ (geq)", "a \\geq b"));
		formulas.add(new MathFormula("≪ (ll)", "a \\ll b"));
		formulas.add(new MathFormula("≫ (gg)", "a \\gg b"));

		// ============================================================
		// 二元运算符 - 集合
		// ============================================================
		formulas.add(new MathFormula("∈ (in)", "x \\in A"));
		formulas.add(new MathFormula("∉ (notin)", "x \\notin A"));
		formulas.add(new MathFormula("⊂ (subset)", "A \\subset B"));
		formulas.add(new MathFormula("⊃ (supset)", "A \\supset B"));
		formulas.add(new MathFormula("⊆ (subseteq)", "A \\subseteq B"));
		formulas.add(new MathFormula("⊇ (supseteq)", "A \\supseteq B"));
		formulas.add(new MathFormula("∪ (cup)", "A \\cup B"));
		formulas.add(new MathFormula("∩ (cap)", "A \\cap B"));
		formulas.add(new MathFormula("∧ (wedge)", "p \\wedge q"));
		formulas.add(new MathFormula("∨ (vee)", "p \\vee q"));

		// ============================================================
		// 二元运算符 - 箭头
		// ============================================================
		formulas.add(new MathFormula("→ (to)", "a \\to b"));
		formulas.add(new MathFormula("→ (rightarrow)", "a \\rightarrow b"));
		formulas.add(new MathFormula("← (leftarrow)", "a \\leftarrow b"));
		formulas.add(new MathFormula("↔ (leftrightarrow)", "a \\leftrightarrow b"));
		formulas.add(new MathFormula("⇒ (Rightarrow)", "A \\Rightarrow B"));
		formulas.add(new MathFormula("⇐ (Leftarrow)", "A \\Leftarrow B"));
		formulas.add(new MathFormula("⇔ (Leftrightarrow)", "A \\Leftrightarrow B"));
		formulas.add(new MathFormula("⇒ (implies)", "A \\implies B"));
		formulas.add(new MathFormula("⇔ (iff)", "A \\iff B"));

		// ============================================================
		// 二元运算符 - 几何
		// ============================================================
		formulas.add(new MathFormula("⟂ (perp)", "a \\perp b"));
		formulas.add(new MathFormula("∥ (parallel)", "a \\parallel b"));
		formulas.add(new MathFormula("| (mid)", "a \\mid b"));

		// ============================================================
		// 可扩展箭头
		// ============================================================
		formulas.add(new MathFormula("xrightarrow", "a \\xrightarrow{text} b"));
		formulas.add(new MathFormula("xleftarrow", "a \\xleftarrow{text} b"));

		// ============================================================
		// 大型运算符
		// ============================================================
		formulas.add(new MathFormula("∑ (sum)", "\\sum_{i=1}^{n}"));
		formulas.add(new MathFormula("∏ (prod)", "\\prod_{i=1}^{n}"));
		formulas.add(new MathFormula("∐ (coprod)", "\\coprod_{i=1}^{n}"));
		formulas.add(new MathFormula("∫ (int)", "\\int_0^1"));
		formulas.add(new MathFormula("∬ (iint)", "\\iint_D"));
		formulas.add(new MathFormula("∭ (iiint)", "\\iiint_V"));
		formulas.add(new MathFormula("∮ (oint)", "\\oint_C"));
		formulas.add(new MathFormula("∯ (oiint)", "\\oiint_S"));
		formulas.add(new MathFormula("∰ (oiiint)", "\\oiiint_V"));
		formulas.add(new MathFormula("⋃ (bigcup)", "\\bigcup_{i=1}^{n}"));
		formulas.add(new MathFormula("⋂ (bigcap)", "\\bigcap_{i=1}^{n}"));
		formulas.add(new MathFormula("⋁ (bigvee)", "\\bigvee_{i=1}^{n}"));
		formulas.add(new MathFormula("⋀ (bigwedge)", "\\bigwedge_{i=1}^{n}"));
		formulas.add(new MathFormula("⨁ (bigoplus)", "\\bigoplus_{i=1}^{n}"));
		formulas.add(new MathFormula("⨂ (bigotimes)", "\\bigotimes_{i=1}^{n}"));
		formulas.add(new MathFormula("⨀ (bigodot)", "\\bigodot_{i=1}^{n}"));
		formulas.add(new MathFormula("⨄ (biguplus)", "\\biguplus_{i=1}^{n}"));
		formulas.add(new MathFormula("⨆ (bigsqcup)", "\\bigsqcup_{i=1}^{n}"));
		formulas.add(new MathFormula("lim (极限)", "\\lim_{x\\to 0}"));
		formulas.add(new MathFormula("limsup", "\\limsup_{n\\to\\infty}"));
		formulas.add(new MathFormula("liminf", "\\liminf_{n\\to\\infty}"));

		// ============================================================
		// 函数
		// ============================================================
		formulas.add(new MathFormula("sin", "\\sin x"));
		formulas.add(new MathFormula("cos", "\\cos x"));
		formulas.add(new MathFormula("tan", "\\tan x"));
		formulas.add(new MathFormula("cot", "\\cot x"));
		formulas.add(new MathFormula("sec", "\\sec x"));
		formulas.add(new MathFormula("csc", "\\csc x"));
		formulas.add(new MathFormula("arcsin", "\\arcsin x"));
		formulas.add(new MathFormula("arccos", "\\arccos x"));
		formulas.add(new MathFormula("arctan", "\\arctan x"));
		formulas.add(new MathFormula("sinh", "\\sinh x"));
		formulas.add(new MathFormula("cosh", "\\cosh x"));
		formulas.add(new MathFormula("tanh", "\\tanh x"));
		formulas.add(new MathFormula("coth", "\\coth x"));
		formulas.add(new MathFormula("log", "\\log x"));
		formulas.add(new MathFormula("log_2", "\\log_2 x"));
		formulas.add(new MathFormula("ln", "\\ln x"));
		formulas.add(new MathFormula("lg", "\\lg x"));
		formulas.add(new MathFormula("exp", "\\exp x"));
		formulas.add(new MathFormula("max", "\\max\\{a,b\\}"));
		formulas.add(new MathFormula("min", "\\min\\{a,b\\}"));
		formulas.add(new MathFormula("sup", "\\sup A"));
		formulas.add(new MathFormula("inf", "\\inf A"));
		formulas.add(new MathFormula("det", "\\det A"));
		formulas.add(new MathFormula("dim", "\\dim V"));
		formulas.add(new MathFormula("gcd", "\\gcd\\left(a,b\\right)"));
		formulas.add(new MathFormula("ker", "\\ker f"));

		// ============================================================
		// 分式
		// ============================================================
		formulas.add(new MathFormula("简单分式 frac", "\\frac{1}{2}"));
		formulas.add(new MathFormula("复杂分式", "\\frac{x+y}{x-y}"));
		formulas.add(new MathFormula("嵌套分式", "\\frac{\\frac{1}{2}}{\\frac{3}{4}}"));
		formulas.add(new MathFormula("dfrac (显示样式)", "\\dfrac{1}{2}"));
		formulas.add(new MathFormula("tfrac (文本样式)", "\\tfrac{1}{2}"));

		// ============================================================
		// 根式
		// ============================================================
		formulas.add(new MathFormula("平方根 sqrt", "\\sqrt{x}"));
		formulas.add(new MathFormula("n次根", "\\sqrt[3]{27}"));
		formulas.add(new MathFormula("复杂根式", "\\sqrt{x^2+y^2}"));

		// ============================================================
		// 定界符
		// ============================================================
		formulas.add(new MathFormula("圆括号 ()", "\\left(x\\right)"));
		formulas.add(new MathFormula("方括号 []", "\\left[x\\right]"));
		formulas.add(new MathFormula("花括号 {}", "\\left\\{x\\right\\}"));
		formulas.add(new MathFormula("绝对值 ||", "\\left|x\\right|"));
		formulas.add(new MathFormula("尖括号 ⟨⟩", "\\left\\langle x\\right\\rangle"));
		formulas.add(new MathFormula("地板函数 ⌊⌋", "\\left\\lfloor x\\right\\rfloor"));
		formulas.add(new MathFormula("天花板函数 ⌈⌉", "\\left\\lceil x\\right\\rceil"));
		formulas.add(new MathFormula("bigl/bigr", "\\bigl(x\\bigr)"));
		formulas.add(new MathFormula("Bigl/Bigr", "\\Bigl(x\\Bigr)"));
		formulas.add(new MathFormula("biggl/biggr", "\\biggl(x\\biggr)"));
		formulas.add(new MathFormula("Biggl/Biggr", "\\Biggl(x\\Biggr)"));

		// ============================================================
		// 上下标
		// ============================================================
		formulas.add(new MathFormula("上标 ^", "x^2"));
		formulas.add(new MathFormula("下标 _", "x_i"));
		formulas.add(new MathFormula("上下标", "x^2_1"));
		formulas.add(new MathFormula("复杂上标", "x^{n+1}"));
		formulas.add(new MathFormula("复杂下标", "a_{i,j}"));
		formulas.add(new MathFormula("嵌套上标", "2^{2^n}"));

		// ============================================================
		// 重音符号
		// ============================================================
		formulas.add(new MathFormula("hat", "\\hat{x}"));
		formulas.add(new MathFormula("widehat", "\\widehat{xyz}"));
		formulas.add(new MathFormula("tilde", "\\tilde{x}"));
		formulas.add(new MathFormula("widetilde", "\\widetilde{xyz}"));
		formulas.add(new MathFormula("bar", "\\bar{x}"));
		formulas.add(new MathFormula("overline", "\\overline{xyz}"));
		formulas.add(new MathFormula("underline", "\\underline{xyz}"));
		formulas.add(new MathFormula("vec", "\\vec{v}"));
		formulas.add(new MathFormula("overrightarrow", "\\overrightarrow{AB}"));
		formulas.add(new MathFormula("overleftarrow", "\\overleftarrow{AB}"));
		formulas.add(new MathFormula("dot", "\\dot{x}"));
		formulas.add(new MathFormula("ddot", "\\ddot{x}"));
		formulas.add(new MathFormula("dddot", "\\dddot{x}"));
		formulas.add(new MathFormula("acute", "\\acute{x}"));
		formulas.add(new MathFormula("grave", "\\grave{x}"));
		formulas.add(new MathFormula("breve", "\\breve{x}"));
		formulas.add(new MathFormula("check", "\\check{x}"));
		formulas.add(new MathFormula("mathring", "\\mathring{x}"));
		formulas.add(new MathFormula("overbrace", "\\overbrace{a+b+c}"));
		formulas.add(new MathFormula("underbrace", "\\underbrace{a+b+c}"));

		// ============================================================
		// 字体命令
		// ============================================================
		formulas.add(new MathFormula("mathbf (粗体)", "\\mathbf{x}"));
		formulas.add(new MathFormula("mathbb (黑板粗体)", "\\mathbb{R}"));
		formulas.add(new MathFormula("mathcal (花体)", "\\mathcal{L}"));
		formulas.add(new MathFormula("mathfrak (哥特体)", "\\mathfrak{g}"));
		formulas.add(new MathFormula("mathsf (无衬线)", "\\mathsf{x}"));
		formulas.add(new MathFormula("mathtt (打字机)", "\\mathtt{x}"));
		formulas.add(new MathFormula("mathrm (罗马体)", "\\mathrm{d}x"));

		// ============================================================
		// 二项式系数
		// ============================================================
		formulas.add(new MathFormula("binom", "\\binom{n}{k}"));
		formulas.add(new MathFormula("dbinom", "\\dbinom{n}{k}"));
		formulas.add(new MathFormula("tbinom", "\\tbinom{n}{k}"));

		// ============================================================
		// 阶乘
		// ============================================================
		formulas.add(new MathFormula("阶乘 n!", "n!"));
		formulas.add(new MathFormula("阶乘在分式中", "\\frac{n!}{k!}"));

		// ============================================================
		// 矩阵
		// ============================================================
		formulas.add(new MathFormula("matrix", "\\begin{matrix}a&b\\\\c&d\\end{matrix}"));
		formulas.add(new MathFormula("pmatrix (圆括号)", "\\begin{pmatrix}1&2\\\\3&4\\end{pmatrix}"));
		formulas.add(new MathFormula("bmatrix (方括号)", "\\begin{bmatrix}1&2\\\\3&4\\end{bmatrix}"));
		formulas.add(new MathFormula("Bmatrix (花括号)", "\\begin{Bmatrix}1&2\\\\3&4\\end{Bmatrix}"));
		formulas.add(new MathFormula("vmatrix (行列式)", "\\begin{vmatrix}1&2\\\\3&4\\end{vmatrix}"));
		formulas.add(new MathFormula("Vmatrix (双竖线)", "\\begin{Vmatrix}1&2\\\\3&4\\end{Vmatrix}"));
		formulas.add(new MathFormula("cases (分段函数)", "\\begin{cases}x,&x\\ge 0\\\\-x,&x<0\\end{cases}"));

		// ============================================================
		// 文本命令
		// ============================================================
		formulas.add(new MathFormula("text", "x \\text{ if } y"));

		// ============================================================
		// 空格命令
		// ============================================================
		formulas.add(new MathFormula("quad 空格", "a\\quad b"));
		formulas.add(new MathFormula("qquad 大空格", "a\\qquad b"));
		formulas.add(new MathFormula("细空格 \\,", "a\\, b"));
		formulas.add(new MathFormula("中空格 \\:", "a\\: b"));
		formulas.add(new MathFormula("粗空格 \\;", "a\\; b"));
		formulas.add(new MathFormula("负空格 \\!", "a\\! b"));

		// ============================================================
		// 真实公式 - 基础
		// ============================================================
		formulas.add(new MathFormula("二次公式", "\\frac{-b\\pm\\sqrt{b^2-4ac}}{2a}"));
		formulas.add(new MathFormula("欧拉公式", "e^{i\\pi}+1=0"));
		formulas.add(new MathFormula("勾股定理", "a^2+b^2=c^2"));
		formulas.add(new MathFormula("极限定义", "\\lim_{x\\to 0}\\frac{\\sin x}{x}=1"));
		formulas.add(new MathFormula("求和公式", "\\sum_{n=1}^{\\infty}\\frac{1}{n^2}=\\frac{\\pi^2}{6}"));

		// ============================================================
		// 真实公式 - 微积分
		// ============================================================
		formulas.add(new MathFormula("高斯积分", "\\int_0^{\\infty}e^{-x^2}dx=\\frac{\\sqrt{\\pi}}{2}"));
		formulas.add(new MathFormula("导数定义", "f'\\left(x\\right)=\\lim_{h\\to 0}\\frac{f\\left(x+h\\right)-f\\left(x\\right)}{h}"));
		formulas.add(new MathFormula("泰勒级数", "f\\left(x\\right)=\\sum_{n=0}^{\\infty}\\frac{f^{\\left(n\\right)}\\left(a\\right)}{n!}\\left(x-a\\right)^n"));

		// ============================================================
		// 真实公式 - 物理
		// ============================================================
		formulas.add(new MathFormula("薛定谔方程", "i\\hbar\\frac{\\partial}{\\partial t}\\Psi=\\left[-\\frac{\\hbar^2}{2m}\\nabla^2+V\\right]\\Psi"));
		formulas.add(new MathFormula("麦克斯韦方程", "\\nabla\\cdot\\mathbf{E}=\\frac{\\rho}{\\epsilon_0}"));
		formulas.add(new MathFormula("海森堡不确定性", "\\Delta x\\cdot\\Delta p\\ge\\frac{\\hbar}{2}"));
		formulas.add(new MathFormula("玻尔兹曼熵", "S=k_B\\ln\\Omega"));
		formulas.add(new MathFormula("洛伦兹变换", "x'=\\gamma\\left(x-vt\\right)"));

		// ============================================================
		// 真实公式 - 概率统计
		// ============================================================
		formulas.add(new MathFormula("正态分布", "f\\left(x\\right)=\\frac{1}{\\sigma\\sqrt{2\\pi}}e^{-\\frac{\\left(x-\\mu\\right)^2}{2\\sigma^2}}"));
		formulas.add(new MathFormula("二项式定理", "\\left(x+y\\right)^n=\\sum_{k=0}^{n}\\binom{n}{k}x^{n-k}y^k"));
		formulas.add(new MathFormula("柯西-施瓦茨不等式", "\\left(\\sum_{i=1}^{n}a_i b_i\\right)^2\\le\\left(\\sum_{i=1}^{n}a_i^2\\right)\\left(\\sum_{i=1}^{n}b_i^2\\right)"));

		// ============================================================
		// 真实公式 - 傅里叶/拉普拉斯
		// ============================================================
		formulas.add(new MathFormula("傅里叶变换", "F\\left(\\omega\\right)=\\int_{-\\infty}^{\\infty}f\\left(t\\right)e^{-i\\omega t}dt"));
		formulas.add(new MathFormula("拉普拉斯变换", "\\mathcal{L}\\left\\{f\\left(t\\right)\\right\\}=\\int_0^{\\infty}f\\left(t\\right)e^{-st}dt"));

		// ============================================================
		// 真实公式 - 向量微积分
		// ============================================================
		formulas.add(new MathFormula("散度定理", "\\iiint_V\\left(\\nabla\\cdot\\mathbf{F}\\right)dV=\\iint_{\\partial V}\\mathbf{F}\\cdot d\\mathbf{S}"));
		formulas.add(new MathFormula("斯托克斯定理", "\\int_S\\left(\\nabla\\times\\mathbf{F}\\right)\\cdot d\\mathbf{S}=\\oint_{\\partial S}\\mathbf{F}\\cdot d\\mathbf{r}"));

		return formulas;
	}

	// 数据类
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

	// Adapter
	private static class MathAdapter extends RecyclerView.Adapter<MathAdapter.ViewHolder> {
		private final List<MathFormula> mFormulas;
		private final OnFormulaClickListener mListener;

		MathAdapter(List<MathFormula> formulas, OnFormulaClickListener listener) {
			mFormulas = formulas;
			mListener = listener;
		}

		@NonNull
		@Override
		public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.item_math_formula, parent, false);
			return new ViewHolder(view);
		}

		@Override
		public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
			MathFormula formula = mFormulas.get(position);
			holder.tvName.setText(formula.name);
			holder.tvFormula.setText(formula.formula);
			holder.itemView.setOnClickListener(v -> mListener.onClick(formula.formula));
		}

		@Override
		public int getItemCount() {
			return mFormulas.size();
		}

		static class ViewHolder extends RecyclerView.ViewHolder {
			final TextView tvName;
			final TextView tvFormula;

			ViewHolder(View itemView) {
				super(itemView);
				tvName = itemView.findViewById(R.id.tv_name);
				tvFormula = itemView.findViewById(R.id.tv_formula);
			}
		}
	}
}