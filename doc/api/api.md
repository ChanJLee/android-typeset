# API

## 约定

1. 所有以 `@Hidden` 或 `@RestrictTo(LIBRARY)` 标注的 API 都是内部 API，随时可能变化，不要调用。
2. 不要长期持有任何 Texas 内部的数据结构（`Document`、`Paragraph`、`Span`、`Selection` 等），即用即走。引擎内部使用对象池，持有过期引用会导致数据错乱。
3. 拥有相同点击谓词的元素被视为同一组元素：多个 Span 如果在 `applySpanClicked` 中互相匹配，点击其中一个时整组都会被高亮。
4. 尽量避免多次调用 `TexasView.refresh()` / `setSource()`，把多个修改合并成一次批量提交。
5. 属性归属的判断标准：与业务无关的属性由库提供（如全局字体颜色，走 `RenderOption`）；业务语义的样式由业务提供（如"生词加红色点状下划线"，走 `SpanStyles.setForeground(new DotUnderLine(color))`）。
6. Tag 对象建议不可变（`final` 字段）。Tag 中只存业务 ID，不要存大对象（Bitmap、大集合）。

## 入口与生命周期

| API | 说明 |
|-----|------|
| `Texas.init(Application)` / `Texas.init(Application, MemoryOption)` | 初始化，Application 中调用一次 |
| `Texas.setDefaultTypeface(Typeface)` | 设置全局默认字体 |
| `Texas.setEnableTexCompat(boolean)` | TeX 排版兼容模式开关 |
| `Texas.setIssueCallback(IssueCallback)` | 内部异常上报回调 |
| `TexasView.release()` | 页面销毁时释放（`onDestroy` 中调用） |

## TexasView 常用 API

| 分类 | API |
|------|-----|
| 内容 | `setSource(DocumentSource)`、`getDocument()` |
| 配置 | `createRendererOption()`、`refresh(RenderOption)`、`setRendererPadding(l, t, r, b)`、`setSegmentDecoration(...)`、`setHasFixedSize(boolean)` |
| 交互 | `setOnClickedListener(...)`、`setSpanTouchEventHandler(...)`、`setOnDragSelectListener(...)`、`setOnScrollListener(...)`、`setRenderListener(...)` |
| 高亮 | `highlightParagraphs(...)`（多个重载 / `HighlightOptions`）、`clearHighlight()`、`getHighlight()` |
| 选中 | `selectParagraphs(...)`、`clearSelection()`、`getSelection()` |
| 滚动 | `scrollToPosition(position[, smooth[, offset]])`、`scrollToSegment(segment)`、`smoothScrollBy(dx, dy)`、`getCurrentPosition()`、`getFirstVisibleSegmentIndex(...)`、`getLastVisibleSegmentIndex(...)`、`getScrollState()` |
| 动画 | `setSegmentAnimator(SegmentAnimator)` |
| 滚动条 | `setScrollBarEnable(boolean)`、`setScrollBarDrawable(Drawable)` |
| 重绘 | `redraw()` |

## RenderOption 常用配置

全部支持链式调用，修改后通过 `texasView.refresh(option)` 生效。

| API | 说明 |
|-----|------|
| `setTextSize(float)` / `setTextSize(Context, unit, value)` | 字号 |
| `setTextColor(int)` | 文本颜色 |
| `setTypeface(Typeface)` | 字体 |
| `setLineSpacingExtra(float)` | 行间距 |
| `setBreakStrategy(BreakStrategy)` | 断行策略：`SIMPLE`（贪心）/ `BALANCED`（TeX 全局最优，两端对齐 + 断字） |
| `setHyphenStrategy(HyphenStrategy)` | 断字词典（美音 / 英音） |
| `setFullWithSymbolOptimizationEnable(boolean)` | 全角符号优化（中文标点挤压），默认开启 |
| `setTextGravity(int)` | 文本对齐方式 |
| `setBidiEnable(boolean)` | 双向文本（RTL）支持 |
| `setDragToSelectEnable(boolean)` / `setWordSelectable(boolean)` | 拖拽选择 / 词级选择开关 |
| `setSelectedBackgroundColor(int)`、`setSelectedTextColor(int)`、`setSelectedBackgroundRoundRadius(float)` | 选中样式 |
| `setSpanHighlightTextColor(int)` | Span 高亮文字色 |
| `setEnableLazyRender(boolean)` | 懒渲染 |
| `setDebugEnable(boolean)` | 调试绘制 |

## 内容构建 API

| API | 说明 |
|-----|------|
| `new Document.Builder()` / `new Document.Builder(previousDocument)` | 全量 / 增量构建文档 |
| `Document.Builder`：`addSegment(...)`、`addSegments(...)`、`removeSegment(...)`、`updateSegment(...)`、`removeIf(...)` | 编辑 Segment 列表 |
| `Paragraph.Builder.newBuilder(TexasOption)` | 创建段落构建器 |
| `Paragraph.Builder`：`tag(Object)`、`text(...)`、`stream(text[, start, end], reader)`、`addTypesetPolicy(int)`、`lineSpacingExtra(...)`、`breakStrategy(...)`、`newSpanBuilder()` | 段落配置；`stream` 自动做语义分词 |
| `Paragraph.SpanStyles.obtain(token)`：`setTag(...)`、`setForeground(Appearance)`、`setBackground(Appearance)`、`setTextStyle(TextStyle)` | `stream` 回调中为每个词设置样式和 tag |
| `SpanBuilder`：`next(text)`、`tag(...)`、`setForeground(...)`、`setBackground(...)`、`setTextStyle(...)`、`buildSpan()` | 手动逐段构建 Span |
| 排版策略常量 | `Paragraph.TYPESET_POLICY_DEFAULT` / `TYPESET_POLICY_CJK_MIX_OPTIMIZATION`（中英混排优化）/ `TYPESET_POLICY_BIDI_TEXT` / `TYPESET_POLICY_ACCEPT_CONTROL_CHAR` |
| 内置 Appearance | `DotUnderLine`、`UnderLine`、`RectGround`（均在 `me.chan.texas.text`） |
| `ViewSegment.Args`：`disableReuse(...)`、`tag(...)`、`addSelectionProvider(viewId, paragraph)` | 自定义 View Segment 配置 |

## 扩展模块入口

| 模块 | 入口类 | 说明 |
|------|--------|------|
| `ext-image` | `ImageLoader`、`Figure` | `new Figure(imageLoader.uri(url).size(w, h))` 构建插图 Segment |
| `ext-markdown` | `MarkdownParser` | `parse(String)` 返回 `MdDocument` AST |
| `ext-markdown-math` | `TexMathParser`、`MathView` | Markdown 中的 TeX 数学公式渲染 |

更完整的用法示例见[使用指南](../guide.md)。
