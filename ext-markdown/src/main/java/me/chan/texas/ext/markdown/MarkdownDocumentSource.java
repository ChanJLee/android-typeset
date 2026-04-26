package me.chan.texas.ext.markdown;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import me.chan.texas.TexasOption;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.text.Document;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextStyle;

public class MarkdownDocumentSource extends TexasView.DocumentSource {
	private final CharSequence mMarkdown;
	private final MarkdownParser mParser;

	public MarkdownDocumentSource(@NonNull CharSequence markdown) {
		this(markdown, new MarkdownParser());
	}

	public MarkdownDocumentSource(@NonNull CharSequence markdown, @NonNull MarkdownParser parser) {
		mMarkdown = markdown;
		mParser = parser;
	}

	@Override
	protected Document onRead(TexasOption option, @Nullable Document previousDocument) {
		MarkdownDocument markdownDocument = mParser.parse(mMarkdown);
		Document.Builder documentBuilder = new Document.Builder();
		appendBlocks(documentBuilder, option, markdownDocument.getBlocks(), null);
		return documentBuilder.build();
	}

	private void appendBlocks(Document.Builder documentBuilder,
							  TexasOption option,
							  List<MarkdownDocument.Block> blocks,
							  @Nullable String prefix) {
		for (int i = 0; i < blocks.size(); ++i) {
			MarkdownDocument.Block block = blocks.get(i);
			switch (block.getType()) {
				case HEADING:
					appendHeading(documentBuilder, option, (MarkdownDocument.HeadingBlock) block, prefix);
					break;
				case PARAGRAPH:
					appendParagraph(documentBuilder, option,
							((MarkdownDocument.ParagraphBlock) block).getInlines(), prefix, TextStyle.NONE);
					break;
				case LIST:
					appendList(documentBuilder, option, (MarkdownDocument.ListBlock) block, prefix);
					break;
				case BLOCK_QUOTE:
					appendBlocks(documentBuilder, option,
							((MarkdownDocument.BlockQuoteBlock) block).getBlocks(), concatPrefix(prefix, "> "));
					break;
				case CODE_BLOCK:
					appendCodeBlock(documentBuilder, option, (MarkdownDocument.CodeBlock) block, prefix);
					break;
				case TABLE:
					appendTable(documentBuilder, option, (MarkdownDocument.TableBlock) block, prefix);
					break;
				case THEMATIC_BREAK:
					appendPlainParagraph(documentBuilder, option, concatPrefix(prefix, "----------"));
					break;
				default:
					break;
			}
		}
	}

	private void appendHeading(Document.Builder documentBuilder,
							   TexasOption option,
							   MarkdownDocument.HeadingBlock heading,
							   @Nullable String prefix) {
		String headingPrefix = repeat("#", heading.getLevel()) + " ";
		appendParagraph(documentBuilder, option, heading.getInlines(),
				concatPrefix(prefix, headingPrefix), TextStyle.BOLD);
	}

	private void appendParagraph(Document.Builder documentBuilder,
								 TexasOption option,
								 List<MarkdownDocument.Inline> inlines,
								 @Nullable String prefix,
								 TextStyle baseStyle) {
		Paragraph.Builder paragraphBuilder = Paragraph.Builder.newBuilder(option);
		if (prefix != null && prefix.length() > 0) {
			paragraphBuilder.text(prefix);
		}
		appendInlines(paragraphBuilder, inlines, baseStyle, null);
		documentBuilder.addSegment(paragraphBuilder.build());
	}

	private void appendPlainParagraph(Document.Builder documentBuilder, TexasOption option, String text) {
		Paragraph.Builder paragraphBuilder = Paragraph.Builder.newBuilder(option);
		paragraphBuilder.text(text);
		documentBuilder.addSegment(paragraphBuilder.build());
	}

	private void appendCodeBlock(Document.Builder documentBuilder,
								 TexasOption option,
								 MarkdownDocument.CodeBlock block,
								 @Nullable String prefix) {
		Paragraph.Builder paragraphBuilder = Paragraph.Builder.newBuilder(option)
				.addTypesetPolicy(Paragraph.TYPESET_POLICY_ACCEPT_CONTROL_CHAR);
		if (prefix != null && prefix.length() > 0) {
			paragraphBuilder.text(prefix);
		}
		paragraphBuilder.newSpanBuilder()
				.next(block.getCode())
				.tag(new CodeTag(block.getInfoString(), true))
				.buildSpan();
		documentBuilder.addSegment(paragraphBuilder.build());
	}

	private void appendList(Document.Builder documentBuilder,
							TexasOption option,
							MarkdownDocument.ListBlock list,
							@Nullable String prefix) {
		List<MarkdownDocument.ListItem> items = list.getItems();
		for (int i = 0; i < items.size(); ++i) {
			String marker;
			if (list.getListType() == MarkdownDocument.ListType.ORDERED) {
				marker = (list.getStartNumber() + i) + ". ";
			} else {
				marker = "- ";
			}
			appendBlocks(documentBuilder, option, items.get(i).getBlocks(), concatPrefix(prefix, marker));
		}
	}

	private void appendTable(Document.Builder documentBuilder,
							 TexasOption option,
							 MarkdownDocument.TableBlock table,
							 @Nullable String prefix) {
		appendTableRow(documentBuilder, option, table.getHeader(), prefix);
		List<MarkdownDocument.TableRow> rows = table.getRows();
		for (int i = 0; i < rows.size(); ++i) {
			appendTableRow(documentBuilder, option, rows.get(i), prefix);
		}
	}

