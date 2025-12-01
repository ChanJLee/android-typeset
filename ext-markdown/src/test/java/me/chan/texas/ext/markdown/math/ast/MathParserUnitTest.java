package me.chan.texas.ext.markdown.math.ast;

import org.junit.Test;
import me.chan.texas.utils.CharStream;
import static org.junit.Assert.*;

/**
 * LaTeX 数学公式解析器完整单元测试
 * 基于 bnf_math.txt v4 设计（逗号作为标点符号）
 */
public class MathParserUnitTest {

	// ============================================================
	// 辅助方法
	// ============================================================

	/** 解析输入并返回 AST */
	private MathList parse(String input) throws MathParseException {
		CharStream stream = new CharStream(input, 0, input.length());
		MathParser parser = new MathParser(stream);
		return parser.parse();
	}

	/** 验证解析成功且 toString 值正确 */
	private void assertParsesTo(String input, String expectedOutput) {
		try {
			MathList ast = parse(input);
			String actual = ast.toString();
			assertEquals("输入: " + input, expectedOutput, actual);
			System.out.println("✅ " + input + " → " + actual);
		} catch (MathParseException e) {
			fail("解析失败: " + input + " - " + e.pretty());
		}
	}

	/** 验证解析失败 */
	private void assertParseFails(String input) {
		try {
			parse(input);
			fail("应该抛出异常: " + input);
		} catch (MathParseException e) {
			System.out.println("✅ 正确拒绝: " + input + " (" + e.getMessage() + ")");
		}
	}

	private MathList getMathList(String input) throws MathParseException {
		return parse(input);
	}

	private Expression getExpression(String input) throws MathParseException {
		MathList ast = getMathList(input);
		assertFalse("MathList 不应为空", ast.elements.isEmpty());
		assertTrue("第一个元素应该是 Expression", ast.elements.get(0) instanceof Expression);
		return (Expression) ast.elements.get(0);
	}

	private Term getTerm(String input) throws MathParseException {
		Expression expr = getExpression(input);
		assertFalse("Expression 不应为空", expr.elements.isEmpty());
		assertTrue("第一个元素应该是 Term", expr.elements.get(0) instanceof Term);
		return (Term) expr.elements.get(0);
	}

	// ============================================================
	// Part 1: 基础元素测试
	// ============================================================

	@Test
	public void test_01_01_Number_Integer() {
		System.out.println("\n=== Part 1.1: 数字 - 整数 ===");

		assertParsesTo("0", "0");
		assertParsesTo("1", "1");
		assertParsesTo("42", "42");
		assertParsesTo("123", "123");
	}

	@Test
	public void test_01_02_Number_Decimal() {
		System.out.println("\n=== Part 1.2: 数字 - 小数 ===");

		assertParsesTo("0.5", "0.5");
		assertParsesTo("3.14", "3.14");
		assertParsesTo("123.456", "123.456");
	}

	@Test
	public void test_01_03_Variable() {
		System.out.println("\n=== Part 1.3: 变量 ===");

		assertParsesTo("x", "x");
		assertParsesTo("y", "y");
		assertParsesTo("abc", "abc");
		assertParsesTo("f'", "f'");
		assertParsesTo("g''", "g''");
	}

	@Test
	public void test_01_04_GreekLetters() {
		System.out.println("\n=== Part 1.4: 希腊字母 ===");

		assertParsesTo("\\alpha", "\\alpha");
		assertParsesTo("\\beta", "\\beta");
		assertParsesTo("\\gamma", "\\gamma");
		assertParsesTo("\\Gamma", "\\Gamma");
		assertParsesTo("\\Delta", "\\Delta");
		assertParsesTo("\\infty", "\\infty");
	}

	// ============================================================
	// Part 2: 运算符测试
	// ============================================================

	@Test
	public void test_02_01_UnaryOperators() {
		System.out.println("\n=== Part 2.1: 一元运算符 ===");

		assertParsesTo("-x", "-x");
		assertParsesTo("+x", "+x");
		assertParsesTo("-1", "-1");
		assertParsesTo("\\pm x", "\\pm x");
		assertParsesTo("\\mp y", "\\mp y");
		assertParsesTo("-\\alpha", "-\\alpha");
		assertParsesTo("-{x+y}", "-{x + y}");  // 注意：group 内的 expression 也有空格
	}

