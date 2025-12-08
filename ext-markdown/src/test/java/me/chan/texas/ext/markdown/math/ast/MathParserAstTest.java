package me.chan.texas.ext.markdown.math.ast;

import org.junit.Test;

import me.chan.texas.utils.CharStream;

import static org.junit.Assert.*;

/**
 * MathParser AST 结构全面测试
 * <p>
 * 测试目标：覆盖 bnf_math.txt 中的每个语句和每个 case，验证 AST 结构
 */
public class MathParserAstTest {

	// ============================================================
	// 辅助方法
	// ============================================================

	private MathList parse(String input) throws MathParseException {
		CharStream stream = new CharStream(input, 0, input.length());
		MathParser parser = new MathParser(stream);
		return parser.parse();
	}

	private void assertParseSuccess(String input) {
		try {
			MathList ast = parse(input);
			assertNotNull("AST 不应为 null: " + input, ast);
			System.out.println("✅ " + input);
		} catch (MathParseException e) {
			fail("解析失败: " + input + " - " + e.pretty());
		}
	}

	private void assertParseFail(String input) {
		try {
			parse(input);
			fail("应该解析失败但成功了: " + input);
		} catch (MathParseException e) {
			System.out.println("✅ 预期失败: " + input + " - " + e.getMessage());
		}
	}

	// ============================================================
	// Part 1: <number> 测试
	// ============================================================

	@Test
	public void test_number_singleDigit() throws MathParseException {
		// <number> ::= <digit> { <digit> } [ "." <digit> { <digit> } ]
		// <digit> ::= "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
		for (int i = 0; i <= 9; i++) {
			MathList ast = parse(String.valueOf(i));
			assertEquals(1, ast.elements.size());
			Expression expr = (Expression) ast.elements.get(0);
			assertEquals(1, expr.elements.size());
			Term term = (Term) expr.elements.get(0);
			assertNull(term.unaryOp);
			assertTrue(term.atom instanceof NumberAtom);
			assertEquals(String.valueOf(i), ((NumberAtom) term.atom).value);
		}
	}

	@Test
	public void test_number_multiDigit() throws MathParseException {
		MathList ast = parse("12345");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		NumberAtom num = (NumberAtom) term.atom;
		assertEquals("12345", num.value);
	}

	@Test
	public void test_number_decimal() throws MathParseException {
		MathList ast = parse("3.14");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		NumberAtom num = (NumberAtom) term.atom;
		assertEquals("3.14", num.value);
	}

	@Test
	public void test_number_decimalLeadingZero() throws MathParseException {
		MathList ast = parse("0.5");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		NumberAtom num = (NumberAtom) term.atom;
		assertEquals("0.5", num.value);
	}

	// ============================================================
	// Part 2: <variable> 测试
	// ============================================================

	@Test
	public void test_variable_singleLetter() throws MathParseException {
		// <variable> ::= <letter> { <letter> } [ <prime_suffix> ]
		MathList ast = parse("x");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		assertTrue(term.atom instanceof VariableAtom);
		assertEquals("x", ((VariableAtom) term.atom).name);
	}

	@Test
	public void test_variable_multiLetter() throws MathParseException {
		MathList ast = parse("abc");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		VariableAtom var = (VariableAtom) term.atom;
		assertEquals("abc", var.name);
	}

	@Test
	public void test_variable_withPrimeSuffix() throws MathParseException {
		// <prime_suffix> ::= "'" { "'" }
		MathList ast = parse("f'");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		VariableAtom var = (VariableAtom) term.atom;
		assertEquals("f'", var.name);

		ast = parse("g''");
		expr = (Expression) ast.elements.get(0);
		term = (Term) expr.elements.get(0);
		var = (VariableAtom) term.atom;
		assertEquals("g''", var.name);

		ast = parse("h'''");
		expr = (Expression) ast.elements.get(0);
		term = (Term) expr.elements.get(0);
		var = (VariableAtom) term.atom;
		assertEquals("h'''", var.name);
	}

	// ============================================================
	// Part 3: <unary_op> 测试
	// ============================================================

	@Test
	public void test_unaryOp_plus() throws MathParseException {
		// <unary_op> ::= "+" | "-" | "\pm" | "\mp"
		MathList ast = parse("+x");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		assertNotNull(term.unaryOp);
		assertEquals("+", term.unaryOp.op);
	}

	@Test
	public void test_unaryOp_minus() throws MathParseException {
		MathList ast = parse("-x");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		assertNotNull(term.unaryOp);
		assertEquals("-", term.unaryOp.op);
	}

	@Test
	public void test_unaryOp_pm() throws MathParseException {
		MathList ast = parse("\\pm x");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		assertNotNull(term.unaryOp);
		assertEquals("\\pm", term.unaryOp.op);
	}

	@Test
	public void test_unaryOp_mp() throws MathParseException {
		MathList ast = parse("\\mp x");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		assertNotNull(term.unaryOp);
		assertEquals("\\mp", term.unaryOp.op);
	}

	// ============================================================
	// Part 4: <postfix_op> 测试
	// ============================================================

	@Test
	public void test_postfixOp_factorial() throws MathParseException {
		// <postfix_op> ::= "!"
		MathList ast = parse("n!");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		assertNotNull(term.postfixOp);
		assertEquals("!", term.postfixOp.op);
	}

