package me.chan.texas.ext.markdown;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class MarkdownParser {
	private String[] mLines;
	private int mIndex;

	public MarkdownDocument parse(@Nullable CharSequence source) {
		String text = source == null ? "" : source.toString();
		mLines = text.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1);
		mIndex = 0;
		return new MarkdownDocument(parseBlocks(false));
	}

	private List<MarkdownDocument.Block> parseBlocks(boolean nested) {
		List<MarkdownDocument.Block> blocks = new ArrayList<>();
		while (mIndex < mLines.length) {
			String line = mLines[mIndex];
			if (isBlank(line)) {
				++mIndex;
				if (nested) {
					break;
				}
				continue;
			}

			MarkdownDocument.Block block = parseBlock();
			if (block != null) {
				blocks.add(block);
			}
		}
		return blocks;
	}

	@Nullable
	private MarkdownDocument.Block parseBlock() {
		String line = mLines[mIndex];
		Fence fence = parseFence(line);
		if (fence != null) {
			return parseFencedCodeBlock(fence);
		}
		if (isIndentedCodeLine(line)) {
			return parseIndentedCodeBlock();
		}
		MarkdownDocument.HeadingBlock atxHeading = parseAtxHeading(line);
		if (atxHeading != null) {
			++mIndex;
			return atxHeading;
		}
		if (isThematicBreak(line)) {
			++mIndex;
			return new MarkdownDocument.ThematicBreakBlock();
		}
		if (isBlockQuoteLine(line)) {
			return parseBlockQuote();
		}
		ListMarker listMarker = parseListMarker(line);
		if (listMarker != null) {
			return parseListBlock(listMarker);
		}
		if (mIndex + 1 < mLines.length && isTableAlign(mLines[mIndex + 1]) && containsPipe(line)) {
			return parseTable();
		}
		if (mIndex + 1 < mLines.length) {
			int level = parseSetextLevel(mLines[mIndex + 1]);
			if (level > 0) {
				MarkdownDocument.HeadingBlock heading = new MarkdownDocument.HeadingBlock(
						level, parseInlines(trim(line)));
				mIndex += 2;
				return heading;
			}
		}
		return parseParagraph();
	}

	private MarkdownDocument.CodeBlock parseFencedCodeBlock(Fence fence) {
		++mIndex;
		StringBuilder code = new StringBuilder();
		while (mIndex < mLines.length) {
			String line = mLines[mIndex];
			if (isClosingFence(line, fence)) {
				++mIndex;
				break;
			}
			if (code.length() > 0) {
				code.append('\n');
			}
			code.append(line);
			++mIndex;
		}
		return new MarkdownDocument.CodeBlock(fence.infoString, code.toString(), true);
	}

	private MarkdownDocument.CodeBlock parseIndentedCodeBlock() {
		StringBuilder code = new StringBuilder();
		while (mIndex < mLines.length) {
			String line = mLines[mIndex];
			if (isBlank(line)) {
				if (code.length() > 0) {
					code.append('\n');
				}
				++mIndex;
				continue;
			}
			if (!isIndentedCodeLine(line)) {
				break;
			}
			if (code.length() > 0) {
				code.append('\n');
			}
			code.append(stripCodeIndent(line));
			++mIndex;
		}
		return new MarkdownDocument.CodeBlock("", code.toString(), false);
	}

	private MarkdownDocument.BlockQuoteBlock parseBlockQuote() {
		StringBuilder inner = new StringBuilder();
		while (mIndex < mLines.length && isBlockQuoteLine(mLines[mIndex])) {
			if (inner.length() > 0) {
				inner.append('\n');
			}
			inner.append(stripBlockQuote(mLines[mIndex]));
			++mIndex;
		}
		MarkdownParser parser = new MarkdownParser();
		return new MarkdownDocument.BlockQuoteBlock(parser.parse(inner).getBlocks());
	}

	private MarkdownDocument.ListBlock parseListBlock(ListMarker firstMarker) {
		List<MarkdownDocument.ListItem> items = new ArrayList<>();
		MarkdownDocument.ListType type = firstMarker.ordered
				? MarkdownDocument.ListType.ORDERED
				: MarkdownDocument.ListType.UNORDERED;
		int startNumber = firstMarker.number;
		while (mIndex < mLines.length) {
			ListMarker marker = parseListMarker(mLines[mIndex]);
			if (marker == null || marker.ordered != firstMarker.ordered) {
				break;
			}
			StringBuilder item = new StringBuilder(marker.content);
			++mIndex;
			while (mIndex < mLines.length) {
				String line = mLines[mIndex];
				if (isBlank(line)) {
					++mIndex;
					break;
				}
				ListMarker next = parseListMarker(line);
				if (next != null && next.ordered == firstMarker.ordered) {
					break;
				}
				if (isContinuationLine(line)) {
					item.append('\n').append(stripContinuationIndent(line));
					++mIndex;
					continue;
				}
				break;
			}
			MarkdownParser parser = new MarkdownParser();
			items.add(new MarkdownDocument.ListItem(parser.parse(item).getBlocks()));
		}
		return new MarkdownDocument.ListBlock(type, startNumber, items);
	}

	private MarkdownDocument.TableBlock parseTable() {
		MarkdownDocument.TableRow header = parseTableRow(mLines[mIndex]);
		List<MarkdownDocument.Alignment> alignments = parseTableAlignments(mLines[mIndex + 1]);
		mIndex += 2;

		List<MarkdownDocument.TableRow> rows = new ArrayList<>();
		while (mIndex < mLines.length && containsPipe(mLines[mIndex]) && !isBlank(mLines[mIndex])) {
			rows.add(parseTableRow(mLines[mIndex]));
			++mIndex;
		}
		return new MarkdownDocument.TableBlock(header, alignments, rows);
	}

	private MarkdownDocument.ParagraphBlock parseParagraph() {
		StringBuilder paragraph = new StringBuilder();
		while (mIndex < mLines.length) {
			String line = mLines[mIndex];
			if (isBlank(line)) {
				break;
			}
			if (paragraph.length() > 0 && startsNewBlock(line)) {
				break;
			}
			if (mIndex + 1 < mLines.length && paragraph.length() == 0 && parseSetextLevel(mLines[mIndex + 1]) > 0) {
				break;
			}
			if (paragraph.length() > 0) {
				paragraph.append('\n');
			}
			paragraph.append(trim(line));
			++mIndex;
		}
		return new MarkdownDocument.ParagraphBlock(parseInlines(paragraph.toString()));
	}

	private boolean startsNewBlock(String line) {
		return parseFence(line) != null
				|| isIndentedCodeLine(line)
				|| parseAtxHeading(line) != null
				|| isThematicBreak(line)
				|| isBlockQuoteLine(line)
				|| parseListMarker(line) != null;
	}

	private MarkdownDocument.TableRow parseTableRow(String line) {
		List<String> cells = splitTableCells(line);
		List<List<MarkdownDocument.Inline>> inlines = new ArrayList<>(cells.size());
		for (int i = 0; i < cells.size(); ++i) {
			inlines.add(parseInlines(trim(cells.get(i))));
		}
		return new MarkdownDocument.TableRow(inlines);
	}

	private List<MarkdownDocument.Alignment> parseTableAlignments(String line) {
		List<String> cells = splitTableCells(line);
		List<MarkdownDocument.Alignment> alignments = new ArrayList<>(cells.size());
		for (int i = 0; i < cells.size(); ++i) {
			String cell = trim(cells.get(i));
			boolean left = cell.startsWith(":");
			boolean right = cell.endsWith(":");
			if (left && right) {
				alignments.add(MarkdownDocument.Alignment.CENTER);
			} else if (right) {
				alignments.add(MarkdownDocument.Alignment.RIGHT);
			} else if (left) {
				alignments.add(MarkdownDocument.Alignment.LEFT);
			} else {
				alignments.add(MarkdownDocument.Alignment.NONE);
			}
		}
		return alignments;
	}

	private boolean isTableAlign(String line) {
		if (!containsPipe(line)) {
			return false;
		}
		List<String> cells = splitTableCells(line);
		if (cells.isEmpty()) {
			return false;
		}
		for (int i = 0; i < cells.size(); ++i) {
			String cell = trim(cells.get(i));
			if (cell.length() < 3) {
				return false;
			}
			int start = cell.startsWith(":") ? 1 : 0;
			int end = cell.endsWith(":") ? cell.length() - 1 : cell.length();
			if (start >= end) {
				return false;
			}
			for (int j = start; j < end; ++j) {
				if (cell.charAt(j) != '-') {
					return false;
				}
			}
		}
		return true;
	}

	private List<String> splitTableCells(String line) {
		String value = trim(line);
		if (value.startsWith("|")) {
			value = value.substring(1);
		}
		if (value.endsWith("|")) {
			value = value.substring(0, value.length() - 1);
		}
		List<String> cells = new ArrayList<>();
		StringBuilder cell = new StringBuilder();
		boolean escape = false;
		for (int i = 0; i < value.length(); ++i) {
			char ch = value.charAt(i);
			if (escape) {
				cell.append(ch);
				escape = false;
			} else if (ch == '\\') {
				escape = true;
			} else if (ch == '|') {
				cells.add(cell.toString());
				cell.setLength(0);
			} else {
				cell.append(ch);
			}
		}
		cells.add(cell.toString());
		return cells;
	}

	private MarkdownDocument.HeadingBlock parseAtxHeading(String line) {
		String trimmed = trimLeft(line);
		int level = 0;
		while (level < trimmed.length() && trimmed.charAt(level) == '#') {
			++level;
		}
		if (level == 0 || level > 6) {
			return null;
		}
		if (level < trimmed.length() && trimmed.charAt(level) != ' ') {
			return null;
		}
		String content = level < trimmed.length() ? trim(trimmed.substring(level + 1)) : "";
		while (content.endsWith("#")) {
			content = trim(content.substring(0, content.length() - 1));
		}
		return new MarkdownDocument.HeadingBlock(level, parseInlines(content));
	}

	private int parseSetextLevel(String line) {
		String trimmed = trim(line);
		if (trimmed.length() == 0) {
			return 0;
		}
		char marker = trimmed.charAt(0);
		if (marker != '=' && marker != '-') {
			return 0;
		}
		for (int i = 0; i < trimmed.length(); ++i) {
			if (trimmed.charAt(i) != marker) {
				return 0;
			}
		}
		return marker == '=' ? 1 : 2;
	}

	private boolean isThematicBreak(String line) {
		String trimmed = trim(line);
		if (trimmed.length() < 3) {
			return false;
		}
		char marker = trimmed.charAt(0);
		if (marker != '-' && marker != '*' && marker != '_') {
			return false;
		}
		int count = 0;
		for (int i = 0; i < trimmed.length(); ++i) {
			char ch = trimmed.charAt(i);
			if (ch == marker) {
				++count;
			} else if (ch != ' ') {
				return false;
			}
		}
		return count >= 3;
	}

	@Nullable
	private Fence parseFence(String line) {
		String trimmed = trimLeft(line);
		if (!trimmed.startsWith("```") && !trimmed.startsWith("~~~")) {
			return null;
		}
		char marker = trimmed.charAt(0);
		int length = 0;
		while (length < trimmed.length() && trimmed.charAt(length) == marker) {
			++length;
		}
		if (length < 3) {
			return null;
		}
		String infoString = trim(trimmed.substring(length));
		return new Fence(marker, length, infoString);
	}

	private boolean isClosingFence(String line, Fence fence) {
		String trimmed = trimLeft(line);
		int length = 0;
		while (length < trimmed.length() && trimmed.charAt(length) == fence.marker) {
			++length;
		}
		if (length < fence.length) {
			return false;
		}
		return trim(trimmed.substring(length)).length() == 0;
	}

	private boolean isIndentedCodeLine(String line) {
		return line.startsWith("    ") || line.startsWith("\t");
	}

	private String stripCodeIndent(String line) {
		if (line.startsWith("\t")) {
			return line.substring(1);
		}
		return line.length() >= 4 ? line.substring(4) : line;
	}

	private boolean isBlockQuoteLine(String line) {
		return trimLeft(line).startsWith(">");
	}

	private String stripBlockQuote(String line) {
		String trimmed = trimLeft(line);
		String value = trimmed.substring(1);
		if (value.startsWith(" ")) {
			return value.substring(1);
		}
		return value;
	}

	@Nullable
	private ListMarker parseListMarker(String line) {
		String trimmed = trimLeft(line);
		if (trimmed.length() < 2) {
			return null;
		}
		char ch = trimmed.charAt(0);
		if ((ch == '-' || ch == '*' || ch == '+') && trimmed.charAt(1) == ' ') {
			return new ListMarker(false, 1, trimLeft(trimmed.substring(2)));
		}
		int index = 0;
		while (index < trimmed.length() && Character.isDigit(trimmed.charAt(index))) {
			++index;
		}
		if (index == 0 || index + 1 >= trimmed.length()) {
			return null;
		}
		if (trimmed.charAt(index) != '.' || trimmed.charAt(index + 1) != ' ') {
			return null;
		}
		int number;
		try {
			number = Integer.parseInt(trimmed.substring(0, index));
		} catch (NumberFormatException e) {
			return null;
		}
		return new ListMarker(true, number, trimLeft(trimmed.substring(index + 2)));
	}

	private boolean isContinuationLine(String line) {
		return line.startsWith("  ") || line.startsWith("\t");
	}

	private String stripContinuationIndent(String line) {
		if (line.startsWith("\t")) {
			return line.substring(1);
		}
		int count = 0;
		while (count < line.length() && count < 4 && line.charAt(count) == ' ') {
			++count;
		}
		return line.substring(count);
	}

	private boolean containsPipe(String line) {
		return line.indexOf('|') >= 0;
	}

	private boolean isBlank(String line) {
		return trim(line).length() == 0;
	}

	private List<MarkdownDocument.Inline> parseInlines(String text) {
		InlineScanner scanner = new InlineScanner(text);
		return scanner.parse(null);
	}

	private static String trim(String value) {
		return value == null ? "" : value.trim();
	}

	private static String trimLeft(String value) {
		int index = 0;
		while (index < value.length() && value.charAt(index) == ' ') {
			++index;
		}
		return value.substring(index);
	}

	private static final class Fence {
		final char marker;
		final int length;
		final String infoString;

		Fence(char marker, int length, String infoString) {
			this.marker = marker;
			this.length = length;
			this.infoString = infoString;
		}
	}

	private static final class ListMarker {
		final boolean ordered;
		final int number;
		final String content;

		ListMarker(boolean ordered, int number, String content) {
			this.ordered = ordered;
			this.number = number;
			this.content = content;
		}
	}

	private static final class InlineScanner {
		private final String text;
		private int index;

		InlineScanner(@NonNull String text) {
			this.text = text;
		}

		List<MarkdownDocument.Inline> parse(@Nullable String endMarker) {
			List<MarkdownDocument.Inline> inlines = new ArrayList<>();
			StringBuilder plain = new StringBuilder();
			while (index < text.length()) {
				if (endMarker != null && text.startsWith(endMarker, index)) {
					break;
				}
				MarkdownDocument.Inline inline = parseInline();
				if (inline == null) {
					plain.append(text.charAt(index));
					++index;
				} else {
					flushText(inlines, plain);
					inlines.add(inline);
				}
			}
			flushText(inlines, plain);
			return inlines;
		}

		@Nullable
		private MarkdownDocument.Inline parseInline() {
			if (text.startsWith("**", index)) {
				return parseStyled("**", MarkdownDocument.InlineType.STRONG);
			}
			if (text.startsWith("__", index)) {
				return parseStyled("__", MarkdownDocument.InlineType.STRONG);
			}
			char ch = text.charAt(index);
			if (ch == '*' || ch == '_') {
				return parseStyled(String.valueOf(ch), MarkdownDocument.InlineType.EMPHASIS);
			}
			if (ch == '`') {
				return parseCode();
			}
			if (text.startsWith("![", index)) {
				return parseLink(true);
			}
			if (ch == '[') {
				return parseLink(false);
			}
			if (ch == '<') {
				return parseAutolink();
			}
			if (ch == '\\' && index + 1 < text.length()) {
				++index;
				return new MarkdownDocument.TextInline(String.valueOf(text.charAt(index++)));
			}
			return null;
		}

		@Nullable
		private MarkdownDocument.Inline parseStyled(String marker, MarkdownDocument.InlineType type) {
			int end = text.indexOf(marker, index + marker.length());
			if (end < 0) {
				return null;
			}
			index += marker.length();
			List<MarkdownDocument.Inline> children = parse(marker);
			if (!text.startsWith(marker, index)) {
				index -= marker.length();
				return null;
			}
			index += marker.length();
			return new MarkdownDocument.StyledInline(type, children);
		}

		@Nullable
		private MarkdownDocument.Inline parseCode() {
			int end = text.indexOf('`', index + 1);
			if (end < 0) {
				return null;
			}
			String code = text.substring(index + 1, end);
			index = end + 1;
			return new MarkdownDocument.CodeInline(code);
		}

		@Nullable
		private MarkdownDocument.Inline parseLink(boolean image) {
			int labelStart = image ? index + 2 : index + 1;
			int labelEnd = findClosing(labelStart, '[', ']');
			if (labelEnd < 0 || labelEnd + 1 >= text.length() || text.charAt(labelEnd + 1) != '(') {
				return null;
			}
			int destinationEnd = findClosing(labelEnd + 2, '(', ')');
			if (destinationEnd < 0) {
				return null;
			}
			String label = text.substring(labelStart, labelEnd);
			LinkDestination destination = parseDestination(text.substring(labelEnd + 2, destinationEnd));
			InlineScanner scanner = new InlineScanner(label);
			index = destinationEnd + 1;
			return new MarkdownDocument.LinkInline(
					image ? MarkdownDocument.InlineType.IMAGE : MarkdownDocument.InlineType.LINK,
					scanner.parse(null), destination.url, destination.title);
		}

		@Nullable
		private MarkdownDocument.Inline parseAutolink() {
			int end = text.indexOf('>', index + 1);
			if (end < 0) {
				return null;
			}
			String url = text.substring(index + 1, end);
			if (url.indexOf(' ') >= 0 || url.indexOf('\t') >= 0 || url.length() == 0) {
				return null;
			}
			index = end + 1;
			List<MarkdownDocument.Inline> label = new ArrayList<>();
			label.add(new MarkdownDocument.TextInline(url));
			return new MarkdownDocument.LinkInline(MarkdownDocument.InlineType.AUTOLINK, label, url, "");
		}

		private int findClosing(int start, char open, char close) {
			int depth = 0;
			boolean escape = false;
			for (int i = start; i < text.length(); ++i) {
				char ch = text.charAt(i);
				if (escape) {
					escape = false;
				} else if (ch == '\\') {
					escape = true;
				} else if (ch == open) {
					++depth;
				} else if (ch == close) {
					if (depth == 0) {
						return i;
					}
					--depth;
				}
			}
			return -1;
		}

		private LinkDestination parseDestination(String raw) {
			String value = trim(raw);
			if (value.length() == 0) {
				return new LinkDestination("", "");
			}
			int space = findFirstSpace(value);
			if (space < 0) {
				return new LinkDestination(value, "");
			}
			String title = trim(value.substring(space + 1));
			if ((title.startsWith("\"") && title.endsWith("\""))
					|| (title.startsWith("'") && title.endsWith("'"))) {
				title = title.substring(1, title.length() - 1);
			}
			return new LinkDestination(value.substring(0, space), title);
		}

		private int findFirstSpace(String value) {
			for (int i = 0; i < value.length(); ++i) {
				char ch = value.charAt(i);
				if (ch == ' ' || ch == '\t') {
					return i;
				}
			}
			return -1;
		}

		private void flushText(List<MarkdownDocument.Inline> inlines, StringBuilder plain) {
			if (plain.length() == 0) {
				return;
			}
			inlines.add(new MarkdownDocument.TextInline(plain.toString()));
			plain.setLength(0);
		}
	}

	private static final class LinkDestination {
		final String url;
		final String title;

		LinkDestination(String url, String title) {
			this.url = url;
			this.title = title;
		}
	}
}
