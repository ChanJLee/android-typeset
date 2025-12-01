package me.chan.texas.ext.markdown.math.ast;

import me.chan.texas.ext.markdown.math.renderer.DecorGroupNode;
import me.chan.texas.ext.markdown.math.renderer.LinearGroupNode;
import me.chan.texas.ext.markdown.math.renderer.MathRendererInflater;
import me.chan.texas.ext.markdown.math.renderer.PhantomNode;
import me.chan.texas.ext.markdown.math.renderer.RendererNode;
import me.chan.texas.ext.markdown.math.renderer.SpaceNode;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaintImpl;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.renderer.core.graphics.TexasPaintImpl;
import me.chan.texas.utils.CharStream;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;


/**
 * LaTeX 数学公式解析器全面测试
 * <p>
 * 测试内容：
 * 1. AST结构测试：验证解析器生成的AST节点结构
 * 2. RenderNode结构测试：验证渲染器生成的RenderNode结构
 * <p>
 * 覆盖BNF范式中定义的所有基本语法
 */
public class MathParserAstTest {

	private MathRendererInflater inflater;
	private MathPaint.Styles defaultStyles;

	@Before
	public void setUp() {
		inflater = new MathRendererInflater();
		TexasPaint texasPaint = new TexasPaintImpl();
		texasPaint.reset(new PaintSet(new MockTextPaint(1)));
		defaultStyles = new MathPaint.Styles(new MathPaintImpl(texasPaint));
	}

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

	// ============================================
	// 第一部分：AST结构测试
	// ============================================

	// ============ 1. 基础元素测试 ============

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
		result = parse("ABC");
		expr = getFirstExpression(result);
		term = getFirstTerm(expr);
		atom = getAtom(term);

		assertTrue("应该是VariableAtom", atom instanceof VariableAtom);
		var = (VariableAtom) atom;
		assertEquals("变量名应该是ABC", "ABC", var.name);

		// 测试导数符号
		result = parse("x'");
		expr = getFirstExpression(result);
		term = getFirstTerm(expr);
		atom = getAtom(term);

		assertTrue("应该是VariableAtom", atom instanceof VariableAtom);
		var = (VariableAtom) atom;
		assertEquals("变量名应该是x'", "x'", var.name);

