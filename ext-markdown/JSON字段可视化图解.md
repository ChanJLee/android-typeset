# JSON 配置文件字段可视化图解

**通过图示理解数学排版参数**

---

## 📐 根号渲染参数

### 基本根号结构

```
                 ┌─ RadicalExtraAscender (40)
                 │
        ─────────┴────────────  ← RadicalRuleThickness (40) - 横线粗细
        │                   │
        │  ← RadicalVerticalGap (50) - 根号与内容间距
        │                   │
    √   ├───────────────────┤  ← 内容 (content)
        │                   │
        └───────────────────┘
```

### 带根指数的根号

```
       RadicalKernBeforeDegree (+278) →  ← RadicalKernAfterDegree (-556)
                                │        │
                                ↓        ↓
                               ┌─┐      ┌┐
                                3       √  x
                               └─┘      └┘
                                ↑
                RadicalDegreeBottomRaisePercent (60%)
                根指数底部提升 60% 的内容高度
```

**完整示例**: ³√(x²+y²)

```
         278 fu        -556 fu
       ◄────────►◄────────────►
                  ↑60%
              ┌───┴───┐
              │   3   │        ─────────────  ← 横线 (40 fu)
              └───────┘       ↑ 50 fu gap
                        ┌─────┼─────────┐
                    √   │  x²+y²       │
                        └───────────────┘
```

---

## 📐 分数渲染参数

### 行内模式 (Inline Style)

```
         ← FractionNumeratorShiftUp (394) →
        │
        ↓
   ─────────────── (基线 baseline)
        │
        │  a + b     ← 分子
        │    ↑
        │    │ FractionNumeratorGapMin (40)
        │    ↓
        │  ─────    ← FractionRuleThickness (40) - 分数线
        │    ↑
        │    │ FractionDenominatorGapMin (40)
        │    ↓
        │  c + d     ← 分母
        │
        ↓
         ← FractionDenominatorShiftDown (345) →
```

### 显示模式 (Display Style)

```
         ← FractionNumeratorDisplayStyleShiftUp (677) →
        │
        ↓
   ─────────────── (基线)
        │
        │  a + b     ← 分子 (更高)
        │    ↑
        │    │ FractionNumDisplayStyleGapMin (120) - 更大间距
        │    ↓
        │  ─────    ← 分数线
        │    ↑
        │    │ FractionDenomDisplayStyleGapMin (120)
        │    ↓
        │  c + d     ← 分母 (更低)
        │
        ↓
         ← FractionDenominatorDisplayStyleShiftDown (686) →
```

**对比**:
- 显示模式的移动距离约是行内模式的 **1.7-2 倍**
- 显示模式的间距是行内模式的 **3 倍**

---

## 📐 上下标渲染参数

### 单独上标

```
        ↑
        │ SuperscriptShiftUp (363)
        │
   ─────┼───────── (基线)
        │
        x²
         ↑
         │ SuperscriptBottomMin (108)
         │ - 上标底部到基线的最小距离
```

### 单独下标

```
   ─────┬───────── (基线)
        │
        │ SubscriptShiftDown (247)
        │
        ↓
        xₙ
        ↑
        │ SubscriptTopMax (344)
        │ - 下标顶部到基线的最大距离
```

### 同时存在上下标

```
        ↑
        │ SuperscriptShiftUp (363)
        │
   ─────┼───────── (基线)
        │    x²   ← 上标
        │     ↑
        │     │ SubSuperscriptGapMin (160)
        │     │ - 确保不重叠
        │     ↓
        │    xₙ   ← 下标
        │
        │ SubscriptShiftDown (247)
        ↓
```

### 上标字号缩放

```
    原字号 48px
    │
    ├──► 上标/下标: 48 × 70% = 33.6px
    │              (ScriptPercentScaleDown = 70)
    │
    └──► 二级上下标: 48 × 50% = 24px
                    (ScriptScriptPercentScaleDown = 50)

示例:  x²ⁿ
       │└─► 二级 (50%)
       └──► 一级 (70%)
```

