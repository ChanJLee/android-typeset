# Latin Modern Math 字体使用指南

## 📐 OpenType MATH 表

### 什么是 MATH 表？

OpenType MATH 是微软和其他公司为数学排版设计的字体扩展，包含：

1. **MathConstants** - 数学排版参数（如上标位置、分数线粗细）
2. **MathGlyphInfo** - 每个字形的扩展信息（斜体校正、附着点）
3. **MathVariants** - 可拉伸字形的变体和部件信息

专业数学字体（Latin Modern Math, STIX, Cambria Math）都包含完整的 MATH 表。

---

## 🔍 根号在 MATH 字体中的结构

### 根号的组成部分

```
     ┌────────────  ← top (顶部横线)
    /|
   / |              ← vertical extension (可重复的垂直部分)
  /  |
 /   |
/    └──────────   ← bottom (底部横线)
\                  
 \                 ← surd (小勾)
```

### 字形变体

数学字体提供多个预制的根号大小：

| 字形 | Unicode | 用途 |
|------|---------|------|
| √ | U+221A | 基础根号（小） |
| √ | U+221A + size1 | 中等根号 |
| √ | U+221A + size2 | 较大根号 |
| √ | U+221A + size3 | 大根号 |

如果预制大小不够，使用部件拼接：
- **U+E000** - 小勾（surd）
- **U+E001** - 垂直延伸部件
- **U+E002** - 顶部横线起点
- **U+E003** - 横线延伸部件

*注意：私有区域字符编码因字体而异*

---

## 🚫 Android 的限制

### 问题：Android 不直接支持 MATH 表

```java
// ❌ Android 没有提供访问 MATH 表的 API
// 无法像 HarfBuzz/FreeType 那样直接读取
```

**原因**：
- Android 的 `Typeface` 类不暴露 OpenType 高级特性
- `Paint.FontMetrics` 只有基本度量
- 无法查询字形变体和部件信息

### 解决方案

有以下几种方案：

---

## 🔧 方案1：使用 HarfBuzz (JNI)

### 概述
HarfBuzz 是 Android 底层使用的文本整形引擎，支持完整的 OpenType MATH。

### 实现步骤

#### 1. 添加 HarfBuzz-ng 依赖

```groovy
// build.gradle
android {
    defaultConfig {
        ndk {
            abiFilters 'armeabi-v7a', 'arm64-v8a', 'x86', 'x86_64'
        }
    }
}

dependencies {
    // 或者自己编译 HarfBuzz
    implementation 'com.github.harfbuzz:harfbuzz-android:2.9.1'
}
```

#### 2. JNI 接口

```cpp
// native-lib.cpp
#include <jni.h>
#include <hb.h>
#include <hb-ot.h>

extern "C" JNIEXPORT jfloatArray JNICALL
Java_me_chan_texas_math_MathFontInfo_getRadicalGlyphPartsNative(
    JNIEnv* env,
    jobject /* this */,
    jstring fontPath,
    jfloat targetHeight) {
    
    const char* path = env->GetStringUTFChars(fontPath, nullptr);
    
    // 1. 加载字体
    hb_blob_t* blob = hb_blob_create_from_file(path);
    hb_face_t* face = hb_face_create(blob, 0);
    hb_font_t* font = hb_font_create(face);
    
    // 2. 获取 MATH 表
    hb_ot_math_glyph_info_t math_info;
    hb_codepoint_t radical_glyph = 0x221A;  // √
    
    // 3. 查询根号的垂直变体
    unsigned int variants_count;
    hb_ot_math_glyph_variant_t* variants = 
        hb_ot_math_get_glyph_variants(
            font,
            radical_glyph,
            HB_DIRECTION_TTB,  // 垂直方向
            0,  // start_offset
            &variants_count,
            nullptr
        );
    
    // 4. 选择合适的变体
    hb_codepoint_t selected_glyph = radical_glyph;
    for (unsigned int i = 0; i < variants_count; i++) {
        if (variants[i].advance >= targetHeight) {
            selected_glyph = variants[i].glyph;
            break;
        }
    }
    
    // 5. 如果没有合适的预制变体，获取部件
    hb_ot_math_glyph_part_t* parts = nullptr;
    unsigned int parts_count;
    
    if (selected_glyph == radical_glyph) {
        parts = hb_ot_math_get_glyph_assembly(
            font,
            selected_glyph,
            HB_DIRECTION_TTB,
            0,
            &parts_count,
            nullptr,
            nullptr
        );
    }
    
    // 6. 返回结果到 Java
    jfloatArray result = env->NewFloatArray(parts_count * 4);
    jfloat* data = env->GetFloatArrayElements(result, nullptr);
    
    for (unsigned int i = 0; i < parts_count; i++) {
        data[i * 4 + 0] = parts[i].glyph;
        data[i * 4 + 1] = parts[i].start_connector_length;
        data[i * 4 + 2] = parts[i].end_connector_length;
        data[i * 4 + 3] = parts[i].full_advance;
    }
    
    env->ReleaseFloatArrayElements(result, data, 0);
    
    // 清理
    hb_font_destroy(font);
    hb_face_destroy(face);
    hb_blob_destroy(blob);
    env->ReleaseStringUTFChars(fontPath, path);
    
    return result;
}
```

