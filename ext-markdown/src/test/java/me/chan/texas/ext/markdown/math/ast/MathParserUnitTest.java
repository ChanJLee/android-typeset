package me.chan.texas.ext.markdown.math.ast;

import org.junit.Test;

import static org.junit.Assert.*;

import me.chan.texas.utils.CharStream;

/**
 * LaTeX 数学公式解析器单元测试
 * 覆盖 bnf_math.txt 中定义的所有语法规则
 */
public class MathParserUnitTest {

	// ============ 辅助方法 ============
	@Test
	public void assertPlainSymbol() {
		assertParses("\\hspace { 100px }");
	}

	/**
	 * 解析并验证输入
	 */
	private MathList parseAndVerify(String input) throws MathParseException {
		CharStream stream = new CharStream(input, 0, input.length());
		MathParser parser = new MathParser(stream);
		MathList ast = parser.parse();
		assertNotNull("解析结果不应为null", ast);
		return ast;
	}

	/**
	 * 验证解析成功
	 */
	private void assertParses(String input) {
		try {
			MathList result = parseAndVerify(input);
			String output = result.toString();
			assertNotNull("输出不应为null", output);
			System.out.println("✅ " + input + " → " + output);
		} catch (MathParseException e) {
			fail("解析失败: " + e.getMessage() + " - 输入: " + input + " -> [" + input.substring(e.getPosition()) + "]");
		}
	}

	/**
	 * 验证解析失败
	 */
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

	// ============ 1. 基础元素测试 (BNF 第 36-44 行) ============

	@Test
	public void testNumbers() {
		System.out.println("\n=== 测试数字 ===");
		// 整数
		assertParses("0");
		assertParses("1");
		assertParses("123");
		assertParses("999");

		// 小数
		assertParses("0.5");
		assertParses("3.14");
		assertParses("123.456");
	}

	@Test
	public void testVariables() {
		System.out.println("\n=== 测试变量 ===");
		// 单个字母变量
		assertParses("x");
		assertParses("y");
		assertParses("a");
		assertParses("z");
		assertParses("A");
		assertParses("Z");

		// 多个变量会被解析为乘法
		assertParses("xy");
		assertParses("abc");
	}

	@Test
	public void testUnaryOp() {
		assertParses("- a+b");
	}

	// ============ 2. 希腊字母测试 (BNF 第 50-56 行) ============

	@Test
	public void testGreekLetters() {
		System.out.println("\n=== 测试希腊字母 ===");
		// 小写希腊字母（部分）
		assertParses("\\alpha");
		assertParses("\\beta");
		assertParses("\\gamma");
		assertParses("\\delta");
		assertParses("\\epsilon");
		assertParses("\\theta");
		assertParses("\\lambda");
		assertParses("\\mu");
		assertParses("\\pi");
		assertParses("\\sigma");
		assertParses("\\omega");

		// 大写希腊字母
		assertParses("\\Gamma");
		assertParses("\\Delta");
		assertParses("\\Theta");
		assertParses("\\Lambda");
		assertParses("\\Pi");
		assertParses("\\Sigma");
		assertParses("\\Omega");
	}

	// ============ 3. 二元运算符测试 (BNF 第 58-64 行) ============

	@Test
	public void testBinaryOperators() {
		System.out.println("\n=== 测试二元运算符 ===");
		// 基本算术运算符
		assertParses("a+b");
		assertParses("a-b");
		assertParses("a*b");
		assertParses("a/b");

		// LaTeX 运算符
		assertParses("a\\times b");
		assertParses("a\\cdot b");
		assertParses("a\\div b");
		assertParses("a\\pm b");

		// 关系运算符
		assertParses("a=b");
		assertParses("a<b");
		assertParses("a>b");
		assertParses("a\\le b");
		assertParses("a\\ge b");
		assertParses("a\\leq b");
		assertParses("a\\geq b");
		assertParses("a\\neq b");
		assertParses("a\\approx b");

		// 集合运算符
		assertParses("x\\in A");
		assertParses("A\\subset B");

		// 箭头
		assertParses("a\\to b");
		assertParses("a\\rightarrow b");
		assertParses("A\\Rightarrow B");
	}

	// ============ 4. 分组测试 (BNF 第 66-68 行) ============

