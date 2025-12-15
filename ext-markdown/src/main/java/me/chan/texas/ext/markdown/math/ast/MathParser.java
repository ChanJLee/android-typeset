package me.chan.texas.ext.markdown.math.ast;

import me.chan.texas.utils.CharStream;

import java.util.*;

public class MathParser {
	private final CharStream stream;
	private int recursionDepth = 0;
	private static final int MAX_RECURSION_DEPTH = 100;
	// 在类开头添加 operator_symbol 集合
	// 这些符号既可以作为二元运算符，也可以在上标中单独使用（如 90^\circ 表示度数）
	private static final Set<String> OPERATOR_SYMBOLS = new HashSet<>(Arrays.asList(
			"pm", "mp",  // \pm 和 \mp
			"circ"       // \circ 可用于度数符号 (90^\circ) 或函数复合 (f \circ g)
	));
	// 二元运算符集合
	private static final Set<String> BINARY_OPERATORS = new HashSet<>(Arrays.asList(
			"+", "-", "*", "/", "=", "<", ">",  // 移除了逗号
			"times", "cdot", "div", "pm", "mp",
			"setminus", "circ",  // 集合差、函数复合
			"oplus", "ominus", "otimes", "oslash", "odot",  // 圈运算符
			"bullet", "star", "dagger", "ddagger",  // 其他二元运算
			"neq", "equiv", "approx", "cong", "sim",
			"simeq", "asymp", "propto",  // 更多关系运算符
			"le", "ge", "leq", "geq", "ll", "gg",
			"prec", "succ", "preceq", "succeq",  // 序关系
			"in", "notin", "subset", "supset", "subseteq", "supseteq",
			"cup", "cap", "wedge", "vee",
			"to", "rightarrow", "leftarrow", "leftrightarrow",
			"Rightarrow", "Leftarrow", "Leftrightarrow",
			"implies", "iff",
			"perp", "parallel",
			"mid", "nmid",  // 整除/不整除
			"triangleleft", "triangleright",  // 三角运算符
			"bowtie", "models"  // 关系运算符
	));

	// 真正的特殊符号（不能被一元运算符修饰）
	private static final Set<String> SPECIAL_SYMBOLS = new HashSet<>(Arrays.asList(
			"dots", "ldots", "cdots", "vdots", "ddots",  // 省略号
			"angle",  // 角度符号
			"therefore", "because",  // 逻辑标记符号
			"forall", "exists", "nexists",  // ∀ ∃ ∄ 量词
			"emptyset", "varnothing"  // ∅ 空集
	));

	// 类希腊字母的符号（可以被一元运算符修饰，视为变量）
	private static final Set<String> SPECIAL_VARIABLE_SYMBOLS = new HashSet<>(Arrays.asList(
			"hbar",           // ℏ 约化普朗克常数
			"nabla",          // ∇ 梯度算子
			"partial",        // ∂ 偏导数
			"ell",            // ℓ 脚本小写L
			"wp",             // ℘ 魏尔斯特拉斯函数
			"Re", "Im",       // ℜ ℑ 实部虚部
			"aleph"           // ℵ 阿列夫数
	));

	// 函数名集合
	private static final Set<String> FUNCTION_NAMES = new HashSet<>(Arrays.asList(
			"sin", "cos", "tan", "cot", "sec", "csc",
			"arcsin", "arccos", "arctan",
			"sinh", "cosh", "tanh", "coth",
			"log", "ln", "lg", "exp",
			"max", "min", "sup", "inf",
			"arg", "deg", "det", "dim", "gcd", "hom", "ker",
			"Pr", "bmod", "pmod"
	));

	// 大型运算符集合
	private static final Set<String> LARGE_OPERATORS = new HashSet<>(Arrays.asList(
			"sum", "prod", "coprod",
			"int", "iint", "iiint", "oint", "oiint", "oiiint",
			"bigcup", "bigcap", "bigvee", "bigwedge",
			"bigoplus", "bigotimes", "bigodot",
			"biguplus", "bigsqcup",
			"lim", "limsup", "liminf"
	));

	// 希腊字母集合
// 希腊字母集合
	private static final Set<String> GREEK_LETTERS = new HashSet<>(Arrays.asList(
			"alpha", "beta", "gamma", "delta", "epsilon", "varepsilon",
			"zeta", "eta", "theta", "vartheta", "iota", "kappa",
			"lambda", "mu", "nu", "xi", "pi", "varpi", "rho", "varrho",
			"sigma", "varsigma", "tau", "upsilon", "phi", "varphi",
			"chi", "psi", "omega",
			"Gamma", "Delta", "Theta", "Lambda", "Xi", "Pi",
			"Sigma", "Upsilon", "Phi", "Psi", "Omega", "infty"
	));

	// 重音符号集合
	private static final Set<String> ACCENT_COMMANDS = new HashSet<>(Arrays.asList(
			"hat", "widehat", "tilde", "widetilde",
			"bar", "overline", "underline",
			"vec", "overrightarrow", "overleftarrow",
			"dot", "ddot", "dddot",
			"acute", "grave", "breve", "check",
			"mathring", "overbrace", "underbrace"
	));

	// 定界符映射
	private static final Set<String> DELIMITERS = new HashSet<>(Arrays.asList(
			"(", ")", "[", "]", "{", "}", "|",
			"langle", "rangle", "lfloor", "rfloor", "lceil", "rceil",
			"lvert", "rvert", "lVert", "rVert"
	));

