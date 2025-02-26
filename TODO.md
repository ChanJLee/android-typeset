1. 移除 TexasOption 在 Paragraph构建过程中的依赖
2. 排版任务拆分，不再统一排版，分派到View上进行懒加载
3. 文本解析懒加载，所以加载更多也可以被简化，这样也会触发android内部的渲染cache