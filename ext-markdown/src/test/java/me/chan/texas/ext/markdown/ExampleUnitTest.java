package me.chan.texas.ext.markdown;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExampleUnitTest {
	@Test
	public void parseHeadingAndInlineStyles() {
		MarkdownDocument document = new MarkdownParser().parse("# Hello **Texas** and *Markdown*");

		assertEquals(1, document.getBlockCount());
		MarkdownDocument.HeadingBlock heading = (MarkdownDocument.HeadingBlock) document.getBlock(0);
		assertEquals(1, heading.getLevel());
		assertEquals(MarkdownDocument.InlineType.TEXT, heading.getInlines().get(0).getType());
		assertEquals(MarkdownDocument.InlineType.STRONG, heading.getInlines().get(1).getType());
		assertEquals(MarkdownDocument.InlineType.EMPHASIS, heading.getInlines().get(3).getType());
	}

	@Test
	public void parseListAndFencedCodeBlock() {
		MarkdownDocument document = new MarkdownParser().parse("- one\n- two\n\n```java\nint a = 1;\n```");

		assertEquals(2, document.getBlockCount());
		MarkdownDocument.ListBlock list = (MarkdownDocument.ListBlock) document.getBlock(0);
		assertEquals(MarkdownDocument.ListType.UNORDERED, list.getListType());
		assertEquals(2, list.getItems().size());

		MarkdownDocument.CodeBlock code = (MarkdownDocument.CodeBlock) document.getBlock(1);
		assertTrue(code.isFenced());
		assertEquals("java", code.getInfoString());
		assertEquals("int a = 1;", code.getCode());
	}

	@Test
	public void parseTableAndLinks() {
		MarkdownDocument document = new MarkdownParser().parse(
				"| Name | Url |\n" +
						"| :--- | ---: |\n" +
						"| Texas | [repo](https://example.com \"title\") |");

		assertEquals(1, document.getBlockCount());
		MarkdownDocument.TableBlock table = (MarkdownDocument.TableBlock) document.getBlock(0);
		assertEquals(MarkdownDocument.Alignment.LEFT, table.getAlignments().get(0));
		assertEquals(MarkdownDocument.Alignment.RIGHT, table.getAlignments().get(1));
		assertEquals(1, table.getRows().size());

		List<MarkdownDocument.Inline> linkCell = table.getRows().get(0).getCells().get(1);
		MarkdownDocument.LinkInline link = (MarkdownDocument.LinkInline) linkCell.get(0);
		assertEquals(MarkdownDocument.InlineType.LINK, link.getType());
		assertEquals("https://example.com", link.getUrl());
		assertEquals("title", link.getTitle());
	}
}