	// 定界符级别（大小）
	public static final String[][] DELIMITER_LEVELS = {
			{"left", "right"},
			{"bigl", "bigr"},
			{"Bigl", "Bigr"},
			{"biggl", "biggr"},
			{"Biggl", "Biggr"}
	};

	// 空格命令集合
	private static final Set<String> SPACING_COMMANDS = new HashSet<>(Arrays.asList(
			"quad", "qquad", ",", ":", ";", "!",
			"hspace", "hphantom", "vphantom", "phantom"
	));
	private static final Set<String> UNITS = new HashSet<>(Arrays.asList(
			"em", "ex", "pt", "px", "cm", "mm", "in"
	));
	private static final Set<String> FONT_COMMANDS = new HashSet<>(Arrays.asList(
			"mathrm", "mathit", "mathbf", "mathsf", "mathtt"
			, "mathcal", "mathbb", "mathfrak", "mathscr"
			, "boldsymbol", "bm"
	));
	private static final Set<String> TEXT_COMMANDS = new HashSet<>(Arrays.asList(
			"text", "mbox", "textrm", "textit", "textbf")
	);
	private static final Set<String> FRAC_COMMANDS = new HashSet<>(
			Arrays.asList(
					"frac", "dfrac", "tfrac", "cfrac"
			)
	);
	// 新增二项式系数命令集合
	private static final Set<String> BINOM_COMMANDS = new HashSet<>(
			Arrays.asList(
					"binom", "dbinom", "tbinom"
			)
	);
	private static final Set<String> MATRIX_COMMANDS = new HashSet<>(
			Arrays.asList("matrix", "pmatrix", "bmatrix", "Bmatrix", "vmatrix", "Vmatrix",
					"smallmatrix", "array", "cases")
	);
	private static final Set<String> EXTENSIBLE_ARROW_COMMANDS = new HashSet<>(Arrays.asList(
			"xrightarrow", "xleftarrow",
			"xRightarrow", "xLeftarrow",
			"xleftrightarrow", "xLeftrightarrow",
			// 添加 BNF 中定义的其他命令
			"xhookrightarrow", "xhookleftarrow",
			"xtwoheadrightarrow", "xtwoheadleftarrow",
			"xmapsto", "xtofrom"
	));

	public MathParser(CharStream stream) {
		this.stream = stream;
	}

	public MathList parse() throws MathParseException {
		skipWhitespace();
		MathList result = parseMathList();
		skipWhitespace();

		if (!stream.eof()) {
			throw new MathParseException("Unexpected characters after expression", stream);
		}

		return result;
	}

	/**
	 * <math_list> ::= <element> { <element> }
	 * <element> ::= <expression>,<spacing>
	 */
	private MathList parseMathList() throws MathParseException {
		checkRecursionDepth();
		List<Ast> ast = new ArrayList<>();
		skipWhitespace();

		while (!isMathListEnd()) {
			// 检查是否是 spacing 命令
			if (isSpacingCommand()) {
				ast.add(parseSpacing());
			} else {
				// 解析表达式（term序列，term之间用二元运算符连接）
				ast.add(parseExpression());
			}
			skipWhitespace();
		}

		return new MathList(ast);
	}

	/**
	 * <expression> ::= <term> { <separator> }
	 * <separator> ::= <binary_op> <term> | <term>
	 * <p>
	 * Expression 是 term 的序列，term 之间可以：
	 * 1. 通过二元运算符连接（如 a+b）
	 * 2. 直接相邻（隐式乘法，如 2x，或标点，如 a,b）
	 */
	private Expression parseExpression() throws MathParseException {
		List<Ast> elements = new ArrayList<>();

		// 第一个 term
		elements.add(parseTerm());
		skipWhitespace();

		// 后续的元素可以是：
		// 1. binary_op term 对
		// 2. 直接相邻的 term（标点、隐式乘法等）
		while (!stream.eof() && !isMathListEnd()) {
			if (isBinaryOperator()) {
				// 消费二元运算符
				elements.add(new BinOpAtom(consumeBinaryOperator()));
				skipWhitespace();

				// 运算符后面必须有 term
				if (stream.eof() || isMathListEnd()) {
					throw new MathParseException("Binary operator must be followed by a term", stream);
				}

				// 解析运算符后面的 term
				elements.add(parseTerm());
				skipWhitespace();
			} else if (isTermStart()) {
				// 直接相邻的 term（标点、隐式乘法）
				elements.add(parseTerm());
				skipWhitespace();
			} else {
				// 既不是二元运算符，也不是 term 开头，退出循环
				break;
			}
		}

		return new Expression(elements);
	}

	/**
	 * 判断当前位置是否是一个 term 的开始
	 */
	private boolean isTermStart() {
		if (stream.eof()) {
			return false;
		}

		char c = (char) stream.peek();

		// 数字
		if (Character.isDigit(c)) {
			return true;
		}

		// 字母（变量）
		if (Character.isLetter(c) && c != '\\') {
			return true;
		}

		// 分组 {
		if (c == '{') {
			return true;
		}

		// 标点符号 (逗号和分号)
		if (c == ',' || c == ';') {
			return true;
		}

// 命令（以 \ 开头）
		if (c == '\\') {
			int saved = stream.save();
			stream.eat();
			String cmd = scanCommandName();
			stream.restore(saved);

// 检查是否是有效的 atom 命令或特殊符号
			return GREEK_LETTERS.contains(cmd)
					|| FRAC_COMMANDS.contains(cmd)
					|| BINOM_COMMANDS.contains(cmd)  // 新增这行
					|| cmd.equals("sqrt")
					|| EXTENSIBLE_ARROW_COMMANDS.contains(cmd)
					|| getDelimitedLevel(cmd) >= 0
					|| FUNCTION_NAMES.contains(cmd)
					|| LARGE_OPERATORS.contains(cmd)
					|| cmd.equals("begin")
					|| TEXT_COMMANDS.contains(cmd)
					|| ACCENT_COMMANDS.contains(cmd)
					|| FONT_COMMANDS.contains(cmd)
					|| SPECIAL_SYMBOLS.contains(cmd)
					|| SPECIAL_VARIABLE_SYMBOLS.contains(cmd);
		}

		return false;
	}

