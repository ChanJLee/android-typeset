# 架构

Texas 的数据流经过四层，从原始数据到最终上屏：

```
Source → Parser → Typesetter → Renderer → TexasView
```

1. **Source** — `TexasView.DocumentSource` 回调向引擎提供原始数据。业务重写 `onRead(TexasOption option, Document previousDocument)`，返回构建好的 `Document`；`previousDocument` 非空时可以做增量构建。
2. **Parser** — 把输入数据解析为引擎可识别的 `Document`（`Segment` 列表）。库内置 `TextDocumentSource` 处理纯文本；扩展模块提供 `MarkdownParser`（ext-markdown）等更复杂的解析器。
3. **Typesetter** — 排版层，把 `Paragraph` 排成 `Layout`。有两种排版器：
   - `TexParagraphTypesetter` — Knuth-Plass 全局断行 + 两端对齐（[算法详解](../algorithm/tex-algorithm.md)）
   - `SimpleParagraphTypesetter` — 简单贪心断行
   由 `BreakStrategy`（`SIMPLE` / `BALANCED`）等配置决定行为。
4. **Renderer** — 把 `Layout` 绘制到 `Canvas`，带指令级渲染缓存。
5. **TexasView** — `FrameLayout` 子类，承载渲染器，对外暴露交互 API（点击、高亮、选中、滚动、Segment 动画）。

## 数据模型

一个 `Document` 由多个 `Segment` 自上而下排列组成。每个 Segment 占满 TexasView 的宽度。

| Segment 类型 | 说明 |
|--------------|------|
| `Paragraph` | 文本段落，由文本 / emoticon / HyperSpan 组成，支持高亮、两端对齐、标点挤压等 |
| `Figure` | 插图（ext-image 模块提供），优化了图片加载导致的抖动 |
| `ViewSegment` | 包装任意 Android `View`，可通过 `Args.addSelectionProvider()` 让内部的 `ParagraphView` 参与全文自由选中 |

`Paragraph` 内部由 `Span` 组成，`Span` 可以携带业务 tag（两级 Tag 系统：Paragraph tag + Span tag），是点击 / 高亮谓词的判断依据。

## 关键包

| 包 | 职责 |
|----|------|
| `me.chan.texas.text` | 数据模型：`Document`、`Segment`、`Paragraph`、`Span`、`TextAttribute`、分词器（tokenizer） |
| `me.chan.texas.typesetter` | 排版引擎：`TexParagraphTypesetter`、`SimpleParagraphTypesetter` |
| `me.chan.texas.renderer` | `TexasView`、`Renderer`、`RenderOption`、`Selection`、`TouchEvent`、内部 RecyclerView 渲染管线（`renderer.ui.rv`） |
| `me.chan.texas.measurer` | 文本测量 |
| `me.chan.texas.hyphenation` | 英文断字 |
| `me.chan.texas.source` | 内置数据源（`TextDocumentSource`） |
| `me.chan.texas.di` | Dagger 2 依赖注入（`TexasComponent`、`TextEngineCoreComponent`） |
| `me.chan.texas.misc` | 基础设施：`ObjectPool`、`LruPool`、`BitBucket`、`Recyclable` |

## 更新与 diff

`setSource()` 提交新的 `DocumentSource` 后，引擎对新旧 `Document` 做 diff（`detectMoves=false`），只有变化的 Segment 会被重新排版和渲染，其余 Segment 直接复用缓存。新增 / 删除的 Segment 会触发 `TexasView.SegmentAnimator` 的动画回调，内部由 `DefaultItemAnimator` 驱动（[触发逻辑详解](item-animator.md)）。

因此增量更新的正确姿势是：在 `onRead` 中用 `new Document.Builder(previousDocument)` 基于旧文档构建，把多个修改合并成一次 `setSource()` 提交。

## 缓存

三层缓存自上而下：

```
布局缓存（Layout）→ 测量缓存（Measure）→ 绘制指令缓存（Instruction）
```

- 缓存只在内容、文本样式（`RenderOption`）或布局宽度变化时失效
- 高亮、选中、滚动、`redraw()` 均复用缓存，不触发重排
- 指令级缓存使滚动吞吐相比无缓存提升 300% 以上

## 典型流程

```java
// 1. 设置 source（最简：内置纯文本源）
texasView.setSource(new TextDocumentSource("hello world"));

// 2. 自定义 source：业务自己解析数据并构建 Document
texasView.setSource(new TexasView.DocumentSource() {
    @Override
    protected Document onRead(TexasOption option, @Nullable Document previousDocument) {
        Document.Builder builder = new Document.Builder();
        for (String text : paragraphs) {
            builder.addSegment(Paragraph.Builder.newBuilder(option)
                    .text(text)
                    .build());
        }
        return builder.build();
    }
});
```

解析、排版、测量在工作线程完成，完成后回到主线程上屏；业务不应长期持有引擎内部的数据结构（`Document`、`Span` 等），即用即走。
