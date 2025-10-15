# JSON 配置文件字段说明

**文件**: `latin_modern_math_data.json`  
**用途**: 为 Android 数学公式渲染提供字体度量和符号数据

---

## 📋 目录

1. [顶层字段](#顶层字段)
2. [Constants (数学常量)](#constants-数学常量)
3. [Symbols (数学符号)](#symbols-数学符号)
4. [Radical (根号数据)](#radical-根号数据)
5. [单位转换](#单位转换)
6. [使用示例](#使用示例)

---

## 顶层字段

### 📌 `fontName`
- **类型**: String
- **含义**: 字体名称
- **示例**: `"Latin Modern Math"`
- **用途**: 标识字体，用于日志和调试

### 📌 `unitsPerEm`
- **类型**: Integer
- **含义**: 每个 EM 单位的字体单位数（Font Units per EM）
- **示例**: `1000`
- **用途**: 将字体单位转换为像素的基准值
- **说明**: 这是字体设计的坐标系统。EM 是排版中的相对单位，等于当前字号大小

**转换公式**:
```
像素 = (字体单位 × 字号) / unitsPerEm
```

**示例**:
```java
// 假设 RadicalVerticalGap = 50 (字体单位)
// unitsPerEm = 1000
// fontSize = 48px
float pixels = (50 * 48) / 1000 = 2.4px
```

---

## Constants (数学常量)

所有常量的值都是**字体单位**（Font Units），需要通过 `unitsPerEm` 转换为像素。

### 🔸 根号相关 (Radical)

#### `RadicalVerticalGap`
- **值**: `50`
- **含义**: 根号符号顶部与被开方内容的垂直间距
- **用途**: 确保根号和内容之间有合适的空白
- **应用**: 在行内模式（inline style）下使用

```
   ┌─────────┐  ← RadicalVerticalGap
√  │ content │
```

#### `RadicalDisplayStyleVerticalGap`
- **值**: `148`
- **含义**: 显示模式下的根号垂直间距
- **用途**: 在独立显示的公式中使用，间距更大
- **应用**: 在 display style（如独立成行的公式）下使用

#### `RadicalRuleThickness`
- **值**: `40`
- **含义**: 根号上方横线的粗细
- **用途**: 绘制根号上方的横线（vinculum）

```
   ────────  ← RadicalRuleThickness (粗细)
√  content
```

#### `RadicalExtraAscender`
- **值**: `40`
- **含义**: 根号上方横线超出内容的额外高度
- **用途**: 让横线在顶部稍微延伸，视觉更好

```
────────────  ← 额外延伸
   ┌──────┐
√  │content│
```

#### `RadicalKernBeforeDegree`
- **值**: `278`
- **含义**: 根指数（如³√）之前的间距
- **用途**: 定位根指数的水平位置

```
  ³ ← RadicalKernBeforeDegree
 √ x
```

#### `RadicalKernAfterDegree`
- **值**: `-556` (负值表示回退)
- **含义**: 根指数之后的间距调整
- **用途**: 让根指数和根号靠近，视觉更紧凑

#### `RadicalDegreeBottomRaisePercent`
- **值**: `60`
- **含义**: 根指数底部相对于根号的提升百分比
- **用途**: 垂直定位根指数（如³√中的3）
- **单位**: 百分比（0-100）

```
   ³  ← 提升 60% 的高度
  √ x
```

### 🔸 分数相关 (Fraction)

#### `FractionNumeratorShiftUp`
- **值**: `394`
- **含义**: 分子向上移动的距离（行内模式）
- **用途**: 确定分子相对于基线的位置

```
  a  ← 向上移动 FractionNumeratorShiftUp
 ─
  b
```

#### `FractionNumeratorDisplayStyleShiftUp`
- **值**: `677`
- **含义**: 分子向上移动的距离（显示模式）
- **用途**: 独立显示的分数，移动距离更大

#### `FractionDenominatorShiftDown`
- **值**: `345`
- **含义**: 分母向下移动的距离（行内模式）
- **用途**: 确定分母相对于基线的位置

```
  a
 ─
  b  ← 向下移动 FractionDenominatorShiftDown
```

#### `FractionDenominatorDisplayStyleShiftDown`
- **值**: `686`
- **含义**: 分母向下移动的距离（显示模式）

#### `FractionNumeratorGapMin`
- **值**: `40`
- **含义**: 分子和分数线之间的最小间距（行内）
- **用途**: 防止分子和线条贴得太近

```
  a
    ← FractionNumeratorGapMin
 ─── 
  b
```

#### `FractionNumDisplayStyleGapMin`
- **值**: `120`
- **含义**: 分子和分数线的最小间距（显示模式）

#### `FractionRuleThickness`
- **值**: `40`
- **含义**: 分数线的粗细
- **用途**: 绘制分数线（fraction bar）

```
  a
 ─── ← FractionRuleThickness (粗细)
  b
```

#### `FractionDenominatorGapMin`
- **值**: `40`
- **含义**: 分母和分数线之间的最小间距（行内）

#### `FractionDenomDisplayStyleGapMin`
- **值**: `120`
- **含义**: 分母和分数线的最小间距（显示模式）

### 🔸 上下标相关 (Superscript/Subscript)

#### `SuperscriptShiftUp`
- **值**: `363`
- **含义**: 上标向上移动的距离
- **用途**: 定位上标（如 x²）

```
x² ← SuperscriptShiftUp
```

#### `SuperscriptShiftUpCramped`
- **值**: `289`
- **含义**: 紧凑模式下上标的移动距离
- **用途**: 在空间受限时使用，移动距离较小

#### `SubscriptShiftDown`
- **值**: `247`
- **含义**: 下标向下移动的距离
- **用途**: 定位下标（如 xₙ）

```
xₙ ← SubscriptShiftDown
```

#### `SuperscriptBaselineDropMax`
- **值**: `250`
- **含义**: 上标基线允许下降的最大距离
- **用途**: 限制上标的最低位置

#### `SubscriptBaselineDropMin`
- **值**: `200`
- **含义**: 下标基线允许上升的最小距离
- **用途**: 限制下标的最高位置

#### `SuperscriptBottomMin`
- **值**: `108`
- **含义**: 上标底部到基线的最小距离
- **用途**: 确保上标不会太低

#### `SubscriptTopMax`
- **值**: `344`
- **含义**: 下标顶部到基线的最大距离
- **用途**: 确保下标不会太高

#### `SubSuperscriptGapMin`
- **值**: `160`
- **含义**: 同时存在上下标时的最小间距
- **用途**: 确保上下标不重叠（如 xₙ²）

```
x² ← 上标
  ← SubSuperscriptGapMin
xₙ ← 下标
```

### 🔸 其他常量

#### `AxisHeight`
- **值**: `250`
- **含义**: 数学轴线的高度（从基线测量）
- **用途**: 对称符号（如 +、=）的中心线位置
- **说明**: 通常在小写字母 x 的中心高度附近

```
  +  ← AxisHeight (中心线)
─────── (基线)
```

#### `ScriptPercentScaleDown`
- **值**: `70`
- **含义**: 上标/下标的缩放百分比
- **用途**: 上标和下标使用正常字号的 70%
- **单位**: 百分比

**示例**:
```java
// 正常字号 48px
float scriptSize = 48 * 70 / 100 = 33.6px
```

#### `ScriptScriptPercentScaleDown`
- **值**: `50`
- **含义**: 二级上下标的缩放百分比
- **用途**: 上标的上标（如 x²³）使用 50%
- **单位**: 百分比

---

## Symbols (数学符号)

每个符号包含以下字段：

### 符号数据结构

```json
"alpha": {
  "unicode": "0x3b1",
  "char": "α",
  "glyphId": 4178,
  "glyphName": "alpha",
  "variants": [],
  "parts": []
}
```

#### `unicode`
- **类型**: String (十六进制)
- **含义**: Unicode 码点
- **示例**: `"0x3b1"` (α)
- **用途**: 通过 Unicode 查找符号

#### `char`
- **类型**: String (单字符)
- **含义**: 实际字符
- **示例**: `"α"`
- **用途**: 显示、调试、文本匹配

#### `glyphId`
- **类型**: Integer
- **含义**: 字体内部的字形 ID
- **示例**: `4178`
- **用途**: 
  - Android S+ 可以使用 `canvas.drawGlyphs()` 直接绘制
  - 比 Unicode 渲染更精确

#### `glyphName`
- **类型**: String
- **含义**: 字形名称（PostScript 名称）
- **示例**: `"alpha"`
- **用途**: 调试、字体编辑、文档

#### `variants`
- **类型**: Array
- **含义**: 该符号的不同大小变体
- **示例**: `[]` (大多数为空)
- **用途**: 
  - 对于括号、根号等可伸缩符号，提供预制的大、中、小版本
  - 根据内容高度选择合适的变体

**变体数据结构** (如果有):
```json
{
  "glyphId": 1234,
  "glyphName": "parenleft.size1",
  "advance": 500,
  "bbox": [10, -200, 100, 800]
}
```

- `advance`: 字符宽度（字体单位）
- `bbox`: 边界框 [left, bottom, right, top]

#### `parts`
- **类型**: Array
- **含义**: 用于拼接大型符号的部件
- **示例**: `[]` (大多数为空)
- **用途**: 
  - 当预制变体不够大时，通过拼接部件构造任意高度的符号
  - 如超大括号、根号

**部件数据结构** (如果有):
```json
{
  "glyphId": 5678,
  "glyphName": "parenleft.top",
  "startConnector": 100,
  "endConnector": 50,
  "fullAdvance": 200,
  "isExtender": false
}
```

- `startConnector`: 连接点位置（顶部）
- `endConnector`: 连接点位置（底部）
- `fullAdvance`: 部件高度
- `isExtender`: 是否是可重复的中间部分

### 符号分类

#### `greekLowercase` (23 个)
小写希腊字母: α, β, γ, δ, ε, ζ, η, θ, ι, κ, λ, μ, ν, ξ, π, ρ, σ, τ, υ, φ, χ, ψ, ω

#### `greekUppercase` (11 个)
大写希腊字母: Γ, Δ, Θ, Λ, Ξ, Π, Σ, Υ, Φ, Ψ, Ω

#### `basicOperators` (10 个)
基本运算符: +, -, ×, ÷, =, ±, ∓, ⋅, ∗, ⋆

#### `relations` (12 个)
关系符号: <, >, ≤, ≥, ≡, ≈, ≠, ∼, ≅, ∝

#### `setOperators` (11 个)
集合符号: ∈, ∉, ⊂, ⊃, ⊆, ⊇, ∪, ∩, ∅, ∃, ∀

#### `arrows` (9 个)
箭头: ←, →, ↑, ↓, ↔, ⇐, ⇒, ⇔, ↦

#### `largeOperators` (11 个)
大型运算符: ∑, ∏, ∐, ∫, ∬, ∭, ∮, ⋃, ⋂, ⋁, ⋀

#### `delimiters` (14 个)
定界符: (, ), [, ], {, }, ⟨, ⟩, ⌊, ⌋, ⌈, ⌉, |, ‖

#### `miscSymbols` (10 个)
其他符号: ∞, ∂, ∇, ∠, ∥, ⊥, ′, ℏ, ℓ, √

#### `logicSymbols` (5 个)
逻辑符号: ¬, ∧, ∨, ⇒, ⇔

---

## Radical (根号数据)

顶层的 `radical` 字段提供快速访问根号数据。

### 数据结构

```json
"radical": {
  "baseGlyphId": 3077,
  "baseGlyphName": "radical",
  "variants": [],
  "parts": []
}
```

#### `baseGlyphId`
- **类型**: Integer
- **含义**: 基础根号符号的字形 ID
- **示例**: `3077`
- **用途**: 使用 `drawGlyphs()` 绘制基础根号

#### `baseGlyphName`
- **类型**: String
- **含义**: 基础根号的字形名称
- **示例**: `"radical"`
- **用途**: 调试和文档

#### `variants`
- **类型**: Array
- **含义**: 不同高度的根号变体
- **当前值**: `[]` (Latin Modern Math 未提供)
- **用途**: 根据内容高度选择合适的根号大小

#### `parts`
- **类型**: Array
- **含义**: 用于拼接大型根号的部件
- **当前值**: `[]` (Latin Modern Math 未提供)
- **用途**: 构造任意高度的根号

**注意**: 由于 variants 和 parts 为空，渲染器会自动降级到：
- Unicode 字符 + 垂直拉伸
- 或 Path 手绘根号

---

## 单位转换

### 字体单位 → 像素

所有 `constants` 中的值都是**字体单位**，需要转换：

```java
/**
 * 将字体单位转换为像素
 * @param fontUnits 字体单位的值
 * @param fontSize 当前字号（像素）
 * @param unitsPerEm 每 EM 的字体单位数（通常是 1000）
 * @return 像素值
 */
public float toPixels(int fontUnits, float fontSize, int unitsPerEm) {
    return fontUnits * fontSize / unitsPerEm;
}
```

### 示例计算

假设：
- `fontSize = 48px`
- `unitsPerEm = 1000`
- `RadicalVerticalGap = 50`

```java
float gap = toPixels(50, 48, 1000);
// gap = 50 * 48 / 1000 = 2.4px
```

### 常见字号对应表

| 常量值 | 12pt | 16pt | 24pt | 48pt |
|--------|------|------|------|------|
| 50     | 0.6  | 0.8  | 1.2  | 2.4  |
| 100    | 1.2  | 1.6  | 2.4  | 4.8  |
| 250    | 3.0  | 4.0  | 6.0  | 12.0 |
| 500    | 6.0  | 8.0  | 12.0 | 24.0 |

*(单位: 像素, 基于 unitsPerEm=1000)*

---

## 使用示例

### 1. 读取常量

```java
// 加载 JSON
JSONObject root = new JSONObject(jsonString);
JSONObject constants = root.getJSONObject("constants");

// 读取常量（字体单位）
int radicalGap = constants.getInt("RadicalVerticalGap"); // 50

// 转换为像素
int unitsPerEm = root.getInt("unitsPerEm"); // 1000
float fontSize = 48f;
float gapPixels = radicalGap * fontSize / unitsPerEm; // 2.4px
```

### 2. 查找符号

```java
// 通过分类和名称查找
JSONObject symbols = root.getJSONObject("symbols");
JSONObject greekLower = symbols.getJSONObject("greekLowercase");
JSONObject alpha = greekLower.getJSONObject("alpha");

int glyphId = alpha.getInt("glyphId"); // 4178
String unicode = alpha.getString("unicode"); // "0x3b1"
String character = alpha.getString("char"); // "α"
```

### 3. 计算分数位置

```java
JSONObject constants = root.getJSONObject("constants");
float fontSize = 48f;
int unitsPerEm = 1000;

// 分子向上移动
int numShiftFU = constants.getInt("FractionNumeratorShiftUp");
float numShiftPx = numShiftFU * fontSize / unitsPerEm;

// 分数线粗细
int ruleThicknessFU = constants.getInt("FractionRuleThickness");
float ruleThicknessPx = ruleThicknessFU * fontSize / unitsPerEm;

// 分母向下移动
int denomShiftFU = constants.getInt("FractionDenominatorShiftDown");
float denomShiftPx = denomShiftFU * fontSize / unitsPerEm;
```

### 4. 计算上标位置

```java
int superShiftFU = constants.getInt("SuperscriptShiftUp");
float superShiftPx = superShiftFU * fontSize / unitsPerEm;

// 上标字号
int scalePercent = constants.getInt("ScriptPercentScaleDown");
float superSize = fontSize * scalePercent / 100; // 48 * 0.7 = 33.6px
```

### 5. 使用字形 ID 绘制 (Android S+)

```java
// 获取根号的字形 ID
JSONObject radical = root.getJSONObject("radical");
int glyphId = radical.getInt("baseGlyphId"); // 3077

// Android S (API 31)+ 可以直接绘制
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    int[] glyphIds = { glyphId };
    float[] positions = { x, y };
    
    canvas.drawGlyphs(
        glyphIds, 0,      // 字形 ID 数组
        positions, 0,     // 位置数组
        1,                // 字形数量
        font,             // Font 对象
        paint             // Paint
    );
}
```

---

## 📊 字段层级总览

```
latin_modern_math_data.json
├── fontName (String)
├── unitsPerEm (Integer)
├── constants (Object)
│   ├── RadicalVerticalGap (Integer)
│   ├── RadicalDisplayStyleVerticalGap (Integer)
│   ├── RadicalRuleThickness (Integer)
│   ├── RadicalExtraAscender (Integer)
│   ├── RadicalKernBeforeDegree (Integer)
│   ├── RadicalKernAfterDegree (Integer)
│   ├── RadicalDegreeBottomRaisePercent (Integer)
│   ├── FractionNumeratorShiftUp (Integer)
│   ├── FractionNumeratorDisplayStyleShiftUp (Integer)
│   ├── FractionDenominatorShiftDown (Integer)
│   ├── FractionDenominatorDisplayStyleShiftDown (Integer)
│   ├── FractionNumeratorGapMin (Integer)
│   ├── FractionNumDisplayStyleGapMin (Integer)
│   ├── FractionRuleThickness (Integer)
│   ├── FractionDenominatorGapMin (Integer)
│   ├── FractionDenomDisplayStyleGapMin (Integer)
│   ├── SuperscriptShiftUp (Integer)
│   ├── SuperscriptShiftUpCramped (Integer)
│   ├── SubscriptShiftDown (Integer)
│   ├── SuperscriptBaselineDropMax (Integer)
│   ├── SubscriptBaselineDropMin (Integer)
│   ├── SuperscriptBottomMin (Integer)
│   ├── SubscriptTopMax (Integer)
│   ├── SubSuperscriptGapMin (Integer)
│   ├── AxisHeight (Integer)
│   ├── ScriptPercentScaleDown (Integer)
│   └── ScriptScriptPercentScaleDown (Integer)
├── symbols (Object)
│   ├── greekLowercase (Object)
│   │   └── [symbol_name] (Object)
│   │       ├── unicode (String)
│   │       ├── char (String)
│   │       ├── glyphId (Integer)
│   │       ├── glyphName (String)
│   │       ├── variants (Array)
│   │       └── parts (Array)
│   ├── greekUppercase (Object)
│   ├── basicOperators (Object)
│   ├── relations (Object)
│   ├── setOperators (Object)
│   ├── arrows (Object)
│   ├── largeOperators (Object)
│   ├── delimiters (Object)
│   ├── miscSymbols (Object)
│   └── logicSymbols (Object)
└── radical (Object)
    ├── baseGlyphId (Integer)
    ├── baseGlyphName (String)
    ├── variants (Array)
    └── parts (Array)
```

---

## 🎯 快速参考

### 最常用的常量

| 常量 | 值 | 用途 |
|------|-----|------|
| `unitsPerEm` | 1000 | 单位转换基准 |
| `AxisHeight` | 250 | 符号中心线 |
| `RadicalVerticalGap` | 50 | 根号间距 |
| `RadicalRuleThickness` | 40 | 根号横线粗细 |
| `FractionRuleThickness` | 40 | 分数线粗细 |
| `SuperscriptShiftUp` | 363 | 上标位置 |
| `SubscriptShiftDown` | 247 | 下标位置 |
| `ScriptPercentScaleDown` | 70 | 上下标字号 |

### 单位转换公式

```
像素 = (字体单位 × 字号) ÷ 1000
```

---

**最后更新**: 2025-10-15  
**文件版本**: 1.0  
**适用于**: Latin Modern Math 字体配置