	/**
	 * <extensible_arrow> ::= <extensible_arrow_cmd> [ "[" <math_list> "]" ] "{" <math_list> "}"
	 * 例如：\xrightarrow{上方} 或 \xrightarrow[下方]{上方}
	 */
	private ExtensibleArrowAtom parseExtensibleArrow(String cmd) throws MathParseException {
		skipWhitespace();

		MathList below = null;

		// 可选的下方内容 [below]
		if (!stream.eof() && stream.peek() == '[') {
			stream.eat();
			recursionDepth++;
			try {
				below = parseMathList();
				skipWhitespace();
				expect(']');
			} finally {
				recursionDepth--;
			}
			skipWhitespace();
		}

		// 必需的上方内容 {above}
		expect('{');
		recursionDepth++;
		try {
			MathList above = parseMathList();
			skipWhitespace();
			expect('}');
			return new ExtensibleArrowAtom(cmd, above, below);
		} finally {
			recursionDepth--;
		}
	}

	/**
	 * 判断当前是否是空格命令
	 */
	private boolean isSpacingCommand() {
		if (stream.peek() != '\\') return false;

		int saved = stream.save();
		stream.eat();
		String cmd = scanCommandName();
		stream.restore(saved);

		return SPACING_COMMANDS.contains(cmd);
	}

	/**
	 * 解析空格命令
	 * <spacing> ::= "\,","\:","\;","\!","\quad","\qquad","\hspace" "{" <length> "}",...
	 */
	private Spacing parseSpacing() throws MathParseException {
		expect('\\');
		String cmd = scanCommandName();

		if (!SPACING_COMMANDS.contains(cmd)) {
			throw new MathParseException("Expected spacing command, got \\" + cmd, stream);
		}

		// 对于 hspace 等需要参数的命令
		if (cmd.equals("hspace")) {
			skipWhitespace();
			expect('{');
			Length lengthAtom = parseLength();
			skipWhitespace();
			expect('}');
			return new Spacing(cmd, lengthAtom);
		}

		// 对于 phantom 类命令（需要 math_list 参数）
		if (cmd.equals("hphantom") || cmd.equals("vphantom") || cmd.equals("phantom")) {
			skipWhitespace();
			expect('{');
			recursionDepth++;
			try {
				MathList content = parseMathList();
				skipWhitespace();
				expect('}');
				return new Spacing(cmd, content);
			} finally {
				recursionDepth--;
			}
		}

		// 简单的空格命令
		return new Spacing(cmd, null);
	}

	private Length parseLength() throws MathParseException {
		skipWhitespace();
		NumberAtom size = parseNumber();
		skipWhitespace();
		SizeUnit unit = parseUnit();
		return new Length(size, unit);
	}

	/**
	 * 辅助方法：判断当前是否是 term 的结束位置
	 * 例如，当遇到右括号、\right 命令或文件末尾时
	 */
	private boolean isMathListEnd() {
		if (stream.eof()) {
			return true;
		}

		char c = (char) stream.peek();
		if (isGroupEnd(c)) { // 组结束, ] 根式可选参数结束
			return true;
		}
		if (c == '&') { // 矩阵列分隔符
			return true;
		}

		if (c == '\\') {
			int savedPos = stream.save();
			stream.eat(); // consume '\'
			String cmd = scanCommandName();
			stream.restore(savedPos);
			// \right、\end、\\ 是明确的结束标志
			return cmd.equals("right") || cmd.equals("bigr") || cmd.equals("Bigr")
					|| cmd.equals("biggr") || cmd.equals("Biggr")
					|| cmd.equals("end") || cmd.isEmpty() || cmd.equals("\\"); // \\ (换行)
		}
		return false;
	}

	/**
	 * <term> ::= [ <unary_op> ] <operand_atom> [ <sup_sub_suffix> ] [ <postfix_op> ]
	 * | <special_symbol> [ <sup_sub_suffix> ] | <punctuation>
	 */
	private Term parseTerm() throws MathParseException {
		skipWhitespace();

		// 检查是否是标点符号
		if (isPunctuation()) {
			PunctuationAtom punctuation = parsePunctuation();
			return new Term(null, punctuation, null);  // 标点不能有一元运算符和上下标
		}

		// 先检查是否是特殊符号
		if (isSpecialSymbol()) {
			SpecialSymbolAtom specialSymbol = parseSpecialSymbol();
			skipWhitespace();
			SupSubSuffix suffix = tryParseSupSubSuffix();
			return new Term(null, specialSymbol, suffix);
		}

		// 尝试解析可选的一元运算符
		String op = parseUnaryOp();
		UnaryOp unaryOp = null;
		if (op != null) {
			unaryOp = new UnaryOp(op);
		}

		// 解析可运算的原子
		Atom atom = parseOperandAtom();
		skipWhitespace();
		SupSubSuffix suffix = tryParseSupSubSuffix();

		// 新增：尝试解析后缀运算符
		skipWhitespace();
		PostfixOp postfixOp = tryParsePostfixOp();

		return new Term(unaryOp, atom, suffix, postfixOp);
	}