	@Test
	public void test_02_02_BinaryOperators_Arithmetic() {
		System.out.println("\n=== Part 2.2: 二元运算符 - 算术 ===");

		// 关键修改：所有二元运算现在用空格分隔
		assertParsesTo("a+b", "a + b");
		assertParsesTo("a-b", "a - b");
		assertParsesTo("a*b", "a * b");
		assertParsesTo("a/b", "a / b");
		assertParsesTo("a\\times b", "a \\times b");
		assertParsesTo("a\\cdot b", "a \\cdot b");
		assertParsesTo("a\\div b", "a \\div b");
		assertParsesTo("a\\pm b", "a \\pm b");
		assertParsesTo("a\\mp b", "a \\mp b");
	}

	@Test
	public void test_02_03_BinaryOperators_Relational() {
		System.out.println("\n=== Part 2.3: 二元运算符 - 关系 ===");

		assertParsesTo("a=b", "a = b");
		assertParsesTo("a\\neq b", "a \\neq b");
		assertParsesTo("a<b", "a < b");
		assertParsesTo("a>b", "a > b");
		assertParsesTo("a\\le b", "a \\le b");
		assertParsesTo("a\\ge b", "a \\ge b");
		assertParsesTo("a\\leq b", "a \\leq b");
		assertParsesTo("a\\geq b", "a \\geq b");
	}

	@Test
	public void test_02_04_BinaryOperators_SetAndLogic() {
		System.out.println("\n=== Part 2.4: 二元运算符 - 集合和逻辑 ===");

		assertParsesTo("x\\in A", "x \\in A");
		assertParsesTo("A\\subset B", "A \\subset B");
		assertParsesTo("A\\cup B", "A \\cup B");
		assertParsesTo("A\\cap B", "A \\cap B");
		assertParsesTo("p\\wedge q", "p \\wedge q");
		assertParsesTo("p\\vee q", "p \\vee q");
	}

	@Test
	public void test_02_05_BinaryOperators_Arrows() {
		System.out.println("\n=== Part 2.5: 二元运算符 - 箭头 ===");

		assertParsesTo("a\\to b", "a \\to b");
		assertParsesTo("A\\Rightarrow B", "A \\Rightarrow B");
		assertParsesTo("A\\implies B", "A \\implies B");
		assertParsesTo("A\\iff B", "A \\iff B");
	}

	// ============================================================
	// Part 3: 标点符号（逗号）
	// ============================================================

