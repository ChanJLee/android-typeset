package me.chan.texas.ext.markdown.math.ast;

import me.chan.texas.utils.CharStream;

import java.util.*;

public class MathParser {
	private final CharStream stream;
	private int recursionDepth = 0;
	private static final int MAX_RECURSION_DEPTH = 100;

	private static final Set<String> SYMBOL = new HashSet<>(Arrays.asList(
			"+", "-", "*", "/", "|", "!", "'", ",", ";", "<", ">", "="
			, "times", "cdot", "div", "pm", "mp"
			, "setminus", "circ", "oplus", "ominus", "otimes", "oslash", "odot"
			, "bullet", "star", "dagger", "ddagger", "neq", "equiv", "approx", "cong", "sim"
			, "simeq", "asymp", "propto", "le", "ge", "leq", "geq", "ll", "gg"
			, "prec", "succ", "preceq", "succeq", "in", "notin", "subset", "supset", "subseteq", "supseteq"
			, "cup", "cap", "wedge", "vee", "to", "rightarrow", "leftarrow", "leftrightarrow"
			, "Rightarrow", "Leftarrow", "Leftrightarrow"
			, "implies", "iff", "perp", "parallel", "mid", "nmid", "triangleleft", "triangleright"
			, "bowtie", "models", "neg", "dots", "ldots", "cdots", "vdots", "ddots"
			, "angle", "therefore", "because", "forall", "exists", "nexists", "emptyset", "varnothing"
			, "hbar", "nabla", "partial", "ell", "wp", "Re", "Im", "aleph"

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
	static final String[][] DELIMITER_LEVELS = {
			{"left", "right"},
			{"bigl", "bigr"},
			{"Bigl", "Bigr"},
			{"biggl", "biggr"},
			{"Biggl", "Biggr"}
	};
	private static final String[][] DELIMITER_PAIRS = {
			{"(", ")"},
			{"[", "]"},
			{"{", "}"},
			{"|", "|"},
			{"||", "||"},
			{"\\|", "\\|"},
			{"\\langle", "\\rangle"},
			{"\\lfloor", "\\rfloor"},
			{"\\lceil", "\\rceil"},
			{"\\lvert", "\\rvert"},
			{"\\lVert", "\\rVert"},
			{"\\{", "\\}"}
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
			"text", "mbox", "textrm", "textit", "textbf", "textfield")
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
	 */
	private MathList parseMathList() throws MathParseException {
		checkRecursionDepth();
		List<Ast> ast = new ArrayList<>();
		skipWhitespace();

		while (!isMathListEnd()) {
			ast.add(parseElement());
			skipWhitespace();
		}

		return new MathList(ast);
	}

	/*
	 * <element> ::= <spacing> | <term>
	 */
	private Ast parseElement() throws MathParseException {
		Spacing spacing = parseSpacing();
		if (spacing != null) {
			return spacing;
		}

		return parseTerm();
	}

	private SymbolAtom parseSymbol() {
		int save = stream.save();
		char c = (char) stream.peek();

		if ("+-*/|!',;<>=".indexOf(c) >= 0) {
			stream.eat();
			return new SymbolAtom(String.valueOf(c));
		}

		if (c == '\\') {
			stream.eat();
			String cmd = scanCommandName();
			if (SYMBOL.contains(cmd)) {
				return new SymbolAtom("\\" + cmd);
			}
		}

		stream.restore(save);
		return null;
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
	 * 解析空格命令
	 * <spacing> ::= "\,","\:","\;","\!","\quad","\qquad","\hspace" "{" <length> "}",...
	 */
	private Spacing parseSpacing() throws MathParseException {
		int save = stream.save();
		if (stream.peek() != '\\') {
			return null;
		}

		stream.eat();
		String cmd = scanCommandName();

		if (!SPACING_COMMANDS.contains(cmd)) {
			stream.restore(save);
			return null;
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
	 * <term> ::= <atom> [ <sup_sub_suffix> ]
	 */
	private Term parseTerm() throws MathParseException {
		Atom atom = parseAtom();
		skipWhitespace();
		SupSubSuffix suffix = parseSupSubSuffix();
		return new Term(atom, suffix);
	}

	/**
	 * <operand_atom> ::=<number> |
	 * <variable> |
	 * <greek_letter_variable> |
	 * <special_letter_variable> |
	 * <group> |
	 * <frac> |
	 * <binom> |
	 * <sqrt> |
	 * <delimited> |
	 * <function_call> |
	 * <large_operator> |
	 * <matrix> |
	 * <text> |
	 * <accent> |
	 * <font_command> |
	 * <extensible_arrow> |
	 * symbol
	 * <p>
	 * 可运算的原子表达式：可以被一元运算符修饰
	 * 注意：不包含 spacing 和 special_symbol
	 */
	private Atom parseAtom() throws MathParseException {
		int save = stream.save();
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

		// 符号
		SymbolAtom symbol = parseSymbol();
		if (symbol != null) {
			return symbol;
		}

		// 命令（以\开头）
		if (c == '\\') {
			return parseAtomStartWithCmd();
		}

		stream.restore(save);
		throw new MathParseException("Expected operand atom, got '" + c + "'", stream);
	}

	/**
	 * 解析命令形式的可运算原子（\开头的）
	 */
	private Atom parseAtomStartWithCmd() throws MathParseException {
		int save = stream.save();
		expect('\\');

		String cmd = scanCommandName();

		if (cmd.isEmpty()) {
			stream.restore(save);
			throw new MathParseException("Empty command", stream);
		}

		// 在希腊字母处理之后添加：
		if (GREEK_LETTERS.contains(cmd)) {
			return parseGreekLetterAtom(cmd);
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

		// 定界符
		int level = getDelimitedLevel(cmd);
		if (level >= 0) {
			stream.restore(save);
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

		// matrix
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

		// 可扩展箭头 - 新增
		if (EXTENSIBLE_ARROW_COMMANDS.contains(cmd)) {
			return parseExtensibleArrow(cmd);
		}

		stream.restore(save);
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
		String leftDelimiter = parseDelimiter();

		recursionDepth++;
		try {
			MathList content = parseMathList();

			skipWhitespace();
			expectCommand(DELIMITER_LEVELS[level][1]);
			skipWhitespace();

			String rightDelimiter = parseDelimiter();
			checkDelimiterPairs(leftDelimiter, rightDelimiter);

			return new DelimitedAtom(level, leftDelimiter, content, rightDelimiter);
		} finally {
			recursionDepth--;
		}
	}

	private void checkDelimiterPairs(String left, String right) throws MathParseException {
		if (".".equals(left) || ".".equals(right)) {
			return;
		}

		for (String[] pair : DELIMITER_PAIRS) {
			if (pair[0].equals(left) && pair[1].equals(right)) {
				return;
			}
		}
		throw new MathParseException("Invalid delimiter pair: [" + left + ", " + right + "]", stream);
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
	 * <function_call> ::= "\" <function_name>
	 */
	private FunctionCallAtom parseFunctionCall(String functionName) throws MathParseException {
		return new FunctionCallAtom(functionName);
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
	 * <large_operator> ::= "\" <large_op_name>
	 */
	private LargeOperatorAtom parseLargeOperator(String operatorName) {
		return new LargeOperatorAtom(operatorName);
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

		SingleToken content = parseSingleToken();
		return new AccentAtom(accentCmd, content);
	}


	/**
	 * <sup_sub_suffix> ::= "^" <script_arg>,"_" <script_arg>
	 * ,"^" <script_arg> "_" <script_arg>
	 * ,"_" <script_arg> "^" <script_arg>
	 */
	private SupSubSuffix parseSupSubSuffix() throws MathParseException {
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
	 * <script_arg> ::= <single_token>, <group>
	 */
	private ScriptArg parseScriptArg() throws MathParseException {
		if (stream.eof()) {
			throw new MathParseException("Expected script argument", stream);
		}

		char c = (char) stream.peek();
		if (isGroupStart(c)) {
			return new ScriptArg(parseGroup(c));
		}
		return new ScriptArg(parseSingleToken());
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

	/**
	 * <single_token> ::= <number> | <letter> | <greek_letter> | <symbol>
	 */
	private SingleToken parseSingleToken() throws MathParseException {
		if (stream.eof()) {
			throw new MathParseException("Expected single token", stream);
		}

		int save = stream.save();
		char c = (char) stream.peek();

		// 数字（可以是多位数字或小数）
		if (Character.isDigit(c)) {
			return new SingleToken(parseNumber());
		}

		// 变量（可以是多字母变量）
		if (Character.isLetter(c) && c != '\\') {
			stream.eat();
			return new SingleToken(new VariableAtom(String.valueOf(c)));
		}

		// 命令（如 \alpha, \pm）
		if (c == '\\') {
			stream.eat();
			String cmd = scanCommandName();
			if (GREEK_LETTERS.contains(cmd)) {
				return new SingleToken(parseGreekLetterAtom(cmd));
			}
			stream.restore(save);
		}

		SymbolAtom symbol = parseSymbol();
		if (symbol != null) {
			return new SingleToken(symbol);
		}

		throw new MathParseException("Expected single token at: " + save, stream);
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