	/**
	 * 尝试解析后缀运算符
	 * <postfix_op> ::= "!" | "!!"
	 */
	private PostfixOp tryParsePostfixOp() {
		if (!stream.eof() && stream.peek() == '!') {
			StringBuilder stringBuilder = new StringBuilder();
			while (!stream.eof() && stream.peek() == '!') {
				stringBuilder.append('!');
				stream.eat();
			}
			return new PostfixOp(stringBuilder.toString());
		}
		return null;
	}

	// 添加标点符号检查和解析方法
	private boolean isPunctuation() {
		if (stream.eof()) return false;
		char c = (char) stream.peek();
		return c == ',' || c == ';';
	}

	private PunctuationAtom parsePunctuation() {
		char c = (char) stream.eat();
		return new PunctuationAtom(String.valueOf(c));
	}

	/**
	 * 判断当前是否是特殊符号
	 */
	private boolean isSpecialSymbol() {
		if (stream.peek() != '\\') return false;

		int saved = stream.save();
		stream.eat();
		String cmd = scanCommandName();
		stream.restore(saved);

		return SPECIAL_SYMBOLS.contains(cmd);
	}

	/**
	 * 解析特殊符号（不能被一元运算符修饰的符号）
	 */
	private SpecialSymbolAtom parseSpecialSymbol() throws MathParseException {
		expect('\\');
		String cmd = scanCommandName();

		if (!SPECIAL_SYMBOLS.contains(cmd)) {
			throw new MathParseException("Expected special symbol, got \\" + cmd, stream);
		}

		return new SpecialSymbolAtom(cmd);
	}

	/**
	 * 解析一元运算符
	 * <unary_op> ::= "+","-","\pm","\mp", "!", "\neg"
	 */
	private String parseUnaryOp() throws MathParseException {
		if (!stream.eof()) {
			char c = (char) stream.peek();
			if (c == '+' || c == '-' || c == '!') {
				// 保存当前位置
				int saved = stream.save();
				stream.eat();
				skipWhitespace();

				// 检查后面是否有operand_atom（不能是special_symbol）
				// 关键：一元运算符后必须紧跟operand_atom（可能有空格）
				if (!stream.eof() && isOperandAtomStart()) {
					// 这是一元运算符
					return String.valueOf(c);
				} else {
					// 不是一元运算符，恢复stream
					stream.restore(saved);
				}
			} else if (c == '\\') {
				int saved = stream.save();
				stream.eat();
				String cmd = scanCommandName();
				if ("pm".equals(cmd) || "mp".equals(cmd) || "neg".equals(cmd)) {
					skipWhitespace();
					if (!stream.eof() && isOperandAtomStart()) {
						return "\\" + cmd;
					}
				}
				stream.restore(saved);
			}
		}

		return null;
	}

	/**
	 * <operand_atom> ::= <number>,<variable>,<greek_letter_variable>,<group>
	 * ,<frac>,<sqrt>,<delimited>,<function_call>,<large_operator>
	 * ,<matrix>,<text>,<accent>,<font_command>
	 * <p>
	 * 可运算的原子表达式：可以被一元运算符修饰
	 * 注意：不包含 spacing 和 special_symbol
	 */
	private Atom parseOperandAtom() throws MathParseException {
		skipWhitespace();

		if (stream.eof()) {
			throw new MathParseException("Unexpected end of input", stream);
		}

		char c = (char) stream.peek();

		// 数字
		if (Character.isDigit(c)) {
			return parseNumber();
		}

		// 字母（变量）
		if (isVariableLetter(c)) {
			return parseVariable();
		}

		// 分组
		if (isGroupStart(c)) {
			return parseGroup(c);
		}

		// 命令（以\开头）
		if (c == '\\') {
			return parseOperandAtom0();
		}

		throw new MathParseException("Expected operand atom, got '" + c + "'", stream);
	}

	/**
	 * 解析命令形式的可运算原子（\开头的）
	 */
	private Atom parseOperandAtom0() throws MathParseException {
		int startPos = stream.save();
		expect('\\');

		String cmd = scanCommandName();

		if (cmd.isEmpty()) {
			stream.restore(startPos);
			throw new MathParseException("Empty command", stream);
		}

		// 在希腊字母处理之后添加：
		if (GREEK_LETTERS.contains(cmd)) {
			return parseGreekLetterAtom(cmd);
		}

		// 新增：特殊变量符号（类似希腊字母的处理）
		if (SPECIAL_VARIABLE_SYMBOLS.contains(cmd)) {
			return parseSpecialVariableAtom(cmd);
		}

		// 分式
		if (FRAC_COMMANDS.contains(cmd)) {
			return parseFrac(cmd);
		}

		// 二项式系数 - 新增
		if (BINOM_COMMANDS.contains(cmd)) {
			return parseBinom(cmd);
		}

		// 根式
		if (cmd.equals("sqrt")) {
			return parseSqrt();
		}

		// 可扩展箭头 - 新增
		if (EXTENSIBLE_ARROW_COMMANDS.contains(cmd)) {
			return parseExtensibleArrow(cmd);
		}

		// 定界符
		int level = getDelimitedLevel(cmd);
		if (level >= 0) {
			stream.restore(startPos);
			return parseDelimited(level);
		}

		// 函数
		if (FUNCTION_NAMES.contains(cmd)) {
			return parseFunctionCall(cmd);
		}

		// 大型运算符
		if (LARGE_OPERATORS.contains(cmd)) {
			return parseLargeOperator(cmd);
		}

		if ("begin".equals(cmd)) {
			return parseMatrix();
		}

		// 文本命令
		if (TEXT_COMMANDS.contains(cmd)) {
			return parseText(cmd);
		}

		// 重音符号
		if (ACCENT_COMMANDS.contains(cmd)) {
			return parseAccent(cmd);
		}

		if (FONT_COMMANDS.contains(cmd)) {
			return parseFont(cmd);
		}

		stream.restore(startPos);
		throw new MathParseException("Unknown command: \\" + cmd, stream);
	}

