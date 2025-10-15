# 数学公式渲染实现指南

## 🎯 核心问题

数学公式渲染的核心挑战：
1. **字体度量** - 精确测量字符的宽度、高度、基线
2. **布局计算** - 确定每个元素的精确位置
3. **复杂结构** - 分式、根号、上下标的嵌套布局
4. **美观度** - 符合数学排版的审美标准

---

## 📚 业界标准：TeX排版系统

### TeX的核心思想：盒子模型（Box Model）

TeX将每个元素看作一个"盒子"，每个盒子有：
- **宽度 (width)**
- **高度 (height)** - 基线以上的高度
- **深度 (depth)** - 基线以下的深度
- **基线 (baseline)** - 对齐参考线

```
        ┌─────────┐
        │    ^    │  ← height (基线以上)
────────┼─────────┼────  ← baseline (基线)
        │    y    │  ← depth (基线以下)
        └─────────┘
        │←width→│
```

### TeX的渲染流程

```
1. 解析 → AST
2. 计算每个元素的盒子尺寸
3. 递归布局，确定相对位置
4. 最终渲染到输出设备
```

---

## 🔧 现代实现方案

### 1. MathJax / KaTeX (Web)

#### 技术栈
- **HTML/CSS** - 布局和定位
- **SVG** - 复杂图形（根号、分数线）
- **Web Fonts** - 数学字体

#### 核心技术：CSS定位
```html
<span style="position: relative;">
    <span style="position: absolute; top: -0.5em;">上标</span>
    基础文字
</span>
```

#### 根号实现方式
```html
<!-- 方式1: Unicode字符 + 拉伸 -->
<span class="sqrt">
    <span class="sqrt-sign">√</span>
    <span class="sqrt-content">x+1</span>
</span>

<!-- 方式2: SVG绘制 -->
<svg viewBox="0 0 100 50">
    <path d="M 0,40 L 10,50 L 20,0 L 100,0" />
</svg>
```

---

### 2. Android Canvas实现

Android上没有HTML/CSS，需要使用Canvas API手动布局。

#### 核心API

```java
// 1. 字体度量
Paint paint = new Paint();
paint.setTextSize(fontSize);

Paint.FontMetrics fm = paint.getFontMetrics();
// fm.ascent  - 基线到顶部（负值）
// fm.descent - 基线到底部（正值）
// fm.top     - 推荐的最高点
// fm.bottom  - 推荐的最低点

// 2. 文本宽度
float width = paint.measureText("text");

// 3. 文本边界
Rect bounds = new Rect();
paint.getTextBounds("text", 0, 4, bounds);
// bounds.width() - 精确宽度
// bounds.height() - 精确高度

// 4. 绘制文本
canvas.drawText("text", x, y, paint);  // y是基线位置

// 5. 绘制路径（用于根号、分数线）
Path path = new Path();
path.moveTo(x1, y1);
path.lineTo(x2, y2);
canvas.drawPath(path, paint);
```

---

## 🧮 具体实现：盒子模型

### Box类定义

```java
/**
 * 数学元素的盒子
 */
class MathBox {
    float width;    // 宽度
    float height;   // 基线以上的高度
    float depth;    // 基线以下的深度
    float x;        // 相对于父元素的x坐标
    float y;        // 相对于父元素的基线偏移
    
    // 总高度
    float totalHeight() {
        return height + depth;
    }
    
    // 中心线位置（用于分数线）
    float axis() {
        return height / 2;
    }
}

/**
 * 渲染上下文
 */
class RenderContext {
    Canvas canvas;
    Paint paint;
    float fontSize;
    float x;  // 当前x位置
    float y;  // 当前基线y位置
    
    // 字体参数（从TeX得来的经验值）
    float xHeight;        // x字符的高度
    float quad;           // em大小
    float num1;           // 分子基线上移量
    float denom1;         // 分母基线下移量
    float sup1;           // 上标上移量
    float sub1;           // 下标下移量
    float delim1;         // 定界符扩展因子
    float axisHeight;     // 中心轴高度
    
    RenderContext(Canvas canvas, float fontSize) {
        this.canvas = canvas;
        this.fontSize = fontSize;
        
        paint = new Paint();
        paint.setTextSize(fontSize);
        paint.setAntiAlias(true);
        
        // 初始化字体参数
        Paint.FontMetrics fm = paint.getFontMetrics();
        xHeight = paint.measureText("x") * 0.5f;
        quad = fontSize;
        axisHeight = xHeight * 0.25f;
        
        // TeX的经验参数
        num1 = fontSize * 0.677f;      // 分子抬升
        denom1 = fontSize * 0.686f;    // 分母下降
        sup1 = fontSize * 0.413f;      // 上标抬升
        sub1 = fontSize * 0.15f;       // 下标下降
        delim1 = fontSize * 2.39f;     // 定界符最小高度
    }
}
```

