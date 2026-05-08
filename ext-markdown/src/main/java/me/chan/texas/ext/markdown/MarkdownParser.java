package me.chan.texas.ext.markdown;

import me.chan.texas.ext.markdown.ast.MdAutolink;
import me.chan.texas.ext.markdown.ast.MdBlankLine;
import me.chan.texas.ext.markdown.ast.MdBlock;
import me.chan.texas.ext.markdown.ast.MdBlockQuote;
import me.chan.texas.ext.markdown.ast.MdCodeBlock;
import me.chan.texas.ext.markdown.ast.MdDocument;
import me.chan.texas.ext.markdown.ast.MdEmphasis;
import me.chan.texas.ext.markdown.ast.MdHeading;
import me.chan.texas.ext.markdown.ast.MdImage;
import me.chan.texas.ext.markdown.ast.MdInline;
import me.chan.texas.ext.markdown.ast.MdInlineCode;
import me.chan.texas.ext.markdown.ast.MdLink;
import me.chan.texas.ext.markdown.ast.MdListBlock;
import me.chan.texas.ext.markdown.ast.MdListItem;
import me.chan.texas.ext.markdown.ast.MdParagraph;
import me.chan.texas.ext.markdown.ast.MdStrong;
import me.chan.texas.ext.markdown.ast.MdTable;
import me.chan.texas.ext.markdown.ast.MdText;
import me.chan.texas.ext.markdown.ast.MdThematicBreak;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 按照 ext-markdown/bnf_markdown 实现的 Markdown 解析器
 */
public final class MarkdownParser {

	public MdDocument parse(String input) {
		if (input == null) {
			input = "";
		}
		String[] raw = input.split("\n", -1);
		List<String> lines = new ArrayList<>(raw.length);
		for (String line : raw) {
			if (line.endsWith("\r")) {
				line = line.substring(0, line.length() - 1);
			}
			lines.add(line);
		}
		// 文本以 \n 结尾时 split 会产生一个尾部空串，去掉它以避免多一行 blank line
		if (!lines.isEmpty() && lines.get(lines.size() - 1).isEmpty()
				&& !input.isEmpty() && input.charAt(input.length() - 1) == '\n') {
			lines.remove(lines.size() - 1);
		}
		Lines src = new Lines(lines);
		List<MdBlock> blocks = parseBlocks(src);
		return new MdDocument(blocks);
	}

	// ========================= 块级 =========================

	private List<MdBlock> parseBlocks(Lines src) {
		List<MdBlock> blocks = new ArrayList<>();
		while (!src.eof()) {
			MdBlock block = parseBlock(src);
			if (block != null) {
				blocks.add(block);
			}
		}
		return blocks;
	}

	private MdBlock parseBlock(Lines src) {
		String line = src.peek();
		if (isBlank(line)) {
			src.consume();
			return MdBlankLine.INSTANCE;
		}
		if (isThematicBreak(line)) {
			src.consume();
			return new MdThematicBreak(line.trim().charAt(0));
		}
		if (isAtxHeading(line)) {
			return parseAtxHeading(src);
		}
		if (line.startsWith(">")) {
			return parseBlockQuote(src);
		}
		if (isFenceLine(line) >= 0) {
			return parseFencedCode(src);
		}
		if (isUnorderedListMarker(line) != 0) {
			return parseUnorderedList(src);
		}
		if (orderedListMarkerLength(line) > 0) {
			return parseOrderedList(src);
		}
		if (isIndentedCodeLine(line)) {
			return parseIndentedCode(src);
		}
		if (line.startsWith("|")) {
			return parseTable(src);
		}
		return parseParagraph(src);
	}

	private MdBlock parseAtxHeading(Lines src) {
		String line = src.consume();
		int i = 0;
		while (i < line.length() && line.charAt(i) == '#') {
			i++;
		}
		int level = i;
		String rest;
		if (i < line.length() && line.charAt(i) == ' ') {
			rest = line.substring(i + 1);
		} else {
			rest = "";
		}
		return new MdHeading(level, MdHeading.STYLE_ATX, parseInlines(rest));
	}