#### 3. Java 包装类

```java
public class MathFontInfo {
    static {
        System.loadLibrary("native-lib");
    }
    
    private String fontPath;
    
    public MathFontInfo(Context context, String assetPath) {
        // 将字体从 assets 复制到缓存目录
        File fontFile = new File(context.getCacheDir(), "math-font.otf");
        try (InputStream is = context.getAssets().open(assetPath);
             FileOutputStream os = new FileOutputStream(fontFile)) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
        }
        this.fontPath = fontFile.getAbsolutePath();
    }
    
    public static class RadicalParts {
        public int[] glyphIds;
        public float[] startConnectors;
        public float[] endConnectors;
        public float[] advances;
    }
    
    public RadicalParts getRadicalParts(float targetHeight) {
        float[] data = getRadicalGlyphPartsNative(fontPath, targetHeight);
        
        RadicalParts parts = new RadicalParts();
        int count = data.length / 4;
        parts.glyphIds = new int[count];
        parts.startConnectors = new float[count];
        parts.endConnectors = new float[count];
        parts.advances = new float[count];
        
        for (int i = 0; i < count; i++) {
            parts.glyphIds[i] = (int) data[i * 4 + 0];
            parts.startConnectors[i] = data[i * 4 + 1];
            parts.endConnectors[i] = data[i * 4 + 2];
            parts.advances[i] = data[i * 4 + 3];
        }
        
        return parts;
    }
    
    private native float[] getRadicalGlyphPartsNative(String fontPath, float targetHeight);
}
```

---

## 🔧 方案2：预提取字体数据（推荐）⭐

### 概述

使用 Python/FontTools 预先提取 MATH 表数据，打包到 assets 中。

### 优势
- ✅ 无需 JNI
- ✅ 纯 Java 实现
- ✅ 性能好（预计算）
- ✅ 易于调试

### 实现步骤

#### 1. Python 脚本提取数据

