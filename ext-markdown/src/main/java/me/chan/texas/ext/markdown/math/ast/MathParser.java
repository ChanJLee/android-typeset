package me.chan.texas.ext.markdown.math.ast;

import me.chan.texas.utils.CharStream;

import java.util.*;

// ==================== AST节点定义 ====================


// ==================== 异常类 ====================

// ==================== 基于CharStream的解析器 ====================

public class MathParser {
	private final CharStream stream;
	private int recursionDepth = 0;
	private static final int MAX_RECURSION_DEPTH = 100;

	// 二元运算符集合
	private static final Set<String> BINARY_OPERATORS = new HashSet<>(Arrays.asList(
			"+", "-", "*", "/", "=", "<", ">",
			"times", "cdot", "div", "pm", "mp",
			"neq", "equiv", "approx", "le", "ge", "leq", "geq",
			"in", "notin", "subset", "supset",
			"to", "rightarrow", "leftarrow", "Rightarrow"
	));

	// 函数名集合
	private static final Set<String> FUNCTION_NAMES = new HashSet<>(Arrays.asList(
			"sin", "cos", "tan", "cot", "sec", "csc",
			"arcsin", "arccos", "arctan",
			"sinh", "cosh", "tanh",
			"log", "ln", "lg", "exp",
			"max", "min", "sup", "inf",
			"det", "dim", "gcd", "ker"
	));

