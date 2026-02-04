# Texas

## 简介

Texas 是一款支持图文混排的文本渲染库，目前支持两边对齐，并且单词支持断字，用户可以自由的自定义效果。

<img src="art/device-2019-11-01-171647.png" alt="显示效果" height="50%" width="50%">

## 支持的特性

### Android 没有的特性
- 全平台的断字功能，比如 triangle 可以因为换行显示成 tri-angle
- 和 TeX 相同算法的两边对齐排版算法
- 自定义插入内容，你可以选择在渲染文章的时候插入视频、图片等任何你想要的视图

### 文字样式修改
- 颜色
- 大小
- 粗细
- 字体
- 行间距

### 段落样式修改
- 段落间距
- 段落装饰（Decoration）
- 段落高亮

## 快速开始

### 1. 初始化

在 Application 中初始化 Texas：

```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Texas.init(this);
    }
}
```

### 2. 在布局中添加 TexasView

```xml
<me.chan.texas.renderer.TexasView
    android:id="@+id/text"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_margin="10dp" />
```

### 3. 设置数据源并渲染

```java
TexasView texasView = findViewById(R.id.text);
texasView.setSource(new TextDocumentSource("hello world"));
```

### 4. 释放资源

在页面销毁时，释放渲染引擎资源：

```java
@Override
protected void onDestroy() {
    if (texasView != null) {
        texasView.release();
    }
    super.onDestroy();
}
```

## API 详细使用

### 基础配置

#### 设置渲染窗口的 Padding

```java
// 设置整个渲染窗口的 padding (left, top, right, bottom)
texasView.setRendererPadding(30, 10, 30, 10);
```

#### 设置 Segment 装饰

Segment 装饰用于设置每个 Segment（段落、图片等）周围的空白间距：

```java
final int paddingHorizontal = 20;
final int paddingVertical = 20;

texasView.setSegmentDecoration((index, count, segment, document, outRect) -> {
    if (segment instanceof Figure) {
        // 图片类型的 Segment
        outRect.set(0, index == 0 ? 0 : paddingVertical, 0, index == count - 1 ? 0 : paddingVertical);
    } else {
        // 文本类型的 Segment
        outRect.set(paddingHorizontal, index == 0 ? 0 : paddingVertical, paddingHorizontal, index == count - 1 ? 0 : paddingVertical);
    }
});
```

#### 设置滚动条样式

```java
Drawable drawable = ContextCompat.getDrawable(this, R.drawable.scrollbar_thumb_demo);
texasView.setScrollBarDrawable(drawable);

// 获取当前滚动条 Drawable
Drawable current = texasView.getScrollBarDrawable();
```

### 触摸事件处理

#### 设置 Span 触摸事件处理器

```java
texasView.setSpanTouchEventHandler(new SpanTouchEventHandler() {
    @Override
    public boolean isSpanClickable(Object tag) {
        // 判断该 Span 是否可点击
        return tag != null;
    }

    @Override
    public boolean applySpanClicked(@Nullable Object clickedTag, @Nullable Object otherTag) {
        // 单击谓词：判断单击时哪些单词要被高亮
        return clickedTag == otherTag;
    }

    @Override
    public boolean applySpanLongClicked(@Nullable Object clickedTag, @Nullable Object otherTag) {
        // 长按谓词：判断长按时哪些内容要被高亮
        if (otherTag == null) {
            return true;
        }
        
        if (clickedTag instanceof BookSource.SpanTag && otherTag instanceof BookSource.SpanTag) {
            BookSource.SpanTag lhs = (BookSource.SpanTag) clickedTag;
            BookSource.SpanTag rhs = (BookSource.SpanTag) otherTag;
            return TexasUtils.equals(lhs.sentId, rhs.sentId);
        }
        
        return false;
    }
});
```

#### 设置点击事件监听器

```java
texasView.setOnClickedListener(new TexasView.OnClickedListener() {
    @Override
    public void onSpanClicked(TexasView view, TouchEvent event, Object tag) {
        // Span 单击事件
        Toast.makeText(context, "点击了 Span", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSpanLongClicked(TexasView view, TouchEvent event, Object tag) {
        // Span 长按事件
        Toast.makeText(context, "长按了 Span", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSegmentClicked(TexasView view, TouchEvent event, Object tag) {
        // Segment 单击事件
        Toast.makeText(context, "点击了 Segment", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmptyClicked(TexasView view, TouchEvent event) {
        // 空白区域点击事件
        Toast.makeText(context, "点击了空白", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSegmentDoubleClicked(TexasView view, TouchEvent event, Object tag) {
        // Segment 双击事件
        Toast.makeText(context, "双击了 Segment", Toast.LENGTH_SHORT).show();
    }
});
```

#### 设置拖拽选择监听器

```java
texasView.setOnDragSelectListener(new TexasView.OnDragSelectListener() {
    @Override
    public void onDragStart(TexasView view, TouchEvent event) {
        // 开始拖拽选择
    }

    @Override
    public void onDragEnd(TexasView view, TouchEvent event) {
        // 结束拖拽选择
    }

    @Override
    public void onDragDismiss(TexasView view) {
        // 取消拖拽选择
    }
});
```

### 渲染选项配置

#### 创建和更新 RenderOption

```java
// 创建渲染选项
RenderOption renderOption = texasView.createRendererOption();

// 设置文本大小
renderOption.setTextSize(TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP, 18, 
    getResources().getDisplayMetrics()
));

// 设置文本颜色
renderOption.setTextColor(Color.BLACK);

// 设置行间距
renderOption.setLineSpacingExtra(200);

// 设置断字策略
renderOption.setBreakStrategy(BreakStrategy.BALANCED); // 或 BreakStrategy.SIMPLE

// 设置字体
Typeface typeface = Typeface.createFromAsset(getAssets(), "opposans_r.ttf");
renderOption.setTypeface(typeface);

// 设置调试模式
renderOption.setDebugEnable(true);

// 设置是否绘制表情符号选择
renderOption.setDrawEmoticonSelection(false);

// 应用渲染选项
texasView.refresh(renderOption);
```