---

## 🎨 具体渲染实现

### 1. 简单变量/数字

```java
class VariableRenderer {
    MathBox render(char c, RenderContext ctx) {
        String text = String.valueOf(c);
        
        // 测量尺寸
        float width = ctx.paint.measureText(text);
        Paint.FontMetrics fm = ctx.paint.getFontMetrics();
        
        MathBox box = new MathBox();
        box.width = width;
        box.height = -fm.ascent;   // ascent是负值
        box.depth = fm.descent;
        
        // 绘制
        canvas.drawText(text, ctx.x, ctx.y, ctx.paint);
        
        // 移动x位置
        ctx.x += width;
        
        return box;
    }
}
```

### 2. 上下标

```java
class SupSubRenderer {
    MathBox render(MathBox base, MathBox sup, MathBox sub, RenderContext ctx) {
        // 1. 渲染基础元素
        float baseX = ctx.x;
        renderBox(base, ctx);
        
        // 2. 计算总尺寸
        float totalWidth = base.width;
        if (sup != null) totalWidth = Math.max(totalWidth, base.width + sup.width);
        if (sub != null) totalWidth = Math.max(totalWidth, base.width + sub.width);
        
        float totalHeight = base.height;
        float totalDepth = base.depth;
        
        // 3. 渲染上标
        if (sup != null) {
            float supX = baseX + base.width;
            float supY = ctx.y - base.height - ctx.sup1;  // 上标抬升
            
            ctx.x = supX;
            ctx.y = supY;
            ctx.fontSize *= 0.7f;  // 上标字体缩小到70%
            renderBox(sup, ctx);
            ctx.fontSize /= 0.7f;
            
            totalHeight = Math.max(totalHeight, base.height + ctx.sup1 + sup.height);
        }
        
        // 4. 渲染下标
        if (sub != null) {
            float subX = baseX + base.width;
            float subY = ctx.y + base.depth + ctx.sub1;  // 下标下降
            
            ctx.x = subX;
            ctx.y = subY;
            ctx.fontSize *= 0.7f;
            renderBox(sub, ctx);
            ctx.fontSize /= 0.7f;
            
            totalDepth = Math.max(totalDepth, base.depth + ctx.sub1 + sub.depth);
        }
        
        // 5. 返回总盒子
        MathBox result = new MathBox();
        result.width = totalWidth;
        result.height = totalHeight;
        result.depth = totalDepth;
        return result;
    }
}
```

### 3. 分式

```java
class FracRenderer {
    MathBox render(MathBox numerator, MathBox denominator, RenderContext ctx) {
        // 1. 计算宽度（取较大者，两边留间距）
        float width = Math.max(numerator.width, denominator.width) + ctx.fontSize * 0.2f;
        
        // 2. 分数线位置（在轴线上）
        float lineY = ctx.y - ctx.axisHeight;
        float lineThickness = ctx.fontSize * 0.04f;  // 线粗细
        
        // 3. 分子位置（分数线上方）
        float numY = lineY - ctx.num1;
        float numX = ctx.x + (width - numerator.width) / 2;  // 居中
        
        // 4. 分母位置（分数线下方）
        float denomY = lineY + ctx.denom1;
        float denomX = ctx.x + (width - denominator.width) / 2;  // 居中
        
        // 5. 绘制分子
        ctx.x = numX;
        ctx.y = numY;
        ctx.fontSize *= 0.7f;  // 分式字体缩小
        renderBox(numerator, ctx);
        
        // 6. 绘制分数线
        canvas.drawLine(
            ctx.x, lineY,
            ctx.x + width, lineY,
            ctx.paint
        );
        
        // 7. 绘制分母
        ctx.x = denomX;
        ctx.y = denomY;
        renderBox(denominator, ctx);
        ctx.fontSize /= 0.7f;
        
        // 8. 计算总盒子
        MathBox result = new MathBox();
        result.width = width;
        result.height = numerator.height + ctx.num1 + lineThickness / 2;
        result.depth = denominator.depth + ctx.denom1 + lineThickness / 2;
        
        ctx.x += width;
        return result;
    }
}
```

