package me.chan.texas.ext.markdown;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import me.chan.texas.ext.markdown.ast.MdAutolink;
import me.chan.texas.ext.markdown.ast.MdBlankLine;
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
import me.chan.texas.ext.markdown.ast.MdParagraph;
import me.chan.texas.ext.markdown.ast.MdStrong;
import me.chan.texas.ext.markdown.ast.MdTable;
import me.chan.texas.ext.markdown.ast.MdText;
import me.chan.texas.ext.markdown.ast.MdThematicBreak;

import org.junit.Test;

import java.util.List;

public class MarkdownParserUnitTest {

	private final MarkdownParser parser = new MarkdownParser();

	@Test
	public void atxHeading() {
		MdDocument doc = parser.parse("# Hello");
		assertEquals(1, doc.blocks.size());
		MdHeading h = (MdHeading) doc.blocks.get(0);
		assertEquals(1, h.level);
		assertEquals(MdHeading.STYLE_ATX, h.style);
		assertEquals("Hello", ((MdText) h.inlines.get(0)).text);
	}

	@Test
	public void atxHeadingLevels() {
		MdDocument doc = parser.parse("###### Six");
		MdHeading h = (MdHeading) doc.blocks.get(0);
		assertEquals(6, h.level);
	}

	@Test
	public void setextHeading() {
		MdDocument doc = parser.parse("Title\n===");
		MdHeading h = (MdHeading) doc.blocks.get(0);
		assertEquals(1, h.level);
		assertEquals(MdHeading.STYLE_SETEXT, h.style);

		MdDocument doc2 = parser.parse("Sub\n---");
		MdHeading h2 = (MdHeading) doc2.blocks.get(0);
		assertEquals(2, h2.level);
	}

	@Test
	public void thematicBreak() {
		MdDocument doc = parser.parse("---");
		assertTrue(doc.blocks.get(0) instanceof MdThematicBreak);
		assertEquals('-', ((MdThematicBreak) doc.blocks.get(0)).marker);
	}

	@Test
	public void blankLineSeparates() {
		MdDocument doc = parser.parse("a\n\nb");
		assertEquals(3, doc.blocks.size());
		assertTrue(doc.blocks.get(0) instanceof MdParagraph);
		assertTrue(doc.blocks.get(1) instanceof MdBlankLine);
		assertTrue(doc.blocks.get(2) instanceof MdParagraph);
	}

	@Test
	public void paragraphMultiLine() {
		MdDocument doc = parser.parse("line1\nline2\nline3");
		assertEquals(1, doc.blocks.size());
		MdParagraph p = (MdParagraph) doc.blocks.get(0);
		assertEquals("line1\nline2\nline3", ((MdText) p.inlines.get(0)).text);
	}

	@Test
	public void emphasisAndStrong() {
		MdDocument doc = parser.parse("a *b* **c** _d_ __e__");
		MdParagraph p = (MdParagraph) doc.blocks.get(0);
		List<MdInline> il = p.inlines;
		assertTrue(il.get(0) instanceof MdText);
		assertTrue(il.get(1) instanceof MdEmphasis);
		assertTrue(il.get(3) instanceof MdStrong);
		assertTrue(il.get(5) instanceof MdEmphasis);
		assertTrue(il.get(7) instanceof MdStrong);
	}

	@Test
	public void inlineCode() {
		MdDocument doc = parser.parse("a `x` b");
		MdParagraph p = (MdParagraph) doc.blocks.get(0);
		assertTrue(p.inlines.get(1) instanceof MdInlineCode);
		assertEquals("x", ((MdInlineCode) p.inlines.get(1)).code);
	}

	@Test
	public void link() {
		MdDocument doc = parser.parse("see [docs](https://example.com \"Title\") here");
		MdParagraph p = (MdParagraph) doc.blocks.get(0);
		MdLink link = (MdLink) p.inlines.get(1);
		assertEquals("https://example.com", link.url);
		assertEquals("Title", link.title);
		assertEquals("docs", ((MdText) link.inlines.get(0)).text);
	}

	@Test
	public void image() {
		MdDocument doc = parser.parse("![alt](pic.png)");
		MdParagraph p = (MdParagraph) doc.blocks.get(0);
		MdImage img = (MdImage) p.inlines.get(0);
		assertEquals("pic.png", img.url);
	}

