package me.chan.te.parser;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import me.chan.te.config.Option;
import me.chan.te.data.Segment;
import me.chan.te.hypher.Hypher;
import me.chan.te.measurer.Measurer;

public class BayReaderParser implements Parser {

	@NonNull
	@Override
	public List<Segment> parse(@NonNull CharSequence charSequence, Measurer measurer, Hypher hypher, Option option) {
		List<Segment> segments = new LinkedList<>();
		XmlPullParser xmlPullParser = Xml.newPullParser();
		try {
			xmlPullParser.setInput(new StringReader((String) charSequence));
			parse(segments, xmlPullParser, measurer, hypher, option);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return segments;
	}

	private void parse(List<Segment> segments, XmlPullParser parser, Measurer measurer, Hypher hypher, Option option)
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
				parseArticleContent(segments, parser, measurer, hypher, option);
			} else {
				skip(parser);
			}
		}
	}

	private void parseArticleContent(List<Segment> segments, XmlPullParser parser, Measurer measurer, Hypher hypher, Option option) throws IOException, XmlPullParserException {
		// TODO add id
		parser.require(XmlPullParser.START_TAG, null, "article_content");
		String id = parser.getAttributeValue(null, "id");
		Segment.Builder builder = new Segment.Builder(measurer, hypher, option);

		while (parser.next() != XmlPullParser.END_TAG) {
			int eventType = parser.getEventType();
			if (eventType != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("para")) {
				builder.newSegment();
				parsePara(parser, builder);
				segments.add(builder.build());
			} else {
				skip(parser);
			}
		}
	}

	private void parsePara(XmlPullParser parser, Segment.Builder builder) throws IOException, XmlPullParserException {
		// TODO add id
		parser.require(XmlPullParser.START_TAG, null, "para");
		String id = parser.getAttributeValue(null, "id");

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
	}

	private void parseImage(XmlPullParser parser, Segment.Builder builder) throws XmlPullParserException, IOException {
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

	private void parseSubtitle(XmlPullParser parser, Segment.Builder builder) throws XmlPullParserException, IOException {
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

	private void parseSent(XmlPullParser parser, Segment.Builder builder) throws IOException, XmlPullParserException {
		// TODO add id
		parser.require(XmlPullParser.START_TAG, null, "sent");
		String id = parser.getAttributeValue(null, "id");

		String text = safeNextText(parser) + " ";
		PlainTextParserUtils.parse(text, 0, text.length(), builder);

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
