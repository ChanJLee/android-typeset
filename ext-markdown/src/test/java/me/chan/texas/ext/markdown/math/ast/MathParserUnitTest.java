package me.chan.texas.ext.markdown.math.ast;

import org.junit.Test;

import me.chan.texas.utils.CharStream;

import static org.junit.Assert.*;

/**
 * LaTeX 数学公式解析器完整单元测试
 * 基于 bnf_math.txt 设计，验证所有语法元素的AST类型和值
 */
public class MathParserUnitTest {

	// ============ 辅助方法 ============

	/**
	 * 解析输入并返回AST
	 */
	private MathList parse(String input) throws MathParseException {
		CharStream stream = new CharStream(input, 0, input.length());
		MathParser parser = new MathParser(stream);
		return parser.parse();
	}

	/**
	 * 验证解析成功且toString值正确
	 */
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

	/**
	 * 验证解析成功且AST类型正确
	 */
	private void assertAstType(String input, Class<? extends Ast> expectedType) {
		try {
			MathList ast = parse(input);
			assertNotNull("AST不应为null", ast);
			assertFalse("MathList不应为空", ast.elements.isEmpty());

			Ast firstElement = ast.elements.get(0);
			assertTrue(
					"期望类型: " + expectedType.getSimpleName() + ", 实际类型: " + firstElement.getClass().getSimpleName(),
					expectedType.isInstance(firstElement)
			);

			System.out.println("✅ " + input + " 类型: " + firstElement.getClass().getSimpleName());
		} catch (MathParseException e) {
			fail("解析失败: " + input + " - " + e.pretty());
		}
	}

	/**
	 * 验证解析成功，并检查AST类型和toString值
	 */
	private void assertParsesToWithType(String input, String expectedOutput, Class<? extends Ast> expectedType) {
		try {
			MathList ast = parse(input);
			String actual = ast.toString();
			assertEquals("输入: " + input, expectedOutput, actual);

			assertFalse("MathList不应为空", ast.elements.isEmpty());
			Ast firstElement = ast.elements.get(0);
			assertTrue(
					"期望类型: " + expectedType.getSimpleName() + ", 实际类型: " + firstElement.getClass().getSimpleName(),
					expectedType.isInstance(firstElement)
			);

			System.out.println("✅ " + input + " → " + actual + " [" + firstElement.getClass().getSimpleName() + "]");
		} catch (MathParseException e) {
			fail("解析失败: " + input + " - " + e.pretty());
		}
	}

	/**
	 * 验证解析失败
	 */
	private void assertParseFails(String input) {
		try {
			parse(input);
			fail("应该抛出异常: " + input);
		} catch (MathParseException e) {
			System.out.println("✅ 正确拒绝: " + input + " (" + e.getMessage() + ")");
		}
	}

	/**
	 * 验证Expression内部结构
	 */
	private Expression getExpression(String input) throws MathParseException {
		MathList ast = parse(input);
		assertFalse("MathList不应为空", ast.elements.isEmpty());
		assertTrue("第一个元素应该是Expression", ast.elements.get(0) instanceof Expression);
		return (Expression) ast.elements.get(0);
	}

	/**
	 * 验证Term结构
	 */
	private Term getTerm(String input) throws MathParseException {
		Expression expr = getExpression(input);
		assertFalse("Expression不应为空", expr.elements.isEmpty());
		assertTrue("第一个元素应该是Term", expr.elements.get(0) instanceof Term);
		return (Term) expr.elements.get(0);
	}

	// ============ 1. 基础元素测试 ============

