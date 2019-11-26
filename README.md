# Te-boys

Te-boys是一款支持图文混排的文本渲染库，目前支持两边对齐，并且单词支持断字，用户可以自由的自定义效果。

![效果图](art/device-2019-11-01-171647.png)

# TODO
- [ ] 渲染view开发
- [ ] adapter异常安全保障
- [ ] 移除extra

unit test 加上 -Xmx8g 的vm flag

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

# workflow

source read text -> parse text -> typeset -> render


# link

[english-words](https://github.com/dwyl/english-words.git)

[the book and the sword](https://www.520txtba.com/Txt/XiaoShuo-146678.html)

[async profiler](https://github.com/jvm-profiling-tools/async-profiler)

[JHyphenator](https://github.com/mfietz/JHyphenator)