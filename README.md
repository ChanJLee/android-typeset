# Texas

## 简介

Texas是一款支持图文混排的文本渲染库，目前支持两边对齐，并且单词支持断字，用户可以自由的自定义效果。

<img src="art/device-2019-11-01-171647.png" alt="显示效果" height="50%" width="50%">

## 支持的特性

### Android没有的特性
- 全平台的断字功能，比如triangle可以因为换行显示成tri-angle
- 和tex相同算法的两边对齐排版算法
- 自定义插入内容，你可以选择在渲染文章的时候插入视频、图片等任何你想要的视图

### 文字样式修改
比如：
- 颜色
- 大小
- 粗细
等等更多功能

## 段落样式修改
- 段落间距
- 段落标注，比如句子高亮

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
texasView.setSource(new TextDocumentSource("hello world"));
```

4. 页面销毁的时候，销毁渲染引擎，释放资源
```java
texasView.release();
```

## 高级内容

### 解析自定义数据

这里我们自定义了一个DocumentSource，重写onRead方法，这个方法提供了两个参数，第一个参数用来创建paragraph，另外一个参数是告诉你之前的document是什么，这个主要用来增量更新。
```java
public class BookSource extends TexasView.DocumentSource {

	@Override
	protected Document onRead(TexasOption option, @Nullable Document previousDocument) {
		try {
			XmlPullParser xmlPullParser = Xml.newPullParser();
			xmlPullParser.setInput(new InputStreamReader(mContext.getResources().getAssets().open(mBook)));
			return parse(xmlPullParser, option);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private Document parse(XmlPullParser parser, TexasOption texasOption)
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
}

```

### 自定义显示内容

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

## 资源引用

[english-words](https://github.com/dwyl/english-words.git)

[the book and the sword](https://www.520txtba.com/Txt/XiaoShuo-146678.html)

[async profiler](https://github.com/jvm-profiling-tools/async-profiler)

[JHyphenator](https://github.com/mfietz/JHyphenator)
