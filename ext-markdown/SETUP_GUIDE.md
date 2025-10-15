# Latin Modern Math 字体使用完整指南

## 🎯 快速开始

### 准备工作

#### 1. 下载字体
```bash
# 从 GUST 下载 Latin Modern Math
wget http://www.gust.org.pl/projects/e-foundry/lm-math/download/latinmodern-math-1959.zip
unzip latinmodern-math-1959.zip
```

或者从这些地方获取：
- https://www.gust.org.pl/projects/e-foundry/lm-math
- Google Fonts (可能有)
- 系统自带（macOS/Linux）

#### 2. 安装 Python 依赖
```bash
pip install fonttools
```

---

## 📦 提取字体数据

### 运行提取脚本

```bash
python extract_math_data.py latinmodern-math.otf
```

输出文件：`latin_modern_math_data.json`

### 验证输出

```bash
# 查看生成的数据
cat latin_modern_math_data.json | head -50

# 应该看到类似这样的内容：
{
  "fontName": "Latin Modern Math",
  "unitsPerEm": 1000,
  "constants": {
    "RadicalVerticalGap": 50,
    "RadicalRuleThickness": 40,
    ...
  },
  "radical": {
    "baseGlyphId": 728,
    "variants": [
      {
        "glyphId": 728,
        "advance": 800,
        ...
      }
    ],
    "parts": [...]
  }
}
```

---

## 📱 集成到 Android 项目

### 1. 目录结构

```
app/src/main/
├── assets/
│   ├── fonts/
│   │   └── latinmodern-math.otf
│   └── latin_modern_math_data.json
└── java/
    └── me/chan/texas/math/render/
        └── LatinModernRadicalRenderer.java
```

### 2. 复制文件

```bash
# 复制字体
cp latinmodern-math.otf app/src/main/assets/fonts/

# 复制数据
cp latin_modern_math_data.json app/src/main/assets/

# 复制代码
cp LatinModernMathRenderer.java.example app/src/main/java/me/chan/texas/math/render/LatinModernRadicalRenderer.java
```

### 3. 使用示例

```java
public class MathView extends View {
    private LatinModernRadicalRenderer radicalRenderer;
    
    public MathView(Context context) {
        super(context);
        radicalRenderer = new LatinModernRadicalRenderer(context);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        float x = 100;
        float y = 200;
        float fontSize = 48;
        
        // 渲染根号
        float width = radicalRenderer.renderRadical(
            canvas, x, y,
            fontSize * 0.7f,  // contentHeight
            fontSize * 0.3f,  // contentDepth
            fontSize,
            null  // rootIndex (null = 平方根)
        );
        
        // 在根号后绘制内容
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        canvas.drawText("x²+1", x + width, y, paint);
    }
}
```

---

## 🔍 Android 版本兼容性

### Android 11+ (API 30+)

✅ **完整支持**
- 使用 `Canvas.drawGlyphs()` 直接绘制字形ID
- 支持预制变体和部件拼接
- 最佳效果

```java
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
    int[] glyphIds = { variant.glyphId };
    float[] positions = { x, y };
    canvas.drawGlyphs(glyphIds, 0, positions, 0, 1, mathFont, paint);
}
```

### Android 10 及以下

⚠️ **自动降级**
- 使用 Unicode 字符 "√" + 纵向拉伸
- 或使用 Path 手动绘制
- 效果略差但可用

```java
else {
    // 降级方案
    renderRadicalFallback(canvas, x, y, height, fontSize);
}
```

---

## 📊 性能对比

| 方案 | Android 11+ | Android 10- | 效果 | 性能 |
|------|------------|------------|------|------|
| **字形ID绘制** | ✅ 完美 | ❌ 不支持 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Unicode拉伸** | ✅ 可用 | ✅ 可用 | ⭐⭐⭐ | ⭐⭐⭐⭐ |
| **Path绘制** | ✅ 可用 | ✅ 可用 | ⭐⭐⭐⭐ | ⭐⭐⭐ |

---

## 🎨 定位原理

### 根号的关键尺寸

```
         ┌────────────────  ← y - contentHeight - extraAscender
         │  extra          
         ├────────────────  ← y - contentHeight (内容顶部)
        /│
       / │  content
      /  │  height
     /   │
    /    ├────────────────  ← y (基线)
   /     │
  /      │  content
 ╱       │  depth
╲        │
 ╲       ├────────────────  ← y + contentDepth (内容底部)
  ╲
   └─────────────────────
   
   │←→│ radicalWidth
```

### 关键参数