	private MdBlock parseParagraph(Lines src) {
		List<String> texts = new ArrayList<>();
		texts.add(src.consume());

		while (!src.eof()) {
			String next = src.peek();
			// setext: 段落已有内容，遇到 === 或 --- 即转为 setext 标题
			int level = setextLevel(next);
			if (level > 0) {
				src.consume();
				String joined = String.join("\n", texts);
				return new MdHeading(level, MdHeading.STYLE_SETEXT, parseInlines(joined));
			}
			if (isBlank(next) || isInterruptingBlockStart(next)) {
				break;
			}
			texts.add(src.consume());
		}
		String joined = String.join("\n", texts);
		return new MdParagraph(parseInlines(joined));
	}

	private MdBlockQuote parseBlockQuote(Lines src) {
		List<String> stripped = new ArrayList<>();
		while (!src.eof()) {
			String line = src.peek();
			if (!line.startsWith(">")) {
				break;
			}
			src.consume();
			String tail = line.substring(1);
			if (tail.startsWith(" ")) {
				tail = tail.substring(1);
			}
			stripped.add(tail);
		}
		List<MdBlock> inner = parseBlocks(new Lines(stripped));
		return new MdBlockQuote(inner);
	}

	private MdListBlock parseUnorderedList(Lines src) {
		List<MdListItem> items = new ArrayList<>();
		char marker = '\0';
		while (!src.eof()) {
			String line = src.peek();
			char m = isUnorderedListMarker(line);
			if (m == 0) {
				break;
			}
			if (items.isEmpty()) {
				marker = m;
			} else if (m != marker) {
				break;
			}
			src.consume();
			String content = line.substring(2);
			items.add(buildListItem(content));
		}
		return new MdListBlock(MdListBlock.KIND_UNORDERED, marker, -1, items);
	}

	private MdListBlock parseOrderedList(Lines src) {
		List<MdListItem> items = new ArrayList<>();
		int start = -1;
		while (!src.eof()) {
			String line = src.peek();
			int prefix = orderedListMarkerLength(line);
			if (prefix == 0) {
				break;
			}
			int dot = line.indexOf('.');
			int num = Integer.parseInt(line.substring(0, dot));
			if (items.isEmpty()) {
				start = num;
			}
			src.consume();
			String content = line.substring(prefix);
			items.add(buildListItem(content));
		}
		return new MdListBlock(MdListBlock.KIND_ORDERED, '\0', start, items);
	}

	private MdListItem buildListItem(String content) {
		List<MdBlock> blocks = new ArrayList<>(1);
		blocks.add(new MdParagraph(parseInlines(content)));
		return new MdListItem(blocks);
	}

	private MdCodeBlock parseFencedCode(Lines src) {
		String first = src.consume();
		int fenceLen = isFenceLine(first);
		char fenceChar = first.charAt(0);
		String info = first.substring(fenceLen).trim();
		StringBuilder content = new StringBuilder();
		boolean firstContentLine = true;
		while (!src.eof()) {
			String line = src.peek();
			if (isFenceClose(line, fenceChar, fenceLen)) {
				src.consume();
				break;
			}
			src.consume();
			if (!firstContentLine) {
				content.append('\n');
			}
			content.append(line);
			firstContentLine = false;
		}
		return new MdCodeBlock(MdCodeBlock.KIND_FENCED, info, content.toString());
	}

	private MdCodeBlock parseIndentedCode(Lines src) {
		StringBuilder content = new StringBuilder();
		boolean first = true;
		while (!src.eof()) {
			String line = src.peek();
			if (!isIndentedCodeLine(line)) {
				break;
			}
			src.consume();
			String stripped = line.startsWith("\t") ? line.substring(1) : line.substring(4);
			if (!first) {
				content.append('\n');
			}
			content.append(stripped);
			first = false;
		}
		return new MdCodeBlock(MdCodeBlock.KIND_INDENTED, "", content.toString());
	}