	/**
	 * <binom> ::= "\binom" "{" <math_list> "}" "{" <math_list> "}"
	 */
	private BinomAtom parseBinom(String cmd) throws MathParseException {
		skipWhitespace();
		expect('{');

		recursionDepth++;
		try {
			MathList upper = parseMathList();
			skipWhitespace();
			expect('}');

			skipWhitespace();
			expect('{');
			MathList lower = parseMathList();
			skipWhitespace();
			expect('}');

			return new BinomAtom(cmd, upper, lower);
		} finally {
			recursionDepth--;
		}
	}

	private SpecialLetterVariableAtom parseSpecialVariableAtom(String cmd) {
		StringBuilder builder = new StringBuilder();
		// 解析可选的 prime 后缀
		while (!stream.eof() && stream.peek() == '\'') {
			builder.append((char) stream.eat());
		}
		return new SpecialLetterVariableAtom(cmd, builder.toString());
	}

	private Atom parseMatrix() throws MathParseException {
		skipWhitespace();
		expect('{');
		skipWhitespace();
		int position = stream.save();
		String lhsEnv = parseMatrixEnv();
		if (!MATRIX_COMMANDS.contains(lhsEnv)) {
			stream.restore(position);
			throw new MathParseException("Unknown matrix environment: \\" + lhsEnv, stream);
		}

		skipWhitespace();
		expect('}');

		String gravity = parseMatrixGravity();

		List<MatrixRow> rows = parseMatrixRows();
		skipWhitespace();
		expect('{');
		skipWhitespace();
		position = stream.save();
		String rhsEnv = parseMatrixEnv();
		if (!lhsEnv.equals(rhsEnv)) {
			stream.restore(position);
			throw new MathParseException("Matrix environment mismatch: [\"" + lhsEnv + "\",\"" + rhsEnv + "\"]", stream);
		}
		skipWhitespace();
		expect('}');

		return new MatrixAtom(lhsEnv, gravity, rows);
	}

	private List<MatrixRow> parseMatrixRows() throws MathParseException {
		List<MatrixRow> rows = new ArrayList<>();
		while (!stream.eof()) {
			rows.add(parseMatrixRow());
			skipWhitespace();
			expect('\\');
			if (!stream.eof() && Character.isWhitespace(stream.peek())) {
				skipWhitespace();
				continue;
			}

			int position = stream.save();
			String cmd = scanCommandName();
			if ("\\".equals(cmd)) {
				continue;
			}

			if (cmd.equals("end")) {
				break;
			} else {
				stream.restore(position);
				throw new MathParseException("Expected \\end, got \\" + cmd, stream);
			}
		}

		return rows;
	}

	private String parseMatrixEnv() {
		return scanCommandName();
	}

	private String parseMatrixGravity() throws MathParseException {
		int save = stream.save();
		skipWhitespace();
		if (stream.eof() || stream.peek() != '{') {
			stream.restore(save);
			return null;
		}

		stream.eat(); // 消费 '{'
		skipWhitespace();
		if (stream.eof()) {
			stream.restore(save);
			return null;
		}

		// 读取所有的对齐字符 (c, l, r)
		StringBuilder gravity = new StringBuilder();
		while (!stream.eof()) {
			char c = (char) stream.peek();
			if ("clr".indexOf(c) >= 0) {
				gravity.append((char) stream.eat());
			} else {
				break;
			}
		}

		if (gravity.length() == 0) {
			stream.restore(save);
			return null;
		}

		skipWhitespace();
		expect('}');
		skipWhitespace();
		return gravity.toString();
	}

	private MatrixRow parseMatrixRow() throws MathParseException {
		List<MathList> elements = new ArrayList<>();
		while (!stream.eof()) {
			elements.add(parseMathList());
			skipWhitespace();

			if (!stream.eof() && stream.peek() == '&') {
				stream.eat();
				continue;
			}

			break;
		}
		return new MatrixRow(elements);
	}

	private Atom parseFont(String cmd) throws MathParseException {
		skipWhitespace();
		expect('{');
		MathList ast = parseMathList();
		skipWhitespace();
		expect('}');
		return new FontAtom(cmd, ast);
	}

	/**
	 * <number> ::= <digit> { <digit> } [ "." <digit> { <digit> } ]
	 */
	private NumberAtom parseNumber() {
		StringBuilder sb = new StringBuilder();

		// 整数部分
		while (!stream.eof() && Character.isDigit((char) stream.peek())) {
			sb.append((char) stream.eat());
		}

		// 小数部分
		if (!stream.eof() && stream.peek() == '.') {
			sb.append((char) stream.eat());
			while (!stream.eof() && Character.isDigit((char) stream.peek())) {
				sb.append((char) stream.eat());
			}
		}

		return new NumberAtom(sb.toString());
	}

	private SizeUnit parseUnit() throws MathParseException {
		// 整数部分
		StringBuilder sb = new StringBuilder();
		while (!stream.eof() && Character.isLetter(((char) stream.peek()))) {
			sb.append((char) stream.eat());
		}
		String unit = sb.toString();
		if (!UNITS.contains(unit)) {
			throw new MathParseException("Expected unit, got \\" + unit, stream);
		}
		return new SizeUnit(unit);
	}