```java
// 从 MATH 表中获取（字体单位）
int radicalVerticalGap;         // 根号顶部到内容的间距
int radicalRuleThickness;       // 上横线粗细
int radicalExtraAscender;       // 根号超出内容的高度

// 转换为像素
float gap = mathData.toPixels(radicalVerticalGap, fontSize);
float thickness = mathData.toPixels(radicalRuleThickness, fontSize);
float extra = mathData.toPixels(radicalExtraAscender, fontSize);

// 计算总高度
float totalHeight = contentHeight + contentDepth + gap + thickness + extra;
```

### 根指数定位

```
  n        n = 根指数
 ┌────
 │
√ 内容

// 位置计算
float rootX = radicalX + kernBefore;
float rootY = baselineY - contentHeight - degreeRaise;

// degreeRaise = 内容总高度 * 0.65  (65%来自MATH表)
```

---

## 🐛 常见问题

### Q1: 根号显示不完整

**问题**：根号被裁剪

**原因**：View 的测量尺寸不够

**解决**：
```java
@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // 预留足够的高度
    int height = (int) (fontSize * 2);  // 根号可能很高
    setMeasuredDimension(width, height);
}
```

### Q2: 根号位置不对

**问题**：根号和内容不对齐

**原因**：基线位置计算错误

**解决**：
```java
// 确保传入的 y 是基线位置
float baseline = y + contentHeight;  // 不是 y 本身！
radicalRenderer.renderRadical(canvas, x, baseline, ...);
```

### Q3: Android 10 显示效果差

**问题**：降级方案不美观

**解决方案**：
1. 使用更精细的 Path 绘制
2. 预渲染根号到 Bitmap
3. 使用 SVG 资源

### Q4: 字体文件太大

**问题**：latinmodern-math.otf 约 500KB

**解决方案**：
1. 使用 subset 工具提取需要的字形
   ```bash
   pyftsubset latinmodern-math.otf \
     --unicodes="U+221A,U+0030-0039,U+0041-005A,U+0061-007A" \
     --output-file=latinmodern-math-subset.otf
   ```
2. 或使用 Android App Bundle 按需下载

---

## 📐 进阶技巧

### 1. 缓存渲染结果

```java
class CachedRadicalRenderer {
    private LruCache<String, Bitmap> cache = new LruCache<>(50);
    
    Bitmap getOrRender(String key, float height, float fontSize) {
        Bitmap cached = cache.get(key);
        if (cached != null) return cached;
        
        // 渲染到 Bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        renderRadical(canvas, 0, 0, height, fontSize, null);
        
        cache.put(key, bitmap);
        return bitmap;
    }
}
```

### 2. 自适应根号大小

```java
// 根据内容自动选择最佳根号
GlyphVariant selectBestVariant(float contentHeight) {
    float targetHeight = contentHeight * 1.2f;
    
    for (GlyphVariant v : mathData.radical.variants) {
        float variantHeight = mathData.toPixels(v.advance, fontSize);
        if (variantHeight >= targetHeight && 
            variantHeight <= targetHeight * 1.1f) {
            return v;  // 找到最接近的
        }
    }
    
    return mathData.radical.variants.get(0);
}
```

### 3. 支持其他可拉伸符号

同样的方法可以用于：
- 括号 `()[]{}⟨⟩`
- 竖线 `|‖`
- 积分号 `∫∬∭`
- 求和号 `∑∏`
- 箭头 `→⇒⟶`

只需要在 Python 脚本中添加相应的提取逻辑。

---

## ✅ 检查清单

使用前确认：
- [ ] 已下载 Latin Modern Math 字体
- [ ] 已运行 `extract_math_data.py`
- [ ] 生成了 `latin_modern_math_data.json`
- [ ] 字体和数据都放在 `assets/` 目录
- [ ] 代码中的路径正确
- [ ] 测试了 Android 11+ 和 10- 的设备

---

## 🎉 总结

### 完整流程

```
1. 下载 Latin Modern Math 字体
   ↓
2. 运行 Python 脚本提取数据
   ↓
3. 将字体和数据放入 assets/
   ↓
4. 使用 LatinModernRadicalRenderer
   ↓
5. 渲染完美的根号！
```

### 核心优势

- ✅ **专业品质** - 使用 TeX 级别的数学字体
- ✅ **精确定位** - 基于 OpenType MATH 表的参数
- ✅ **版本兼容** - Android 11+ 完美，10- 降级可用
- ✅ **性能优化** - 预提取数据，运行时无解析开销
- ✅ **易于扩展** - 同样方法可用于其他数学符号

---

**现在你可以在 Android 上渲染专业级的数学公式了！** 🚀

有任何问题请参考：
- `LatinModernMath_Guide.md` - 详细技术说明
- `MathRendering_Guide.md` - 通用渲染指南
- `extract_math_data.py` - 提取脚本
- `LatinModernMathRenderer.java.example` - 完整实现

