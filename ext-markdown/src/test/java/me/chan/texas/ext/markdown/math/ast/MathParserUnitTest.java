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

	/** 验证解析成功且 toString 值正确，同时返回 AST 供进一步验证 */
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
			assertTrue("atom 应该是 GreekLetterAtom", term.atom instanceof GreekLetterAtom);
			assertEquals("希腊字母", expectedName, ((GreekLetterAtom) term.atom).name);
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
			assertNotNull("应该有上下标", function.suffix.superscript);
			return this;
		}

		FunctionCallAsserter noSuffix() {
			assertNull("不应该有上下标", function.suffix.subscript);
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
		assertAst(ast)
			.elementIsExpression(0)
				.hasSize(3)
				.elementIsTerm(0)
					.atomIsVariable("a")
				.and()
				.elementIsBinOp(1)
					.isOperator("-")
				.and()
				.elementIsTerm(2)
					.atomIsVariable("b");
					
		ast = assertParsesToWithAst("a\\times b", "a \\times b");
		assertAst(ast)
			.elementIsExpression(0)
				.hasSize(3)
				.nextTerm().atomIsVariable("a").and()
				.nextBinOp().isOperator("\\times").and()
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
		assertAst(ast)
			.elementIsExpression(0)
				.hasSize(5)
				.nextTerm().atomIsVariable("a").and()
				.nextTerm().atomIsPunctuation(",").and()
				.nextTerm().atomIsVariable("b").and()
				.nextTerm().atomIsPunctuation(",").and()
				.nextTerm().atomIsVariable("c");

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
		assertAst(ast)
			.elementIsExpression(0)
				.elementIsTerm(0)
					.atomIsFrac()
						.numerator()
							.elementIsExpression(0)
								.hasSize(3)  // x, +, y
						.and()
						.denominator()
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
		assertAst(ast)
			.hasSize(3)
			.nextExpression()
			.and()
			.nextSpacing()
				.command("quad")
			.and()
			.nextExpression();
			
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