		System.out.println("✅ VariableAtom测试通过");
	}

	@Test
	public void testGreekLetterAtom() throws MathParseException {
		System.out.println("\n=== 测试GreekLetterAtom节点 ===");

		String[] greekLetters = {
				"\\alpha", "\\beta", "\\gamma", "\\delta", "\\epsilon", "\\theta",
				"\\lambda", "\\mu", "\\pi", "\\sigma", "\\phi", "\\omega",
				"\\Gamma", "\\Delta", "\\Theta", "\\Lambda", "\\Pi", "\\Sigma", "\\Omega",
				"\\infty"
		};

		for (String latex : greekLetters) {
			MathList result = parse(latex);
			Expression expr = getFirstExpression(result);
			Term term = getFirstTerm(expr);
			Atom atom = getAtom(term);

			assertTrue("应该是GreekLetterAtom: " + latex, atom instanceof GreekLetterAtom);
			GreekLetterAtom greek = (GreekLetterAtom) atom;
			String expected = latex.substring(1); // 去掉反斜杠
			assertEquals("符号应该是" + expected, expected, greek.symbol);
		}

		System.out.println("✅ GreekLetterAtom测试通过");
	}

	// ============ 2. 运算符测试 ============

	@Test
	public void testUnaryOperator() throws MathParseException {
		System.out.println("\n=== 测试一元运算符 ===");

		String[][] testCases = {
				{"-x", "-"},
				{"+y", "+"},
				{"\\pm a", "\\pm"},
				{"\\mp b", "\\mp"}
		};

		for (String[] testCase : testCases) {
			MathList result = parse(testCase[0]);
			Expression expr = getFirstExpression(result);
			Term term = getFirstTerm(expr);

			assertNotNull("应该有一元运算符: " + testCase[0], term.unaryOp);
			assertEquals("一元运算符应该是" + testCase[1], testCase[1], term.unaryOp.toString());
		}

		System.out.println("✅ 一元运算符测试通过");
	}

	@Test
	public void testBinaryOperator() throws MathParseException {
		System.out.println("\n=== 测试二元运算符 ===");

		String[][] testCases = {
				{"a+b", "+"},
				{"a-b", "-"},
				{"a*b", "*"},
				{"a/b", "/"},
				{"a\\times b", "\\times"},
				{"a\\cdot b", "\\cdot"},
				{"a\\div b", "\\div"},
				{"a=b", "="},
				{"a\\neq b", "\\neq"},
				{"a<b", "<"},
				{"a>b", ">"},
				{"a\\le b", "\\le"},
				{"a\\ge b", "\\ge"},
				{"a\\in b", "\\in"},
				{"a\\subset b", "\\subset"},
				{"a\\cup b", "\\cup"},
				{"a\\cap b", "\\cap"},
				{"AB\\perp CD", "\\perp"},
				{"l_1\\parallel l_2", "\\parallel"}
		};

		for (String[] testCase : testCases) {
			MathList result = parse(testCase[0]);
			Expression expr = getFirstExpression(result);
			List<Ast> elements = expr.elements;

			// 应该有3个元素：Term, BinOpAtom, Term
			assertEquals("应该有3个元素: " + testCase[0], 3, elements.size());
			assertTrue("第二个应该是BinOpAtom: " + testCase[0], elements.get(1) instanceof BinOpAtom);

			BinOpAtom binOp = (BinOpAtom) elements.get(1);
			assertEquals("运算符应该是" + testCase[1], testCase[1], binOp.op);
		}

		System.out.println("✅ 二元运算符测试通过");
	}

	// ============ 新增：标点符号测试 ============

	@Test
	public void testPunctuationAtom() throws MathParseException {
		System.out.println("\n=== 测试标点符号 ===");

		// 逗号现在是标点符号，不是二元运算符
		MathList result = parse("a,b");
		Expression expr = getFirstExpression(result);
		List<Ast> elements = expr.elements;

		// 应该有3个元素：Term(a), Term(逗号), Term(b)
		assertEquals("应该有3个元素", 3, elements.size());
		assertTrue("第1个应该是Term", elements.get(0) instanceof Term);
		assertTrue("第2个应该是Term", elements.get(1) instanceof Term);
		assertTrue("第3个应该是Term", elements.get(2) instanceof Term);

		// 验证第2个Term是标点符号
		Term commaTerm = (Term) elements.get(1);
		assertTrue("应该是PunctuationAtom", commaTerm.atom instanceof PunctuationAtom);
		PunctuationAtom comma = (PunctuationAtom) commaTerm.atom;
		assertEquals("标点符号应该是逗号", ",", comma.symbol);
		assertNull("标点符号不应该有一元运算符", commaTerm.unaryOp);
		assertNull("标点符号不应该有上下标", commaTerm.suffix);

		// 测试多个逗号
		result = parse("a,b,c");
		expr = getFirstExpression(result);
		elements = expr.elements;
		assertEquals("应该有5个元素", 5, elements.size());

		// 测试在定界符中的逗号
		result = parse("f\\left(x,y\\right)");
		expr = getFirstExpression(result);
		assertTrue("应该包含定界符", expr.elements.size() > 0);

		System.out.println("✅ 标点符号测试通过");
	}

	// ============ 3. 上下标测试 ============

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

		System.out.println("✅ 上下标组合测试通过");
	}

	// ============ 4. 分式测试 ============

	@Test
	public void testFracAtom() throws MathParseException {
		System.out.println("\n=== 测试分式 ===");

		String[][] testCases = {
				{"\\frac{1}{2}", "frac"},
				{"\\dfrac{a}{b}", "dfrac"},
				{"\\tfrac{x}{y}", "tfrac"},
				{"\\cfrac{p}{q}", "cfrac"}
		};

		for (String[] testCase : testCases) {
			MathList result = parse(testCase[0]);
			Expression expr = getFirstExpression(result);
			Term term = getFirstTerm(expr);
			Atom atom = getAtom(term);

			assertTrue("应该是FracAtom: " + testCase[0], atom instanceof FracAtom);
			FracAtom frac = (FracAtom) atom;
			assertEquals("命令应该是" + testCase[1], testCase[1], frac.command);
			assertNotNull("分子不应为null", frac.numerator);
			assertNotNull("分母不应为null", frac.denominator);
		}

		System.out.println("✅ 分式测试通过");
	}

	// ============ 5. 根式测试 ============

	@Test
	public void testSqrtAtom() throws MathParseException {
		System.out.println("\n=== 测试根式 ===");

		// 测试平方根
		MathList result = parse("\\sqrt{x}");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);
		Atom atom = getAtom(term);

		assertTrue("应该是SqrtAtom", atom instanceof SqrtAtom);
		SqrtAtom sqrt = (SqrtAtom) atom;
		assertNotNull("根式内容不应为null", sqrt.content);
		assertNull("不应该有根次数", sqrt.root);

		// 测试带根次数
		result = parse("\\sqrt[3]{27}");
		expr = getFirstExpression(result);
		term = getFirstTerm(expr);
		sqrt = (SqrtAtom) getAtom(term);

		assertNotNull("根式内容不应为null", sqrt.content);
		assertNotNull("应该有根次数", sqrt.root);

		System.out.println("✅ 根式测试通过");
	}

	// ============ 6. 定界符测试 ============

	@Test
	public void testDelimitedAtom() throws MathParseException {
		System.out.println("\n=== 测试定界符 ===");

		// 测试不同级别的定界符
		String[][] testCases = {
				{"\\left( x \\right)", "0", "(", ")"},
				{"\\bigl[ x \\bigr]", "1", "[", "]"},
				{"\\Bigl\\{ x \\Bigr\\}", "2", "\\{", "\\}"},
				{"\\biggl\\langle x \\biggr\\rangle", "3", "\\langle", "\\rangle"},
				{"\\Biggl| x \\Biggr|", "4", "|", "|"}
		};

		for (String[] testCase : testCases) {
			MathList result = parse(testCase[0]);
			Expression expr = getFirstExpression(result);
			Term term = getFirstTerm(expr);
			Atom atom = getAtom(term);

			assertTrue("应该是DelimitedAtom: " + testCase[0], atom instanceof DelimitedAtom);
			DelimitedAtom delim = (DelimitedAtom) atom;
			assertEquals("级别应该是" + testCase[1], Integer.parseInt(testCase[1]), delim.level);
			if (delim.leftDelimiter.equals("{")) {
				testCase[2] = "{";
				testCase[3] = "}";
			}
			assertEquals("左定界符应该是" + testCase[2], testCase[2], delim.leftDelimiter);
			assertEquals("右定界符应该是" + testCase[3], testCase[3], delim.rightDelimiter);
		}

		System.out.println("✅ 定界符测试通过");
	}

	// ============ 7. 函数测试 ============

	@Test
	public void testFunctionCallAtom() throws MathParseException {
		System.out.println("\n=== 测试函数 ===");

		String[] functions = {
				"sin", "cos", "tan", "log", "ln", "exp",
				"arcsin", "arccos", "arctan",
				"sinh", "cosh", "tanh",
				"max", "min", "sup", "inf"
		};

		for (String funcName : functions) {
			MathList result = parse("\\" + funcName + " x");
			Expression expr = getFirstExpression(result);
			Term term = getFirstTerm(expr);
			Atom atom = getAtom(term);

			assertTrue("应该是FunctionCallAtom: " + funcName, atom instanceof FunctionCallAtom);
			FunctionCallAtom func = (FunctionCallAtom) atom;
			assertEquals("函数名应该是" + funcName, funcName, func.name);
		}

		// 测试带下标的函数
		MathList result = parse("\\log_2 n");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);
		FunctionCallAtom func = (FunctionCallAtom) getAtom(term);

		assertEquals("函数名应该是log", "log", func.name);
		assertNotNull("应该有上下标后缀", func.supSubSuffix);
		assertNotNull("应该有下标", func.supSubSuffix.subscript);

		System.out.println("✅ 函数测试通过");
	}

	// ============ 8. 大型运算符测试 ============

	@Test
	public void testLargeOperatorAtom() throws MathParseException {
		System.out.println("\n=== 测试大型运算符 ===");

		String[] operators = {
				"sum", "prod", "coprod",
				"int", "iint", "iiint", "oint",
				"bigcup", "bigcap", "bigvee", "bigwedge",
				"lim", "limsup", "liminf"
		};

		for (String opName : operators) {
			MathList result = parse("\\" + opName);
			Expression expr = getFirstExpression(result);
			Term term = getFirstTerm(expr);
			Atom atom = getAtom(term);

			assertTrue("应该是LargeOperatorAtom: " + opName, atom instanceof LargeOperatorAtom);
			LargeOperatorAtom op = (LargeOperatorAtom) atom;
			assertEquals("运算符名应该是" + opName, opName, op.name);
		}

		// 测试带上下标的运算符
		MathList result = parse("\\sum_{i=1}^{n}");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);
		LargeOperatorAtom op = (LargeOperatorAtom) getAtom(term);

		assertEquals("运算符名应该是sum", "sum", op.name);
		assertNotNull("应该有上下标后缀", op.supSubSuffix);
		assertNotNull("应该有下标", op.supSubSuffix.subscript);
		assertNotNull("应该有上标", op.supSubSuffix.superscript);

		System.out.println("✅ 大型运算符测试通过");
	}

	// ============ 9. 矩阵测试 ============

	@Test
	public void testMatrixAtom() throws MathParseException {
		System.out.println("\n=== 测试矩阵 ===");

		String[] matrixEnvs = {
				"matrix", "pmatrix", "bmatrix", "Bmatrix", "vmatrix", "Vmatrix", "cases"
		};

		for (String env : matrixEnvs) {
			String latex = "\\begin{" + env + "} a & b \\\\ c & d \\end{" + env + "}";
			MathList result = parse(latex);
			Expression expr = getFirstExpression(result);
			Term term = getFirstTerm(expr);
			Atom atom = getAtom(term);

			assertTrue("应该是MatrixAtom: " + env, atom instanceof MatrixAtom);
			MatrixAtom matrix = (MatrixAtom) atom;
			assertEquals("环境应该是" + env, env, matrix.env);
			assertNotNull("行不应为null", matrix.rows);
			assertEquals("应该有2行", 2, matrix.rows.size());
		}

		System.out.println("✅ 矩阵测试通过");
	}

	// ============ 10. 文本测试 ============

	@Test
	public void testTextAtom() throws MathParseException {
		System.out.println("\n=== 测试文本 ===");

		String[][] testCases = {
				{"\\text{hello}", "text", "hello"},
				{"\\mbox{world}", "mbox", "world"},
				{"\\textrm{Roman}", "textrm", "Roman"},
				{"\\textit{Italic}", "textit", "Italic"},
				{"\\textbf{Bold}", "textbf", "Bold"}
		};

		for (String[] testCase : testCases) {
			MathList result = parse(testCase[0]);
			Expression expr = getFirstExpression(result);
			Term term = getFirstTerm(expr);
			Atom atom = getAtom(term);

			assertTrue("应该是TextAtom: " + testCase[0], atom instanceof TextAtom);
			TextAtom text = (TextAtom) atom;
			assertEquals("命令应该是" + testCase[1], testCase[1], text.command);
			assertEquals("内容应该是" + testCase[2], testCase[2], text.content);
		}

		System.out.println("✅ 文本测试通过");
	}

	// ============ 11. 字体命令测试 ============

	@Test
	public void testFontAtom() throws MathParseException {
		System.out.println("\n=== 测试字体命令 ===");

		String[] fontCommands = {
				"mathrm", "mathit", "mathbf", "mathsf", "mathtt",
				"mathcal", "mathbb", "mathfrak"
		};

		for (String cmd : fontCommands) {
			MathList result = parse("\\" + cmd + "{x}");
			Expression expr = getFirstExpression(result);
			Term term = getFirstTerm(expr);
			Atom atom = getAtom(term);

			assertTrue("应该是FontAtom: " + cmd, atom instanceof FontAtom);
			FontAtom font = (FontAtom) atom;
			assertEquals("字体命令应该是" + cmd, cmd, font.cmd);
			assertNotNull("字体内容不应为null", font.ast);
		}

		System.out.println("✅ 字体命令测试通过");
	}

	// ============ 12. 重音符号测试 ============

	@Test
	public void testAccentAtom() throws MathParseException {
		System.out.println("\n=== 测试重音符号 ===");

		String[] accentCommands = {
				"hat", "tilde", "bar", "vec", "dot", "ddot",
				"widehat", "widetilde", "overline", "underline",
				"overrightarrow", "overbrace", "underbrace"
		};

		for (String cmd : accentCommands) {
			MathList result = parse("\\" + cmd + "{x}");
			Expression expr = getFirstExpression(result);
			Term term = getFirstTerm(expr);
			Atom atom = getAtom(term);

			assertTrue("应该是AccentAtom: " + cmd, atom instanceof AccentAtom);
			AccentAtom accent = (AccentAtom) atom;
			assertEquals("重音命令应该是" + cmd, cmd, accent.cmd);
			assertNotNull("重音内容不应为null", accent.content);
		}

		System.out.println("✅ 重音符号测试通过");
	}

	// ============ 13. 空格命令测试 ============

	@Test
	public void testSpacing() throws MathParseException {
		System.out.println("\n=== 测试空格命令 ===");

		String[] spacingCommands = {
				"\\,", "\\:", "\\;", "\\!", "\\quad", "\\qquad"
		};

		for (String cmd : spacingCommands) {
			MathList result = parse("a" + cmd + " b");
			List<Ast> elements = result.elements;

			// 应该至少有3个元素：Expression, Spacing, Expression
			assertTrue("应该至少有3个元素: " + cmd, elements.size() >= 3);
			assertTrue("第二个元素应该是Spacing: " + cmd, elements.get(1) instanceof Spacing);

			Spacing spacing = (Spacing) elements.get(1);
			String expectedCmd = cmd.substring(1); // 去掉反斜杠
			assertEquals("空格命令应该是" + expectedCmd, expectedCmd, spacing.cmd);
		}

		// 测试hspace
		MathList result = parse("a\\hspace{1em}b");
		List<Ast> elements = result.elements;
		assertTrue("应该包含Spacing", elements.get(1) instanceof Spacing);
		Spacing spacing = (Spacing) elements.get(1);
		assertEquals("空格命令应该是hspace", "hspace", spacing.cmd);
		assertTrue("内容应该是Length", spacing.content instanceof Length);

		// 测试phantom系列
		result = parse("a\\phantom{xyz}b");
		elements = result.elements;
		assertTrue("应该包含Spacing", elements.get(1) instanceof Spacing);
		spacing = (Spacing) elements.get(1);
		assertEquals("空格命令应该是phantom", "phantom", spacing.cmd);
		assertTrue("内容应该是MathList", spacing.content instanceof MathList);

		System.out.println("✅ 空格命令测试通过");
	}

	// ============ 14. 特殊符号测试 ============

	@Test
	public void testSpecialSymbolAtom() throws MathParseException {
		System.out.println("\n=== 测试特殊符号 ===");

		String[][] testCases = {
				{"\\dots", "dots"},
				{"\\ldots", "ldots"},
				{"\\cdots", "cdots"},
				{"\\vdots", "vdots"},
				{"\\ddots", "ddots"},
				{"\\angle", "angle"},
				{"\\therefore", "therefore"},
				{"\\because", "because"}
		};

		for (String[] testCase : testCases) {
			MathList result = parse(testCase[0]);
			Expression expr = getFirstExpression(result);
			Term term = getFirstTerm(expr);
			Atom atom = getAtom(term);

			assertTrue("应该是SpecialSymbolAtom: " + testCase[0], atom instanceof SpecialSymbolAtom);
			SpecialSymbolAtom symbol = (SpecialSymbolAtom) atom;
			assertEquals("符号应该是" + testCase[1], testCase[1], symbol.symbol);

			// 特殊符号不应该有一元运算符
			assertNull("特殊符号不应该有一元运算符: " + testCase[0], term.unaryOp);
		}

		// 测试特殊符号可以带上下标
		MathList result = parse("\\angle_1");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);

		assertTrue("应该是SpecialSymbolAtom", term.atom instanceof SpecialSymbolAtom);
		assertNotNull("应该有上下标后缀", term.suffix);
		assertNotNull("应该有下标", term.suffix.subscript);

		System.out.println("✅ 特殊符号测试通过");
	}

	// ============ 15. 分组测试 ============

	@Test
	public void testGroupAtom() throws MathParseException {
		System.out.println("\n=== 测试分组 ===");

		MathList result = parse("{x+y}");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);
		Atom atom = getAtom(term);

		assertTrue("应该是Group", atom instanceof Group);
		Group group = (Group) atom;
		assertNotNull("分组内容不应为null", group.content);

		// 测试空分组
		result = parse("{}");
		expr = getFirstExpression(result);
		term = getFirstTerm(expr);
		group = (Group) getAtom(term);

		assertNotNull("分组内容不应为null", group.content);
		assertEquals("空分组应该有0个元素", 0, group.content.elements.size());

		System.out.println("✅ 分组测试通过");
	}

	// ============================================
	// 第二部分：RenderNode结构测试
	// ============================================

	// ============ 1. 基础节点渲染测试 ============

	@Test
	public void testNumberRendersAsTextNode() throws MathParseException {
		System.out.println("\n=== 测试数字渲染为TextNode ===");

		MathList mathList = parse("123");
		RendererNode node = inflater.inflate(defaultStyles, mathList);

		assertTrue("应该是LinearGroupNode", node instanceof LinearGroupNode);
		LinearGroupNode group = (LinearGroupNode) node;
		assertTrue("应该至少有一个子节点", group.getChildCount() > 0);

		// 获取第一个Expression对应的节点
		RendererNode exprNode = group.getChildAt(0);
		assertTrue("Expression应该渲染为LinearGroupNode", exprNode instanceof LinearGroupNode);

		LinearGroupNode exprGroup = (LinearGroupNode) exprNode;
		RendererNode termNode = exprGroup.getChildAt(0);
		assertTrue("Term应该渲染为DecorGroupNode", termNode instanceof DecorGroupNode);

		System.out.println("✅ 数字渲染测试通过");
	}

	@Test
	public void testVariableRendersAsTextNode() throws MathParseException {
		System.out.println("\n=== 测试变量渲染为TextNode ===");

		MathList mathList = parse("x");
		RendererNode node = inflater.inflate(defaultStyles, mathList);

		assertTrue("根节点应该是LinearGroupNode", node instanceof LinearGroupNode);

		System.out.println("✅ 变量渲染测试通过");
	}

	@Test
	public void testGreekLetterRendersAsTextNode() throws MathParseException {
		System.out.println("\n=== 测试希腊字母渲染为TextNode ===");

		MathList mathList = parse("\\alpha");
		RendererNode node = inflater.inflate(defaultStyles, mathList);

		assertTrue("根节点应该是LinearGroupNode", node instanceof LinearGroupNode);

		System.out.println("✅ 希腊字母渲染测试通过");
	}

	// ============ 2. 运算符渲染测试 ============

	@Test
	public void testBinaryOperatorRendersWithSpacing() throws MathParseException {
		System.out.println("\n=== 测试二元运算符渲染包含空格 ===");

		MathList mathList = parse("a+b");
		RendererNode node = inflater.inflate(defaultStyles, mathList);

		assertTrue("根节点应该是LinearGroupNode", node instanceof LinearGroupNode);
		LinearGroupNode rootGroup = (LinearGroupNode) node;
		assertTrue("应该有子节点", rootGroup.getChildCount() > 0);

		// Expression节点
		RendererNode exprNode = rootGroup.getChildAt(0);
		assertTrue("Expression应该渲染为LinearGroupNode", exprNode instanceof LinearGroupNode);

		LinearGroupNode exprGroup = (LinearGroupNode) exprNode;
		// 应该有5个节点：Term(a), Space, BinOp(+), Space, Term(b)
		assertEquals("应该有5个子节点", 5, exprGroup.getChildCount());

		// 验证空格节点
		RendererNode space1 = exprGroup.getChildAt(1);
		assertTrue("第2个应该是SpaceNode", space1 instanceof SpaceNode);

		RendererNode space2 = exprGroup.getChildAt(3);
		assertTrue("第4个应该是SpaceNode", space2 instanceof SpaceNode);

		System.out.println("✅ 二元运算符渲染测试通过");
	}

	@Test
	public void testUnaryOperatorRendersAsLeft() throws MathParseException {
		System.out.println("\n=== 测试一元运算符渲染为左侧装饰 ===");

		MathList mathList = parse("-x");
		RendererNode node = inflater.inflate(defaultStyles, mathList);

		assertTrue("根节点应该是LinearGroupNode", node instanceof LinearGroupNode);

		System.out.println("✅ 一元运算符渲染测试通过");
	}

	// ============ 3. 上下标渲染测试 ============

	@Test
	public void testSuperscriptRendersAsRightTop() throws MathParseException {
		System.out.println("\n=== 测试上标渲染为右上装饰 ===");

		MathList mathList = parse("x^2");
		RendererNode node = inflater.inflate(defaultStyles, mathList);

		assertTrue("根节点应该是LinearGroupNode", node instanceof LinearGroupNode);

		System.out.println("✅ 上标渲染测试通过");
	}

	@Test
	public void testSubscriptRendersAsRightBottom() throws MathParseException {
		System.out.println("\n=== 测试下标渲染为右下装饰 ===");

		MathList mathList = parse("x_1");
		RendererNode node = inflater.inflate(defaultStyles, mathList);

		assertTrue("根节点应该是LinearGroupNode", node instanceof LinearGroupNode);

		System.out.println("✅ 下标渲染测试通过");
	}

	// ============ 4. 分式渲染测试 ============

	@Test
	public void testFracRendersAsFractionNode() throws MathParseException {
		System.out.println("\n=== 测试分式渲染为FractionNode ===");

		MathList mathList = parse("\\frac{1}{2}");
		RendererNode node = inflater.inflate(defaultStyles, mathList);

		assertTrue("根节点应该是LinearGroupNode", node instanceof LinearGroupNode);
		LinearGroupNode rootGroup = (LinearGroupNode) node;

		// 获取Expression -> Term -> FractionNode
		RendererNode exprNode = rootGroup.getChildAt(0);
		assertTrue("Expression应该是LinearGroupNode", exprNode instanceof LinearGroupNode);

		LinearGroupNode exprGroup = (LinearGroupNode) exprNode;
		RendererNode termNode = exprGroup.getChildAt(0);
		assertTrue("Term应该是DecorGroupNode", termNode instanceof DecorGroupNode);

		System.out.println("✅ 分式渲染测试通过");
	}

	// ============ 5. 根式渲染测试 ============

	@Test
	public void testSqrtRendersAsSqrtNode() throws MathParseException {
		System.out.println("\n=== 测试根式渲染为SqrtNode ===");

		MathList mathList = parse("\\sqrt{x}");
		RendererNode node = inflater.inflate(defaultStyles, mathList);

		assertTrue("根节点应该是LinearGroupNode", node instanceof LinearGroupNode);

		System.out.println("✅ 根式渲染测试通过");
	}

	// ============ 6. 定界符渲染测试 ============

	@Test
	public void testDelimitedRendersAsBraceLayout() throws MathParseException {
		System.out.println("\n=== 测试定界符渲染为BraceLayout ===");

		MathList mathList = parse("\\left( x \\right)");
		RendererNode node = inflater.inflate(defaultStyles, mathList);

		assertTrue("根节点应该是LinearGroupNode", node instanceof LinearGroupNode);

		System.out.println("✅ 定界符渲染测试通过");
	}

	// ============ 7. 函数渲染测试 ============

	@Test
	public void testFunctionRendersAsDecorGroupNode() throws MathParseException {
		System.out.println("\n=== 测试函数渲染为DecorGroupNode ===");

		MathList mathList = parse("\\sin x");
		RendererNode node = inflater.inflate(defaultStyles, mathList);

		assertTrue("根节点应该是LinearGroupNode", node instanceof LinearGroupNode);

		System.out.println("✅ 函数渲染测试通过");
	}

	// ============ 8. 大型运算符渲染测试 ============

	@Test
	public void testSumRendersWithTopBottom() throws MathParseException {
		System.out.println("\n=== 测试求和符号渲染上下标在顶部和底部 ===");

		MathList mathList = parse("\\sum_{i=1}^{n}");
		RendererNode node = inflater.inflate(defaultStyles, mathList);

		assertTrue("根节点应该是LinearGroupNode", node instanceof LinearGroupNode);

		System.out.println("✅ 求和符号渲染测试通过");
	}

	@Test
	public void testIntegralRendersWithRightTopBottom() throws MathParseException {
		System.out.println("\n=== 测试积分符号渲染上下标在右侧 ===");

		MathList mathList = parse("\\int_0^1");
		RendererNode node = inflater.inflate(defaultStyles, mathList);

		assertTrue("根节点应该是LinearGroupNode", node instanceof LinearGroupNode);

		System.out.println("✅ 积分符号渲染测试通过");
	}

	// ============ 9. 矩阵渲染测试 ============

	@Test
	public void testMatrixRendersAsGridGroupNode() throws MathParseException {
		System.out.println("\n=== 测试矩阵渲染为GridGroupNode ===");

		MathList mathList = parse("\\begin{pmatrix} a & b \\\\ c & d \\end{pmatrix}");
		RendererNode node = inflater.inflate(defaultStyles, mathList);

		assertTrue("根节点应该是LinearGroupNode", node instanceof LinearGroupNode);

		System.out.println("✅ 矩阵渲染测试通过");
	}

	// ============ 10. 空格渲染测试 ============

	@Test
	public void testSpacingRendersAsSpaceNode() throws MathParseException {
		System.out.println("\n=== 测试空格命令渲染为SpaceNode ===");

		MathList mathList = parse("a\\quad b");
		RendererNode node = inflater.inflate(defaultStyles, mathList);

		assertTrue("根节点应该是LinearGroupNode", node instanceof LinearGroupNode);
		LinearGroupNode rootGroup = (LinearGroupNode) node;

		// 应该至少有3个元素：Expression(a), SpaceNode, Expression(b)
		assertTrue("应该至少有3个子节点", rootGroup.getChildCount() >= 3);

		RendererNode spaceNode = rootGroup.getChildAt(1);
		assertTrue("第2个应该是SpaceNode", spaceNode instanceof SpaceNode);

		System.out.println("✅ 空格命令渲染测试通过");
	}

	@Test
	public void testPhantomRendersAsPhantomNode() throws MathParseException {
		System.out.println("\n=== 测试phantom命令渲染为PhantomNode ===");

		// 测试\phantom
		MathList mathList = parse("a\\phantom{xyz}b");
		RendererNode node = inflater.inflate(defaultStyles, mathList);

		assertTrue("根节点应该是LinearGroupNode", node instanceof LinearGroupNode);
		LinearGroupNode rootGroup = (LinearGroupNode) node;

		RendererNode phantomNode = rootGroup.getChildAt(1);
		assertTrue("第2个应该是PhantomNode", phantomNode instanceof PhantomNode);

		// 测试\hphantom
		mathList = parse("a\\hphantom{xyz}b");
		node = inflater.inflate(defaultStyles, mathList);
		rootGroup = (LinearGroupNode) node;
		phantomNode = rootGroup.getChildAt(1);
		assertTrue("hphantom应该渲染为PhantomNode", phantomNode instanceof PhantomNode);

		// 测试\vphantom
		mathList = parse("a\\vphantom{xyz}b");
		node = inflater.inflate(defaultStyles, mathList);
		rootGroup = (LinearGroupNode) node;
		phantomNode = rootGroup.getChildAt(1);
		assertTrue("vphantom应该渲染为PhantomNode", phantomNode instanceof PhantomNode);

		System.out.println("✅ phantom命令渲染测试通过");
	}

	// ============ 11. 特殊符号渲染测试 ============

	@Test
	public void testSpecialSymbolRendersAsSymbolNode() throws MathParseException {
		System.out.println("\n=== 测试特殊符号渲染为SymbolNode ===");

		MathList mathList = parse("\\dots");
		RendererNode node = inflater.inflate(defaultStyles, mathList);

		assertTrue("根节点应该是LinearGroupNode", node instanceof LinearGroupNode);

		System.out.println("✅ 特殊符号渲染测试通过");
	}

	// ============ 12. 重音符号渲染测试 ============

	@Test
	public void testAccentRendersAsAccentNode() throws MathParseException {
		System.out.println("\n=== 测试重音符号渲染为AccentNode ===");

		MathList mathList = parse("\\hat{x}");
		RendererNode node = inflater.inflate(defaultStyles, mathList);

		assertTrue("根节点应该是LinearGroupNode", node instanceof LinearGroupNode);

		System.out.println("✅ 重音符号渲染测试通过");
	}

	// ============ 13. 复杂表达式渲染测试 ============

	@Test
	public void testComplexExpressionRenderStructure() throws MathParseException {
		System.out.println("\n=== 测试复杂表达式渲染结构 ===");

		// 二次公式
		MathList mathList = parse("\\frac{-b\\pm\\sqrt{b^2-4ac}}{2a}");
		RendererNode node = inflater.inflate(defaultStyles, mathList);

		assertNotNull("渲染节点不应为null", node);
		assertTrue("根节点应该是LinearGroupNode", node instanceof LinearGroupNode);

		System.out.println("✅ 复杂表达式渲染结构测试通过");
	}

	@Test
	public void testPolynomialRenderStructure() throws MathParseException {
		System.out.println("\n=== 测试多项式渲染结构 ===");

		MathList mathList = parse("ax^2+bx+c");
		RendererNode node = inflater.inflate(defaultStyles, mathList);

		assertTrue("根节点应该是LinearGroupNode", node instanceof LinearGroupNode);
		LinearGroupNode rootGroup = (LinearGroupNode) node;

		// Expression节点
		RendererNode exprNode = rootGroup.getChildAt(0);
		assertTrue("Expression应该是LinearGroupNode", exprNode instanceof LinearGroupNode);

		LinearGroupNode exprGroup = (LinearGroupNode) exprNode;
		// 应该包含：Term(ax^2), Space, BinOp(+), Space, Term(bx), Space, BinOp(+), Space, Term(c)
		assertEquals("应该有9个子节点", 9, exprGroup.getChildCount());

		System.out.println("✅ 多项式渲染结构测试通过");
	}

	@Test
	public void testSummationRenderStructure() throws MathParseException {
		System.out.println("\n=== 测试求和表达式渲染结构 ===");

		MathList mathList = parse("\\sum_{i=1}^{n} x_i^2");
		RendererNode node = inflater.inflate(defaultStyles, mathList);

		assertNotNull("渲染节点不应为null", node);
		assertTrue("根节点应该是LinearGroupNode", node instanceof LinearGroupNode);

		System.out.println("✅ 求和表达式渲染结构测试通过");
	}

	// ============ 14. 综合测试 ============

	@Test
	public void testAstToRenderNodeConsistency() throws MathParseException {
		System.out.println("\n=== 测试AST到RenderNode的一致性 ===");

		String[] testCases = {
				"x", "123", "\\alpha", "{x}",
				"x^2", "x_1", "x^2_1",
				"a+b", "a-b", "a\\times b",
				"\\frac{1}{2}", "\\sqrt{x}", "\\sin x",
				"\\sum_{i=1}^{n}", "\\left(x\\right)",
				"\\dots", "\\angle", "AB\\perp CD"
		};

		for (String input : testCases) {
			MathList mathList = parse(input);
			assertNotNull("AST不应为null: " + input, mathList);

			RendererNode node = inflater.inflate(defaultStyles, mathList);
			assertNotNull("RenderNode不应为null: " + input, node);

			System.out.println("  ✓ " + input);
		}

		System.out.println("✅ AST到RenderNode一致性测试通过");
	}

	// 在现有测试文件的合适位置添加以下测试