### 4. 根号（重点！）

根号是最复杂的，因为需要：
1. 绘制可拉伸的根号符号
2. 绘制上横线
3. 放置根指数（n次根）

#### 方案1: 使用Unicode根号字符 + 上划线

```java
class SqrtRenderer {
    MathBox render(MathBox content, MathBox root, RenderContext ctx) {
        // 1. 根号符号尺寸
        String sqrtSymbol = "√";  // Unicode U+221A
        float symbolWidth = ctx.paint.measureText(sqrtSymbol);
        
        // 2. 计算根号高度（需要覆盖内容）
        float contentHeight = content.height + content.depth;
        float sqrtHeight = contentHeight * 1.2f;  // 比内容高20%
        
        // 3. 根号符号需要拉伸
        // 方法：使用scaleY变换
        canvas.save();
        float scaleY = sqrtHeight / ctx.fontSize;
        canvas.scale(1, scaleY, ctx.x, ctx.y);
        
        // 绘制根号符号
        canvas.drawText(sqrtSymbol, ctx.x, ctx.y, ctx.paint);
        canvas.restore();
        
        // 4. 绘制上横线
        float lineY = ctx.y - content.height - ctx.fontSize * 0.1f;
        float lineStartX = ctx.x + symbolWidth;
        float lineEndX = lineStartX + content.width + ctx.fontSize * 0.1f;
        float lineThickness = ctx.fontSize * 0.04f;
        
        ctx.paint.setStrokeWidth(lineThickness);
        canvas.drawLine(lineStartX, lineY, lineEndX, lineY, ctx.paint);
        
        // 5. 绘制内容
        float contentX = lineStartX + ctx.fontSize * 0.05f;
        ctx.x = contentX;
        renderBox(content, ctx);
        
        // 6. 绘制根指数（如果有）
        if (root != null) {
            float rootX = ctx.x - symbolWidth * 0.5f;
            float rootY = ctx.y - content.height - ctx.fontSize * 0.5f;
            
            ctx.x = rootX;
            ctx.y = rootY;
            ctx.fontSize *= 0.6f;  // 根指数更小
            renderBox(root, ctx);
            ctx.fontSize /= 0.6f;
        }
        
        // 7. 返回盒子
        MathBox result = new MathBox();
        result.width = symbolWidth + content.width + ctx.fontSize * 0.15f;
        result.height = content.height + ctx.fontSize * 0.1f;
        result.depth = content.depth;
        return result;
    }
}
```

#### 方案2: 使用Path绘制根号（更精确）

