package me.chan.te.parser;

import android.support.annotation.NonNull;

import me.chan.te.config.Option;
import me.chan.te.text.Document;
import me.chan.te.text.Paragraph;
import me.chan.te.hypher.Hypher;
import me.chan.te.measurer.Measurer;

import static me.chan.te.parser.PlainTextParserUtils.findNewline;
import static me.chan.te.parser.PlainTextParserUtils.skipBlank;

public class TextParser implements Parser {
	@Override
	@NonNull
	public Document parse(CharSequence charSequence, Measurer measurer, Hypher hypher, Option option) {
		Document document = Document.obtain();
		Paragraph.Builder builder = new Paragraph.Builder(measurer, hypher, option);
		int len = charSequence.length();
		for (int i = skipBlank(charSequence, 0, len); i < len; ) {
			int last = findNewline(charSequence, i, len);
			if (i != last) {
				builder.newParagraph();
				PlainTextParserUtils.parse(charSequence, i, last, builder);
				document.addParagraph(builder.build());
			}
			i = skipBlank(charSequence, last, len);
		}
		return document;
	}
}