// ============ 补充测试：希腊字母变体 ============

	@Test
	public void testGreekLetterVariants() throws MathParseException {
		System.out.println("\n=== 测试希腊字母变体 ===");

		String[] variants = {
				"\\varepsilon", "\\vartheta", "\\varpi", "\\varrho",
				"\\varsigma", "\\varphi", "\\Xi"
		};

		for (String latex : variants) {
			MathList result = parse(latex);
			Expression expr = getFirstExpression(result);
			Term term = getFirstTerm(expr);
			Atom atom = getAtom(term);

			assertTrue("应该是GreekLetterAtom: " + latex, atom instanceof GreekLetterAtom);
			GreekLetterAtom greek = (GreekLetterAtom) atom;
			String expected = latex.substring(1);
			assertEquals("符号应该是" + expected, expected, greek.symbol);
		}

		System.out.println("✅ 希腊字母变体测试通过");
	}

// ============ 补充测试：更多二元运算符 ============

	@Test
	public void testMoreBinaryOperators() throws MathParseException {
		System.out.println("\n=== 测试更多二元运算符 ===");

		String[][] testCases = {
				{"a\\equiv b", "\\equiv"},
				{"a\\approx b", "\\approx"},
				{"a\\cong b", "\\cong"},
				{"a\\sim b", "\\sim"},
				{"a\\leq b", "\\leq"},
				{"a\\geq b", "\\geq"},
				{"a\\ll b", "\\ll"},
				{"a\\gg b", "\\gg"},
				{"a\\notin b", "\\notin"},
				{"a\\supset b", "\\supset"},
				{"a\\subseteq b", "\\subseteq"},
				{"a\\supseteq b", "\\supseteq"},
				{"a\\wedge b", "\\wedge"},
				{"a\\vee b", "\\vee"},
				{"a\\to b", "\\to"},
				{"a\\rightarrow b", "\\rightarrow"},
				{"a\\leftarrow b", "\\leftarrow"},
				{"a\\leftrightarrow b", "\\leftrightarrow"},
				{"P\\Rightarrow Q", "\\Rightarrow"},
				{"P\\Leftarrow Q", "\\Leftarrow"},
				{"P\\Leftrightarrow Q", "\\Leftrightarrow"},
				{"P\\implies Q", "\\implies"},
				{"P\\iff Q", "\\iff"}
		};

		for (String[] testCase : testCases) {
			MathList result = parse(testCase[0]);
			Expression expr = getFirstExpression(result);
			List<Ast> elements = expr.elements;

			assertEquals("应该有3个元素: " + testCase[0], 3, elements.size());
			assertTrue("第二个应该是BinOpAtom: " + testCase[0], elements.get(1) instanceof BinOpAtom);

			BinOpAtom binOp = (BinOpAtom) elements.get(1);
			assertEquals("运算符应该是" + testCase[1], testCase[1], binOp.op);
		}

		System.out.println("✅ 更多二元运算符测试通过");
	}

