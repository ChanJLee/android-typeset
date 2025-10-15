# JSON 配置文件字段快速参考

**一页速查表** - `latin_modern_math_data.json`

---

## 🔄 单位转换

```java
像素 = (字体单位 × 字号) ÷ unitsPerEm
例: (50 × 48) ÷ 1000 = 2.4px
```

---

## 📦 顶层字段

| 字段 | 类型 | 值 | 说明 |
|------|------|-----|------|
| `fontName` | String | "Latin Modern Math" | 字体名称 |
| `unitsPerEm` | Int | 1000 | 转换基准值 |
| `constants` | Object | - | 27 个数学常量 |
| `symbols` | Object | - | 116 个符号，10 分类 |
| `radical` | Object | - | 根号专用数据 |

---

## 🔢 Constants 速查表

### 根号 (Radical)

| 字段 | 值 | 说明 |
|------|-----|------|
| `RadicalVerticalGap` | 50 | 根号与内容间距 |
| `RadicalDisplayStyleVerticalGap` | 148 | 显示模式间距 |
| `RadicalRuleThickness` | 40 | 横线粗细 |
| `RadicalExtraAscender` | 40 | 横线额外延伸 |
| `RadicalKernBeforeDegree` | 278 | 根指数前间距 |
| `RadicalKernAfterDegree` | -556 | 根指数后间距 |
| `RadicalDegreeBottomRaisePercent` | 60 | 根指数提升% |

**示意图**:
```
     3  ← RadicalDegreeBottomRaisePercent (60%)
  ────  ← RadicalRuleThickness (40)
 √  x   ← RadicalVerticalGap (50)
```

### 分数 (Fraction)

| 字段 | 值 | 说明 |
|------|-----|------|
| `FractionNumeratorShiftUp` | 394 | 分子上移 |
| `FractionNumeratorDisplayStyleShiftUp` | 677 | 显示模式分子上移 |
| `FractionDenominatorShiftDown` | 345 | 分母下移 |
| `FractionDenominatorDisplayStyleShiftDown` | 686 | 显示模式分母下移 |
| `FractionNumeratorGapMin` | 40 | 分子最小间距 |
| `FractionNumDisplayStyleGapMin` | 120 | 显示模式分子间距 |
| `FractionRuleThickness` | 40 | 分数线粗细 |
| `FractionDenominatorGapMin` | 40 | 分母最小间距 |
| `FractionDenomDisplayStyleGapMin` | 120 | 显示模式分母间距 |

**示意图**:
```
  a  ← FractionNumeratorShiftUp (394)
    ← FractionNumeratorGapMin (40)
 ─── ← FractionRuleThickness (40)
    ← FractionDenominatorGapMin (40)
  b  ← FractionDenominatorShiftDown (345)
```

### 上下标 (Script)

| 字段 | 值 | 说明 |
|------|-----|------|
| `SuperscriptShiftUp` | 363 | 上标上移 |
| `SuperscriptShiftUpCramped` | 289 | 紧凑模式上标 |
| `SubscriptShiftDown` | 247 | 下标下移 |
| `SuperscriptBaselineDropMax` | 250 | 上标基线最大下降 |
| `SubscriptBaselineDropMin` | 200 | 下标基线最小上升 |
| `SuperscriptBottomMin` | 108 | 上标底部最小距离 |
| `SubscriptTopMax` | 344 | 下标顶部最大距离 |
| `SubSuperscriptGapMin` | 160 | 上下标最小间距 |
| `ScriptPercentScaleDown` | 70 | 上下标缩放 (70%) |
| `ScriptScriptPercentScaleDown` | 50 | 二级上下标 (50%) |

**示意图**:
```
x²  ← SuperscriptShiftUp (363)
   ← SubSuperscriptGapMin (160)
xₙ  ← SubscriptShiftDown (247)
```

### 其他

| 字段 | 值 | 说明 |
|------|-----|------|
| `AxisHeight` | 250 | 数学轴线高度 |

---

## 🔤 Symbols 结构