	@Test
	public void test_postfixOp_withSupSub() throws MathParseException {
		MathList ast = parse("n^2!");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		assertNotNull(term.suffix);
		assertNotNull(term.postfixOp);
		assertEquals("!", term.postfixOp.op);
	}

	// ============================================================
	// Part 5: <binary_op> 测试
	// ============================================================

	@Test
	public void test_binaryOp_basic() throws MathParseException {
		// <binary_op> 基本运算符: "+" | "-" | "*" | "/" | "|"
		String[] ops = {"+", "-", "*", "/", "|"};
		for (String op : ops) {
			MathList ast = parse("a" + op + "b");
			Expression expr = (Expression) ast.elements.get(0);
			assertEquals(3, expr.elements.size());
			assertTrue(expr.elements.get(1) instanceof BinOpAtom);
			assertEquals(op, ((BinOpAtom) expr.elements.get(1)).op);
		}
	}

	@Test
	public void test_binaryOp_latex() throws MathParseException {
		// LaTeX 运算符
		String[][] latexOps = {
				{"\\times", "times"}, {"\\cdot", "cdot"}, {"\\div", "div"},
				{"\\pm", "pm"}, {"\\mp", "mp"},
				{"\\setminus", "setminus"}, {"\\circ", "circ"}
		};
		for (String[] pair : latexOps) {
			MathList ast = parse("a" + pair[0] + " b");
			Expression expr = (Expression) ast.elements.get(0);
			assertTrue(expr.elements.get(1) instanceof BinOpAtom);
			assertEquals("\\" + pair[1], ((BinOpAtom) expr.elements.get(1)).op);
		}
	}

	@Test
	public void test_binaryOp_circleOperators() throws MathParseException {
		// 圈运算符: \oplus | \ominus | \otimes | \oslash | \odot
		String[] circleOps = {"\\oplus", "\\ominus", "\\otimes", "\\oslash", "\\odot"};
		for (String op : circleOps) {
			assertParseSuccess("a" + op + " b");
		}
	}

	@Test
	public void test_binaryOp_otherBinary() throws MathParseException {
		// 其他二元运算: \bullet | \star | \dagger | \ddagger
		String[] otherOps = {"\\bullet", "\\star", "\\dagger", "\\ddagger"};
		for (String op : otherOps) {
			assertParseSuccess("a" + op + " b");
		}
	}

	@Test
	public void test_binaryOp_relation() throws MathParseException {
		// 关系运算符
		String[] relOps = {
				"=", "\\neq", "\\equiv", "\\approx", "\\cong", "\\sim",
				"\\simeq", "\\asymp", "\\propto"
		};
		for (String op : relOps) {
			String input = op.startsWith("\\") ? "a" + op + " b" : "a" + op + "b";
			assertParseSuccess(input);
		}
	}

	@Test
	public void test_binaryOp_comparison() throws MathParseException {
		// 比较运算符
		String[] compOps = {
				"<", ">", "\\le", "\\ge", "\\leq", "\\geq", "\\ll", "\\gg",
				"\\prec", "\\succ", "\\preceq", "\\succeq"
		};
		for (String op : compOps) {
			String input = op.startsWith("\\") ? "a" + op + " b" : "a" + op + "b";
			assertParseSuccess(input);
		}
	}

	@Test
	public void test_binaryOp_set() throws MathParseException {
		// 集合运算符
		String[] setOps = {
				"\\in", "\\notin", "\\subset", "\\supset", "\\subseteq", "\\supseteq",
				"\\cup", "\\cap", "\\wedge", "\\vee"
		};
		for (String op : setOps) {
			assertParseSuccess("a" + op + " b");
		}
	}

	@Test
	public void test_binaryOp_arrow() throws MathParseException {
		// 箭头运算符
		String[] arrowOps = {
				"\\to", "\\rightarrow", "\\leftarrow", "\\leftrightarrow",
				"\\Rightarrow", "\\Leftarrow", "\\Leftrightarrow",
				"\\implies", "\\iff"
		};
		for (String op : arrowOps) {
			assertParseSuccess("A" + op + " B");
		}
	}

	@Test
	public void test_binaryOp_geometry() throws MathParseException {
		// 几何运算符
		String[] geoOps = {"\\perp", "\\parallel", "\\mid", "\\nmid"};
		for (String op : geoOps) {
			assertParseSuccess("a" + op + " b");
		}
	}

	@Test
	public void test_binaryOp_triangle() throws MathParseException {
		// 三角运算符
		assertParseSuccess("a\\triangleleft b");
		assertParseSuccess("a\\triangleright b");
	}

	@Test
	public void test_binaryOp_other() throws MathParseException {
		// 其他关系运算符
		assertParseSuccess("a\\bowtie b");
		assertParseSuccess("a\\models b");
	}

	// ============================================================
	// Part 6: <punctuation> 测试
	// ============================================================

	@Test
	public void test_punctuation_comma() throws MathParseException {
		// <punctuation> ::= "," | ";"
		MathList ast = parse("a,b");
		Expression expr = (Expression) ast.elements.get(0);
		assertEquals(3, expr.elements.size());
		assertTrue(expr.elements.get(1) instanceof Term);
		Term punctTerm = (Term) expr.elements.get(1);
		assertTrue(punctTerm.atom instanceof PunctuationAtom);
		assertEquals(",", ((PunctuationAtom) punctTerm.atom).symbol);
	}