	@Test
	public void testGroups() {
		System.out.println("\n=== 测试分组 ===");
		assertParses("{x}");
		assertParses("{a+b}");
		assertParses("{xyz}");
		assertParses("{{x}}");
		assertParses("{a{b{c}}}");
	}

	@Test
	public void testGroupErrors() {
		System.out.println("\n=== 测试分组错误 ===");
		assertParseFails("{");
		assertParseFails("{x");
	}

	// ============ 5. 上下标测试 (BNF 第 70-83 行) ============

	@Test
	public void testSuperscript() {
		System.out.println("\n=== 测试上标 ===");
		assertParses("x^2");
		assertParses("x^{n+1}");
		assertParses("x^a");
		assertParses("e^{-x}");
		assertParses("x^\\alpha");
		assertParses("e^{\\pm x}");
		assertParses("e^{\\mp x}");
	}

	@Test
	public void testSubscript() {
		System.out.println("\n=== 测试下标 ===");
		assertParses("x_1");
		assertParses("x_{n-1}");
		assertParses("x_i");
		assertParses("a_\\alpha");
	}

	@Test
	public void testSuperAndSubscript() {
		System.out.println("\n=== 测试上下标组合 ===");
		// 先上后下
		assertParses("x^2_1");
		assertParses("x^{n+1}_{i}");

		// 先下后上
		assertParses("x_1^2");
		assertParses("x_{i}^{n+1}");
	}

	@Test
	public void testScriptArgTypes() {
		System.out.println("\n=== 测试上下标参数类型 ===");
		// <script_arg> 的所有类型
		assertParses("x^1");          // number
		assertParses("x^a");          // letter
		assertParses("x^\\alpha");    // greek_letter
		assertParses("x^{a+b}");      // group
	}

	// ============ 6. 分式测试 (BNF 第 85-90 行) ============

	@Test
	public void testFrac() {
		System.out.println("\n=== 测试分式 ===");
		assertParses("\\frac{1}{2}");
		assertParses("\\frac{a}{b}");
		assertParses("\\frac{x+y}{x-y}");
		assertParses("\\frac{\\frac{1}{2}}{3}");  // 嵌套分式
	}

	@Test
	public void testFracVariants() {
		System.out.println("\n=== 测试分式变体 ===");
		assertParses("\\dfrac{a}{b}");
		assertParses("\\tfrac{1}{2}");
		assertParses("\\cfrac{a}{b}");
	}

	// ============ 7. 根式测试 (BNF 第 92-95 行) ============

	@Test
	public void testSqrt() {
		System.out.println("\n=== 测试根式 ===");
		assertParses("\\sqrt{x}");
		assertParses("\\sqrt{2}");
		assertParses("\\sqrt{x+1}");
		assertParses("\\sqrt{\\sqrt{x}}");  // 嵌套根式
	}

	@Test
	public void testSqrtWithIndex() {
		System.out.println("\n=== 测试带指数的根式 ===");
		assertParses("\\sqrt[3]{27}");
		assertParses("\\sqrt[n]{x}");
		assertParses("\\sqrt[n+1]{x^2}");
	}

	// ============ 8. 定界符测试 (BNF 第 97-111 行) ============

	@Test
	public void testDelimited() {
		System.out.println("\n=== 测试定界符 ===");
		assertParses("\\left( x \\right)");
		assertParses("\\left[ x \\right]");
		assertParses("\\left| x \\right|");
		assertParses("\\left( \\frac{a}{b} \\right)");
		assertParses("\\left. x \\right|");  // 空定界符
	}

	// ============ 9. 函数测试 (BNF 第 113-127 行) ============

	@Test
	public void testTrigFunctions() {
		System.out.println("\n=== 测试三角函数 ===");
		assertParses("\\sin x");
		assertParses("\\cos x");
		assertParses("\\tan x");
		assertParses("\\sin{x}");
		assertParses("\\sin{x+y}");
	}

	@Test
	public void testLogFunctions() {
		System.out.println("\n=== 测试对数函数 ===");
		assertParses("\\log x");
		assertParses("\\ln x");
		assertParses("\\log_2 n");
		assertParses("\\log_{10} x");
	}

	@Test
	public void testOtherFunctions() {
		System.out.println("\n=== 测试其他函数 ===");
		assertParses("\\lim x");
		assertParses("\\max x");
		assertParses("\\min x");
		assertParses("\\det x");
		assertParses("\\exp x");
	}