### 高亮功能

#### 高亮段落（静态高亮）

```java
texasView.highlightParagraphs(new ParagraphPredicates() {
    @Override
    public boolean acceptSpan(@Nullable Object spanTag) {
        // 判断哪些 Span 需要高亮
        if (!(spanTag instanceof BookSource.SpanTag)) {
            return false;
        }
        BookSource.SpanTag tag = (BookSource.SpanTag) spanTag;
        return "A9127P127017S210459".equals(tag.sentId);
    }

    @Override
    public boolean acceptParagraph(@Nullable Object paragraphTag) {
        // 判断哪些段落需要高亮
        return "A9127P127017".equals(paragraphTag);
    }
}, true, 0);
```

#### 高亮段落（带动画）

```java
// 创建选择并设置样式
Selection selection = texasView.highlightParagraphs(new ParagraphPredicates() {
    @Override
    public boolean acceptSpan(@Nullable Object spanTag) {
        return true;
    }

    @Override
    public boolean acceptParagraph(@Nullable Object paragraphTag) {
        return "A9127P126972".equals(paragraphTag);
    }
}, Selection.Styles.create(Color.BLUE, Color.RED));

if (selection != null) {
    // 创建动画
    ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1f);
    valueAnimator.setDuration(3000);
    valueAnimator.setRepeatCount(3);
    
    selection.startAnimator(valueAnimator, new Selection.SelectionAnimatorListener() {
        @Override
        protected void onUpdate(ValueAnimator animation, Selection.Styles styles) {
            int backgroundColor = (int) styles.getBackgroundColor();
            int textColor = styles.getTextColor();
            float v = (float) animation.getAnimatedValue();
            
            // 更新透明度
            styles.setTextColor(Color.argb(
                (int) (255 * v), 
                Color.red(textColor), 
                Color.green(textColor), 
                Color.blue(textColor)
            ));
            styles.setBackgroundColor(Color.argb(
                (int) (255 * v), 
                Color.red(backgroundColor), 
                Color.green(backgroundColor), 
                Color.blue(backgroundColor)
            ));
        }

        @Override
        protected void onAnimationEnd(Animator animation, boolean isReverse, Selection.Styles styles) {
            // 动画结束后清除高亮
            selection.clear();
        }
    });
}
```

### 滚动控制

```java
// 滚动到指定位置
texasView.scrollToPosition(0);
```

### 其他功能

#### 重绘

```java
// 重新绘制视图
texasView.redraw();
```

#### 清理缓存

```java
// 清理 Texas 缓存
Texas.clean();
```

#### 生命周期管理

```java
@Override
protected void onDestroy() {
    if (texasView != null) {
        texasView.release();
    }
    super.onDestroy();
}
```

## 自定义数据源

### 解析自定义数据

你可以继承 `TexasView.DocumentSource` 来创建自定义数据源：

```java
public class BookSource extends TexasView.DocumentSource {

    @Override
    protected Document onRead(TexasOption option, @Nullable Document previousDocument) {
        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setInput(new InputStreamReader(
                context.getResources().getAssets().open(bookName)
            ));
            return parse(xmlPullParser, option);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private Document parse(XmlPullParser parser, TexasOption texasOption)
            throws IOException, XmlPullParserException {
        // 解析逻辑
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

### 增量更新内容

```java
texasView.setSource(new TexasView.DocumentSource() {
    @Override
    protected Document onRead(TexasOption option, @Nullable Document previousDocument) {
        // 使用 previousDocument 进行增量更新
        return new Document.Builder(previousDocument)
                .addSegment(
                    Paragraph.Builder.newBuilder(option)
                            .text("hello world")
                            .build()
                )
                .build();
    }
});
```

## Segment 详解

在 Texas 内部，文本引擎处理的最小单元叫 Segment，一个个 Segment 由上而下排列组成了渲染内容。拿文章举例，我们可以设置文章 Title 为一个 Segment，顶部的插图为一个 Segment，正文由很多段落 Segment 组成。每个 Segment 都是占满文本引擎窗口 TexasView 的宽度进行显示。

### Segment 接口

```java
/**
 * 渲染的最小单元
 */
public class Segment extends DefaultRecyclable {

    /**
     * @param segmentSpace segment 的垂直距离
     * @return 距离上一个 segment 的距离
     */
    public float getTopMargin(float segmentSpace) {
        return 0;
    }

    /**
     * @param segmentSpace segment 的垂直距离
     * @return 距离下一个 segment 的距离
     */
    public float getBottomMargin(float segmentSpace) {
        return segmentSpace;
    }
}
```

### 内置 Segment 类型

1. **Paragraph** - 用来显示文本，文本可以进行高亮、两边对齐等操作
2. **Figure** - 用来显示插图，该 Segment 优化了图片显示，减少了因图片渲染导致的界面抖动
3. **ViewSegment** - 用来显示用户自定义内容

### 自定义 ViewSegment

```java
new ViewSegment(R.layout.test_header) {

    @Override
    protected void onRender(View view) {
        // 在这里设置视图内容
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

## 资源引用

- [english-words](https://github.com/dwyl/english-words.git)
- [the book and the sword](https://www.520txtba.com/Txt/XiaoShuo-146678.html)
- [async profiler](https://github.com/jvm-profiling-tools/async-profiler)
- [JHyphenator](https://github.com/mfietz/JHyphenator)