	// 大型运算符集合
	private static final Set<String> LARGE_OPERATORS = new HashSet<>(Arrays.asList(
			"sum", "prod", "coprod",
			"int", "iint", "iiint", "oint", "oiint",
			"bigcup", "bigcap", "bigvee", "bigwedge",
			"bigoplus", "bigotimes", "bigodot",
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
	public static final String[][] DELIMITER_LEVELS = {
			{
					"left", "right"
			},
			{
					"bigl", "bigr"
			},
			{
					"Bigl", "Bigr"
			},
			{
					"biggl", "biggr"
			},
			{
					"Biggl", "Biggr"
			}
	};

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
	 * <math_list> ::= <term> { <binary_op> <term> }
	 */
	private MathList parseMathList() throws MathParseException {
		checkRecursionDepth();

		List<Ast> ast = new ArrayList<>();

		skipWhitespace();

		// 第一个term
		ast.add(parseTerm());

		// 后续的 op term 对
		skipWhitespace();
		while (!stream.eof()) {
			if (isBinaryOperator()) {
				ast.add(new BinOpAtom(consumeBinaryOperator()));
			} else if (isTermEnd()) {
				break;
			}
			skipWhitespace();
			ast.add(parseTerm());
			skipWhitespace();
		}

		return new MathList(ast);
	}

	/**
	 * 辅助方法：判断当前是否是 term 的结束位置
	 * 例如，当遇到右括号、\right 命令或文件末尾时
	 */
	private boolean isTermEnd() {
		if (stream.eof()) {
			return true;
		}
		char c = (char) stream.peek();
		if (c == '}') { // 组结束
			return true;
		}
		if (c == ']') { // 组结束
			return true;
		}

		if (c == '\\') {
			int savedPos = stream.save();
			stream.eat(); // consume '\'
			String cmd = scanCommandName();
			stream.restore(savedPos);
			// \right 和 \end 是明确的结束标志
			return cmd.equals("right") || cmd.equals("end");
		}
		return false;
	}

	/**
	 * <term> ::= [ <unary_op> ] <atom> [ <sup_sub_suffix> ]
	 */
	private Term parseTerm() throws MathParseException {
		skipWhitespace();

		// 尝试解析可选的一元运算符
		// 一元运算符只在以下情况下被识别：
		// 1. 紧跟着数字、字母或命令
		// 2. 不在两个 term 之间（那是二元运算符）
		String unaryOp = parseUnaryOp();

		Atom atom = parseAtom();
		skipWhitespace();
		SupSubSuffix suffix = tryParseSupSubSuffix();
		return new Term(unaryOp, atom, suffix);
	}

	private String parseUnaryOp() throws MathParseException {
		if (!stream.eof()) {
			char c = (char) stream.peek();
			if (c == '+' || c == '-') {
				// 保存当前位置
				int saved = stream.save();
				stream.eat();
				skipWhitespace();

				// 检查后面是否有atom，且这个+/-不应该被当作二元运算符
				// 关键：一元运算符后必须紧跟atom（可能有空格）
				if (!stream.eof() && isAtomStart()) {
					// 这是一元运算符
					return String.valueOf(c);
					// 注意：不要恢复stream，已经消耗了运算符和空格
				} else {
					// 不是一元运算符（可能是二元运算符或其他），恢复stream
					stream.restore(saved);
				}
			} else if (c == '\\') {
				int saved = stream.save();
				stream.eat();
				String cmd = scanCommandName();
				if ("pm".equals(cmd) || "mp".equals(cmd)) {
					return "\\" + cmd;
				}
				stream.restore(saved);
			}
		}

		return null;
	}

	/**
	 * <atom> ::= <number> | <variable> | <greek_letter> | <group>
	 * | <frac> | <sqrt> | <delimited> | <function_call> | <large_operator> | ...
	 */
	private Atom parseAtom() throws MathParseException {
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
		if (Character.isLetter(c) && c != '\\') {
			return parseVariable();
		}

		// 分组
		if (c == '{') {
			return parseGroup();
		}

		// 命令（以\开头）
		if (c == '\\') {
			return parseCommand();
		}

		throw new MathParseException("Expected atom, got '" + c + "'", stream.save());
	}

	/**
	 * 解析命令（\开头的）
	 */
	private Atom parseCommand() throws MathParseException {
		int startPos = stream.save();
		expect('\\');

		String cmd = scanCommandName();

		if (cmd.isEmpty()) {
			throw new MathParseException("Empty command", startPos);
		}

		// 分式
		if (cmd.equals("frac") || cmd.equals("dfrac") || cmd.equals("tfrac") || cmd.equals("cfrac")) {
			return parseFrac(cmd);
		}

		// 根式
		if (cmd.equals("sqrt")) {
			return parseSqrt();
		}

		// 定界符
		int level = -1;
		if ((level = getDelimitedLevel(cmd)) >= 0) {
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

		// 希腊字母
		if (GREEK_LETTERS.contains(cmd)) {
			return new GreekLetterAtom(cmd);
		}

		// 文本命令
		if (cmd.equals("text") || cmd.equals("mbox") || cmd.equals("textrm")) {
			return parseText(cmd);
		}

		// 重音符号
		if (ACCENT_COMMANDS.contains(cmd)) {
			return parseAccent(cmd);
		}

		throw new MathParseException("Unknown command: \\" + cmd, startPos);
	}

	/**
	 * <number> ::= <digit> { <digit> } [ "." <digit> { <digit> } ]
	 */
	private NumberAtom parseNumber() throws MathParseException {
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

	/**
	 * <variable> ::= <letter>
	 */
	private VariableAtom parseVariable() throws MathParseException {
		char c = (char) stream.eat();
		return new VariableAtom(c);
	}

	/**
	 * <group> ::= "{" <math_list> "}"
	 */
	private GroupAtom parseGroup() throws MathParseException {
		expect('{');

		recursionDepth++;
		try {
			MathList content = parseMathList();
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
			expect('}');

			skipWhitespace();
			expect('{');
			MathList denominator = parseMathList();
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
			expect('}');
			return new SqrtAtom(content, root);
		} finally {
			recursionDepth--;
		}
	}

	/**
	 * <delimited> ::= "\left" <delimiter> <math_list> "\right" <delimiter>
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

			if (cmd.equals("{") || cmd.equals("}")) {
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
			expect('}');
			return new AccentAtom(accentCmd, content);
		} finally {
			recursionDepth--;
		}
	}

	/**
	 * <sup_sub_suffix> ::= "^" <script_arg> | "_" <script_arg>
	 * | "^" <script_arg> "_" <script_arg>
	 * | "_" <script_arg> "^" <script_arg>
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
	 * <script_arg> ::= <single_token> | <group>
	 */
	private ScriptArg parseScriptArg() throws MathParseException {
		skipWhitespace();

		if (stream.eof()) {
			throw new MathParseException("Expected script argument", stream.save());
		}

		if (stream.peek() == '{') {
			GroupAtom group = parseGroup();
			return new GroupScriptArg(group.getContent());
		} else if (isSingleTokenStart()) {
			String token = scanSingleToken();
			return new SingleTokenScriptArg(token);
		} else {
			throw new MathParseException("Expected script argument", stream.save());
		}
	}

	// ========== 辅助方法 ==========

	/**
	 * 判断当前位置是否是一个有效的 atom 开头
	 */
	private boolean isAtomStart() {
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

		// 命令（以\开头）
		if (c == '\\') {
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

		// 命令形式的运算符（如 \times, \cdot）
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
		return Character.isDigit(c) || Character.isLetter(c) || c == '\\';
	}

	/**
	 * 扫描单个token（数字、字母或希腊字母命令）
	 */
	/**
	 * 扫描单个token（数字、字母、希腊字母命令或运算符符号）
	 * 用于上下标等场景
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

		// 命令（如 \alpha）
		if (c == '\\') {
			stream.eat();
			String cmd = scanCommandName();
			if (GREEK_LETTERS.contains(cmd)) {
				return "\\" + cmd;
			}
			// 也可以是命令形式的运算符
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

		// 特殊情况：单字符命令（如 \{ \}）
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

	private boolean peekCommand(String expectedCmd) {
		if (stream.eof() || stream.peek() != '\\') {
			return false;
		}

		int saved = stream.save();
		stream.eat();  // 跳过 \
		String cmd = scanCommandName();
		stream.restore(saved);

		return cmd.equals(expectedCmd);
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