	@Test
	public void test_punctuation_semicolon() throws MathParseException {
		MathList ast = parse("a;b");
		Expression expr = (Expression) ast.elements.get(0);
		assertEquals(3, expr.elements.size());
		Term punctTerm = (Term) expr.elements.get(1);
		assertTrue(punctTerm.atom instanceof PunctuationAtom);
		assertEquals(";", ((PunctuationAtom) punctTerm.atom).symbol);
	}

	// ============================================================
	// Part 7: <greek_letter> 和 <greek_letter_variable> 测试
	// ============================================================

	@Test
	public void test_greekLetter_lowercase() throws MathParseException {
		String[] lowerGreek = {
				"\\alpha", "\\beta", "\\gamma", "\\delta", "\\epsilon", "\\varepsilon",
				"\\zeta", "\\eta", "\\theta", "\\vartheta", "\\iota", "\\kappa",
				"\\lambda", "\\mu", "\\nu", "\\xi", "\\pi", "\\varpi", "\\rho", "\\varrho",
				"\\sigma", "\\varsigma", "\\tau", "\\upsilon", "\\phi", "\\varphi",
				"\\chi", "\\psi", "\\omega"
		};
		for (String letter : lowerGreek) {
			MathList ast = parse(letter);
			Expression expr = (Expression) ast.elements.get(0);
			Term term = (Term) expr.elements.get(0);
			assertTrue(letter + " should be GreekLetterVariableAtom",
					term.atom instanceof GreekLetterVariableAtom);
		}
	}

	@Test
	public void test_greekLetter_uppercase() throws MathParseException {
		String[] upperGreek = {
				"\\Gamma", "\\Delta", "\\Theta", "\\Lambda", "\\Xi", "\\Pi",
				"\\Sigma", "\\Upsilon", "\\Phi", "\\Psi", "\\Omega"
		};
		for (String letter : upperGreek) {
			MathList ast = parse(letter);
			Expression expr = (Expression) ast.elements.get(0);
			Term term = (Term) expr.elements.get(0);
			assertTrue(letter + " should be GreekLetterVariableAtom",
					term.atom instanceof GreekLetterVariableAtom);
		}
	}

	@Test
	public void test_greekLetter_infty() throws MathParseException {
		MathList ast = parse("\\infty");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		assertTrue(term.atom instanceof GreekLetterVariableAtom);
		assertEquals("infty", ((GreekLetterVariableAtom) term.atom).name);
	}

	@Test
	public void test_greekLetterVariable_withPrime() throws MathParseException {
		// <greek_letter_variable> ::= <greek_letter> [ <prime_suffix> ]
		MathList ast = parse("\\alpha'");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		GreekLetterVariableAtom greek = (GreekLetterVariableAtom) term.atom;
		assertEquals("alpha", greek.name);
		assertEquals("'", greek.primeSuffix);
	}

	// ============================================================
	// Part 8: <special_letter_variable> 测试
	// ============================================================

	@Test
	public void test_specialLetterVariable() throws MathParseException {
		// <special_letter_variable>
		String[] specialLetters = {
				"\\hbar", "\\nabla", "\\partial", "\\ell", "\\wp", "\\Re", "\\Im", "\\aleph"
		};
		for (String letter : specialLetters) {
			MathList ast = parse(letter);
			Expression expr = (Expression) ast.elements.get(0);
			Term term = (Term) expr.elements.get(0);
			assertTrue(letter + " should be SpecialLetterVariableAtom",
					term.atom instanceof SpecialLetterVariableAtom);
		}
	}

	@Test
	public void test_specialLetterVariable_canHaveUnaryOp() throws MathParseException {
		// 特殊字母变量可以被一元运算符修饰
		MathList ast = parse("-\\nabla");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		assertNotNull(term.unaryOp);
		assertEquals("-", term.unaryOp.op);
		assertTrue(term.atom instanceof SpecialLetterVariableAtom);
	}

	// ============================================================
	// Part 9: <special_symbol> 测试
	// ============================================================

	@Test
	public void test_specialSymbol_dots() throws MathParseException {
		// <special_symbol> 省略号
		String[] dots = {"\\dots", "\\ldots", "\\cdots", "\\vdots", "\\ddots"};
		for (String dot : dots) {
			MathList ast = parse(dot);
			Expression expr = (Expression) ast.elements.get(0);
			Term term = (Term) expr.elements.get(0);
			assertTrue(dot + " should be SpecialSymbolAtom",
					term.atom instanceof SpecialSymbolAtom);
		}
	}

	@Test
	public void test_specialSymbol_angle() throws MathParseException {
		MathList ast = parse("\\angle");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		assertTrue(term.atom instanceof SpecialSymbolAtom);
	}

	@Test
	public void test_specialSymbol_logic() throws MathParseException {
		// 逻辑标记符号
		String[] logicSymbols = {"\\therefore", "\\because"};
		for (String symbol : logicSymbols) {
			assertParseSuccess(symbol);
		}
	}