	private VariableAtom parseSingleVariable() {
		StringBuilder builder = new StringBuilder()
				.append((char) stream.eat());

		// 解析可选的 prime 后缀
		while (!stream.eof() && stream.peek() == '\'') {
			builder.append((char) stream.eat());
		}

		return new VariableAtom(builder.toString());
	}

	/**
	 * <variable> ::= <letter> { <letter> } [ <prime_suffix> ]
	 * <prime_suffix> ::= "'" { "'" }
	 */
	private VariableAtom parseVariable() {
		StringBuilder builder = new StringBuilder()
				.append((char) stream.eat());

		// 解析后续字母
		while (!stream.eof()) {
			char c = (char) stream.peek();
			if (isVariableLetter(c)) {
				builder.append((char) stream.eat());
			} else {
				break;
			}
		}

		// 解析可选的 prime 后缀
		while (!stream.eof() && stream.peek() == '\'') {
			builder.append((char) stream.eat());
		}

		return new VariableAtom(builder.toString());
	}

	private boolean isVariableLetter(char c) {
		return Character.isLetter(c) && c != '\\';
	}

	/**
	 * <group> ::= "{" <math_list> "}" | "(" <math_list> ")" | "[" <math_list> "]" | | "\{" <math_list> "\}"
	 *
	 */
	private Group parseGroup(char s) throws MathParseException {
		expect(s);
		char e;
		if (s == '{') {
			e = '}';
		} else if (s == '(') {
			e = ')';
		} else if (s == '[') {
			e = ']';
		} else if (s == '\\') {
			expect('{');
			e = '\\';
		} else {
			throw new IllegalStateException("Unexpected group start: " + s);
		}

		recursionDepth++;
		try {
			MathList content = parseMathList();
			skipWhitespace();
			expect(e);
			if (s == '\\') {
				expect('}');
			}
			return new Group(s, e, content);
		} finally {
			recursionDepth--;
		}
	}

	/**
	 * <frac> ::= "\frac" "{" <math_list> "}" "{" <math_list> "}"
	 */
	private FracAtom parseFrac(String cmd) throws MathParseException {
		skipWhitespace();
		expect('{');

		recursionDepth++;
		try {
			MathList numerator = parseMathList();
			skipWhitespace();
			expect('}');

			skipWhitespace();
			expect('{');
			MathList denominator = parseMathList();
			skipWhitespace();
			expect('}');

			return new FracAtom(cmd, numerator, denominator);
		} finally {
			recursionDepth--;
		}
	}

	/**
	 * <sqrt> ::= "\sqrt" [ "[" <math_list> "]" ] "{" <math_list> "}"
	 */
	private SqrtAtom parseSqrt() throws MathParseException {
		skipWhitespace();

		MathList root = null;

		// 可选的根次数 [n]
		if (!stream.eof() && stream.peek() == '[') {
			stream.eat();
			recursionDepth++;
			try {
				root = parseMathList();
				skipWhitespace();
				expect(']');
			} finally {
				recursionDepth--;
			}
			skipWhitespace();
		}

		// 被开方数 {x}
		expect('{');
		recursionDepth++;
		try {
			MathList content = parseMathList();
			skipWhitespace();
			expect('}');
			return new SqrtAtom(content, root);
		} finally {
			recursionDepth--;
		}
	}

	/**
	 * <delimited> ::= "\left" <delimiter> <math_list> "\right" <delimiter>
	 * 或其他大小级别的定界符
	 */
	private DelimitedAtom parseDelimited(int level) throws MathParseException {
		skipWhitespace();
		expectCommand(DELIMITER_LEVELS[level][0]);
		skipWhitespace();

		// 解析左定界符
		String leftDelim = parseDelimiter();

		recursionDepth++;
		try {
			MathList content = parseMathList();

			skipWhitespace();
			expectCommand(DELIMITER_LEVELS[level][1]);
			skipWhitespace();

			String rightDelim = parseDelimiter();

			return new DelimitedAtom(level, leftDelim, content, rightDelim);
		} finally {
			recursionDepth--;
		}
	}

	/**
	 * 解析定界符
	 * <delimiter> ::= "(",")","[","]","\{","\}","|",".","\langle",...
	 */
	private String parseDelimiter() throws MathParseException {
		skipWhitespace();

		if (stream.eof()) {
			throw new MathParseException("Expected delimiter", stream);
		}

		char c = (char) stream.peek();

		// 单字符定界符
		if (c == '(' || c == ')' || c == '[' || c == ']' || c == '|' || c == '.') {
			stream.eat();
			return String.valueOf(c);
		}

		// 命令形式的定界符（如 \{ \langle）
		if (c == '\\') {
			stream.eat();
			String cmd = scanCommandName();

			if (cmd.equals("{") || cmd.equals("}")) {
				return cmd;
			}

			// \| 表示双竖线 ||
			if (cmd.equals("|")) {
				return "||";
			}

			if (DELIMITERS.contains(cmd)) {
				return "\\" + cmd;
			}

			throw new MathParseException("Invalid delimiter: \\" + cmd, stream);
		}

		throw new MathParseException("Expected delimiter, got '" + c + "'", stream);
	}

