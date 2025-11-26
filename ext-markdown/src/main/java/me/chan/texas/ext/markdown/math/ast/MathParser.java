package me.chan.texas.ext.markdown.math.ast;

import me.chan.texas.utils.CharStream;

import java.util.*;

public class MathParser {
	private final CharStream stream;
	private int recursionDepth = 0;
	private static final int MAX_RECURSION_DEPTH = 100;

	// 二元运算符集合
	private static final Set<String> BINARY_OPERATORS = new HashSet<>(Arrays.asList(
			"+", "-", "*", "/", "=", "<", ">",
			"times", "cdot", "div", "pm", "mp",
			"neq", "equiv", "approx", "cong", "sim",
			"le", "ge", "leq", "geq", "ll", "gg",
			"in", "notin", "subset", "supset", "subseteq", "supseteq",
			"cup", "cap", "wedge", "vee",
			"to", "rightarrow", "leftarrow", "leftrightarrow",
			"Rightarrow", "Leftarrow", "Leftrightarrow",
			"implies", "iff",
			"perp", "parallel"           // 几何关系符号
	));

	// 特殊符号集合（不能被一元运算符修饰，但可带上下标）
	private static final Set<String> SPECIAL_SYMBOLS = new HashSet<>(Arrays.asList(
			"dots", "ldots", "cdots", "vdots", "ddots",  // 省略号
			"angle",  // 角度符号
			"therefore", "because"  // 逻辑标记符号
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
	private static final Set<String> MATRIX_COMMANDS = new HashSet<>(
			Arrays.asList("matrix", "pmatrix", "bmatrix", "Bmatrix", "vmatrix", "Vmatrix",
					"smallmatrix", "array", "cases")
	);

	public MathParser(CharStream stream) {
		this.stream = stream;
	}

	public MathList parse() throws MathParseException {
		skipWhitespace();
		MathList result = parseMathList();
		skipWhitespace();

		if (!stream.eof()) {
			throw new MathParseException("Unexpected characters after expression", stream.save());
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
	 * <expression> ::= <term> { <binary_op> <term> }
	 * 确保每个二元运算符后面必须跟一个term
	 */
	private Expression parseExpression() throws MathParseException {
		List<Ast> elements = new ArrayList<>();

		// 第一个term
		elements.add(parseTerm());
		skipWhitespace();

		// 后续的 binary_op term 对
		while (!stream.eof() && isBinaryOperator()) {
			// 消费二元运算符
			elements.add(new BinOpAtom(consumeBinaryOperator()));
			skipWhitespace();

			// 运算符后面必须有term
			if (stream.eof()) {
				throw new MathParseException("Binary operator must be followed by a term", stream.save());
			}

			// 解析运算符后面的term
			elements.add(parseTerm());
			skipWhitespace();
		}

		return new Expression(elements);
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
			throw new MathParseException("Expected spacing command, got \\" + cmd, stream.save());
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
		if (c == '}') { // 组结束
			return true;
		}
		if (c == ']') { // 根式可选参数结束
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
					|| cmd.equals("end") || cmd.isEmpty(); // \\ (换行)
		}
		return false;
	}

	/**
	 * <term> ::= [ <unary_op> ] <operand_atom> [ <sup_sub_suffix> ]
	 * | <special_symbol> [ <sup_sub_suffix> ]
	 * <p>
	 * term 分为两类：
	 * 1. 可运算的原子（可被一元运算符修饰）
	 * 2. 特殊符号（不能被一元运算符修饰，如省略号、角度符号等）
	 */
	private Term parseTerm() throws MathParseException {
		skipWhitespace();

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
		return new Term(unaryOp, atom, suffix);
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
			throw new MathParseException("Expected special symbol, got \\" + cmd, stream.save());
		}

		return new SpecialSymbolAtom(cmd);
	}

	/**
	 * 解析一元运算符
	 * <unary_op> ::= "+","-","\pm","\mp"
	 */
	private String parseUnaryOp() throws MathParseException {
		if (!stream.eof()) {
			char c = (char) stream.peek();
			if (c == '+' || c == '-') {
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
				if ("pm".equals(cmd) || "mp".equals(cmd)) {
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
	 * <operand_atom> ::= <number>,<variable>,<greek_letter>,<group>
	 * ,<frac>,<sqrt>,<delimited>,<function_call>,<large_operator>
	 * ,<matrix>,<text>,<accent>,<font_command>
	 * <p>
	 * 可运算的原子表达式：可以被一元运算符修饰
	 * 注意：不包含 spacing 和 special_symbol
	 */
	private Atom parseOperandAtom() throws MathParseException {
		skipWhitespace();

		if (stream.eof()) {
			throw new MathParseException("Unexpected end of input", stream.save());
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
		if (c == '{') {
			return parseGroup();
		}

		// 命令（以\开头）
		if (c == '\\') {
			return parseOperandAtom0();
		}

		throw new MathParseException("Expected operand atom, got '" + c + "'", stream.save());
	}

	/**
	 * 解析命令形式的可运算原子（\开头的）
	 */
	private Atom parseOperandAtom0() throws MathParseException {
		int startPos = stream.save();
		expect('\\');

		String cmd = scanCommandName();

		if (cmd.isEmpty()) {
			throw new MathParseException("Empty command", startPos);
		}

		// 希腊字母
		if (GREEK_LETTERS.contains(cmd)) {
			return new GreekLetterAtom(cmd);
		}

		// 分式
		if (FRAC_COMMANDS.contains(cmd)) {
			return parseFrac(cmd);
		}

		// 根式
		if (cmd.equals("sqrt")) {
			return parseSqrt();
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

		throw new MathParseException("Unknown command: \\" + cmd, startPos);
	}

	private Atom parseMatrix() throws MathParseException {
		skipWhitespace();
		expect('{');
		skipWhitespace();
		int position = stream.save();
		String lhsEnv = parseMatrixEnv();
		if (!MATRIX_COMMANDS.contains(lhsEnv)) {
			throw new MathParseException("Unknown matrix environment: \\" + lhsEnv, position);
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
			throw new MathParseException("Matrix environment mismatch: [\"" + lhsEnv + "\",\"" + rhsEnv + "\"]", position);
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
			if (cmd.equals("end")) {
				break;
			} else {
				throw new MathParseException("Expected \\end, got \\" + cmd, position);
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
		if (stream.peek() != '{') {
			stream.restore(save);
			return null;
		}

		skipWhitespace();
		if (stream.eof()) {
			return null;
		}

		char gravity = (char) stream.eat();
		if ("clr".indexOf(gravity) == -1) {
			stream.restore(save);
			return null;
		}

		skipWhitespace();
		expect('}');
		skipWhitespace();
		return String.valueOf(gravity);
	}

	private MatrixRow parseMatrixRow() throws MathParseException {
		List<Ast> elements = new ArrayList<>();
		while (!stream.eof()) {
			elements.add(parseMathList());
			skipWhitespace();

			if (!stream.eof() && stream.peek() == '&') {
				continue;
			}

			break;
		}
		return new MatrixRow(elements);
	}

	private Atom parseFont(String cmd) throws MathParseException {
		skipWhitespace();
		expect('{');
		Ast ast = parseMathList();
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
			throw new MathParseException("Expected unit, got \\" + unit, stream.save());
		}
		return new SizeUnit(unit);
	}

	/**
	 * <variable> ::= <letter> { <letter> }
	 */
	private VariableAtom parseVariable() {
		StringBuilder builder = new StringBuilder()
				.append((char) stream.eat());
		while (!stream.eof()) {
			char c = (char) stream.peek();
			if (isVariableLetter(c)) {
				builder.append((char) stream.eat());
			} else {
				break;
			}
		}
		return new VariableAtom(builder.toString());
	}

	private boolean isVariableLetter(char c) {
		return Character.isLetter(c) && c != '\\';
	}

	/**
	 * <group> ::= "{" <math_list> "}"
	 */
	private GroupAtom parseGroup() throws MathParseException {
		expect('{');

		recursionDepth++;
		try {
			MathList content = parseMathList();
			skipWhitespace();
			expect('}');
			return new GroupAtom(content);
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
			throw new MathParseException("Expected delimiter", stream.save());
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

			if (cmd.equals("{") || cmd.equals("}") || cmd.equals("|")) {
				return cmd;
			}

			if (DELIMITERS.contains(cmd)) {
				return "\\" + cmd;
			}

			throw new MathParseException("Invalid delimiter: \\" + cmd, stream.save());
		}

		throw new MathParseException("Expected delimiter, got '" + c + "'", stream.save());
	}

	/**
	 * <function_call> ::= "\" <function_name> [ "_" <script_arg> ] [ "^" <script_arg> ] [ <function_arg> ]
	 */
	private FunctionCallAtom parseFunctionCall(String functionName) throws MathParseException {
		skipWhitespace();

		ScriptArg subscript = null;
		ScriptArg superscript = null;

		// 下标（如 \log_2）
		if (!stream.eof() && stream.peek() == '_') {
			stream.eat();
			skipWhitespace();
			subscript = parseScriptArg();
			skipWhitespace();
		}

		// 上标
		if (!stream.eof() && stream.peek() == '^') {
			stream.eat();
			skipWhitespace();
			superscript = parseScriptArg();
			skipWhitespace();
		}

		// 函数参数（可选，只吃一个token或group）
		Ast argument = null;
		if (!stream.eof()) {
			char c = (char) stream.peek();
			int level = -1;
			if (c == '{') {
				argument = parseGroup();
			} else if (c == '\\' && (level = peekDelimitedLevel()) >= 0) {
				argument = parseDelimited(level);
			} else if (isSingleTokenStart()) {
				argument = new SingleTokenScriptArg(scanSingleToken());
			}
		}

		return new FunctionCallAtom(functionName, subscript, superscript, argument);
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

		ScriptArg subscript = null;
		ScriptArg superscript = null;

		// 可以是 _{}^{} 或 ^{}_{}
		if (!stream.eof() && stream.peek() == '_') {
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
		} else if (!stream.eof() && stream.peek() == '^') {
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
		}

		return new LargeOperatorAtom(operatorName, subscript, superscript);
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
	 */
	private AccentAtom parseAccent(String accentCmd) throws MathParseException {
		skipWhitespace();
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

		return new SupSubSuffix(superscript, subscript);
	}

	/**
	 * <script_arg> ::= <single_token>,<group>
	 */
	private ScriptArg parseScriptArg() throws MathParseException {
		skipWhitespace();

		if (stream.eof()) {
			throw new MathParseException("Expected script argument", stream.save());
		}

		if (stream.peek() == '{') {
			GroupAtom group = parseGroup();
			return new GroupScriptArg(group);
		} else if (isSingleTokenStart()) {
			String token = scanSingleToken();
			return new SingleTokenScriptArg(token);
		} else {
			throw new MathParseException("Expected script argument", stream.save());
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
		if (c == '{') {
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
			throw new MathParseException("Maximum recursion depth exceeded", stream.save());
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

		// 单字符运算符
		if ("+-*/=<>".indexOf(c) >= 0) {
			return true;
		}

		// 命令形式的运算符（如 \times, \cdot, \perp, \therefore）
		if (c == '\\') {
			int saved = stream.save();
			stream.eat();
			String cmd = scanCommandName();
			stream.restore(saved);
			return BINARY_OPERATORS.contains(cmd);
		}

		return false;
	}

	private String consumeBinaryOperator() throws MathParseException {
		char c = (char) stream.peek();

		// 单字符运算符
		if ("+-*/=<>".indexOf(c) >= 0) {
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
			throw new MathParseException("Expected binary operator, got \\" + cmd, stream.save());
		}

		throw new MathParseException("Expected binary operator", stream.save());
	}

	private boolean isSingleTokenStart() {
		if (stream.eof()) {
			return false;
		}

		char c = (char) stream.peek();
		return Character.isDigit(c) || Character.isLetter(c) || c == '\\' || "+-*/=<>".indexOf(c) >= 0;
	}

	/**
	 * 扫描单个token（数字、字母、希腊字母命令或运算符符号）
	 * 用于上下标等场景
	 * <single_token> ::= <digit>,<letter>,<greek_letter>,<operator_symbol>
	 */
	private String scanSingleToken() throws MathParseException {
		if (stream.eof()) {
			throw new MathParseException("Expected single token", stream.save());
		}

		char c = (char) stream.peek();

		// 数字
		if (Character.isDigit(c)) {
			return String.valueOf((char) stream.eat());
		}

		// 字母
		if (Character.isLetter(c) && c != '\\') {
			return String.valueOf((char) stream.eat());
		}

		// 运算符符号（允许在上下标中使用）
		if ("+-*/=<>".indexOf(c) >= 0) {
			return String.valueOf((char) stream.eat());
		}

		// 命令（如 \alpha, \pm）
		if (c == '\\') {
			stream.eat();
			String cmd = scanCommandName();
			if (GREEK_LETTERS.contains(cmd)) {
				return "\\" + cmd;
			}
			// 也可以是命令形式的运算符（如 \pm）
			if (BINARY_OPERATORS.contains(cmd)) {
				return "\\" + cmd;
			}
			throw new MathParseException("Expected single token, got \\" + cmd, stream.save());
		}

		throw new MathParseException("Expected single token", stream.save());
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
			throw new MathParseException("Expected \\" + expectedCmd + ", got \\" + cmd, startPos);
		}
	}

	private void expect(char expected) throws MathParseException {
		if (stream.eof()) {
			throw new MathParseException("Expected '" + expected + "', got end of input", stream.save());
		}

		char actual = (char) stream.peek();
		if (actual != expected) {
			throw new MathParseException("Expected '" + expected + "', got '" + actual + "'", stream.save());
		}

		stream.eat();
	}
}