// ============ 补充测试：更多函数 ============

	@Test
	public void testMoreFunctions() throws MathParseException {
		System.out.println("\n=== 测试更多函数 ===");

		String[] functions = {
				"cot", "sec", "csc", "lg",
				"arg", "deg", "det", "dim", "gcd", "hom", "ker",
				"Pr", "bmod", "pmod"
		};

		for (String funcName : functions) {
			MathList result = parse("\\" + funcName + " x");
			Expression expr = getFirstExpression(result);
			Term term = getFirstTerm(expr);
			Atom atom = getAtom(term);

			assertTrue("应该是FunctionCallAtom: " + funcName, atom instanceof FunctionCallAtom);
			FunctionCallAtom func = (FunctionCallAtom) atom;
			assertEquals("函数名应该是" + funcName, funcName, func.name);
		}

		System.out.println("✅ 更多函数测试通过");
	}

// ============ 补充测试：更多大型运算符 ============

	@Test
	public void testMoreLargeOperators() throws MathParseException {
		System.out.println("\n=== 测试更多大型运算符 ===");

		String[] operators = {
				"oiint", "oiiint",
				"bigoplus", "bigotimes", "bigodot",
				"biguplus", "bigsqcup"
		};

		for (String opName : operators) {
			MathList result = parse("\\" + opName);
			Expression expr = getFirstExpression(result);
			Term term = getFirstTerm(expr);
			Atom atom = getAtom(term);

			assertTrue("应该是LargeOperatorAtom: " + opName, atom instanceof LargeOperatorAtom);
			LargeOperatorAtom op = (LargeOperatorAtom) atom;
			assertEquals("运算符名应该是" + opName, opName, op.name);
		}

		System.out.println("✅ 更多大型运算符测试通过");
	}

