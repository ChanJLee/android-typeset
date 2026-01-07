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
import me.chan.texas.ext.markdown.math.view.MathView;

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
//		mMathView.render("afg \\, " + formula + " \\, afg");
		mMathView.render(formula);
		mTextView.setText(formula);
	}

	private List<FormulaCategory> getFormulaCategories() {
		List<FormulaCategory> categories = new ArrayList<>();
		categories.add(new FormulaCategory("自定义", Arrays.asList(
				new MathFormula("混合", "--a\\alpha!++babcdfg\\iota"),
				new MathFormula("div", "div  \\div = ÷ "),
				new MathFormula("preceq", "preceq  \\preceq = ≼"),
				new MathFormula("succeq", "succeq  \\succeq = ≽"),
				new MathFormula("re", "re  \\Re = ℜ"),
				new MathFormula("im", "Im  \\Im = ℑ")
		)));

		// ============================================================
		// 1. BNF: <greek_letter> (希腊字母 - 穷举所有 BNF 定义)
		// ============================================================
		categories.add(new FormulaCategory("希腊字母 (Greek Letters)", Arrays.asList(
				// 小写
				new MathFormula("\\alpha", "\\alpha"),
				new MathFormula("\\beta", "\\beta"),
				new MathFormula("\\gamma", "\\gamma"),
				new MathFormula("\\delta", "\\delta"),
				new MathFormula("\\epsilon", "\\epsilon"),
				new MathFormula("\\varepsilon (var)", "\\varepsilon"),
				new MathFormula("\\zeta", "\\zeta"),
				new MathFormula("\\eta", "\\eta"),
				new MathFormula("\\theta", "\\theta"),
				new MathFormula("\\vartheta (var)", "\\vartheta"),
				new MathFormula("\\iota", "\\iota"),
				new MathFormula("\\kappa", "\\kappa"),
				new MathFormula("\\lambda", "\\lambda"),
				new MathFormula("\\mu", "\\mu"),
				new MathFormula("\\nu", "\\nu"),
				new MathFormula("\\xi", "\\xi"),
				new MathFormula("\\pi", "\\pi"),
				new MathFormula("\\varpi (var)", "\\varpi"),
				new MathFormula("\\rho", "\\rho"),
				new MathFormula("\\varrho (var)", "\\varrho"),
				new MathFormula("\\sigma", "\\sigma"),
				new MathFormula("\\varsigma (var)", "\\varsigma"),
				new MathFormula("\\tau", "\\tau"),
				new MathFormula("\\upsilon", "\\upsilon"),
				new MathFormula("\\phi", "\\phi"),
				new MathFormula("\\varphi (var)", "\\varphi"),
				new MathFormula("\\chi", "\\chi"),
				new MathFormula("\\psi", "\\psi"),
				new MathFormula("\\omega", "\\omega"),

				// 大写
				new MathFormula("\\Gamma", "\\Gamma"),
				new MathFormula("\\Delta", "\\Delta"),
				new MathFormula("\\Theta", "\\Theta"),
				new MathFormula("\\Lambda", "\\Lambda"),
				new MathFormula("\\Xi", "\\Xi"),
				new MathFormula("\\Pi", "\\Pi"),
				new MathFormula("\\Sigma", "\\Sigma"),
				new MathFormula("\\Upsilon", "\\Upsilon"),
				new MathFormula("\\Phi", "\\Phi"),
				new MathFormula("\\Psi", "\\Psi"),
				new MathFormula("\\Omega", "\\Omega"),

				// BNF 中 \infty 定义在 <greek_letter> 末尾
				new MathFormula("\\infty", "\\infty")
		)));

		// ============================================================
		// 2. BNF: <operand_atom> -> <special_letter_variable>
		// ============================================================
		categories.add(new FormulaCategory("特殊字母变量 (Special Letters)", Arrays.asList(
				new MathFormula("\\hbar (Planck)", "\\hbar"),
				new MathFormula("\\nabla (Nabla)", "\\nabla"),
				new MathFormula("\\partial (Partial)", "\\partial"),
				new MathFormula("\\ell (Script l)", "\\ell"),
				new MathFormula("\\wp (Weierstrass)", "\\wp"),
				new MathFormula("\\Re (Real)", "\\Re"),
				new MathFormula("\\Im (Imaginary)", "\\Im"),
				new MathFormula("\\aleph (Aleph)", "\\aleph")
		)));

		// ============================================================
		// 3. BNF: <unary_op> & <postfix_op>
		// ============================================================
		categories.add(new FormulaCategory("一元与后缀运算符 (Unary & Postfix)", Arrays.asList(
				// Unary
				new MathFormula("+", "+x"),
				new MathFormula("-", "-x"),
				new MathFormula("\\pm", "\\pm x"),
				new MathFormula("\\mp", "\\mp x"),
				new MathFormula("!", "!x"), // 逻辑非或阶乘前缀(视语义)
				new MathFormula("\\neg", "\\neg A"),
				// Postfix
				new MathFormula("! (Factorial)", "n!"),
				new MathFormula("!! (Double Factorial)", "n!!"),
				// Prime suffix
				new MathFormula("' (Prime)", "f'"),
				new MathFormula("'' (Double Prime)", "f''")
		)));

		// ============================================================
		// 4. BNF: <binary_op> (二元运算符 - 穷举所有 BNF 定义)
		// ============================================================
		categories.add(new FormulaCategory("二元运算符 (Binary Ops)", Arrays.asList(
				// 基础算术
				new MathFormula("+", "a + b"),
				new MathFormula("-", "a - b"),
				new MathFormula("*", "a * b"),
				new MathFormula("/", "a / b"),
				new MathFormula("|", "a | b"), // 竖线作为二元运算
				new MathFormula("\\times", "a \\times b"),
				new MathFormula("\\cdot", "a \\cdot b"),
				new MathFormula("\\div", "a \\div b"),
				new MathFormula("\\pm", "a \\pm b"),
				new MathFormula("\\mp", "a \\mp b"),
				new MathFormula("\\setminus", "A \\setminus B"),
				new MathFormula("\\circ", "f \\circ g"),

				// 圈/点运算符
				new MathFormula("\\oplus", "a \\oplus b"),
				new MathFormula("\\ominus", "a \\ominus b"),
				new MathFormula("\\otimes", "a \\otimes b"),
				new MathFormula("\\oslash", "a \\oslash b"),
				new MathFormula("\\odot", "a \\odot b"),
				new MathFormula("\\bullet", "a \\bullet b"),
				new MathFormula("\\star", "a \\star b"),
				new MathFormula("\\dagger", "a \\dagger b"),
				new MathFormula("\\ddagger", "a \\ddagger b"),

				// 关系运算符
				new MathFormula("=", "a = b"),
				new MathFormula("\\neq", "a \\neq b"),
				new MathFormula("\\equiv", "a \\equiv b"),
				new MathFormula("\\approx", "a \\approx b"),
				new MathFormula("\\cong", "a \\cong b"),
				new MathFormula("\\sim", "a \\sim b"),
				new MathFormula("\\simeq", "a \\simeq b"),
				new MathFormula("\\asymp", "a \\asymp b"),
				new MathFormula("\\propto", "a \\propto b"),

				// 不等式与序
				new MathFormula("<", "a < b"),
				new MathFormula(">", "a > b"),
				new MathFormula("\\le", "a \\le b"),
				new MathFormula("\\ge", "a \\ge b"),
				new MathFormula("\\leq", "a \\leq b"),
				new MathFormula("\\geq", "a \\geq b"),
				new MathFormula("\\ll", "a \\ll b"),
				new MathFormula("\\gg", "a \\gg b"),
				new MathFormula("\\prec", "a \\prec b"),
				new MathFormula("\\succ", "a \\succ b"),
				new MathFormula("\\preceq", "a \\preceq b"),
				new MathFormula("\\succeq", "a \\succeq b"),

				// 集合与逻辑
				new MathFormula("\\in", "x \\in A"),
				new MathFormula("\\notin", "x \\notin A"),
				new MathFormula("\\subset", "A \\subset B"),
				new MathFormula("\\supset", "A \\supset B"),
				new MathFormula("\\subseteq", "A \\subseteq B"),
				new MathFormula("\\supseteq", "A \\supseteq B"),
				new MathFormula("\\cup", "A \\cup B"),
				new MathFormula("\\cap", "A \\cap B"),
				new MathFormula("\\wedge", "p \\wedge q"),
				new MathFormula("\\vee", "p \\vee q"),

				// 箭头与推导
				new MathFormula("\\to", "a \\to b"),
				new MathFormula("\\rightarrow", "a \\rightarrow b"),
				new MathFormula("\\leftarrow", "a \\leftarrow b"),
				new MathFormula("\\leftrightarrow", "a \\leftrightarrow b"),
				new MathFormula("\\Rightarrow", "a \\Rightarrow b"),
				new MathFormula("\\Leftarrow", "a \\Leftarrow b"),
				new MathFormula("\\Leftrightarrow", "a \\Leftrightarrow b"),
				new MathFormula("\\implies", "p \\implies q"),
				new MathFormula("\\iff", "p \\iff q"),

				// 几何与其他
				new MathFormula("\\perp", "a \\perp b"),
				new MathFormula("\\parallel", "a \\parallel b"),
				new MathFormula("\\mid", "a \\mid b"),
				new MathFormula("\\nmid", "a \\nmid b"),
				new MathFormula("\\triangleleft", "A \\triangleleft B"),
				new MathFormula("\\triangleright", "A \\triangleright B"),
				new MathFormula("\\bowtie", "A \\bowtie B"),
				new MathFormula("\\models", "M \\models \\phi")
		)));

		// ============================================================
		// 5. BNF: <function_call> (函数名 - 穷举所有 BNF 定义)
		// ============================================================
		categories.add(new FormulaCategory("标准函数 (Standard Functions)", Arrays.asList(
				// 三角函数
				new MathFormula("\\sin", "\\sin x"),
				new MathFormula("\\cos", "\\cos x"),
				new MathFormula("\\tan", "\\tan x"),
				new MathFormula("\\cot", "\\cot x"),
				new MathFormula("\\sec", "\\sec x"),
				new MathFormula("\\csc", "\\csc x"),
				// 反三角
				new MathFormula("\\arcsin", "\\arcsin x"),
				new MathFormula("\\arccos", "\\arccos x"),
				new MathFormula("\\arctan", "\\arctan x"),
				// 双曲
				new MathFormula("\\sinh", "\\sinh x"),
				new MathFormula("\\cosh", "\\cosh x"),
				new MathFormula("\\tanh", "\\tanh x"),
				new MathFormula("\\coth", "\\coth x"),
				// 对数指数
				new MathFormula("\\log", "\\log x"),
				new MathFormula("\\ln", "\\ln x"),
				new MathFormula("\\lg", "\\lg x"),
				new MathFormula("\\exp", "\\exp x"),
				// 极值与界
				new MathFormula("\\max", "\\max S"),
				new MathFormula("\\min", "\\min S"),
				new MathFormula("\\sup", "\\sup S"),
				new MathFormula("\\inf", "\\inf S"),
				// 线性代数与数论
				new MathFormula("\\arg", "\\arg z"),
				new MathFormula("\\deg", "\\deg P"),
				new MathFormula("\\det", "\\det A"),
				new MathFormula("\\dim", "\\dim V"),
				new MathFormula("\\gcd", "\\gcd(a,b)"),
				new MathFormula("\\hom", "\\hom(V,W)"),
				new MathFormula("\\ker", "\\ker \\phi"),
				new MathFormula("\\Pr", "\\Pr(E)"),
				// 取模
				new MathFormula("\\bmod", "a \\bmod n"),
				new MathFormula("\\pmod", "a \\pmod n")
		)));

		// ============================================================
		// 6. BNF: <large_operator> (大型运算符 - 穷举所有 BNF 定义)
		// ============================================================
		categories.add(new FormulaCategory("大型运算符 (Large Operators)", Arrays.asList(
				new MathFormula("\\sum", "\\sum_{i=1}^n i"),
				new MathFormula("\\prod", "\\prod_{i=1}^n x_i"),
				new MathFormula("\\coprod", "\\coprod_{i=1}^n A_i"),
				new MathFormula("\\int", "\\int_a^b f(x)dx"),
				new MathFormula("\\iint", "\\iint_D f(x,y)dxdy"),
				new MathFormula("\\iiint", "\\iiint_V f(x,y,z)dV"),
				new MathFormula("\\oint", "\\oint_C \\vec{F}\\cdot d\\vec{r}"),
				new MathFormula("\\oiint", "\\oiint_S \\vec{F}\\cdot d\\vec{S}"),
				new MathFormula("\\oiiint", "\\oiiint_V f dV"),
				new MathFormula("\\bigcup", "\\bigcup_{i \\in I} A_i"),
				new MathFormula("\\bigcap", "\\bigcap_{i \\in I} A_i"),
				new MathFormula("\\bigvee", "\\bigvee_{i=1}^n p_i"),
				new MathFormula("\\bigwedge", "\\bigwedge_{i=1}^n p_i"),
				new MathFormula("\\bigoplus", "\\bigoplus V_i"),
				new MathFormula("\\bigotimes", "\\bigotimes V_i"),
				new MathFormula("\\bigodot", "\\bigodot V_i"),
				new MathFormula("\\biguplus", "\\biguplus A_i"),
				new MathFormula("\\bigsqcup", "\\bigsqcup A_i"),
				new MathFormula("\\lim", "\\lim_{x \\to 0} f(x)"),
				new MathFormula("\\limsup", "\\limsup_{n \\to \\infty} a_n"),
				new MathFormula("\\liminf", "\\liminf_{n \\to \\infty} a_n")
		)));

		// ============================================================
		// 7. BNF: <special_symbol> (特殊符号 - 穷举所有 BNF 定义)
		// ============================================================
		categories.add(new FormulaCategory("特殊符号 (Special Symbols)", Arrays.asList(
				// 省略号
				new MathFormula("\\dots", "1, 2, \\dots"),
				new MathFormula("\\ldots", "1, 2, \\ldots, n"),
				new MathFormula("\\cdots", "1 + 2 + \\cdots + n"),
				new MathFormula("\\vdots", "\\vdots"),
				new MathFormula("\\ddots", "\\ddots"),
				// 其他
				new MathFormula("\\angle", "\\angle ABC"),
				new MathFormula("\\therefore", "\\therefore"),
				new MathFormula("\\because", "\\because"),
				new MathFormula("\\forall", "\\forall x"),
				new MathFormula("\\exists", "\\exists y"),
				new MathFormula("\\nexists", "\\nexists z"),
				new MathFormula("\\emptyset", "\\emptyset"),
				new MathFormula("\\varnothing", "\\varnothing")
		)));

		// ============================================================
		// 8. BNF: <accent> (重音/装饰 - 穷举所有 BNF 定义)
		// ============================================================
		categories.add(new FormulaCategory("重音与装饰 (Accents)", Arrays.asList(
				new MathFormula("\\hat", "\\hat{a}"),
				new MathFormula("\\widehat", "\\widehat{abc}"),
				new MathFormula("\\tilde", "\\tilde{a}"),
				new MathFormula("\\widetilde", "\\widetilde{abc}"),
				new MathFormula("\\bar", "\\bar{a}"),
				new MathFormula("\\overline", "\\overline{AB}"),
				new MathFormula("\\underline", "\\underline{AB}"),
				new MathFormula("\\vec", "\\vec{v}"),
				new MathFormula("\\overrightarrow", "\\overrightarrow{AB}"),
				new MathFormula("\\overleftarrow", "\\overleftarrow{AB}"),
				new MathFormula("\\dot", "\\dot{a}"),
				new MathFormula("\\ddot", "\\ddot{a}"),
				new MathFormula("\\dddot", "\\dddot{a}"),
				new MathFormula("\\acute", "\\acute{a}"),
				new MathFormula("\\grave", "\\grave{a}"),
				new MathFormula("\\breve", "\\breve{a}"),
				new MathFormula("\\check", "\\check{a}"),
				new MathFormula("\\mathring", "\\mathring{a}"),
				new MathFormula("\\overbrace", "\\overbrace{a+b+c}"),
				new MathFormula("\\underbrace", "\\underbrace{a+b+c}")
		)));

		// ============================================================
		// 9. BNF: <extensible_arrow> (可扩展箭头 - 穷举所有 BNF 定义)
		// ============================================================
		categories.add(new FormulaCategory("可扩展箭头 (Extensible Arrows)", Arrays.asList(
				new MathFormula("\\xrightarrow", "\\xrightarrow{f}"),
				new MathFormula("\\xleftarrow", "\\xleftarrow{g}"),
				new MathFormula("\\xleftrightarrow", "\\xleftrightarrow{iff}"),
				new MathFormula("\\xRightarrow", "\\xRightarrow{\\text{implies}}"),
				new MathFormula("\\xLeftarrow", "\\xLeftarrow{}"),
				new MathFormula("\\xLeftrightarrow", "\\xLeftrightarrow{\\text{iff}}"),
				new MathFormula("\\xhookrightarrow", "\\xhookrightarrow{i}"),
				new MathFormula("\\xhookleftarrow", "\\xhookleftarrow{}"),
				new MathFormula("\\xtwoheadrightarrow", "\\xtwoheadrightarrow{surj}"),
				new MathFormula("\\xtwoheadleftarrow", "\\xtwoheadleftarrow{}"),
				new MathFormula("\\xmapsto", "\\xmapsto{x}"),
				new MathFormula("\\xtofrom", "\\xtofrom{}")
		)));

		// ============================================================
		// 10. BNF: <font_command> & <text> (字体与文本 - 穷举)
		// ============================================================
		categories.add(new FormulaCategory("字体与文本 (Fonts & Text)", Arrays.asList(
				// Fonts
				new MathFormula("\\mathrm", "\\mathrm{Roman}"),
				new MathFormula("\\mathit", "\\mathit{Italic}"),
				new MathFormula("\\mathbf", "\\mathbf{Bold}"),
				new MathFormula("\\mathsf", "\\mathsf{SansSerif}"),
				new MathFormula("\\mathtt", "\\mathtt{Typewriter}"),
				new MathFormula("\\mathcal", "\\mathcal{CAL}"),
				new MathFormula("\\mathbb", "\\mathbb{R}"),
				new MathFormula("\\mathfrak", "\\mathfrak{g}"),
				new MathFormula("\\mathscr", "\\mathscr{L}"),
				new MathFormula("\\boldsymbol", "\\boldsymbol{\\alpha}"),
				new MathFormula("\\bm", "\\bm{x}"),
				// Text
				new MathFormula("\\text", "\\text{Normal Text}"),
				new MathFormula("\\mbox", "\\mbox{mbox Content}"),
				new MathFormula("\\textrm", "\\textrm{Rm Text}"),
				new MathFormula("\\textit", "\\textit{It Text}"),
				new MathFormula("\\textbf", "\\textbf{Bf Text}"),
				new MathFormula("\\textfield", "\\textfield{Custom}")
		)));

		// ============================================================
		// 11. BNF: <matrix> (矩阵环境 - 穷举)
		// ============================================================
		categories.add(new FormulaCategory("矩阵 (Matrices)", Arrays.asList(
				new MathFormula("matrix", "\\begin{matrix} a & b \\\\ c & d \\end{matrix}"),
				new MathFormula("pmatrix", "\\begin{pmatrix} a & b \\\\ c & d \\end{pmatrix}"),
				new MathFormula("bmatrix", "\\begin{bmatrix} a & b \\\\ c & d \\end{bmatrix}"),
				new MathFormula("Bmatrix", "\\begin{Bmatrix} a & b \\\\ c & d \\end{Bmatrix}"),
				new MathFormula("vmatrix", "\\begin{vmatrix} a & b \\\\ c & d \\end{vmatrix}"),
				new MathFormula("Vmatrix", "\\begin{Vmatrix} a & b \\\\ c & d \\end{Vmatrix}"),
				new MathFormula("smallmatrix", "\\left(\\begin{smallmatrix} a & b \\\\ c & d \\end{smallmatrix}\\right)"),
				new MathFormula("array", "\\begin{array}{lcr} left & center & right \\\\ 1 & 2 & 3 \\end{array}"),
				new MathFormula("cases", "f(x) = \\begin{cases} x & x > 0 \\\\ -x & x \\le 0 \\end{cases}")
		)));

		// ============================================================
		// 12. BNF: <frac>, <binom>, <sqrt> (结构 - 穷举)
		// ============================================================
		categories.add(new FormulaCategory("结构 (Structures)", Arrays.asList(
				// Frac
				new MathFormula("\\frac", "\\frac{a}{b}"),
				new MathFormula("\\dfrac", "\\dfrac{a}{b}"),
				new MathFormula("\\tfrac", "\\tfrac{a}{b}"),
				new MathFormula("\\cfrac", "\\cfrac{1}{1 + \\cfrac{1}{x}}"),
				// Binom
				new MathFormula("\\binom", "\\binom{n}{k}"),
				new MathFormula("\\dbinom", "\\dbinom{n}{k}"),
				new MathFormula("\\tbinom", "\\tbinom{n}{k}"),
				// Sqrt
				new MathFormula("\\sqrt", "\\sqrt{x}"),
				new MathFormula("\\sqrt[n]", "\\sqrt[3]{x+y}")
		)));

		// ============================================================
		// 13. BNF: <spacing> (空格 - 穷举)
		// ============================================================
		categories.add(new FormulaCategory("空格与间距 (Spacing)", Arrays.asList(
				new MathFormula("\\, (Thin)", "a \\, b"),
				new MathFormula("\\: (Medium)", "a \\: b"),
				new MathFormula("\\; (Thick)", "a \\; b"),
				new MathFormula("\\! (Neg)", "a \\! b"),
				new MathFormula("\\quad", "a \\quad b"),
				new MathFormula("\\qquad", "a \\qquad b"),
				new MathFormula("\\hspace", "a \\hspace{1cm} b"),
				new MathFormula("\\hphantom", "a \\hphantom{phantom} b"),
				new MathFormula("\\vphantom", "\\sqrt{x \\vphantom{y^2}}"),
				new MathFormula("\\phantom", "a \\phantom{b} c")
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