```python
# extract_math_data.py
from fontTools.ttLib import TTFont
import json

def extract_radical_data(font_path):
    font = TTFont(font_path)
    
    # 获取 MATH 表
    if 'MATH' not in font:
        print("Font doesn't have MATH table")
        return None
    
    math_table = font['MATH'].table
    
    # 根号的 glyph ID
    cmap = font.getBestCmap()
    radical_glyph_id = cmap[0x221A]  # √
    
    data = {
        'constants': {},
        'radical': {
            'glyph_id': radical_glyph_id,
            'variants': [],
            'parts': []
        }
    }
    
    # 提取常量
    constants = math_table.MathConstants
    data['constants'] = {
        'RadicalVerticalGap': constants.RadicalVerticalGap,
        'RadicalDisplayStyleVerticalGap': constants.RadicalDisplayStyleVerticalGap,
        'RadicalRuleThickness': constants.RadicalRuleThickness,
        'RadicalExtraAscender': constants.RadicalExtraAscender,
        'RadicalKernBeforeDegree': constants.RadicalKernBeforeDegree,
        'RadicalKernAfterDegree': constants.RadicalKernAfterDegree,
    }
    
    # 提取垂直变体
    if radical_glyph_id in math_table.MathGlyphInfo.MathGlyphConstruction.VertGlyphConstruction:
        vert_construction = math_table.MathGlyphInfo.MathGlyphConstruction.VertGlyphConstruction[radical_glyph_id]
        
        # 预制变体
        if hasattr(vert_construction, 'VariantCount') and vert_construction.VariantCount > 0:
            for variant in vert_construction.MathGlyphVariantRecord:
                glyph_name = font.getGlyphName(variant.VariantGlyph)
                # 获取字形的边界框
                glyph_set = font.getGlyphSet()
                bbox = glyph_set[glyph_name]._glyph.getBounds(glyph_set)
                
                data['radical']['variants'].append({
                    'glyph_id': variant.VariantGlyph,
                    'glyph_name': glyph_name,
                    'advance': variant.AdvanceMeasurement,
                    'bbox': bbox
                })
        
        # 组装部件
        if hasattr(vert_construction, 'GlyphAssembly') and vert_construction.GlyphAssembly:
            assembly = vert_construction.GlyphAssembly
            for part in assembly.PartRecords:
                glyph_name = font.getGlyphName(part.glyph)
                
                data['radical']['parts'].append({
                    'glyph_id': part.glyph,
                    'glyph_name': glyph_name,
                    'start_connector': part.StartConnectorLength,
                    'end_connector': part.EndConnectorLength,
                    'full_advance': part.FullAdvance,
                    'flags': part.PartFlags  # 0x0001 = extender (可重复)
                })
    
    return data

if __name__ == '__main__':
    import sys
    
    if len(sys.argv) < 2:
        print("Usage: python extract_math_data.py <font_path>")
        sys.exit(1)
    
    font_path = sys.argv[1]
    data = extract_radical_data(font_path)
    
    if data:
        output_path = 'latin_modern_math_data.json'
        with open(output_path, 'w') as f:
            json.dump(data, f, indent=2)
        print(f"Data extracted to {output_path}")
```

运行脚本：
```bash
pip install fonttools
python extract_math_data.py latinmodern-math.otf
```

#### 2. 生成的数据示例

```json
{
  "constants": {
    "RadicalVerticalGap": 50,
    "RadicalRuleThickness": 40,
    "RadicalExtraAscender": 80
  },
  "radical": {
    "glyph_id": 728,
    "variants": [
      {
        "glyph_id": 728,
        "glyph_name": "radical",
        "advance": 800,
        "bbox": [10, -50, 400, 950]
      },
      {
        "glyph_id": 2456,
        "glyph_name": "radical.size1",
        "advance": 1200,
        "bbox": [10, -50, 400, 1450]
      },
      {
        "glyph_id": 2457,
        "glyph_name": "radical.size2",
        "advance": 1600,
        "bbox": [10, -50, 400, 1950]
      }
    ],
    "parts": [
      {
        "glyph_id": 2458,
        "glyph_name": "radical.bottom",
        "start_connector": 0,
        "end_connector": 100,
        "full_advance": 400,
        "flags": 0
      },
      {
        "glyph_id": 2459,
        "glyph_name": "radical.extension",
        "start_connector": 100,
        "end_connector": 100,
        "full_advance": 300,
        "flags": 1
      },
      {
        "glyph_id": 2460,
        "glyph_name": "radical.top",
        "start_connector": 100,
        "end_connector": 0,
        "full_advance": 400,
        "flags": 0
      }
    ]
  }
}
```

#### 3. Java 加载和使用

```java
public class LatinModernMathData {
    private static final String DATA_FILE = "latin_modern_math_data.json";
    
    public static class MathConstants {
        public float radicalVerticalGap;
        public float radicalRuleThickness;
        public float radicalExtraAscender;
    }
    
    public static class GlyphVariant {
        public int glyphId;
        public String glyphName;
        public float advance;
        public RectF bbox;
    }
    
    public static class GlyphPart {
        public int glyphId;
        public String glyphName;
        public float startConnector;
        public float endConnector;
        public float fullAdvance;
        public boolean isExtender;  // flags & 0x0001
    }
    
    public static class RadicalData {
        public int baseGlyphId;
        public List<GlyphVariant> variants;
        public List<GlyphPart> parts;
    }
    
    public MathConstants constants;
    public RadicalData radical;
    
    public static LatinModernMathData load(Context context) {
        try {
            InputStream is = context.getAssets().open(DATA_FILE);
            String json = new String(readBytes(is), StandardCharsets.UTF_8);
            return parseJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static LatinModernMathData parseJson(String json) {
        // 使用 Gson 或手动解析
        Gson gson = new Gson();
        return gson.fromJson(json, LatinModernMathData.class);
    }
    
    /**
     * 选择合适的根号变体
     */
    public GlyphVariant selectRadicalVariant(float targetHeight) {
        for (GlyphVariant variant : radical.variants) {
            if (variant.advance >= targetHeight) {
                return variant;
            }
        }
        // 如果没有足够大的预制变体，返回最大的（后续用部件拼接）
        return radical.variants.get(radical.variants.size() - 1);
    }
    
    /**
     * 计算使用部件拼接时需要重复的次数
     */
    public int calculateExtenderCount(float targetHeight) {
        // 找出固定部件和可重复部件
        float fixedHeight = 0;
        float extenderHeight = 0;
        
        for (GlyphPart part : radical.parts) {
            if (part.isExtender) {
                extenderHeight = part.fullAdvance;
            } else {
                fixedHeight += part.fullAdvance;
            }
        }
        
        if (extenderHeight == 0) {
            return 0;
        }
        
        float remainingHeight = targetHeight - fixedHeight;
        return Math.max(0, (int) Math.ceil(remainingHeight / extenderHeight));
    }
}
```

