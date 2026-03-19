package me.chan.texas;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Path;

import me.chan.texas.debug.R;
import me.chan.texas.ext.image.Figure;
import me.chan.texas.ext.image.ImageLoader;
import me.chan.texas.ext.markdown.math.MathSpan;
import me.chan.texas.ext.markdown.math.ast.MathList;
import me.chan.texas.ext.markdown.math.ast.MathParseException;
import me.chan.texas.ext.markdown.math.ast.MathParser;
import me.chan.texas.ext.markdown.math.renderer.MathRendererInflater;
import me.chan.texas.ext.markdown.math.renderer.RendererNode;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.view.MathView;
import me.chan.texas.misc.Rect;
import me.chan.texas.misc.RectF;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import me.chan.texas.renderer.ParagraphPredicates;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.renderer.ui.decor.ParagraphDecor;
import me.chan.texas.text.Appearance;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Document;
import me.chan.texas.text.RectGround;
import me.chan.texas.renderer.RendererContext;
import me.chan.texas.text.Emoticon;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextGravity;
import me.chan.texas.text.TextStyle;
import me.chan.texas.text.DotUnderLine;
import me.chan.texas.text.ViewSegment;
import me.chan.texas.text.layout.Span;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.layout.TextSpan;
import me.chan.texas.text.tokenizer.Token;
import me.chan.texas.utils.CharStream;
import me.chan.texas.utils.TexasUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class BookSource extends TexasView.DocumentSource {
	private static final Pattern PATTERN = Pattern.compile("\\p{Z}+|\\t|\\r|\\n");

	private final android.content.Context mContext;
	private final float mFlagWidth;
	private final float mFlagHeight;
	private final TexasView mTexasView;
	private final ImageLoader mImageLoader;

	public static class SpanTag {
		public String sentId;
		public String text;
		public boolean isWord;

		public SpanTag(String sentId, String text, boolean isWord) {
			this.sentId = sentId;
			this.text = text;
			this.isWord = isWord;
		}
	}

	public static class FlagTag {
	}

	private final int mPolicy;

	private final String mBook;

	public BookSource(android.content.Context context, TexasView texasView, int policy, String book) {
		mContext = context;
		mPolicy = policy;
		Resources resources = context.getResources();
		mFlagWidth = resources.getDimension(me.chan.texas.debug.R.dimen.texas_flag_width);
		mFlagHeight = resources.getDimension(me.chan.texas.debug.R.dimen.texas_flag_height);
		// for test
		mTexasView = texasView;
		mBook = book;
		mImageLoader = new ImageLoader(context);
	}

	@Override
	protected Document onRead(TexasOption option, @Nullable Document previousDocument) {
		try {
			XmlPullParser xmlPullParser = Xml.newPullParser();
			xmlPullParser.setInput(new InputStreamReader(mContext.getResources().getAssets().open(mBook)));
			mSeq = 0;
			return parse(xmlPullParser, option);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private Document parse(XmlPullParser parser, TexasOption texasOption)
			throws IOException, XmlPullParserException, MathParseException {
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

	private void setupUserDefineView(Document.Builder document) {
		// 添加自定义的视图
		document.addSegment(new ViewSegment(me.chan.texas.debug.R.layout.test_header) {

			@Override
			protected void onRender(View view) {

			}
		});
	}

	private void setupLongWordUnitTest(Document.Builder document, TexasOption texasOption) {
		// 用于测试超长单词
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
		Paragraph paragraph = builder.newSpanBuilder()
				.next("QWERTYUIOPASDFGHJKLZXCVBNM")
				.setTextStyle(new TextStyle() {
					@Override
					public void update(@NonNull TexasPaint textPaint, TextSpan span) {
						textPaint.setTextSize(120);
						textPaint.setFakeBoldText(true);
					}
				})
				.buildSpan()
				.build();
		document.addSegment(paragraph);
	}

	// 增量更新就是当前页共享一个实例
	// 因为文本引擎可能会渲染特别长的内容，因此会使用回收机制保证内存占用的稳定性
	// 当视图不可见时就会被回收
	// 增量更新就是不会参与页面内容的回收，都使用一个实例
	private void setupIncrementalUserDefineView(Document.Builder builder) {
		builder.addSegment(new ViewSegment(me.chan.texas.debug.R.layout.test_layout, true) {
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
		builder.addSegment(new ViewSegment(me.chan.texas.debug.R.layout.test_layout2, true) {
			@Override
			protected void onRender(View view) {
				Log.d("chan_debug", "渲染隐含元素");
			}
		});
		builder.addSegment(new ViewSegment(me.chan.texas.debug.R.layout.test_layout2, true) {
			@Override
			protected void onRender(View view) {
				Log.d("chan_debug", "渲染隐含元素2");
			}
		});
	}

	private Document parseArticleContent(XmlPullParser parser, TexasOption texasOption) throws IOException, XmlPullParserException, MathParseException {
		parser.require(XmlPullParser.START_TAG, null, "article_content");
		final String id = parser.getAttributeValue(null, "id");

		Document.Builder builder = new Document.Builder();
		setupUserDefineView(builder);

		setupLongWordUnitTest(builder, texasOption);

		setupIncrementalUserDefineView(builder);

		while (parser.next() != XmlPullParser.END_TAG) {
			int eventType = parser.getEventType();
			if (eventType != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("para")) {
				parsePara(parser, builder, texasOption);
			} else if (name.equals("math-block")) {
				parseMathBlock(parser, builder, texasOption);
				Paragraph.Builder b = Paragraph.Builder.newBuilder(texasOption);
				parseParagraph(b, "But since the grown-ups were not able to understand it, I made another drawing: I drew the inside of a boa constrictor, so that the grown-ups could see it clearly.", "xxxxxx");

				builder.addSegment(new ParallelViewSegment(b.build(), "简单的标注"));
			} else {
				skip(parser);
			}
		}

		// 测试页面滚动
		builder.addSegment(new ViewSegment(me.chan.texas.debug.R.layout.test_layout) {

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

		return builder.build();
	}

	private void parseMathBlock(XmlPullParser parser, Document.Builder builder, TexasOption texasOption) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, null, "math-block");
		String math = safeNextText(parser);
		builder.addSegment(new MathViewSegment(math));
		parser.require(XmlPullParser.END_TAG, null, "math-block");
	}

	private static class MathViewSegment extends ViewSegment {
		private final String mFormula;

		public MathViewSegment(String math) {
			super(me.chan.texas.debug.R.layout.item_math);
			mFormula = math;
		}

		@Override
		protected void onRender(View view) {
			MathView mathView = view.findViewById(R.id.math);
			mathView.render(mFormula);
		}
	}

	private static final int STATE_NONE = 0;
	private static final int STATE_SENT = 1;
	private static final int STATE_IMG = 2;
	private static final int STATE_SUBTITLE = 3;
	private static final int STATE_MATH = 4;

	// FOR TEST
	private int mSeq = 0;

	private void parsePara(XmlPullParser parser, Document.Builder documentBuilder, TexasOption texasOption) throws IOException, XmlPullParserException, MathParseException {
		parser.require(XmlPullParser.START_TAG, null, "para");
		String id = parser.getAttributeValue(null, "id");

		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption, mPolicy);
		builder.tag(id)
				.text((mSeq++) + ". ");
		int lastState = STATE_NONE;

		if (TextUtils.equals("A9127P127023", id)) {
			builder.breakStrategy(BreakStrategy.SIMPLE)
					.textGravity(TextGravity.END);
		} else if (TextUtils.equals("A9127P127029", id)) {
			builder.breakStrategy(BreakStrategy.SIMPLE)
					.textGravity(TextGravity.CENTER_HORIZONTAL);
		} else if (TextUtils.equals("A9127P127035", id)) {
			builder.breakStrategy(BreakStrategy.SIMPLE)
					.textGravity(TextGravity.START);
		}

		if (TextUtils.equals("A9127P127008", id)) {
			setupSidebar(builder);
		}

		String firstSent = null;
		while (parser.next() != XmlPullParser.END_TAG) {
			int eventType = parser.getEventType();
			if (eventType != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();
			if (TextUtils.equals("sent", name)) {
				String sent = parseSent(parser, builder);
				lastState = STATE_SENT;
				if (firstSent == null) {
					firstSent = sent;
				}
			} else if (TextUtils.equals("img", name)) {
				parseImage(parser, documentBuilder);
				lastState = STATE_IMG;
			} else if (TextUtils.equals("subtitle", name)) {
				parseSubtitle(parser, builder);
				lastState = STATE_SUBTITLE;
			} else if (TexasUtils.equals("math-inline", name)) {
				parseMathInline(parser, builder, texasOption);
				lastState = STATE_MATH;
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
			documentBuilder.addSegment(paragraph);
		}

		if (firstSent != null) {
			documentBuilder.addSegment(new RecycleableViewSegment(firstSent));
			documentBuilder.addSegment(new NotRecycleableViewSegment());
		}
	}

	private void parseImage(XmlPullParser parser, Document.Builder builder) throws XmlPullParserException, IOException {
		String url = null;
		while (parser.next() != XmlPullParser.END_TAG) {
			int eventType = parser.getEventType();
			if (eventType != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();
			if (TextUtils.equals("url", name)) {
				url = safeNextText(parser);
				parser.require(XmlPullParser.END_TAG, null, "url");
			} else {
				skip(parser);
			}
		}

		if (url == null) {
			return;
		}

		builder.addSegment(new Figure(mImageLoader.uri(url)));
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

	private void parseMathInline(XmlPullParser parser, Paragraph.Builder builder, TexasOption texasOption) throws XmlPullParserException, IOException, MathParseException {
		parser.require(XmlPullParser.START_TAG, null, "math-inline");
		String formula = safeNextText(parser);
		if (!TextUtils.isEmpty(formula)) {
			parseMathInline(formula, builder, texasOption);
		}
		parser.require(XmlPullParser.END_TAG, null, "math-inline");
	}

	private void parseMathInline(String formula, Paragraph.Builder builder, TexasOption texasOption) throws MathParseException {
		MathParser mathParser = new MathParser(new CharStream(formula));
		MathList list = mathParser.parse();

		MathPaint paint = MathView.create(Texas.getAppContext());
		MathRendererInflater inflater = new MathRendererInflater();
		RendererNode rendererNode = inflater.inflate(new MathPaint.Styles(paint), list);
		builder.hyperSpan(new MathSpan(rendererNode, paint));
	}

	private String parseSent(XmlPullParser parser, Paragraph.Builder builder) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, null, "sent");
		final String id = parser.getAttributeValue(null, "id");
		String text = safeNextText(parser);
		if (!TextUtils.isEmpty(text)) {
			parseParagraph(builder, text, id);
		}
		parser.require(XmlPullParser.END_TAG, null, "sent");
		return text;
	}

	// 这里给了个demo显示带圆角的背景
	// 注意这只是demo代码，因此质量不可控
	private void parseParagraph(Paragraph.Builder builder, String paragraph, String sentId) {
		builder.stream(paragraph, 0, paragraph.length(), (token) -> {
			Paragraph.SpanStyles span = Paragraph.SpanStyles.obtain(token)
					.setForeground(RED_UL)
					.tag(new SpanTag(sentId,
							token.getCharSequence().subSequence(token.getStart(), token.getEnd()).toString(),
							token.getCategory() == Token.CATEGORY_NORMAL));

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
					public void draw(TexasCanvas canvas, TexasPaint paint, RectF inner, RectF outer, RendererContext context) {
						paint.setColor(Color.GREEN);

						// 独立的单元，左右都要有圆角
						Span span = context.getSpan();
						if (span.isIsolate(true) && span.isIsolate(false)) {
							canvas.drawRoundRect(outer.left, outer.top, outer.right, outer.bottom, 20, 20, paint);
							return;
						}

						// 前面没有单词
						if (span.isIsolate(false)) {
							mPath.reset();
							mPath.addRoundRect(
									outer.left, outer.top, outer.right, outer.bottom,
									mLeftRound,
									Path.Direction.CW
							);
							canvas.drawPath(mPath, paint);
						} else if (span.isIsolate(true)) {
							// 后面没有单词
							mPath.reset();
							mPath.addRoundRect(
									outer.left, outer.top, outer.right, outer.bottom,
									mRightRound,
									Path.Direction.CW
							);
							canvas.drawPath(mPath, paint);
						} else {
							// 夹在中间
							canvas.drawRect(outer.left, outer.top, outer.right, outer.bottom, paint);
						}
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


	/**
	 * 安装side bar控件
	 */
	private void setupSidebar(Paragraph.Builder builder) {
		// side bar 的渲染和 普通view一样 都会经历 layout 的过程
		// 不过文本引擎里有点特别
		// 会先 onPreDrawDecor，用户可以在这个接口里做一些清理工作
		// 因为 ParagraphDecor 是全局共享的，每绘制一个 Paragraph 都会调用
		// 调用完 然后经历 onLayoutDecor 让你去准备这一段落的 渲染信息，比如你探测到这段
		// 有句子id是 A9127P126972S210390，我便在这个句子旁边画个🔥
		// 最后会 onDrawDecor 让你去绘制🔥
		final Drawable fireDrawable = ContextCompat.getDrawable(mContext, me.chan.texas.debug.R.drawable.fire);
		ParagraphDecor paragraphDecor = new ParagraphDecor() {
			private boolean mClicked = false;
			private boolean mDraw = false;
			private final android.graphics.Rect mDest = new android.graphics.Rect();

			@Override
			protected void onLayout(Paragraph paragraph, Rect decorOuter, Rect decorInner) {
				Layout layout = paragraph.getLayout();
				for (int l = 0; l < layout.getLineCount(); ++l) {
					Line line = layout.getLine(l);
					for (int b = 0; b < line.getSpanCount(); ++b) {
						Span span = line.getSpan(b);
						Object spanTag = span.getTag();
						if (!(spanTag instanceof BookSource.SpanTag)) {
							continue;
						}

						BookSource.SpanTag tag = (BookSource.SpanTag) spanTag;
						if (!"A9127P127008S210441".equals(tag.sentId)) {
							continue;
						}

						RectF spanOuter = span.getOuterBounds();
						mDraw = true;
						mDest.set(decorOuter.right - 20, (int) spanOuter.bottom - 40, decorOuter.right + 20, (int) spanOuter.bottom);
						return;
					}
				}
			}

			@Override
			protected void onDraw(TexasCanvas canvas, TexasPaint paint, Paragraph paragraph, Rect decorOuter, Rect decorInner) {
				if (!mDraw) {
					return;
				}

				fireDrawable.setBounds(mDest);

				// 选中了就变色
				fireDrawable.setTint(mClicked ? Color.RED : Color.GRAY);
				canvas.draw(fireDrawable);
			}

			@Override
			protected boolean onTouchEvent(MotionEvent event, Paragraph paragraph, Rect decorOuter, Rect decorInner) {
				// 需要根据 onCollectDecorRenderInfo 缓存区域去判断事件点击
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					float x = event.getX();
					float y = event.getY();
					if (mDest.top - 10 < y && mDest.bottom + 10 > y &&
							mDest.left - 10 < x && mDest.right + 10 > x) {
						mClicked = true;
						return true;
					}
					return false;
				} else if (action == MotionEvent.ACTION_UP) {
					mTexasView.selectParagraphs(new ParagraphPredicates() {
						@Override
						public boolean acceptSpan(@NonNull Span span) {
							Object spanTag = span.getTag();
							if (!(spanTag instanceof BookSource.SpanTag)) {
								return false;
							}

							BookSource.SpanTag tag = (BookSource.SpanTag) spanTag;
							return "A9127P127008S210441".equals(tag.sentId);
						}

						@Override
						public boolean acceptParagraph(@NonNull Paragraph paragraph) {
							return true;
						}
					});
				}
				return true;
			}
		};
		builder.decor(paragraphDecor);
	}
}
