# Texas

## 简介

Texas是一款支持图文混排的文本渲染库，目前支持两边对齐，并且单词支持断字，用户可以自由的自定义效果。

<img src="art/device-2019-11-01-171647.png" alt="显示效果" height="50%" width="50%">

## 支持的特性

1. 字体颜色
2. 字体
3. 字体大小
4. 行间距
5. 首行缩进
6. 选中字体背景色
7. 选中字体颜色
8. 片段间间距
9. 断字策略
10. 是否可选中
11. 自定义解析
12. 资源复用，内存/cpu高度优化

## 实例代码

1. 在application中初始化

```java
public class MyApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		Texas.init(this);
	}
}
```

2. 在layout中引用自定义view

```xml
<com.shanbay.lib.texas.renderer.TexasView
    android:id="@+id/text"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_margin="10dp" />
```

3. 渲染文本

```java
TextAdapter adapter = new TextAdapter();
texasView.setAdapter(adapter);
adapter.setSource(new AssetsTextSource(content, "sample.txt"));
```

4. 页面销毁的时候，销毁渲染引擎，释放资源
```java
texasView.release()
```

## 高级内容

1. 设置Texas内容读取源

```java
void setData(Object o)
void setSource(final Source<?> source)
```

Source读取数据并返回，因为Source是泛型的，所以你可以返回任何你想要的数据结构，比如对象，或者直接是二进制内容。

2. Adapter

adapter负责将Source读取的内容识别为Texas引擎识别的内容。可以理解为adapter，具体的信息见后面自定义显示内容一节。

3. 设置文本样式

通过调用 texas.createRendererOption()获取当前的渲染选项。通过设置RendererOption内容来设置文本属性。
目前支持修改字体，修改字号，行间距等。在修改完属性之后，通过调用

```java
void refresh(RenderOption renderOption)
```

进行刷新。详见API

## 自定义显示内容

在texas内部，文本引擎处理的最小单元叫Segment，一个个Segment由上而下排列组成了渲染内容。拿文章举例，我们可以设置文章Title为一个Segment，
顶部的插图为一个Segment，正文由很多段落Segment组成。每个Segment都是占满文本引擎窗口TexasView的宽度进行显示。最终用户看到的，便是由上而下
排列的文章。

Segment可以自定义自己想要的内容。

Segment的接口如下：

```java
/**
 * 渲染的最小单元
 */
public class Segment extends DefaultRecyclable {

	/**
	 * @param segmentSpace segment的垂直距离 {@link RenderOption#getSegmentSpace()} {@link RenderOption#setSegmentSpace(float)}
	 * @return 距离上一个segment的距离
	 */
	public float getTopMargin(float segmentSpace) {
		return 0;
	}

	/**
	 * @param segmentSpace segment的垂直距离 {@link RenderOption#getSegmentSpace()} {@link RenderOption#setSegmentSpace(float)}
	 * @return 距离下一个segment的距离
	 */
	public float getBottomMargin(float segmentSpace) {
		return segmentSpace;
	}
}
```

因为目前文本引擎是由上而下列表式的渲染，所以提供了两个函数用来决定它离附近元素的距离。
目前texas提供了几个内置Segment

1. Paragraph 用来显示文本，文本可以进行高亮，两边对齐等操作

2. Figure 用来显示插图，该Segment优化了图片显示，减少了因图片渲染导致的界面抖动。

3. ViewSegment 用来显示用户自定义内容。

一般我们自定义的都是view视图，所以可以直接继承ViewSegment

```java
new ViewSegment(R.layout.test_header) {

	@Override
	protected void onRender(View View) {
		/* do nothing */
	}

	@Override
	public float getTopMargin(float segmentSpace) {
		return 0;
	}

	@Override
	public float getBottomMargin(float segmentSpace) {
		return 0;
	}
}
```

ViewSegment构造函数接受一个布局layout, onRender会在每次需要渲染的时候调用。

## 自定义Adapter

以BookAdapter为例，这是一个接受xml文本为输入类型，然后转化成Document的适配器。

```java
public class BookParser extends TexasView.Adapter<CharSequence> {
	@NonNull
	@Override
	public Document parse(@NonNull CharSequence charSequence, TexasOption option) throws ParseException {
		XmlPullParser xmlPullParser = Xml.newPullParser();
		try {
			xmlPullParser.setInput(new StringReader((String) charSequence));
			return parse(xmlPullParser, option);
		} catch (Throwable e) {
			throw new ParseException("parse document failed", e);
		}
	}
}
```

每个adapter都需要重载parse。第一个参数charSequence即为输入的xml文本。我们调用系统库的XmlPullParser进行解析。

