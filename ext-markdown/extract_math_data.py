#!/usr/bin/env python3
"""
完整的 OTF 字体文件解析脚本
将 Latin Modern Math 字体的所有信息输出为 JSON
"""

from fontTools.ttLib import TTFont
import json
import sys
import unicodedata

def to_serializable(obj):
    """将 fontTools 的特殊类型转换为可序列化的 Python 原生类型"""
    # 处理 MathValueRecord
    if hasattr(obj, 'Value'):
        return to_serializable(obj.Value)
    
    # 处理 fontTools 的数值类型（Short, UShort, Fixed 等）
    if hasattr(obj, '__int__'):
        return int(obj)
    if hasattr(obj, '__float__'):
        return float(obj)
    
    # 处理列表
    if isinstance(obj, list):
        return [to_serializable(item) for item in obj]
    
    # 处理字典
    if isinstance(obj, dict):
        return {key: to_serializable(value) for key, value in obj.items()}
    
    # 已经是原生类型
    if isinstance(obj, (int, float, str, bool, type(None))):
        return obj
    
    # 尝试转换为字符串（最后的保险）
    try:
        return str(obj)
    except:
        return None

def safe_get(fn, *args, default=None):
    """安全地调用函数，出错时返回默认值"""
    try:
        return fn(*args)
    except Exception as e:
        print(f"Warning: {e}", file=sys.stderr)
        return default

def get_unicode_info(codepoint):
    """获取 Unicode 字符的额外信息"""
    try:
        char = chr(codepoint)
        info = {
            "name": None,
            "category": None,
            "block": None,
            "bidirectional": None
        }
        
        # 获取字符名称（如 "NOT NORMAL SUBGROUP OF"）
        try:
            info["name"] = unicodedata.name(char)
        except ValueError:
            info["name"] = None
        
        # 获取字符类别（如 Sm=数学符号, Lu=大写字母）
        try:
            info["category"] = unicodedata.category(char)
        except:
            pass
        
        # 获取双向文本类型
        try:
            info["bidirectional"] = unicodedata.bidirectional(char)
        except:
            pass
        
        # 尝试获取 Unicode block（需要额外判断）
        if codepoint <= 0x007F:
            info["block"] = "Basic Latin"
        elif codepoint <= 0x00FF:
            info["block"] = "Latin-1 Supplement"
        elif 0x0370 <= codepoint <= 0x03FF:
            info["block"] = "Greek and Coptic"
        elif 0x2000 <= codepoint <= 0x206F:
            info["block"] = "General Punctuation"
        elif 0x2070 <= codepoint <= 0x209F:
            info["block"] = "Superscripts and Subscripts"
        elif 0x20A0 <= codepoint <= 0x20CF:
            info["block"] = "Currency Symbols"
        elif 0x2100 <= codepoint <= 0x214F:
            info["block"] = "Letterlike Symbols"
        elif 0x2190 <= codepoint <= 0x21FF:
            info["block"] = "Arrows"
        elif 0x2200 <= codepoint <= 0x22FF:
            info["block"] = "Mathematical Operators"
        elif 0x2300 <= codepoint <= 0x23FF:
            info["block"] = "Miscellaneous Technical"
        elif 0x2500 <= codepoint <= 0x257F:
            info["block"] = "Box Drawing"
        elif 0x25A0 <= codepoint <= 0x25FF:
            info["block"] = "Geometric Shapes"
        elif 0x2600 <= codepoint <= 0x26FF:
            info["block"] = "Miscellaneous Symbols"
        elif 0x27C0 <= codepoint <= 0x27EF:
            info["block"] = "Miscellaneous Mathematical Symbols-A"
        elif 0x2980 <= codepoint <= 0x29FF:
            info["block"] = "Miscellaneous Mathematical Symbols-B"
        elif 0x2A00 <= codepoint <= 0x2AFF:
            info["block"] = "Supplemental Mathematical Operators"
        elif 0x1D400 <= codepoint <= 0x1D7FF:
            info["block"] = "Mathematical Alphanumeric Symbols"
        
        return info
    except:
        return None