```java
class SqrtPathRenderer {
    MathBox render(MathBox content, MathBox root, RenderContext ctx) {
        float contentHeight = content.height + content.depth;
        float thickness = ctx.fontSize * 0.04f;
        
        // 根号路径设计
        //     ┌─────────  ← 上横线
        //    /
        //   /
        //  /
        // ╱  ← 斜线
        // ╲
        //  ╲ ← 小勾
        
        Path path = new Path();
        
        // 起点（小勾底部）
        float x0 = ctx.x;
        float y0 = ctx.y;
        
        // 小勾顶部
        float x1 = x0 + ctx.fontSize * 0.1f;
        float y1 = y0 - contentHeight * 0.2f;
        
        // 主斜线底部（转折点）
        float x2 = x1 + ctx.fontSize * 0.05f;
        float y2 = y0 + content.depth;
        
        // 主斜线顶部
        float x3 = x2 + ctx.fontSize * 0.15f;
        float y3 = y0 - content.height - ctx.fontSize * 0.1f;
        
        // 上横线结束
        float x4 = x3 + content.width + ctx.fontSize * 0.1f;
        float y4 = y3;
        
        // 绘制路径
        path.moveTo(x0, y0);
        path.lineTo(x1, y1);  // 小勾
        path.lineTo(x2, y2);  // 到底部
        path.lineTo(x3, y3);  // 到顶部
        path.lineTo(x4, y4);  // 上横线
        
        ctx.paint.setStyle(Paint.Style.STROKE);
        ctx.paint.setStrokeWidth(thickness);
        ctx.paint.setStrokeCap(Paint.Cap.SQUARE);
        canvas.drawPath(path, ctx.paint);
        ctx.paint.setStyle(Paint.Style.FILL);
        
        // 绘制内容
        float contentX = x3 + ctx.fontSize * 0.05f;
        ctx.x = contentX;
        renderBox(content, ctx);
        
        // 绘制根指数
        if (root != null) {
            float rootX = x0 + ctx.fontSize * 0.05f;
            float rootY = y3 - ctx.fontSize * 0.2f;
            
            ctx.x = rootX;
            ctx.y = rootY;
            ctx.fontSize *= 0.6f;
            renderBox(root, ctx);
            ctx.fontSize /= 0.6f;
        }
        
        MathBox result = new MathBox();
        result.width = x4 - x0;
        result.height = content.height + ctx.fontSize * 0.1f;
        result.depth = content.depth;
        return result;
    }
}
```

#### 方案3: 使用数学字体的可拉伸根号（最专业）

专业的数学字体（如 Latin Modern Math, STIX）包含可拉伸的根号字形：
- 顶部（top）
- 中间可重复部分（extension）
- 底部（bottom）
- 小勾（surd）

```java
class ProfessionalSqrtRenderer {
    // 需要使用支持OpenType MATH表的字体
    // 如 Latin Modern Math, STIX Two Math
    
    MathBox render(MathBox content, MathBox root, RenderContext ctx) {
        // 1. 从字体中获取根号部件
        Typeface mathFont = Typeface.createFromAsset(
            context.getAssets(), 
            "fonts/latinmodern-math.otf"
        );
        ctx.paint.setTypeface(mathFont);
        
        // 2. 根号字符编码
        // U+221A: 基础根号
        // U+E000-U+E0FF: 私有区域的拉伸部件（取决于字体）
        
        float targetHeight = content.height + content.depth + ctx.fontSize * 0.2f;
        
        // 3. 计算需要的拉伸
        float baseHeight = ctx.fontSize;
        int extensionCount = (int) Math.ceil((targetHeight - baseHeight) / (ctx.fontSize * 0.5f));
        
        // 4. 绘制根号（简化版，实际需要查询MATH表）
        String sqrtGlyph = "\u221A";
        
        canvas.save();
        float scaleY = targetHeight / baseHeight;
        canvas.scale(1, scaleY, ctx.x, ctx.y);
        canvas.drawText(sqrtGlyph, ctx.x, ctx.y, ctx.paint);
        canvas.restore();
        
        // ... 后续与方案1类似
    }
}
```

---

### 5. 定界符（括号拉伸）