	/**
	 * <function_call> ::= "\" <function_name> [ "_" <script_arg> ] [ "^" <script_arg> ] [ <function_arg> ]
	 */
	private FunctionCallAtom parseFunctionCall(String functionName) throws MathParseException {
		skipWhitespace();

		SupSubSuffix supSubSuffix = tryParseSupSubSuffix();

		// 函数参数（可选，只吃一个token或group）
		Ast argument = null;
		if (!stream.eof()) {
			char c = (char) stream.peek();
			int level = -1;
			if (isGroupStart(c)) {
				argument = parseGroup(c);
			} else if (c == '\\' && (level = peekDelimitedLevel()) >= 0) {
				argument = parseDelimited(level);
			} else if (isSingleTokenStart()) {
				argument = new SingleToken(scanSingleToken());
			}
		}

		return new FunctionCallAtom(functionName, supSubSuffix, argument);
	}

	private boolean isGroupStart(char c) {
		if (c == '{' || c == '(' || c == '[') {
			return true;
		}

		if (c == '\\') {
			int save = stream.save();
			stream.eat();
			boolean ret = stream.peek() == '{';
			stream.restore(save);
			return ret;
		}
		return false;
	}

	private boolean isGroupEnd(char c) {
		if (c == '}' || c == ')' || c == ']') {
			return true;
		}

		if (c == '\\') {
			int save = stream.save();
			stream.eat();
			boolean ret = stream.peek() == '}';
			stream.restore(save);
			return ret;
		}
		return false;
	}

	/**
	 * 预览下一个定界符级别
	 */
	private int peekDelimitedLevel() {
		if (stream.eof() || stream.peek() != '\\') {
			return -1;
		}

		int saved = stream.save();
		stream.eat();  // 跳过 \
		String cmd = scanCommandName();
		stream.restore(saved);

		return getDelimitedLevel(cmd);
	}