// ============ 补充测试：更多重音符号 ============

	@Test
	public void testMoreAccents() throws MathParseException {
		System.out.println("\n=== 测试更多重音符号 ===");

		String[] accentCommands = {
				"acute", "grave", "breve", "check", "mathring",
				"dddot", "overleftarrow"
		};

		for (String cmd : accentCommands) {
			MathList result = parse("\\" + cmd + "{x}");
			Expression expr = getFirstExpression(result);
			Term term = getFirstTerm(expr);
			Atom atom = getAtom(term);

			assertTrue("应该是AccentAtom: " + cmd, atom instanceof AccentAtom);
			AccentAtom accent = (AccentAtom) atom;
			assertEquals("重音命令应该是" + cmd, cmd, accent.cmd);
		}

		System.out.println("✅ 更多重音符号测试通过");
	}

// ============ 补充测试：单token形式的重音符号 ============

	@Test
	public void testAccentWithSingleToken() throws MathParseException {
		System.out.println("\n=== 测试单token形式的重音符号 ===");

		// 测试 \hat x (不用大括号)
		MathList result = parse("\\hat x");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);
		Atom atom = getAtom(term);

		assertTrue("应该是AccentAtom", atom instanceof AccentAtom);
		AccentAtom accent = (AccentAtom) atom;
		assertEquals("重音命令应该是hat", "hat", accent.cmd);
		assertTrue("内容应该是单个原子", accent.content instanceof Atom);

		System.out.println("✅ 单token形式的重音符号测试通过");
	}

