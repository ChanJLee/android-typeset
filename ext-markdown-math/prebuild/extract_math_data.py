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

        e = {"c": char, "bbox": bbox}

        if (category_name.startswith("P") or category_name.startswith("S") or category_name == "Lo"):
            if bbox:
                if char == "\\" or char == "\"":
                    print("\t\tall.put(\"%s\", new Symbol(\"\\%s\", %sf, %sf, %sf, %sf));\n" % (glyph_name, char, bbox[0], bbox[1], bbox[2], bbox[3]))
                else:
                    print("\t\tall.put(\"%s\", new Symbol(\"%s\", %sf, %sf, %sf, %sf));\n" % (glyph_name, char, bbox[0], bbox[1], bbox[2], bbox[3]))
                result.append(e)
            else:
                print("\t\t// missing bbox: %s %s\n" % (glyph_name, char))

    # 输出 JSON
    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(result, f, ensure_ascii=False, indent=2)

    print(f"✅ 字体解析完成，结果保存至：{output_path}")
    return result

# 命令行执行
if __name__ == "__main__":
    print("""package me.chan.texas.ext.markdown.math.renderer.fonts;

             import java.util.HashMap;
             import java.util.Map;

             public class %s {\n
           	    public final Map<String, Symbol> all = new HashMap<>();
           	    public %s() {""" % (sys.argv[2], sys.argv[2]))
    parse_font_to_json(sys.argv[1], "%s.json" % sys.argv[2])
    print("""}
    }""")