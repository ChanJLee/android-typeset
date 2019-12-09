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
10. 渲染模式，是否是单页，还是多页
11. 是否可选中
12. 自定义解析
13. 资源复用，内存/cpu高度优化

## 实例代码

1. 在layout中引用自定义view

```xml
<me.chan.texas.renderer.TexasView
    android:id="@+id/text"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_margin="10dp" />
```

2. 渲染文本

```java
teView.setSource(new AssetsTextSource(content, "sample.txt"));
```

## 文档

[算法描述](doc/tex-algorithm.md)

[开发规范](doc/code/dev.md)

[架构设计](doc/arch/arch.md)

[API](doc/api/api.md)

## 资源引用

[english-words](https://github.com/dwyl/english-words.git)

[the book and the sword](https://www.520txtba.com/Txt/XiaoShuo-146678.html)

[async profiler](https://github.com/jvm-profiling-tools/async-profiler)

[JHyphenator](https://github.com/mfietz/JHyphenator)