	@Test
	public void test01_Number() {
		System.out.println("\n=== 测试 <number> ===");

		// 整数
		assertParsesToWithType("123", "123", Expression.class);
		assertParsesTo("0", "0");
		assertParsesTo("999", "999");

		// 小数
		assertParsesTo("3.14", "3.14");
		assertParsesTo("0.5", "0.5");
		assertParsesTo("123.456", "123.456");

		// 验证AST结构
		try {
			Term term = getTerm("42");
			assertTrue("Atom应该是NumberAtom", term.atom instanceof NumberAtom);
			assertEquals("42", ((NumberAtom) term.atom).value);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test02_Variable() {
		System.out.println("\n=== 测试 <variable> ===");

		// 单字母
		assertParsesTo("x", "x");
		assertParsesTo("y", "y");
		assertParsesTo("A", "A");
		assertParsesTo("Z", "Z");

		// 多字母（隐式乘法）
		assertParsesTo("abc", "abc");
		assertParsesTo("xyz", "xyz");

		// 带prime后缀
		assertParsesTo("f'", "f'");
		assertParsesTo("g''", "g''");
		assertParsesTo("y'''", "y'''");

		// 验证AST结构
		try {
			Term term = getTerm("x");
			assertTrue("Atom应该是VariableAtom", term.atom instanceof VariableAtom);
			assertEquals("x", ((VariableAtom) term.atom).name);

			term = getTerm("f'");
			assertEquals("f'", ((VariableAtom) term.atom).name);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test03_GreekLetters() {
		System.out.println("\n=== 测试 <greek_letter> ===");

		// 小写希腊字母（采样）
		assertParsesTo("\\alpha", "\\alpha");
		assertParsesTo("\\beta", "\\beta");
		assertParsesTo("\\gamma", "\\gamma");
		assertParsesTo("\\delta", "\\delta");
		assertParsesTo("\\epsilon", "\\epsilon");
		assertParsesTo("\\theta", "\\theta");
		assertParsesTo("\\lambda", "\\lambda");
		assertParsesTo("\\pi", "\\pi");
		assertParsesTo("\\sigma", "\\sigma");
		assertParsesTo("\\omega", "\\omega");

		// 变体
		assertParsesTo("\\varepsilon", "\\varepsilon");
		assertParsesTo("\\vartheta", "\\vartheta");

		// 大写希腊字母
		assertParsesTo("\\Gamma", "\\Gamma");
		assertParsesTo("\\Delta", "\\Delta");
		assertParsesTo("\\Theta", "\\Theta");
		assertParsesTo("\\Lambda", "\\Lambda");
		assertParsesTo("\\Sigma", "\\Sigma");
		assertParsesTo("\\Omega", "\\Omega");

		// 无穷大
		assertParsesTo("\\infty", "\\infty");

		// 验证AST结构
		try {
			Term term = getTerm("\\alpha");
			assertTrue("Atom应该是GreekLetterAtom", term.atom instanceof GreekLetterAtom);
			assertEquals("alpha", ((GreekLetterAtom) term.atom).symbol);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	// ============ 2. 运算符测试 ============

	@Test
	public void test04_UnaryOperators() {
		System.out.println("\n=== 测试 <unary_op> ===");

		assertParsesTo("-x", "-x");
		assertParsesTo("+x", "+x");
		assertParsesTo("-123", "-123");
		assertParsesTo("+456", "+456");
		assertParsesTo("\\pm x", "\\pm x");
		assertParsesTo("\\mp y", "\\mp y");

		// 一元运算符修饰复杂原子
		assertParsesTo("-\\alpha", "-\\alpha");
		assertParsesTo("-{x+y}", "-{x+y}");
		assertParsesTo("-\\frac{1}{2}", "-\\frac{1}{2}");

		// 验证AST结构
		try {
			Term term = getTerm("-x");
			assertNotNull("应该有一元运算符", term.unaryOp);
			assertEquals("-", term.unaryOp.op);
			assertTrue("Atom应该是VariableAtom", term.atom instanceof VariableAtom);

			term = getTerm("\\pm x");
			assertEquals("\\pm", term.unaryOp.op);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test05_BinaryOperators() {
		System.out.println("\n=== 测试 <binary_op> ===");

		// 基本算术运算符
		assertParsesTo("a+b", "a+b");
		assertParsesTo("a-b", "a-b");
		assertParsesTo("a*b", "a*b");
		assertParsesTo("a/b", "a/b");
		assertParsesTo("a,b", "a,b");

		// LaTeX运算符
		assertParsesTo("a\\times b", "a\\times b");
		assertParsesTo("a\\cdot b", "a\\cdot b");
		assertParsesTo("a\\div b", "a\\div b");
		assertParsesTo("a\\pm b", "a\\pm b");
		assertParsesTo("a\\mp b", "a\\mp b");

		// 关系运算符
		assertParsesTo("a=b", "a=b");
		assertParsesTo("a\\neq b", "a\\neq b");
		assertParsesTo("a\\equiv b", "a\\equiv b");
		assertParsesTo("a\\approx b", "a\\approx b");
		assertParsesTo("a<b", "a<b");
		assertParsesTo("a>b", "a>b");
		assertParsesTo("a\\le b", "a\\le b");
		assertParsesTo("a\\ge b", "a\\ge b");
		assertParsesTo("a\\leq b", "a\\leq b");
		assertParsesTo("a\\geq b", "a\\geq b");

		// 集合运算符
		assertParsesTo("x\\in A", "x\\in A");
		assertParsesTo("x\\notin A", "x\\notin A");
		assertParsesTo("A\\subset B", "A\\subset B");
		assertParsesTo("A\\cup B", "A\\cup B");
		assertParsesTo("A\\cap B", "A\\cap B");

		// 箭头
		assertParsesTo("a\\to b", "a\\to b");
		assertParsesTo("A\\Rightarrow B", "A\\Rightarrow B");
		assertParsesTo("A\\iff B", "A\\iff B");

		// 几何关系
		assertParsesTo("a\\perp b", "a\\perp b");
		assertParsesTo("a\\parallel b", "a\\parallel b");

		// 验证AST结构：a+b应该有3个元素（Term, BinOp, Term）
		try {
			Expression expr = getExpression("a+b");
			assertEquals("应该有3个元素", 3, expr.elements.size());
			assertTrue("第1个应该是Term", expr.elements.get(0) instanceof Term);
			assertTrue("第2个应该是BinOpAtom", expr.elements.get(1) instanceof BinOpAtom);
			assertTrue("第3个应该是Term", expr.elements.get(2) instanceof Term);

			BinOpAtom binOp = (BinOpAtom) expr.elements.get(1);
			assertEquals("+", binOp.op);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	// ============ 3. 分组测试 ============

	@Test
	public void test06_Group() {
		System.out.println("\n=== 测试 <group> ===");

		assertParsesTo("{x}", "{x}");
		assertParsesTo("{a+b}", "{a+b}");
		assertParsesTo("{xyz}", "{xyz}");
		assertParsesTo("{{x}}", "{{x}}");
		assertParsesTo("{}", "{}");

		// 验证AST结构
		try {
			Term term = getTerm("{x}");
			assertTrue("Atom应该是Group", term.atom instanceof Group);
			Group group = (Group) term.atom;
			assertFalse("Group内容不应为空", group.content.elements.isEmpty());
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	// ============ 4. 上下标测试 ============

	@Test
	public void test07_Superscript() {
		System.out.println("\n=== 测试上标 ===");

		assertParsesTo("x^2", "x^2");
		assertParsesTo("x^{n+1}", "x^{n+1}");
		assertParsesTo("x^a", "x^a");
		assertParsesTo("x^\\alpha", "x^\\alpha");
		assertParsesTo("e^{-x}", "e^{-x}");

		// 验证AST结构
		try {
			Term term = getTerm("x^2");
			assertNotNull("应该有上下标后缀", term.suffix);
			assertNotNull("应该有上标", term.suffix.superscript);
			assertNull("不应该有下标", term.suffix.subscript);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test08_Subscript() {
		System.out.println("\n=== 测试下标 ===");

		assertParsesTo("x_1", "x_1");
		assertParsesTo("x_{n-1}", "x_{n-1}");
		assertParsesTo("x_i", "x_i");
		assertParsesTo("a_\\alpha", "a_\\alpha");

		// 验证AST结构
		try {
			Term term = getTerm("x_1");
			assertNotNull("应该有上下标后缀", term.suffix);
			assertNull("不应该有上标", term.suffix.superscript);
			assertNotNull("应该有下标", term.suffix.subscript);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test09_SuperAndSubscript() {
		System.out.println("\n=== 测试上下标组合 ===");

		assertParsesTo("x^2_1", "x^2_1");
		assertParsesTo("x_1^2", "x_1^2");
		assertParsesTo("x^{n+1}_{i}", "x^{n+1}_{i}");
		assertParsesTo("x_{i}^{n+1}", "x_{i}^{n+1}");

		// 验证AST结构
		try {
			Term term = getTerm("x^2_1");
			assertNotNull("应该有上下标后缀", term.suffix);
			assertNotNull("应该有上标", term.suffix.superscript);
			assertNotNull("应该有下标", term.suffix.subscript);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	// ============ 5. 分式测试 ============

	@Test
	public void test10_Frac() {
		System.out.println("\n=== 测试 <frac> ===");

		assertParsesTo("\\frac{1}{2}", "\\frac{1}{2}");
		assertParsesTo("\\frac{a}{b}", "\\frac{a}{b}");
		assertParsesTo("\\frac{x+y}{x-y}", "\\frac{x+y}{x-y}");
		assertParsesTo("\\dfrac{a}{b}", "\\dfrac{a}{b}");
		assertParsesTo("\\tfrac{1}{2}", "\\tfrac{1}{2}");
		assertParsesTo("\\cfrac{a}{b}", "\\cfrac{a}{b}");

		// 嵌套分式
		assertParsesTo("\\frac{\\frac{1}{2}}{3}", "\\frac{\\frac{1}{2}}{3}");

		// 验证AST结构
		try {
			Term term = getTerm("\\frac{1}{2}");
			assertTrue("Atom应该是FracAtom", term.atom instanceof FracAtom);
			FracAtom frac = (FracAtom) term.atom;
			assertEquals("frac", frac.command);
			assertEquals("1", frac.numerator.toString());
			assertEquals("2", frac.denominator.toString());
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	// ============ 6. 根式测试 ============

	@Test
	public void test11_Sqrt() {
		System.out.println("\n=== 测试 <sqrt> ===");

		assertParsesTo("\\sqrt{x}", "\\sqrt{x}");
		assertParsesTo("\\sqrt{2}", "\\sqrt{2}");
		assertParsesTo("\\sqrt{x+1}", "\\sqrt{x+1}");
		assertParsesTo("\\sqrt[3]{27}", "\\sqrt[3]{27}");
		assertParsesTo("\\sqrt[n]{x}", "\\sqrt[n]{x}");

		// 嵌套根式
		assertParsesTo("\\sqrt{\\sqrt{x}}", "\\sqrt{\\sqrt{x}}");

		// 验证AST结构
		try {
			Term term = getTerm("\\sqrt{x}");
			assertTrue("Atom应该是SqrtAtom", term.atom instanceof SqrtAtom);
			SqrtAtom sqrt = (SqrtAtom) term.atom;
			assertNull("不应该有根次数", sqrt.root);
			assertEquals("x", sqrt.content.toString());

			term = getTerm("\\sqrt[3]{27}");
			sqrt = (SqrtAtom) term.atom;
			assertNotNull("应该有根次数", sqrt.root);
			assertEquals("3", sqrt.root.toString());
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	// ============ 7. 定界符测试 ============

	@Test
	public void test12_Delimited() {
		System.out.println("\n=== 测试 <delimited> ===");

		// 不同级别
		assertParsesTo("\\left( x \\right)", "\\left( x \\right)");
		assertParsesTo("\\bigl[ x \\bigr]", "\\bigl[ x \\bigr]");
		assertParsesTo("\\Bigl\\{ x \\Bigr\\}", "\\Bigl{ x \\Bigr}");

		// 不同定界符类型
		assertParsesTo("\\left( x \\right)", "\\left( x \\right)");
		assertParsesTo("\\left[ x \\right]", "\\left[ x \\right]");
		assertParsesTo("\\left\\{ x \\right\\}", "\\left{ x \\right}");
		assertParsesTo("\\left| x \\right|", "\\left| x \\right|");
		assertParsesTo("\\left\\langle x \\right\\rangle", "\\left\\langle x \\right\\rangle");
		assertParsesTo("\\left. x \\right|", "\\left. x \\right|");  // 空定界符

		// 验证AST结构
		try {
			Term term = getTerm("\\left( x \\right)");
			assertTrue("Atom应该是DelimitedAtom", term.atom instanceof DelimitedAtom);
			DelimitedAtom delim = (DelimitedAtom) term.atom;
			assertEquals(0, delim.level);  // left/right是level 0
			assertEquals("(", delim.leftDelimiter);
			assertEquals(")", delim.rightDelimiter);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	// ============ 8. 函数测试 ============

	@Test
	public void test13_Functions() {
		System.out.println("\n=== 测试 <function_call> ===");

		// 三角函数
		assertParsesTo("\\sin x", "\\sin x");
		assertParsesTo("\\cos x", "\\cos x");
		assertParsesTo("\\tan x", "\\tan x");
		assertParsesTo("\\sin{x+y}", "\\sin{x+y}");

		// 对数函数
		assertParsesTo("\\log x", "\\log x");
		assertParsesTo("\\ln x", "\\ln x");
		assertParsesTo("\\log_2 x", "\\log_2 x");
		assertParsesTo("\\log_{10} x", "\\log_{10} x");

		// 其他函数
		assertParsesTo("\\max x", "\\max x");
		assertParsesTo("\\min x", "\\min x");
		assertParsesTo("\\det A", "\\det A");

		// 验证AST结构
		try {
			Term term = getTerm("\\sin x");
			assertTrue("Atom应该是FunctionCallAtom", term.atom instanceof FunctionCallAtom);
			FunctionCallAtom func = (FunctionCallAtom) term.atom;
			assertEquals("sin", func.name);
			assertNotNull("应该有参数", func.argument);

			term = getTerm("\\log_2 x");
			func = (FunctionCallAtom) term.atom;
			assertNotNull("应该有上下标", func.supSubSuffix);
			assertNotNull("应该有下标", func.supSubSuffix.subscript);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	// ============ 9. 大型运算符测试 ============

	@Test
	public void test14_LargeOperators() {
		System.out.println("\n=== 测试 <large_operator> ===");

		assertParsesTo("\\sum", "\\sum");
		assertParsesTo("\\prod", "\\prod");
		assertParsesTo("\\int", "\\int");
		assertParsesTo("\\sum_{i=1}^{n}", "\\sum_{i=1}^{n}");
		assertParsesTo("\\int_0^1", "\\int_0^1");
		assertParsesTo("\\prod_{k=1}^{\\infty}", "\\prod_{k=1}^{\\infty}");
		assertParsesTo("\\lim_{x\\to 0}", "\\lim_{x\\to 0}");

		// 多重积分
		assertParsesTo("\\iint", "\\iint");
		assertParsesTo("\\iiint", "\\iiint");
		assertParsesTo("\\oint", "\\oint");

		// 验证AST结构
		try {
			Term term = getTerm("\\sum");
			assertTrue("Atom应该是LargeOperatorAtom", term.atom instanceof LargeOperatorAtom);
			LargeOperatorAtom op = (LargeOperatorAtom) term.atom;
			assertEquals("sum", op.name);

			term = getTerm("\\sum_{i=1}^{n}");
			op = (LargeOperatorAtom) term.atom;
			assertNotNull("应该有上下标", op.supSubSuffix);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	// ============ 10. 文本模式测试 ============

	@Test
	public void test15_Text() {
		System.out.println("\n=== 测试 <text> ===");

		assertParsesTo("\\text{hello}", "\\text{hello}");
		assertParsesTo("\\text{当x趋近于0时}", "\\text{当x趋近于0时}");
		assertParsesTo("\\mbox{test}", "\\mbox{test}");
		assertParsesTo("\\textrm{roman}", "\\textrm{roman}");
		assertParsesTo("\\textit{italic}", "\\textit{italic}");
		assertParsesTo("\\textbf{bold}", "\\textbf{bold}");

		// 验证AST结构
		try {
			Term term = getTerm("\\text{hello}");
			assertTrue("Atom应该是TextAtom", term.atom instanceof TextAtom);
			TextAtom text = (TextAtom) term.atom;
			assertEquals("text", text.command);
			assertEquals("hello", text.content);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	// ============ 11. 重音符号测试 ============

	@Test
	public void test16_Accents() {
		System.out.println("\n=== 测试 <accent> ===");

		assertParsesTo("\\hat{x}", "\\hat{x}");
		assertParsesTo("\\vec{v}", "\\vec{v}");
		assertParsesTo("\\bar{x}", "\\bar{x}");
		assertParsesTo("\\tilde{x}", "\\tilde{x}");
		assertParsesTo("\\dot{x}", "\\dot{x}");
		assertParsesTo("\\ddot{x}", "\\ddot{x}");
		assertParsesTo("\\widehat{xyz}", "\\widehat{xyz}");
		assertParsesTo("\\overline{x+y}", "\\overline{x+y}");

		// single_token形式
		assertParsesTo("\\hat x", "\\hat x");

		// 验证AST结构
		try {
			Term term = getTerm("\\hat{x}");
			assertTrue("Atom应该是AccentAtom", term.atom instanceof AccentAtom);
			AccentAtom accent = (AccentAtom) term.atom;
			assertEquals("hat", accent.cmd);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	// ============ 12. 字体命令测试 ============

	@Test
	public void test17_FontCommands() {
		System.out.println("\n=== 测试 <font_command> ===");

		assertParsesTo("\\mathbf{x}", "\\mathbf{x}");
		assertParsesTo("\\mathrm{ABC}", "\\mathrm{ABC}");
		assertParsesTo("\\mathit{text}", "\\mathit{text}");
		assertParsesTo("\\mathbb{R}", "\\mathbb{R}");
		assertParsesTo("\\mathcal{L}", "\\mathcal{L}");
		assertParsesTo("\\mathfrak{g}", "\\mathfrak{g}");
		assertParsesTo("\\boldsymbol{\\alpha}", "\\boldsymbol{\\alpha}");

		// 验证AST结构
		try {
			Term term = getTerm("\\mathbf{x}");
			assertTrue("Atom应该是FontAtom", term.atom instanceof FontAtom);
			FontAtom font = (FontAtom) term.atom;
			assertEquals("mathbf", font.cmd);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	// ============ 13. 空格命令测试 ============

	@Test
	public void test18_Spacing() {
		System.out.println("\n=== 测试 <spacing> ===");

		assertParsesTo("a\\quad b", "a\\quad b");
		assertParsesTo("a\\qquad b", "a\\qquad b");
		assertParsesTo("a\\, b", "a\\, b");
		assertParsesTo("a\\: b", "a\\: b");
		assertParsesTo("a\\; b", "a\\; b");
		assertParsesTo("a\\! b", "a\\! b");

		// 验证AST结构：空格命令是MathList的独立元素
		try {
			MathList ast = parse("a\\quad b");
			assertEquals("应该有3个元素", 3, ast.elements.size());
			assertTrue("第1个应该是Expression", ast.elements.get(0) instanceof Expression);
			assertTrue("第2个应该是Spacing", ast.elements.get(1) instanceof Spacing);
			assertTrue("第3个应该是Expression", ast.elements.get(2) instanceof Expression);

			Spacing spacing = (Spacing) ast.elements.get(1);
			assertEquals("quad", spacing.cmd);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	// ============ 14. 特殊符号测试 ============

	@Test
	public void test19_SpecialSymbols() {
		System.out.println("\n=== 测试 <special_symbol> ===");

		// 省略号
		assertParsesTo("\\dots", "\\dots");
		assertParsesTo("\\ldots", "\\ldots");
		assertParsesTo("\\cdots", "\\cdots");
		assertParsesTo("\\vdots", "\\vdots");
		assertParsesTo("\\ddots", "\\ddots");

		// 角度符号
		assertParsesTo("\\angle", "\\angle");
		assertParsesTo("\\angle ABC", "\\angle ABC");

		// 逻辑符号
		assertParsesTo("\\therefore", "\\therefore");
		assertParsesTo("\\because", "\\because");

		// 特殊符号可以带上下标
		assertParsesTo("\\angle_1", "\\angle_1");
		assertParsesTo("\\dots^{n}", "\\dots^{n}");

		// 验证AST结构
		try {
			Term term = getTerm("\\dots");
			assertTrue("Atom应该是SpecialSymbolAtom", term.atom instanceof SpecialSymbolAtom);
			SpecialSymbolAtom special = (SpecialSymbolAtom) term.atom;
			assertEquals("dots", special.symbol);

			// 验证特殊符号不能有一元运算符
			assertNull("特殊符号不应该有一元运算符", term.unaryOp);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test20_SpecialSymbolsNoUnaryOp() {
		System.out.println("\n=== 测试特殊符号不能被一元运算符修饰 ===");

		assertParseFails("-\\dots");
		assertParseFails("+\\angle");
		assertParseFails("-\\therefore");
		assertParseFails("\\pm\\cdots");
	}

	// ============ 15. 复杂表达式测试 ============

	@Test
	public void test21_ComplexExpressions() {
		System.out.println("\n=== 测试复杂表达式 ===");

		// 二次公式
		assertParsesTo(
				"\\frac{-b\\pm\\sqrt{b^2-4ac}}{2a}",
				"\\frac{-b\\pm \\sqrt{b^2-4 ac}}{2 a}"
		);

		// 极限
		assertParsesTo(
				"\\lim_{x\\to 0}\\frac{\\sin x}{x}",
				"\\lim_{x\\to 0} \\frac{\\sin x}{x}"
		);

		// 求和
		assertParsesTo(
				"\\sum_{n=1}^{\\infty}\\frac{1}{n^2}",
				"\\sum_{n=1}^{\\infty} \\frac{1}{n^2}"
		);

		// 欧拉公式
		assertParsesTo("e^{i\\pi}+1=0", "e^{i \\pi}+1=0");

		// 勾股定理
		assertParsesTo("a^2+b^2=c^2", "a^2+b^2=c^2");
	}

	@Test
	public void test22_NestedStructures() {
		System.out.println("\n=== 测试嵌套结构 ===");

		// 嵌套分式
		assertParsesTo("\\frac{1}{1+\\frac{1}{x}}", "\\frac{1}{1+\\frac{1}{x}}");

		// 嵌套根式
		assertParsesTo("\\sqrt{1+\\sqrt{x}}", "\\sqrt{1+\\sqrt{x}}");

		// 嵌套上下标
		assertParsesTo("x^{a^2}", "x^{a^2}");
		assertParsesTo("x_{a_1}", "x_{a_1}");

		// 嵌套定界符
		assertParsesTo("\\left(\\left(x\\right)\\right)", "\\left( \\left( x \\right) \\right)");
	}

	// ============ 16. 歧义性测试 ============

	@Test
	public void test23_AmbiguousCases() {
		System.out.println("\n=== 测试歧义性情况 ===");

		// 一元 vs 二元运算符
		assertParsesTo("a+-b", "a+-b");   // a + (-b)
		assertParsesTo("a-+b", "a-+b");   // a - (+b)
		assertParsesTo("a--b", "a--b");   // a - (-b)

		// \pm 和 \mp 的双重身份
		assertParsesTo("\\pm a", "\\pm a");         // 一元
		assertParsesTo("a\\pm b", "a\\pm b");       // 二元
		assertParsesTo("a\\pm\\pm b", "a\\pm \\pm b"); // 二元 + 一元

		// 上下标优先级
		assertParsesTo("x^2+1", "x^2+1");     // x^2 + 1
		assertParsesTo("x^{2+1}", "x^{2+1}");   // x^(2+1)

		// 逗号
		assertParsesTo("a,b,c", "a,b,c");
		assertParsesTo("f\\left(x,y\\right)", "f \\left( x,y \\right)");
	}

	// ============ 17. 错误处理测试 ============

	@Test
	public void test24_ErrorCases() {
		System.out.println("\n=== 测试错误情况 ===");

		// 不完整的结构
		assertParseFails("\\frac{1}{");
		assertParseFails("\\sqrt[");
		assertParseFails("\\left(x");
		assertParseFails("{x");

		// 二元运算符后面没有操作数
		assertParseFails("a+");
		assertParseFails("a-");
		assertParseFails("a*");
		assertParseFails("a/");

		// 未知命令
		assertParseFails("\\unknowncommand");

		// 连续的上下标符号
		assertParseFails("x^^2");
		assertParseFails("x__2");
	}

	// ============ 18. BNF完整覆盖验证 ============

	@Test
	public void test25_BnfCompleteCoverage() {
		System.out.println("\n=== BNF完整覆盖验证 ===");

		// operand_atom的所有类型
		System.out.println("验证operand_atom...");
		assertAstType("123", Expression.class);              // number
		assertAstType("x", Expression.class);                // variable
		assertAstType("\\alpha", Expression.class);          // greek_letter
		assertAstType("{x}", Expression.class);              // group
		assertAstType("\\frac{a}{b}", Expression.class);     // frac
		assertAstType("\\sqrt{x}", Expression.class);        // sqrt
		assertAstType("\\left(x\\right)", Expression.class); // delimited
		assertAstType("\\sin x", Expression.class);          // function_call
		assertAstType("\\sum", Expression.class);            // large_operator
		assertAstType("\\text{hi}", Expression.class);       // text
		assertAstType("\\hat{x}", Expression.class);         // accent
		assertAstType("\\mathbf{x}", Expression.class);      // font_command

		// special_symbol的所有类型
		System.out.println("验证special_symbol...");
		assertAstType("\\dots", Expression.class);
		assertAstType("\\angle", Expression.class);
		assertAstType("\\therefore", Expression.class);

		// 运算符覆盖
		System.out.println("验证运算符...");
		assertParsesTo("-x", "-x");              // unary_op
		assertParsesTo("a+b", "a+b");            // binary_op

		// 上下标覆盖
		System.out.println("验证上下标...");
		assertParsesTo("x^a", "x^a");            // 仅上标
		assertParsesTo("x_a", "x_a");            // 仅下标
		assertParsesTo("x^a_b", "x^a_b");        // 上下标组合

		// 空格命令覆盖
		System.out.println("验证空格命令...");
		assertAstType("a\\quad b", Expression.class);

		System.out.println("\n✅ BNF完整覆盖验证通过!");
	}

	@Test
	public void test26_AllGreekLetters() {
		System.out.println("\n=== 测试所有希腊字母 ===");

		// 小写希腊字母（完整列表）
		String[] lowercase = {
				"\\alpha", "\\beta", "\\gamma", "\\delta", "\\epsilon", "\\varepsilon",
				"\\zeta", "\\eta", "\\theta", "\\vartheta", "\\iota", "\\kappa",
				"\\lambda", "\\mu", "\\nu", "\\xi", "\\pi", "\\varpi", "\\rho", "\\varrho",
				"\\sigma", "\\varsigma", "\\tau", "\\upsilon", "\\phi", "\\varphi",
				"\\chi", "\\psi", "\\omega"
		};

		for (String letter : lowercase) {
			assertParsesTo(letter, letter);
		}

		// 大写希腊字母（完整列表）
		String[] uppercase = {
				"\\Gamma", "\\Delta", "\\Theta", "\\Lambda", "\\Xi", "\\Pi",
				"\\Sigma", "\\Upsilon", "\\Phi", "\\Psi", "\\Omega"
		};

		for (String letter : uppercase) {
			assertParsesTo(letter, letter);
		}

		assertParsesTo("\\infty", "\\infty");

		System.out.println("✅ 所有希腊字母测试通过");
	}

	@Test
	public void test27_AllBinaryOperators() {
		System.out.println("\n=== 测试所有二元运算符 ===");

		String[] operators = {
				// 算术运算符
				"+", "-", "*", "/", ",",
				"\\times", "\\cdot", "\\div", "\\pm", "\\mp",
				// 关系运算符
				"=", "\\neq", "\\equiv", "\\approx", "\\cong", "\\sim",
				"<", ">", "\\le", "\\ge", "\\leq", "\\geq", "\\ll", "\\gg",
				// 集合运算符
				"\\in", "\\notin", "\\subset", "\\supset", "\\subseteq", "\\supseteq",
				"\\cup", "\\cap", "\\wedge", "\\vee",
				// 箭头
				"\\to", "\\rightarrow", "\\leftarrow", "\\leftrightarrow",
				"\\Rightarrow", "\\Leftarrow", "\\Leftrightarrow",
				"\\implies", "\\iff",
				// 几何关系
				"\\perp", "\\parallel"
		};

		for (String op : operators) {
			if (op.startsWith("\\")) {
				assertParsesTo("a" + op + " b", "a" + op + " b");
			} else {
				assertParsesTo("a" + op + "b", "a" + op + "b");
			}
		}

		System.out.println("✅ 所有二元运算符测试通过");
	}

	@Test
	public void test28_AllFunctions() {
		System.out.println("\n=== 测试所有函数 ===");

		String[] functions = {
				// 三角函数
				"\\sin", "\\cos", "\\tan", "\\cot", "\\sec", "\\csc",
				"\\arcsin", "\\arccos", "\\arctan",
				// 双曲函数
				"\\sinh", "\\cosh", "\\tanh", "\\coth",
				// 对数函数
				"\\log", "\\ln", "\\lg", "\\exp",
				// 其他函数
				"\\max", "\\min", "\\sup", "\\inf",
				"\\arg", "\\deg", "\\det", "\\dim", "\\gcd", "\\hom", "\\ker",
				"\\Pr", "\\bmod", "\\pmod"
		};

		for (String func : functions) {
			assertParsesTo(func + " x", func + " x");
		}

		System.out.println("✅ 所有函数测试通过");
	}

	@Test
	public void test29_AllLargeOperators() {
		System.out.println("\n=== 测试所有大型运算符 ===");

		String[] operators = {
				"\\sum", "\\prod", "\\coprod",
				"\\int", "\\iint", "\\iiint", "\\oint", "\\oiint", "\\oiiint",
				"\\bigcup", "\\bigcap", "\\bigvee", "\\bigwedge",
				"\\bigoplus", "\\bigotimes", "\\bigodot",
				"\\biguplus", "\\bigsqcup",
				"\\lim", "\\limsup", "\\liminf"
		};

		for (String op : operators) {
			assertParsesTo(op, op);
		}

		System.out.println("✅ 所有大型运算符测试通过");
	}

	@Test
	public void test30_AllDelimiters() {
		System.out.println("\n=== 测试所有定界符 ===");

		// 所有定界符类型
		String[][] delimiters = {
				{"(", ")"},
				{"[", "]"},
				{"\\{", "\\}"},
				{"|", "|"},
				{"\\|", "\\|"},
				{"\\langle", "\\rangle"},
				{"\\lfloor", "\\rfloor"},
				{"\\lceil", "\\rceil"},
				{"\\lvert", "\\rvert"},
				{"\\lVert", "\\rVert"},
				{".", "|"}  // 空定界符
		};

		for (String[] pair : delimiters) {
			String expr = "\\left" + pair[0] + " x \\right" + pair[1];
			if ("\\{".equals(pair[0])) {
				assertParsesTo(expr, "\\left{ x \\right}");
			} else if ("\\|".equals(pair[0])) {
				assertParsesTo(expr, "\\left| x \\right|");
			} else {
				assertParsesTo(expr, "\\left" + pair[0] + " x \\right" + pair[1]);
			}
		}

		System.out.println("✅ 所有定界符测试通过");
	}

	@Test
	public void test31_RealWorldExpressions() {
		System.out.println("\n=== 测试真实世界表达式 ===");

		// 二项式定理
		assertParsesTo(
				"\\left(a+b\\right)^n=\\sum_{k=0}^{n}C_n^k a^{n-k}b^k",
				"\\left( a+b \\right)^n=\\sum_{k=0}^{n} C_n^k a^{n-k} b^k"
		);

		// 导数定义
		assertParsesTo(
				"f'\\left(x\\right)=\\lim_{h\\to 0}\\frac{f\\left(x+h\\right)-f\\left(x\\right)}{h}",
				"f' \\left( x \\right)=\\lim_{h\\to 0} \\frac{f \\left( x+h \\right)-f \\left( x \\right)}{h}"
		);

		// 积分
		assertParsesTo(
				"\\int_0^{\\infty}e^{-x^2}",
				"\\int_0^{\\infty} e^{-x^2}"
		);

		// 几何推理
		assertParsesTo(
				"AB\\perp CD\\therefore\\angle ABC=90",
				"AB\\perp CD \\therefore \\angle ABC=90"
		);

		System.out.println("✅ 真实世界表达式测试通过");
	}
}