	private MdBlock parseTable(Lines src) {
		// 至少需要 header + 对齐行
		if (!src.hasAt(1)) {
			return parseParagraph(src);
		}
		String headerLine = src.peek();
		String alignLine = src.peekAt(1);
		int[] aligns = parseAlignmentRow(alignLine);
		if (aligns == null) {
			// 不是合法的对齐行，回退为段落
			return parseParagraph(src);
		}
		src.consume();
		src.consume();

		int columns = aligns.length;
		List<List<MdInline>> header = parseTableRow(headerLine, columns);
		List<List<List<MdInline>>> body = new ArrayList<>();
		while (!src.eof()) {
			String line = src.peek();
			if (!line.startsWith("|")) {
				break;
			}
			src.consume();
			body.add(parseTableRow(line, columns));
		}
		return new MdTable(header, aligns, body);
	}

	private List<List<MdInline>> parseTableRow(String line, int columns) {
		List<String> cells = splitTableCells(line);
		List<List<MdInline>> row = new ArrayList<>(columns);
		for (int i = 0; i < columns; i++) {
			String cell = i < cells.size() ? cells.get(i) : "";
			row.add(parseInlines(cell));
		}
		return row;
	}

	/**
	 * 解析对齐行 `| :--- | :---: | ---: |`，非法返回 null
	 */
	private static int[] parseAlignmentRow(String line) {
		if (!line.startsWith("|")) {
			return null;
		}
		List<String> cells = splitTableCells(line);
		if (cells.isEmpty()) {
			return null;
		}
		int[] aligns = new int[cells.size()];
		for (int i = 0; i < cells.size(); i++) {
			String cell = cells.get(i).trim();
			if (cell.isEmpty()) {
				return null;
			}
			boolean leftColon = cell.charAt(0) == ':';
			boolean rightColon = cell.charAt(cell.length() - 1) == ':';
			int s = leftColon ? 1 : 0;
			int e = rightColon ? cell.length() - 1 : cell.length();
			if (e - s < 1) {
				return null;
			}
			for (int k = s; k < e; k++) {
				if (cell.charAt(k) != '-') {
					return null;
				}
			}
			if (leftColon && rightColon) {
				aligns[i] = MdTable.ALIGN_CENTER;
			} else if (leftColon) {
				aligns[i] = MdTable.ALIGN_LEFT;
			} else if (rightColon) {
				aligns[i] = MdTable.ALIGN_RIGHT;
			} else {
				aligns[i] = MdTable.ALIGN_NONE;
			}
		}
		return aligns;
	}

	/**
	 * 按 GFM 切分单元格：剥掉首尾 `|`，按未转义的 `|` 分隔，对每个单元格 trim
	 */
	private static List<String> splitTableCells(String line) {
		int from = 0;
		int to = line.length();
		if (from < to && line.charAt(from) == '|') {
			from++;
		}
		if (to > from && line.charAt(to - 1) == '|'
				&& !(to - 2 >= from && line.charAt(to - 2) == '\\')) {
			to--;
		}
		List<String> cells = new ArrayList<>();
		StringBuilder cur = new StringBuilder();
		for (int i = from; i < to; i++) {
			char c = line.charAt(i);
			if (c == '\\' && i + 1 < to && line.charAt(i + 1) == '|') {
				cur.append('|');
				i++;
				continue;
			}
			if (c == '|') {
				cells.add(cur.toString().trim());
				cur.setLength(0);
				continue;
			}
			cur.append(c);
		}
		cells.add(cur.toString().trim());
		return cells;
	}

	// ========================= 块级判断 =========================

