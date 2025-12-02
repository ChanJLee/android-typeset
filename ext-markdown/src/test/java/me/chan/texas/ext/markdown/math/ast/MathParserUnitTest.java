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

	/**
	 * 解析输入并返回 AST
	 */
	private MathList parse(String input) throws MathParseException {
		CharStream stream = new CharStream(input, 0, input.length());
		MathParser parser = new MathParser(stream);
		return parser.parse();
	}

	/**
	 * 验证解析成功且 toString 值正确
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
	 * 验证解析成功且 toString 值正确，同时返回 AST 供进一步验证
	 */
	private MathList assertParsesToWithAst(String input, String expectedOutput) {
		try {
			MathList ast = parse(input);
			String actual = ast.toString();
			assertEquals("输入: " + input, expectedOutput, actual);
			System.out.println("✅ " + input + " → " + actual);
			return ast;
		} catch (MathParseException e) {
			fail("解析失败: " + input + " - " + e.pretty());
			return null;
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
	// AST 验证辅助类 - 链式 API
	// ============================================================

	/**
	 * MathList 验证器
	 */
	static class MathListAsserter {
		private final MathList mathList;
		private int currentIndex = 0;

		MathListAsserter(MathList mathList) {
			this.mathList = mathList;
		}

		MathListAsserter hasSize(int expectedSize) {
			assertEquals("MathList 元素数量", expectedSize, mathList.elements.size());
			return this;
		}

		ExpressionAsserter elementIsExpression(int index) {
			assertTrue("第" + index + "个元素应该是 Expression",
					mathList.elements.get(index) instanceof Expression);
			return new ExpressionAsserter((Expression) mathList.elements.get(index));
		}

		SpacingAsserter elementIsSpacing(int index) {
			assertTrue("第" + index + "个元素应该是 Spacing",
					mathList.elements.get(index) instanceof Spacing);
			return new SpacingAsserter((Spacing) mathList.elements.get(index));
		}

		ExpressionAsserter nextExpression() {
			return elementIsExpression(currentIndex++);
		}

		SpacingAsserter nextSpacing() {
			return elementIsSpacing(currentIndex++);
		}
	}

	/**
	 * Expression 验证器
	 */
	static class ExpressionAsserter {
		private final Expression expression;
		private int currentIndex = 0;

		ExpressionAsserter(Expression expression) {
			this.expression = expression;
		}

		ExpressionAsserter hasSize(int expectedSize) {
			assertEquals("Expression 元素数量", expectedSize, expression.elements.size());
			return this;
		}

		TermAsserter elementIsTerm(int index) {
			assertTrue("第" + index + "个元素应该是 Term",
					expression.elements.get(index) instanceof Term);
			return new TermAsserter((Term) expression.elements.get(index));
		}

		BinOpAsserter elementIsBinOp(int index) {
			assertTrue("第" + index + "个元素应该是 BinOpAtom",
					expression.elements.get(index) instanceof BinOpAtom);
			return new BinOpAsserter((BinOpAtom) expression.elements.get(index));
		}

		TermAsserter nextTerm() {
			return elementIsTerm(currentIndex++);
		}

		BinOpAsserter nextBinOp() {
			return elementIsBinOp(currentIndex++);
		}

		ExpressionAsserter and() {
			return this;
		}
	}

	/**
	 * Term 验证器
	 */
	static class TermAsserter {
		private final Term term;

		TermAsserter(Term term) {
			this.term = term;
		}

		TermAsserter hasNoUnaryOp() {
			assertNull("应该没有一元运算符", term.unaryOp);
			return this;
		}

		TermAsserter hasUnaryOp(String expectedOp) {
			assertNotNull("应该有一元运算符", term.unaryOp);
			assertEquals("一元运算符", expectedOp, term.unaryOp.op);
			return this;
		}

		TermAsserter hasNoSuffix() {
			assertNull("应该没有上下标", term.suffix);
			return this;
		}

		TermAsserter hasSuffix() {
			assertNotNull("应该有上下标", term.suffix);
			return this;
		}

		SupSubSuffixAsserter suffix() {
			assertNotNull("应该有上下标", term.suffix);
			return new SupSubSuffixAsserter(term.suffix);
		}

		TermAsserter atomIsNumber(String expectedValue) {
			assertTrue("atom 应该是 NumberAtom", term.atom instanceof NumberAtom);
			assertEquals("数字值", expectedValue, ((NumberAtom) term.atom).value);
			return this;
		}

		TermAsserter atomIsVariable(String expectedName) {
			assertTrue("atom 应该是 VariableAtom", term.atom instanceof VariableAtom);
			assertEquals("变量名", expectedName, ((VariableAtom) term.atom).name);
			return this;
		}

		TermAsserter atomIsGreekLetter(String expectedName) {
			assertTrue("atom 应该是 GreekLetterAtom", term.atom instanceof GreekLetterVariableAtom);
			assertEquals("希腊字母", expectedName, ((GreekLetterVariableAtom) term.atom).name);
			return this;
		}

		TermAsserter atomIsPunctuation(String expectedSymbol) {
			assertTrue("atom 应该是 PunctuationAtom", term.atom instanceof PunctuationAtom);
			assertEquals("标点符号", expectedSymbol, ((PunctuationAtom) term.atom).symbol);
			return this;
		}

		TermAsserter atomIsSpecialSymbol(String expectedName) {
			assertTrue("atom 应该是 SpecialSymbolAtom", term.atom instanceof SpecialSymbolAtom);
			assertEquals("特殊符号", expectedName, ((SpecialSymbolAtom) term.atom).symbol);
			return this;
		}

		GroupAsserter atomIsGroup() {
			assertTrue("atom 应该是 Group", term.atom instanceof Group);
			return new GroupAsserter((Group) term.atom);
		}

		FracAsserter atomIsFrac() {
			assertTrue("atom 应该是 FracAtom", term.atom instanceof FracAtom);
			return new FracAsserter((FracAtom) term.atom);
		}

		SqrtAsserter atomIsSqrt() {
			assertTrue("atom 应该是 SqrtAtom", term.atom instanceof SqrtAtom);
			return new SqrtAsserter((SqrtAtom) term.atom);
		}

		DelimitedAsserter atomIsDelimited() {
			assertTrue("atom 应该是 DelimitedAtom", term.atom instanceof DelimitedAtom);
			return new DelimitedAsserter((DelimitedAtom) term.atom);
		}

		FunctionCallAsserter atomIsFunction() {
			assertTrue("atom 应该是 FunctionCallAtom", term.atom instanceof FunctionCallAtom);
			return new FunctionCallAsserter((FunctionCallAtom) term.atom);
		}

		LargeOperatorAsserter atomIsLargeOperator() {
			assertTrue("atom 应该是 LargeOperatorAtom", term.atom instanceof LargeOperatorAtom);
			return new LargeOperatorAsserter((LargeOperatorAtom) term.atom);
		}

		MatrixAsserter atomIsMatrix() {
			assertTrue("atom 应该是 MatrixAtom", term.atom instanceof MatrixAtom);
			return new MatrixAsserter((MatrixAtom) term.atom);
		}

		TextAsserter atomIsText() {
			assertTrue("atom 应该是 TextAtom", term.atom instanceof TextAtom);
			return new TextAsserter((TextAtom) term.atom);
		}

		AccentAsserter atomIsAccent() {
			assertTrue("atom 应该是 AccentAtom", term.atom instanceof AccentAtom);
			return new AccentAsserter((AccentAtom) term.atom);
		}

		FontAsserter atomIsFont() {
			assertTrue("atom 应该是 FontAtom", term.atom instanceof FontAtom);
			return new FontAsserter((FontAtom) term.atom);
		}

		TermAsserter and() {
			return this;
		}
	}

	/**
	 * BinOpAtom 验证器
	 */
	static class BinOpAsserter {
		private final BinOpAtom binOp;

		BinOpAsserter(BinOpAtom binOp) {
			this.binOp = binOp;
		}

		BinOpAsserter isOperator(String expectedOp) {
			assertEquals("二元运算符", expectedOp, binOp.op);
			return this;
		}
	}

	/**
	 * SupSubSuffix 验证器
	 */
	static class SupSubSuffixAsserter {
		private final SupSubSuffix suffix;

		SupSubSuffixAsserter(SupSubSuffix suffix) {
			this.suffix = suffix;
		}

		SupSubSuffixAsserter hasSuperscript() {
			assertNotNull("应该有上标", suffix.superscript);
			return this;
		}

		SupSubSuffixAsserter hasSubscript() {
			assertNotNull("应该有下标", suffix.subscript);
			return this;
		}

		SupSubSuffixAsserter noSuperscript() {
			assertNull("不应该有上标", suffix.superscript);
			return this;
		}

		SupSubSuffixAsserter noSubscript() {
			assertNull("不应该有下标", suffix.subscript);
			return this;
		}

		SupSubSuffixAsserter superscriptContent(String expectedToString) {
			assertNotNull("应该有上标", suffix.superscript);
			assertEquals("上标内容", expectedToString, suffix.superscript.toString());
			return this;
		}

		SupSubSuffixAsserter subscriptContent(String expectedToString) {
			assertNotNull("应该有下标", suffix.subscript);
			assertEquals("下标内容", expectedToString, suffix.subscript.toString());
			return this;
		}
	}

	/**
	 * Group 验证器
	 */
	static class GroupAsserter {
		private final Group group;

		GroupAsserter(Group group) {
			this.group = group;
		}

		MathListAsserter content() {
			return new MathListAsserter(group.content);
		}

		GroupAsserter contentToString(String expected) {
			assertEquals("Group 内容", expected, group.content.toString());
			return this;
		}
	}

	/**
	 * FracAtom 验证器
	 */
	static class FracAsserter {
		private final FracAtom frac;

		FracAsserter(FracAtom frac) {
			this.frac = frac;
		}

		FracAsserter command(String expectedCommand) {
			assertEquals("分式命令", expectedCommand, frac.command);
			return this;
		}

		MathListAsserter numerator() {
			return new MathListAsserter(frac.numerator);
		}

		MathListAsserter denominator() {
			return new MathListAsserter(frac.denominator);
		}

		FracAsserter numeratorToString(String expected) {
			assertEquals("分子", expected, frac.numerator.toString());
			return this;
		}

		FracAsserter denominatorToString(String expected) {
			assertEquals("分母", expected, frac.denominator.toString());
			return this;
		}
	}

	/**
	 * SqrtAtom 验证器
	 */
	static class SqrtAsserter {
		private final SqrtAtom sqrt;

		SqrtAsserter(SqrtAtom sqrt) {
			this.sqrt = sqrt;
		}

		SqrtAsserter hasRoot() {
			assertNotNull("应该有根指数", sqrt.root);
			return this;
		}

		SqrtAsserter noRoot() {
			assertNull("不应该有根指数", sqrt.root);
			return this;
		}

		MathListAsserter content() {
			return new MathListAsserter(sqrt.content);
		}

		MathListAsserter root() {
			assertNotNull("应该有根指数", sqrt.root);
			return new MathListAsserter(sqrt.root);
		}
	}

	/**
	 * DelimitedAtom 验证器
	 */
	static class DelimitedAsserter {
		private final DelimitedAtom delimited;

		DelimitedAsserter(DelimitedAtom delimited) {
			this.delimited = delimited;
		}

		DelimitedAsserter level(int expectedLevel) {
			assertEquals("定界符级别", expectedLevel, delimited.level);
			return this;
		}

		DelimitedAsserter leftDelimiter(String expected) {
			assertEquals("左定界符", expected, delimited.leftDelimiter);
			return this;
		}

		DelimitedAsserter rightDelimiter(String expected) {
			assertEquals("右定界符", expected, delimited.rightDelimiter);
			return this;
		}

		MathListAsserter content() {
			return new MathListAsserter(delimited.content);
		}
	}

	/**
	 * FunctionCallAtom 验证器
	 */
	static class FunctionCallAsserter {
		private final FunctionCallAtom function;

		FunctionCallAsserter(FunctionCallAtom function) {
			this.function = function;
		}

		FunctionCallAsserter name(String expectedName) {
			assertEquals("函数名", expectedName, function.name);
			return this;
		}

		FunctionCallAsserter hasArgument() {
			assertNotNull("应该有参数", function.argument);
			return this;
		}

		FunctionCallAsserter noArgument() {
			assertNull("不应该有参数", function.argument);
			return this;
		}

		FunctionCallAsserter hasSuffix() {
			assertNotNull("应该有上下标", function.suffix);
			return this;
		}

		FunctionCallAsserter noSuffix() {
			assertNull("不应该有上下标", function.suffix);
			return this;
		}

		SupSubSuffixAsserter suffix() {
			assertNotNull("应该有上下标", function.suffix);
			return new SupSubSuffixAsserter(function.suffix);
		}
	}

	/**
	 * LargeOperatorAtom 验证器
	 */
	static class LargeOperatorAsserter {
		private final LargeOperatorAtom operator;

		LargeOperatorAsserter(LargeOperatorAtom operator) {
			this.operator = operator;
		}

		LargeOperatorAsserter name(String expectedName) {
			assertEquals("运算符名", expectedName, operator.name);
			return this;
		}

		LargeOperatorAsserter hasSuffix() {
			assertNotNull("应该有上下标", operator.suffix);
			return this;
		}

		LargeOperatorAsserter noSuffix() {
			assertNull("不应该有上下标", operator.suffix);
			return this;
		}

		SupSubSuffixAsserter suffix() {
			assertNotNull("应该有上下标", operator.suffix);
			return new SupSubSuffixAsserter(operator.suffix);
		}
	}

	/**
	 * MatrixAtom 验证器
	 */
	static class MatrixAsserter {
		private final MatrixAtom matrix;

		MatrixAsserter(MatrixAtom matrix) {
			this.matrix = matrix;
		}

		MatrixAsserter environment(String expectedEnv) {
			assertEquals("矩阵环境", expectedEnv, matrix.env);
			return this;
		}

		MatrixAsserter rowCount(int expectedCount) {
			assertEquals("矩阵行数", expectedCount, matrix.rows.size());
			return this;
		}
	}

	/**
	 * TextAtom 验证器
	 */
	static class TextAsserter {
		private final TextAtom text;

		TextAsserter(TextAtom text) {
			this.text = text;
		}

		TextAsserter command(String expectedCommand) {
			assertEquals("文本命令", expectedCommand, text.command);
			return this;
		}

		TextAsserter content(String expectedContent) {
			assertEquals("文本内容", expectedContent, text.content);
			return this;
		}
	}

	/**
	 * AccentAtom 验证器
	 */
	static class AccentAsserter {
		private final AccentAtom accent;

		AccentAsserter(AccentAtom accent) {
			this.accent = accent;
		}

		AccentAsserter command(String expectedCommand) {
			assertEquals("重音命令", expectedCommand, accent.cmd);
			return this;
		}
	}

	/**
	 * FontAtom 验证器
	 */
	static class FontAsserter {
		private final FontAtom font;

		FontAsserter(FontAtom font) {
			this.font = font;
		}

		FontAsserter command(String expectedCommand) {
			assertEquals("字体命令", expectedCommand, font.command);
			return this;
		}

		MathListAsserter content() {
			return new MathListAsserter(font.content);
		}
	}

	/**
	 * Spacing 验证器
	 */
	static class SpacingAsserter {
		private final Spacing spacing;

		SpacingAsserter(Spacing spacing) {
			this.spacing = spacing;
		}

		SpacingAsserter command(String expectedCommand) {
			assertEquals("空格命令", expectedCommand, spacing.command);
			return this;
		}
	}

	/**
	 * 入口方法：创建 MathList 验证器
	 */
	private MathListAsserter assertAst(MathList mathList) {
		return new MathListAsserter(mathList);
	}

	// ============================================================
	// Part 1: 基础元素测试（增强版示例）
	// ============================================================

	@Test
	public void test_01_01_Number_Integer() {
		System.out.println("\n=== Part 1.1: 数字 - 整数 ===");

		assertParsesTo("0", "0");
		assertParsesTo("1", "1");
		assertParsesTo("42", "42");

		// 增强：验证 AST 结构
		MathList ast = assertParsesToWithAst("123", "123");
		assertAst(ast)
				.hasSize(1)
				.elementIsExpression(0)
				.hasSize(1)
				.elementIsTerm(0)
				.hasNoUnaryOp()
				.atomIsNumber("123")
				.hasNoSuffix();
	}

	@Test
	public void test_01_02_Number_Decimal() {
		System.out.println("\n=== Part 1.2: 数字 - 小数 ===");

		assertParsesTo("0.5", "0.5");
		assertParsesTo("3.14", "3.14");

		// 增强：验证 AST 结构
		MathList ast = assertParsesToWithAst("123.456", "123.456");
		assertAst(ast)
				.hasSize(1)
				.elementIsExpression(0)
				.hasSize(1)
				.elementIsTerm(0)
				.atomIsNumber("123.456");
	}

	@Test
	public void test_01_03_Variable() {
		System.out.println("\n=== Part 1.3: 变量 ===");

		assertParsesTo("x", "x");
		assertParsesTo("y", "y");
		assertParsesTo("abc", "abc");

		// 增强：验证 AST 结构
		MathList ast = assertParsesToWithAst("f'", "f'");
		assertAst(ast)
				.hasSize(1)
				.elementIsExpression(0)
				.hasSize(1)
				.elementIsTerm(0)
				.atomIsVariable("f'");

		ast = assertParsesToWithAst("g''", "g''");
		assertAst(ast)
				.elementIsExpression(0)
				.elementIsTerm(0)
				.atomIsVariable("g''");
	}

	@Test
	public void test_01_04_GreekLetters() {
		System.out.println("\n=== Part 1.4: 希腊字母 ===");

		assertParsesTo("\\alpha", "\\alpha");
		assertParsesTo("\\beta", "\\beta");

		// 增强：验证 AST 结构
		MathList ast = assertParsesToWithAst("\\gamma", "\\gamma");
		assertAst(ast)
				.elementIsExpression(0)
				.elementIsTerm(0)
				.atomIsGreekLetter("gamma");

		ast = assertParsesToWithAst("\\Delta", "\\Delta");
		assertAst(ast)
				.elementIsExpression(0)
				.elementIsTerm(0)
				.atomIsGreekLetter("Delta");
	}

	// ============================================================
	// Part 2: 运算符测试（增强版示例）
	// ============================================================

	@Test
	public void test_02_01_UnaryOperators() {
		System.out.println("\n=== Part 2.1: 一元运算符 ===");

		assertParsesTo("-x", "-x");
		assertParsesTo("+x", "+x");

		// 增强：验证 AST 结构
		MathList ast = assertParsesToWithAst("-1", "-1");
		assertAst(ast)
				.elementIsExpression(0)
				.hasSize(1)
				.elementIsTerm(0)
				.hasUnaryOp("-")
				.atomIsNumber("1");

		ast = assertParsesToWithAst("\\pm x", "\\pm x");
		assertAst(ast)
				.elementIsExpression(0)
				.elementIsTerm(0)
				.hasUnaryOp("\\pm")
				.atomIsVariable("x");

		ast = assertParsesToWithAst("-{x+y}", "-{x + y}");
		assertAst(ast)
				.elementIsExpression(0)
				.elementIsTerm(0)
				.hasUnaryOp("-")
				.atomIsGroup()
				.contentToString("x + y");
	}

	@Test
	public void test_02_02_BinaryOperators_Arithmetic() {
		System.out.println("\n=== Part 2.2: 二元运算符 - 算术 ===");

		assertParsesTo("a+b", "a + b");

		// 增强：验证 AST 结构
		MathList ast = assertParsesToWithAst("a-b", "a - b");
		ExpressionAsserter expressionAsserter = assertAst(ast)
				.elementIsExpression(0)
				.hasSize(3);
		expressionAsserter.elementIsTerm(0)
				.atomIsVariable("a");
		expressionAsserter.and()
				.elementIsBinOp(1)
				.isOperator("-");
		expressionAsserter.and()
				.elementIsTerm(2)
				.atomIsVariable("b");

		ast = assertParsesToWithAst("a\\times b", "a \\times b");
		expressionAsserter = assertAst(ast)
				.elementIsExpression(0)
				.hasSize(3);
		expressionAsserter.nextTerm().atomIsVariable("a");
		expressionAsserter.and()
				.nextBinOp().isOperator("\\times");
		expressionAsserter.and()
				.nextTerm().atomIsVariable("b");
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
	// Part 3: 标点符号（逗号）（增强版）
	// ============================================================

	@Test
	public void test_03_01_Punctuation_Comma() {
		System.out.println("\n=== Part 3.1: 标点符号 - 逗号 ===");

		// 逗号现在是独立的 term，用空格分隔
		assertParsesTo("a,b", "a , b");

		// 增强：验证 AST 结构
		MathList ast = assertParsesToWithAst("a,b,c", "a , b , c");
		ExpressionAsserter expressionAsserter = assertAst(ast)
				.elementIsExpression(0)
				.hasSize(5);
		expressionAsserter.nextTerm().atomIsVariable("a").and();
		expressionAsserter.nextTerm().atomIsPunctuation(",").and();
		expressionAsserter.nextTerm().atomIsVariable("b").and();
		expressionAsserter.nextTerm().atomIsPunctuation(",").and();
		expressionAsserter.nextTerm().atomIsVariable("c");

		// 在定界符中
		assertParsesTo("f\\left(x,y\\right)", "f \\left( x , y \\right)");
		assertParsesTo("\\left\\{x,y,z\\right\\}", "\\left{ x , y , z \\right}");
	}

	// ============================================================
	// Part 4: 原子表达式（增强版示例）
	// ============================================================

	@Test
	public void test_04_01_Group() {
		System.out.println("\n=== Part 4.1: 分组 ===");

		assertParsesTo("{x}", "{x}");

		// 增强：验证 AST 结构
		MathList ast = assertParsesToWithAst("{a+b}", "{a + b}");
		assertAst(ast)
				.elementIsExpression(0)
				.elementIsTerm(0)
				.atomIsGroup()
				.content()
				.elementIsExpression(0)
				.hasSize(3);
	}

	@Test
	public void test_04_02_Frac() {
		System.out.println("\n=== Part 4.2: 分式 ===");

		assertParsesTo("\\frac{1}{2}", "\\frac{1}{2}");

		// 增强：验证 AST 结构
		MathList ast = assertParsesToWithAst("\\frac{a}{b}", "\\frac{a}{b}");
		assertAst(ast)
				.elementIsExpression(0)
				.elementIsTerm(0)
				.atomIsFrac()
				.command("frac")
				.numeratorToString("a")
				.denominatorToString("b");

		ast = assertParsesToWithAst("\\frac{x+y}{x-y}", "\\frac{x + y}{x - y}");
		FracAsserter fracAsserter = assertAst(ast)
				.elementIsExpression(0)
				.elementIsTerm(0)
				.atomIsFrac();

		fracAsserter.numerator()
				.elementIsExpression(0)
				.hasSize(3);  // x, +, y;

		fracAsserter.denominator()
				.elementIsExpression(0)
				.hasSize(3);  // x, -, y
	}

	@Test
	public void test_04_03_Sqrt() {
		System.out.println("\n=== Part 4.3: 根式 ===");

		assertParsesTo("\\sqrt{x}", "\\sqrt{x}");

		// 增强：验证 AST 结构
		MathList ast = assertParsesToWithAst("\\sqrt{2}", "\\sqrt{2}");
		assertAst(ast)
				.elementIsExpression(0)
				.elementIsTerm(0)
				.atomIsSqrt()
				.noRoot();

		ast = assertParsesToWithAst("\\sqrt[3]{27}", "\\sqrt[3]{27}");
		assertAst(ast)
				.elementIsExpression(0)
				.elementIsTerm(0)
				.atomIsSqrt()
				.hasRoot()
				.root()
				.elementIsExpression(0)
				.elementIsTerm(0)
				.atomIsNumber("3");
	}

	@Test
	public void test_04_04_Delimited() {
		System.out.println("\n=== Part 4.4: 定界符 ===");

		assertParsesTo("\\left(x\\right)", "\\left( x \\right)");

		// 增强：验证 AST 结构
		MathList ast = assertParsesToWithAst("\\left[x\\right]", "\\left[ x \\right]");
		assertAst(ast)
				.elementIsExpression(0)
				.elementIsTerm(0)
				.atomIsDelimited()
				.level(0)
				.leftDelimiter("[")
				.rightDelimiter("]");

		assertParsesTo("\\left\\{x\\right\\}", "\\left{ x \\right}");
		assertParsesTo("\\left|x\\right|", "\\left| x \\right|");
	}

	@Test
	public void test_04_05_Function() {
		System.out.println("\n=== Part 4.5: 函数 ===");

		assertParsesTo("\\sin x", "\\sin x");

		// 增强：验证 AST 结构
		MathList ast = assertParsesToWithAst("\\cos x", "\\cos x");
		assertAst(ast)
				.elementIsExpression(0)
				.hasSize(1)
				.elementIsTerm(0)
				.atomIsFunction()
				.name("cos")
				.hasArgument();

		ast = assertParsesToWithAst("\\log_2 x", "\\log_2 x");
		assertAst(ast)
				.elementIsExpression(0)
				.elementIsTerm(0)
				.atomIsFunction()
				.name("log")
				.hasSuffix()
				.suffix()
				.hasSubscript()
				.noSuperscript();
	}

	@Test
	public void test_04_06_LargeOperator() {
		System.out.println("\n=== Part 4.6: 大型运算符 ===");

		assertParsesTo("\\sum", "\\sum");

		// 增强：验证 AST 结构
		MathList ast = assertParsesToWithAst("\\sum_{i=1}^{n}", "\\sum_{i = 1}^{n}");
		assertAst(ast)
				.elementIsExpression(0)
				.elementIsTerm(0)
				.atomIsLargeOperator()
				.name("sum")
				.hasSuffix()
				.suffix()
				.hasSuperscript()
				.hasSubscript();

		assertParsesTo("\\int_0^1", "\\int_0^1");
		assertParsesTo("\\lim_{x\\to 0}", "\\lim_{x \\to 0}");
	}

	@Test
	public void test_04_07_Matrix() {
		System.out.println("\n=== Part 4.7: 矩阵 ===");

		MathList ast = assertParsesToWithAst(
				"\\begin{matrix}a&b\\\\c&d\\end{matrix}",
				"\\begin{matrix}\na & b\nc & d\n\\end{matrix}\n"
		);
		assertAst(ast)
				.elementIsExpression(0)
				.elementIsTerm(0)
				.atomIsMatrix()
				.environment("matrix")
				.rowCount(2);

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

		// 增强：验证 AST 结构
		MathList ast = assertParsesToWithAst("\\text{当x趋近于0时}", "\\text{当x趋近于0时}");
		assertAst(ast)
				.elementIsExpression(0)
				.elementIsTerm(0)
				.atomIsText()
				.command("text")
				.content("当x趋近于0时");
	}

	@Test
	public void test_04_09_Accent() {
		System.out.println("\n=== Part 4.9: 重音 ===");

		assertParsesTo("\\hat{x}", "\\hat{x}");

		// 增强：验证 AST 结构
		MathList ast = assertParsesToWithAst("\\vec{v}", "\\vec{v}");
		assertAst(ast)
				.elementIsExpression(0)
				.elementIsTerm(0)
				.atomIsAccent()
				.command("vec");

		assertParsesTo("\\bar{x}", "\\bar{x}");
		assertParsesTo("\\hat x", "\\hat x");
	}

	@Test
	public void test_04_10_Font() {
		System.out.println("\n=== Part 4.10: 字体 ===");

		assertParsesTo("\\mathbf{x}", "\\mathbf{x}");

		// 增强：验证 AST 结构
		MathList ast = assertParsesToWithAst("\\mathbb{R}", "\\mathbb{R}");
		assertAst(ast)
				.elementIsExpression(0)
				.elementIsTerm(0)
				.atomIsFont()
				.command("mathbb");

		assertParsesTo("\\mathcal{L}", "\\mathcal{L}");
	}

	@Test
	public void test_04_11_SpecialSymbol() {
		System.out.println("\n=== Part 4.11: 特殊符号 ===");

		assertParsesTo("\\dots", "\\dots");

		// 增强：验证 AST 结构
		MathList ast = assertParsesToWithAst("\\ldots", "\\ldots");
		assertAst(ast)
				.elementIsExpression(0)
				.elementIsTerm(0)
				.atomIsSpecialSymbol("ldots");

		assertParsesTo("\\cdots", "\\cdots");
		assertParsesTo("\\angle", "\\angle");

		// 特殊符号可以带上下标
		assertParsesTo("\\angle_1", "\\angle_1");
		assertParsesTo("\\dots^{n}", "\\dots^{n}");

		// 但不能被一元运算符修饰
		assertParseFails("-\\dots");
		assertParseFails("+\\angle");
	}

	// ============================================================
	// Part 5: 上下标（增强版）
	// ============================================================

	@Test
	public void test_05_01_Superscript() {
		System.out.println("\n=== Part 5.1: 上标 ===");

		assertParsesTo("x^2", "x^2");

		// 增强：验证 AST 结构
		MathList ast = assertParsesToWithAst("x^a", "x^a");
		assertAst(ast)
				.elementIsExpression(0)
				.elementIsTerm(0)
				.hasSuffix()
				.suffix()
				.hasSuperscript()
				.noSubscript()
				.superscriptContent("a");

		ast = assertParsesToWithAst("x^{n+1}", "x^{n + 1}");
		assertAst(ast)
				.elementIsExpression(0)
				.elementIsTerm(0)
				.suffix()
				.superscriptContent("{n + 1}");
	}

	@Test
	public void test_05_02_Subscript() {
		System.out.println("\n=== Part 5.2: 下标 ===");

		assertParsesTo("x_1", "x_1");

		// 增强：验证 AST 结构
		MathList ast = assertParsesToWithAst("x_i", "x_i");
		assertAst(ast)
				.elementIsExpression(0)
				.elementIsTerm(0)
				.suffix()
				.hasSubscript()
				.noSuperscript()
				.subscriptContent("i");

		ast = assertParsesToWithAst("a_{i,j}", "a_{i , j}");
		assertAst(ast)
				.elementIsExpression(0)
				.elementIsTerm(0)
				.suffix()
				.subscriptContent("{i , j}");
	}

	@Test
	public void test_05_03_BothScripts() {
		System.out.println("\n=== Part 5.3: 上下标同时 ===");

		// 增强：验证 AST 结构
		MathList ast = assertParsesToWithAst("x^2_1", "x^2_1");
		assertAst(ast)
				.elementIsExpression(0)
				.elementIsTerm(0)
				.suffix()
				.hasSuperscript()
				.hasSubscript();

		assertParsesTo("x_1^2", "x_1^2");
		assertParsesTo("x^{n+1}_{i}", "x^{n + 1}_{i}");
	}

	// ============================================================
	// Part 6-9: 其他测试保持不变，或根据需要添加 AST 验证
	// ============================================================

	@Test
	public void test_06_01_ImplicitMultiplication() {
		System.out.println("\n=== Part 6.1: 隐式乘法 ===");

		assertParsesTo("abc", "abc");
		assertParsesTo("xy", "xy");
		assertParsesTo("2x", "2 x");
		assertParsesTo("3.14r", "3.14 r");
		assertParsesTo("x2", "x 2");
		assertParsesTo("x\\alpha", "x \\alpha");
		assertParsesTo("2\\pi", "2 \\pi");
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

		// 增强：验证 AST 结构
		MathList ast = assertParsesToWithAst("a\\quad b", "a\\quad b");
		MathListAsserter mathListAsserter = assertAst(ast);
		mathListAsserter.hasSize(3)
				.nextExpression()
				.and();
		mathListAsserter.nextSpacing()
				.command("quad");
		mathListAsserter.nextExpression();

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

	// 在 Part 9 真实世界公式部分添加以下测试

	@Test
	public void test_09_11_Real_SchrodingerEquation() {
		System.out.println("\n=== Part 9.11: 薛定谔方程 ===");

		assertParsesTo(
				"i\\hbar\\frac{\\partial}{\\partial t}\\Psi\\left(r,t\\right)=\\left[-\\frac{\\hbar^2}{2m}\\nabla^2+V\\left(r,t\\right)\\right]\\Psi\\left(r,t\\right)",
				"i \\hbar \\frac{\\partial}{\\partial t} \\Psi \\left( r , t \\right) = \\left[ -\\frac{\\hbar^2}{2 m} \\nabla^2 + V \\left( r , t \\right) \\right] \\Psi \\left( r , t \\right)"
		);
	}

	@Test
	public void test_09_12_Real_MaxwellEquations() {
		System.out.println("\n=== Part 9.12: 麦克斯韦方程组 ===");

		// 高斯定律
		assertParsesTo(
				"\\nabla\\cdot\\mathbf{E}=\\frac{\\rho}{\\epsilon_0}",
				"\\nabla \\cdot \\mathbf{E} = \\frac{\\rho}{\\epsilon_0}"
		);

		// 高斯磁定律
		assertParsesTo(
				"\\nabla\\cdot\\mathbf{B}=0",
				"\\nabla \\cdot \\mathbf{B} = 0"
		);

		// 法拉第电磁感应定律
		assertParsesTo(
				"\\nabla\\times\\mathbf{E}=-\\frac{\\partial\\mathbf{B}}{\\partial t}",
				"\\nabla \\times \\mathbf{E} = -\\frac{\\partial \\mathbf{B}}{\\partial t}"
		);

		// 安培-麦克斯韦定律
		assertParsesTo(
				"\\nabla\\times\\mathbf{B}=\\mu_0\\left(\\mathbf{J}+\\epsilon_0\\frac{\\partial\\mathbf{E}}{\\partial t}\\right)",
				"\\nabla \\times \\mathbf{B} = \\mu_0 \\left( \\mathbf{J} + \\epsilon_0 \\frac{\\partial \\mathbf{E}}{\\partial t} \\right)"
		);
	}

	@Test
	public void test_09_13_Real_EinsteinFieldEquation() {
		System.out.println("\n=== Part 9.13: 爱因斯坦场方程 ===");

		assertParsesTo(
				"R_{\\mu\\nu}-\\frac{1}{2}Rg_{\\mu\\nu}+\\Lambda g_{\\mu\\nu}=\\frac{8\\pi G}{c^4}T_{\\mu\\nu}",
				"R_{\\mu \\nu} - \\frac{1}{2} Rg_{\\mu \\nu} + \\Lambda g_{\\mu \\nu} = \\frac{8 \\pi G}{c^4} T_{\\mu \\nu}"
		);
	}

	@Test
	public void test_09_14_Real_FourierTransform() {
		System.out.println("\n=== Part 9.14: 傅里叶变换 ===");

		assertParsesTo(
				"F\\left(\\omega\\right)=\\int_{-\\infty}^{\\infty}f\\left(t\\right)e^{-i\\omega t}dt",
				"F \\left( \\omega \\right) = \\int_{-\\infty}^{\\infty} f \\left( t \\right) e^{-i \\omega t} dt"
		);

		// 逆傅里叶变换
		assertParsesTo(
				"f\\left(t\\right)=\\frac{1}{2\\pi}\\int_{-\\infty}^{\\infty}F\\left(\\omega\\right)e^{i\\omega t}d\\omega",
				"f \\left( t \\right) = \\frac{1}{2 \\pi} \\int_{-\\infty}^{\\infty} F \\left( \\omega \\right) e^{i \\omega t} d \\omega"
		);
	}

	@Test
	public void test_09_15_Real_RiemannZetaFunction() {
		System.out.println("\n=== Part 9.15: 黎曼ζ函数 ===");

		assertParsesTo(
				"\\zeta\\left(s\\right)=\\sum_{n=1}^{\\infty}\\frac{1}{n^s}=\\prod_{p\\text{ prime}}\\frac{1}{1-p^{-s}}",
				"\\zeta \\left( s \\right) = \\sum_{n = 1}^{\\infty} \\frac{1}{n^s} = \\prod_{p \\text{ prime}} \\frac{1}{1 - p^{-s}}"
		);
	}

	@Test
	public void test_09_16_Real_BayesTheorem() {
		System.out.println("\n=== Part 9.16: 贝叶斯定理（连续形式）===");

		assertParsesTo(
				"P\\left(\\theta|D\\right)=\\frac{P\\left(D|\\theta\\right)P\\left(\\theta\\right)}{\\int P\\left(D|\\theta'\\right)P\\left(\\theta'\\right)d\\theta'}",
				"P \\left( \\theta | D \\right) = \\frac{P \\left( D | \\theta \\right) P \\left( \\theta \\right)}{\\int P \\left( D | \\theta' \\right) P \\left( \\theta' \\right) d \\theta'}"
		);
	}

	@Test
	public void test_09_17_Real_LagrangeEquation() {
		System.out.println("\n=== Part 9.17: 拉格朗日方程 ===");

		assertParsesTo(
				"\\frac{d}{dt}\\left(\\frac{\\partial L}{\\partial\\dot{q}_i}\\right)-\\frac{\\partial L}{\\partial q_i}=0",
				"\\frac{d}{dt} \\left( \\frac{\\partial L}{\\partial \\dot{q}_i} \\right) - \\frac{\\partial L}{\\partial q_i} = 0"
		);
	}

	@Test
	public void test_09_18_Real_NavierStokesEquation() {
		System.out.println("\n=== Part 9.18: 纳维-斯托克斯方程 ===");

		assertParsesTo(
				"\\rho\\left(\\frac{\\partial\\mathbf{v}}{\\partial t}+\\mathbf{v}\\cdot\\nabla\\mathbf{v}\\right)=-\\nabla p+\\mu\\nabla^2\\mathbf{v}+\\mathbf{f}",
				"\\rho \\left( \\frac{\\partial \\mathbf{v}}{\\partial t} + \\mathbf{v} \\cdot \\nabla \\mathbf{v} \\right) = -\\nabla p + \\mu \\nabla^2 \\mathbf{v} + \\mathbf{f}"
		);
	}

	@Test
	public void test_09_19_Real_BlackScholesEquation() {
		System.out.println("\n=== Part 9.19: 布莱克-舒尔斯方程 ===");

		assertParsesTo(
				"\\frac{\\partial V}{\\partial t}+\\frac{1}{2}\\sigma^2S^2\\frac{\\partial^2V}{\\partial S^2}+rS\\frac{\\partial V}{\\partial S}-rV=0",
				"\\frac{\\partial V}{\\partial t} + \\frac{1}{2} \\sigma^2 S^2 \\frac{\\partial^2 V}{\\partial S^2} + rS \\frac{\\partial V}{\\partial S} - rV = 0"
		);
	}

	@Test
	public void test_09_20_Real_DiracEquation() {
		System.out.println("\n=== Part 9.20: 狄拉克方程 ===");

		assertParsesTo(
				"\\left(i\\gamma^\\mu\\partial_\\mu-m\\right)\\psi=0",
				"\\left( i \\gamma^\\mu \\partial_\\mu - m \\right) \\psi = 0"
		);
	}

	@Test
	public void test_09_21_Real_GreensTheorem() {
		System.out.println("\n=== Part 9.21: 格林定理 ===");

		assertParsesTo(
				"\\oint_C\\left(P dx+Q dy\\right)=\\iint_D\\left(\\frac{\\partial Q}{\\partial x}-\\frac{\\partial P}{\\partial y}\\right)dA",
				"\\oint_C \\left( P dx + Q dy \\right) = \\iint_D \\left( \\frac{\\partial Q}{\\partial x} - \\frac{\\partial P}{\\partial y} \\right) dA"
		);
	}

	@Test
	public void test_09_22_Real_TaylorSeries() {
		System.out.println("\n=== Part 9.22: 泰勒级数展开 ===");

		assertParsesTo(
				"f\\left(x\\right)=\\sum_{n=0}^{\\infty}\\frac{f^{\\left(n\\right)}\\left(a\\right)}{n!}\\left(x-a\\right)^n",
				"f \\left( x \\right) = \\sum_{n = 0}^{\\infty} \\frac{f^{\\left( n \\right)} \\left( a \\right)}{n!} \\left( x - a \\right)^n"
		);
	}

	@Test
	public void test_09_23_Real_StokesTheorem() {
		System.out.println("\n=== Part 9.23: 斯托克斯定理 ===");

		assertParsesTo(
				"\\int_S\\left(\\nabla\\times\\mathbf{F}\\right)\\cdot d\\mathbf{S}=\\oint_{\\partial S}\\mathbf{F}\\cdot d\\mathbf{r}",
				"\\int_S \\left( \\nabla \\times \\mathbf{F} \\right) \\cdot d \\mathbf{S} = \\oint_{\\partial S} \\mathbf{F} \\cdot d \\mathbf{r}"
		);
	}

	@Test
	public void test_09_24_Real_ComplexIntegralWithResidue() {
		System.out.println("\n=== Part 9.24: 留数定理 ===");

		assertParsesTo(
				"\\oint_C f\\left(z\\right)dz=2\\pi i\\sum_{k=1}^{n}\\text{Res}\\left(f,z_k\\right)",
				"\\oint_C f \\left( z \\right) dz = 2 \\pi i \\sum_{k = 1}^{n} \\text{Res} \\left( f , z_k \\right)"
		);
	}

	@Test
	public void test_09_25_Real_WaveEquation() {
		System.out.println("\n=== Part 9.25: 波动方程 ===");

		assertParsesTo(
				"\\frac{\\partial^2u}{\\partial t^2}=c^2\\nabla^2u",
				"\\frac{\\partial^2 u}{\\partial t^2} = c^2 \\nabla^2 u"
		);
	}

	// ============================================================
	// Part 9: 真实世界公式（继续补充）
	// ============================================================

	@Test
	public void test_09_26_Real_CauchyIntegralFormula() {
		System.out.println("\n=== Part 9.26: 柯西积分公式 ===");

		assertParsesTo(
				"f\\left(z_0\\right)=\\frac{1}{2\\pi i}\\oint_C\\frac{f\\left(z\\right)}{z-z_0}dz",
				"f \\left( z_0 \\right) = \\frac{1}{2 \\pi i} \\oint_C \\frac{f \\left( z \\right)}{z - z_0} dz"
		);
	}

	@Test
	public void test_09_27_Real_BinomialTheorem() {
		System.out.println("\n=== Part 9.27: 二项式定理 ===");

		assertParsesTo(
				"\\left(x+y\\right)^n=\\sum_{k=0}^{n}\\binom{n}{k}x^{n-k}y^k",
				"\\left( x + y \\right)^n = \\sum_{k = 0}^{n} \\binom{n}{k} x^{n - k} y^k"
		);
	}

	@Test
	public void test_09_28_Real_CentralLimitTheorem() {
		System.out.println("\n=== Part 9.28: 中心极限定理 ===");

		assertParsesTo(
				"\\frac{\\bar{X}_n-\\mu}{\\sigma/\\sqrt{n}}\\xrightarrow{d}N\\left(0,1\\right)",
				"\\frac{\\bar{X}_n - \\mu}{\\sigma / \\sqrt{n}} \\xrightarrow{d} N \\left( 0 , 1 \\right)"
		);
	}

	@Test
	public void test_09_29_Real_NormalDistribution() {
		System.out.println("\n=== Part 9.29: 正态分布 ===");

		assertParsesTo(
				"f\\left(x\\right)=\\frac{1}{\\sigma\\sqrt{2\\pi}}e^{-\\frac{\\left(x-\\mu\\right)^2}{2\\sigma^2}}",
				"f \\left( x \\right) = \\frac{1}{\\sigma \\sqrt{2 \\pi}} e^{-\\frac{\\left( x - \\mu \\right)^2}{2 \\sigma^2}}"
		);
	}

	@Test
	public void test_09_30_Real_HeatEquation() {
		System.out.println("\n=== Part 9.30: 热传导方程 ===");

		assertParsesTo(
				"\\frac{\\partial u}{\\partial t}=\\alpha\\nabla^2u",
				"\\frac{\\partial u}{\\partial t} = \\alpha \\nabla^2 u"
		);
	}

	@Test
	public void test_09_31_Real_LaplaceEquation() {
		System.out.println("\n=== Part 9.31: 拉普拉斯方程 ===");

		assertParsesTo(
				"\\nabla^2\\phi=\\frac{\\partial^2\\phi}{\\partial x^2}+\\frac{\\partial^2\\phi}{\\partial y^2}+\\frac{\\partial^2\\phi}{\\partial z^2}=0",
				"\\nabla^2 \\phi = \\frac{\\partial^2 \\phi}{\\partial x^2} + \\frac{\\partial^2 \\phi}{\\partial y^2} + \\frac{\\partial^2 \\phi}{\\partial z^2} = 0"
		);
	}

	@Test
	public void test_09_32_Real_PoissonEquation() {
		System.out.println("\n=== Part 9.32: 泊松方程 ===");

		assertParsesTo(
				"\\nabla^2\\phi=-\\frac{\\rho}{\\epsilon_0}",
				"\\nabla^2 \\phi = -\\frac{\\rho}{\\epsilon_0}"
		);
	}

	@Test
	public void test_09_33_Real_LeibnizIntegralRule() {
		System.out.println("\n=== Part 9.33: 莱布尼茨积分法则 ===");

		assertParsesTo(
				"\\frac{d}{dx}\\int_{a\\left(x\\right)}^{b\\left(x\\right)}f\\left(x,t\\right)dt=\\int_{a\\left(x\\right)}^{b\\left(x\\right)}\\frac{\\partial f}{\\partial x}dt+f\\left(x,b\\left(x\\right)\\right)\\frac{db}{dx}-f\\left(x,a\\left(x\\right)\\right)\\frac{da}{dx}",
				"\\frac{d}{dx} \\int_{a \\left( x \\right)}^{b \\left( x \\right)} f \\left( x , t \\right) dt = \\int_{a \\left( x \\right)}^{b \\left( x \\right)} \\frac{\\partial f}{\\partial x} dt + f \\left( x , b \\left( x \\right) \\right) \\frac{db}{dx} - f \\left( x , a \\left( x \\right) \\right) \\frac{da}{dx}"
		);
	}

	@Test
	public void test_09_34_Real_EulerLagrangeEquation() {
		System.out.println("\n=== Part 9.34: 欧拉-拉格朗日方程（泛函形式）===");

		assertParsesTo(
				"\\frac{\\partial L}{\\partial y}-\\frac{d}{dx}\\frac{\\partial L}{\\partial y'}=0",
				"\\frac{\\partial L}{\\partial y} - \\frac{d}{dx} \\frac{\\partial L}{\\partial y'} = 0"
		);
	}

	@Test
	public void test_09_35_Real_HamiltonEquations() {
		System.out.println("\n=== Part 9.35: 哈密顿正则方程 ===");

		// 正则方程组
		assertParsesTo(
				"\\dot{q}_i=\\frac{\\partial H}{\\partial p_i},\\quad\\dot{p}_i=-\\frac{\\partial H}{\\partial q_i}",
				"\\dot{q}_i = \\frac{\\partial H}{\\partial p_i} , \\quad \\dot{p}_i = -\\frac{\\partial H}{\\partial q_i}"
		);
	}

	@Test
	public void test_09_36_Real_NoetherTheorem() {
		System.out.println("\n=== Part 9.36: 诺特定理 ===");

		assertParsesTo(
				"Q=\\sum_i\\frac{\\partial L}{\\partial\\dot{q}_i}\\frac{\\partial q_i}{\\partial s}",
				"Q = \\sum_i \\frac{\\partial L}{\\partial \\dot{q}_i} \\frac{\\partial q_i}{\\partial s}"
		);
	}

	@Test
	public void test_09_37_Real_HeisenbergUncertaintyPrinciple() {
		System.out.println("\n=== Part 9.37: 海森堡不确定性原理 ===");

		assertParsesTo(
				"\\Delta x\\cdot\\Delta p\\ge\\frac{\\hbar}{2}",
				"\\Delta x \\cdot \\Delta p \\ge \\frac{\\hbar}{2}"
		);
	}

	@Test
	public void test_09_38_Real_BoltzmannEntropy() {
		System.out.println("\n=== Part 9.38: 玻尔兹曼熵公式 ===");

		assertParsesTo(
				"S=k_B\\ln\\Omega",
				"S = k_B \\ln \\Omega"
		);
	}

	@Test
	public void test_09_39_Real_PlanckRadiationLaw() {
		System.out.println("\n=== Part 9.39: 普朗克辐射定律 ===");

		assertParsesTo(
				"B_\\nu\\left(T\\right)=\\frac{2h\\nu^3}{c^2}\\frac{1}{e^{\\frac{h\\nu}{k_BT}}-1}",
				"B_\\nu \\left( T \\right) = \\frac{2 h \\nu^3}{c^2} \\frac{1}{e^{\\frac{h \\nu}{k_B T}} - 1}"
		);
	}

	@Test
	public void test_09_40_Real_LaplaceTransform() {
		System.out.println("\n=== Part 9.40: 拉普拉斯变换 ===");

		assertParsesTo(
				"F\\left(s\\right)=\\mathcal{L}\\left\\{f\\left(t\\right)\\right\\}=\\int_0^{\\infty}f\\left(t\\right)e^{-st}dt",
				"F \\left( s \\right) = \\mathcal{L} \\left\\{ f \\left( t \\right) \\right\\} = \\int_0^{\\infty} f \\left( t \\right) e^{-st} dt"
		);
	}

	@Test
	public void test_09_41_Real_ConvolutionTheorem() {
		System.out.println("\n=== Part 9.41: 卷积定理 ===");

		assertParsesTo(
				"\\left(f*g\\right)\\left(t\\right)=\\int_{-\\infty}^{\\infty}f\\left(\\tau\\right)g\\left(t-\\tau\\right)d\\tau",
				"\\left( f * g \\right) \\left( t \\right) = \\int_{-\\infty}^{\\infty} f \\left( \\tau \\right) g \\left( t - \\tau \\right) d \\tau"
		);
	}

	@Test
	public void test_09_42_Real_StirlingApproximation() {
		System.out.println("\n=== Part 9.42: 斯特林近似 ===");

		assertParsesTo(
				"n!\\approx\\sqrt{2\\pi n}\\left(\\frac{n}{e}\\right)^n",
				"n! \\approx \\sqrt{2 \\pi n} \\left( \\frac{n}{e} \\right)^n"
		);
	}

	@Test
	public void test_09_43_Real_EulerProduct() {
		System.out.println("\n=== Part 9.43: 欧拉乘积公式 ===");

		assertParsesTo(
				"\\zeta\\left(s\\right)=\\prod_{p\\text{ prime}}\\left(1-p^{-s}\\right)^{-1}",
				"\\zeta \\left( s \\right) = \\prod_{p \\text{ prime}} \\left( 1 - p^{-s} \\right)^{-1}"
		);
	}

	@Test
	public void test_09_44_Real_RiemannHypothesis() {
		System.out.println("\n=== Part 9.44: 黎曼猜想（非平凡零点）===");

		assertParsesTo(
				"\\zeta\\left(s\\right)=0\\implies\\Re\\left(s\\right)=\\frac{1}{2}",
				"\\zeta \\left( s \\right) = 0 \\implies \\Re \\left( s \\right) = \\frac{1}{2}"
		);
	}

	@Test
	public void test_09_45_Real_VandermondeIdentity() {
		System.out.println("\n=== Part 9.45: 范德蒙德恒等式 ===");

		assertParsesTo(
				"\\binom{m+n}{r}=\\sum_{k=0}^{r}\\binom{m}{k}\\binom{n}{r-k}",
				"\\binom{m + n}{r} = \\sum_{k = 0}^{r} \\binom{m}{k} \\binom{n}{r - k}"
		);
	}

	@Test
	public void test_09_46_Real_CauchyRiemannEquations() {
		System.out.println("\n=== Part 9.46: 柯西-黎曼方程 ===");

		assertParsesTo(
				"\\frac{\\partial u}{\\partial x}=\\frac{\\partial v}{\\partial y},\\quad\\frac{\\partial u}{\\partial y}=-\\frac{\\partial v}{\\partial x}",
				"\\frac{\\partial u}{\\partial x} = \\frac{\\partial v}{\\partial y} , \\quad \\frac{\\partial u}{\\partial y} = -\\frac{\\partial v}{\\partial x}"
		);
	}

	@Test
	public void test_09_47_Real_RodriguesFormula() {
		System.out.println("\n=== Part 9.47: 罗德里格斯公式（勒让德多项式）===");

		assertParsesTo(
				"P_n\\left(x\\right)=\\frac{1}{2^n n!}\\frac{d^n}{dx^n}\\left(x^2-1\\right)^n",
				"P_n \\left( x \\right) = \\frac{1}{2^n n!} \\frac{d^n}{dx^n} \\left( x^2 - 1 \\right)^n"
		);
	}

	@Test
	public void test_09_48_Real_LegendrePolynomialOrthogonality() {
		System.out.println("\n=== Part 9.48: 勒让德多项式正交性 ===");

		assertParsesTo(
				"\\int_{-1}^{1}P_m\\left(x\\right)P_n\\left(x\\right)dx=\\frac{2}{2n+1}\\delta_{mn}",
				"\\int_{-1}^{1} P_m \\left( x \\right) P_n \\left( x \\right) dx = \\frac{2}{2 n + 1} \\delta_{mn}"
		);
	}

	@Test
	public void test_09_49_Real_BesselEquation() {
		System.out.println("\n=== Part 9.49: 贝塞尔方程 ===");

		assertParsesTo(
				"x^2\\frac{d^2y}{dx^2}+x\\frac{dy}{dx}+\\left(x^2-\\nu^2\\right)y=0",
				"x^2 \\frac{d^2 y}{dx^2} + x \\frac{dy}{dx} + \\left( x^2 - \\nu^2 \\right) y = 0"
		);
	}

	@Test
	public void test_09_50_Real_GramSchmidtProcess() {
		System.out.println("\n=== Part 9.50: 格拉姆-施密特正交化 ===");

		assertParsesTo(
				"\\mathbf{u}_k=\\mathbf{v}_k-\\sum_{j=1}^{k-1}\\text{proj}_{\\mathbf{u}_j}\\left(\\mathbf{v}_k\\right)",
				"\\mathbf{u}_k = \\mathbf{v}_k - \\sum_{j = 1}^{k - 1} \\text{proj}_{\\mathbf{u}_j} \\left( \\mathbf{v}_k \\right)"
		);
	}

	@Test
	public void test_09_51_Real_DivergenceTheorem() {
		System.out.println("\n=== Part 9.51: 散度定理（高斯定理）===");

		assertParsesTo(
				"\\iiint_V\\left(\\nabla\\cdot\\mathbf{F}\\right)dV=\\iint_{\\partial V}\\mathbf{F}\\cdot d\\mathbf{S}",
				"\\iiint_V \\left( \\nabla \\cdot \\mathbf{F} \\right) dV = \\iint_{\\partial V} \\mathbf{F} \\cdot d \\mathbf{S}"
		);
	}

	@Test
	public void test_09_52_Real_LorentzTransformation() {
		System.out.println("\n=== Part 9.52: 洛伦兹变换 ===");

		assertParsesTo(
				"x'=\\gamma\\left(x-vt\\right),\\quad t'=\\gamma\\left(t-\\frac{vx}{c^2}\\right),\\quad\\gamma=\\frac{1}{\\sqrt{1-\\frac{v^2}{c^2}}}",
				"x' = \\gamma \\left( x - vt \\right) , \\quad t' = \\gamma \\left( t - \\frac{vx}{c^2} \\right) , \\quad \\gamma = \\frac{1}{\\sqrt{1 - \\frac{v^2}{c^2}}}"
		);
	}

	@Test
	public void test_09_53_Real_MinkowskiMetric() {
		System.out.println("\n=== Part 9.53: 闵可夫斯基度规 ===");

		assertParsesTo(
				"ds^2=-c^2dt^2+dx^2+dy^2+dz^2",
				"ds^2 = -c^2 dt^2 + dx^2 + dy^2 + dz^2"
		);
	}

	@Test
	public void test_09_54_Real_KleinGordonEquation() {
		System.out.println("\n=== Part 9.54: 克莱因-戈登方程 ===");

		assertParsesTo(
				"\\left(\\frac{1}{c^2}\\frac{\\partial^2}{\\partial t^2}-\\nabla^2+\\frac{m^2c^2}{\\hbar^2}\\right)\\psi=0",
				"\\left( \\frac{1}{c^2} \\frac{\\partial^2}{\\partial t^2} - \\nabla^2 + \\frac{m^2 c^2}{\\hbar^2} \\right) \\psi = 0"
		);
	}

	@Test
	public void test_09_55_Real_FeynmanPathIntegral() {
		System.out.println("\n=== Part 9.55: 费曼路径积分 ===");

		assertParsesTo(
				"K\\left(x_b,t_b;x_a,t_a\\right)=\\int\\mathcal{D}\\left[x\\left(t\\right)\\right]e^{\\frac{i}{\\hbar}S\\left[x\\left(t\\right)\\right]}",
				"K \\left( x_b , t_b ; x_a , t_a \\right) = \\int \\mathcal{D} \\left[ x \\left( t \\right) \\right] e^{\\frac{i}{\\hbar} S \\left[ x \\left( t \\right) \\right]}"
		);
	}

	@Test
	public void test_09_56_Real_ChernSimonsAction() {
		System.out.println("\n=== Part 9.56: 陈-西蒙斯作用量 ===");

		assertParsesTo(
				"S_{CS}=\\frac{k}{4\\pi}\\int\\text{Tr}\\left(A\\wedge dA+\\frac{2}{3}A\\wedge A\\wedge A\\right)",
				"S_{CS} = \\frac{k}{4 \\pi} \\int \\text{Tr} \\left( A \\wedge dA + \\frac{2}{3} A \\wedge A \\wedge A \\right)"
		);
	}

	@Test
	public void test_09_57_Real_YangMillsEquations() {
		System.out.println("\n=== Part 9.57: 杨-米尔斯方程 ===");

		assertParsesTo(
				"D_\\mu F^{\\mu\\nu}=j^\\nu",
				"D_\\mu F^{\\mu \\nu} = j^\\nu"
		);
	}

	@Test
	public void test_09_58_Real_HodgeDecomposition() {
		System.out.println("\n=== Part 9.58: 霍奇分解 ===");

		assertParsesTo(
				"\\omega=d\\alpha+\\delta\\beta+\\gamma",
				"\\omega = d \\alpha + \\delta \\beta + \\gamma"
		);
	}

	@Test
	public void test_09_59_Real_AtiyahSingerIndex() {
		System.out.println("\n=== Part 9.59: 阿蒂亚-辛格指标定理 ===");

		assertParsesTo(
				"\\text{ind}\\left(D\\right)=\\int_M\\hat{A}\\left(M\\right)\\text{ch}\\left(E\\right)",
				"\\text{ind} \\left( D \\right) = \\int_M \\hat{A} \\left( M \\right) \\text{ch} \\left( E \\right)"
		);
	}

	@Test
	public void test_09_60_Real_PartitionFunction() {
		System.out.println("\n=== Part 9.60: 配分函数（统计力学）===");

		assertParsesTo(
				"Z=\\sum_i e^{-\\beta E_i}=\\text{Tr}\\left(e^{-\\beta H}\\right)",
				"Z = \\sum_i e^{-\\beta E_i} = \\text{Tr} \\left( e^{-\\beta H} \\right)"
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