我们深入parse函数进行查看

```java

public class BookParser {
    private Document parse(XmlPullParser parser, TexasOption option)
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
    				return parseArticleContent(parser, option);
    			} else {
    				skip(parser);
    			}
    		}
    		return Document.createEmptyDocument();
    }
}
```

数据是包含在article_content这个标签下的，并且我们调用parseArticleContent去进行解析，如果没有article_content这个标签就返回一个空文档。

```java
public class BookParser {
    private Document parseArticleContent(XmlPullParser parser, TexasOption option) throws IOException, XmlPullParserException {
    		parser.require(XmlPullParser.START_TAG, null, "article_content");
    		final String id = parser.getAttributeValue(null, "id");
    		Document document = Document.obtain();

    		while (parser.next() != XmlPullParser.END_TAG) {
    			int eventType = parser.getEventType();
    			if (eventType != XmlPullParser.START_TAG) {
    				continue;
    			}
    			String name = parser.getName();
    			if (name.equals("para")) {
    				parsePara(parser, document, option);
    			} else {
    				skip(parser);
    			}
    		}

            // 添加一个自定义按钮
    		document.addSegment(new ViewSegment(R.layout.your_layout) {
    			// do something
    		});

    		return document;
    }
}
```

每一篇文章末尾都要加一个完成按钮，所以我们自定义了ViewSegment去显示这个按钮。每个article_content标签内，有很多个
para这个标签，代表的是每一段的内容，我们解析para就得到一段段的文本。

```java
public class BookParser {
    private void parsePara(XmlPullParser parser, Document document, TexasOption option) throws IOException, XmlPullParserException {
    		parser.require(XmlPullParser.START_TAG, null, "para");
    		String id = parser.getAttributeValue(null, "id");

    		Paragraph.Builder builder = Paragraph.Builder.newBuilder(option);
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
    			} else {
    				skip(parser);
    			}
    		}

    		if (lastState == STATE_SENT) {
    			final Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.me_chan_te_flag);
    			final Emoticon emoticon = Emoticon.obtain(drawable, mFlagWidth, mFlagHeight);
    			emoticon.setOnClickedListener(new OnClickedListener() {
    				@Override
    				public void onClicked(float x, float y) {
    					Log.d("BookParser", "click image");
    					if (mListener != null) {
    						mListener.onDrawableClicked(emoticon);
    					}
    				}
    			});
    			builder.emoticon(emoticon);
    		}

    		Paragraph paragraph = builder.build();
    		if (paragraph.getElementCount() > 0) {
    			document.addSegment(paragraph);
    			if ("A9127P127029".equals(id)) {
    				document.setFocusSegment(paragraph);
    			}
    		}
    }
}
```

这里就比较复杂了。我们解析para的内容，如果发现img标签就调用parseImage去解析图片。如果发现sent就去解析一个个句子。如果不能识别，就跳过。
注意，如果解析完para发现最后一个元素是句子，还在要末尾插上一个颜表情Emoticon，这里是一个小旗子。另外，如果发现当前的段落id是A9127P127029。
我们还要将当前paragraph识别为focus segment（document.setFocusSegment），他的作用是，渲染整个文本引擎会直接跳到当前segment进行显示。

我们先看解析图片，这比较简单

```java
public class BookParser {
    private void parseImage(XmlPullParser parser, Document document) throws XmlPullParserException, IOException {

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
    		document.addSegment(figure);
    }
}
```

图片即为我们的Figure。figure需要可以提供高宽，以便文本引擎提前preload，这样渲染的时候，不会因为图片加载的比较慢，而导致整个布局有一个突然拉伸的效果。
优化用户体验。

再看解析句子，这个比较复杂，已经到了文本渲染的核心。

```java
public class BookParser {

    private void parseSent(XmlPullParser parser, Paragraph.Builder builder) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, null, "sent");
		final String id = parser.getAttributeValue(null, "id");
		OnClickedListener sentOnClickedListener = new OnClickedListener() {
			@Override
			public void onClicked(float x, float y) {
				Log.d("BookParser", "select sent: " + id);
			}
		};
		String text = safeNextText(parser);
		if (!TextUtils.isEmpty(text)) {
			parseParagraph(builder, text, sentOnClickedListener);
		}
		parser.require(XmlPullParser.END_TAG, null, "sent");
	}
}
```

我们先创建了sentOnClickedListener，用来通知一个句子被点击。然后解析句子内容text。我们需要把text分解成一个个单词流。