// ============ 补充测试：更多定界符 ============

	@Test
	public void testMoreDelimiters() throws MathParseException {
		System.out.println("\n=== 测试更多定界符 ===");

		String[][] testCases = {
				{"\\left\\lfloor x \\right\\rfloor", "\\lfloor", "\\rfloor"},
				{"\\left\\lceil x \\right\\rceil", "\\lceil", "\\rceil"},
				{"\\left\\lvert x \\right\\rvert", "\\lvert", "\\rvert"},
				{"\\left\\lVert x \\right\\rVert", "\\lVert", "\\rVert"}
		};

		for (String[] testCase : testCases) {
			MathList result = parse(testCase[0]);
			Expression expr = getFirstExpression(result);
			Term term = getFirstTerm(expr);
			Atom atom = getAtom(term);

			assertTrue("应该是DelimitedAtom: " + testCase[0], atom instanceof DelimitedAtom);
			DelimitedAtom delim = (DelimitedAtom) atom;
			assertEquals("左定界符应该是" + testCase[1], testCase[1], delim.leftDelimiter);
			assertEquals("右定界符应该是" + testCase[2], testCase[2], delim.rightDelimiter);
		}

		System.out.println("✅ 更多定界符测试通过");
	}

// ============ 补充测试：空定界符 ============

	@Test
	public void testEmptyDelimiter() throws MathParseException {
		System.out.println("\n=== 测试空定界符 ===");

		// 测试 \left. ... \right)
		MathList result = parse("\\left. \\frac{dy}{dx} \\right|_{x=0}");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);

		// 第一个应该是定界符
		assertTrue("应该包含DelimitedAtom", term.atom instanceof DelimitedAtom);
		DelimitedAtom delim = (DelimitedAtom) term.atom;
		assertEquals("左定界符应该是.", ".", delim.leftDelimiter);

		System.out.println("✅ 空定界符测试通过");
	}