def extract_font_info(font_path):
    """提取字体的完整信息"""
    font = TTFont(font_path)
    result = {
        "metadata": {},
        "glyphs": {},
        "cmap": {},
        "math_table": {},
        "horizontal_variants": {},
        "vertical_variants": {}
    }
    
    # ===== 1. 基本元数据 =====
    if "name" in font:
        name_table = font["name"]
        result["metadata"]["font_names"] = {
            record.nameID: record.toUnicode()
            for record in name_table.names
            if record.platformID == 3  # Windows平台
        }
    
    if "head" in font:
        head = font["head"]
        result["metadata"]["head"] = {
            "unitsPerEm": to_serializable(head.unitsPerEm),
            "created": str(head.created),
            "modified": str(head.modified),
            "fontRevision": to_serializable(head.fontRevision),
        }
    
    if "hhea" in font:
        hhea = font["hhea"]
        result["metadata"]["hhea"] = {
            "ascent": to_serializable(hhea.ascent),
            "descent": to_serializable(hhea.descent),
            "lineGap": to_serializable(hhea.lineGap),
        }
    
    # ===== 2. 字形列表和Unicode映射 (cmap) =====
    glyph_order = font.getGlyphOrder()
    result["metadata"]["total_glyphs"] = len(glyph_order)
    
    # 创建字形名到Unicode的映射字典
    glyph_to_unicode = {}
    
    # 获取Unicode映射
    if "cmap" in font:
        # 选择最佳的cmap表（通常是format 4或12）
        for table in font["cmap"].tables:
            if table.isUnicode():
                for codepoint, glyph_name in table.cmap.items():
                    char = chr(codepoint)
                    unicode_extra_info = get_unicode_info(codepoint)
                    
                    # 保存到 cmap 部分
                    cmap_entry = {
                        "unicode": f"U+{codepoint:04X}",
                        "codepoint": codepoint,
                        "char": char if codepoint >= 32 else "",  # 跳过控制字符
                        "name": glyph_name
                    }
                    
                    # 添加 Unicode 额外信息
                    if unicode_extra_info:
                        cmap_entry["unicode_name"] = unicode_extra_info["name"]
                        cmap_entry["category"] = unicode_extra_info["category"]
                        cmap_entry["block"] = unicode_extra_info["block"]
                        if unicode_extra_info["bidirectional"]:
                            cmap_entry["bidirectional"] = unicode_extra_info["bidirectional"]
                    
                    result["cmap"][glyph_name] = cmap_entry
                    
                    # 同时保存到映射字典，供后续使用
                    glyph_to_unicode[glyph_name] = cmap_entry
                break
    
    # ===== 3. MATH 表信息（重点） =====
    if "MATH" not in font:
        print("警告：此字体没有 MATH 表", file=sys.stderr)
        return result
        
    math_table = font["MATH"].table
    
    # 3.1 MathConstants - 数学常量
    if hasattr(math_table, "MathConstants"):
        constants = {}
        for attr in dir(math_table.MathConstants):
            if not attr.startswith("_"):
                value = getattr(math_table.MathConstants, attr, None)
                if value is not None and not callable(value):
                    # 转换为可序列化类型
                    constants[attr] = to_serializable(value)
        result["math_table"]["constants"] = constants
    
    # 3.2 MathVariants - 字形变体和拼装信息
    if hasattr(math_table, "MathVariants"):
        variants_table = math_table.MathVariants
        
        # 检查表结构
        has_vert_glyph_coverage = hasattr(variants_table, "VertGlyphCoverage")
        has_horiz_glyph_coverage = hasattr(variants_table, "HorizGlyphCoverage")
        
        for glyph in glyph_order:
            glyph_info = {}
            
            # ===== 垂直变体和拼装 =====
            if has_vert_glyph_coverage:
                # 检查字形是否在垂直覆盖范围内
                try:
                    if glyph in variants_table.VertGlyphCoverage.glyphs:
                        # 获取垂直构造
                        vert_construction = variants_table.VertGlyphConstruction
                        if glyph in vert_construction:
                            construction = vert_construction[glyph]
                            
                            # 垂直拼装
                            if hasattr(construction, "GlyphAssembly") and construction.GlyphAssembly:
                                assembly = construction.GlyphAssembly
                                glyph_info["vert_assembly"] = {
                                    "italic_correction": to_serializable(getattr(assembly, "ItalicCorrection", 0)),
                                    "parts": [
                                        {
                                            "glyph": part.glyph,
                                            "is_extender": bool(part.PartFlags & 1),
                                            "start_connector": to_serializable(part.StartConnectorLength),
                                            "end_connector": to_serializable(part.EndConnectorLength),
                                            "full_advance": to_serializable(part.FullAdvance),
                                        }
                                        for part in assembly.PartRecords
                                    ]
                                }
                            
                            # 预设的垂直大小变体
                            if hasattr(construction, "MathGlyphVariantRecord"):
                                variants = construction.MathGlyphVariantRecord
                                if variants:
                                    glyph_info["vert_variants"] = [
                                        {
                                            "glyph": v.VariantGlyph,
                                            "advance": to_serializable(v.AdvanceMeasurement)
                                        }
                                        for v in variants
                                    ]
                except Exception as e:
                    print(f"垂直变体错误 {glyph}: {e}", file=sys.stderr)
            
            # ===== 水平变体和拼装 =====
            if has_horiz_glyph_coverage:
                try:
                    if glyph in variants_table.HorizGlyphCoverage.glyphs:
                        # 获取水平构造
                        horiz_construction = variants_table.HorizGlyphConstruction
                        if glyph in horiz_construction:
                            construction = horiz_construction[glyph]
                            
                            # 水平拼装
                            if hasattr(construction, "GlyphAssembly") and construction.GlyphAssembly:
                                assembly = construction.GlyphAssembly
                                glyph_info["horiz_assembly"] = {
                                    "italic_correction": to_serializable(getattr(assembly, "ItalicCorrection", 0)),
                                    "parts": [
                                        {
                                            "glyph": part.glyph,
                                            "is_extender": bool(part.PartFlags & 1),
                                            "start_connector": to_serializable(part.StartConnectorLength),
                                            "end_connector": to_serializable(part.EndConnectorLength),
                                            "full_advance": to_serializable(part.FullAdvance),
                                        }
                                        for part in assembly.PartRecords
                                    ]
                                }
                            
                            # 预设的水平大小变体
                            if hasattr(construction, "MathGlyphVariantRecord"):
                                variants = construction.MathGlyphVariantRecord
                                if variants:
                                    glyph_info["horiz_variants"] = [
                                        {
                                            "glyph": v.VariantGlyph,
                                            "advance": to_serializable(v.AdvanceMeasurement)
                                        }
                                        for v in variants
                                    ]
                except Exception as e:
                    print(f"水平变体错误 {glyph}: {e}", file=sys.stderr)
            
            if glyph_info:
                result["glyphs"][glyph] = glyph_info
    
    # 3.3 MathGlyphInfo - 字形信息
    if hasattr(math_table, "MathGlyphInfo"):
        glyph_info_table = math_table.MathGlyphInfo
        
        # ItalicCorrection
        if hasattr(glyph_info_table, "MathItalicsCorrectionInfo"):
            italic_info = glyph_info_table.MathItalicsCorrectionInfo
            if hasattr(italic_info, "Coverage") and hasattr(italic_info, "ItalicsCorrection"):
                for glyph in glyph_order:
                    if glyph in italic_info.Coverage.glyphs:
                        idx = italic_info.Coverage.glyphs.index(glyph)
                        if idx < len(italic_info.ItalicsCorrection):
                            if glyph not in result["glyphs"]:
                                result["glyphs"][glyph] = {}
                            result["glyphs"][glyph]["italic_correction"] = to_serializable(italic_info.ItalicsCorrection[idx])
        
        # TopAccentAttachment
        if hasattr(glyph_info_table, "MathTopAccentAttachment"):
            accent_info = glyph_info_table.MathTopAccentAttachment
            if hasattr(accent_info, "TopAccentCoverage") and hasattr(accent_info, "TopAccentAttachment"):
                for glyph in glyph_order:
                    if glyph in accent_info.TopAccentCoverage.glyphs:
                        idx = accent_info.TopAccentCoverage.glyphs.index(glyph)
                        if idx < len(accent_info.TopAccentAttachment):
                            if glyph not in result["glyphs"]:
                                result["glyphs"][glyph] = {}
                            result["glyphs"][glyph]["top_accent_attachment"] = to_serializable(accent_info.TopAccentAttachment[idx])
        
        # ExtendedShape - 标记是否为扩展形状
        if hasattr(glyph_info_table, "ExtendedShapeCoverage"):
            extended_glyphs = glyph_info_table.ExtendedShapeCoverage.glyphs
            result["math_table"]["extended_shape_glyphs"] = list(extended_glyphs)
    
    # ===== 4. 字形度量信息 =====
    if "hmtx" in font:
        hmtx = font["hmtx"]
        for glyph in glyph_order:
            if glyph in hmtx.metrics:
                width, lsb = hmtx.metrics[glyph]
                if glyph not in result["glyphs"]:
                    result["glyphs"][glyph] = {}
                result["glyphs"][glyph]["metrics"] = {
                    "advance_width": to_serializable(width),
                    "left_side_bearing": to_serializable(lsb)
                }
    
    # ===== 5. 为所有字形添加 Unicode 和额外信息 =====
    for glyph in result["glyphs"].keys():
        if glyph in glyph_to_unicode:
            # 在字形信息的开头添加 unicode 信息（放在最前面）
            unicode_info = glyph_to_unicode[glyph]
            glyph_data = {
                "glyph_name": glyph,
                "unicode": unicode_info.get("unicode"),
                "char": unicode_info.get("char", ""),
            }
            
            # 添加 Unicode 名称和分类信息
            if unicode_info.get("unicode_name"):
                glyph_data["unicode_name"] = unicode_info["unicode_name"]
            if unicode_info.get("category"):
                glyph_data["category"] = unicode_info["category"]
            if unicode_info.get("block"):
                glyph_data["block"] = unicode_info["block"]
            
            # 合并原有信息
            glyph_data.update(result["glyphs"][glyph])
            result["glyphs"][glyph] = glyph_data
        else:
            # 如果没有 Unicode 映射，只添加字形名称
            result["glyphs"][glyph] = {
                "glyph_name": glyph,
                "unicode": None,
                "char": "",
                **result["glyphs"][glyph]
            }
    
    # ===== 6. 特殊：水平/垂直可伸缩字符汇总 =====
    result["horizontal_variants"]["summary"] = {}
    result["vertical_variants"]["summary"] = {}
    
    for glyph, info in result["glyphs"].items():
        if "horiz_variants" in info or "horiz_assembly" in info:
            result["horizontal_variants"]["summary"][glyph] = {
                "has_variants": "horiz_variants" in info,
                "has_assembly": "horiz_assembly" in info,
                "unicode": info.get("unicode"),
                "char": info.get("char", ""),
                "unicode_name": info.get("unicode_name")
            }
        
        if "vert_variants" in info or "vert_assembly" in info:
            result["vertical_variants"]["summary"][glyph] = {
                "has_variants": "vert_variants" in info,
                "has_assembly": "vert_assembly" in info,
                "unicode": info.get("unicode"),
                "char": info.get("char", ""),
                "unicode_name": info.get("unicode_name")
            }
    
    return result

def main():
    if len(sys.argv) < 2:
        print("用法: python extract_math_data.py <字体文件路径> [输出JSON文件]")
        print("示例: python extract_math_data.py latinmodern-math.otf output.json")
        sys.exit(1)
    
    font_path = sys.argv[1]
    output_path = sys.argv[2] if len(sys.argv) > 2 else None
    
    print(f"正在解析字体文件: {font_path}")
    result = extract_font_info(font_path)
    
    json_output = json.dumps(result, indent=2, ensure_ascii=False)
    
    if output_path:
        with open(output_path, 'w', encoding='utf-8') as f:
            f.write(json_output)
        print(f"✅ 解析完成！已保存到: {output_path}")
        print(f"   - 总字形数: {result['metadata']['total_glyphs']}")
        print(f"   - Unicode映射数: {len(result['cmap'])}")
        print(f"   - 有变体的字形数: {len(result['glyphs'])}")
        print(f"   - 水平可伸缩字符: {len(result['horizontal_variants']['summary'])}")
        print(f"   - 垂直可伸缩字符: {len(result['vertical_variants']['summary'])}")
    else:
        print(json_output)

if __name__ == "__main__":
    main()