#### 4. 渲染根号

```java
public class RadicalRenderer {
    private LatinModernMathData mathData;
    private Typeface mathFont;
    private Paint paint;
    
    public RadicalRenderer(Context context) {
        // 加载数学数据
        mathData = LatinModernMathData.load(context);
        
        // 加载字体
        mathFont = Typeface.createFromAsset(
            context.getAssets(),
            "fonts/latinmodern-math.otf"
        );
        
        paint = new Paint();
        paint.setTypeface(mathFont);
        paint.setAntiAlias(true);
    }
    
    public void renderRadical(Canvas canvas, float x, float y, 
                             float contentHeight, float fontSize) {
        paint.setTextSize(fontSize);
        
        // 1. 计算所需高度
        float gap = mathData.constants.radicalVerticalGap * fontSize / 1000f;
        float ruleThickness = mathData.constants.radicalRuleThickness * fontSize / 1000f;
        float extraAscender = mathData.constants.radicalExtraAscender * fontSize / 1000f;
        
        float targetHeight = contentHeight + gap + ruleThickness + extraAscender;
        
        // 2. 选择合适的变体
        LatinModernMathData.GlyphVariant variant = 
            mathData.selectRadicalVariant(targetHeight * 1000f / fontSize);
        
        if (variant.advance >= targetHeight * 1000f / fontSize) {
            // 使用预制变体
            renderPrebuiltRadical(canvas, x, y, variant, fontSize);
        } else {
            // 使用部件拼接
            renderAssembledRadical(canvas, x, y, targetHeight, fontSize);
        }
    }
    
    private void renderPrebuiltRadical(Canvas canvas, float x, float y,
                                      LatinModernMathData.GlyphVariant variant,
                                      float fontSize) {
        // 方式1: 使用字形名称（如果字体支持通过名称访问）
        // Android 不直接支持，需要通过字形 ID
        
        // 方式2: 缩放绘制
        canvas.save();
        
        // 计算缩放比例
        float scale = fontSize / 1000f;  // 假设 unitsPerEm = 1000
        canvas.scale(scale, scale, x, y);
        
        // 绘制字形（需要使用 drawGlyphs 或 drawTextRun）
        // Android 11+ 支持 drawGlyphs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int[] glyphIds = { variant.glyphId };
            float[] positions = { 0, 0 };
            canvas.drawGlyphs(glyphIds, 0, positions, 0, 1, 
                            mathFont, paint);
        } else {
            // 降级方案：使用 Unicode 字符
            canvas.drawText("√", x, y, paint);
        }
        
        canvas.restore();
    }
    
    private void renderAssembledRadical(Canvas canvas, float x, float y,
                                       float targetHeight, float fontSize) {
        float scale = fontSize / 1000f;
        float currentY = y;
        
        // 计算需要多少个扩展部件
        int extenderCount = mathData.calculateExtenderCount(
            targetHeight * 1000f / fontSize);
        
        // 绘制底部
        for (LatinModernMathData.GlyphPart part : mathData.radical.parts) {
            if (part.isExtender) {
                // 绘制多次扩展部件
                for (int i = 0; i < extenderCount; i++) {
                    drawGlyphPart(canvas, x, currentY, part, scale);
                    currentY -= part.fullAdvance * scale;
                }
            } else {
                // 绘制固定部件
                drawGlyphPart(canvas, x, currentY, part, scale);
                currentY -= part.fullAdvance * scale;
            }
        }
    }
    
    private void drawGlyphPart(Canvas canvas, float x, float y,
                              LatinModernMathData.GlyphPart part,
                              float scale) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            canvas.save();
            canvas.scale(scale, scale, x, y);
            
            int[] glyphIds = { part.glyphId };
            float[] positions = { 0, 0 };
            canvas.drawGlyphs(glyphIds, 0, positions, 0, 1,
                            mathFont, paint);
            
            canvas.restore();
        } else {
            // 降级方案
            // 可以预渲染每个部件到 Bitmap，或使用 Path
        }
    }
}
```