```java
class DelimiterRenderer {
    MathBox render(String leftDelim, MathBox content, String rightDelim, RenderContext ctx) {
        float contentHeight = content.height + content.depth;
        float minHeight = Math.max(contentHeight * 1.2f, ctx.delim1);
        
        // 绘制左括号
        drawStretchyDelimiter(leftDelim, ctx.x, ctx.y, minHeight, ctx);
        float leftWidth = ctx.fontSize * 0.3f;
        ctx.x += leftWidth;
        
        // 绘制内容
        renderBox(content, ctx);
        
        // 绘制右括号
        drawStretchyDelimiter(rightDelim, ctx.x, ctx.y, minHeight, ctx);
        ctx.x += leftWidth;
        
        MathBox result = new MathBox();
        result.width = leftWidth + content.width + leftWidth;
        result.height = minHeight / 2;
        result.depth = minHeight / 2;
        return result;
    }
    
    private void drawStretchyDelimiter(String delim, float x, float y, float height, RenderContext ctx) {
        if (delim.equals("(") || delim.equals(")")) {
            // 使用Path绘制可拉伸的圆括号
            Path path = new Path();
            
            boolean isLeft = delim.equals("(");
            float sign = isLeft ? 1 : -1;
            
            float width = ctx.fontSize * 0.2f;
            float y1 = y - height / 2;
            float y2 = y + height / 2;
            
            // 贝塞尔曲线绘制括号
            path.moveTo(x + sign * width, y1);
            path.cubicTo(
                x, y1,
                x, y,
                x, y
            );
            path.cubicTo(
                x, y,
                x, y2,
                x + sign * width, y2
            );
            
            ctx.paint.setStyle(Paint.Style.STROKE);
            ctx.paint.setStrokeWidth(ctx.fontSize * 0.04f);
            canvas.drawPath(path, ctx.paint);
            ctx.paint.setStyle(Paint.Style.FILL);
        } else {
            // 其他定界符类似处理
        }
    }
}
```

---

## 📐 关键参数和经验值

### TeX的标准参数（基于字号）

```java
class MathConstants {
    // 所有值都是相对于fontSize的比例
    
    // 上下标
    static final float SUP_SHIFT_UP = 0.413f;      // 上标抬升
    static final float SUB_SHIFT_DOWN = 0.15f;     // 下标下降
    static final float SUP_SIZE = 0.7f;            // 上标大小比例
    static final float SUB_SIZE = 0.7f;            // 下标大小比例
    
    // 分式
    static final float NUM_SHIFT_UP = 0.677f;      // 分子抬升
    static final float DENOM_SHIFT_DOWN = 0.686f;  // 分母下降
    static final float FRAC_SIZE = 0.7f;           // 分式内容大小
    static final float FRAC_LINE_THICKNESS = 0.04f;// 分数线粗细
    static final float FRAC_GAP = 0.1f;            // 分子分母与线的间距
    
    // 根号
    static final float SQRT_EXTRA_HEIGHT = 0.1f;   // 根号额外高度
    static final float SQRT_LINE_THICKNESS = 0.04f;// 根号线粗细
    static final float SQRT_ROOT_SIZE = 0.6f;      // 根指数大小
    
    // 定界符
    static final float DELIM_SIZE = 2.39f;         // 最小定界符高度
    static final float DELIM_EXTRA = 0.2f;         // 定界符额外高度
    
    // 间距
    static final float THIN_SPACE = 0.16667f;      // \, 小间距
    static final float MED_SPACE = 0.22222f;       // \: 中间距
    static final float THICK_SPACE = 0.27778f;     // \; 大间距
    static final float QUAD_SPACE = 1.0f;          // \quad 全宽
    
    // 轴线（用于运算符对齐）
    static final float AXIS_HEIGHT = 0.25f;        // 相对于x-height
}
```

---

## 🎨 字体选择

### 推荐的数学字体

1. **Latin Modern Math**
   - TeX的现代继承者
   - 完整的OpenType MATH支持
   - 免费开源

2. **STIX Two Math**
   - Times风格
   - Unicode数学符号完整
   - 适合科学出版

3. **Cambria Math**
   - Microsoft的数学字体
   - Windows系统自带
   - Android需要额外引入

4. **思源黑体 + 数学符号补充**
   - 中文支持好
   - 需要补充数学符号

### Android字体使用

```java
// 加载自定义字体
Typeface mathFont = Typeface.createFromAsset(
    context.getAssets(),
    "fonts/latinmodern-math.otf"
);
paint.setTypeface(mathFont);

// 或使用系统字体
Typeface systemFont = Typeface.create("serif", Typeface.NORMAL);
```

---

## 🚀 完整示例

