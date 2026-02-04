# Texas

一款支持动态更新、Tag 标识系统的高性能文本渲染库

## 简介

Texas 是一款支持图文混排的文本渲染库，目前支持两边对齐，并且单词支持断字，用户可以自由的自定义效果。

**最大特点：** 通过 Tag 系统精确标识每个文本片段，支持高效的动态内容更新，非常适合需要复杂交互的文本场景。

<img src="art/device-2019-11-01-171647.png" alt="显示效果" height="50%" width="50%">

## 目录

- [核心优势](#核心优势)
  - [动态内容更新](#-动态内容更新)
  - [Tag 标识系统](#️-强大的-tag-标识系统)
- [快速开始](#快速开始)
- [Tag 系统详解](#tag-系统详解)
- [动态内容更新](#动态内容更新-1)
- [API 详细使用](#api-详细使用)
- [自定义数据源完整示例](#自定义数据源完整示例)
- [Segment 详解](#segment-详解)

## 核心优势

### 🚀 动态内容更新

Texas 最大的优势在于**支持动态更新文字内容**，无需重新构建整个文档，只需增量更新即可。这使得它非常适合：

- 实时内容编辑器
- 聊天消息流
- 动态加载的长文本（如小说阅读器）
- 需要频繁更新部分内容的场景

### 🏷️ 强大的 Tag 标识系统

Texas 提供了两级 Tag 系统，让你可以精确标识和操作文本的任意部分：

#### Paragraph Tag - 段落级标识
```java
// 为整个段落设置唯一标识
builder.tag("paragraph_001");
```

#### Span Tag - 文本片段级标识
```java
// 为每个单词或字符设置标识
span.tag(new SpanTag(sentenceId, wordContent, isNormalWord));
```

**Tag 系统的应用场景：**
- ✅ 精确定位和高亮文本（如搜索结果高亮）
- ✅ 识别用户点击的具体单词或句子
- ✅ 实现复杂的交互逻辑（如单词翻译、笔记标注）
- ✅ 追踪文本来源和元数据
- ✅ 实现协同编辑中的冲突检测

### 其他特性

#### Android 没有的特性
- 全平台的断字功能，比如 triangle 可以因为换行显示成 tri-angle
- 和 TeX 相同算法的两边对齐排版算法
- 自定义插入内容，你可以选择在渲染文章的时候插入视频、图片等任何你想要的视图

#### 文字样式修改
- 颜色
- 大小
- 粗细
- 字体
- 行间距

#### 段落样式修改
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

### 3. 设置数据源并渲染（带 Tag）

```java
TexasView texasView = findViewById(R.id.text);

texasView.setSource(new TexasView.DocumentSource() {
    @Override
    protected Document onRead(TexasOption option, @Nullable Document previousDocument) {
        Paragraph.Builder builder = Paragraph.Builder.newBuilder(option);
        
        // 设置段落 Tag
        builder.tag("paragraph_001");
        
        // 使用 stream API 自动分词并为每个单词设置 Tag
        String text = "Hello world, this is Texas!";
        builder.stream(text, 0, text.length(), (token) -> {
            return Paragraph.Span.obtain(token)
                    .tag(new WordTag(
                        token.getCharSequence()
                            .subSequence(token.getStart(), token.getEnd())
                            .toString()
                    ));
        });
        
        return new Document.Builder(null)
                .addSegment(builder.build())
                .build();
    }
});

// 定义简单的 Tag 类
class WordTag {
    public final String word;
    
    public WordTag(String word) {
        this.word = word;
    }
}
```

**简单用法（无 Tag）：**

```java
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

## Tag 系统详解

Tag 系统是 Texas 的核心特性，它让你能够为文本的每个部分附加标识和元数据，从而实现精确的内容定位和交互。

### 定义自定义 Tag 类

建议创建自己的 Tag 类来存储文本元数据：

```java
public class SpanTag {
    public final String sentenceId;  // 句子ID
    public final String wordContent; // 单词内容
    public final boolean isNormal;   // 是否为普通单词
    
    public SpanTag(String sentenceId, String wordContent, boolean isNormal) {
        this.sentenceId = sentenceId;
        this.wordContent = wordContent;
        this.isNormal = isNormal;
    }
}
```

### 设置 Paragraph Tag

```java
Paragraph.Builder builder = Paragraph.Builder.newBuilder(option);
// 为段落设置唯一标识
builder.tag("paragraph_A9127P127017");
```

### 设置 Span Tag - 方式一：使用 stream API

推荐使用 stream API，它会自动将文本分词，并为每个单词设置样式和 tag：

```java
builder.stream(text, 0, text.length(), (token) -> {
    // token 是词法引擎解析出的单词
    // 为每个单词创建 Span 并设置 tag
    return Paragraph.Span.obtain(token)
            .tag(new SpanTag(
                sentenceId,
                token.getCharSequence().subSequence(token.getStart(), token.getEnd()).toString(),
                token.getCategory() == Token.CATEGORY_NORMAL
            ))
            .setForeground(new DotUnderLine(Color.RED))  // 设置样式
            .setBackground(new RectGround(Color.YELLOW));
});
```

### 设置 Span Tag - 方式二：使用 SpanBuilder

```java
Paragraph.Builder builder = Paragraph.Builder.newBuilder(option);

builder.newSpanBuilder()
    .text("Hello")
    .tag(new SpanTag("sent_001", "Hello", true))
    .textColor(Color.BLUE)
    .buildSpan();

builder.newSpanBuilder()
    .text(" ")
    .buildSpan();

builder.newSpanBuilder()
    .text("World")
    .tag(new SpanTag("sent_001", "World", true))
    .textColor(Color.RED)
    .buildSpan();
```

### 通过 Tag 实现点击识别

```java
texasView.setSpanTouchEventHandler(new SpanTouchEventHandler() {
    @Override
    public boolean isSpanClickable(Object tag) {
        // 根据 tag 判断是否可点击
        return tag != null;
    }

    @Override
    public boolean applySpanClicked(@Nullable Object clickedTag, @Nullable Object otherTag) {
        // 单击时，高亮相同 tag 的所有 Span
        return clickedTag == otherTag;
    }

    @Override
    public boolean applySpanLongClicked(@Nullable Object clickedTag, @Nullable Object otherTag) {
        // 长按时，高亮同一句子的所有 Span
        if (clickedTag instanceof SpanTag && otherTag instanceof SpanTag) {
            SpanTag clicked = (SpanTag) clickedTag;
            SpanTag other = (SpanTag) otherTag;
            return clicked.sentenceId.equals(other.sentenceId);
        }
        return false;
    }
});
```

### 通过 Tag 实现精确高亮

```java
// 高亮特定句子
texasView.highlightParagraphs(new ParagraphPredicates() {
    @Override
    public boolean acceptSpan(@Nullable Object spanTag) {
        if (!(spanTag instanceof SpanTag)) {
            return false;
        }
        SpanTag tag = (SpanTag) spanTag;
        return "A9127P127017S210459".equals(tag.sentenceId);
    }

    @Override
    public boolean acceptParagraph(@Nullable Object paragraphTag) {
        return "A9127P127017".equals(paragraphTag);
    }
}, true, 0);
```

### 在点击事件中获取 Tag

```java
texasView.setOnClickedListener(new TexasView.OnClickedListener() {
    @Override
    public void onSpanClicked(TexasView view, TouchEvent event, Object tag) {
        if (tag instanceof SpanTag) {
            SpanTag spanTag = (SpanTag) tag;
            // 根据 tag 信息执行相应操作
            String word = spanTag.wordContent;
            String sentenceId = spanTag.sentenceId;
            
            // 例如：显示单词翻译
            showTranslation(word);
            
            // 例如：跳转到相关内容
            navigateToSentence(sentenceId);
        }
    }
    
    @Override
    public void onSegmentClicked(TexasView view, TouchEvent event, Object tag) {
        // 获取 Paragraph 的 tag
        String paragraphId = (String) tag;
    }
    
    // ... 其他回调
});
```

## 动态内容更新

Texas 支持高效的增量内容更新，无需重新渲染整个文档。

### 增量添加内容

```java
texasView.setSource(new TexasView.DocumentSource() {
    @Override
    protected Document onRead(TexasOption option, @Nullable Document previousDocument) {
        // 基于之前的 document 进行增量更新
        return new Document.Builder(previousDocument)
                .addSegment(
                    Paragraph.Builder.newBuilder(option)
                            .tag("new_paragraph_001")
                            .text("这是新添加的内容")
                            .build()
                )
                .build();
    }
});
```

### 完全替换内容

```java
texasView.setSource(new TexasView.DocumentSource() {
    @Override
    protected Document onRead(TexasOption option, @Nullable Document previousDocument) {
        // previousDocument 为 null，表示重新构建
        Document.Builder builder = new Document.Builder(null);
        
        // 添加多个段落
        for (int i = 0; i < paragraphs.size(); i++) {
            String text = paragraphs.get(i);
            builder.addSegment(
                Paragraph.Builder.newBuilder(option)
                        .tag("paragraph_" + i)
                        .stream(text, 0, text.length(), (token) -> {
                            return Paragraph.Span.obtain(token)
                                    .tag(new SpanTag("sent_" + i, token.toString(), true));
                        })
                        .build()
            );
        }
        
        return builder.build();
    }
});
```

### 动态更新实际应用场景

#### 场景一：聊天消息流

```java
// 收到新消息时增量添加
void onNewMessage(Message message) {
    texasView.setSource(new TexasView.DocumentSource() {
        @Override
        protected Document onRead(TexasOption option, @Nullable Document previousDocument) {
            return new Document.Builder(previousDocument)
                    .addSegment(createMessageParagraph(option, message))
                    .build();
        }
    });
}
```

#### 场景二：搜索结果高亮

```java
// 动态高亮搜索关键词
void highlightSearchResults(String keyword) {
    texasView.highlightParagraphs(new ParagraphPredicates() {
        @Override
        public boolean acceptSpan(@Nullable Object spanTag) {
            if (spanTag instanceof SpanTag) {
                SpanTag tag = (SpanTag) spanTag;
                return tag.wordContent.contains(keyword);
            }
            return false;
        }

        @Override
        public boolean acceptParagraph(@Nullable Object paragraphTag) {
            return true; // 接受所有段落
        }
    }, Selection.Styles.create(Color.YELLOW, Color.BLACK));
}
```

#### 场景三：实时编辑器

```java
// 文本变化时局部更新
void onTextChanged(int paragraphIndex, String newText) {
    texasView.setSource(new TexasView.DocumentSource() {
        @Override
        protected Document onRead(TexasOption option, @Nullable Document previousDocument) {
            // 只更新变化的段落
            Document.Builder builder = new Document.Builder(null);
            
            for (int i = 0; i < document.getSegmentCount(); i++) {
                if (i == paragraphIndex) {
                    // 重新构建变化的段落
                    builder.addSegment(createParagraph(option, newText, i));
                } else {
                    // 复用未变化的段落
                    builder.addSegment(document.getSegment(i));
                }
            }
            
            return builder.build();
        }
    });
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

## 自定义数据源完整示例

### 创建带 Tag 的自定义数据源

```java
public class BookSource extends TexasView.DocumentSource {
    
    private Context context;
    private String bookName;
    
    public BookSource(Context context, String bookName) {
        this.context = context;
        this.bookName = bookName;
    }

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
        Document.Builder documentBuilder = new Document.Builder(null);
        
        while (parser.next() != XmlPullParser.END_TAG) {
            int eventType = parser.getEventType();
            if (eventType == XmlPullParser.END_DOCUMENT) {
                break;
            } else if (eventType != XmlPullParser.START_TAG) {
                continue;
            }
            
            String name = parser.getName();
            if ("paragraph".equals(name)) {
                String paragraphId = parser.getAttributeValue(null, "id");
                String sentenceId = parser.getAttributeValue(null, "sentId");
                String text = parser.nextText();
                
                // 创建段落并设置 Tag
                Paragraph paragraph = createParagraphWithTag(
                    texasOption, 
                    text, 
                    paragraphId, 
                    sentenceId
                );
                documentBuilder.addSegment(paragraph);
            }
        }
        
        return documentBuilder.build();
    }
    
    private Paragraph createParagraphWithTag(
            TexasOption option, 
            String text, 
            String paragraphId, 
            String sentenceId) {
        
        Paragraph.Builder builder = Paragraph.Builder.newBuilder(option);
        
        // 设置段落 Tag
        builder.tag(paragraphId);
        
        // 使用 stream API 为每个单词设置 Tag
        builder.stream(text, 0, text.length(), (token) -> {
            return Paragraph.Span.obtain(token)
                    .tag(new SpanTag(
                        sentenceId,
                        token.getCharSequence()
                            .subSequence(token.getStart(), token.getEnd())
                            .toString(),
                        token.getCategory() == Token.CATEGORY_NORMAL
                    ))
                    .setForeground(new DotUnderLine(Color.RED));
        });
        
        return builder.build();
    }
    
    // 自定义 Span Tag 类
    public static class SpanTag {
        public final String sentId;
        public final String word;
        public final boolean isNormalWord;
        
        public SpanTag(String sentId, String word, boolean isNormalWord) {
            this.sentId = sentId;
            this.word = word;
            this.isNormalWord = isNormalWord;
        }
    }
}
```

### 使用自定义数据源

```java
// 初始化时加载
BookSource source = new BookSource(
    this, 
    texasView, 
    Paragraph.TYPESET_POLICY_CJK_MIX_OPTIMIZATION, 
    "book.xml"
);
texasView.setSource(source);
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

## 最佳实践

### Tag 系统使用建议

#### ✅ 推荐做法

1. **为 Tag 类创建明确的数据结构**
```java
public class SpanTag {
    public final String id;          // 唯一标识
    public final String content;     // 文本内容
    public final String type;        // 类型（如：word, punctuation）
    public final Map<String, Object> metadata; // 扩展元数据
}
```

2. **使用 stream API 处理文本**
```java
// 推荐：自动分词，高效处理
builder.stream(text, 0, text.length(), (token) -> {
    return Paragraph.Span.obtain(token).tag(createTag(token));
});
```

3. **Tag 保持唯一性和一致性**
```java
// 使用有意义的 ID 格式
String paragraphId = "article_" + articleId + "_paragraph_" + index;
String spanId = paragraphId + "_span_" + spanIndex;
```

4. **在 Tag 中存储必要的业务信息**
```java
public class SpanTag {
    public final String wordId;
    public final String translation;  // 翻译
    public final int difficulty;      // 难度等级
    public final boolean isBookmarked; // 是否收藏
}
```

#### ❌ 避免的做法

1. **不要在 Tag 中存储大量数据**
```java
// 错误：Tag 中存储大对象
class SpanTag {
    public Bitmap image; // ❌ 不要存储图片
    public List<String> largeList; // ❌ 不要存储大集合
}
```

2. **不要使用可变的 Tag 对象**
```java
// 错误：Tag 应该是不可变的
class SpanTag {
    public String id;
    public void setId(String id) { this.id = id; } // ❌
}

// 正确：使用 final 字段
class SpanTag {
    public final String id;
    public SpanTag(String id) { this.id = id; } // ✅
}
```

3. **不要在高频回调中进行重度计算**
```java
@Override
public boolean applySpanClicked(Object clickedTag, Object otherTag) {
    // ❌ 不要在这里进行数据库查询或网络请求
    // ✅ 应该只做简单的比较操作
    return clickedTag == otherTag;
}
```

### 动态更新性能优化

1. **增量更新优于全量更新**
```java
// 推荐：只更新变化的部分
Document.Builder(previousDocument).addSegment(newParagraph).build()

// 避免：频繁全量重建
Document.Builder(null).addAllSegments(allParagraphs).build()
```

2. **批量更新合并操作**
```java
// 推荐：批量添加多个段落
texasView.setSource(new TexasView.DocumentSource() {
    @Override
    protected Document onRead(TexasOption option, Document previousDocument) {
        Document.Builder builder = new Document.Builder(previousDocument);
        for (Paragraph p : newParagraphs) {
            builder.addSegment(p);
        }
        return builder.build();
    }
});
```

3. **使用对象池减少 GC 压力**
```java
// Texas 内部使用了对象池，Span 会自动回收
// 不需要手动管理，但要注意在使用完后不要继续引用
Paragraph.Span span = Paragraph.Span.obtain(token);
// 使用 span...
// 不要在回调外继续持有 span 引用
```

### 实际应用架构建议

#### 阅读器应用架构

```java
public class ReaderActivity extends AppCompatActivity {
    
    private TexasView texasView;
    private BookManager bookManager;
    
    // 1. 加载章节内容
    private void loadChapter(int chapterId) {
        Chapter chapter = bookManager.getChapter(chapterId);
        
        texasView.setSource(new TexasView.DocumentSource() {
            @Override
            protected Document onRead(TexasOption option, Document prev) {
                return createChapterDocument(option, chapter);
            }
        });
    }
    
    // 2. 使用 Tag 追踪段落位置
    private Document createChapterDocument(TexasOption option, Chapter chapter) {
        Document.Builder builder = new Document.Builder(null);
        
        for (int i = 0; i < chapter.paragraphs.size(); i++) {
            String text = chapter.paragraphs.get(i);
            Paragraph paragraph = createParagraphWithTag(
                option, 
                text, 
                chapter.id, 
                i
            );
            builder.addSegment(paragraph);
        }
        
        return builder.build();
    }
    
    // 3. 处理单词点击（如：查词）
    private void setupWordClickListener() {
        texasView.setOnClickedListener(new TexasView.OnClickedListener() {
            @Override
            public void onSpanClicked(TexasView view, TouchEvent event, Object tag) {
                if (tag instanceof WordTag) {
                    WordTag wordTag = (WordTag) tag;
                    showDictionary(wordTag.word);
                    
                    // 记录阅读进度
                    bookManager.saveProgress(wordTag.chapterId, wordTag.paragraphIndex);
                }
            }
        });
    }
    
    // 4. 实现搜索高亮
    private void searchAndHighlight(String keyword) {
        texasView.highlightParagraphs(new ParagraphPredicates() {
            @Override
            public boolean acceptSpan(@Nullable Object spanTag) {
                if (spanTag instanceof WordTag) {
                    return ((WordTag) spanTag).word.contains(keyword);
                }
                return false;
            }
            
            @Override
            public boolean acceptParagraph(@Nullable Object paragraphTag) {
                return true;
            }
        }, Selection.Styles.create(Color.YELLOW, Color.BLACK));
    }
}
```

## 常见问题

### Q: Tag 系统的性能开销如何？

A: Tag 系统的性能开销非常小，只是简单的对象引用。在 `SpanTouchEventHandler` 的回调中，需要确保比较操作足够快（通常是简单的对象比较或字符串比较）。

### Q: 如何在 Tag 中存储复杂的业务数据？

A: 建议在 Tag 中只存储 ID，通过 ID 从业务层获取完整数据：

```java
class SpanTag {
    public final String wordId; // 只存储 ID
}

// 点击时通过 ID 查询完整数据
void onSpanClicked(Object tag) {
    if (tag instanceof SpanTag) {
        String wordId = ((SpanTag) tag).wordId;
        WordData data = wordRepository.getById(wordId); // 从仓库获取数据
    }
}
```

### Q: 动态更新会导致滚动位置丢失吗？

A: Texas 会尽量保持滚动位置，但在大规模内容替换时可能需要手动保存和恢复位置：

```java
// 保存位置
int position = texasView.getCurrentPosition();

// 更新内容
texasView.setSource(newSource);

// 恢复位置
texasView.scrollToPosition(position);
```

### Q: 可以在运行时修改已渲染文本的 Tag 吗？

A: 不可以直接修改，需要重新设置数据源。但由于支持增量更新，性能影响很小。

## 资源引用

- [english-words](https://github.com/dwyl/english-words.git)
- [the book and the sword](https://www.520txtba.com/Txt/XiaoShuo-146678.html)
- [async profiler](https://github.com/jvm-profiling-tools/async-profiler)
- [JHyphenator](https://github.com/mfietz/JHyphenator)

## 总结

Texas 的核心价值在于：

1. **🏷️ Tag 系统**：为每个文本片段提供精确的标识，实现复杂的交互逻辑
2. **🚀 动态更新**：支持高效的增量内容更新，无需重新渲染整个文档
3. **🎨 灵活渲染**：支持自定义样式、TeX 级别的排版算法、图文混排

这使得 Texas 非常适合：
- 📖 阅读器应用（单词查询、笔记标注）
- 💬 聊天应用（消息流动态更新）
- ✏️ 文本编辑器（实时编辑、协同编辑）
- 🔍 搜索应用（关键词高亮）
- 📚 教育应用（单词学习、语法分析）