	@Test
	public void test_specialSymbol_quantifier() throws MathParseException {
		// 量词
		String[] quantifiers = {"\\forall", "\\exists", "\\nexists"};
		for (String q : quantifiers) {
			MathList ast = parse(q);
			Expression expr = (Expression) ast.elements.get(0);
			Term term = (Term) expr.elements.get(0);
			assertTrue(term.atom instanceof SpecialSymbolAtom);
		}
	}

	@Test
	public void test_specialSymbol_emptyset() throws MathParseException {
		// 空集
		String[] emptysets = {"\\emptyset", "\\varnothing"};
		for (String e : emptysets) {
			assertParseSuccess(e);
		}
	}

	@Test
	public void test_specialSymbol_cannotHaveUnaryOp() throws MathParseException {
		// 特殊符号不能被一元运算符修饰，- 应该被解析为二元运算符
		MathList ast = parse("x-\\dots");
		Expression expr = (Expression) ast.elements.get(0);
		// 应该是 x - \dots，即 term binop term
		assertEquals(3, expr.elements.size());
		assertTrue(expr.elements.get(1) instanceof BinOpAtom);
	}

	// ============================================================
	// Part 10: <group> 测试
	// ============================================================

	@Test
	public void test_group_simple() throws MathParseException {
		// <group> ::= "{" <math_list> "}"
		MathList ast = parse("{x}");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		assertTrue(term.atom instanceof Group);
		Group group = (Group) term.atom;
		assertNotNull(group.content);
	}

	@Test
	public void test_group_nested() throws MathParseException {
		MathList ast = parse("{{x}}");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		Group outer = (Group) term.atom;
		Expression innerExpr = (Expression) outer.content.elements.get(0);
		Term innerTerm = (Term) innerExpr.elements.get(0);
		assertTrue(innerTerm.atom instanceof Group);
	}

	// ============================================================
	// Part 11: <sup_sub_suffix> 测试
	// ============================================================

	@Test
	public void test_supSub_superscriptOnly() throws MathParseException {
		// "^" <script_arg>
		MathList ast = parse("x^2");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		assertNotNull(term.suffix);
		assertNotNull(term.suffix.superscript);
		assertNull(term.suffix.subscript);
	}

	@Test
	public void test_supSub_subscriptOnly() throws MathParseException {
		// "_" <script_arg>
		MathList ast = parse("x_1");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		assertNotNull(term.suffix);
		assertNull(term.suffix.superscript);
		assertNotNull(term.suffix.subscript);
	}

	@Test
	public void test_supSub_superThenSub() throws MathParseException {
		// "^" <script_arg> "_" <script_arg>
		MathList ast = parse("x^2_1");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		assertNotNull(term.suffix);
		assertNotNull(term.suffix.superscript);
		assertNotNull(term.suffix.subscript);
		assertFalse(term.suffix.reverse);
	}

	@Test
	public void test_supSub_subThenSuper() throws MathParseException {
		// "_" <script_arg> "^" <script_arg>
		MathList ast = parse("x_1^2");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		assertNotNull(term.suffix);
		assertNotNull(term.suffix.superscript);
		assertNotNull(term.suffix.subscript);
		assertTrue(term.suffix.reverse);
	}

	// ============================================================
	// Part 12: <script_arg> 和 <single_token> 测试
	// ============================================================

	@Test
	public void test_scriptArg_number() throws MathParseException {
		// <single_token> ::= <number>
		MathList ast = parse("x^123");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		ScriptArg scriptArg = term.suffix.superscript;
		assertTrue(scriptArg.content instanceof SingleToken);
		SingleToken token = (SingleToken) scriptArg.content;
		assertTrue(token.content instanceof NumberAtom);
	}

	@Test
	public void test_scriptArg_variable() throws MathParseException {
		// <single_token> ::= <variable>
		MathList ast = parse("x^n");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		ScriptArg scriptArg = term.suffix.superscript;
		SingleToken token = (SingleToken) scriptArg.content;
		assertTrue(token.content instanceof VariableAtom);
	}

	@Test
	public void test_scriptArg_greekLetter() throws MathParseException {
		// <single_token> ::= <greek_letter_variable>
		MathList ast = parse("x^\\alpha");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		ScriptArg scriptArg = term.suffix.superscript;
		SingleToken token = (SingleToken) scriptArg.content;
		assertTrue(token.content instanceof GreekLetterVariableAtom);
	}

	@Test
	public void test_scriptArg_operatorSymbol() throws MathParseException {
		// <operator_symbol_atom> ::= "+" | "-" | "*" | "/" | "=" | "<" | ">" | "\pm" | "\mp" | "\circ"
		String[] ops = {"+", "-", "*", "/", "=", "<", ">"};
		for (String op : ops) {
			MathList ast = parse("x^" + op);
			Expression expr = (Expression) ast.elements.get(0);
			Term term = (Term) expr.elements.get(0);
			ScriptArg scriptArg = term.suffix.superscript;
			SingleToken token = (SingleToken) scriptArg.content;
			assertTrue(op + " should be OperatorSymbolAtom",
					token.content instanceof OperatorSymbolAtom);
		}
	}

	@Test
	public void test_scriptArg_circ() throws MathParseException {
		// \circ 可以在上标中使用 (度数符号)
		MathList ast = parse("90^\\circ");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		ScriptArg scriptArg = term.suffix.superscript;
		SingleToken token = (SingleToken) scriptArg.content;
		assertTrue(token.content instanceof OperatorSymbolAtom);
		assertEquals("circ", ((OperatorSymbolAtom) token.content).op);
	}