	@Test
	public void testFunctionWithScripts() {
		System.out.println("\n=== 测试带上下标的函数 ===");
		assertParses("\\lim_{x\\to 0}");
		assertParses("\\lim_{n\\to\\infty}");
	}

	// ============ 10. 大型运算符测试 (BNF 第 129-137 行) ============

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
	}

	@Test
	public void testIntegrals() {
		System.out.println("\n=== 测试积分 ===");
		assertParses("\\int");
		assertParses("\\iint");
		assertParses("\\iiint");
		assertParses("\\oint");
	}

	@Test
	public void testBigSetOperators() {
		System.out.println("\n=== 测试大型集合运算符 ===");
		assertParses("\\bigcup");
		assertParses("\\bigcap");
		assertParses("\\bigvee");
		assertParses("\\bigwedge");
		assertParses("\\bigoplus");
		assertParses("\\bigotimes");
	}

	// ============ 11. 文本模式测试 (BNF 第 150-158 行) ============

	@Test
	public void testText() {
		System.out.println("\n=== 测试文本模式 ===");
		assertParses("\\text{hello}");
		assertParses("\\text{当x趋近于0时}");
		assertParses("\\mbox{test}");
		assertParses("\\textrm{roman}");
	}

	// ============ 12. 重音符号测试 (BNF 第 168-180 行) ============

	@Test
	public void testAccents() {
		System.out.println("\n=== 测试重音符号 ===");
//		assertParses("\\hat{x}");
//		assertParses("\\vec{v}");
//		assertParses("\\bar{x}");
//		assertParses("\\tilde{x}");
//		assertParses("\\dot{x}");
//		assertParses("\\ddot{x}");
		assertParses("\\widehat{xyz}");
		assertParses("\\overline{x+y}");
		assertParses("\\underline{text}");
		assertParses("\\overrightarrow{AB}");
	}

	@Test
	public void testExtendedAccents() {
		System.out.println("\n=== 测试扩展重音符号 ===");
		assertParses("\\grave{a}");
		assertParses("\\acute{a}");
		assertParses("\\breve{a}");
		assertParses("\\check{a}");
		assertParses("\\dot{y}");
		assertParses("\\ddot{y}");
		assertParses("\\overbrace{a+b+c}");
		assertParses("\\underbrace{a+b+c}");
		assertParses("\\widetilde{xyz}");
		assertParses("\\widehat{AB}");
	}

	@Test
	public void testAccentErrors() {
		System.out.println("\n=== 测试重音符号错误 ===");
		assertParseFails("\\bar");            // 缺少花括号
		assertParseFails("\\overline");       // 缺少参数
		assertParseFails("\\dot{");           // 不完整参数
	}

	@Test
	public void testNestedAccents() {
		System.out.println("\n=== 测试嵌套重音结构 ===");
		assertParses("\\hat{x_i}");
		assertParses("\\vec{x^2}");
		assertParses("\\bar{\\frac{a}{b}}");
		assertParses("\\tilde{\\sqrt{x}}");
		assertParses("\\overrightarrow{\\hat{v}}");
	}

	// ============ 13. 复杂表达式测试 ============

	@Test
	public void testComplexExpressions() {
		System.out.println("\n=== 测试复杂表达式 ===");
		// 二次公式
		assertParses("\\frac{-b\\pm\\sqrt{b^2-4ac}}{2a}");

		// 极限
		assertParses("\\frac{\\sin x}{x}");

		// 极限
		assertParses("\\lim_{x\\to 0}\\frac{\\sin x}{x}");

		// 积分
		assertParses("\\int_0^1 x^2");

		// 求和
		assertParses("\\sum_{n=1}^{\\infty}\\frac{1}{n^2}");
	}

	@Test
	public void testNestedStructures() {
		System.out.println("\n=== 测试嵌套结构 ===");
		// 嵌套分式
		assertParses("\\frac{1}{1+\\frac{1}{x}}");

		// 嵌套根式
		assertParses("\\sqrt{1+\\sqrt{x}}");

		// 嵌套上下标
		assertParses("x^{a^2}");
		assertParses("x_{a_1}");
	}

	@Test
	public void testMixedExpressions() {
		System.out.println("\n=== 测试混合表达式 ===");
		// 三角恒等式
		assertParses("\\sin^2\\theta+\\cos^2\\theta");

		// 多项式
		assertParses("ax^2+bx+c");

		// 向量表示
		assertParses("\\vec{v}+\\vec{w}");
	}

	// ============ 14. 原有测试用例 ============

	@Test
	public void testOriginalCases() {
		System.out.println("\n=== 测试原有用例 ===");
		String[] testCases = {
				"x^2",
				"x^{n+1}",
				"x^a+b",
				"\\frac{1}{2}",
				"\\sqrt{x+1}",
				"\\sqrt[3]{27}",
				"\\sum_{i=1}^{n} i^2",
				"\\sin x + y",
				"\\sin{x+y}",
				"\\alpha + \\beta",
				"a+b*c",
				"\\left( \\frac{a}{b} \\right)",
				"\\text{当x趋近于0时}",
				"\\hat{x} + \\vec{v}",
				"\\log_2 n"
		};

		for (String testCase : testCases) {
			assertParses(testCase);
		}
	}

	// ============ 15. 边界情况和错误处理 ============

	@Test
	public void testMissingBraces() {
		System.out.println("\n=== 测试缺少括号 ===");
		assertParseFails("\\frac{a}");      // 缺少第二个参数
		assertParseFails("\\sqrt{");        // 缺少右括号
	}

	@Test
	public void testUnknownCommands() {
		System.out.println("\n=== 测试未知命令 ===");
		assertParseFails("\\unknowncommand");
	}

	// ============ 16. 性能测试 ============

	@Test
	public void testLongExpression() {
		System.out.println("\n=== 测试长表达式 ===");
		// 构造一个较长的表达式
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
		// 5 层嵌套根式
		String expr = "x";
		for (int i = 0; i < 5; i++) {
			expr = "\\sqrt{" + expr + "}";
		}
		assertParses(expr);
	}

	// ============ 17. BNF 完整覆盖验证 ============

	@Test
	public void testBnfCoverage() {
		System.out.println("\n=== BNF 完整覆盖验证 ===");

		// 1. <math_list> & <term>
		assertParses("a+b");

		// 2. <atom> 的所有类型
		assertParses("123");           // number
		assertParses("x");             // variable
		assertParses("\\alpha");       // greek_letter
		assertParses("{x}");           // group
		assertParses("\\frac{a}{b}");  // frac
		assertParses("\\sqrt{x}");     // sqrt
		assertParses("\\left(x\\right)"); // delimited
		assertParses("\\sin x");       // function_call
		assertParses("\\sum");         // large_operator
		assertParses("\\text{hi}");    // text
		assertParses("\\hat{x}");      // accent

		// 3. <sup_sub_suffix> 的所有组合
		assertParses("x^a");
		assertParses("x_a");
		assertParses("x^a_b");
		assertParses("x_a^b");

		System.out.println("\n✅ BNF 覆盖率测试完成!");
	}

	// ============ 20. 错误恢复与健壮性 ============

	@Test
	public void testErrorRecovery() {
		System.out.println("\n=== 测试解析器错误恢复能力 ===");
		String[] badInputs = {
				"\\frac{1}{",        // 缺右括号
				"\\sqrt[",           // 缺指数参数
				"\\left(x",          // 缺 right
				"\\hat",             // 缺花括号
				"\\text",            // 缺参数
				"\\sum_{i=1}^{",     // 不完整上下标
				"{x",                // 括号未闭合
		};

		for (String input : badInputs) {
			assertParseFails(input);
		}
	}

	// ============ 18. 特殊符号测试 (BNF 第 223-227 行) ============

	@Test
	public void testEllipsisSymbols() {
		System.out.println("\n=== 测试省略号符号 ===");
		// 各种省略号
		assertParses("\\dots");
		assertParses("\\ldots");
		assertParses("\\cdots");
		assertParses("\\vdots");
		assertParses("\\ddots");

		// 省略号在表达式中的使用
		assertParses("1, 2, \\dots, n");
		assertParses("a_1 + a_2 + \\cdots + a_n");
		assertParses("x_1, x_2, \\ldots, x_n");
	}

	@Test
	public void testGeometrySymbols() {
		System.out.println("\n=== 测试几何符号 ===");
		// 角度符号（特殊符号）
		assertParses("\\angle");
		assertParses("\\angle ABC");
		assertParses("\\angle_1");
		assertParses("\\angle_2");

		// 垂直和平行符号（二元运算符）
		assertParses("AB \\perp CD");
		assertParses("AB \\parallel CD");
		assertParses("l_1 \\perp l_2");
		assertParses("a \\parallel b");
	}

	@Test
	public void testLogicSymbols() {
		System.out.println("\n=== 测试逻辑符号 ===");
		// \therefore 和 \because 作为特殊符号
		assertParses("\\therefore");
		assertParses("\\because");

		// 在表达式中的使用
		assertParses("\\because a = b");
		assertParses("a = b \\therefore c = d");
		assertParses("x > 0 \\therefore x^2 > 0");
	}

	@Test
	public void testSpecialSymbolsWithScripts() {
		System.out.println("\n=== 测试特殊符号的上下标 ===");
		// 特殊符号可以带上下标
		assertParses("\\angle_1");
		assertParses("\\angle_2");
		assertParses("\\angle_{ABC}");
		assertParses("\\dots^{n}");
		assertParses("\\cdots^{m}");
	}

	@Test
	public void testSpecialSymbolsCannotHaveUnaryOp() {
		System.out.println("\n=== 测试特殊符号不能被一元运算符修饰 ===");
		// 这些应该失败：特殊符号不能被一元运算符修饰
		assertParseFails("-\\dots");
		assertParseFails("+\\angle");
		assertParseFails("-\\therefore");
		assertParseFails("+\\because");
		assertParseFails("\\pm\\cdots");
	}

	@Test
	public void testGeometryRelationOperators() {
		System.out.println("\n=== 测试几何关系作为二元运算符 ===");
		// \perp 和 \parallel 作为二元运算符连接两个 term
		assertParses("AB \\perp CD");
		assertParses("l \\parallel m");
		assertParses("\\vec{v} \\perp \\vec{w}");
		assertParses("x \\parallel y");

		// 在复杂表达式中
		assertParses("a \\perp b \\perp c");
		assertParses("l_1 \\parallel l_2 \\parallel l_3");
	}

	@Test
	public void testSpecialSymbolsInComplexExpressions() {
		System.out.println("\n=== 测试特殊符号在复杂表达式中的使用 ===");
		// 省略号在序列中
		assertParses("a_1 + a_2 + \\cdots + a_n = S");
		assertParses("{1, 2, 3, \\ldots, n}");

		// 角度在几何表达式中
		assertParses("\\angle ABC = 90");
		assertParses("\\angle_1 + \\angle_2 = 180");

		// 逻辑推理
		assertParses("x > 0 \\therefore x^2 > 0");
		assertParses("\\because a = b, c = d");

		// 几何关系
		assertParses("AB \\perp CD \\therefore \\angle ABC = 90");
		assertParses("l_1 \\parallel l_2, l_2 \\parallel l_3 \\therefore l_1 \\parallel l_3");
	}

