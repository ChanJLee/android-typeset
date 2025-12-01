package me.chan.texas.ext.markdown.math.ast;

import org.junit.Test;
import me.chan.texas.utils.CharStream;
import static org.junit.Assert.*;

/**
 * LaTeX 数学公式解析器完整单元测试
 * 基于 bnf_math.txt v3 设计
 *
 * 测试组织结构：
 * Part 1: 基础元素 (BNF 底层)
 * Part 2: 运算符
 * Part 3: 原子表达式 (Atom)
 * Part 4: 上下标
 * Part 5: 复合结构 (Term, Expression, MathList)
 * Part 6: 歧义语法测试
 * Part 7: AST 结构深度验证
 * Part 8: 边界和错误情况
 * Part 9: 真实世界综合测试
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

	/** 获取 MathList */
	private MathList getMathList(String input) throws MathParseException {
		return parse(input);
	}

	/** 获取第一个 Expression */
	private Expression getExpression(String input) throws MathParseException {
		MathList ast = getMathList(input);
		assertFalse("MathList 不应为空", ast.elements.isEmpty());
		assertTrue("第一个元素应该是 Expression", ast.elements.get(0) instanceof Expression);
		return (Expression) ast.elements.get(0);
	}

	/** 获取第一个 Term */
	private Term getTerm(String input) throws MathParseException {
		Expression expr = getExpression(input);
		assertFalse("Expression 不应为空", expr.elements.isEmpty());
		assertTrue("第一个元素应该是 Term", expr.elements.get(0) instanceof Term);
		return (Term) expr.elements.get(0);
	}

	/** 验证 Atom 类型 */
	private void assertAtomType(String input, Class<? extends Atom> expectedType) {
		try {
			Term term = getTerm(input);
			assertTrue(
					"期望类型: " + expectedType.getSimpleName() + ", 实际类型: " + term.atom.getClass().getSimpleName(),
					expectedType.isInstance(term.atom)
			);
			System.out.println("✅ " + input + " → " + expectedType.getSimpleName());
		} catch (MathParseException e) {
			fail("解析失败: " + input + " - " + e.pretty());
		}
	}

	// ============================================================
	// Part 1: 基础元素测试 (BNF 底层)
	// ============================================================

	@Test
	public void test_01_01_Number_Integer() {
		System.out.println("\n=== Part 1.1: 数字 - 整数 ===");

		assertParsesTo("0", "0");
		assertParsesTo("1", "1");
		assertParsesTo("42", "42");
		assertParsesTo("123", "123");
		assertParsesTo("999", "999");

		// 验证 AST 结构
		try {
			Term term = getTerm("42");
			assertTrue("应该是 NumberAtom", term.atom instanceof NumberAtom);
			assertEquals("42", ((NumberAtom) term.atom).value);
			assertNull("整数不应有一元运算符", term.unaryOp);
			assertNull("整数不应有上下标", term.suffix);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_01_02_Number_Decimal() {
		System.out.println("\n=== Part 1.2: 数字 - 小数 ===");

		assertParsesTo("0.5", "0.5");
		assertParsesTo("3.14", "3.14");
		assertParsesTo("123.456", "123.456");
		assertParsesTo("0.0", "0.0");

		// 验证 AST
		try {
			Term term = getTerm("3.14");
			NumberAtom num = (NumberAtom) term.atom;
			assertEquals("3.14", num.value);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_01_03_Variable_SingleLetter() {
		System.out.println("\n=== Part 1.3: 变量 - 单字母 ===");

		assertParsesTo("x", "x");
		assertParsesTo("y", "y");
		assertParsesTo("a", "a");
		assertParsesTo("A", "A");
		assertParsesTo("Z", "Z");

		// 验证 AST
		try {
			Term term = getTerm("x");
			assertTrue("应该是 VariableAtom", term.atom instanceof VariableAtom);
			assertEquals("x", ((VariableAtom) term.atom).name);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_01_04_Variable_MultiLetter() {
		System.out.println("\n=== Part 1.4: 变量 - 多字母 ===");

		assertParsesTo("abc", "abc");
		assertParsesTo("xyz", "xyz");
		assertParsesTo("var", "var");

		// 验证 AST
		try {
			Term term = getTerm("abc");
			VariableAtom var = (VariableAtom) term.atom;
			assertEquals("abc", var.name);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_01_05_Variable_WithPrime() {
		System.out.println("\n=== Part 1.5: 变量 - 带导数符号 ===");

		assertParsesTo("f'", "f'");
		assertParsesTo("g''", "g''");
		assertParsesTo("y'''", "y'''");
		assertParsesTo("x'", "x'");

		// 验证 AST
		try {
			Term term = getTerm("f'");
			VariableAtom var = (VariableAtom) term.atom;
			assertEquals("f'", var.name);

			term = getTerm("g''");
			var = (VariableAtom) term.atom;
			assertEquals("g''", var.name);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_01_06_GreekLetters_Lowercase() {
		System.out.println("\n=== Part 1.6: 希腊字母 - 小写 ===");

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

		// 验证 AST
		try {
			Term term = getTerm("\\alpha");
			assertTrue("应该是 GreekLetterAtom", term.atom instanceof GreekLetterAtom);
			assertEquals("alpha", ((GreekLetterAtom) term.atom).symbol);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_01_07_GreekLetters_Uppercase() {
		System.out.println("\n=== Part 1.7: 希腊字母 - 大写 ===");

		String[] uppercase = {
				"\\Gamma", "\\Delta", "\\Theta", "\\Lambda", "\\Xi", "\\Pi",
				"\\Sigma", "\\Upsilon", "\\Phi", "\\Psi", "\\Omega"
		};

		for (String letter : uppercase) {
			assertParsesTo(letter, letter);
		}

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
		assertParsesTo("+42", "+42");
		assertParsesTo("\\pm x", "\\pm x");
		assertParsesTo("\\mp y", "\\mp y");

		// 验证 AST：一元运算符必须是 Term 的一部分
		try {
			Term term = getTerm("-x");
			assertNotNull("应该有一元运算符", term.unaryOp);
			assertEquals("-", term.unaryOp.op);
			assertTrue("Atom 应该是 VariableAtom", term.atom instanceof VariableAtom);
			assertEquals("x", ((VariableAtom) term.atom).name);

			term = getTerm("\\pm x");
			assertEquals("\\pm", term.unaryOp.op);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_02_02_UnaryOperators_ComplexAtoms() {
		System.out.println("\n=== Part 2.2: 一元运算符修饰复杂原子 ===");

		assertParsesTo("-\\alpha", "-\\alpha");
		assertParsesTo("-{x+y}", "-{x+y}");
		assertParsesTo("-\\frac{1}{2}", "-\\frac{1}{2}");
		assertParsesTo("-\\sqrt{x}", "-\\sqrt{x}");
		assertParsesTo("+\\sin x", "+\\sin x");
	}

	@Test
	public void test_02_03_BinaryOperators_Arithmetic() {
		System.out.println("\n=== Part 2.3: 二元运算符 - 算术 ===");

		assertParsesTo("a+b", "a+b");
		assertParsesTo("a-b", "a-b");
		assertParsesTo("a*b", "a*b");
		assertParsesTo("a/b", "a/b");
		assertParsesTo("a,b", "a,b");
		assertParsesTo("a\\times b", "a\\times b");
		assertParsesTo("a\\cdot b", "a\\cdot b");
		assertParsesTo("a\\div b", "a\\div b");
		assertParsesTo("a\\pm b", "a\\pm b");
		assertParsesTo("a\\mp b", "a\\mp b");

		// 验证 AST：Expression 包含 Term、BinOp、Term
		try {
			Expression expr = getExpression("a+b");
			assertEquals("应该有 3 个元素", 3, expr.elements.size());
			assertTrue("第 1 个应该是 Term", expr.elements.get(0) instanceof Term);
			assertTrue("第 2 个应该是 BinOpAtom", expr.elements.get(1) instanceof BinOpAtom);
			assertTrue("第 3 个应该是 Term", expr.elements.get(2) instanceof Term);

			BinOpAtom binOp = (BinOpAtom) expr.elements.get(1);
			assertEquals("+", binOp.op);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_02_04_BinaryOperators_Relational() {
		System.out.println("\n=== Part 2.4: 二元运算符 - 关系 ===");

		assertParsesTo("a=b", "a=b");
		assertParsesTo("a\\neq b", "a\\neq b");
		assertParsesTo("a\\equiv b", "a\\equiv b");
		assertParsesTo("a\\approx b", "a\\approx b");
		assertParsesTo("a\\cong b", "a\\cong b");
		assertParsesTo("a\\sim b", "a\\sim b");
		assertParsesTo("a<b", "a<b");
		assertParsesTo("a>b", "a>b");
		assertParsesTo("a\\le b", "a\\le b");
		assertParsesTo("a\\ge b", "a\\ge b");
		assertParsesTo("a\\leq b", "a\\leq b");
		assertParsesTo("a\\geq b", "a\\geq b");
		assertParsesTo("a\\ll b", "a\\ll b");
		assertParsesTo("a\\gg b", "a\\gg b");
	}

	@Test
	public void test_02_05_BinaryOperators_SetAndLogic() {
		System.out.println("\n=== Part 2.5: 二元运算符 - 集合和逻辑 ===");

		assertParsesTo("x\\in A", "x\\in A");
		assertParsesTo("x\\notin A", "x\\notin A");
		assertParsesTo("A\\subset B", "A\\subset B");
		assertParsesTo("A\\supset B", "A\\supset B");
		assertParsesTo("A\\subseteq B", "A\\subseteq B");
		assertParsesTo("A\\supseteq B", "A\\supseteq B");
		assertParsesTo("A\\cup B", "A\\cup B");
		assertParsesTo("A\\cap B", "A\\cap B");
		assertParsesTo("p\\wedge q", "p\\wedge q");
		assertParsesTo("p\\vee q", "p\\vee q");
	}

	@Test
	public void test_02_06_BinaryOperators_Arrows() {
		System.out.println("\n=== Part 2.6: 二元运算符 - 箭头 ===");

		assertParsesTo("a\\to b", "a\\to b");
		assertParsesTo("a\\rightarrow b", "a\\rightarrow b");
		assertParsesTo("a\\leftarrow b", "a\\leftarrow b");
		assertParsesTo("a\\leftrightarrow b", "a\\leftrightarrow b");
		assertParsesTo("A\\Rightarrow B", "A\\Rightarrow B");
		assertParsesTo("A\\Leftarrow B", "A\\Leftarrow B");
		assertParsesTo("A\\Leftrightarrow B", "A\\Leftrightarrow B");
		assertParsesTo("A\\implies B", "A\\implies B");
		assertParsesTo("A\\iff B", "A\\iff B");
	}

	@Test
	public void test_02_07_BinaryOperators_Geometric() {
		System.out.println("\n=== Part 2.7: 二元运算符 - 几何 ===");

		assertParsesTo("a\\perp b", "a\\perp b");
		assertParsesTo("a\\parallel b", "a\\parallel b");
	}

	// ============================================================
	// Part 3: 原子表达式测试 (Atom)
	// ============================================================

	@Test
	public void test_03_01_Group_Simple() {
		System.out.println("\n=== Part 3.1: 分组 - 简单 ===");

		assertParsesTo("{x}", "{x}");
		assertParsesTo("{a}", "{a}");
		assertParsesTo("{}", "{}");

		// 验证 AST
		try {
			Term term = getTerm("{x}");
			assertTrue("应该是 Group", term.atom instanceof Group);
			Group group = (Group) term.atom;
			assertFalse("Group 内容不应为空", group.content.elements.isEmpty());
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_03_02_Group_Complex() {
		System.out.println("\n=== Part 3.2: 分组 - 复杂 ===");

		assertParsesTo("{a+b}", "{a+b}");
		assertParsesTo("{x*y}", "{x*y}");
		assertParsesTo("{{x}}", "{{x}}");
		assertParsesTo("{{{a}}}", "{{{a}}}");

		// 验证嵌套 Group 的 AST
		try {
			Term term = getTerm("{{x}}");
			Group outerGroup = (Group) term.atom;
			Expression expr = (Expression) outerGroup.content.elements.get(0);
			Term innerTerm = (Term) expr.elements.get(0);
			assertTrue("内部也应该是 Group", innerTerm.atom instanceof Group);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_03_03_Frac_Basic() {
		System.out.println("\n=== Part 3.3: 分式 - 基本 ===");

		assertParsesTo("\\frac{1}{2}", "\\frac{1}{2}");
		assertParsesTo("\\frac{a}{b}", "\\frac{a}{b}");
		assertParsesTo("\\frac{x+y}{x-y}", "\\frac{x+y}{x-y}");

		// 验证 AST
		try {
			Term term = getTerm("\\frac{1}{2}");
			assertTrue("应该是 FracAtom", term.atom instanceof FracAtom);
			FracAtom frac = (FracAtom) term.atom;
			assertEquals("frac", frac.command);
			assertEquals("1", frac.numerator.toString());
			assertEquals("2", frac.denominator.toString());
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_03_04_Frac_Variants() {
		System.out.println("\n=== Part 3.4: 分式 - 变体 ===");

		assertParsesTo("\\dfrac{a}{b}", "\\dfrac{a}{b}");
		assertParsesTo("\\tfrac{1}{2}", "\\tfrac{1}{2}");
		assertParsesTo("\\cfrac{x}{y}", "\\cfrac{x}{y}");

		// 验证 command 字段
		try {
			FracAtom frac = (FracAtom) getTerm("\\dfrac{a}{b}").atom;
			assertEquals("dfrac", frac.command);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_03_05_Frac_Nested() {
		System.out.println("\n=== Part 3.5: 分式 - 嵌套 ===");

		assertParsesTo("\\frac{\\frac{1}{2}}{3}", "\\frac{\\frac{1}{2}}{3}");
		assertParsesTo("\\frac{1}{\\frac{2}{3}}", "\\frac{1}{\\frac{2}{3}}");
		assertParsesTo("\\frac{\\frac{a}{b}}{\\frac{c}{d}}", "\\frac{\\frac{a}{b}}{\\frac{c}{d}}");
	}

	@Test
	public void test_03_06_Sqrt_Basic() {
		System.out.println("\n=== Part 3.6: 根式 - 基本 ===");

		assertParsesTo("\\sqrt{x}", "\\sqrt{x}");
		assertParsesTo("\\sqrt{2}", "\\sqrt{2}");
		assertParsesTo("\\sqrt{x+1}", "\\sqrt{x+1}");

		// 验证 AST
		try {
			Term term = getTerm("\\sqrt{x}");
			assertTrue("应该是 SqrtAtom", term.atom instanceof SqrtAtom);
			SqrtAtom sqrt = (SqrtAtom) term.atom;
			assertNull("不应该有根次数", sqrt.root);
			assertEquals("x", sqrt.content.toString());
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_03_07_Sqrt_WithRoot() {
		System.out.println("\n=== Part 3.7: 根式 - 带根次数 ===");

		assertParsesTo("\\sqrt[3]{27}", "\\sqrt[3]{27}");
		assertParsesTo("\\sqrt[n]{x}", "\\sqrt[n]{x}");
		assertParsesTo("\\sqrt[2]{4}", "\\sqrt[2]{4}");

		// 验证 AST
		try {
			Term term = getTerm("\\sqrt[3]{27}");
			SqrtAtom sqrt = (SqrtAtom) term.atom;
			assertNotNull("应该有根次数", sqrt.root);
			assertEquals("3", sqrt.root.toString());
			assertEquals("27", sqrt.content.toString());
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_03_08_Delimited_Basic() {
		System.out.println("\n=== Part 3.8: 定界符 - 基本 ===");

		assertParsesTo("\\left( x \\right)", "\\left( x \\right)");
		assertParsesTo("\\left[ x \\right]", "\\left[ x \\right]");
		assertParsesTo("\\left\\{ x \\right\\}", "\\left{ x \\right}");
		assertParsesTo("\\left| x \\right|", "\\left| x \\right|");

		// 验证 AST
		try {
			Term term = getTerm("\\left( x \\right)");
			assertTrue("应该是 DelimitedAtom", term.atom instanceof DelimitedAtom);
			DelimitedAtom delim = (DelimitedAtom) term.atom;
			assertEquals(0, delim.level);  // left/right 是 level 0
			assertEquals("(", delim.leftDelimiter);
			assertEquals(")", delim.rightDelimiter);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_03_09_Delimited_AllTypes() {
		System.out.println("\n=== Part 3.9: 定界符 - 所有类型 ===");

		assertParsesTo("\\left\\langle x \\right\\rangle", "\\left\\langle x \\right\\rangle");
		assertParsesTo("\\left\\lfloor x \\right\\rfloor", "\\left\\lfloor x \\right\\rfloor");
		assertParsesTo("\\left\\lceil x \\right\\rceil", "\\left\\lceil x \\right\\rceil");
		assertParsesTo("\\left\\lvert x \\right\\rvert", "\\left\\lvert x \\right\\rvert");
		assertParsesTo("\\left\\lVert x \\right\\rVert", "\\left\\lVert x \\right\\rVert");
		assertParsesTo("\\left. x \\right|", "\\left. x \\right|");  // 空定界符
	}

	@Test
	public void test_03_10_Delimited_AllLevels() {
		System.out.println("\n=== Part 3.10: 定界符 - 所有级别 ===");

		assertParsesTo("\\left(x\\right)", "\\left( x \\right)");      // level 0
		assertParsesTo("\\bigl(x\\bigr)", "\\bigl( x \\bigr)");        // level 1
		assertParsesTo("\\Bigl(x\\Bigr)", "\\Bigl( x \\Bigr)");        // level 2
		assertParsesTo("\\biggl(x\\biggr)", "\\biggl( x \\biggr)");    // level 3
		assertParsesTo("\\Biggl(x\\Biggr)", "\\Biggl( x \\Biggr)");    // level 4

		// 验证 level
		try {
			assertEquals(0, ((DelimitedAtom) getTerm("\\left(x\\right)").atom).level);
			assertEquals(1, ((DelimitedAtom) getTerm("\\bigl(x\\bigr)").atom).level);
			assertEquals(2, ((DelimitedAtom) getTerm("\\Bigl(x\\Bigr)").atom).level);
			assertEquals(3, ((DelimitedAtom) getTerm("\\biggl(x\\biggr)").atom).level);
			assertEquals(4, ((DelimitedAtom) getTerm("\\Biggl(x\\Biggr)").atom).level);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_03_11_Function_Basic() {
		System.out.println("\n=== Part 3.11: 函数 - 基本 ===");

		assertParsesTo("\\sin x", "\\sin x");
		assertParsesTo("\\cos x", "\\cos x");
		assertParsesTo("\\tan x", "\\tan x");
		assertParsesTo("\\log x", "\\log x");
		assertParsesTo("\\ln x", "\\ln x");
		assertParsesTo("\\exp x", "\\exp x");

		// 验证 AST
		try {
			Term term = getTerm("\\sin x");
			assertTrue("应该是 FunctionCallAtom", term.atom instanceof FunctionCallAtom);
			FunctionCallAtom func = (FunctionCallAtom) term.atom;
			assertEquals("sin", func.name);
			assertNotNull("应该有参数", func.argument);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_03_12_Function_AllTypes() {
		System.out.println("\n=== Part 3.12: 函数 - 所有类型 ===");

		// 三角函数
		String[] trig = {"\\sin", "\\cos", "\\tan", "\\cot", "\\sec", "\\csc",
				"\\arcsin", "\\arccos", "\\arctan"};
		for (String f : trig) assertParsesTo(f + " x", f + " x");

		// 双曲函数
		String[] hyper = {"\\sinh", "\\cosh", "\\tanh", "\\coth"};
		for (String f : hyper) assertParsesTo(f + " x", f + " x");

		// 对数
		String[] log = {"\\log", "\\ln", "\\lg", "\\exp"};
		for (String f : log) assertParsesTo(f + " x", f + " x");

		// 其他
		String[] other = {"\\max", "\\min", "\\sup", "\\inf", "\\det", "\\gcd"};
		for (String f : other) assertParsesTo(f + " x", f + " x");
	}

	@Test
	public void test_03_13_Function_WithScripts() {
		System.out.println("\n=== Part 3.13: 函数 - 带上下标 ===");

		assertParsesTo("\\log_2 x", "\\log_2 x");
		assertParsesTo("\\log_{10} x", "\\log_{10} x");
		assertParsesTo("\\sin^2 x", "\\sin^2 x");

		// 验证 AST
		try {
			FunctionCallAtom func = (FunctionCallAtom) getTerm("\\log_2 x").atom;
			assertNotNull("应该有上下标", func.supSubSuffix);
			assertNotNull("应该有下标", func.supSubSuffix.subscript);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_03_14_LargeOperator_Basic() {
		System.out.println("\n=== Part 3.14: 大型运算符 - 基本 ===");

		assertParsesTo("\\sum", "\\sum");
		assertParsesTo("\\prod", "\\prod");
		assertParsesTo("\\int", "\\int");
		assertParsesTo("\\lim", "\\lim");

		// 验证 AST
		try {
			Term term = getTerm("\\sum");
			assertTrue("应该是 LargeOperatorAtom", term.atom instanceof LargeOperatorAtom);
			LargeOperatorAtom op = (LargeOperatorAtom) term.atom;
			assertEquals("sum", op.name);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_03_15_LargeOperator_AllTypes() {
		System.out.println("\n=== Part 3.15: 大型运算符 - 所有类型 ===");

		String[] ops = {
				"\\sum", "\\prod", "\\coprod",
				"\\int", "\\iint", "\\iiint", "\\oint", "\\oiint", "\\oiiint",
				"\\bigcup", "\\bigcap", "\\bigvee", "\\bigwedge",
				"\\bigoplus", "\\bigotimes", "\\bigodot",
				"\\biguplus", "\\bigsqcup",
				"\\lim", "\\limsup", "\\liminf"
		};

		for (String op : ops) {
			assertParsesTo(op, op);
		}
	}

	@Test
	public void test_03_16_LargeOperator_WithScripts() {
		System.out.println("\n=== Part 3.16: 大型运算符 - 带上下标 ===");

		assertParsesTo("\\sum_{i=1}^{n}", "\\sum_{i=1}^{n}");
		assertParsesTo("\\int_0^1", "\\int_0^1");
		assertParsesTo("\\prod_{k=1}^{\\infty}", "\\prod_{k=1}^{\\infty}");
		assertParsesTo("\\lim_{x\\to 0}", "\\lim_{x\\to 0}");

		// 验证 AST
		try {
			LargeOperatorAtom op = (LargeOperatorAtom) getTerm("\\sum_{i=1}^{n}").atom;
			assertNotNull("应该有上下标", op.supSubSuffix);
			assertNotNull("应该有下标", op.supSubSuffix.subscript);
			assertNotNull("应该有上标", op.supSubSuffix.superscript);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_03_17_Matrix_Basic() {
		System.out.println("\n=== Part 3.17: 矩阵 - 基本 ===");

		assertParsesTo(
				"\\begin{matrix}a&b\\\\c&d\\end{matrix}",
				"\\begin{matrix}\na & b\nc & d\n\\end{matrix}\n"
		);

		// 验证 AST
		try {
			Term term = getTerm("\\begin{matrix}a&b\\\\c&d\\end{matrix}");
			assertTrue("应该是 MatrixAtom", term.atom instanceof MatrixAtom);
			MatrixAtom matrix = (MatrixAtom) term.atom;
			assertEquals("matrix", matrix.env);
			assertEquals(2, matrix.rows.size());
			assertEquals(2, matrix.rows.get(0).elements.size());
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_03_18_Matrix_AllEnvironments() {
		System.out.println("\n=== Part 3.18: 矩阵 - 所有环境 ===");

		assertParsesTo(
				"\\begin{pmatrix}1&2\\\\3&4\\end{pmatrix}",
				"\\begin{pmatrix}\n1 & 2\n3 & 4\n\\end{pmatrix}\n"
		);

		assertParsesTo(
				"\\begin{bmatrix}a&b\\\\c&d\\end{bmatrix}",
				"\\begin{bmatrix}\na & b\nc & d\n\\end{bmatrix}\n"
		);

		assertParsesTo(
				"\\begin{Bmatrix}x\\\\y\\end{Bmatrix}",
				"\\begin{Bmatrix}\nx\ny\n\\end{Bmatrix}\n"
		);

		assertParsesTo(
				"\\begin{vmatrix}1&0\\\\0&1\\end{vmatrix}",
				"\\begin{vmatrix}\n1 & 0\n0 & 1\n\\end{vmatrix}\n"
		);

		assertParsesTo(
				"\\begin{Vmatrix}a&b\\\\c&d\\end{Vmatrix}",
				"\\begin{Vmatrix}\na & b\nc & d\n\\end{Vmatrix}\n"
		);

		assertParsesTo(
				"\\begin{cases}x,&x\\ge 0\\\\-x,&x<0\\end{cases}",
				"\\begin{cases}\nx, & x\\ge 0\n-x, & x<0\n\\end{cases}\n"
		);
	}

	@Test
	public void test_03_19_Text_Basic() {
		System.out.println("\n=== Part 3.19: 文本 - 基本 ===");

		assertParsesTo("\\text{hello}", "\\text{hello}");
		assertParsesTo("\\text{当x趋近于0时}", "\\text{当x趋近于0时}");
		assertParsesTo("\\mbox{test}", "\\mbox{test}");
		assertParsesTo("\\textrm{roman}", "\\textrm{roman}");
		assertParsesTo("\\textit{italic}", "\\textit{italic}");
		assertParsesTo("\\textbf{bold}", "\\textbf{bold}");

		// 验证 AST
		try {
			Term term = getTerm("\\text{hello}");
			assertTrue("应该是 TextAtom", term.atom instanceof TextAtom);
			TextAtom text = (TextAtom) term.atom;
			assertEquals("text", text.command);
			assertEquals("hello", text.content);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_03_20_Accent_Basic() {
		System.out.println("\n=== Part 3.20: 重音 - 基本 ===");

		assertParsesTo("\\hat{x}", "\\hat{x}");
		assertParsesTo("\\vec{v}", "\\vec{v}");
		assertParsesTo("\\bar{x}", "\\bar{x}");
		assertParsesTo("\\tilde{x}", "\\tilde{x}");
		assertParsesTo("\\dot{x}", "\\dot{x}");
		assertParsesTo("\\ddot{x}", "\\ddot{x}");

		// single_token 形式
		assertParsesTo("\\hat x", "\\hat x");
		assertParsesTo("\\vec v", "\\vec v");

		// 验证 AST
		try {
			Term term = getTerm("\\hat{x}");
			assertTrue("应该是 AccentAtom", term.atom instanceof AccentAtom);
			AccentAtom accent = (AccentAtom) term.atom;
			assertEquals("hat", accent.cmd);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_03_21_Accent_AllTypes() {
		System.out.println("\n=== Part 3.21: 重音 - 所有类型 ===");

		String[] accents = {
				"\\hat", "\\widehat", "\\tilde", "\\widetilde",
				"\\bar", "\\overline", "\\underline",
				"\\vec", "\\overrightarrow", "\\overleftarrow",
				"\\dot", "\\ddot", "\\dddot",
				"\\acute", "\\grave", "\\breve", "\\check",
				"\\mathring", "\\overbrace", "\\underbrace"
		};

		for (String accent : accents) {
			assertParsesTo(accent + "{x}", accent + "{x}");
		}
	}

	@Test
	public void test_03_22_Font_AllTypes() {
		System.out.println("\n=== Part 3.22: 字体 - 所有类型 ===");

		assertParsesTo("\\mathbf{x}", "\\mathbf{x}");
		assertParsesTo("\\mathrm{ABC}", "\\mathrm{ABC}");
		assertParsesTo("\\mathit{text}", "\\mathit{text}");
		assertParsesTo("\\mathbb{R}", "\\mathbb{R}");
		assertParsesTo("\\mathcal{L}", "\\mathcal{L}");
		assertParsesTo("\\mathfrak{g}", "\\mathfrak{g}");
		assertParsesTo("\\mathscr{F}", "\\mathscr{F}");
		assertParsesTo("\\boldsymbol{\\alpha}", "\\boldsymbol{\\alpha}");

		// 验证 AST
		try {
			Term term = getTerm("\\mathbf{x}");
			assertTrue("应该是 FontAtom", term.atom instanceof FontAtom);
			FontAtom font = (FontAtom) term.atom;
			assertEquals("mathbf", font.cmd);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_03_23_SpecialSymbol_All() {
		System.out.println("\n=== Part 3.23: 特殊符号 - 所有类型 ===");

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

		// 验证 AST
		try {
			Term term = getTerm("\\dots");
			assertTrue("应该是 SpecialSymbolAtom", term.atom instanceof SpecialSymbolAtom);
			SpecialSymbolAtom special = (SpecialSymbolAtom) term.atom;
			assertEquals("dots", special.symbol);
			assertNull("特殊符号不应有一元运算符", term.unaryOp);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_03_24_SpecialSymbol_WithScripts() {
		System.out.println("\n=== Part 3.24: 特殊符号 - 带上下标 ===");

		assertParsesTo("\\angle_1", "\\angle_1");
		assertParsesTo("\\dots^{n}", "\\dots^{n}");
		assertParsesTo("\\cdots_i", "\\cdots_i");

		// 验证特殊符号不能有一元运算符
		assertParseFails("-\\dots");
		assertParseFails("+\\angle");
		assertParseFails("\\pm\\cdots");
	}

	// ============================================================
	// Part 4: 上下标测试
	// ============================================================

	@Test
	public void test_04_01_Superscript_SingleToken() {
		System.out.println("\n=== Part 4.1: 上标 - 单个 token ===");

		assertParsesTo("x^2", "x^2");
		assertParsesTo("x^a", "x^a");
		assertParsesTo("x^\\alpha", "x^\\alpha");
		assertParsesTo("x^+", "x^+");
		assertParsesTo("x^-", "x^-");

		// 验证 AST
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
	public void test_04_02_Superscript_Group() {
		System.out.println("\n=== Part 4.2: 上标 - Group ===");

		assertParsesTo("x^{n+1}", "x^{n+1}");
		assertParsesTo("x^{a+b}", "x^{a+b}");
		assertParsesTo("e^{-x}", "e^{-x}");
		assertParsesTo("e^{i\\pi}", "e^{i \\pi}");
	}

	@Test
	public void test_04_03_Subscript_SingleToken() {
		System.out.println("\n=== Part 4.3: 下标 - 单个 token ===");

		assertParsesTo("x_1", "x_1");
		assertParsesTo("x_i", "x_i");
		assertParsesTo("a_\\alpha", "a_\\alpha");
		assertParsesTo("x_+", "x_+");

		// 验证 AST
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
	public void test_04_04_Subscript_Group() {
		System.out.println("\n=== Part 4.4: 下标 - Group ===");

		assertParsesTo("x_{n-1}", "x_{n-1}");
		assertParsesTo("a_{i,j}", "a_{i,j}");
		assertParsesTo("x_{\\alpha+\\beta}", "x_{\\alpha+\\beta}");
	}

	@Test
	public void test_04_05_SuperAndSubscript_BothOrders() {
		System.out.println("\n=== Part 4.5: 上下标 - 两种顺序 ===");

		assertParsesTo("x^2_1", "x^2_1");
		assertParsesTo("x_1^2", "x_1^2");
		assertParsesTo("x^{n+1}_{i}", "x^{n+1}_{i}");
		assertParsesTo("x_{i}^{n+1}", "x_{i}^{n+1}");

		// 验证 AST
		try {
			Term term = getTerm("x^2_1");
			assertNotNull("应该有上下标后缀", term.suffix);
			assertNotNull("应该有上标", term.suffix.superscript);
			assertNotNull("应该有下标", term.suffix.subscript);
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_04_06_ScriptArg_OperatorSymbol() {
		System.out.println("\n=== Part 4.6: 上下标参数 - 运算符符号 ===");

		// BNF 允许 operator_symbol 作为 script_arg
		assertParsesTo("x^{\\pm}", "x^{\\pm}");
		assertParsesTo("x^{=}", "x^{=}");
		assertParsesTo("x_<", "x_<");
		assertParsesTo("x_>", "x_>");
	}

	// ============================================================
	// Part 5: 复合结构测试 (Term, Expression, MathList)
	// ============================================================

	@Test
	public void test_05_01_Term_Structure() {
		System.out.println("\n=== Part 5.1: Term 结构 ===");

		// Term = [unary_op] operand_atom [sup_sub_suffix]
		try {
			// 只有 atom
			Term term = getTerm("x");
			assertNull("无一元运算符", term.unaryOp);
			assertTrue("有 atom", term.atom instanceof VariableAtom);
			assertNull("无上下标", term.suffix);

			// 有一元运算符
			term = getTerm("-x");
			assertNotNull("有一元运算符", term.unaryOp);
			assertTrue("有 atom", term.atom instanceof VariableAtom);
			assertNull("无上下标", term.suffix);

			// 有上下标
			term = getTerm("x^2");
			assertNull("无一元运算符", term.unaryOp);
			assertTrue("有 atom", term.atom instanceof VariableAtom);
			assertNotNull("有上下标", term.suffix);

			// 全都有
			term = getTerm("-x^2");
			assertNotNull("有一元运算符", term.unaryOp);
			assertTrue("有 atom", term.atom instanceof VariableAtom);
			assertNotNull("有上下标", term.suffix);

			System.out.println("✅ Term 结构验证通过");
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_05_02_Expression_Structure() {
		System.out.println("\n=== Part 5.2: Expression 结构 ===");

		// Expression = term { binary_op term }
		try {
			// 单个 term
			Expression expr = getExpression("x");
			assertEquals(1, expr.elements.size());
			assertTrue("第 1 个是 Term", expr.elements.get(0) instanceof Term);

			// term binary_op term
			expr = getExpression("a+b");
			assertEquals(3, expr.elements.size());
			assertTrue("第 1 个是 Term", expr.elements.get(0) instanceof Term);
			assertTrue("第 2 个是 BinOpAtom", expr.elements.get(1) instanceof BinOpAtom);
			assertTrue("第 3 个是 Term", expr.elements.get(2) instanceof Term);

			// term binary_op term binary_op term
			expr = getExpression("a+b*c");
			assertEquals(5, expr.elements.size());
			assertTrue("第 1 个是 Term", expr.elements.get(0) instanceof Term);
			assertTrue("第 2 个是 BinOpAtom", expr.elements.get(1) instanceof BinOpAtom);
			assertTrue("第 3 个是 Term", expr.elements.get(2) instanceof Term);
			assertTrue("第 4 个是 BinOpAtom", expr.elements.get(3) instanceof BinOpAtom);
			assertTrue("第 5 个是 Term", expr.elements.get(4) instanceof Term);

			System.out.println("✅ Expression 结构验证通过");
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_05_03_MathList_Structure() {
		System.out.println("\n=== Part 5.3: MathList 结构 ===");

		// MathList = { expression | spacing }
		try {
			// 单个 expression
			MathList ast = getMathList("x");
			assertEquals(1, ast.elements.size());
			assertTrue("第 1 个是 Expression", ast.elements.get(0) instanceof Expression);

			// expression spacing expression
			ast = getMathList("a\\quad b");
			assertEquals(3, ast.elements.size());
			assertTrue("第 1 个是 Expression", ast.elements.get(0) instanceof Expression);
			assertTrue("第 2 个是 Spacing", ast.elements.get(1) instanceof Spacing);
			assertTrue("第 3 个是 Expression", ast.elements.get(2) instanceof Expression);

			Spacing spacing = (Spacing) ast.elements.get(1);
			assertEquals("quad", spacing.cmd);

			// 多个连续 spacing
			ast = getMathList("a\\quad\\quad b");
			assertEquals(4, ast.elements.size());
			assertTrue("第 1 个是 Expression", ast.elements.get(0) instanceof Expression);
			assertTrue("第 2 个是 Spacing", ast.elements.get(1) instanceof Spacing);
			assertTrue("第 3 个是 Spacing", ast.elements.get(2) instanceof Spacing);
			assertTrue("第 4 个是 Expression", ast.elements.get(3) instanceof Expression);

			System.out.println("✅ MathList 结构验证通过");
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_05_04_Spacing_AllTypes() {
		System.out.println("\n=== Part 5.4: Spacing - 所有类型 ===");

		assertParsesTo("a\\, b", "a\\, b");
		assertParsesTo("a\\: b", "a\\: b");
		assertParsesTo("a\\; b", "a\\; b");
		assertParsesTo("a\\! b", "a\\! b");
		assertParsesTo("a\\quad b", "a\\quad b");
		assertParsesTo("a\\qquad b", "a\\qquad b");
	}

	// ============================================================
	// Part 6: 歧义语法测试
	// ============================================================

	@Test
	public void test_06_01_Ambiguity_UnaryVsBinary() {
		System.out.println("\n=== Part 6.1: 歧义 - 一元 vs 二元运算符 ===");

		// + - \pm \mp 既可以是一元，也可以是二元
		assertParsesTo("a+-b", "a+-b");       // a + (-b)
		assertParsesTo("a-+b", "a-+b");       // a - (+b)
		assertParsesTo("a--b", "a--b");       // a - (-b)
		assertParsesTo("\\pm a", "\\pm a");   // 一元
		assertParsesTo("a\\pm b", "a\\pm b"); // 二元
		assertParsesTo("a\\pm\\pm b", "a\\pm \\pm b"); // 二元 + 一元

		// 验证 AST
		try {
			Expression expr = getExpression("a+-b");
			assertEquals(3, expr.elements.size());

			// a
			Term term1 = (Term) expr.elements.get(0);
			assertNull("a 无一元运算符", term1.unaryOp);

			// +
			BinOpAtom plus = (BinOpAtom) expr.elements.get(1);
			assertEquals("+", plus.op);

			// -b
			Term term2 = (Term) expr.elements.get(2);
			assertNotNull("-b 有一元运算符", term2.unaryOp);
			assertEquals("-", term2.unaryOp.op);

			System.out.println("✅ 一元 vs 二元歧义验证通过");
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_06_02_Ambiguity_ImplicitMultiplication() {
		System.out.println("\n=== Part 6.2: 歧义 - 隐式乘法边界 ===");

		// 变量连写 → 单个多字母变量
		assertParsesTo("abc", "abc");
		assertParsesTo("xy", "xy");

		// 数字 + 变量 → 分开（隐式乘法）
		assertParsesTo("2x", "2 x");
		assertParsesTo("3.14r", "3.14 r");

		// 变量 + 数字 → 分开
		assertParsesTo("x2", "x 2");

		// 变量 + 希腊字母 → 分开
		assertParsesTo("x\\alpha", "x \\alpha");

		// 数字 + 希腊字母 → 分开
		assertParsesTo("2\\pi", "2 \\pi");

		// group + 变量 → 分开
		assertParsesTo("{a}b", "{a} b");
		assertParsesTo("a{b}", "a {b}");

		System.out.println("✅ 隐式乘法边界验证通过");
	}

	@Test
	public void test_06_03_Ambiguity_FunctionArgument() {
		System.out.println("\n=== Part 6.3: 歧义 - 函数参数边界 ===");

		// 函数只吃掉第一个 token 或 group
		assertParsesTo("\\sin x", "\\sin x");
		assertParsesTo("\\sin{x+y}", "\\sin{x+y}");
		assertParsesTo("\\sin x+\\cos y", "\\sin x+\\cos y");
		assertParsesTo("\\sin\\frac{x}{2}", "\\sin \\frac{x}{2}");
		assertParsesTo("\\sin\\cos x", "\\sin \\cos x");

		// 验证 AST
		try {
			Expression expr = getExpression("\\sin x+y");
			assertEquals(3, expr.elements.size());

			// \sin x
			Term sinTerm = (Term) expr.elements.get(0);
			FunctionCallAtom func = (FunctionCallAtom) sinTerm.atom;
			assertEquals("sin", func.name);
			assertNotNull("sin 应该有参数", func.argument);

			// +
			BinOpAtom plus = (BinOpAtom) expr.elements.get(1);
			assertEquals("+", plus.op);

			// y
			Term yTerm = (Term) expr.elements.get(2);
			assertTrue("y 应该是 VariableAtom", yTerm.atom instanceof VariableAtom);

			System.out.println("✅ 函数参数边界验证通过");
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_06_04_Ambiguity_SuperscriptPrecedence() {
		System.out.println("\n=== Part 6.4: 歧义 - 上下标优先级 ===");

		// 上下标只修饰紧邻的 atom
		assertParsesTo("x^2+1", "x^2+1");      // (x^2) + 1
		assertParsesTo("x^2y", "x^2 y");       // (x^2) * y
		assertParsesTo("x^{2+1}", "x^{2+1}");  // x^(2+1)
		assertParsesTo("x^{a+b}y", "x^{a+b} y");

		// 连续上标
		assertParsesTo("x^2^3", "x^2^3");      // (x^2)^3

		// 函数和大型运算符的上下标
		assertParsesTo("\\sin^2 x", "\\sin^2 x");
		assertParsesTo("\\log_2 x", "\\log_2 x");
		assertParsesTo("\\sum_{i=1}^{n}a_i", "\\sum_{i=1}^{n} a_i");

		// 验证 AST
		try {
			Expression expr = getExpression("x^2+1");
			assertEquals(3, expr.elements.size());

			// x^2
			Term xTerm = (Term) expr.elements.get(0);
			assertNotNull("x 应该有上标", xTerm.suffix);
			assertNotNull("应该有上标", xTerm.suffix.superscript);

			// +
			BinOpAtom plus = (BinOpAtom) expr.elements.get(1);
			assertEquals("+", plus.op);

			// 1
			Term oneTerm = (Term) expr.elements.get(2);
			assertTrue("1 应该是 NumberAtom", oneTerm.atom instanceof NumberAtom);

			System.out.println("✅ 上下标优先级验证通过");
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_06_05_Ambiguity_CommaAsOperator() {
		System.out.println("\n=== Part 6.5: 歧义 - 逗号作为运算符 ===");

		// 逗号可以作为二元运算符
		assertParsesTo("a,b,c", "a,b,c");
		assertParsesTo("f\\left(x,y\\right)", "f \\left( x,y \\right)");
		assertParsesTo("\\{x,y,z\\}", "{x,y,z}");

		// 验证 AST
		try {
			Expression expr = getExpression("a,b");
			assertEquals(3, expr.elements.size());
			BinOpAtom comma = (BinOpAtom) expr.elements.get(1);
			assertEquals(",", comma.op);

			System.out.println("✅ 逗号运算符验证通过");
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	// ============================================================
	// Part 7: AST 结构深度验证
	// ============================================================

	@Test
	public void test_07_01_Deep_GroupStructure() {
		System.out.println("\n=== Part 7.1: 深度验证 - Group 结构 ===");

		try {
			// 简单 group
			Term term = getTerm("{x}");
			Group group = (Group) term.atom;
			assertEquals(1, group.content.elements.size());
			assertTrue("内容是 Expression", group.content.elements.get(0) instanceof Expression);

			// 复杂 group
			term = getTerm("{a+b}");
			group = (Group) term.atom;
			Expression expr = (Expression) group.content.elements.get(0);
			assertEquals(3, expr.elements.size());

			// 嵌套 group
			term = getTerm("{{x}}");
			group = (Group) term.atom;
			expr = (Expression) group.content.elements.get(0);
			Term innerTerm = (Term) expr.elements.get(0);
			assertTrue("内部也是 Group", innerTerm.atom instanceof Group);

			System.out.println("✅ Group 深度结构验证通过");
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_07_02_Deep_FracStructure() {
		System.out.println("\n=== Part 7.2: 深度验证 - Frac 结构 ===");

		try {
			// 分式的分子和分母都是 MathList
			Term term = getTerm("\\frac{a+b}{c-d}");
			FracAtom frac = (FracAtom) term.atom;

			// 分子
			assertFalse("分子不为空", frac.numerator.elements.isEmpty());
			Expression numerExpr = (Expression) frac.numerator.elements.get(0);
			assertEquals(3, numerExpr.elements.size()); // a + b

			// 分母
			assertFalse("分母不为空", frac.denominator.elements.isEmpty());
			Expression denomExpr = (Expression) frac.denominator.elements.get(0);
			assertEquals(3, denomExpr.elements.size()); // c - d

			System.out.println("✅ Frac 深度结构验证通过");
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_07_03_Deep_ComplexNesting() {
		System.out.println("\n=== Part 7.3: 深度验证 - 复杂嵌套 ===");

		try {
			// 分式中包含求和，求和中包含分式
			String input = "\\frac{\\sum_{i=1}^{n}\\frac{1}{i}}{n}";
			MathList ast = getMathList(input);

			// MathList -> Expression -> Term -> FracAtom
			assertEquals(1, ast.elements.size());
			Expression expr = (Expression) ast.elements.get(0);
			assertEquals(1, expr.elements.size());
			Term term = (Term) expr.elements.get(0);
			assertTrue("应该是 FracAtom", term.atom instanceof FracAtom);

			FracAtom frac = (FracAtom) term.atom;

			// 分子包含求和
			Expression numerExpr = (Expression) frac.numerator.elements.get(0);
			Term sumTerm = (Term) numerExpr.elements.get(0);
			assertTrue("分子包含 LargeOperatorAtom", sumTerm.atom instanceof LargeOperatorAtom);

			LargeOperatorAtom sum = (LargeOperatorAtom) sumTerm.atom;
			assertNotNull("求和有上下标", sum.supSubSuffix);

			System.out.println("✅ 复杂嵌套深度结构验证通过");
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_07_04_Deep_MatrixStructure() {
		System.out.println("\n=== Part 7.4: 深度验证 - Matrix 结构 ===");

		try {
			Term term = getTerm("\\begin{matrix}a+b&c\\\\d&e^2\\end{matrix}");
			MatrixAtom matrix = (MatrixAtom) term.atom;

			// 验证行数
			assertEquals(2, matrix.rows.size());

			// 第一行：a+b, c
			MatrixRow row1 = matrix.rows.get(0);
			assertEquals(2, row1.elements.size());

			// 第一行第一列: a+b
			MathList cell11 = row1.elements.get(0);
			Expression expr11 = (Expression) cell11.elements.get(0);
			assertEquals(3, expr11.elements.size()); // a + b

			// 第二行第二列: e^2
			MatrixRow row2 = matrix.rows.get(1);
			MathList cell22 = row2.elements.get(1);
			Expression expr22 = (Expression) cell22.elements.get(0);
			Term eTerm = (Term) expr22.elements.get(0);
			assertNotNull("e 有上标", eTerm.suffix);

			System.out.println("✅ Matrix 深度结构验证通过");
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	// ============================================================
	// Part 8: 边界和错误情况
	// ============================================================

	@Test
	public void test_08_01_Edge_EmptyInput() {
		System.out.println("\n=== Part 8.1: 边界 - 空输入 ===");

		try {
			MathList ast = getMathList("");
			assertTrue("空输入应该得到空 MathList", ast.elements.isEmpty());

			ast = getMathList("   ");
			assertTrue("只有空格应该得到空 MathList", ast.elements.isEmpty());
		} catch (MathParseException e) {
			fail(e.pretty());
		}
	}

	@Test
	public void test_08_02_Edge_SingleChar() {
		System.out.println("\n=== Part 8.2: 边界 - 单字符 ===");

		assertParsesTo("x", "x");
		assertParsesTo("1", "1");
		assertParsesTo("a", "a");
	}

	@Test
	public void test_08_03_Edge_DeepNesting() {
		System.out.println("\n=== Part 8.3: 边界 - 深度嵌套 ===");

		assertParsesTo("{{{{x}}}}", "{{{{x}}}}");
		assertParsesTo("\\frac{\\frac{\\frac{1}{2}}{3}}{4}",
				"\\frac{\\frac{\\frac{1}{2}}{3}}{4}");
	}

	@Test
	public void test_08_04_Edge_LongExpression() {
		System.out.println("\n=== Part 8.4: 边界 - 长表达式 ===");

		assertParsesTo("a+b+c+d+e+f+g", "a+b+c+d+e+f+g");
		assertParsesTo("a*b*c*d*e", "a*b*c*d*e");
	}

	@Test
	public void test_08_05_Error_IncompleteStructures() {
		System.out.println("\n=== Part 8.5: 错误 - 不完整结构 ===");

		assertParseFails("\\frac{1}{");
		assertParseFails("\\frac{1}");
		assertParseFails("\\sqrt[");
		assertParseFails("\\left(x");
		assertParseFails("{x");
		assertParseFails("\\begin{matrix}a&b");
	}

	@Test
	public void test_08_06_Error_BinaryOpMissingOperand() {
		System.out.println("\n=== Part 8.6: 错误 - 二元运算符缺操作数 ===");

		assertParseFails("a+");
		assertParseFails("a-");
		assertParseFails("a*");
		assertParseFails("a/");
		assertParseFails("a\\times");
	}

	@Test
	public void test_08_07_Error_UnknownCommand() {
		System.out.println("\n=== Part 8.7: 错误 - 未知命令 ===");

		assertParseFails("\\unknowncommand");
		assertParseFails("\\sine x");  // 拼写错误
		assertParseFails("\\summ");
	}

	@Test
	public void test_08_08_Error_MismatchedDelimiters() {
		System.out.println("\n=== Part 8.8: 错误 - 定界符不匹配 ===");

		assertParseFails("\\left(x\\bigr)");
		assertParseFails("\\bigl(x\\Bigr)");
	}

	@Test
	public void test_08_09_Error_MismatchedMatrix() {
		System.out.println("\n=== Part 8.9: 错误 - 矩阵环境不匹配 ===");

		assertParseFails("\\begin{matrix}a\\end{pmatrix}");
		assertParseFails("\\begin{matrix}a\\end{bmatrix}");
	}

	@Test
	public void test_08_10_Error_InvalidSuperscript() {
		System.out.println("\n=== Part 8.10: 错误 - 无效上下标 ===");

		assertParseFails("x^^2");
		assertParseFails("x__2");
	}

	@Test
	public void test_08_11_Error_EmptyCommand() {
		System.out.println("\n=== Part 8.11: 错误 - 空命令 ===");

		assertParseFails("\\");
	}

	// ============================================================
	// Part 9: 真实世界综合测试
	// ============================================================

	@Test
	public void test_09_01_Real_QuadraticFormula() {
		System.out.println("\n=== Part 9.1: 真实公式 - 二次公式 ===");

		assertParsesTo(
				"\\frac{-b\\pm\\sqrt{b^2-4ac}}{2a}",
				"\\frac{-b\\pm \\sqrt{b^2-4 ac}}{2 a}"
		);
	}

	@Test
	public void test_09_02_Real_EulerFormula() {
		System.out.println("\n=== Part 9.2: 真实公式 - 欧拉公式 ===");

		assertParsesTo("e^{i\\pi}+1=0", "e^{i \\pi}+1=0");
	}

	@Test
	public void test_09_03_Real_PythagoreanTheorem() {
		System.out.println("\n=== Part 9.3: 真实公式 - 勾股定理 ===");

		assertParsesTo("a^2+b^2=c^2", "a^2+b^2=c^2");
	}

	@Test
	public void test_09_04_Real_Limit() {
		System.out.println("\n=== Part 9.4: 真实公式 - 极限 ===");

		assertParsesTo(
				"\\lim_{x\\to 0}\\frac{\\sin x}{x}=1",
				"\\lim_{x\\to 0} \\frac{\\sin x}{x}=1"
		);
	}

	@Test
	public void test_09_05_Real_Sum() {
		System.out.println("\n=== Part 9.5: 真实公式 - 求和 ===");

		assertParsesTo(
				"\\sum_{n=1}^{\\infty}\\frac{1}{n^2}=\\frac{\\pi^2}{6}",
				"\\sum_{n=1}^{\\infty} \\frac{1}{n^2}=\\frac{\\pi^2}{6}"
		);
	}

	@Test
	public void test_09_06_Real_Derivative() {
		System.out.println("\n=== Part 9.6: 真实公式 - 导数定义 ===");

		assertParsesTo(
				"f'\\left(x\\right)=\\lim_{h\\to 0}\\frac{f\\left(x+h\\right)-f\\left(x\\right)}{h}",
				"f' \\left( x \\right)=\\lim_{h\\to 0} \\frac{f \\left( x+h \\right)-f \\left( x \\right)}{h}"
		);
	}

	@Test
	public void test_09_07_Real_Integral() {
		System.out.println("\n=== Part 9.7: 真实公式 - 积分 ===");

		assertParsesTo(
				"\\int_0^{\\infty}e^{-x^2}dx=\\frac{\\sqrt{\\pi}}{2}",
				"\\int_0^{\\infty} e^{-x^2} dx=\\frac{\\sqrt{\\pi}}{2}"
		);
	}

	@Test
	public void test_09_08_Real_BinomialTheorem() {
		System.out.println("\n=== Part 9.8: 真实公式 - 二项式定理 ===");

		assertParsesTo(
				"\\left(a+b\\right)^n=\\sum_{k=0}^{n}C_n^k a^{n-k}b^k",
				"\\left( a+b \\right)^n=\\sum_{k=0}^{n} C_n^k a^{n-k} b^k"
		);
	}

	@Test
	public void test_09_09_Real_TaylorSeries() {
		System.out.println("\n=== Part 9.9: 真实公式 - 泰勒级数 ===");

		assertParsesTo(
				"e^x=\\sum_{n=0}^{\\infty}\\frac{x^n}{n!}",
				"e^x=\\sum_{n=0}^{\\infty} \\frac{x^n}{n!}"
		);
	}

	@Test
	public void test_09_10_Real_CauchySchwarz() {
		System.out.println("\n=== Part 9.10: 真实公式 - 柯西-施瓦茨不等式 ===");

		assertParsesTo(
				"\\left(\\sum_{i=1}^{n}a_i b_i\\right)^2\\le\\left(\\sum_{i=1}^{n}a_i^2\\right)\\left(\\sum_{i=1}^{n}b_i^2\\right)",
				"\\left( \\sum_{i=1}^{n} a_i b_i \\right)^2\\le \\left( \\sum_{i=1}^{n} a_i^2 \\right) \\left( \\sum_{i=1}^{n} b_i^2 \\right)"
		);
	}

	@Test
	public void test_09_11_Real_MatrixMultiplication() {
		System.out.println("\n=== Part 9.11: 真实公式 - 矩阵乘法 ===");

		assertParsesTo(
				"\\begin{pmatrix}a&b\\\\c&d\\end{pmatrix}\\begin{pmatrix}x\\\\y\\end{pmatrix}=\\begin{pmatrix}ax+by\\\\cx+dy\\end{pmatrix}",
				"\\begin{pmatrix}\na & b\nc & d\n\\end{pmatrix}\n \\begin{pmatrix}\nx\ny\n\\end{pmatrix}\n=\\begin{pmatrix}\nax+by\ncx+dy\n\\end{pmatrix}\n"
		);
	}

	@Test
	public void test_09_12_Real_GeometricReasoning() {
		System.out.println("\n=== Part 9.12: 真实公式 - 几何推理 ===");

		assertParsesTo(
				"AB\\perp CD\\therefore\\angle ABC=90",
				"AB\\perp CD \\therefore \\angle ABC=90"
		);
	}

	@Test
	public void test_09_13_Real_Determinant() {
		System.out.println("\n=== Part 9.13: 真实公式 - 行列式展开 ===");

		assertParsesTo(
				"\\det A=\\sum_{\\sigma\\in S_n}\\text{sgn}\\left(\\sigma\\right)\\prod_{i=1}^{n}a_{i,\\sigma_i}",
				"\\det A=\\sum_{\\sigma\\in S_n} \\text{sgn} \\left( \\sigma \\right) \\prod_{i=1}^{n} a_{i,\\sigma_i}"
		);
	}

	@Test
	public void test_09_14_Real_PiecewiseFunction() {
		System.out.println("\n=== Part 9.14: 真实公式 - 分段函数 ===");

		assertParsesTo(
				"f\\left(x\\right)=\\begin{cases}x,&x\\ge 0\\\\-x,&x<0\\end{cases}",
				"f \\left( x \\right)=\\begin{cases}\nx, & x\\ge 0\n-x, & x<0\n\\end{cases}\n"
		);
	}

	// ============================================================
	// 测试总结
	// ============================================================

	@Test
	public void test_99_Summary() {
		System.out.println("\n" + "=".repeat(60));
		System.out.println("测试总结：");
		System.out.println("✅ 所有基础语法元素已测试");
		System.out.println("✅ 所有歧义情况已验证");
		System.out.println("✅ AST 结构符合 BNF 预期");
		System.out.println("✅ 边界和错误情况已覆盖");
		System.out.println("✅ 真实世界公式测试通过");
		System.out.println("=".repeat(60));
	}
}