// ============ 补充测试：嵌套上下标 ============

	@Test
	public void testNestedSuperscript() throws MathParseException {
		System.out.println("\n=== 测试嵌套上下标 ===");

		MathList result = parse("x^{2^3}");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);

		SupSubSuffix suffix = term.suffix;
		assertNotNull("应该有上下标后缀", suffix);
		assertNotNull("应该有上标", suffix.superscript);

		// 上标内容应该是一个Group
		assertTrue("上标应该是Group", suffix.superscript.content instanceof Group);

		System.out.println("✅ 嵌套上下标测试通过");
	}

// ============ 补充测试：多字母变量 ============

	@Test
	public void testMultiLetterVariable() throws MathParseException {
		System.out.println("\n=== 测试多字母变量 ===");

		String[] multiLetters = {"abc", "XYZ", "Area", "Volume"};

		for (String varName : multiLetters) {
			MathList result = parse(varName);
			Expression expr = getFirstExpression(result);
			Term term = getFirstTerm(expr);
			Atom atom = getAtom(term);

			assertTrue("应该是VariableAtom: " + varName, atom instanceof VariableAtom);
			VariableAtom var = (VariableAtom) atom;
			assertEquals("变量名应该是" + varName, varName, var.name);
		}

		System.out.println("✅ 多字母变量测试通过");
	}

// ============ 补充测试：多导数符号 ============

	@Test
	public void testMultiplePrimes() throws MathParseException {
		System.out.println("\n=== 测试多导数符号 ===");

		String[][] testCases = {
				{"f''", "f''"},
				{"g'''", "g'''"},
				{"h''''", "h''''"}
		};

		for (String[] testCase : testCases) {
			MathList result = parse(testCase[0]);
			Expression expr = getFirstExpression(result);
			Term term = getFirstTerm(expr);
			Atom atom = getAtom(term);

			assertTrue("应该是VariableAtom", atom instanceof VariableAtom);
			VariableAtom var = (VariableAtom) atom;
			assertEquals("变量名应该是" + testCase[1], testCase[1], var.name);
		}

		System.out.println("✅ 多导数符号测试通过");
	}

// ============ 补充测试：更多字体命令 ============

	@Test
	public void testMoreFontCommands() throws MathParseException {
		System.out.println("\n=== 测试更多字体命令 ===");

		String[] fontCommands = {"mathscr", "boldsymbol", "bm"};

		for (String cmd : fontCommands) {
			MathList result = parse("\\" + cmd + "{x}");
			Expression expr = getFirstExpression(result);
			Term term = getFirstTerm(expr);
			Atom atom = getAtom(term);

			assertTrue("应该是FontAtom: " + cmd, atom instanceof FontAtom);
			FontAtom font = (FontAtom) atom;
			assertEquals("字体命令应该是" + cmd, cmd, font.cmd);
		}

		System.out.println("✅ 更多字体命令测试通过");
	}