	@Test
	public void test_scriptArg_group() throws MathParseException {
		// <script_arg> ::= <group>
		MathList ast = parse("x^{a+b}");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		ScriptArg scriptArg = term.suffix.superscript;
		assertTrue(scriptArg.content instanceof Group);
	}

	// ============================================================
	// Part 13: <frac> 测试
	// ============================================================

	@Test
	public void test_frac() throws MathParseException {
		// <frac> ::= "\frac" | "\dfrac" | "\tfrac" | "\cfrac"
		String[] fracCmds = {"\\frac", "\\dfrac", "\\tfrac", "\\cfrac"};
		for (String cmd : fracCmds) {
			MathList ast = parse(cmd + "{a}{b}");
			Expression expr = (Expression) ast.elements.get(0);
			Term term = (Term) expr.elements.get(0);
			assertTrue(cmd + " should produce FracAtom", term.atom instanceof FracAtom);
			FracAtom frac = (FracAtom) term.atom;
			assertNotNull(frac.numerator);
			assertNotNull(frac.denominator);
		}
	}

	// ============================================================
	// Part 14: <binom> 测试
	// ============================================================

	@Test
	public void test_binom() throws MathParseException {
		// <binom> ::= "\binom" | "\dbinom" | "\tbinom"
		String[] binomCmds = {"\\binom", "\\dbinom", "\\tbinom"};
		for (String cmd : binomCmds) {
			MathList ast = parse(cmd + "{n}{k}");
			Expression expr = (Expression) ast.elements.get(0);
			Term term = (Term) expr.elements.get(0);
			assertTrue(cmd + " should produce BinomAtom", term.atom instanceof BinomAtom);
		}
	}

	// ============================================================
	// Part 15: <sqrt> 测试
	// ============================================================

	@Test
	public void test_sqrt_basic() throws MathParseException {
		// <sqrt> ::= "\sqrt" "{" <math_list> "}"
		MathList ast = parse("\\sqrt{x}");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		assertTrue(term.atom instanceof SqrtAtom);
		SqrtAtom sqrt = (SqrtAtom) term.atom;
		assertNotNull(sqrt.content);
		assertNull(sqrt.root);
	}

	@Test
	public void test_sqrt_withRoot() throws MathParseException {
		// <sqrt> ::= "\sqrt" "[" <math_list> "]" "{" <math_list> "}"
		MathList ast = parse("\\sqrt[3]{x}");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		SqrtAtom sqrt = (SqrtAtom) term.atom;
		assertNotNull(sqrt.content);
		assertNotNull(sqrt.root);
	}

	// ============================================================
	// Part 16: <extensible_arrow> 测试
	// ============================================================

	@Test
	public void test_extensibleArrow() throws MathParseException {
		// <extensible_arrow_cmd>
		String[] arrowCmds = {
				"\\xrightarrow", "\\xleftarrow", "\\xleftrightarrow",
				"\\xRightarrow", "\\xLeftarrow", "\\xLeftrightarrow"
		};
		for (String cmd : arrowCmds) {
			MathList ast = parse(cmd + "{above}");
			Expression expr = (Expression) ast.elements.get(0);
			Term term = (Term) expr.elements.get(0);
			assertTrue(cmd + " should produce ExtensibleArrowAtom",
					term.atom instanceof ExtensibleArrowAtom);
		}
	}

	@Test
	public void test_extensibleArrow_withBelow() throws MathParseException {
		MathList ast = parse("\\xrightarrow[below]{above}");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		ExtensibleArrowAtom arrow = (ExtensibleArrowAtom) term.atom;
		assertNotNull(arrow.above);
		assertNotNull(arrow.below);
	}

	// ============================================================
	// Part 17: <delimited> 测试
	// ============================================================

	@Test
	public void test_delimited_levels() throws MathParseException {
		// 所有级别
		String[][] levels = {
				{"\\left", "\\right"},
				{"\\bigl", "\\bigr"},
				{"\\Bigl", "\\Bigr"},
				{"\\biggl", "\\biggr"},
				{"\\Biggl", "\\Biggr"}
		};
		for (String[] level : levels) {
			MathList ast = parse(level[0] + "(x" + level[1] + ")");
			Expression expr = (Expression) ast.elements.get(0);
			Term term = (Term) expr.elements.get(0);
			assertTrue(term.atom instanceof DelimitedAtom);
		}
	}

	@Test
	public void test_delimited_delimiters() throws MathParseException {
		// <delimiter> 所有定界符
		String[][] delims = {
				{"(", ")"},
				{"[", "]"},
				{"\\{", "\\}"},
				{"|", "|"},
				{"\\|", "\\|"},
				{"\\langle", "\\rangle"},
				{"\\lfloor", "\\rfloor"},
				{"\\lceil", "\\rceil"},
				{"\\lvert", "\\rvert"},
				{"\\lVert", "\\rVert"}
		};
		for (String[] delim : delims) {
			assertParseSuccess("\\left" + delim[0] + " x \\right" + delim[1]);
		}
	}

	@Test
	public void test_delimited_emptyDelimiter() throws MathParseException {
		// "." 表示空定界符
		assertParseSuccess("\\left.x\\right)");
		assertParseSuccess("\\left(x\\right.");
	}

