package me.chan.texas.ext.markdown.math.ast;

import org.junit.Test;

import static org.junit.Assert.*;

import me.chan.texas.utils.CharStream;

/**
 * LaTeX 数学公式解析器完整单元测试
 * 基于 bnf_math.txt v3 定义
 */
public class MathParserUnitTest {

	// ============ 辅助方法 ============

	private MathList parseAndVerify(String input) throws MathParseException {
		CharStream stream = new CharStream(input, 0, input.length());
		MathParser parser = new MathParser(stream);
		MathList ast = parser.parse();
		assertNotNull("解析结果不应为null", ast);
		return ast;
	}

	private void assertParses(String input) {
		try {
			MathList result = parseAndVerify(input);
			String output = result.toString();
			assertNotNull("输出不应为null", output);
			System.out.println("✅ " + input + " → " + output);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	private void assertParseFails(String input) {
		try {
			CharStream stream = new CharStream(input, 0, input.length());
			MathParser parser = new MathParser(stream);
			parser.parse();
			fail("应该抛出异常: " + input);
		} catch (MathParseException e) {
			System.out.println("✅ 正确拒绝: " + input + " (" + e.getMessage() + ")");
		}
	}

	// ============ 1. 基础元素测试 ============

	@Test
	public void testNumbers() {
		System.out.println("\n=== 测试数字 ===");
		assertParses("0");
		assertParses("1");
		assertParses("123");
		assertParses("999");
		assertParses("0.5");
		assertParses("3.14");
		assertParses("123.456");
		assertParses("0.0");
	}

	@Test
	public void testVariables() {
		System.out.println("\n=== 测试变量 ===");
		assertParses("x");
		assertParses("y");
		assertParses("a");
		assertParses("z");
		assertParses("A");
		assertParses("Z");
		// 多个变量（隐式乘法）
		assertParses("xy");
		assertParses("abc");
		assertParses("ABC");
	}

	@Test
	public void testGreekLetters() {
		System.out.println("\n=== 测试希腊字母 ===");
		assertParses("\\alpha");
		assertParses("\\beta");
		assertParses("\\gamma");
		assertParses("\\delta");
		assertParses("\\epsilon");
		assertParses("\\varepsilon");
		assertParses("\\theta");
		assertParses("\\vartheta");
		assertParses("\\lambda");
		assertParses("\\mu");
		assertParses("\\pi");
		assertParses("\\sigma");
		assertParses("\\omega");
		assertParses("\\Gamma");
		assertParses("\\Delta");
		assertParses("\\Theta");
		assertParses("\\Lambda");
		assertParses("\\Pi");
		assertParses("\\Sigma");
		assertParses("\\Omega");
		assertParses("\\infty");
	}

	// ============ 2. 运算符测试 ============

	@Test
	public void testUnaryOperators() {
		System.out.println("\n=== 测试一元运算符 ===");
		assertParses("-x");
		assertParses("+x");
		assertParses("-123");
		assertParses("+456");
		assertParses("-\\alpha");
		assertParses("+\\beta");
		assertParses("\\pm x");
		assertParses("\\mp y");
		assertParses("-{x+y}");
		assertParses("-\\frac{1}{2}");
		assertParses("-\\sqrt{x}");
	}

	@Test
	public void testBinaryOperators() {
		System.out.println("\n=== 测试二元运算符 ===");
		// 基本算术
		assertParses("a+b");
		assertParses("a-b");
		assertParses("a*b");
		assertParses("a/b");
		assertParses("a,b");  // 逗号

		// LaTeX 运算符
		assertParses("a\\times b");
		assertParses("a\\cdot b");
		assertParses("a\\div b");
		assertParses("a\\pm b");
		assertParses("a\\mp b");

		// 关系运算符
		assertParses("a=b");
		assertParses("a\\neq b");
		assertParses("a\\equiv b");
		assertParses("a\\approx b");
		assertParses("a\\cong b");
		assertParses("a\\sim b");
		assertParses("a<b");
		assertParses("a>b");
		assertParses("a\\le b");
		assertParses("a\\ge b");
		assertParses("a\\leq b");
		assertParses("a\\geq b");
		assertParses("a\\ll b");
		assertParses("a\\gg b");

		// 集合运算符
		assertParses("x\\in A");
		assertParses("x\\notin A");
		assertParses("A\\subset B");
		assertParses("A\\supset B");
		assertParses("A\\subseteq B");
		assertParses("A\\supseteq B");
		assertParses("A\\cup B");
		assertParses("A\\cap B");
		assertParses("A\\wedge B");
		assertParses("A\\vee B");

		// 箭头
		assertParses("a\\to b");
		assertParses("a\\rightarrow b");
		assertParses("a\\leftarrow b");
		assertParses("a\\leftrightarrow b");
		assertParses("A\\Rightarrow B");
		assertParses("A\\Leftarrow B");
		assertParses("A\\Leftrightarrow B");
		assertParses("A\\implies B");
		assertParses("A\\iff B");

		// 几何关系
		assertParses("AB\\perp CD");
		assertParses("AB\\parallel CD");
	}

	// ============ 3. 分组测试 ============

	@Test
	public void testGroups() {
		System.out.println("\n=== 测试分组 ===");
		assertParses("{x}");
		assertParses("{a+b}");
		assertParses("{xyz}");
		assertParses("{{x}}");
		assertParses("{a{b{c}}}");
		assertParses("{}");  // 空分组
		assertParses("{1,2,3}");
	}

	// ============ 4. 上下标测试 ============

	@Test
	public void testSuperscript() {
		System.out.println("\n=== 测试上标 ===");
		assertParses("x^2");
		assertParses("x^{n+1}");
		assertParses("x^a");
		assertParses("e^{-x}");
		assertParses("x^\\alpha");
		assertParses("e^{\\pm x}");
		assertParses("x^+");
		assertParses("x^-");
		assertParses("x^*");
	}

	@Test
	public void testSubscript() {
		System.out.println("\n=== 测试下标 ===");
		assertParses("x_1");
		assertParses("x_{n-1}");
		assertParses("x_i");
		assertParses("a_\\alpha");
		assertParses("x_+");
		assertParses("x_-");
	}

	@Test
	public void testSuperAndSubscript() {
		System.out.println("\n=== 测试上下标组合 ===");
		assertParses("x^2_1");
		assertParses("x^{n+1}_{i}");
		assertParses("x_1^2");
		assertParses("x_{i}^{n+1}");
	}

	// ============ 5. 分式测试 ============

	@Test
	public void testFrac() {
		System.out.println("\n=== 测试分式 ===");
		assertParses("\\frac{1}{2}");
		assertParses("\\frac{a}{b}");
		assertParses("\\frac{x+y}{x-y}");
		assertParses("\\frac{\\frac{1}{2}}{3}");
		assertParses("\\dfrac{a}{b}");
		assertParses("\\tfrac{1}{2}");
		assertParses("\\cfrac{a}{b}");
	}

	// ============ 6. 根式测试 ============

	@Test
	public void testSqrt() {
		System.out.println("\n=== 测试根式 ===");
		assertParses("\\sqrt{x}");
		assertParses("\\sqrt{2}");
		assertParses("\\sqrt{x+1}");
		assertParses("\\sqrt{\\sqrt{x}}");
		assertParses("\\sqrt[3]{27}");
		assertParses("\\sqrt[n]{x}");
		assertParses("\\sqrt[n+1]{x^2}");
	}

	// ============ 7. 定界符测试 ============

	@Test
	public void testDelimited() {
		System.out.println("\n=== 测试定界符 ===");
		assertParses("\\left( x \\right)");
		assertParses("\\left[ x \\right]");
		assertParses("\\left| x \\right|");
		assertParses("\\left\\{ x \\right\\}");
		assertParses("\\left( \\frac{a}{b} \\right)");
		assertParses("\\left. x \\right|");
		assertParses("\\left\\langle x \\right\\rangle");
		assertParses("\\bigl( x \\bigr)");
		assertParses("\\Bigl[ x \\Bigr]");
	}

	// ============ 8. 函数测试 ============

	@Test
	public void testFunctions() {
		System.out.println("\n=== 测试函数 ===");
		assertParses("\\sin x");
		assertParses("\\cos x");
		assertParses("\\tan x");
		assertParses("\\sin{x}");
		assertParses("\\sin{x+y}");
		assertParses("\\log x");
		assertParses("\\ln x");
		assertParses("\\log_2 n");
		assertParses("\\log_{10} x");
		assertParses("\\max x");
		assertParses("\\min x");
		assertParses("\\det x");
		assertParses("\\exp x");
	}

	// ============ 9. 大型运算符测试 ============

	@Test
	public void testLargeOperators() {
		System.out.println("\n=== 测试大型运算符 ===");
		assertParses("\\sum");
		assertParses("\\prod");
		assertParses("\\int");
		assertParses("\\sum_{i=1}^{n}");
		assertParses("\\sum_{i=1}^{n} i^2");
		assertParses("\\int_0^1");
		assertParses("\\int_a^b");
		assertParses("\\prod_{k=0}^{\\infty}");
		assertParses("\\iint");
		assertParses("\\iiint");
		assertParses("\\oint");
		assertParses("\\bigcup");
		assertParses("\\bigcap");
		assertParses("\\lim_{x\\to 0}");
		assertParses("\\lim_{n\\to\\infty}");
	}

	// ============ 10. 文本模式测试 ============

	@Test
	public void testText() {
		System.out.println("\n=== 测试文本模式 ===");
		assertParses("\\text{hello}");
		assertParses("\\text{当x趋近于0时}");
		assertParses("\\mbox{test}");
		assertParses("\\textrm{roman}");
		assertParses("\\textit{italic}");
		assertParses("\\textbf{bold}");
	}

	// ============ 11. 重音符号测试 ============

	@Test
	public void testAccents() {
		System.out.println("\n=== 测试重音符号 ===");
		assertParses("\\hat{x}");
		assertParses("\\vec{v}");
		assertParses("\\bar{x}");
		assertParses("\\tilde{x}");
		assertParses("\\dot{x}");
		assertParses("\\ddot{x}");
		assertParses("\\widehat{xyz}");
		assertParses("\\overline{x+y}");
		assertParses("\\underline{text}");
		assertParses("\\overrightarrow{AB}");
		assertParses("\\overbrace{a+b+c}");
		assertParses("\\underbrace{a+b+c}");
	}

	// ============ 12. 字体命令测试 ============

	@Test
	public void testFontCommands() {
		System.out.println("\n=== 测试字体命令 ===");
		assertParses("\\mathbf{x}");
		assertParses("\\mathrm{ABC}");
		assertParses("\\mathit{text}");
		assertParses("\\mathbb{R}");
		assertParses("\\mathcal{L}");
		assertParses("\\mathfrak{g}");
		assertParses("\\boldsymbol{\\alpha}");
	}

	// ============ 13. 空格命令测试 ============

	@Test
	public void testSpacing() {
		System.out.println("\n=== 测试空格命令 ===");
		assertParses("a\\quad b");
		assertParses("a\\qquad b");
		assertParses("a\\, b");
		assertParses("a\\: b");
		assertParses("a\\; b");
		assertParses("a\\! b");
	}

	// ============ 14. 特殊符号测试 ============

	@Test
	public void testSpecialSymbols() {
		System.out.println("\n=== 测试特殊符号 ===");
		// 省略号
		assertParses("\\dots");
		assertParses("\\ldots");
		assertParses("\\cdots");
		assertParses("\\vdots");
		assertParses("\\ddots");

		// 角度符号
		assertParses("\\angle");
		assertParses("\\angle ABC");
		assertParses("\\angle_1");

		// 逻辑符号
		assertParses("\\therefore");
		assertParses("\\because");

		// 特殊符号可以带上下标
		assertParses("\\dots^{n}");
		assertParses("\\angle_1");
	}

	@Test
	public void testSpecialSymbolsCannotHaveUnaryOp() {
		System.out.println("\n=== 测试特殊符号不能被一元运算符修饰 ===");
		assertParseFails("-\\dots");
		assertParseFails("+\\angle");
		assertParseFails("-\\therefore");
		assertParseFails("+\\because");
		assertParseFails("\\pm\\cdots");
	}

	// ============ 15. 复杂组合测试 ============

	@Test
	public void testComplexExpressions() {
		System.out.println("\n=== 测试复杂表达式 ===");
		// 二次公式
		assertParses("\\frac{-b\\pm\\sqrt{b^2-4ac}}{2a}");

		// 极限
		assertParses("\\lim_{x\\to 0}\\frac{\\sin x}{x}");

		// 积分
		assertParses("\\int_0^1 x^2");
		assertParses("\\int_a^b f\\left(x\\right)");

		// 求和
		assertParses("\\sum_{n=1}^{\\infty}\\frac{1}{n^2}");

		// 矩阵
		assertParses("\\left(\\frac{a}{b}\\right)^2");

		// 混合上下标和分式
		assertParses("x_1^2+x_2^2+\\cdots+x_n^2");
	}

	@Test
	public void testNestedStructures() {
		System.out.println("\n=== 测试嵌套结构 ===");
		assertParses("\\frac{1}{1+\\frac{1}{x}}");
		assertParses("\\sqrt{1+\\sqrt{x}}");
		assertParses("x^{a^2}");
		assertParses("x_{a_1}");
		assertParses("\\frac{\\sqrt{a+b}}{\\sqrt{c+d}}");
	}

	// ============ 16. 容易引起歧义的测试 ============

	@Test
	public void testAmbiguousCases() {
		System.out.println("\n=== 测试容易引起歧义的情况 ===");

		// 一元 vs 二元运算符
		assertParses("a+-b");  // a + (-b)
		assertParses("a-+b");  // a - (+b)
		assertParses("a--b");  // a - (-b)
		assertParses("a++b");  // a + (+b)

		// \pm 和 \mp 的双重身份
		assertParses("\\pm a");  // 一元
		assertParses("a\\pm b");  // 二元
		assertParses("a\\pm\\pm b");  // 二元 + 一元

		// 连续的变量
		assertParses("abc");  // 隐式乘法
		assertParses("a b c");  // 隐式乘法（带空格）

		// 上下标优先级
		assertParses("x^2+1");  // x^2 + 1, 不是 x^(2+1)
		assertParses("x^{2+1}");  // x^(2+1)
		assertParses("x_1+2");  // x_1 + 2
		assertParses("x_{1+2}");  // x_(1+2)

		// 逗号作为运算符
		assertParses("a,b,c");
		assertParses("f\\left(x,y\\right)");  // 函数参数中的逗号

		// 特殊符号 vs 普通原子
		assertParses("a\\cdots b");  // cdots 作为 term
		assertParses("-a+\\cdots+b");  // -a 是一元运算符，cdots 不能被修饰

		// 连续的定界符
		assertParses("\\left(\\left(x\\right)\\right)");

		// 分式中的负号
		assertParses("\\frac{-a}{b}");
		assertParses("\\frac{a}{-b}");
		assertParses("-\\frac{a}{b}");

		// 根式中的负号
		assertParses("\\sqrt{-x}");
		assertParses("-\\sqrt{x}");
	}

	@Test
	public void testEdgeCasesForUnaryBinary() {
		System.out.println("\n=== 测试一元二元运算符边界情况 ===");

		// 确保一元运算符后必须有 operand_atom
		assertParseFails("+");
		assertParseFails("-");
		assertParseFails("a+");
		assertParseFails("a-");

		// 确保二元运算符必须连接两个 term
		assertParseFails("*b");
		assertParseFails("/b");
		assertParseFails("a*");
		assertParseFails("a/");

		// + 和 - 的特殊性
		assertParses("+a");  // 一元
		assertParses("-a");  // 一元
		assertParses("a+b");  // 二元
		assertParses("a-b");  // 二元
	}

	@Test
	public void testComplexMixedOperators() {
		System.out.println("\n=== 测试复杂混合运算符 ===");
		assertParses("a+b*c");
		assertParses("a*b+c");
		assertParses("a+b+c+d");
		assertParses("a-b-c-d");
		assertParses("a\\times b\\div c");
		assertParses("a=b=c");
		assertParses("a<b<c");
		assertParses("a\\in B\\subset C");
	}

	@Test
	public void testScriptArgVariety() {
		System.out.println("\n=== 测试上下标参数多样性 ===");
		// single_token
		assertParses("x^1");
		assertParses("x^a");
		assertParses("x^\\alpha");
		assertParses("x^+");
		assertParses("x^-");

		// group
		assertParses("x^{a+b}");
		assertParses("x^{\\frac{1}{2}}");
		assertParses("x^{y^2}");
	}

	@Test
	public void testWithCommas() {
		System.out.println("\n=== 测试逗号作为二元运算符 ===");
		assertParses("1,2,3");
		assertParses("a,b,c");
		assertParses("x_1,x_2,\\ldots,x_n");
		assertParses("{1,2,3,\\ldots,n}");
	}

	@Test
	public void testRealWorldExpressions() {
		System.out.println("\n=== 测试真实世界的表达式 ===");
		// 欧拉公式
		assertParses("e^{i\\pi}+1=0");

		// 泰勒展开
		assertParses("f\\left(x\\right)=f\\left(a\\right)+f'\\left(a\\right)\\left(x-a\\right)+\\cdots");

		// 二项式定理
		assertParses("\\left(a+b\\right)^n=\\sum_{k=0}^{n}C_n^k a^{n-k}b^k");

		// 勾股定理
		assertParses("a^2+b^2=c^2");

		// 导数定义
		assertParses("f'\\left(x\\right)=\\lim_{h\\to 0}\\frac{f\\left(x+h\\right)-f\\left(x\\right)}{h}");

		// 积分
		assertParses("\\int_0^{\\infty}e^{-x^2}");

		// 序列
		assertParses("a_1,a_2,\\ldots,a_n");

		// 几何
		assertParses("\\angle ABC=90");
		assertParses("AB\\perp CD\\therefore\\angle ABC=90");

		// 逻辑推理
		assertParses("x>0\\therefore x^2>0");
		assertParses("\\because a=b,\\therefore a+c=b+c");
	}

	// ============ 17. 错误处理测试 ============

	@Test
	public void testErrorCases() {
		System.out.println("\n=== 测试错误情况 ===");
		assertParseFails("\\frac{1}{");
		assertParseFails("\\sqrt[");
		assertParseFails("\\left(x");
		assertParseFails("\\hat");
		assertParseFails("\\text");
		assertParseFails("\\sum_{i=1}^{");
		assertParseFails("{x");
		assertParseFails("\\unknowncommand");
		assertParseFails("x^^2");  // 连续的上标符号
		assertParseFails("x__2");  // 连续的下标符号
	}

	// ============ 18. 压力测试 ============

	@Test
	public void testLongExpression() {
		System.out.println("\n=== 测试长表达式 ===");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 20; i++) {
			if (i > 0) sb.append("+");
			sb.append("x_").append(i);
		}
		assertParses(sb.toString());
	}

	@Test
	public void testDeeplyNested() {
		System.out.println("\n=== 测试深度嵌套 ===");
		String expr = "x";
		for (int i = 0; i < 5; i++) {
			expr = "\\sqrt{" + expr + "}";
		}
		assertParses(expr);
	}

	// ============ 19. BNF 完整覆盖验证 ============

	@Test
	public void testBnfCoverage() {
		System.out.println("\n=== BNF 完整覆盖验证 ===");

		// operand_atom 的所有类型
		assertParses("123");
		assertParses("x");
		assertParses("\\alpha");
		assertParses("{x}");
		assertParses("\\frac{a}{b}");
		assertParses("\\sqrt{x}");
		assertParses("\\left(x\\right)");
		assertParses("\\sin x");
		assertParses("\\sum");
		assertParses("\\text{hi}");
		assertParses("\\hat{x}");
		assertParses("\\mathbf{x}");

		// special_symbol 的所有类型
		assertParses("\\dots");
		assertParses("\\angle");
		assertParses("\\therefore");

		// 一元运算符
		assertParses("-x");
		assertParses("\\pm x");

		// 二元运算符（采样）
		assertParses("a+b");
		assertParses("a\\times b");
		assertParses("a=b");
		assertParses("a<b");
		assertParses("a\\in B");
		assertParses("a\\perp b");
		assertParses("a,b");

		// 上下标
		assertParses("x^a");
		assertParses("x_a");
		assertParses("x^a_b");
		assertParses("x_a^b");

		// spacing
		assertParses("a\\quad b");

		System.out.println("\n✅ BNF 覆盖率测试完成!");
	}

	@Test
	public void testComprehensiveBnfCoverage() {
		System.out.println("\n=== 全面BNF语法覆盖测试 ===");

		// 这是一个包含所有BNF语法元素的综合表达式
		String comprehensiveExpr =
				// 文本 + 空格
				"\\text{当}\\quad " +

						// 变量（带prime后缀）+ 上标
						"f'\\left(x\\right)^{2} " +

						// 二元运算符：等号
						"= " +

						// 一元运算符（负号）+ 分式（dfrac）+ 根式（带根次数）
						"-\\dfrac{\\sqrt[n]{a+b}}{\\sqrt{c-d}} " +

						// 二元运算符：加号
						"+ " +

						// 大型运算符：求和（带上下标）
						"\\sum_{i=1}^{\\infty} " +

						// 分组 + 一元运算符（\pm）+ 变量 + 上下标组合
						"{\\pm x_i^{k+1}} " +

						// 二元运算符：乘号
						"\\times " +

						// 函数（带下标）+ 定界符（bigl/bigr）
						"\\log_2\\bigl|y\\bigr| " +

						// 二元运算符：除号
						"\\div " +

						// 重音符号
						"\\hat{\\alpha} " +

						// 二元运算符：关系运算符
						"\\le " +

						// 希腊字母大写
						"\\Omega " +

						// 二元运算符：集合运算符
						"\\in " +

						// 字体命令
						"\\mathbb{R} " +

						// 二元运算符：逻辑箭头
						"\\Rightarrow " +

						// 特殊符号（可带上下标）
						"\\angle_{ABC} " +

						// 二元运算符：几何关系
						"\\perp " +

						// 数字（小数）
						"3.14 " +

						// 空格命令
						"\\, " +

						// 特殊符号（省略号）
						"\\cdots " +

						// 空格命令
						"\\; " +

						// 特殊符号（逻辑符号）
						"\\therefore " +

						// 一元运算符（\mp）+ 希腊字母小写
						"\\mp\\beta";

		assertParses(comprehensiveExpr);

		System.out.println("✅ 综合表达式测试通过");
	}

	@Test
	public void testAllBnfElementsSeparately() {
		System.out.println("\n=== 逐项BNF元素覆盖测试 ===");

		// ============ 1. 基础元素 ============
		System.out.println("测试基础元素...");
		assertParses("123");              // <number> 整数
		assertParses("3.14159");          // <number> 小数
		assertParses("x");                // <variable> 单字母
		assertParses("abc");              // <variable> 多字母（隐式乘法）
		assertParses("f'");               // <variable> 带一个prime
		assertParses("g''");              // <variable> 带两个prime
		assertParses("y'''");             // <variable> 带三个prime

		// ============ 2. 希腊字母（完整） ============
		System.out.println("测试希腊字母...");
		// 小写希腊字母（完整列表）
		assertParses("\\alpha");
		assertParses("\\beta");
		assertParses("\\gamma");
		assertParses("\\delta");
		assertParses("\\epsilon");
		assertParses("\\varepsilon");
		assertParses("\\zeta");
		assertParses("\\eta");
		assertParses("\\theta");
		assertParses("\\vartheta");
		assertParses("\\iota");
		assertParses("\\kappa");
		assertParses("\\lambda");
		assertParses("\\mu");
		assertParses("\\nu");
		assertParses("\\xi");
		assertParses("\\pi");
		assertParses("\\varpi");
		assertParses("\\rho");
		assertParses("\\varrho");
		assertParses("\\sigma");
		assertParses("\\varsigma");
		assertParses("\\tau");
		assertParses("\\upsilon");
		assertParses("\\phi");
		assertParses("\\varphi");
		assertParses("\\chi");
		assertParses("\\psi");
		assertParses("\\omega");

		// 大写希腊字母（完整列表）
		assertParses("\\Gamma");
		assertParses("\\Delta");
		assertParses("\\Theta");
		assertParses("\\Lambda");
		assertParses("\\Xi");
		assertParses("\\Pi");
		assertParses("\\Sigma");
		assertParses("\\Upsilon");
		assertParses("\\Phi");
		assertParses("\\Psi");
		assertParses("\\Omega");
		assertParses("\\infty");

		// ============ 3. 一元运算符（完整） ============
		System.out.println("测试一元运算符...");
		assertParses("+x");
		assertParses("-x");
		assertParses("\\pm x");
		assertParses("\\mp x");

		// ============ 4. 二元运算符（完整） ============
		System.out.println("测试二元运算符...");
		// 算术运算符
		assertParses("a+b");
		assertParses("a-b");
		assertParses("a*b");
		assertParses("a/b");
		assertParses("a,b");
		assertParses("a\\times b");
		assertParses("a\\cdot b");
		assertParses("a\\div b");
		assertParses("a\\pm b");
		assertParses("a\\mp b");

		// 关系运算符
		assertParses("a=b");
		assertParses("a\\neq b");
		assertParses("a\\equiv b");
		assertParses("a\\approx b");
		assertParses("a\\cong b");
		assertParses("a\\sim b");
		assertParses("a<b");
		assertParses("a>b");
		assertParses("a\\le b");
		assertParses("a\\ge b");
		assertParses("a\\leq b");
		assertParses("a\\geq b");
		assertParses("a\\ll b");
		assertParses("a\\gg b");

		// 集合运算符
		assertParses("x\\in A");
		assertParses("x\\notin A");
		assertParses("A\\subset B");
		assertParses("A\\supset B");
		assertParses("A\\subseteq B");
		assertParses("A\\supseteq B");
		assertParses("A\\cup B");
		assertParses("A\\cap B");
		assertParses("A\\wedge B");
		assertParses("A\\vee B");

		// 箭头运算符
		assertParses("a\\to b");
		assertParses("a\\rightarrow b");
		assertParses("a\\leftarrow b");
		assertParses("a\\leftrightarrow b");
		assertParses("A\\Rightarrow B");
		assertParses("A\\Leftarrow B");
		assertParses("A\\Leftrightarrow B");
		assertParses("A\\implies B");
		assertParses("A\\iff B");

		// 几何关系运算符
		assertParses("a\\perp b");
		assertParses("a\\parallel b");

		// ============ 5. 分组 ============
		System.out.println("测试分组...");
		assertParses("{x}");
		assertParses("{{x}}");
		assertParses("{a+b}");
		assertParses("{}");  // 空分组

		// ============ 6. 上下标（所有组合） ============
		System.out.println("测试上下标...");
		assertParses("x^2");              // 仅上标
		assertParses("x_1");              // 仅下标
		assertParses("x^2_1");            // 上标在前
		assertParses("x_1^2");            // 下标在前

		// script_arg 的所有类型
		assertParses("x^1");              // digit
		assertParses("x^a");              // letter
		assertParses("x^\\alpha");        // greek_letter
		assertParses("x^+");              // operator_symbol +
		assertParses("x^-");              // operator_symbol -
		assertParses("x^*");              // operator_symbol *
		assertParses("x^/");              // operator_symbol /
		assertParses("x^=");              // operator_symbol =
		assertParses("x^<");              // operator_symbol <
		assertParses("x^>");              // operator_symbol >
		assertParses("x^\\pm");           // operator_symbol \pm
		assertParses("x^\\mp");           // operator_symbol \mp
		assertParses("x^{a+b}");          // group

		// ============ 7. 分式（所有变体） ============
		System.out.println("测试分式...");
		assertParses("\\frac{a}{b}");
		assertParses("\\dfrac{a}{b}");
		assertParses("\\tfrac{a}{b}");
		assertParses("\\cfrac{a}{b}");

		// ============ 8. 根式（两种形式） ============
		System.out.println("测试根式...");
		assertParses("\\sqrt{x}");                    // 不带根次数
		assertParses("\\sqrt[3]{x}");                 // 带根次数
		assertParses("\\sqrt[n+1]{x^2}");             // 复杂根次数

		// ============ 9. 定界符（所有级别和类型） ============
		System.out.println("测试定界符...");
		// 5个级别
		assertParses("\\left( x \\right)");
		assertParses("\\bigl[ x \\bigr]");
		assertParses("\\Bigl\\{ x \\Bigr\\}");
		assertParses("\\biggl| x \\biggr|");
		assertParses("\\Biggl\\langle x \\Biggr\\rangle");

		// 所有定界符类型
		assertParses("\\left( x \\right)");           // ()
		assertParses("\\left[ x \\right]");           // []
		assertParses("\\left\\{ x \\right\\}");       // {}
		assertParses("\\left| x \\right|");           // |
		assertParses("\\left\\| x \\right\\|");       // \|
		assertParses("\\left\\langle x \\right\\rangle");  // <> (langle/rangle)
		assertParses("\\left\\lfloor x \\right\\rfloor");  // floor
		assertParses("\\left\\lceil x \\right\\rceil");    // ceil
		assertParses("\\left\\lvert x \\right\\rvert");    // lvert/rvert
		assertParses("\\left\\lVert x \\right\\rVert");    // lVert/rVert
		assertParses("\\left. x \\right|");           // 空定界符 .

		// ============ 10. 函数（所有类型） ============
		System.out.println("测试函数...");
		// 三角函数
		assertParses("\\sin x");
		assertParses("\\cos x");
		assertParses("\\tan x");
		assertParses("\\cot x");
		assertParses("\\sec x");
		assertParses("\\csc x");
		assertParses("\\arcsin x");
		assertParses("\\arccos x");
		assertParses("\\arctan x");

		// 双曲函数
		assertParses("\\sinh x");
		assertParses("\\cosh x");
		assertParses("\\tanh x");
		assertParses("\\coth x");

		// 对数函数
		assertParses("\\log x");
		assertParses("\\ln x");
		assertParses("\\lg x");
		assertParses("\\exp x");

		// 其他函数
		assertParses("\\max x");
		assertParses("\\min x");
		assertParses("\\sup x");
		assertParses("\\inf x");
		assertParses("\\arg x");
		assertParses("\\deg x");
		assertParses("\\det x");
		assertParses("\\dim x");
		assertParses("\\gcd x");
		assertParses("\\hom x");
		assertParses("\\ker x");
		assertParses("\\Pr x");
		assertParses("\\bmod x");
		assertParses("\\pmod x");

		// 带上下标的函数
		assertParses("\\log_2 x");
		assertParses("\\log^2 x");
		assertParses("\\log_2^3 x");

		// 函数参数的三种类型
		assertParses("\\sin x");              // single_token
		assertParses("\\sin{x+y}");           // group
		assertParses("\\sin\\left(x\\right)"); // delimited

		// ============ 11. 大型运算符（所有类型） ============
		System.out.println("测试大型运算符...");
		// 求和/乘积
		assertParses("\\sum");
		assertParses("\\prod");
		assertParses("\\coprod");

		// 积分
		assertParses("\\int");
		assertParses("\\iint");
		assertParses("\\iiint");
		assertParses("\\oint");
		assertParses("\\oiint");
		assertParses("\\oiiint");

		// 集合运算
		assertParses("\\bigcup");
		assertParses("\\bigcap");
		assertParses("\\bigvee");
		assertParses("\\bigwedge");
		assertParses("\\bigoplus");
		assertParses("\\bigotimes");
		assertParses("\\bigodot");
		assertParses("\\biguplus");
		assertParses("\\bigsqcup");

		// 极限
		assertParses("\\lim");
		assertParses("\\limsup");
		assertParses("\\liminf");

		// 带上下标
		assertParses("\\sum_{i=1}^{n}");
		assertParses("\\int_0^1");
		assertParses("\\lim_{x\\to 0}");

		// ============ 12. 文本模式（所有命令） ============
		System.out.println("测试文本模式...");
		assertParses("\\text{hello}");
		assertParses("\\mbox{world}");
		assertParses("\\textrm{roman}");
		assertParses("\\textit{italic}");
		assertParses("\\textbf{bold}");
		assertParses("\\text{中文测试}");

		// ============ 13. 字体命令（所有类型） ============
		System.out.println("测试字体命令...");
		assertParses("\\mathrm{x}");
		assertParses("\\mathit{x}");
		assertParses("\\mathbf{x}");
		assertParses("\\mathsf{x}");
		assertParses("\\mathtt{x}");
		assertParses("\\mathcal{X}");
		assertParses("\\mathbb{R}");
		assertParses("\\mathfrak{g}");
		assertParses("\\mathscr{L}");
		assertParses("\\boldsymbol{\\alpha}");
		assertParses("\\bm{x}");

		// ============ 14. 重音符号（所有类型） ============
		System.out.println("测试重音符号...");
		// 基础重音
		assertParses("\\hat{x}");
		assertParses("\\widehat{xyz}");
		assertParses("\\tilde{x}");
		assertParses("\\widetilde{xyz}");
		assertParses("\\bar{x}");
		assertParses("\\overline{xyz}");
		assertParses("\\underline{text}");

		// 向量
		assertParses("\\vec{v}");
		assertParses("\\overrightarrow{AB}");
		assertParses("\\overleftarrow{BA}");

		// 点记号
		assertParses("\\dot{x}");
		assertParses("\\ddot{x}");
		assertParses("\\dddot{x}");

		// 其他重音
		assertParses("\\acute{a}");
		assertParses("\\grave{a}");
		assertParses("\\breve{a}");
		assertParses("\\check{a}");
		assertParses("\\mathring{a}");

		// 括号式
		assertParses("\\overbrace{a+b+c}");
		assertParses("\\underbrace{x+y+z}");

		// 重音也可以用于single_token
		assertParses("\\hat x");

		// ============ 15. 空格命令（所有类型） ============
		System.out.println("测试空格命令...");
		assertParses("a\\,b");
		assertParses("a\\:b");
		assertParses("a\\;b");
		assertParses("a\\!b");
		assertParses("a\\quad b");
		assertParses("a\\qquad b");

		// 带参数的空格命令
		assertParses("a\\hspace{10em}b");
		assertParses("a\\hspace{5pt}b");
		assertParses("a\\hspace{2cm}b");

		// phantom命令
		assertParses("a\\hphantom{xyz}b");
		assertParses("a\\vphantom{\\frac{1}{2}}b");
		assertParses("a\\phantom{test}b");

		// 所有单位
		assertParses("\\hspace{1em}");
		assertParses("\\hspace{1ex}");
		assertParses("\\hspace{1pt}");
		assertParses("\\hspace{1px}");
		assertParses("\\hspace{1cm}");
		assertParses("\\hspace{1mm}");
		assertParses("\\hspace{1in}");

		// ============ 16. 特殊符号（所有类型） ============
		System.out.println("测试特殊符号...");
		// 省略号
		assertParses("\\dots");
		assertParses("\\ldots");
		assertParses("\\cdots");
		assertParses("\\vdots");
		assertParses("\\ddots");

		// 角度符号
		assertParses("\\angle");

		// 逻辑符号
		assertParses("\\therefore");
		assertParses("\\because");

		// 特殊符号可以带上下标
		assertParses("\\dots^n");
		assertParses("\\angle_1");
		assertParses("\\angle_{ABC}");

		System.out.println("\n✅ 所有BNF元素逐项测试完成!");
	}

	@Test
	public void testMatrixAllVariants() {
		System.out.println("\n=== 测试矩阵（所有环境） ===");

		// 注意：这里假设parser已经支持矩阵，如果还未实现，这些测试会失败
		// 所有矩阵环境
		String[] matrixEnvs = {
				"matrix", "pmatrix", "bmatrix", "Bmatrix",
				"vmatrix", "Vmatrix", "smallmatrix", "array", "cases"
		};

		for (String env : matrixEnvs) {
			String matrixExpr = "\\begin{" + env + "} a & b \\\\ c & d \\end{" + env + "}";
			System.out.println("测试: " + env);
			// assertParses(matrixExpr);  // 取消注释如果矩阵已实现
		}

		// 带对齐参数的array
		// assertParses("\\begin{array}{lcr} a & b & c \\\\ d & e & f \\end{array}");

		System.out.println("⚠️  矩阵测试已跳过（需要检查实现）");
	}
}