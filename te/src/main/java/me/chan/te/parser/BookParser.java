package me.chan.te.parser;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Pattern;

import me.chan.te.R;
import me.chan.te.hypher.Hypher;
import me.chan.te.measurer.Measurer;
import me.chan.te.text.Document;
import me.chan.te.text.Figure;
import me.chan.te.text.TextAttribute;
import me.chan.te.text.Paragraph;
import me.chan.te.text.UnderLine;

public class BookParser implements Parser<CharSequence> {
	private static final Pattern PATTERN = Pattern.compile("\\p{Z}+|\\t|\\r|\\n");

	private Context mContext;
	private float mFlagWidth;
	private float mFlagHeight;

	public BookParser(Context context) {
		mContext = context;
		Resources resources = context.getResources();
		mFlagWidth = resources.getDimension(R.dimen.me_chan_te_flag_width);
		mFlagHeight = resources.getDimension(R.dimen.me_chan_te_flag_height);
	}

	@NonNull
	@Override
	public Document parse(@NonNull CharSequence charSequence, Measurer measurer, Hypher hypher, TextAttribute textAttribute) throws ParseException {
		XmlPullParser xmlPullParser = Xml.newPullParser();
		try {
			xmlPullParser.setInput(new StringReader((String) charSequence));
			return parse(xmlPullParser, measurer, hypher, textAttribute);
		} catch (Throwable e) {
			throw new ParseException("parse document failed", e);
		}
	}

	private Document parse(XmlPullParser parser, Measurer measurer, Hypher hypher, TextAttribute textAttribute)
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
				return parseArticleContent(parser, measurer, hypher, textAttribute);
			} else {
				skip(parser);
			}
		}
		return Document.EMPTY;
	}

	private Document parseArticleContent(XmlPullParser parser, Measurer measurer, Hypher hypher, TextAttribute textAttribute) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, null, "article_content");
		String id = parser.getAttributeValue(null, "id");
		Document document = Document.obtain(id);

		while (parser.next() != XmlPullParser.END_TAG) {
			int eventType = parser.getEventType();
			if (eventType != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("para")) {
				parsePara(parser, document, measurer, hypher, textAttribute);
			} else {
				skip(parser);
			}
		}
		return document;
	}

	private static final int STATE_NONE = 0;
	private static final int STATE_SENT = 1;
	private static final int STATE_IMG = 2;
	private static final int STATE_SUBTITLE = 3;

	private void parsePara(XmlPullParser parser, Document document,
						   Measurer measurer, Hypher hypher, TextAttribute textAttribute) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, null, "para");
		String id = parser.getAttributeValue(null, "id");

		Paragraph.Builder builder = Paragraph.Builder.newBuilder(measurer, hypher, textAttribute, id);
		int lastState = STATE_NONE;

		while (parser.next() != XmlPullParser.END_TAG) {
			int eventType = parser.getEventType();
			if (eventType != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();
			if (TextUtils.equals("sent", name)) {
				parseSent(parser, builder);
				lastState = STATE_SENT;
			} else if (TextUtils.equals("img", name)) {
				parseImage(parser, document);
				lastState = STATE_IMG;
			} else if (TextUtils.equals("subtitle", name)) {
				parseSubtitle(parser, builder);
				lastState = STATE_SUBTITLE;
			} else {
				skip(parser);
			}
		}

		if (lastState == STATE_SENT) {
			builder.drawable(ContextCompat.getDrawable(mContext, R.drawable.me_chan_te_icon_no_note), mFlagWidth, mFlagHeight);
		}

		Paragraph paragraph = builder.build();
		if (paragraph.getElementCount() > 0) {
			document.addSegment(paragraph);
		}
	}

	private void parseImage(XmlPullParser parser, Document document) throws XmlPullParserException, IOException {

		String url = null;
		float width = -1;
		float height = -1;
		String description = null;
		while (parser.next() != XmlPullParser.END_TAG) {
			int eventType = parser.getEventType();
			if (eventType != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();
			if (TextUtils.equals("url", name)) {
				url = safeNextText(parser);
				parser.require(XmlPullParser.END_TAG, null, "url");
			} else if (TextUtils.equals("desc", name)) {
				description = safeNextText(parser);
				parser.require(XmlPullParser.END_TAG, null, "desc");
			} else if (TextUtils.equals("width", name)) {
				width = safeNextFloat(parser);
				parser.require(XmlPullParser.END_TAG, null, "width");
			} else if (TextUtils.equals("height", name)) {
				height = safeNextFloat(parser);
				parser.require(XmlPullParser.END_TAG, null, "height");
			} else {
				skip(parser);
			}
		}

		if (url == null) {
			return;
		}

		Figure figure = Figure.obtain(url, width, height);
		figure.setDescription(description);
		document.addSegment(figure);
	}

	private float safeNextFloat(XmlPullParser parser) throws IOException, XmlPullParserException {
		String value = safeNextText(parser);
		if (TextUtils.isEmpty(value)) {
			return -1;
		}

		try {
			return Float.parseFloat(value);
		} catch (Throwable throwable) {
			return -1;
		}
	}

	private void parseSubtitle(XmlPullParser parser, Paragraph.Builder builder) throws XmlPullParserException, IOException {
		// TODO add subtitle
		parser.require(XmlPullParser.START_TAG, null, "subtitle");
		String id = parser.getAttributeValue(null, "id");
		while (parser.next() != XmlPullParser.END_TAG) {
			int eventType = parser.getEventType();
			if (eventType != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			String title = safeNextText(parser);
		}
		parser.require(XmlPullParser.END_TAG, null, "subtitle");
	}

	private void parseSent(XmlPullParser parser, Paragraph.Builder builder) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, null, "sent");
		String id = parser.getAttributeValue(null, "id");
		String text = safeNextText(parser);
		if (!TextUtils.isEmpty(text)) {
			parseParagraph(builder, text, id);
		}
		parser.require(XmlPullParser.END_TAG, null, "sent");
	}

	private void parseParagraph(Paragraph.Builder builder, String paragraph, String id) {
		String[] strings = PATTERN.split(paragraph);
		for (int i = 0; strings != null && i < strings.length; ++i) {
			String text = strings[i];
			if (TextUtils.isEmpty(text)) {
				continue;
			}

			builder.newSpanBuilder(null)
					.next(text)
					.setForeground(UnderLine.obtain(Color.RED))
					.setExtra(id)
					.buildSpan();
		}
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