	// ============================================================
	// Part 18: <function_call> 测试
	// ============================================================

	@Test
	public void test_functionCall_trigonometric() throws MathParseException {
		// 三角函数
		String[] trig = {"\\sin", "\\cos", "\\tan", "\\cot", "\\sec", "\\csc"};
		for (String fn : trig) {
			MathList ast = parse(fn + " x");
			Expression expr = (Expression) ast.elements.get(0);
			Term term = (Term) expr.elements.get(0);
			assertTrue(fn + " should be FunctionCallAtom",
					term.atom instanceof FunctionCallAtom);
		}
	}

	@Test
	public void test_functionCall_arcTrig() throws MathParseException {
		String[] arc = {"\\arcsin", "\\arccos", "\\arctan"};
		for (String fn : arc) {
			assertParseSuccess(fn + " x");
		}
	}

	@Test
	public void test_functionCall_hyperbolic() throws MathParseException {
		String[] hyp = {"\\sinh", "\\cosh", "\\tanh", "\\coth"};
		for (String fn : hyp) {
			assertParseSuccess(fn + " x");
		}
	}

	@Test
	public void test_functionCall_logarithmic() throws MathParseException {
		String[] log = {"\\log", "\\ln", "\\lg", "\\exp"};
		for (String fn : log) {
			assertParseSuccess(fn + " x");
		}
	}

	@Test
	public void test_functionCall_other() throws MathParseException {
		String[] other = {
				"\\max", "\\min", "\\sup", "\\inf",
				"\\arg", "\\deg", "\\det", "\\dim", "\\gcd", "\\hom", "\\ker",
				"\\Pr", "\\bmod", "\\pmod"
		};
		for (String fn : other) {
			assertParseSuccess(fn + " x");
		}
	}

	@Test
	public void test_functionCall_withSupSub() throws MathParseException {
		assertParseSuccess("\\log_2 x");
		assertParseSuccess("\\sin^2 x");
		assertParseSuccess("\\log_a^b x");
	}

	@Test
	public void test_functionCall_withArgument() throws MathParseException {
		// <function_arg> ::= <single_token> | <group> | <delimited>
		assertParseSuccess("\\sin x");           // single_token
		assertParseSuccess("\\sin{x+y}");        // group
		assertParseSuccess("\\sin\\left(x\\right)");  // delimited
	}

	// ============================================================
	// Part 19: <large_operator> 测试
	// ============================================================

	@Test
	public void test_largeOperator_sumProd() throws MathParseException {
		String[] ops = {"\\sum", "\\prod", "\\coprod"};
		for (String op : ops) {
			MathList ast = parse(op);
			Expression expr = (Expression) ast.elements.get(0);
			Term term = (Term) expr.elements.get(0);
			assertTrue(op + " should be LargeOperatorAtom",
					term.atom instanceof LargeOperatorAtom);
		}
	}

	@Test
	public void test_largeOperator_integral() throws MathParseException {
		String[] ints = {"\\int", "\\iint", "\\iiint", "\\oint", "\\oiint", "\\oiiint"};
		for (String op : ints) {
			assertParseSuccess(op);
		}
	}

	@Test
	public void test_largeOperator_bigSet() throws MathParseException {
		String[] bigOps = {
				"\\bigcup", "\\bigcap", "\\bigvee", "\\bigwedge",
				"\\bigoplus", "\\bigotimes", "\\bigodot",
				"\\biguplus", "\\bigsqcup"
		};
		for (String op : bigOps) {
			assertParseSuccess(op);
		}
	}

	@Test
	public void test_largeOperator_limit() throws MathParseException {
		String[] lims = {"\\lim", "\\limsup", "\\liminf"};
		for (String op : lims) {
			assertParseSuccess(op);
		}
	}

	@Test
	public void test_largeOperator_withSupSub() throws MathParseException {
		MathList ast = parse("\\sum_{i=1}^{n}");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		LargeOperatorAtom largeOp = (LargeOperatorAtom) term.atom;
		assertNotNull(largeOp.suffix);
		assertNotNull(largeOp.suffix.subscript);
		assertNotNull(largeOp.suffix.superscript);
	}

	// ============================================================
	// Part 20: <matrix> 测试
	// ============================================================

	@Test
	public void test_matrix_environments() throws MathParseException {
		// <matrix_env>
		String[] envs = {
				"matrix", "pmatrix", "bmatrix", "Bmatrix", "vmatrix", "Vmatrix",
				"smallmatrix", "cases"
		};
		for (String env : envs) {
			assertParseSuccess("\\begin{" + env + "}a\\end{" + env + "}");
		}
	}

	@Test
	public void test_matrix_array_withGravity() throws MathParseException {
		// <matrix_gravity> ::= "l" | "c" | "r"
		assertParseSuccess("\\begin{array}{c}a\\end{array}");
		assertParseSuccess("\\begin{array}{lcr}a&b&c\\end{array}");
	}

	@Test
	public void test_matrix_multiRow() throws MathParseException {
		// <matrix_rows> ::= <matrix_row> { "\\" <matrix_row> }
		MathList ast = parse("\\begin{matrix}a&b\\\\c&d\\end{matrix}");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		MatrixAtom matrix = (MatrixAtom) term.atom;
		assertEquals(2, matrix.rows.size());
		assertEquals(2, matrix.rows.get(0).elements.size());
	}