---

## 📐 数学轴线 (Axis)

```
        ↑
        │
   ─────┼───────── x 字符顶部
        │
        │ AxisHeight (250)
   ─────●───────── ← 数学轴线 (对称符号的中心)
        │            (+, -, =, × 等符号的中心线)
        │
   ─────┴───────── (基线 baseline)
```

**用途**:
- 对称运算符 (+, -, ×, ÷, =) 的垂直中心
- 大型运算符 (∑, ∏, ∫) 的对齐参考
- 括号等定界符的中心对齐

**示例**:

```
    a + b = c
      ↑   ↑
      │   └─ AxisHeight 中心
      └───── AxisHeight 中心

    ─────●───────── 轴线
    ─────────────── 基线
```

---

## 📐 完整公式示例

### 示例 1: 分数加根号

```
      ³√(x²+y²)
公式: ─────────
         2

可视化参数:
                     278        -556
                   ◄────►◄──────────►
                      ↑60%
                   ┌──┴──┐
                   │  3  │        ───────  ← 根号横线 (40)
                   └─────┘       ↑ 50 gap
           ┌───────────────┬─────┴────────┐
       √   │   x² + y²     │              │  ← 分子内容
           └───────────────┴──────────────┘
                ↑ 394 (分子上移)
   ──────────────────────────────────────── (基线)
                │ 40 gap
           ─────────────────  ← 分数线 (40)
                │ 40 gap
               2               ← 分母
                │ 345 (分母下移)
                ↓
```

### 示例 2: 求和公式

```
         n
        ∑  i²
       i=1

可视化:
            ↑ SuperscriptShiftUp
            │
       ─────●───────── AxisHeight (∑ 的中心)
            │   n      ← 上标上限
            │  ∑       ← 大型运算符
            │  i=1     ← 下标下限
       ─────┴───────── 基线
            │
            ↓ SubscriptShiftDown
```

---

## 📐 字体单位与像素转换图解

### 转换原理

```
    字体设计坐标系          渲染坐标系
    (Font Units)           (Pixels)

    1000 fu               48 px (fontSize)
    ├───────┤             ├────────┤
    │       │             │        │
    │   A   │   ────►     │   A    │
    │       │             │        │
    └───────┘             └────────┘
      ↑                     ↑
   unitsPerEm            fontSize
   (1000)                (48)

    转换公式:
    pixels = (fontUnits × fontSize) / unitsPerEm
           = (fontUnits × 48) / 1000
```

### 实例转换

```
RadicalVerticalGap = 50 fu

字号 12pt:  50 × 12 / 1000 = 0.6 px   │▌  (很小)
字号 16pt:  50 × 16 / 1000 = 0.8 px   ││
字号 24pt:  50 × 24 / 1000 = 1.2 px   │││
字号 48pt:  50 × 48 / 1000 = 2.4 px   ││││││
字号 96pt:  50 × 96 / 1000 = 4.8 px   ││││││││││││  (大)
```

---

## 📐 Display Style vs Inline Style

### 行内模式 (Inline)

```
文本中的公式 a/b 看起来像这样，不会打断行高。

   ─────────────────────────────────
   文字 文字 文字 a/b 文字 文字 文字
              ─
   ─────────────────────────────────
   文字 文字 文字 文字 文字 文字 文字
   ─────────────────────────────────

   使用较小的间距和移动距离
```

### 显示模式 (Display)

```
独立成行的公式会使用更大的间距:

                 a + b
                ───────
                 c + d

   ─────────────────────────────────
   
           更大的垂直空间
           更清晰的视觉效果
   
   ─────────────────────────────────

   使用较大的间距和移动距离
```

**参数对比**:

