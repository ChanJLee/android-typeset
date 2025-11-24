package me.chan.texas.ext.markdown.math.ast;

import me.chan.texas.utils.CharStream;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * LaTeX 数学公式解析器 AST 节点内容验证测试
 * 针对每种节点类型验证其字段和子节点
 */
public class MathParserAstTest {

	// ============ 辅助方法 ============

	/**
	 * 解析输入并返回MathList
	 */
	private MathList parse(String input) throws MathParseException {
		CharStream stream = new CharStream(input, 0, input.length());
		MathParser parser = new MathParser(stream);
		return parser.parse();
	}

	/**
	 * 从MathList中提取第一个Expression
	 */
	private Expression getFirstExpression(MathList mathList) {
		assertNotNull("MathList不应为null", mathList);
		List<Ast> elements = mathList.elements;
		assertTrue("MathList应至少包含一个元素", elements.size() > 0);
		assertTrue("第一个元素应是Expression", elements.get(0) instanceof Expression);
		return (Expression) elements.get(0);
	}

	/**
	 * 从Expression中提取第一个Term
	 */
	private Term getFirstTerm(Expression expr) {
		assertNotNull("Expression不应为null", expr);
		List<Ast> elements = expr.elements;
		assertTrue("Expression应至少包含一个元素", elements.size() > 0);
		assertTrue("第一个元素应是Term", elements.get(0) instanceof Term);
		return (Term) elements.get(0);
	}

	/**
	 * 从Term中获取Atom
	 */
	private Atom getAtom(Term term) {
		assertNotNull("Term不应为null", term);
		Atom atom = term.atom;
		assertNotNull("Atom不应为null", atom);
		return atom;
	}

	// ============ 1. 数字测试 ============

	@Test
	public void testNumberAtom() throws MathParseException {
		System.out.println("\n=== 测试NumberAtom节点 ===");

		// 测试整数
		MathList result = parse("123");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);
		Atom atom = getAtom(term);

		assertTrue("应该是NumberAtom", atom instanceof NumberAtom);
		NumberAtom number = (NumberAtom) atom;
		assertEquals("数字值应该是123", "123", number.value);

		// 测试小数
		result = parse("3.14");
		expr = getFirstExpression(result);
		term = getFirstTerm(expr);
		atom = getAtom(term);

		assertTrue("应该是NumberAtom", atom instanceof NumberAtom);
		number = (NumberAtom) atom;
		assertEquals("数字值应该是3.14", "3.14", number.value);

