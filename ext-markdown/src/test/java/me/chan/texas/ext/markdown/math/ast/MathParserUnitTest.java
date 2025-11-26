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
}