---

## 🔧 方案3：简化方案（Path绘制）

如果上述方案太复杂，可以使用简化版：

```java
public class SimplifiedRadicalRenderer {
    /**
     * 使用 Latin Modern Math 的根号字形作为参考
     * 但用 Path 手动绘制，保持相同的比例
     */
    public void renderRadical(Canvas canvas, float x, float y,
                             float contentHeight, float fontSize) {
        // 基于 Latin Modern Math 的实际比例
        float hookWidth = fontSize * 0.166f;      // 小勾宽度
        float hookHeight = fontSize * 0.4f;       // 小勾高度
        float surdWidth = fontSize * 0.25f;       // 主斜线宽度
        float thickness = fontSize * 0.06f;       // 线条粗细
        float extraHeight = fontSize * 0.1f;      // 额外高度
        
        Path path = new Path();
        
        // 小勾
        float x0 = x;
        float y0 = y;
        float x1 = x0 + hookWidth;
        float y1 = y0 - hookHeight;
        
        path.moveTo(x0, y0);
        path.lineTo(x1, y1);
        
        // 到底部
        float x2 = x1 + hookWidth * 0.3f;
        float y2 = y0 + contentHeight * 0.15f;
        path.lineTo(x2, y2);
        
        // 到顶部
        float x3 = x2 + surdWidth;
        float y3 = y0 - contentHeight - extraHeight;
        path.lineTo(x3, y3);
        
        // 上横线
        float x4 = x3 + contentHeight * 0.1f;  // 稍微延伸
        path.lineTo(x4, y3);
        
        // 绘制
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(thickness);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAntiAlias(true);
        
        canvas.drawPath(path, paint);
    }
}
```

---

## 📊 方案对比

| 方案 | 优点 | 缺点 | 推荐度 |
|------|------|------|--------|
| **HarfBuzz (JNI)** | 最精确、完整支持 | 需要编译 native 代码、复杂 | ⭐⭐⭐ |
| **预提取数据** | 纯 Java、易调试、性能好 | 需要预处理、依赖 Android 11+ | ⭐⭐⭐⭐⭐ |
| **Path 绘制** | 简单、通用 | 不如真实字形精确 | ⭐⭐⭐⭐ |

---

## 🎯 推荐方案

### 对于你的项目

**建议使用方案2（预提取数据）+ 方案3（Path 降级）**

```java
public class HybridRadicalRenderer {
    private LatinModernMathData mathData;
    
    public void renderRadical(Canvas canvas, float x, float y, 
                             float contentHeight, float fontSize) {
        // 尝试使用真实字形
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && mathData != null) {
            renderWithMathFont(canvas, x, y, contentHeight, fontSize);
        } else {
            // 降级到 Path 绘制
            renderWithPath(canvas, x, y, contentHeight, fontSize);
        }
    }
}
```

### 实施步骤

1. ✅ 下载 Latin Modern Math 字体
2. ✅ 运行 Python 脚本提取数据
3. ✅ 将 JSON 和字体放入 assets
4. ✅ 实现 Android 11+ 的字形渲染
5. ✅ 实现 Path 降级方案
6. ✅ 测试不同高度的根号

---

## 📚 参考资料

1. **OpenType MATH 规范** - https://docs.microsoft.com/en-us/typography/opentype/spec/math
2. **FontTools 文档** - https://fonttools.readthedocs.io/
3. **HarfBuzz 文档** - https://harfbuzz.github.io/
4. **Latin Modern Math** - http://www.gust.org.pl/projects/e-foundry/lm-math

---

需要我提供完整的可运行代码吗？包括 Python 提取脚本和 Android 实现？