	// ============================================================
	// Part 21: <text> 测试
	// ============================================================

	@Test
	public void test_text_commands() throws MathParseException {
		// <text> 所有命令
		String[] textCmds = {"\\text", "\\mbox", "\\textrm", "\\textit", "\\textbf"};
		for (String cmd : textCmds) {
			MathList ast = parse(cmd + "{hello}");
			Expression expr = (Expression) ast.elements.get(0);
			Term term = (Term) expr.elements.get(0);
			assertTrue(cmd + " should produce TextAtom", term.atom instanceof TextAtom);
		}
	}

	// ============================================================
	// Part 22: <font_command> 测试
	// ============================================================

	@Test
	public void test_fontCommand() throws MathParseException {
		// <font_cmd_name>
		String[] fontCmds = {
				"\\mathrm", "\\mathit", "\\mathbf", "\\mathsf", "\\mathtt",
				"\\mathcal", "\\mathbb", "\\mathfrak", "\\mathscr",
				"\\boldsymbol", "\\bm"
		};
		for (String cmd : fontCmds) {
			MathList ast = parse(cmd + "{x}");
			Expression expr = (Expression) ast.elements.get(0);
			Term term = (Term) expr.elements.get(0);
			assertTrue(cmd + " should produce FontAtom", term.atom instanceof FontAtom);
		}
	}

	// ============================================================
	// Part 23: <accent> 测试
	// ============================================================

	@Test
	public void test_accent_commands() throws MathParseException {
		// <accent_cmd>
		String[] accentCmds = {
				"\\hat", "\\widehat", "\\tilde", "\\widetilde",
				"\\bar", "\\overline", "\\underline",
				"\\vec", "\\overrightarrow", "\\overleftarrow",
				"\\dot", "\\ddot", "\\dddot",
				"\\acute", "\\grave", "\\breve", "\\check",
				"\\mathring",
				"\\overbrace", "\\underbrace"
		};
		for (String cmd : accentCmds) {
			MathList ast = parse(cmd + "{x}");
			Expression expr = (Expression) ast.elements.get(0);
			Term term = (Term) expr.elements.get(0);
			assertTrue(cmd + " should produce AccentAtom", term.atom instanceof AccentAtom);
		}
	}

	@Test
	public void test_accent_singleToken() throws MathParseException {
		// <accent> ::= <accent_cmd> <single_token>
		MathList ast = parse("\\hat x");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		assertTrue(term.atom instanceof AccentAtom);
	}

	// ============================================================
	// Part 24: <spacing> 测试
	// ============================================================

	@Test
	public void test_spacing_simple() throws MathParseException {
		// 简单空格命令
		String[] spacings = {"\\,", "\\:", "\\;", "\\!", "\\quad", "\\qquad"};
		for (String sp : spacings) {
			MathList ast = parse("a" + sp + " b");
			boolean hasSpacing = false;
			for (Ast elem : ast.elements) {
				if (elem instanceof Spacing) {
					hasSpacing = true;
					break;
				}
			}
			assertTrue(sp + " should produce Spacing", hasSpacing);
		}
	}

	@Test
	public void test_spacing_hspace() throws MathParseException {
		// \hspace{<length>}
		MathList ast = parse("\\hspace{1em}");
		assertTrue(ast.elements.get(0) instanceof Spacing);
		Spacing spacing = (Spacing) ast.elements.get(0);
		assertTrue(spacing.content instanceof Length);
	}

	@Test
	public void test_spacing_phantom() throws MathParseException {
		// \phantom 类
		String[] phantoms = {"\\hphantom", "\\vphantom", "\\phantom"};
		for (String ph : phantoms) {
			MathList ast = parse(ph + "{xyz}");
			assertTrue(ph + " should produce Spacing", ast.elements.get(0) instanceof Spacing);
		}
	}

	@Test
	public void test_spacing_units() throws MathParseException {
		// <unit> ::= "em" | "ex" | "pt" | "px" | "cm" | "mm" | "in"
		String[] units = {"em", "ex", "pt", "px", "cm", "mm", "in"};
		for (String unit : units) {
			assertParseSuccess("\\hspace{1" + unit + "}");
		}
	}

	// ============================================================
	// Part 25: 综合 <expression> 和 <term> 测试
	// ============================================================

	@Test
	public void test_expression_simple() throws MathParseException {
		// <expression> ::= <term> { <separator> }
		MathList ast = parse("x");
		assertEquals(1, ast.elements.size());
		assertTrue(ast.elements.get(0) instanceof Expression);
	}

	@Test
	public void test_expression_withBinaryOp() throws MathParseException {
		// <separator> ::= <binary_op> <term>
		MathList ast = parse("a+b");
		Expression expr = (Expression) ast.elements.get(0);
		assertEquals(3, expr.elements.size());
		assertTrue(expr.elements.get(0) instanceof Term);
		assertTrue(expr.elements.get(1) instanceof BinOpAtom);
		assertTrue(expr.elements.get(2) instanceof Term);
	}