	@Test
	public void autolink() {
		MdDocument doc = parser.parse("see <https://x.dev>");
		MdParagraph p = (MdParagraph) doc.blocks.get(0);
		MdAutolink al = (MdAutolink) p.inlines.get(1);
		assertEquals("https://x.dev", al.url);
	}

	@Test
	public void blockQuote() {
		MdDocument doc = parser.parse("> hello\n> world");
		MdBlockQuote bq = (MdBlockQuote) doc.blocks.get(0);
		assertEquals(1, bq.blocks.size());
		MdParagraph p = (MdParagraph) bq.blocks.get(0);
		assertEquals("hello\nworld", ((MdText) p.inlines.get(0)).text);
	}

	@Test
	public void unorderedList() {
		MdDocument doc = parser.parse("- a\n- b\n- c");
		MdListBlock list = (MdListBlock) doc.blocks.get(0);
		assertEquals(MdListBlock.KIND_UNORDERED, list.kind);
		assertEquals('-', list.marker);
		assertEquals(3, list.items.size());
	}

	@Test
	public void orderedList() {
		MdDocument doc = parser.parse("1. a\n2. b");
		MdListBlock list = (MdListBlock) doc.blocks.get(0);
		assertEquals(MdListBlock.KIND_ORDERED, list.kind);
		assertEquals(1, list.start);
		assertEquals(2, list.items.size());
	}

	@Test
	public void fencedCodeBlock() {
		MdDocument doc = parser.parse("```java\nint x = 1;\n```");
		MdCodeBlock cb = (MdCodeBlock) doc.blocks.get(0);
		assertEquals(MdCodeBlock.KIND_FENCED, cb.kind);
		assertEquals("java", cb.infoString);
		assertEquals("int x = 1;", cb.content);
	}

	@Test
	public void indentedCodeBlock() {
		MdDocument doc = parser.parse("    code\n    more");
		MdCodeBlock cb = (MdCodeBlock) doc.blocks.get(0);
		assertEquals(MdCodeBlock.KIND_INDENTED, cb.kind);
		assertEquals("code\nmore", cb.content);
	}

	@Test
	public void table() {
		MdDocument doc = parser.parse("| a | b |\n| :--- | ---: |\n| 1 | 2 |\n| 3 | 4 |");
		MdTable t = (MdTable) doc.blocks.get(0);
		assertEquals(2, t.header.size());
		assertEquals("a", ((MdText) t.header.get(0).get(0)).text);
		assertEquals("b", ((MdText) t.header.get(1).get(0)).text);
		assertEquals(MdTable.ALIGN_LEFT, t.alignments[0]);
		assertEquals(MdTable.ALIGN_RIGHT, t.alignments[1]);
		assertEquals(2, t.body.size());
		assertEquals("1", ((MdText) t.body.get(0).get(0).get(0)).text);
		assertEquals("4", ((MdText) t.body.get(1).get(1).get(0)).text);
	}

	@Test
	public void tableCenterAlignAndInlines() {
		MdDocument doc = parser.parse("| h |\n| :---: |\n| **x** |");
		MdTable t = (MdTable) doc.blocks.get(0);
		assertEquals(MdTable.ALIGN_CENTER, t.alignments[0]);
		assertTrue(t.body.get(0).get(0).get(0) instanceof MdStrong);
	}

	@Test
	public void tableEscapedPipe() {
		MdDocument doc = parser.parse("| a | b |\n| --- | --- |\n| x \\| y | z |");
		MdTable t = (MdTable) doc.blocks.get(0);
		assertEquals("x | y", ((MdText) t.body.get(0).get(0).get(0)).text);
	}

	@Test
	public void tableInvalidAlignFallsBackToParagraph() {
		MdDocument doc = parser.parse("| a | b |\n| not | align |");
		assertTrue(doc.blocks.get(0) instanceof MdParagraph);
	}

	@Test
	public void mixedDocument() {
		String src = "# Title\n\nIntro **bold**.\n\n- item1\n- item2\n\n```\ncode\n```\n";
		MdDocument doc = parser.parse(src);
		assertTrue(doc.blocks.get(0) instanceof MdHeading);
	}
}