```java
public class BookParser {
    private void parseParagraph(Paragraph.Builder builder, String paragraph, OnClickedListener spanListener, String sentId) {
    		String[] strings = PATTERN.split(paragraph);
    		for (int i = 0; strings != null && i < strings.length; ++i) {
    			final String text = strings[i];
    			if (TextUtils.isEmpty(text)) {
    				continue;
    			}

    			builder.newSpanBuilder(spanListener)
    					.next(text)
    					.tag(sentId)
    					.setForeground(UnderLine.obtain(Color.RED))
    					.setOnClickedListener(onClickedListener)
    					.buildSpan();
    		}
    }
}
```

span使用newSpanBuilder创建，你可以理解为span就是一个单词组，当长按的时候会触发span的spanListener回调。

## 文档

[算法描述](doc/algorithm/tex-algorithm.md)

[开发规范](doc/code/dev.md)

[架构设计](doc/arch/arch.md)

[API](doc/api/api.md)

[配套工具](doc/tools/tools.md)

## 中文排版

1.1 引号
我国国家标准要求弯引号，个人建议使用直角引号。
示例：你竟然喜欢「苹果表」？

引号中再用引号使用双直角引号。
示例：我问他，「你竟然喜欢『苹果表』？」

当引号表示讽刺、反语暗示时，使用弯引号（用法参考「西文排版」部分）。
示例：说真的，我也很 “喜欢”「苹果表」哦。

1.2 省略号（删节号）与破折号
省略号占两个汉字空间，包含六个点。
正确示例：中国设计还有太长路要走……
错误示例：中国设计还有太长路要走…

破折号占两个汉字空间。
示例：中国设计还有太长路要走──加油罢。

1.3 行首行尾禁则
点号（顿号、逗号、句号等）、结束引号、结束括号等，不能出现在一行的开头。
错误示例：
排版时注意某些
符号不能在行首
，别弄错了。

正确示例：
排版时注意某些
符号不能在行首，
别弄错了。

开始引号、开始括号、开始双书名号等，不能出现在一行的结尾。
错误示例：
她对我们说：「
这书太赞了。」

正确示例：
她对我们说：
「这书太赞了。」



## 西文排版基础


2.1 西文撰写基础
句首字母大写。
单词间留空格。
示例：Have a question?

2.2 西文标点相关
点号后加一个空格（如逗号、句号等）。
示例：Hello everyone! Welcome to my blog.

符号前不加空格的：度的标志、百分号等。
示例：17°, 100%

符号后不加空格的：货币标志、表正负数符号等。
示例：$10, -23

符号后加空格：「at」标志（电子邮件除外）、版权标识、项目符号等。
示例：@ Hindy, ?0?8 Hindy

括号、引号前后加空格，中间内容无空格。
示例 1：5.04 ounces (143 grams)
示例 2：Did you say “I love that”?

连字符（-）将两个相关单词组合成一个单词。
示例：Multi?6?2Touch, Jean-Jacques Rousseau

全角连接号（—）常表示文章中断、转折或说明。
示例：So not only will you see what a press can do — you’ll feel it.

2.3 斜体的用法
用来强调文中某个词或某句话。
用来标记外来语以及读者不习惯的单词。
文中出现的书名、剧名、美术作品的题目等等。

2.4 大小写的区别
专有名词使用特定大小写。
示例 1：the white house 是白色房子，the White House 则是美国白宫。
示例 2：I like iPhone and iMac.
示例 2 错误示范：l like iphone and IMAC.

标题可单用大写字母来排。
示例：JUST DO IT.



## 中西文混排

3.1 基础原则
中英文之间需要加空格。
示例：iOS 是个不错的操作系统。

中文与数字之间需要加空格。
示例：已经到了 12 月了啊。

中文与链接之间增加空格。
示例：我觉得 知乎 这个网站很赞。

专有名词使用特定大小写。
示例：我刚买了台 iPhone 6s。

使用正确的缩写。
正确示例：UI 设计师应该学点 HTML5。

错误示例：UI 设计师应该学点 H5。

3.2 标点相关
使用全角标点。
示例：苹果公司（Apple Inc.）真有钱……

全角标点与英文或数字之间不加空格。
正确示例：我刚买了台 iPhone，好开心！
错误示例：我刚买了台 iPhone ，好开心！

遇到完整的英文句子使用半角标点。
示例：《阿甘正传》的「You never know what you’re gonna get.」这句台词令我印象最深。

转自：http://zhuanlan.zhihu.com/p/20506092

## 资源引用

[english-words](https://github.com/dwyl/english-words.git)

[the book and the sword](https://www.520txtba.com/Txt/XiaoShuo-146678.html)

[async profiler](https://github.com/jvm-profiling-tools/async-profiler)

[JHyphenator](https://github.com/mfietz/JHyphenator)