| 参数 | Inline | Display | 比例 |
|------|--------|---------|------|
| 分子上移 | 394 | 677 | 1.7x |
| 分母下移 | 345 | 686 | 2.0x |
| 分子间距 | 40 | 120 | 3.0x |
| 分母间距 | 40 | 120 | 3.0x |
| 根号间距 | 50 | 148 | 3.0x |

---

## 📐 字形 ID 系统图解

### Unicode vs GlyphId

```
Unicode 字符                字体内部字形
(通用标识)                  (特定于字体)

  U+03B1                   Glyph #4178
    ↓                          ↓
   'α'        ────────►        α
  (alpha)                  (Latin Modern Math
                            的 alpha 字形)

使用场景:
• Unicode: 文本处理、输入、存储
• GlyphId: 精确渲染 (Android S+)
```

### 为什么使用 GlyphId?

```
方法 1: drawText()
   "α" ──► [字体查找] ──► 字形 ──► 屏幕
          (可能不准确)

方法 2: drawGlyphs() (推荐)
   4178 ──────────────► 字形 ──► 屏幕
   (直接访问，精确)
```

---

## 📐 符号变体系统 (Variants & Parts)

### 括号大小变体

```
如果字体提供变体 (Latin Modern Math 大多未提供):

variants: [
  { glyphId: 9,   height: 100 },  // 小
  { glyphId: 10,  height: 200 },  // 中
  { glyphId: 11,  height: 400 },  // 大
  { glyphId: 12,  height: 800 }   // 特大
]

内容高度 = 150  ──► 选择 variant[1] (200)
内容高度 = 500  ──► 选择 variant[3] (800)
```

### 括号拼接部件

```
如果变体不够大，使用部件拼接:

parts: [
  { name: "top",     isExtender: false },  // 顶部
  { name: "extender", isExtender: true  },  // 可重复中间
  { name: "bottom",  isExtender: false }   // 底部
]

小括号:  (  = top + bottom
        (

大括号:  (  = top + extender + bottom
        (
        (

超大:   (  = top + extender × 3 + bottom
       (
       (
       (
       (
```

---

## 🎯 实用记忆技巧

### 1. "上移" 和 "下移" 都是正值

```
SuperscriptShiftUp (363)      → 正值，向上
SubscriptShiftDown (247)      → 正值，向下
FractionNumeratorShiftUp      → 正值，向上
FractionDenominatorShiftDown  → 正值，向下
```

### 2. "Gap" 都是间距

```
RadicalVerticalGap         → 垂直间距
FractionNumeratorGapMin    → 最小间距
SubSuperscriptGapMin       → 最小间距
```

### 3. "Thickness" 都是粗细

```
RadicalRuleThickness     → 40 (横线粗细)
FractionRuleThickness    → 40 (分数线粗细)
```

### 4. "Percent" 需要除以 100

```
RadicalDegreeBottomRaisePercent = 60
实际使用: 60 / 100 = 0.6 (60%)

ScriptPercentScaleDown = 70
实际字号: fontSize × 70 / 100 = fontSize × 0.7
```

---

## 📊 常用组合参数

### 渲染根号需要:

1. `RadicalVerticalGap` - 间距
2. `RadicalRuleThickness` - 横线粗细
3. `RadicalExtraAscender` - 额外高度
4. `radical.baseGlyphId` - 字形 ID

### 渲染分数需要:

1. `FractionNumeratorShiftUp` - 分子位置
2. `FractionDenominatorShiftDown` - 分母位置
3. `FractionRuleThickness` - 分数线粗细
4. `FractionNumeratorGapMin` - 上间距
5. `FractionDenominatorGapMin` - 下间距

### 渲染上下标需要:

1. `SuperscriptShiftUp` - 上标位置
2. `SubscriptShiftDown` - 下标位置
3. `ScriptPercentScaleDown` - 字号缩放
4. `SubSuperscriptGapMin` - (如果同时存在)

---

**详细说明**: 参见 `JSON配置文件字段说明.md`  
**快速参考**: 参见 `JSON字段快速参考.md`  
**更新时间**: 2025-10-15