// ============ 19. 边界情况测试 ============

	@Test
	public void testUnaryOpWithOperandAtom() {
		System.out.println("\n=== 测试一元运算符只能修饰可运算原子 ===");
		// 这些应该成功：一元运算符修饰可运算的原子
		assertParses("-x");
		assertParses("+a");
		assertParses("-123");
		assertParses("-\\alpha");
		assertParses("-{x+y}");
		assertParses("-\\frac{1}{2}");
		assertParses("-\\sqrt{x}");
		assertParses("\\pm x");
		assertParses("\\mp y");

		// 这些应该失败：一元运算符不能修饰特殊符号
		assertParseFails("-\\dots");
		assertParseFails("+\\ldots");
		assertParseFails("-\\angle");
		assertParseFails("+\\therefore");
		assertParseFails("-\\because");
	}

	@Test
	public void testMixedSpecialAndRegularSymbols() {
		System.out.println("\n=== 测试特殊符号和普通符号混合使用 ===");
		// 混合使用
		assertParses("1 + 2 + \\cdots + n");
		assertParses("-x + \\cdots + \\left(-1\\right)^n x^n");  // 合法：使用定界符
		assertParses("\\angle ABC + \\angle BCD = 180");
		assertParses("a \\parallel b, c \\perp d");
		assertParses("\\because x > 0, \\therefore x^2 > 0");
	}
}