```json
"alpha": {
  "unicode": "0x3b1",     // Unicode 码点
  "char": "α",            // 实际字符
  "glyphId": 4178,        // 字形 ID (用于 drawGlyphs)
  "glyphName": "alpha",   // 字形名称
  "variants": [],         // 大小变体 (通常为空)
  "parts": []            // 拼接部件 (通常为空)
}
```

### 符号分类

| 分类 | 数量 | 示例 |
|------|------|------|
| `greekLowercase` | 23 | α β γ δ ε... |
| `greekUppercase` | 11 | Γ Δ Θ Λ Ξ... |
| `basicOperators` | 10 | + - × ÷ =... |
| `relations` | 12 | < > ≤ ≥ ≡... |
| `setOperators` | 11 | ∈ ∉ ⊂ ⊃ ∪... |
| `arrows` | 9 | ← → ↑ ↓ ⇐... |
| `largeOperators` | 11 | ∑ ∏ ∫ ∬ ∭... |
| `delimiters` | 14 | ( ) [ ] { }... |
| `miscSymbols` | 10 | ∞ ∂ ∇ √ ℏ... |
| `logicSymbols` | 5 | ¬ ∧ ∨ ⇒ ⇔ |

---

## 🔍 Radical 字段

```json
"radical": {
  "baseGlyphId": 3077,      // 基础根号字形 ID
  "baseGlyphName": "radical", // 字形名称
  "variants": [],            // 大小变体 (空)
  "parts": []               // 拼接部件 (空)
}
```

---

## 💻 代码示例

### 读取常量并转换

```java
JSONObject root = new JSONObject(jsonString);
int unitsPerEm = root.getInt("unitsPerEm"); // 1000

JSONObject constants = root.getJSONObject("constants");
int gapFU = constants.getInt("RadicalVerticalGap"); // 50

float fontSize = 48f;
float gapPx = gapFU * fontSize / unitsPerEm; // 2.4px
```

### 查找符号

```java
JSONObject symbols = root.getJSONObject("symbols");
JSONObject alpha = symbols
    .getJSONObject("greekLowercase")
    .getJSONObject("alpha");

int glyphId = alpha.getInt("glyphId"); // 4178
String chr = alpha.getString("char");   // "α"
```

### 绘制字形 (Android S+)

```java
JSONObject radical = root.getJSONObject("radical");
int glyphId = radical.getInt("baseGlyphId");

if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    canvas.drawGlyphs(
        new int[]{glyphId}, 0,
        new float[]{x, y}, 0,
        1, font, paint
    );
}
```

### 计算分数位置

```java
float fontSize = 48f;
int unitsPerEm = 1000;

// 分子
int numShift = constants.getInt("FractionNumeratorShiftUp");
float numY = baselineY - (numShift * fontSize / unitsPerEm);

// 分数线
int ruleThick = constants.getInt("FractionRuleThickness");
float rulePx = ruleThick * fontSize / unitsPerEm;

// 分母
int denomShift = constants.getInt("FractionDenominatorShiftDown");
float denomY = baselineY + (denomShift * fontSize / unitsPerEm);
```

---

## 📊 常用值对照表 (unitsPerEm=1000)

| 字体单位 | 12pt | 16pt | 24pt | 48pt |
|---------|------|------|------|------|
| 50      | 0.6  | 0.8  | 1.2  | 2.4  |
| 100     | 1.2  | 1.6  | 2.4  | 4.8  |
| 250     | 3.0  | 4.0  | 6.0  | 12.0 |
| 363     | 4.4  | 5.8  | 8.7  | 17.4 |
| 500     | 6.0  | 8.0  | 12.0 | 24.0 |

*(单位: 像素)*

---

## 🎯 关键要点

1. **所有 constants 的值都是字体单位**，需要除以 `unitsPerEm` 转换
2. **字号越大，实际像素值越大** (成正比)
3. **Display Style 的值通常是 Inline Style 的 2-3 倍**
4. **上下标字号** = 原字号 × `ScriptPercentScaleDown` / 100
5. **负值表示反向** (如 `RadicalKernAfterDegree = -556`)
6. **百分比常量** (如 `RadicalDegreeBottomRaisePercent`) 不需要转换

---

**详细文档**: 参见 `JSON配置文件字段说明.md`  
**更新时间**: 2025-10-15

