from fontTools.ttLib import TTFont
from fontTools.pens.boundsPen import BoundsPen
import unicodedata
import json
import sys

def get_glyph_bbox(font, glyph_name):
    """使用 BoundsPen 计算字形的真实包围盒"""
    try:
        glyph_set = font.getGlyphSet()
        glyph = glyph_set[glyph_name]
        pen = BoundsPen(glyph_set)
        glyph.draw(pen)
        if pen.bounds:
            return list(map(float, pen.bounds))  # (xMin, yMin, xMax, yMax)
    except Exception:
        pass
    return None

def parse_font_to_json(font_path, output_path="font_info.json"):
    font = TTFont(font_path)
    cmap = font["cmap"].getBestCmap()
    hmtx = font["hmtx"]
    glyph_set = font.getGlyphSet()

    result = []

    for code, glyph_name in cmap.items():
        char = chr(code)

        # 分类
        category_name = unicodedata.category(char)


        # 宽度、高度、bbox
        width, lsb = hmtx[glyph_name]
        bbox = get_glyph_bbox(font, glyph_name)

        e = {"name": glyph_name,
            "unicode": f"U+{code:04X}",
                        "char": char,
                        "category": category_name,
                        "width": width,
                        "bbox": bbox
        }
        print(e)

        result.append(e)

    # 提取 MATH 表的附加信息
    if "MATH" in font:
        math_table = font["MATH"].table
        for record in result:
            glyph_name = record["name"]
            variants, topAccent = [], None

            # 垂直变体
            try:
                vertCov = getattr(math_table, "VertGlyphCoverage", None)
                vertConstr = getattr(math_table, "VertGlyphConstruction", None)
                if vertCov and vertConstr and glyph_name in vertCov.glyphs:
                    i = vertCov.glyphs.index(glyph_name)
                    variants = [v.VariantGlyph for v in vertConstr[i].Variants]
            except Exception:
                pass

            # Top accent
            try:
                topAcc = getattr(math_table, "TopAccentAttachment", None)
                if topAcc and topAcc.Coverage and glyph_name in topAcc.Coverage.glyphs:
                    i = topAcc.Coverage.glyphs.index(glyph_name)
                    topAccent = topAcc.TopAccentAttachment[i].Value
            except Exception:
                pass

            record["VertVariant"] = variants
            record["TopAccentAttachment"] = topAccent

    # 输出 JSON
    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(result, f, ensure_ascii=False, indent=2)

    print(f"✅ 字体解析完成，结果保存至：{output_path}")
    return result

# 命令行执行
if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("用法: python parse_font.py your_font.otf")
    else:
        parse_font_to_json(sys.argv[1])