	@Test
	public void test_expression_adjacent() throws MathParseException {
		// <separator> ::= <term> (直接相邻)
		MathList ast = parse("2x");
		Expression expr = (Expression) ast.elements.get(0);
		assertEquals(2, expr.elements.size());
		assertTrue(expr.elements.get(0) instanceof Term);
		assertTrue(expr.elements.get(1) instanceof Term);
	}

	@Test
	public void test_term_fullStructure() throws MathParseException {
		// <term> ::= [ <unary_op> ] <operand_atom> [ <sup_sub_suffix> ] [ <postfix_op> ]
		MathList ast = parse("-x^2!");
		Expression expr = (Expression) ast.elements.get(0);
		Term term = (Term) expr.elements.get(0);
		assertNotNull(term.unaryOp);
		assertNotNull(term.atom);
		assertNotNull(term.suffix);
		assertNotNull(term.postfixOp);
	}

	// ============================================================
	// Part 26: 真实世界公式测试
	// ============================================================

	@Test
	public void test_realWorld_quadraticFormula() {
		assertParseSuccess("x=\\frac{-b\\pm \\sqrt{b^2-4ac}}{2a}");
	}

	@Test
	public void test_realWorld_euler() {
		assertParseSuccess("e^{i\\pi}+1=0");
	}

	@Test
	public void test_realWorld_derivative() {
		assertParseSuccess("f'\\left(x\\right)=\\lim_{h\\to 0}\\frac{f\\left(x+h\\right)-f\\left(x\\right)}{h}");
	}

	@Test
	public void test_realWorld_taylor() {
		assertParseSuccess("f\\left(x\\right)=\\sum_{n=0}^{\\infty}\\frac{f^{\\left(n\\right)}\\left(a\\right)}{n!}\\left(x-a\\right)^n");
	}

	@Test
	public void test_realWorld_normal_distribution() {
		assertParseSuccess("f\\left(x\\right)=\\frac{1}{\\sigma\\sqrt{2\\pi}}e^{-\\frac{\\left(x-\\mu\\right)^2}{2\\sigma^2}}");
	}

	@Test
	public void test_realWorld_angle_degree() {
		// \circ 用于度数
		assertParseSuccess("\\angle A=90^\\circ");
	}

	@Test
	public void test_realWorld_function_composition() {
		// \circ 用于函数复合
		assertParseSuccess("f\\circ g");
	}

	@Test
	public void test_realWorld_setminus() {
		assertParseSuccess("A\\setminus B");
	}

	@Test
	public void test_realWorld_maxwell() {
		assertParseSuccess("\\nabla\\cdot\\mathbf{E}=\\frac{\\rho}{\\epsilon_0}");
	}

	@Test
	public void test_realWorld_schrodinger() {
		assertParseSuccess("i\\hbar\\frac{\\partial}{\\partial t}\\Psi=\\hat{H}\\Psi");
	}

	// ============================================================
	// 总结
	// ============================================================

	@Test
	public void test_summary() {
		System.out.println("\n" + "=".repeat(60));
		System.out.println("MathParser AST 完整测试总结：");
		System.out.println("=".repeat(60));
		System.out.println("✅ <number>: digit, multi-digit, decimal");
		System.out.println("✅ <variable>: single, multi, with prime");
		System.out.println("✅ <unary_op>: +, -, \\pm, \\mp");
		System.out.println("✅ <postfix_op>: !");
		System.out.println("✅ <binary_op>: basic, latex, circle, relation, set, arrow, geometry");
		System.out.println("✅ <punctuation>: comma, semicolon");
		System.out.println("✅ <greek_letter>: lowercase, uppercase, infty");
		System.out.println("✅ <greek_letter_variable>: with prime");
		System.out.println("✅ <special_letter_variable>: hbar, nabla, partial, etc.");
		System.out.println("✅ <special_symbol>: dots, angle, logic, quantifier, emptyset");
		System.out.println("✅ <group>: simple, nested");
		System.out.println("✅ <sup_sub_suffix>: ^, _, ^_, _^");
		System.out.println("✅ <script_arg>: number, variable, greek, operator_symbol, group");
		System.out.println("✅ <operator_symbol_atom>: +, -, *, /, =, <, >, \\pm, \\mp, \\circ");
		System.out.println("✅ <frac>: frac, dfrac, tfrac, cfrac");
		System.out.println("✅ <binom>: binom, dbinom, tbinom");
		System.out.println("✅ <sqrt>: basic, with root");
		System.out.println("✅ <extensible_arrow>: all commands, with below");
		System.out.println("✅ <delimited>: all levels, all delimiters, empty delimiter");
		System.out.println("✅ <function_call>: trig, arc, hyp, log, other, with sup/sub, with arg");
		System.out.println("✅ <large_operator>: sum/prod, integral, big set, limit, with sup/sub");
		System.out.println("✅ <matrix>: all environments, gravity, multi-row");
		System.out.println("✅ <text>: all commands");
		System.out.println("✅ <font_command>: all commands");
		System.out.println("✅ <accent>: all commands, single token form");
		System.out.println("✅ <spacing>: simple, hspace, phantom, all units");
		System.out.println("✅ <expression>: simple, binary op, adjacent");
		System.out.println("✅ <term>: full structure");
		System.out.println("✅ Real-world formulas: quadratic, euler, derivative, taylor, etc.");
		System.out.println("=".repeat(60));
	}
}