	private void appendTableRow(Document.Builder documentBuilder,
								TexasOption option,
								MarkdownDocument.TableRow row,
								@Nullable String prefix) {
		Paragraph.Builder paragraphBuilder = Paragraph.Builder.newBuilder(option);
		if (prefix != null && prefix.length() > 0) {
			paragraphBuilder.text(prefix);
		}
		List<List<MarkdownDocument.Inline>> cells = row.getCells();
		for (int i = 0; i < cells.size(); ++i) {
			if (i > 0) {
				paragraphBuilder.text(" | ");
			}
			appendInlines(paragraphBuilder, cells.get(i), TextStyle.NONE, null);
		}
		documentBuilder.addSegment(paragraphBuilder.build());
	}

	private void appendInlines(Paragraph.Builder paragraphBuilder,
							   List<MarkdownDocument.Inline> inlines,
							   TextStyle baseStyle,
							   @Nullable Object tag) {
		for (int i = 0; i < inlines.size(); ++i) {
			MarkdownDocument.Inline inline = inlines.get(i);
			switch (inline.getType()) {
				case TEXT:
					appendStyledText(paragraphBuilder, ((MarkdownDocument.TextInline) inline).getText(), baseStyle, tag);
					break;
				case EMPHASIS:
					appendInlines(paragraphBuilder, ((MarkdownDocument.StyledInline) inline).getChildren(),
							mergeStyle(baseStyle, TextStyle.ITALIC), tag);
					break;
				case STRONG:
					appendInlines(paragraphBuilder, ((MarkdownDocument.StyledInline) inline).getChildren(),
							mergeStyle(baseStyle, TextStyle.BOLD), tag);
					break;
				case INLINE_CODE:
					appendStyledText(paragraphBuilder, ((MarkdownDocument.CodeInline) inline).getCode(),
							baseStyle, new CodeTag("", false));
					break;
				case LINK:
				case AUTOLINK:
					MarkdownDocument.LinkInline link = (MarkdownDocument.LinkInline) inline;
					appendInlines(paragraphBuilder, link.getLabel(), baseStyle,
							new LinkTag(link.getUrl(), link.getTitle()));
					break;
				case IMAGE:
					MarkdownDocument.LinkInline image = (MarkdownDocument.LinkInline) inline;
					String label = plainText(image.getLabel());
					appendStyledText(paragraphBuilder, label.length() == 0 ? image.getUrl() : label,
							baseStyle, new ImageTag(image.getUrl(), image.getTitle()));
					break;
				default:
					break;
			}
		}
	}

	private void appendStyledText(Paragraph.Builder paragraphBuilder,
								  String text,
								  TextStyle style,
								  @Nullable Object tag) {
		if (text.length() == 0) {
			return;
		}
		String normalized = text.replace('\n', ' ');
		if (style == TextStyle.NONE && tag == null) {
			paragraphBuilder.text(normalized);
			return;
		}
		Paragraph.SpanBuilder spanBuilder = paragraphBuilder.newSpanBuilder()
				.next(normalized)
				.setTextStyle(style);
		if (tag != null) {
			spanBuilder.tag(tag);
		}
		spanBuilder.buildSpan();
	}

	private TextStyle mergeStyle(TextStyle baseStyle, TextStyle style) {
		if (baseStyle == TextStyle.BOLD && style == TextStyle.ITALIC) {
			return TextStyle.BOLD_ITALIC;
		}
		if (baseStyle == TextStyle.ITALIC && style == TextStyle.BOLD) {
			return TextStyle.BOLD_ITALIC;
		}
		if (baseStyle == TextStyle.BOLD_ITALIC) {
			return TextStyle.BOLD_ITALIC;
		}
		return style;
	}

	private String plainText(List<MarkdownDocument.Inline> inlines) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < inlines.size(); ++i) {
			MarkdownDocument.Inline inline = inlines.get(i);
			switch (inline.getType()) {
				case TEXT:
					builder.append(((MarkdownDocument.TextInline) inline).getText());
					break;
				case INLINE_CODE:
					builder.append(((MarkdownDocument.CodeInline) inline).getCode());
					break;
				case EMPHASIS:
				case STRONG:
					builder.append(plainText(((MarkdownDocument.StyledInline) inline).getChildren()));
					break;
				case LINK:
				case IMAGE:
				case AUTOLINK:
					builder.append(plainText(((MarkdownDocument.LinkInline) inline).getLabel()));
					break;
				default:
					break;
			}
		}
		return builder.toString();
	}

	private String concatPrefix(@Nullable String prefix, String value) {
		return prefix == null ? value : prefix + value;
	}

	private String repeat(String value, int count) {
		StringBuilder builder = new StringBuilder(value.length() * count);
		for (int i = 0; i < count; ++i) {
			builder.append(value);
		}
		return builder.toString();
	}

	public static final class LinkTag {
		public final String url;
		public final String title;

		LinkTag(String url, String title) {
			this.url = url;
			this.title = title;
		}
	}

	public static final class ImageTag {
		public final String url;
		public final String title;

		ImageTag(String url, String title) {
			this.url = url;
			this.title = title;
		}
	}

	public static final class CodeTag {
		public final String infoString;
		public final boolean block;

		CodeTag(String infoString, boolean block) {
			this.infoString = infoString;
			this.block = block;
		}
	}
}