	private static boolean isBlank(String line) {
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (c != ' ' && c != '\t') {
				return false;
			}
		}
		return true;
	}

	private static boolean isThematicBreak(String line) {
		String trimmed = line.trim();
		if (trimmed.length() < 3) {
			return false;
		}
		char c = trimmed.charAt(0);
		if (c != '-' && c != '*' && c != '_') {
			return false;
		}
		for (int i = 1; i < trimmed.length(); i++) {
			if (trimmed.charAt(i) != c) {
				return false;
			}
		}
		return true;
	}

	private static int setextLevel(String line) {
		String trimmed = line.trim();
		if (trimmed.length() < 3) {
			return 0;
		}
		char c = trimmed.charAt(0);
		if (c != '=' && c != '-') {
			return 0;
		}
		for (int i = 1; i < trimmed.length(); i++) {
			if (trimmed.charAt(i) != c) {
				return 0;
			}
		}
		return c == '=' ? 1 : 2;
	}

	private static boolean isAtxHeading(String line) {
		int i = 0;
		while (i < line.length() && line.charAt(i) == '#') {
			i++;
		}
		if (i == 0 || i > 6) {
			return false;
		}
		// '#' 之后必须是行尾或空格
		return i == line.length() || line.charAt(i) == ' ';
	}

	private static char isUnorderedListMarker(String line) {
		if (line.length() < 2) {
			return 0;
		}
		char c = line.charAt(0);
		if ((c == '-' || c == '*' || c == '+') && line.charAt(1) == ' ') {
			return c;
		}
		return 0;
	}

	/**
	 * @return 0 表示不是有序列表标记，否则返回 "数字." 之后空格的下一位下标（即内容起点）
	 */
	private static int orderedListMarkerLength(String line) {
		int i = 0;
		while (i < line.length() && line.charAt(i) >= '0' && line.charAt(i) <= '9') {
			i++;
		}
		if (i == 0 || i >= line.length()) {
			return 0;
		}
		if (line.charAt(i) != '.') {
			return 0;
		}
		if (i + 1 >= line.length() || line.charAt(i + 1) != ' ') {
			return 0;
		}
		return i + 2;
	}

	private static boolean isIndentedCodeLine(String line) {
		if (line.startsWith("\t")) {
			return true;
		}
		if (line.length() >= 4 && line.startsWith("    ")) {
			return true;
		}
		return false;
	}

	/**
	 * @return fence 的长度（包括 ``` 或 ~~~ 标记），不是 fence 时返回 -1
	 */
	private static int isFenceLine(String line) {
		if (line.length() < 3) {
			return -1;
		}
		char c = line.charAt(0);
		if (c != '`' && c != '~') {
			return -1;
		}
		int i = 0;
		while (i < line.length() && line.charAt(i) == c) {
			i++;
		}
		return i >= 3 ? i : -1;
	}

	private static boolean isFenceClose(String line, char fenceChar, int fenceLen) {
		String trimmed = line.trim();
		if (trimmed.length() < fenceLen) {
			return false;
		}
		for (int i = 0; i < trimmed.length(); i++) {
			if (trimmed.charAt(i) != fenceChar) {
				return false;
			}
		}
		return trimmed.length() >= fenceLen;
	}

	/**
	 * 段落延续中是否遇到了新的块级标记
	 */
	private static boolean isInterruptingBlockStart(String line) {
		if (isThematicBreak(line)) {
			return true;
		}
		if (isAtxHeading(line)) {
			return true;
		}
		if (line.startsWith(">")) {
			return true;
		}
		if (isFenceLine(line) > 0) {
			return true;
		}
		if (isUnorderedListMarker(line) != 0) {
			return true;
		}
		if (orderedListMarkerLength(line) > 0) {
			return true;
		}
		if (line.startsWith("|")) {
			return true;
		}
		return false;
	}

	// ========================= 内联 =========================

	public List<MdInline> parseInlines(String text) {
		List<MdInline> result = new ArrayList<>();
		StringBuilder acc = new StringBuilder();
		int i = 0;
		int n = text.length();
		while (i < n) {
			char c = text.charAt(i);
			// image: ![alt](url)
			if (c == '!' && i + 1 < n && text.charAt(i + 1) == '[') {
				int next = tryParseLinkOrImage(text, i, true, result, acc);
				if (next > i) {
					i = next;
					continue;
				}
			}
			// link: [text](url)
			if (c == '[') {
				int next = tryParseLinkOrImage(text, i, false, result, acc);
				if (next > i) {
					i = next;
					continue;
				}
			}
			// strong **
			if (c == '*' && i + 1 < n && text.charAt(i + 1) == '*') {
				int close = findCloseDouble(text, i + 2, '*');
				if (close > i + 2) {
					flushText(acc, result);
					result.add(new MdStrong("**", parseInlines(text.substring(i + 2, close))));
					i = close + 2;
					continue;
				}
			}
			// strong __
			if (c == '_' && i + 1 < n && text.charAt(i + 1) == '_') {
				int close = findCloseDouble(text, i + 2, '_');
				if (close > i + 2) {
					flushText(acc, result);
					result.add(new MdStrong("__", parseInlines(text.substring(i + 2, close))));
					i = close + 2;
					continue;
				}
			}
			// emphasis *
			if (c == '*') {
				int close = findCloseSingle(text, i + 1, '*');
				if (close > i + 1) {
					flushText(acc, result);
					result.add(new MdEmphasis("*", parseInlines(text.substring(i + 1, close))));
					i = close + 1;
					continue;
				}
			}
			// emphasis _
			if (c == '_') {
				int close = findCloseSingle(text, i + 1, '_');
				if (close > i + 1) {
					flushText(acc, result);
					result.add(new MdEmphasis("_", parseInlines(text.substring(i + 1, close))));
					i = close + 1;
					continue;
				}
			}
			// inline code
			if (c == '`') {
				int close = text.indexOf('`', i + 1);
				if (close > i) {
					flushText(acc, result);
					result.add(new MdInlineCode(text.substring(i + 1, close)));
					i = close + 1;
					continue;
				}
			}
			// autolink <url>
			if (c == '<') {
				int close = text.indexOf('>', i + 1);
				if (close > i) {
					String inner = text.substring(i + 1, close);
					if (looksLikeAutolink(inner)) {
						flushText(acc, result);
						result.add(new MdAutolink(inner));
						i = close + 1;
						continue;
					}
				}
			}
			acc.append(c);
			i++;
		}
		flushText(acc, result);
		return result;
	}

	private int tryParseLinkOrImage(String text, int start, boolean image,
									List<MdInline> result, StringBuilder acc) {
		int i = start + (image ? 2 : 1);
		// 寻找匹配的 ]
		int depth = 1;
		int closeBracket = -1;
		for (int j = i; j < text.length(); j++) {
			char ch = text.charAt(j);
			if (ch == '`') {
				int codeEnd = text.indexOf('`', j + 1);
				if (codeEnd < 0) {
					break;
				}
				j = codeEnd;
				continue;
			}
			if (ch == '[') {
				depth++;
			} else if (ch == ']') {
				depth--;
				if (depth == 0) {
					closeBracket = j;
					break;
				}
			}
		}
		if (closeBracket < 0) {
			return start;
		}
		if (closeBracket + 1 >= text.length() || text.charAt(closeBracket + 1) != '(') {
			return start;
		}
		int parenStart = closeBracket + 2;
		// 找配对的 ) — 跳过引号包裹的标题
		int parenClose = findClosingParen(text, parenStart);
		if (parenClose < 0) {
			return start;
		}
		String inside = text.substring(parenStart, parenClose);
		String url;
		String title = "";
		int sp = findSpaceOutsideQuotes(inside);
		if (sp >= 0) {
			url = inside.substring(0, sp).trim();
			String t = inside.substring(sp + 1).trim();
			if (t.length() >= 2) {
				char q0 = t.charAt(0);
				char qe = t.charAt(t.length() - 1);
				if ((q0 == '"' && qe == '"') || (q0 == '\'' && qe == '\'')) {
					title = t.substring(1, t.length() - 1);
				} else {
					title = t;
				}
			} else {
				title = t;
			}
		} else {
			url = inside.trim();
		}
		String linkText = text.substring(i, closeBracket);
		flushText(acc, result);
		List<MdInline> inner = parseInlines(linkText);
		if (image) {
			result.add(new MdImage(inner, url, title));
		} else {
			result.add(new MdLink(inner, url, title));
		}
		return parenClose + 1;
	}

	private static int findClosingParen(String text, int start) {
		int depth = 1;
		int i = start;
		while (i < text.length()) {
			char c = text.charAt(i);
			if (c == '"' || c == '\'') {
				int end = text.indexOf(c, i + 1);
				if (end < 0) {
					return -1;
				}
				i = end + 1;
				continue;
			}
			if (c == '(') {
				depth++;
			} else if (c == ')') {
				depth--;
				if (depth == 0) {
					return i;
				}
			}
			i++;
		}
		return -1;
	}

	private static int findSpaceOutsideQuotes(String s) {
		boolean inQuote = false;
		char quoteChar = 0;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (inQuote) {
				if (c == quoteChar) {
					inQuote = false;
				}
				continue;
			}
			if (c == '"' || c == '\'') {
				inQuote = true;
				quoteChar = c;
				continue;
			}
			if (c == ' ' || c == '\t') {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 在 start 起搜索单字符强调闭合符 d；跳过 inline code 与同字符的双字符（强调）块
	 */
	private static int findCloseSingle(String text, int start, char d) {
		int i = start;
		int n = text.length();
		while (i < n) {
			char c = text.charAt(i);
			if (c == '`') {
				int close = text.indexOf('`', i + 1);
				if (close < 0) {
					return -1;
				}
				i = close + 1;
				continue;
			}
			if (c == d) {
				if (i + 1 < n && text.charAt(i + 1) == d) {
					int close = text.indexOf("" + d + d, i + 2);
					if (close < 0) {
						return -1;
					}
					i = close + 2;
					continue;
				}
				return i;
			}
			i++;
		}
		return -1;
	}

	/**
	 * 在 start 起搜索双字符强调闭合符（dd）；跳过 inline code
	 */
	private static int findCloseDouble(String text, int start, char d) {
		int i = start;
		int n = text.length();
		while (i < n - 1) {
			char c = text.charAt(i);
			if (c == '`') {
				int close = text.indexOf('`', i + 1);
				if (close < 0) {
					return -1;
				}
				i = close + 1;
				continue;
			}
			if (c == d && text.charAt(i + 1) == d) {
				return i;
			}
			i++;
		}
		return -1;
	}

	private static boolean looksLikeAutolink(String s) {
		// scheme:// 或 mailto: 或包含 @
		if (s.isEmpty()) {
			return false;
		}
		int colon = s.indexOf(':');
		if (colon > 0) {
			for (int i = 0; i < colon; i++) {
				char c = s.charAt(i);
				if (!(Character.isLetterOrDigit(c) || c == '+' || c == '.' || c == '-')) {
					return false;
				}
			}
			return true;
		}
		// email-style
		int at = s.indexOf('@');
		return at > 0 && at < s.length() - 1;
	}

	private static void flushText(StringBuilder acc, List<MdInline> result) {
		if (acc.length() == 0) {
			return;
		}
		result.add(new MdText(acc.toString()));
		acc.setLength(0);
	}

	// ========================= 行游标 =========================

	private static final class Lines {
		private final List<String> data;
		private int idx;

		Lines(List<String> data) {
			this.data = data;
		}

		Lines(String[] data) {
			this.data = new ArrayList<>(Arrays.asList(data));
		}

		boolean eof() {
			return idx >= data.size();
		}

		boolean hasAt(int offset) {
			return idx + offset < data.size();
		}

		String peek() {
			return data.get(idx);
		}

		String peekAt(int offset) {
			return data.get(idx + offset);
		}

		String consume() {
			return data.get(idx++);
		}
	}
}