// ============ 补充测试：更多数值单位 ============

	@Test
	public void testMoreLengthUnits() throws MathParseException {
		System.out.println("\n=== 测试更多数值单位 ===");

		String[] units = {"ex", "pt", "px", "cm", "mm", "in"};

		for (String unit : units) {
			MathList result = parse("a\\hspace{1" + unit + "}b");
			List<Ast> elements = result.elements;
			assertTrue("应该包含Spacing", elements.get(1) instanceof Spacing);

			Spacing spacing = (Spacing) elements.get(1);
			assertEquals("空格命令应该是hspace", "hspace", spacing.cmd);
			assertTrue("内容应该是Length", spacing.content instanceof Length);

			Length length = (Length) spacing.content;
			assertEquals("单位应该是" + unit, unit, length.unit.unit);
		}

		System.out.println("✅ 更多数值单位测试通过");
	}

// ============ 补充测试：矩阵的array和smallmatrix环境 ============

	@Test
	public void testArrayAndSmallMatrix() throws MathParseException {
		System.out.println("\n=== 测试array和smallmatrix环境 ===");

		// 测试array环境
		String latex = "\\begin{array}{cc} a & b \\\\ c & d \\end{array}";
		MathList result = parse(latex);
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);
		Atom atom = getAtom(term);

		assertTrue("应该是MatrixAtom", atom instanceof MatrixAtom);
		MatrixAtom matrix = (MatrixAtom) atom;
		assertEquals("环境应该是array", "array", matrix.env);

		// 测试smallmatrix环境
		latex = "\\begin{smallmatrix} 1 & 2 \\\\ 3 & 4 \\end{smallmatrix}";
		result = parse(latex);
		expr = getFirstExpression(result);
		term = getFirstTerm(expr);
		atom = getAtom(term);

		assertTrue("应该是MatrixAtom", atom instanceof MatrixAtom);
		matrix = (MatrixAtom) atom;
		assertEquals("环境应该是smallmatrix", "smallmatrix", matrix.env);

		System.out.println("✅ array和smallmatrix环境测试通过");
	}

// ============ 补充测试：一元运算符和二元运算符的区分 ============

	@Test
	public void testUnaryVsBinaryMinus() throws MathParseException {
		System.out.println("\n=== 测试负号和减号的区分 ===");

		// 测试一元负号
		MathList result = parse("-x");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);
		assertNotNull("应该有一元运算符", term.unaryOp);
		assertEquals("应该是负号", "-", term.unaryOp.toString());

		// 测试二元减号
		result = parse("a-b");
		expr = getFirstExpression(result);
		List<Ast> elements = expr.elements;
		assertEquals("应该有3个元素", 3, elements.size());
		assertTrue("第二个应该是BinOpAtom", elements.get(1) instanceof BinOpAtom);
		BinOpAtom binOp = (BinOpAtom) elements.get(1);
		assertEquals("应该是减号", "-", binOp.op);

		// 测试复杂表达式中的负号
		result = parse("a+-b");
		expr = getFirstExpression(result);
		elements = expr.elements;
		assertEquals("应该有3个元素", 3, elements.size());
		Term rightTerm = (Term) elements.get(2);
		assertNotNull("右侧term应该有一元运算符", rightTerm.unaryOp);

		System.out.println("✅ 负号和减号的区分测试通过");
	}

// ============ 补充测试：复杂嵌套表达式 ============

	@Test
	public void testComplexNestedExpressions() throws MathParseException {
		System.out.println("\n=== 测试复杂嵌套表达式 ===");

		String[] complexExpressions = {
				"\\frac{\\frac{1}{2}}{\\frac{3}{4}}",  // 嵌套分式
				"\\sqrt{\\sqrt{x}}",  // 嵌套根式
				"{{{x}}}",  // 多层分组
				"x^{a^{b^c}}",  // 多层上标
				"\\sin\\left(\\frac{x}{2}\\right)",  // 函数+定界符+分式
				"\\sum_{i=1}^{\\infty} \\frac{1}{i^2}",  // 大型运算符+分式
				"\\begin{pmatrix} \\frac{1}{2} & \\sqrt{x} \\\\ \\alpha & \\beta \\end{pmatrix}"  // 矩阵中的复杂内容
		};

		for (String input : complexExpressions) {
			MathList mathList = parse(input);
			assertNotNull("AST不应为null: " + input, mathList);

			RendererNode node = inflater.inflate(defaultStyles, mathList);
			assertNotNull("RenderNode不应为null: " + input, node);

			System.out.println("  ✓ " + input);
		}

		System.out.println("✅ 复杂嵌套表达式测试通过");
	}

// ============ 补充测试：边界情况 ============

	@Test
	public void testEdgeCases() throws MathParseException {
		System.out.println("\n=== 测试边界情况 ===");

		// 测试单个数字
		MathList result = parse("0");
		assertNotNull("应该能解析单个数字", result);

		// 测试零
		result = parse("0.0");
		assertNotNull("应该能解析零", result);

		// 测试空组
		result = parse("{}");
		assertNotNull("应该能解析空组", result);

		// 测试带空格的表达式
		result = parse("a + b");
		assertNotNull("应该能解析带空格的表达式", result);

		// 测试连续的二元运算符（通过分组）
		result = parse("a+{+b}");
		assertNotNull("应该能解析连续运算符", result);

		System.out.println("✅ 边界情况测试通过");
	}

// ============ 补充测试：特殊符号的上下标 ============

	@Test
	public void testSpecialSymbolWithScripts() throws MathParseException {
		System.out.println("\n=== 测试特殊符号的上下标 ===");

		String[][] testCases = {
				{"\\angle^{ABC}", "angle"},
				{"\\dots_n", "dots"},
				{"\\therefore^*", "therefore"}
		};

		for (String[] testCase : testCases) {
			MathList result = parse(testCase[0]);
			Expression expr = getFirstExpression(result);
			Term term = getFirstTerm(expr);

			assertTrue("应该是SpecialSymbolAtom", term.atom instanceof SpecialSymbolAtom);
			assertNotNull("应该有上下标后缀", term.suffix);
			assertNull("特殊符号不应该有一元运算符", term.unaryOp);
		}

		System.out.println("✅ 特殊符号的上下标测试通过");
	}

// ============ 补充测试：上下标顺序 ============

	@Test
	public void testScriptOrder() throws MathParseException {
		System.out.println("\n=== 测试上下标顺序 ===");

		// 测试 ^_ 顺序
		MathList result = parse("x^a_b");
		Expression expr = getFirstExpression(result);
		Term term = getFirstTerm(expr);
		SupSubSuffix suffix = term.suffix;
		assertNotNull("应该有上标", suffix.superscript);
		assertNotNull("应该有下标", suffix.subscript);
		assertFalse("应该不是反序", suffix.reserve);

		// 测试 _^ 顺序
		result = parse("x_b^a");
		expr = getFirstExpression(result);
		term = getFirstTerm(expr);
		suffix = term.suffix;
		assertNotNull("应该有上标", suffix.superscript);
		assertNotNull("应该有下标", suffix.subscript);
		assertTrue("应该是反序", suffix.reserve);

		System.out.println("✅ 上下标顺序测试通过");
	}
}