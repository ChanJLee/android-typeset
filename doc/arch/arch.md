# 架构

任何想要被渲染的数据都需要通过parser解析成为渲染引擎能够识别的数据结构。

其中能被识别的数据结构我们称为Document。一个Document由很多Segment组成，一个Segment可以理解为需要渲染的片段

目前的segment分为以下几种

- Paragraph 文章段落，内部是由文本\drawable组成。

- Figure 插图

- ViewSegment android里的View

渲染引擎在渲染的时候，通过给texas view设置source来给定输入数据。之后数据会被读取并被回传给parser

parser将输入数据解析成 document。document会被渲染引擎识别，并被显示。

典型的过程

1. 设置source

```java
texasView.setSource(new AssetsTextSource(this, "bay.xml"));
```

这行代码设置了来自assets目录下的一个文本

2. parser解析

```java
public class TextParser implements Parser<CharSequence> {
	@Override
	@NonNull
	public Document parse(CharSequence charSequence, Measurer measurer, Hypher hypher,
						  TextAttribute textAttribute, RenderOption renderOption) {
		Document document = Document.obtain();
		int len = charSequence.length();
		for (int i = skipBlank(charSequence, 0, len); i < len; ) {
			int last = findNewline(charSequence, i, len);
			if (i != last) {
				Paragraph.Builder builder = Paragraph.Builder.newBuilder(measurer, hypher, textAttribute);
				parse(charSequence, i, last, builder);
				document.addSegment(builder.build());
			}
			i = skipBlank(charSequence, last, len);
		}
		return document;
	}
}
```