package me.chan.texas;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Toast;

import me.chan.texas.adapter.ParseException;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.text.Appearance;
import me.chan.texas.text.RectGround;
import me.chan.texas.renderer.RendererContext;
import me.chan.texas.text.Emoticon;
import me.chan.texas.text.Figure;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.text.TextStyle;
import me.chan.texas.text.DotUnderLine;
import me.chan.texas.text.ViewSegment;
import me.chan.texas.text.tokenizer.Token;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class BookParser extends TexasView.Adapter<CharSequence> {
	private static final Pattern PATTERN = Pattern.compile("\\p{Z}+|\\t|\\r|\\n");

	private final android.content.Context mContext;
	private final float mFlagWidth;
	private final float mFlagHeight;
	private final TexasView mTexasView;

	public static class SpanTag {
		public String sentId;
		public String text;

		public SpanTag(String sentId, String text) {
			this.sentId = sentId;
			this.text = text;
		}
	}

	public static class FlagTag {
	}

	private final int mPolicy;

	public BookParser(android.content.Context context, TexasView texasView, int policy) {
		mContext = context;
		mPolicy = policy;
		Resources resources = context.getResources();
		mFlagWidth = resources.getDimension(me.chan.texas.debug.R.dimen.com_shanbay_lib_texas_flag_width);
		mFlagHeight = resources.getDimension(me.chan.texas.debug.R.dimen.com_shanbay_lib_texas_flag_height);
		// for test
		mTexasView = texasView;
	}

	@NonNull
	@Override
	public List<Segment> parse(@NonNull CharSequence charSequence, TexasOption texasOption) throws ParseException {
		XmlPullParser xmlPullParser = Xml.newPullParser();
		try {
			xmlPullParser.setInput(new StringReader((String) charSequence));
			return parse(xmlPullParser, texasOption);
		} catch (Throwable e) {
			throw new ParseException("parse document failed", e);
		}
	}

	private List<Segment> parse(XmlPullParser parser, TexasOption texasOption)
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
				return parseArticleContent(parser, texasOption);
			} else {
				skip(parser);
			}
		}
		return null;
	}

	private void setupUserDefineView(List<Segment> segments) {
		// 添加自定义的视图
		segments.add(new ViewSegment(me.chan.texas.debug.R.layout.test_header) {

			@Override
			protected void onRender(View view) {

			}
		});
	}

	private void setupLongWordUnitTest(List<Segment> segments, TexasOption texasOption) {
		// 用于测试超长单词
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
		Paragraph paragraph = builder.newSpanBuilder()
				.next("QWERTYUIOPASDFGHJKLZXCVBNM")
				.setTextStyle(new TextStyle() {
					@Override
					public void update(@NonNull TextPaint textPaint, @Nullable Object tag) {
						textPaint.setTextSize(120);
						textPaint.setFakeBoldText(true);
					}
				})
				.buildSpan()
				.build();
		segments.add(paragraph);
	}

	// 增量更新就是当前页共享一个实例
	// 因为文本引擎可能会渲染特别长的内容，因此会使用回收机制保证内存占用的稳定性
	// 当视图不可见时就会被回收
	// 增量更新就是不会参与页面内容的回收，都使用一个实例
	private void setupIncrementalUserDefineView(List<Segment> segments) {
		segments.add(new ViewSegment(me.chan.texas.debug.R.layout.test_layout, true) {
			@Override
			protected void onRender(View view) {
				if (view.getTag() != null) {
					Log.d("chan_debug", "渲染过了: " + this);
					return;
				}

				Log.d("chan_debug", "设置元素： " + this);
				view.findViewById(me.chan.texas.debug.R.id.finish).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Toast.makeText(mContext, "click me", Toast.LENGTH_SHORT).show();
						mTexasView.redraw();
					}
				});
				view.setTag("fuck");
			}
		});
		segments.add(new ViewSegment(me.chan.texas.debug.R.layout.test_layout2, true) {
			@Override
			protected void onRender(View view) {
				Log.d("chan_debug", "渲染隐含元素");
			}
		});
		segments.add(new ViewSegment(me.chan.texas.debug.R.layout.test_layout2, true) {
			@Override
			protected void onRender(View view) {
				Log.d("chan_debug", "渲染隐含元素2");
			}
		});
	}

	private List<Segment> parseArticleContent(XmlPullParser parser, TexasOption texasOption) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, null, "article_content");
		final String id = parser.getAttributeValue(null, "id");
		List<Segment> segments = new ArrayList<>();

		setupUserDefineView(segments);

		setupLongWordUnitTest(segments, texasOption);

		setupIncrementalUserDefineView(segments);

		while (parser.next() != XmlPullParser.END_TAG) {
			int eventType = parser.getEventType();
			if (eventType != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("para")) {
				parsePara(parser, segments, texasOption);
			} else {
				skip(parser);
			}
		}

		// 测试页面滚动
		segments.add(new ViewSegment(me.chan.texas.debug.R.layout.test_layout) {

			@Override
			protected void onRender(View view) {
				view.findViewById(me.chan.texas.debug.R.id.finish).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mTexasView.scrollToPosition(0);
					}
				});
			}
		});

		return segments;
	}

	private static final int STATE_NONE = 0;
	private static final int STATE_SENT = 1;
	private static final int STATE_IMG = 2;
	private static final int STATE_SUBTITLE = 3;

	private void parsePara(XmlPullParser parser, List<Segment> segments, TexasOption texasOption) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, null, "para");
		String id = parser.getAttributeValue(null, "id");

		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption, mPolicy);

		builder.tag(id);
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
				parseImage(parser, segments);
				lastState = STATE_IMG;
			} else if (TextUtils.equals("subtitle", name)) {
				parseSubtitle(parser, builder);
				lastState = STATE_SUBTITLE;
			} else {
				skip(parser);
			}
		}

		if (lastState == STATE_SENT) {
			final Drawable drawable = ContextCompat.getDrawable(mContext, me.chan.texas.debug.R.drawable.me_chan_te_flag);
			final Emoticon emoticon = Emoticon.obtain(drawable, mFlagWidth, mFlagHeight, new FlagTag(), null, null);
			builder.emoticon(emoticon);
		}

		Paragraph paragraph = builder.build();
		Log.d("BookParser", "para element count: " + paragraph.getElementCount());
		if (paragraph.getElementCount() > 0) {
			segments.add(paragraph);
			// todo support focus
//			if ("A9127P127029".equals(id)) {
//				document.setFocusSegment(paragraph);
//			}
		}
	}

	private void parseImage(XmlPullParser parser, List<Segment> segments) throws XmlPullParserException, IOException {

		String url = null;
		float width = -1;
		float height = -1;
		while (parser.next() != XmlPullParser.END_TAG) {
			int eventType = parser.getEventType();
			if (eventType != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();
			if (TextUtils.equals("url", name)) {
				url = safeNextText(parser);
				parser.require(XmlPullParser.END_TAG, null, "url");
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
		segments.add(figure);
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
		final String id = parser.getAttributeValue(null, "id");
		String text = safeNextText(parser);
		if (!TextUtils.isEmpty(text)) {
			parseParagraph(builder, text, id);
		}
		parser.require(XmlPullParser.END_TAG, null, "sent");
	}

	// 这里给了个demo显示带圆角的背景
	// 注意这只是demo代码，因此质量不可控
	private void parseParagraph(Paragraph.Builder builder, String paragraph, String sentId) {
		builder.stream(paragraph, 0, paragraph.length(), (token) -> {
			Paragraph.Span span = Paragraph.Span.obtain(token)
					.setForeground(RED_UL);
			if (token.getCategory() == Token.CATEGORY_NORMAL) {
				span.tag(new SpanTag(sentId, token.getCharSequence().subSequence(token.getStart(), token.getEnd()).toString()));
			}

			if ("A9127P126990S210411".equals(sentId)) {
				span.setBackground(new RectGround(0xffC09453));
			} else if ("A344173P2435118S1".equals(sentId)) {
				span.setBackground(new Appearance() {
					private Path mPath = new Path();
					private float[] mLeftRound = new float[]{
							20, 20,
							0, 0,
							0, 0,
							20, 20
					};
					private float[] mRightRound = new float[]{
							0, 0,
							20, 20,
							20, 20,
							0, 0
					};

					@Override
					public void draw(Canvas canvas, Paint paint, RectF inner, RectF outer, RendererContext context) {
						paint.setColor(Color.GREEN);

						// 独立的单元，左右都要有圆角
						if (!checkTagIsSelected(context.getPrevTag()) && !checkTagIsSelected(context.getNextTag())) {
							canvas.drawRoundRect(outer, 20, 20, paint);
							return;
						}

						// 前面没有单词
						if (checkTagIsSelected(context.getPrevTag())) {
							mPath.reset();
							mPath.addRoundRect(
									outer,
									mLeftRound,
									Path.Direction.CW
							);
							canvas.drawPath(mPath, paint);
						} else if (checkTagIsSelected(context.getNextTag())) {
							// 后面没有单词
							mPath.reset();
							mPath.addRoundRect(
									outer,
									mRightRound,
									Path.Direction.CW
							);
							canvas.drawPath(mPath, paint);
						} else {
							// 夹在中间
							canvas.drawRect(outer, paint);
						}
					}

					private boolean checkTagIsSelected(Object tag) {
						// 你这里改成自己的逻辑
						return true;
					}
				});
			}
			return span;
		});
	}

	private static final DotUnderLine RED_UL = new DotUnderLine(Color.RED);

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
