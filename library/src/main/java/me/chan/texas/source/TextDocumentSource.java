package me.chan.texas.source;

import androidx.annotation.Nullable;

import me.chan.texas.TexasOption;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.text.Document;
import me.chan.texas.text.Paragraph;

public class TextDocumentSource extends TexasView.DocumentSource {
	private final CharSequence mText;

	@Paragraph.TypesetPolicy
	private final int mTypesetPolicy;

	public TextDocumentSource(CharSequence text) {
		this(text, Paragraph.TYPESET_POLICY_DEFAULT);
	}

	public TextDocumentSource(CharSequence text, int typesetPolicy) {
		mTypesetPolicy = typesetPolicy;
		mText = text;
	}

	@Override
	protected Document onRead(TexasOption option, @Nullable Document previousDocument) {
		Document.Builder build = new Document.Builder();
		int len = mText.length();
		for (int i = skipBlank(mText, 0, len); i < len; ) {
			int last = findNewline(mText, i, len);
			if (i != last) {
				Paragraph.Builder builder = Paragraph.Builder.newBuilder(option)
						.setTypesetPolicy(mTypesetPolicy);
				parse(mText, i, last, builder);
				build.addSegment(builder.build());
			}
			i = skipBlank(mText, last, len);
		}
		return build.build();
	}

	private static void parse(CharSequence paragraph, int start, int end, Paragraph.Builder builder) {
		for (int i = start; i < end; ) {
			int last = findWord(paragraph, i, end);
			int first = i;
			i = skipBlank(paragraph, last, end);
			if (first == last) {
				continue;
			}

			builder.text(paragraph, first, last);
		}
	}

	private static int findWord(CharSequence charSequence, int start, int end) {
		return skip(charSequence, start, end, true);
	}

	private static int skipBlank(CharSequence charSequence, int start, int end) {
		return skip(charSequence, start, end, false);
	}

	private static int findNewline(CharSequence charSequence, int start, int end) {
		for (; start < end; ++start) {
			char ch = charSequence.charAt(start);
			if (ch == '\n') {
				break;
			}
		}
		return start;
	}

	private static final char CN_SPACE = 0x3000;

	private static int skip(CharSequence charSequence, int start, int end, boolean untilBlank) {
		for (; start < end; ++start) {
			char ch = charSequence.charAt(start);
			boolean isBlank = ch == ' ' || ch == '\t' ||
					ch == '\r' || ch == '\n' || ch == CN_SPACE;
			if (isBlank == untilBlank) {
				break;
			}
		}
		return start;
	}
}