	/**
	 * 获取定界符命令的级别
	 */
	private int getDelimitedLevel(String cmd) {
		for (int i = 0; i < DELIMITER_LEVELS.length; ++i) {
			if (cmd.equals(DELIMITER_LEVELS[i][0])) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * <large_operator> ::= "\" <large_op_name> [ "_" <script_arg> ] [ "^" <script_arg> ]
	 */
	private LargeOperatorAtom parseLargeOperator(String operatorName) throws MathParseException {
		skipWhitespace();

		return new LargeOperatorAtom(operatorName, tryParseSupSubSuffix());
	}

	/**
	 * <text> ::= "\text" "{" <text_content> "}"
	 */
	private TextAtom parseText(String command) throws MathParseException {
		skipWhitespace();
		expect('{');

		StringBuilder sb = new StringBuilder();
		while (!stream.eof() && stream.peek() != '}') {
			sb.append((char) stream.eat());
		}

		skipWhitespace();
		expect('}');
		return new TextAtom(command, sb.toString());
	}

	/**
	 * <accent> ::= <accent_cmd> "{" <math_list> "}"
	 * | <accent_cmd> <single_token>
	 */
	private AccentAtom parseAccent(String accentCmd) throws MathParseException {
		skipWhitespace();

		// 检查是否是花括号形式
		if (!stream.eof() && stream.peek() == '{') {
			expect('{');

			recursionDepth++;
			try {
				MathList content = parseMathList();
				skipWhitespace();
				expect('}');
				return new AccentAtom(accentCmd, content);
			} finally {
				recursionDepth--;
			}
		}

		// 否则尝试解析 single_token 形式
		if (isSingleTokenStart()) {
			Atom content = scanSingleToken();
			return new AccentAtom(accentCmd, content);
		}

		throw new MathParseException("Expected '{' or single token after \\" + accentCmd, stream);
	}


	/**
	 * <sup_sub_suffix> ::= "^" <script_arg>,"_" <script_arg>
	 * ,"^" <script_arg> "_" <script_arg>
	 * ,"_" <script_arg> "^" <script_arg>
	 */
	private SupSubSuffix tryParseSupSubSuffix() throws MathParseException {
		skipWhitespace();

		if (stream.eof()) {
			return null;
		}

		ScriptArg superscript = null;
		ScriptArg subscript = null;

		boolean reverse = false;
		if (stream.peek() == '^') {
			stream.eat();
			skipWhitespace();
			superscript = parseScriptArg();
			skipWhitespace();

			if (!stream.eof() && stream.peek() == '_') {
				stream.eat();
				skipWhitespace();
				subscript = parseScriptArg();
				skipWhitespace();
			}
		} else if (stream.peek() == '_') {
			reverse = true;
			stream.eat();
			skipWhitespace();
			subscript = parseScriptArg();
			skipWhitespace();

			if (!stream.eof() && stream.peek() == '^') {
				stream.eat();
				skipWhitespace();
				superscript = parseScriptArg();
				skipWhitespace();
			}
		} else {
			return null;  // 没有上下标
		}

		return new SupSubSuffix(superscript, subscript, reverse);
	}

	/**
	 * <script_arg> ::= <single_token>,<group>
	 */
	private ScriptArg parseScriptArg() throws MathParseException {
		skipWhitespace();

		if (stream.eof()) {
			throw new MathParseException("Expected script argument", stream);
		}

		char c = (char) stream.peek();
		if (isGroupStart(c)) {
			return new ScriptArg(parseGroup(c));
		} else if (isSingleTokenStart()) {
			return new ScriptArg(new SingleToken(scanSingleToken()));
		} else {
			throw new MathParseException("Expected script argument", stream);
		}
	}

	// ========== 辅助方法 ==========

	/**
	 * 判断当前位置是否是一个有效的 operand_atom 开头
	 * (不包括 special_symbol)
	 */
	private boolean isOperandAtomStart() {
		if (stream.eof()) {
			return false;
		}

		char c = (char) stream.peek();

		// 数字
		if (Character.isDigit(c)) {
			return true;
		}

		// 字母（变量）
		if (Character.isLetter(c) && c != '\\') {
			return true;
		}

		// 分组 {
		if (isGroupStart(c)) {
			return true;
		}

		// 命令（以\开头），但排除 special_symbol
		if (c == '\\') {
			int saved = stream.save();
			stream.eat();
			String cmd = scanCommandName();
			stream.restore(saved);

			// 排除特殊符号
			if (SPECIAL_SYMBOLS.contains(cmd)) {
				return false;
			}
			return true;
		}

		return false;
	}

	private void checkRecursionDepth() throws MathParseException {
		if (recursionDepth > MAX_RECURSION_DEPTH) {
			throw new MathParseException("Maximum recursion depth exceeded", stream);
		}
	}

	private void skipWhitespace() {
		while (!stream.eof() && Character.isWhitespace((char) stream.peek())) {
			stream.eat();
		}
	}

	private boolean isBinaryOperator() {
		if (stream.eof()) {
			return false;
		}

		char c = (char) stream.peek();

		// 单字符运算符（移除逗号）
		if ("+-*/=<>|".indexOf(c) >= 0) {
			return true;
		}

		// 命令形式的运算符（如 \times, \cdot, \perp）
		if (c == '\\') {
			int saved = stream.save();
			stream.eat();
			String cmd = scanCommandName();
			stream.restore(saved);

			// 排除空格命令（\, \: \; \! 等），它们不是二元运算符
			if (SPACING_COMMANDS.contains(cmd)) {
				return false;
			}

			return BINARY_OPERATORS.contains(cmd);
		}

		return false;
	}

	private String consumeBinaryOperator() throws MathParseException {
		char c = (char) stream.peek();

		// 单字符运算符
		if ("+-*/=<>|".indexOf(c) >= 0) {
			stream.eat();
			return String.valueOf(c);
		}

		// 命令形式的运算符
		if (c == '\\') {
			stream.eat();
			String cmd = scanCommandName();
			if (BINARY_OPERATORS.contains(cmd)) {
				return "\\" + cmd;
			}
			throw new MathParseException("Expected binary operator, got \\" + cmd, stream);
		}

		throw new MathParseException("Expected binary operator", stream);
	}

	private boolean isSingleTokenStart() {
		if (stream.eof()) {
			return false;
		}

		char c = (char) stream.peek();
		return Character.isDigit(c) || Character.isLetter(c) || c == '\\' || "+-*/=<>".indexOf(c) >= 0;
	}

	/**
	 * 扫描单个token（数字、变量、希腊字母命令或运算符符号）
	 * 用于上下标等场景
	 * <single_token> ::= <number> | <variable> | <greek_letter_variable> | <operator_symbol>
	 */
	private Atom scanSingleToken() throws MathParseException {
		if (stream.eof()) {
			throw new MathParseException("Expected single token", stream);
		}

		char c = (char) stream.peek();

		// 数字（可以是多位数字或小数）
		if (Character.isDigit(c)) {
			return parseNumber();
		}

		// 变量（可以是多字母变量）
		if (Character.isLetter(c) && c != '\\') {
			return parseSingleVariable();
		}

		// 单字符运算符符号
		if ("+-*/=<>".indexOf(c) >= 0) {
			return new OperatorSymbolAtom(String.valueOf((char) stream.eat()));
		}

		// 命令（如 \alpha, \pm）
		if (c == '\\') {
			stream.eat();
			String cmd = scanCommandName();

			// 希腊字母
			if (GREEK_LETTERS.contains(cmd)) {
				return parseGreekLetterAtom(cmd);
			}

			// 运算符符号
			if (OPERATOR_SYMBOLS.contains(cmd)) {
				return new OperatorSymbolAtom(cmd);
			}

			throw new MathParseException("Expected single token, got \\" + cmd, stream);
		}

		throw new MathParseException("Expected single token", stream);
	}

	private GreekLetterVariableAtom parseGreekLetterAtom(String cmd) {
		StringBuilder builder = new StringBuilder();
		// 解析可选的 prime 后缀
		while (!stream.eof() && stream.peek() == '\'') {
			builder.append((char) stream.eat());
		}

		return new GreekLetterVariableAtom(cmd, builder.toString());
	}

	/**
	 * 扫描命令名（\后面的字母序列）
	 * 注意：不包含前导的\
	 */
	private String scanCommandName() {
		StringBuilder sb = new StringBuilder();

		// 特殊情况：单字符命令（如 \{ \} \, \;）
		if (!stream.eof()) {
			char first = (char) stream.peek();
			if (!Character.isLetter(first)) {
				sb.append((char) stream.eat());
				return sb.toString();
			}
		}

		// 字母命令（如 \alpha, \frac）
		while (!stream.eof() && Character.isLetter((char) stream.peek())) {
			sb.append((char) stream.eat());
		}

		return sb.toString();
	}

	private void expectCommand(String expectedCmd) throws MathParseException {
		int startPos = stream.save();
		expect('\\');
		String cmd = scanCommandName();

		if (!cmd.equals(expectedCmd)) {
			stream.restore(startPos);
			throw new MathParseException("Expected \\" + expectedCmd + ", got \\" + cmd, stream);
		}
	}

	private void expect(char expected) throws MathParseException {
		if (stream.eof()) {
			throw new MathParseException("Expected '" + expected + "', got end of input", stream);
		}

		char actual = (char) stream.peek();
		if (actual != expected) {
			throw new MathParseException("Expected '" + expected + "', got '" + actual + "'", stream);
		}

		stream.eat();
	}
}