		System.out.println("✅ NumberAtom测试通过");
	}

	// ============ 2. 变量测试 ============

	@Test
	public void testVariableAtom() throws MathParseException {
		System.out.println("\n=== 测试VariableAtom节点 ===");

		MathList result = parse("x");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);
		Atom atom = getAtom(term);

		assertTrue("应该是VariableAtom", atom instanceof VariableAtom);
		VariableAtom var = (VariableAtom) atom;
		assertEquals("变量名应该是x", "x", var.name);

		// 测试大写字母
		result = parse("A");
		expr = getFirstExpression(result);
		term = getFirstTerm(expr);
		atom = getAtom(term);

		assertTrue("应该是VariableAtom", atom instanceof VariableAtom);
		var = (VariableAtom) atom;
		assertEquals("变量名应该是A", "A", var.name);

		System.out.println("✅ VariableAtom测试通过");
	}

	// ============ 3. 希腊字母测试 ============

	@Test
	public void testGreekLetterAtom() throws MathParseException {
		System.out.println("\n=== 测试GreekLetterAtom节点 ===");

		MathList result = parse("\\alpha");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);
		Atom atom = getAtom(term);

		assertTrue("应该是GreekLetterAtom", atom instanceof GreekLetterAtom);
		GreekLetterAtom greek = (GreekLetterAtom) atom;
		assertEquals("符号应该是alpha", "alpha", greek.symbol);

		// 测试大写希腊字母
		result = parse("\\Omega");
		expr = getFirstExpression(result);
		term = getFirstTerm(expr);
		atom = getAtom(term);

		assertTrue("应该是GreekLetterAtom", atom instanceof GreekLetterAtom);
		greek = (GreekLetterAtom) atom;
		assertEquals("符号应该是Omega", "Omega", greek.symbol);

		System.out.println("✅ GreekLetterAtom测试通过");
	}

	// ============ 4. 一元运算符测试 ============

	@Test
	public void testUnaryOperator() throws MathParseException {
		System.out.println("\n=== 测试一元运算符 ===");

		MathList result = parse("-x");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);

		assertEquals("一元运算符应该是-", "-", term.unaryOp.toString());
		assertTrue("Atom应该是VariableAtom", term.atom instanceof VariableAtom);

		// 测试正号
		result = parse("+y");
		expr = getFirstExpression(result);
		term = getFirstTerm(expr);

		assertEquals("一元运算符应该是+", "+", term.unaryOp.toString());

		result = parse("\\pm a");
		expr = getFirstExpression(result);
		term = getFirstTerm(expr);

		assertEquals("一元运算符应该是\\pm", "\\pm", term.unaryOp.toString());

		result = parse("\\mp a");
		expr = getFirstExpression(result);
		term = getFirstTerm(expr);

		assertEquals("一元运算符应该是\\pm", "\\mp", term.unaryOp.toString());

		System.out.println("✅ 一元运算符测试通过");
	}

	// ============ 5. 二元运算符测试 ============

	@Test
	public void testBinaryOperator() throws MathParseException {
		System.out.println("\n=== 测试二元运算符 ===");

		MathList result = parse("a+b");
		Expression expr = getFirstExpression(result);
		List<Ast> elements = expr.elements;

		// 应该有3个元素：Term, BinOpAtom, Term
		assertEquals("应该有3个元素", 3, elements.size());

		assertTrue("第一个应该是Term", elements.get(0) instanceof Term);
		assertTrue("第二个应该是BinOpAtom", elements.get(1) instanceof BinOpAtom);
		assertTrue("第三个应该是Term", elements.get(2) instanceof Term);

		BinOpAtom binOp = (BinOpAtom) elements.get(1);
		assertEquals("运算符应该是+", "+", binOp.op);

		// 测试LaTeX运算符
		result = parse("a\\times b");
		expr = getFirstExpression(result);
		elements = expr.elements;

		binOp = (BinOpAtom) elements.get(1);
		assertEquals("运算符应该是\\times", "\\times", binOp.op);

		System.out.println("✅ 二元运算符测试通过");
	}

	// ============ 6. 上下标测试 ============

	@Test
	public void testSuperscript() throws MathParseException {
		System.out.println("\n=== 测试上标 ===");

		MathList result = parse("x^2");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);

		SupSubSuffix suffix = term.suffix;
		assertNotNull("应该有上下标后缀", suffix);
		assertNotNull("应该有上标", suffix.superscript);
		assertNull("不应该有下标", suffix.subscript);

		assertTrue("上标应该是SingleTokenScriptArg",
				suffix.superscript instanceof SingleTokenScriptArg);
		SingleTokenScriptArg superscript = (SingleTokenScriptArg) suffix.superscript;
		assertEquals("上标内容应该是2", "2", superscript.token);

		System.out.println("✅ 上标测试通过");
	}

	@Test
	public void testSubscript() throws MathParseException {
		System.out.println("\n=== 测试下标 ===");

		MathList result = parse("x_1");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);

		SupSubSuffix suffix = term.suffix;
		assertNotNull("应该有上下标后缀", suffix);
		assertNull("不应该有上标", suffix.superscript);
		assertNotNull("应该有下标", suffix.subscript);

		assertTrue("下标应该是SingleTokenScriptArg",
				suffix.subscript instanceof SingleTokenScriptArg);
		SingleTokenScriptArg subscript = (SingleTokenScriptArg) suffix.subscript;
		assertEquals("下标内容应该是1", "1", subscript.token);

		System.out.println("✅ 下标测试通过");
	}

	@Test
	public void testSuperAndSubscript() throws MathParseException {
		System.out.println("\n=== 测试上下标组合 ===");

		MathList result = parse("x^2_1");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);

		SupSubSuffix suffix = term.suffix;
		assertNotNull("应该有上下标后缀", suffix);
		assertNotNull("应该有上标", suffix.superscript);
		assertNotNull("应该有下标", suffix.subscript);

		SingleTokenScriptArg superscript = (SingleTokenScriptArg) suffix.superscript;
		SingleTokenScriptArg subscript = (SingleTokenScriptArg) suffix.subscript;

		assertEquals("上标内容应该是2", "2", superscript.token);
		assertEquals("下标内容应该是1", "1", subscript.token);

		System.out.println("✅ 上下标组合测试通过");
	}

	@Test
	public void testGroupScriptArg() throws MathParseException {
		System.out.println("\n=== 测试GroupScriptArg ===");

		MathList result = parse("x^{n+1}");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);

		SupSubSuffix suffix = term.suffix;
		assertNotNull("应该有上下标后缀", suffix);

		assertTrue("上标应该是GroupScriptArg",
				suffix.superscript instanceof GroupScriptArg);
		GroupScriptArg groupArg = (GroupScriptArg) suffix.superscript;

		MathList content = groupArg.content.content;
		assertNotNull("GroupScriptArg的内容不应为null", content);

		System.out.println("✅ GroupScriptArg测试通过");
	}

	// ============ 7. 分式测试 ============

	@Test
	public void testFracAtom() throws MathParseException {
		System.out.println("\n=== 测试FracAtom节点 ===");

		MathList result = parse("\\frac{1}{2}");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);
		Atom atom = getAtom(term);

		assertTrue("应该是FracAtom", atom instanceof FracAtom);
		FracAtom frac = (FracAtom) atom;

		assertEquals("命令应该是frac", "frac", frac.command);
		assertNotNull("分子不应为null", frac.numerator);
		assertNotNull("分母不应为null", frac.denominator);

		// 验证分子的内容
		List<Ast> numeratorElements = frac.numerator.elements;
		assertEquals("分子应该有1个元素", 1, numeratorElements.size());

		// 验证分母的内容
		List<Ast> denominatorElements = frac.denominator.elements;
		assertEquals("分母应该有1个元素", 1, denominatorElements.size());

		System.out.println("✅ FracAtom测试通过");
	}

	@Test
	public void testFracVariants() throws MathParseException {
		System.out.println("\n=== 测试Frac变体 ===");

		// 测试dfrac
		MathList result = parse("\\dfrac{a}{b}");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);
		FracAtom frac = (FracAtom) getAtom(term);
		assertEquals("命令应该是dfrac", "dfrac", frac.command);

		// 测试tfrac
		result = parse("\\tfrac{a}{b}");
		expr = getFirstExpression(result);
		term = getFirstTerm(expr);
		frac = (FracAtom) getAtom(term);
		assertEquals("命令应该是tfrac", "tfrac", frac.command);

		System.out.println("✅ Frac变体测试通过");
	}

	@Test
	public void testNestedFrac() throws MathParseException {
		System.out.println("\n=== 测试嵌套分式 ===");

		MathList result = parse("\\frac{\\frac{1}{2}}{3}");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);
		FracAtom outerFrac = (FracAtom) getAtom(term);

		// 验证外层分式
		assertNotNull("外层分式不应为null", outerFrac);

		// 验证分子是一个分式
		List<Ast> numeratorElements = outerFrac.numerator.elements;
		assertTrue("分子应该包含元素", numeratorElements.size() > 0);

		Expression innerExpr = (Expression) numeratorElements.get(0);
		Term innerTerm = (Term) innerExpr.elements.get(0);
		assertTrue("分子应该包含FracAtom", innerTerm.atom instanceof FracAtom);

		FracAtom innerFrac = (FracAtom) innerTerm.atom;
		assertNotNull("内层分式不应为null", innerFrac);

		System.out.println("✅ 嵌套分式测试通过");
	}

	// ============ 8. 根式测试 ============

	@Test
	public void testSqrtAtom() throws MathParseException {
		System.out.println("\n=== 测试SqrtAtom节点 ===");

		MathList result = parse("\\sqrt{x}");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);
		Atom atom = getAtom(term);

		assertTrue("应该是SqrtAtom", atom instanceof SqrtAtom);
		SqrtAtom sqrt = (SqrtAtom) atom;

		assertNotNull("根式内容不应为null", sqrt.content);
		assertNull("不应该有根次数", sqrt.root);

		System.out.println("✅ SqrtAtom测试通过");
	}

	@Test
	public void testSqrtWithRoot() throws MathParseException {
		System.out.println("\n=== 测试带根次数的SqrtAtom ===");

		MathList result = parse("\\sqrt[3]{27}");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);
		SqrtAtom sqrt = (SqrtAtom) getAtom(term);

		assertNotNull("根式内容不应为null", sqrt.content);
		assertNotNull("应该有根次数", sqrt.root);

		// 验证根次数
		List<Ast> rootElements = sqrt.root.elements;
		assertTrue("根次数应该包含元素", rootElements.size() > 0);

		System.out.println("✅ 带根次数的SqrtAtom测试通过");
	}

	// ============ 9. 函数测试 ============

	@Test
	public void testFunctionCallAtom() throws MathParseException {
		System.out.println("\n=== 测试FunctionCallAtom节点 ===");

		MathList result = parse("\\sin x");
		Expression expr = getFirstExpression(result);
		List<Ast> elements = expr.elements;

		// 第一个元素应该是包含FunctionCallAtom的Term
		Term funcTerm = (Term) elements.get(0);
		Atom atom = funcTerm.atom;

		assertTrue("应该是FunctionCallAtom", atom instanceof FunctionCallAtom);
		FunctionCallAtom func = (FunctionCallAtom) atom;

		assertEquals("函数名应该是sin", "sin", func.functionName);
		assertNull("不应该有下标", func.subscript);
		assertNull("不应该有上标", func.superscript);
		assertNotNull("应该有参数", func.argument);

		System.out.println("✅ FunctionCallAtom测试通过");
	}

	@Test
	public void testFunctionWithSubscript() throws MathParseException {
		System.out.println("\n=== 测试带下标的函数 ===");

		MathList result = parse("\\log_2 n");
		Expression expr = getFirstExpression(result);
		Term funcTerm = (Term) expr.elements.get(0);
		FunctionCallAtom func = (FunctionCallAtom) funcTerm.atom;

		assertEquals("函数名应该是log", "log", func.functionName);
		assertNotNull("应该有下标", func.subscript);

		assertTrue("下标应该是SingleTokenScriptArg",
				func.subscript instanceof SingleTokenScriptArg);
		SingleTokenScriptArg subscript = (SingleTokenScriptArg) func.subscript;
		assertEquals("下标内容应该是2", "2", subscript.token);

		System.out.println("✅ 带下标的函数测试通过");
	}

	// ============ 10. 大型运算符测试 ============

	@Test
	public void testLargeOperatorAtom() throws MathParseException {
		System.out.println("\n=== 测试LargeOperatorAtom节点 ===");

		MathList result = parse("\\sum_{i=1}^{n}");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);
		Atom atom = getAtom(term);

		assertTrue("应该是LargeOperatorAtom", atom instanceof LargeOperatorAtom);
		LargeOperatorAtom op = (LargeOperatorAtom) atom;

		assertEquals("运算符名应该是sum", "sum", op.op);
		assertNotNull("应该有下标", op.subscript);
		assertNotNull("应该有上标", op.superscript);

		// 验证下标和上标都是GroupScriptArg
		assertTrue("下标应该是GroupScriptArg", op.subscript instanceof GroupScriptArg);
		assertTrue("上标应该是GroupScriptArg", op.superscript instanceof GroupScriptArg);

		System.out.println("✅ LargeOperatorAtom测试通过");
	}

	@Test
	public void testIntegralOperator() throws MathParseException {
		System.out.println("\n=== 测试积分运算符 ===");

		MathList result = parse("\\int_0^1");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);
		LargeOperatorAtom op = (LargeOperatorAtom) getAtom(term);

		assertEquals("运算符名应该是int", "int", op.op);
		assertNotNull("应该有下标", op.subscript);
		assertNotNull("应该有上标", op.superscript);

		System.out.println("✅ 积分运算符测试通过");
	}

	// ============ 11. 分组测试 ============

	@Test
	public void testGroupAtom() throws MathParseException {
		System.out.println("\n=== 测试GroupAtom节点 ===");

		MathList result = parse("{x+y}");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);
		Atom atom = getAtom(term);

		assertTrue("应该是GroupAtom", atom instanceof GroupAtom);
		GroupAtom group = (GroupAtom) atom;

		assertNotNull("分组内容不应为null", group.content);
		List<Ast> content = group.content.elements;
		assertTrue("分组应该包含元素", content.size() > 0);

		System.out.println("✅ GroupAtom测试通过");
	}

	// ============ 12. 定界符测试 ============

	@Test
	public void testDelimitedAtom() throws MathParseException {
		System.out.println("\n=== 测试DelimitedAtom节点 ===");

		MathList result = parse("\\left( x \\right)");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);
		Atom atom = getAtom(term);

		assertTrue("应该是DelimitedAtom", atom instanceof DelimitedAtom);
		DelimitedAtom delim = (DelimitedAtom) atom;

		assertEquals("级别应该是0 (left/right)", 0, delim.level);
		assertEquals("左定界符应该是(", "(", delim.leftDelimiter);
		assertEquals("右定界符应该是)", ")", delim.rightDelimiter);
		assertNotNull("内容不应为null", delim.content);

		System.out.println("✅ DelimitedAtom测试通过");
	}

	// ============ 13. 文本测试 ============

	@Test
	public void testTextAtom() throws MathParseException {
		System.out.println("\n=== 测试TextAtom节点 ===");

		MathList result = parse("\\text{hello}");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);
		Atom atom = getAtom(term);

		assertTrue("应该是TextAtom", atom instanceof TextAtom);
		TextAtom text = (TextAtom) atom;

		assertEquals("命令应该是text", "text", text.command);
		assertEquals("文本内容应该是hello", "hello", text.text);

		System.out.println("✅ TextAtom测试通过");
	}

	// ============ 14. 重音符号测试 ============

	@Test
	public void testAccentAtom() throws MathParseException {
		System.out.println("\n=== 测试AccentAtom节点 ===");

		MathList result = parse("\\hat{x}");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);
		Atom atom = getAtom(term);

		assertTrue("应该是AccentAtom", atom instanceof AccentAtom);
		AccentAtom accent = (AccentAtom) atom;

		assertEquals("重音命令应该是hat", "hat", accent.cmd);
		assertNotNull("重音内容不应为null", accent.content);

		System.out.println("✅ AccentAtom测试通过");
	}

	// ============ 15. 字体命令测试 ============

	@Test
	public void testFontAtom() throws MathParseException {
		System.out.println("\n=== 测试FontAtom节点 ===");

		MathList result = parse("\\mathbf{x}");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);
		Atom atom = getAtom(term);

		assertTrue("应该是FontAtom", atom instanceof FontAtom);
		FontAtom font = (FontAtom) atom;

		assertEquals("字体命令应该是mathbf", "mathbf", font.styles);
		assertNotNull("字体内容不应为null", font.ast);

		System.out.println("✅ FontAtom测试通过");
	}

	// ============ 16. 空格命令测试 ============

	@Test
	public void testSpacing() throws MathParseException {
		System.out.println("\n=== 测试Spacing节点 ===");

		MathList result = parse("a\\quad b");
		List<Ast> elements = result.elements;

		// 应该有3个元素：Expression(a), Spacing, Expression(b)
		assertTrue("应该至少有3个元素", elements.size() >= 3);
		assertTrue("第二个元素应该是Spacing", elements.get(1) instanceof Spacing);

		Spacing spacing = (Spacing) elements.get(1);
		assertEquals("空格命令应该是quad", "quad", spacing.cmd);

		System.out.println("✅ Spacing测试通过");
	}

	// ============ 17. 复杂表达式测试 ============

	@Test
	public void testComplexExpression() throws MathParseException {
		System.out.println("\n=== 测试复杂表达式AST结构 ===");

		// 二次公式: (-b±√(b²-4ac))/(2a)
		MathList result = parse("\\frac{-b\\pm\\sqrt{b^2-4ac}}{2a}");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);

		assertTrue("根节点应该是FracAtom", term.atom instanceof FracAtom);
		FracAtom frac = (FracAtom) term.atom;

		// 验证分子包含内容
		assertNotNull("分子不应为null", frac.numerator);
		assertTrue("分子应该包含元素", frac.numerator.elements.size() > 0);

		// 验证分母包含内容
		assertNotNull("分母不应为null", frac.denominator);
		assertTrue("分母应该包含元素", frac.denominator.elements.size() > 0);

		System.out.println("✅ 复杂表达式AST结构测试通过");
	}

	@Test
	public void testMultipleTermsExpression() throws MathParseException {
		System.out.println("\n=== 测试多项式表达式 ===");

		// ax^2 + bx + c
		MathList result = parse("ax^2+bx+c");
		Expression expr = getFirstExpression(result);
		List<Ast> elements = expr.elements;

		// 应该有5个元素: Term(ax^2) BinOp(+) Term(bx) BinOp(+) Term(c)
		assertEquals("应该有5个元素", 5, elements.size());

		// 验证第一项
		assertTrue("第1个应该是Term", elements.get(0) instanceof Term);
		Term term1 = (Term) elements.get(0);
		assertTrue("第1项的Atom应该是VariableAtom", term1.atom instanceof VariableAtom);
		assertNotNull("第1项应该有上标", term1.suffix);

		// 验证运算符
		assertTrue("第2个应该是BinOpAtom", elements.get(1) instanceof BinOpAtom);
		BinOpAtom op1 = (BinOpAtom) elements.get(1);
		assertEquals("第1个运算符应该是+", "+", op1.op);

		// 验证第二项
		assertTrue("第3个应该是Term", elements.get(2) instanceof Term);

		// 验证第二个运算符
		assertTrue("第4个应该是BinOpAtom", elements.get(3) instanceof BinOpAtom);
		BinOpAtom op2 = (BinOpAtom) elements.get(3);
		assertEquals("第2个运算符应该是+", "+", op2.op);

		// 验证第三项
		assertTrue("第5个应该是Term", elements.get(4) instanceof Term);

		System.out.println("✅ 多项式表达式测试通过");
	}

	// ============ 18. MathList结构测试 ============

	@Test
	public void testMathListStructure() throws MathParseException {
		System.out.println("\n=== 测试MathList结构 ===");

		MathList result = parse("x+y");
		assertNotNull("MathList不应为null", result);

		List<Ast> ast = result.elements;
		assertNotNull("AST列表不应为null", ast);
		assertTrue("AST应该包含元素", ast.size() > 0);

		// 验证toLatex方法
		String latex = result.toString();
		assertNotNull("toLatex输出不应为null", latex);
		assertTrue("toLatex输出不应为空", latex.length() > 0);

		System.out.println("✅ MathList结构测试通过");
	}

	// ============ 19. 边界情况测试 ============

	@Test
	public void testEmptyGroup() throws MathParseException {
		System.out.println("\n=== 测试空分组 ===");

		MathList result = parse("{}");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);
		GroupAtom group = (GroupAtom) getAtom(term);

		assertNotNull("分组内容不应为null", group.content);
		assertEquals("空分组应该有0个元素", 0, group.content.elements.size());

		System.out.println("✅ 空分组测试通过");
	}

	@Test
	public void testSingleDigitNumber() throws MathParseException {
		System.out.println("\n=== 测试单个数字 ===");

		MathList result = parse("5");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);
		NumberAtom num = (NumberAtom) getAtom(term);

		assertEquals("数字应该是5", "5", num.value);
		assertNull("不应该有一元运算符", term.unaryOp);
		assertNull("不应该有上下标", term.suffix);

		System.out.println("✅ 单个数字测试通过");
	}

	// ============ 20. 综合测试 ============

	@Test
	public void testFullAstTraversal() throws MathParseException {
		System.out.println("\n=== 测试完整AST遍历 ===");

		// 测试一个包含多种节点类型的表达式
		String input = "\\sum_{i=1}^{n} x_i^2";
		MathList result = parse(input);

		// 验证MathList
		assertNotNull("MathList不应为null", result);
		List<Ast> topElements = result.elements;
		assertTrue("应该包含元素", topElements.size() > 0);

		// 获取第一个Expression
		Expression expr = (Expression) topElements.get(0);
		List<Ast> exprElements = expr.elements;

		// 第一个Term应该包含LargeOperatorAtom
		Term sumTerm = (Term) exprElements.get(0);
		assertTrue("应该是LargeOperatorAtom", sumTerm.atom instanceof LargeOperatorAtom);
		LargeOperatorAtom sumOp = (LargeOperatorAtom) sumTerm.atom;
		assertEquals("运算符应该是sum", "sum", sumOp.op);
		assertNotNull("sum应该有下标", sumOp.subscript);
		assertNotNull("sum应该有上标", sumOp.superscript);

		// 应该有第二个Term (x_i^2)
		if (exprElements.size() > 1) {
			Term xTerm = (Term) exprElements.get(1);
			assertNotNull("x项应该有上下标", xTerm.suffix);
			SupSubSuffix suffix = xTerm.suffix;
			assertNotNull("应该有上标", suffix.superscript);
			assertNotNull("应该有下标", suffix.subscript);
		}

		System.out.println("✅ 完整AST遍历测试通过");
	}

	@Test
	public void testAstConsistency() throws MathParseException {
		System.out.println("\n=== 测试AST一致性 ===");

		String[] testCases = {
				"x", "123", "\\alpha", "{x}",
				"x^2", "x_1", "x^2_1",
				"a+b", "a-b", "a\\times b",
				"\\frac{1}{2}", "\\sqrt{x}", "\\sin x",
				"\\sum_{i=1}^{n}", "\\left(x\\right)"
		};

		for (String input : testCases) {
			MathList result = parse(input);
			assertNotNull("解析结果不应为null: " + input, result);

			String latex = result.toString();
			assertNotNull("toLatex结果不应为null: " + input, latex);
			assertTrue("toLatex结果不应为空: " + input, latex.length() > 0);

			System.out.println("  ✓ " + input + " → " + latex);
		}

		System.out.println("✅ AST一致性测试通过");
	}
}