```java
public class MathRenderer {
    private Canvas canvas;
    private RenderContext context;
    
    public MathRenderer(Canvas canvas, float fontSize) {
        this.canvas = canvas;
        this.context = new RenderContext(canvas, fontSize);
    }
    
    public void render(MathList ast, float x, float y) {
        context.x = x;
        context.y = y;
        
        for (int i = 0; i < ast.terms.size(); i++) {
            Term term = ast.terms.get(i);
            renderTerm(term);
            
            // 渲染运算符
            if (i < ast.operators.size()) {
                String op = ast.operators.get(i);
                renderOperator(op);
            }
        }
    }
    
    private void renderTerm(Term term) {
        MathBox atomBox = renderAtom(term.atom);
        
        if (term.suffix != null) {
            renderSupSub(atomBox, term.suffix);
        }
    }
    
    private MathBox renderAtom(Atom atom) {
        if (atom instanceof NumberAtom) {
            return renderNumber((NumberAtom) atom);
        } else if (atom instanceof VariableAtom) {
            return renderVariable((VariableAtom) atom);
        } else if (atom instanceof FracAtom) {
            return renderFrac((FracAtom) atom);
        } else if (atom instanceof SqrtAtom) {
            return renderSqrt((SqrtAtom) atom);
        }
        // ... 其他类型
        
        return new MathBox();
    }
    
    // ... 具体渲染方法的实现
}
```

---

## 📊 性能优化

### 1. 缓存盒子尺寸

```java
class CachedMathRenderer {
    private Map<MathNode, MathBox> boxCache = new HashMap<>();
    
    MathBox getBox(MathNode node, RenderContext ctx) {
        if (boxCache.containsKey(node)) {
            return boxCache.get(node);
        }
        
        MathBox box = computeBox(node, ctx);
        boxCache.put(node, box);
        return box;
    }
}
```

### 2. 预渲染到Bitmap

```java
class PrerenderedMath {
    private Bitmap bitmap;
    private MathBox box;
    
    void prerender(MathList ast, float fontSize) {
        // 1. 计算尺寸
        box = computeBoxSize(ast, fontSize);
        
        // 2. 创建Bitmap
        bitmap = Bitmap.createBitmap(
            (int) box.width,
            (int) box.totalHeight(),
            Bitmap.Config.ARGB_8888
        );
        
        // 3. 渲染到Bitmap
        Canvas canvas = new Canvas(bitmap);
        MathRenderer renderer = new MathRenderer(canvas, fontSize);
        renderer.render(ast, 0, box.height);
    }
    
    void draw(Canvas canvas, float x, float y) {
        canvas.drawBitmap(bitmap, x, y, null);
    }
}
```

---

## 📚 参考资源

### 论文和文档
1. **"Mathematical Typesetting"** - Donald Knuth (TeX作者)
2. **"The TeXbook"** - TeX的官方手册
3. **MathML规范** - W3C的数学标记语言标准
4. **OpenType MATH表规范** - 微软

### 开源项目
1. **KaTeX** - 快速的Web数学渲染库
   - https://github.com/KaTeX/KaTeX
   
2. **MathJax** - 功能完整的Web数学渲染
   - https://github.com/mathjax/MathJax
   
3. **iosMath** - iOS的数学渲染库
   - https://github.com/kostub/iosMath
   - 可以参考其布局算法

4. **MathView** - Android数学公式View
   - 可以搜索GitHub上的现有实现

---

## 🎯 总结

### 核心要点

1. **盒子模型** - 一切基于盒子的width/height/depth
2. **递归布局** - 从叶子节点向上计算尺寸
3. **精确定位** - 基于基线的坐标系统
4. **字体度量** - 使用Paint.FontMetrics和measureText
5. **经验参数** - 使用TeX验证过的比例参数

### 实现顺序建议

1. ✅ 先实现简单元素（数字、变量）
2. ✅ 实现上下标（掌握基线偏移）
3. ✅ 实现分式（掌握垂直布局）
4. ✅ 实现根号（Path绘制）
5. ✅ 实现定界符（可拉伸绘制）
6. ✅ 优化性能（缓存、预渲染）

### 你的项目

对于你的Android项目：
1. 使用我提供的 `MathParser_CharStream.java` 解析公式
2. 基于本文档实现 `MathRenderer`
3. 使用Canvas API和Paint进行渲染
4. 参考TeX的参数保证美观
5. 根据需要优化性能

**需要代码实现吗？我可以为你创建一个完整的Android MathRenderer示例！**

