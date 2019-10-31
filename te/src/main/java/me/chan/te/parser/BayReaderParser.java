package me.chan.te.parser;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;

import me.chan.te.config.Option;
import me.chan.te.text.Document;
import me.chan.te.text.Paragraph;
import me.chan.te.hypher.Hypher;
import me.chan.te.measurer.Measurer;

public class BayReaderParser implements Parser {

	@NonNull
	@Override
	public Document parse(@NonNull CharSequence charSequence, Measurer measurer, Hypher hypher, Option option) throws ParseException {
		XmlPullParser xmlPullParser = Xml.newPullParser();
		try {
			xmlPullParser.setInput(new StringReader((String) charSequence));
			return parse(xmlPullParser, measurer, hypher, option);
		} catch (Throwable e) {
			throw new ParseException("parse document failed", e);
		}
	}

	private Document parse(XmlPullParser parser, Measurer measurer, Hypher hypher, Option option)
			throws IOException, XmlPullParserException {
		while (parser.next() != XmlPullParser.END_TAG) {
			int eventType = parser.getEventType();
			if (eventType == XmlPullParser.END_DOCUMENT) {
				break;
			} else if (eventType != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (TextUtils.equals("article_content", name)) {
				return parseArticleContent(parser, measurer, hypher, option);
			} else {
				skip(parser);
			}
		}
		return Document.EMPTY;
	}

	private Document parseArticleContent(XmlPullParser parser, Measurer measurer, Hypher hypher, Option option) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, null, "article_content");
		String id = parser.getAttributeValue(null, "id");
		Document document = Document.obtain(id);

		Paragraph.Builder builder = new Paragraph.Builder(measurer, hypher, option);

		while (parser.next() != XmlPullParser.END_TAG) {
			int eventType = parser.getEventType();
			if (eventType != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("para")) {
				document.addParagraph(parsePara(parser, builder));
			} else {
				skip(parser);
			}
		}
		return document;
	}

	private Paragraph parsePara(XmlPullParser parser, Paragraph.Builder builder) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, null, "para");
		String id = parser.getAttributeValue(null, "id");
		builder.newParagraph(id);

		while (parser.next() != XmlPullParser.END_TAG) {
			int eventType = parser.getEventType();
			if (eventType != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();
			if (TextUtils.equals("sent", name)) {
				parseSent(parser, builder);
			} else if (TextUtils.equals("img", name)) {
				parseImage(parser, builder);
			} else if (TextUtils.equals("subtitle", name)) {
				parseSubtitle(parser, builder);
			} else {
				skip(parser);
			}
		}

		return builder.build();
	}

	private void parseImage(XmlPullParser parser, Paragraph.Builder builder) throws XmlPullParserException, IOException {
		while (parser.next() != XmlPullParser.END_TAG) {
			int eventType = parser.getEventType();
			if (eventType != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();
			if (name.equals("url")) {
				// TODO
				safeNextText(parser);
//				img.setUrl(safeNextText(parser));
				parser.require(XmlPullParser.END_TAG, null, "url");
			} else if (name.equals("desc")) {
				// TODO
				safeNextText(parser);
//				img.setDesc(String.valueOf(safeNextText(parser)));
				parser.require(XmlPullParser.END_TAG, null, "desc");
			} else {
				skip(parser);
			}
		}
	}

	private void parseSubtitle(XmlPullParser parser, Paragraph.Builder builder) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, null, "subtitle");
		String id = parser.getAttributeValue(null, "id");
		while (parser.next() != XmlPullParser.END_TAG) {
			int eventType = parser.getEventType();
			if (eventType != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			safeNextText(parser);
//			subtitle.setHeadingTimes(name);
//			subtitle.setSubtitle(safeNextText(parser));
		}
		parser.require(XmlPullParser.END_TAG, null, "subtitle");
	}

	private void parseSent(XmlPullParser parser, Paragraph.Builder builder) throws IOException, XmlPullParserException {
		// TODO add id
		parser.require(XmlPullParser.START_TAG, null, "sent");
		String id = parser.getAttributeValue(null, "id");
		String text = safeNextText(parser) + " ";
		PlainTextParserUtils.parse(text, 0, text.length(), builder, id);
		parser.require(XmlPullParser.END_TAG, null, "sent");
	}

	private String safeNextText(XmlPullParser parser) throws XmlPullParserException, IOException {
		String result = parser.nextText();
		if (parser.getEventType() != XmlPullParser.END_TAG) {
			parser.nextTag();
		}
		return result;
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}

		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
				case XmlPullParser.END_TAG:
					depth--;
					break;
				case XmlPullParser.START_TAG:
					depth++;
					break;
			}
		}
	}
}