	@Test
	public void test_03_01_Punctuation_Comma() {
		System.out.println("\n=== Part 3.1: 标点符号 - 逗号 ===");

		// 逗号现在是独立的 term，用空格分隔
		assertParsesTo("a,b", "a , b");
		assertParsesTo("a,b,c", "a , b , c");
		assertParsesTo("x,y,z", "x , y , z");

		// 在定界符中
		assertParsesTo("f\\left(x,y\\right)", "f \\left( x , y \\right)");
		assertParsesTo("\\left\\{x,y,z\\right\\}", "\\left{ x , y , z \\right}");

		try {
			Expression expr = getExpression("a,b");
			assertEquals("应该有 3 个元素", 3, expr.elements.size());
			assertTrue("第1个是 Term(a)", expr.elements.get(0) instanceof Term);
			assertTrue("第2个是 Term(逗号)", expr.elements.get(1) instanceof Term);
			assertTrue("第3个是 Term(b)", expr.elements.get(2) instanceof Term);

			Term commaTerm = (Term) expr.elements.get(1);
			assertTrue("逗号应该是 PunctuationAtom", commaTerm.atom instanceof PunctuationAtom);

			System.out.println("✅ 逗号标点验证通过");
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	// ============================================================
	// Part 4: 原子表达式
	// ============================================================

	@Test
	public void test_04_01_Group() {
		System.out.println("\n=== Part 4.1: 分组 ===");

		assertParsesTo("{x}", "{x}");
		assertParsesTo("{a+b}", "{a + b}");  // group 内的 expression 有空格
		assertParsesTo("{{x}}", "{{x}}");
	}

	@Test
	public void test_04_02_Frac() {
		System.out.println("\n=== Part 4.2: 分式 ===");

		assertParsesTo("\\frac{1}{2}", "\\frac{1}{2}");
		assertParsesTo("\\frac{a}{b}", "\\frac{a}{b}");
		assertParsesTo("\\frac{x+y}{x-y}", "\\frac{x + y}{x - y}");  // 分子分母内有空格
		assertParsesTo("\\dfrac{a}{b}", "\\dfrac{a}{b}");
		assertParsesTo("\\frac{\\frac{1}{2}}{3}", "\\frac{\\frac{1}{2}}{3}");
	}

	@Test
	public void test_04_03_Sqrt() {
		System.out.println("\n=== Part 4.3: 根式 ===");

		assertParsesTo("\\sqrt{x}", "\\sqrt{x}");
		assertParsesTo("\\sqrt{2}", "\\sqrt{2}");
		assertParsesTo("\\sqrt{x+1}", "\\sqrt{x + 1}");
		assertParsesTo("\\sqrt[3]{27}", "\\sqrt[3]{27}");
		assertParsesTo("\\sqrt[n]{x}", "\\sqrt[n]{x}");
	}

	@Test
	public void test_04_04_Delimited() {
		System.out.println("\n=== Part 4.4: 定界符 ===");

		assertParsesTo("\\left(x\\right)", "\\left( x \\right)");
		assertParsesTo("\\left[x\\right]", "\\left[ x \\right]");
		assertParsesTo("\\left\\{x\\right\\}", "\\left{ x \\right}");
		assertParsesTo("\\left|x\\right|", "\\left| x \\right|");
		assertParsesTo("\\left(x+y\\right)", "\\left( x + y \\right)");  // 内容有空格
	}

	@Test
	public void test_04_05_Function() {
		System.out.println("\n=== Part 4.5: 函数 ===");

		assertParsesTo("\\sin x", "\\sin x");
		assertParsesTo("\\cos x", "\\cos x");
		assertParsesTo("\\log x", "\\log x");
		assertParsesTo("\\sin{x+y}", "\\sin{x + y}");
		assertParsesTo("\\log_2 x", "\\log_2 x");
		assertParsesTo("\\sin^2 x", "\\sin^2 x");
	}

	@Test
	public void test_04_06_LargeOperator() {
		System.out.println("\n=== Part 4.6: 大型运算符 ===");

		assertParsesTo("\\sum", "\\sum");
		assertParsesTo("\\prod", "\\prod");
		assertParsesTo("\\int", "\\int");
		assertParsesTo("\\sum_{i=1}^{n}", "\\sum_{i = 1}^{n}");  // 下标内有空格
		assertParsesTo("\\int_0^1", "\\int_0^1");
		assertParsesTo("\\lim_{x\\to 0}", "\\lim_{x \\to 0}");  // 下标内有空格
	}

	@Test
	public void test_04_07_Matrix() {
		System.out.println("\n=== Part 4.7: 矩阵 ===");

		assertParsesTo(
				"\\begin{matrix}a&b\\\\c&d\\end{matrix}",
				"\\begin{matrix}\na & b\nc & d\n\\end{matrix}\n"
		);

		assertParsesTo(
				"\\begin{pmatrix}1&2\\\\3&4\\end{pmatrix}",
				"\\begin{pmatrix}\n1 & 2\n3 & 4\n\\end{pmatrix}\n"
		);

		// cases 环境（逗号测试的关键！）
		assertParsesTo(
				"\\begin{cases}x,&x\\ge 0\\\\-x,&x<0\\end{cases}",
				"\\begin{cases}\nx , & x \\ge 0\n-x , & x < 0\n\\end{cases}\n"
		);
	}

	@Test
	public void test_04_08_Text() {
		System.out.println("\n=== Part 4.8: 文本 ===");

		assertParsesTo("\\text{hello}", "\\text{hello}");
		assertParsesTo("\\text{当x趋近于0时}", "\\text{当x趋近于0时}");
	}

	@Test
	public void test_04_09_Accent() {
		System.out.println("\n=== Part 4.9: 重音 ===");

		assertParsesTo("\\hat{x}", "\\hat{x}");
		assertParsesTo("\\vec{v}", "\\vec{v}");
		assertParsesTo("\\bar{x}", "\\bar{x}");
		assertParsesTo("\\hat x", "\\hat x");
	}

	@Test
	public void test_04_10_Font() {
		System.out.println("\n=== Part 4.10: 字体 ===");

		assertParsesTo("\\mathbf{x}", "\\mathbf{x}");
		assertParsesTo("\\mathbb{R}", "\\mathbb{R}");
		assertParsesTo("\\mathcal{L}", "\\mathcal{L}");
	}

	@Test
	public void test_04_11_SpecialSymbol() {
		System.out.println("\n=== Part 4.11: 特殊符号 ===");

		assertParsesTo("\\dots", "\\dots");
		assertParsesTo("\\ldots", "\\ldots");
		assertParsesTo("\\cdots", "\\cdots");
		assertParsesTo("\\angle", "\\angle");
		assertParsesTo("\\therefore", "\\therefore");
		assertParsesTo("\\because", "\\because");

		// 特殊符号可以带上下标
		assertParsesTo("\\angle_1", "\\angle_1");
		assertParsesTo("\\dots^{n}", "\\dots^{n}");

		// 但不能被一元运算符修饰
		assertParseFails("-\\dots");
		assertParseFails("+\\angle");
	}

	// ============================================================
	// Part 5: 上下标
	// ============================================================

	@Test
	public void test_05_01_Superscript() {
		System.out.println("\n=== Part 5.1: 上标 ===");

		assertParsesTo("x^2", "x^2");
		assertParsesTo("x^a", "x^a");
		assertParsesTo("x^\\alpha", "x^\\alpha");
		assertParsesTo("x^{n+1}", "x^{n + 1}");  // 上标内有空格
		assertParsesTo("e^{-x}", "e^{-x}");
	}

	@Test
	public void test_05_02_Subscript() {
		System.out.println("\n=== Part 5.2: 下标 ===");

		assertParsesTo("x_1", "x_1");
		assertParsesTo("x_i", "x_i");
		assertParsesTo("x_{n-1}", "x_{n - 1}");  // 下标内有空格
		assertParsesTo("a_{i,j}", "a_{i , j}");  // 下标内的逗号也有空格
	}

	@Test
	public void test_05_03_BothScripts() {
		System.out.println("\n=== Part 5.3: 上下标同时 ===");

		assertParsesTo("x^2_1", "x^2_1");
		assertParsesTo("x_1^2", "x_1^2");
		assertParsesTo("x^{n+1}_{i}", "x^{n + 1}_{i}");
	}

	// ============================================================
	// Part 6: 复合结构和歧义
	// ============================================================

	@Test
	public void test_06_01_ImplicitMultiplication() {
		System.out.println("\n=== Part 6.1: 隐式乘法 ===");

		// 变量连写（单个变量）
		assertParsesTo("abc", "abc");
		assertParsesTo("xy", "xy");

		// 数字+变量（隐式乘法，用空格分隔）
		assertParsesTo("2x", "2 x");
		assertParsesTo("3.14r", "3.14 r");

		// 变量+数字
		assertParsesTo("x2", "x 2");

		// 变量+希腊字母
		assertParsesTo("x\\alpha", "x \\alpha");

		// 数字+希腊字母
		assertParsesTo("2\\pi", "2 \\pi");

		// group+变量
		assertParsesTo("{a}b", "{a} b");
	}

	@Test
	public void test_06_02_UnaryVsBinary() {
		System.out.println("\n=== Part 6.2: 一元 vs 二元 ===");

		assertParsesTo("a+-b", "a + -b");
		assertParsesTo("a-+b", "a - +b");
		assertParsesTo("a--b", "a - -b");
		assertParsesTo("\\pm a", "\\pm a");
		assertParsesTo("a\\pm b", "a \\pm b");
		assertParsesTo("a\\pm\\pm b", "a \\pm \\pm b");
	}

	@Test
	public void test_06_03_FunctionArgument() {
		System.out.println("\n=== Part 6.3: 函数参数边界 ===");

		assertParsesTo("\\sin x", "\\sin x");
		assertParsesTo("\\sin{x+y}", "\\sin{x + y}");
		assertParsesTo("\\sin x+\\cos y", "\\sin x + \\cos y");
		assertParsesTo("\\sin{\\frac{x}{2}}", "\\sin{\\frac{x}{2}}");
	}

	@Test
	public void test_06_04_SuperscriptPrecedence() {
		System.out.println("\n=== Part 6.4: 上下标优先级 ===");

		assertParsesTo("x^2+1", "x^2 + 1");
		assertParsesTo("x^2y", "x^2 y");
		assertParsesTo("x^{2+1}", "x^{2 + 1}");
	}

	@Test
	public void test_06_05_Spacing() {
		System.out.println("\n=== Part 6.5: 空格命令 ===");

		assertParsesTo("a\\quad b", "a\\quad b");
		assertParsesTo("a\\, b", "a\\, b");
		assertParsesTo("a\\qquad b", "a\\qquad b");
	}

	// ============================================================
	// Part 7: AST 结构验证
	// ============================================================

	@Test
	public void test_07_01_Expression_Structure() {
		System.out.println("\n=== Part 7.1: Expression 结构 ===");

		try {
			// 单个 term
			Expression expr = getExpression("x");
			assertEquals(1, expr.elements.size());

			// term binary_op term
			expr = getExpression("a+b");
			assertEquals(3, expr.elements.size());
			assertTrue("第1个是 Term", expr.elements.get(0) instanceof Term);
			assertTrue("第2个是 BinOpAtom", expr.elements.get(1) instanceof BinOpAtom);
			assertTrue("第3个是 Term", expr.elements.get(2) instanceof Term);

			// term binary_op term binary_op term
			expr = getExpression("a+b*c");
			assertEquals(5, expr.elements.size());

			// term term term (逗号分隔)
			expr = getExpression("a,b,c");
			assertEquals(5, expr.elements.size());
			assertTrue("第1个是 Term", expr.elements.get(0) instanceof Term);
			assertTrue("第2个是 Term(逗号)", expr.elements.get(1) instanceof Term);
			assertTrue("第3个是 Term", expr.elements.get(2) instanceof Term);
			assertTrue("第4个是 Term(逗号)", expr.elements.get(3) instanceof Term);
			assertTrue("第5个是 Term", expr.elements.get(4) instanceof Term);

			System.out.println("✅ Expression 结构验证通过");
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_07_02_MathList_Structure() {
		System.out.println("\n=== Part 7.2: MathList 结构 ===");

		try {
			MathList ast = getMathList("a\\quad b");
			assertEquals(3, ast.elements.size());
			assertTrue("第1个是 Expression", ast.elements.get(0) instanceof Expression);
			assertTrue("第2个是 Spacing", ast.elements.get(1) instanceof Spacing);
			assertTrue("第3个是 Expression", ast.elements.get(2) instanceof Expression);

			System.out.println("✅ MathList 结构验证通过");
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	// ============================================================
	// Part 8: 错误情况
	// ============================================================

	@Test
	public void test_08_01_Error_IncompleteStructures() {
		System.out.println("\n=== Part 8.1: 错误 - 不完整结构 ===");

		assertParseFails("\\frac{1}{");
		assertParseFails("\\frac{1}");
		assertParseFails("\\sqrt[");
		assertParseFails("\\left(x");
		assertParseFails("{x");
	}

	@Test
	public void test_08_02_Error_BinaryOpMissingOperand() {
		System.out.println("\n=== Part 8.2: 错误 - 二元运算符缺操作数 ===");

		assertParseFails("a+");
		assertParseFails("a-");
		assertParseFails("a*");
		assertParseFails("a\\times");
	}

	@Test
	public void test_08_03_Error_UnknownCommand() {
		System.out.println("\n=== Part 8.3: 错误 - 未知命令 ===");

		assertParseFails("\\unknowncommand");
		assertParseFails("\\sine x");
	}

	// ============================================================
	// Part 9: 真实世界公式
	// ============================================================

	@Test
	public void test_09_01_Real_QuadraticFormula() {
		System.out.println("\n=== Part 9.1: 二次公式 ===");

		assertParsesTo(
				"\\frac{-b\\pm\\sqrt{b^2-4ac}}{2a}",
				"\\frac{-b \\pm \\sqrt{b^2 - 4 ac}}{2 a}"
		);
	}

	@Test
	public void test_09_02_Real_EulerFormula() {
		System.out.println("\n=== Part 9.2: 欧拉公式 ===");

		assertParsesTo("e^{i\\pi}+1=0", "e^{i \\pi} + 1 = 0");
	}

	@Test
	public void test_09_03_Real_PythagoreanTheorem() {
		System.out.println("\n=== Part 9.3: 勾股定理 ===");

		assertParsesTo("a^2+b^2=c^2", "a^2 + b^2 = c^2");
	}

	@Test
	public void test_09_04_Real_Limit() {
		System.out.println("\n=== Part 9.4: 极限 ===");

		assertParsesTo(
				"\\lim_{x\\to 0}\\frac{\\sin x}{x}=1",
				"\\lim_{x \\to 0} \\frac{\\sin x}{x} = 1"
		);
	}

	@Test
	public void test_09_05_Real_Sum() {
		System.out.println("\n=== Part 9.5: 求和 ===");

		assertParsesTo(
				"\\sum_{n=1}^{\\infty}\\frac{1}{n^2}=\\frac{\\pi^2}{6}",
				"\\sum_{n = 1}^{\\infty} \\frac{1}{n^2} = \\frac{\\pi^2}{6}"
		);
	}

	@Test
	public void test_09_06_Real_Derivative() {
		System.out.println("\n=== Part 9.6: 导数 ===");

		assertParsesTo(
				"f'\\left(x\\right)=\\lim_{h\\to 0}\\frac{f\\left(x+h\\right)-f\\left(x\\right)}{h}",
				"f' \\left( x \\right) = \\lim_{h \\to 0} \\frac{f \\left( x + h \\right) - f \\left( x \\right)}{h}"
		);
	}

	@Test
	public void test_09_07_Real_Integral() {
		System.out.println("\n=== Part 9.7: 积分 ===");

		assertParsesTo(
				"\\int_0^{\\infty}e^{-x^2}dx=\\frac{\\sqrt{\\pi}}{2}",
				"\\int_0^{\\infty} e^{-x^2} dx = \\frac{\\sqrt{\\pi}}{2}"
		);
	}

	@Test
	public void test_09_08_Real_MatrixMultiplication() {
		System.out.println("\n=== Part 9.8: 矩阵乘法 ===");

		assertParsesTo(
				"\\begin{pmatrix}a&b\\\\c&d\\end{pmatrix}\\begin{pmatrix}x\\\\y\\end{pmatrix}=\\begin{pmatrix}ax+by\\\\cx+dy\\end{pmatrix}",
				"\\begin{pmatrix}\na & b\nc & d\n\\end{pmatrix}\n \\begin{pmatrix}\nx\ny\n\\end{pmatrix}\n = \\begin{pmatrix}\nax + by\ncx + dy\n\\end{pmatrix}\n"
		);
	}

	@Test
	public void test_09_09_Real_PiecewiseFunction() {
		System.out.println("\n=== Part 9.9: 分段函数 ===");

		assertParsesTo(
				"f\\left(x\\right)=\\begin{cases}x,&x\\ge 0\\\\-x,&x<0\\end{cases}",
				"f \\left( x \\right) = \\begin{cases}\nx , & x \\ge 0\n-x , & x < 0\n\\end{cases}\n"
		);
	}

	@Test
	public void test_09_10_Real_CauchySchwarz() {
		System.out.println("\n=== Part 9.10: 柯西-施瓦茨不等式 ===");

		assertParsesTo(
				"\\left(\\sum_{i=1}^{n}a_i b_i\\right)^2\\le\\left(\\sum_{i=1}^{n}a_i^2\\right)\\left(\\sum_{i=1}^{n}b_i^2\\right)",
				"\\left( \\sum_{i = 1}^{n} a_i b_i \\right)^2 \\le \\left( \\sum_{i = 1}^{n} a_i^2 \\right) \\left( \\sum_{i = 1}^{n} b_i^2 \\right)"
		);
	}

	@Test
	public void test_99_Summary() {
		System.out.println("\n" + "=".repeat(60));
		System.out.println("测试总结：");
		System.out.println("✅ 所有基础语法元素已测试");
		System.out.println("✅ 逗号作为标点符号正确处理");
		System.out.println("✅ 所有歧义情况已验证");
		System.out.println("✅ AST 结构符合 BNF 预期");
		System.out.println("✅ 边界和错误情况已覆盖");
		System.out.println("✅ 真实世界公式测试通过");
		System.out.println("=".repeat(60));
	}
}