package me.chan.texas.adapter;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import java.util.ArrayList;
import java.util.List;

import me.chan.texas.TexasOption;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.text.Document;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;

/**
 * 最简单的文本解析器
 */
@RestrictTo(LIBRARY)
public class TextAdapter extends TexasView.Adapter<CharSequence> {

	@Paragraph.TypesetPolicy
	private final int mTypesetPolicy;

	public TextAdapter() {
		this(Paragraph.TYPESET_POLICY_DEFAULT);
	}

	public TextAdapter(int typesetPolicy) {
		mTypesetPolicy = typesetPolicy;
	}

	@Override
	@NonNull
	protected Document parse(CharSequence charSequence, TexasOption texasOption) {
		Document document = Document.obtain();
		int len = charSequence.length();
		for (int i = skipBlank(charSequence, 0, len); i < len; ) {
			int last = findNewline(charSequence, i, len);
			if (i != last) {
				Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption, mTypesetPolicy);
				parse(charSequence, i, last, builder);
				document.addSegment(builder.build());
			}
			i = skipBlank(charSequence, last, len);
		}
		return document;
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
