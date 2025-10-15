#!/usr/bin/env python3
"""
从 OpenType MATH 字体中提取根号等数学符号的信息

依赖：
    pip install fonttools

使用：
    python extract_math_data.py latinmodern-math.otf

输出：
    latin_modern_math_data.json
"""

from fontTools.ttLib import TTFont
import json
import sys

def extract_math_constants(math_table):
    """提取 MATH 常量"""
    constants = math_table.MathConstants
    
    # 辅助函数：将 MathValueRecord 转换为整数
    def to_int(value):
        if hasattr(value, 'Value'):
            return int(value.Value)
        return int(value) if value else 0
    
    return {
        # 根号相关
        'RadicalVerticalGap': to_int(constants.RadicalVerticalGap),
        'RadicalDisplayStyleVerticalGap': to_int(constants.RadicalDisplayStyleVerticalGap),
        'RadicalRuleThickness': to_int(constants.RadicalRuleThickness),
        'RadicalExtraAscender': to_int(constants.RadicalExtraAscender),
        'RadicalKernBeforeDegree': to_int(constants.RadicalKernBeforeDegree),
        'RadicalKernAfterDegree': to_int(constants.RadicalKernAfterDegree),
        'RadicalDegreeBottomRaisePercent': to_int(constants.RadicalDegreeBottomRaisePercent),
        
        # 分式相关
        'FractionNumeratorShiftUp': to_int(constants.FractionNumeratorShiftUp),
        'FractionNumeratorDisplayStyleShiftUp': to_int(constants.FractionNumeratorDisplayStyleShiftUp),
        'FractionDenominatorShiftDown': to_int(constants.FractionDenominatorShiftDown),
        'FractionDenominatorDisplayStyleShiftDown': to_int(constants.FractionDenominatorDisplayStyleShiftDown),
        'FractionNumeratorGapMin': to_int(constants.FractionNumeratorGapMin),
        'FractionNumDisplayStyleGapMin': to_int(constants.FractionNumDisplayStyleGapMin),
        'FractionRuleThickness': to_int(constants.FractionRuleThickness),
        'FractionDenominatorGapMin': to_int(constants.FractionDenominatorGapMin),
        'FractionDenomDisplayStyleGapMin': to_int(constants.FractionDenomDisplayStyleGapMin),
        
        # 上下标相关
        'SuperscriptShiftUp': to_int(constants.SuperscriptShiftUp),
        'SuperscriptShiftUpCramped': to_int(constants.SuperscriptShiftUpCramped),
        'SubscriptShiftDown': to_int(constants.SubscriptShiftDown),
        'SuperscriptBaselineDropMax': to_int(constants.SuperscriptBaselineDropMax),
        'SubscriptBaselineDropMin': to_int(constants.SubscriptBaselineDropMin),
        'SuperscriptBottomMin': to_int(constants.SuperscriptBottomMin),
        'SubscriptTopMax': to_int(constants.SubscriptTopMax),
        'SubSuperscriptGapMin': to_int(constants.SubSuperscriptGapMin),
        
        # 轴线
        'AxisHeight': to_int(constants.AxisHeight),
        
        # 字体单位
        'ScriptPercentScaleDown': to_int(constants.ScriptPercentScaleDown),
        'ScriptScriptPercentScaleDown': to_int(constants.ScriptScriptPercentScaleDown),
    }

def extract_glyph_variants(font, math_table, glyph_id, glyph_name):
    """提取字形的垂直变体"""
    variants = []
    
    try:
        # 检查是否有 MathGlyphInfo
        if not hasattr(math_table, 'MathGlyphInfo'):
            return variants
        
        # 检查是否有 MathGlyphConstruction
        if not hasattr(math_table.MathGlyphInfo, 'MathGlyphConstruction'):
            return variants
        
        # 检查是否有 VertGlyphConstruction
        if not hasattr(math_table.MathGlyphInfo.MathGlyphConstruction, 'VertGlyphConstruction'):
            return variants
        
        vert_construction = math_table.MathGlyphInfo.MathGlyphConstruction.VertGlyphConstruction.get(glyph_id)
        
        if not vert_construction:
            return variants
        
        # 预制变体
        if hasattr(vert_construction, 'VariantCount') and vert_construction.VariantCount > 0:
            glyph_set = font.getGlyphSet()
            
            for variant in vert_construction.MathGlyphVariantRecord:
                variant_name = font.getGlyphName(variant.VariantGlyph)
                
                # 获取字形边界
                try:
                    glyph = glyph_set[variant_name]
                    if hasattr(glyph, '_glyph'):
                        bbox = glyph._glyph.getBounds(glyph_set)
                    else:
                        bbox = [0, 0, 0, 0]
                except:
                    bbox = [0, 0, 0, 0]
                
                variants.append({
                    'glyphId': variant.VariantGlyph,
                    'glyphName': variant_name,
                    'advance': variant.AdvanceMeasurement,
                    'bbox': list(bbox) if bbox else [0, 0, 0, 0]
                })
    except Exception as e:
        print(f"Warning: Failed to extract variants for {glyph_name}: {e}")
    
    return variants

def extract_glyph_assembly(font, math_table, glyph_id, glyph_name):
    """提取字形的组装部件"""
    parts = []
    
    try:
        # 检查是否有 MathGlyphInfo
        if not hasattr(math_table, 'MathGlyphInfo'):
            return parts
        
        # 检查是否有 MathGlyphConstruction
        if not hasattr(math_table.MathGlyphInfo, 'MathGlyphConstruction'):
            return parts
        
        # 检查是否有 VertGlyphConstruction
        if not hasattr(math_table.MathGlyphInfo.MathGlyphConstruction, 'VertGlyphConstruction'):
            return parts
        
        vert_construction = math_table.MathGlyphInfo.MathGlyphConstruction.VertGlyphConstruction.get(glyph_id)
        
        if not vert_construction:
            return parts
        
        # 组装部件
        if hasattr(vert_construction, 'GlyphAssembly') and vert_construction.GlyphAssembly:
            assembly = vert_construction.GlyphAssembly
            
            for part in assembly.PartRecords:
                part_name = font.getGlyphName(part.glyph)
                
                parts.append({
                    'glyphId': part.glyph,
                    'glyphName': part_name,
                    'startConnector': part.StartConnectorLength,
                    'endConnector': part.EndConnectorLength,
                    'fullAdvance': part.FullAdvance,
                    'isExtender': bool(part.PartFlags & 0x0001)
                })
    except Exception as e:
        print(f"Warning: Failed to extract assembly for {glyph_name}: {e}")
    
    return parts

def extract_radical_data(font, math_table):
    """提取根号相关数据"""
    cmap = font.getBestCmap()
    
    if 0x221A not in cmap:
        print("Warning: Radical symbol (U+221A) not found in font")
        return None
    
    radical_glyph_name = cmap[0x221A]
    
    # 如果 cmap 返回的是字形名称，转换为 ID
    if isinstance(radical_glyph_name, str):
        radical_glyph_id = font.getGlyphID(radical_glyph_name)
    else:
        radical_glyph_id = radical_glyph_name
        radical_glyph_name = font.getGlyphName(radical_glyph_id)
    
    return {
        'baseGlyphId': radical_glyph_id,
        'baseGlyphName': radical_glyph_name,
        'variants': extract_glyph_variants(font, math_table, radical_glyph_id, radical_glyph_name),
        'parts': extract_glyph_assembly(font, math_table, radical_glyph_id, radical_glyph_name)
    }

def extract_left_paren_data(font, math_table):
    """提取左括号数据（示例）"""
    cmap = font.getBestCmap()
    
    if ord('(') not in cmap:
        return None
    
    paren_glyph_name = cmap[ord('(')]
    
    # 如果 cmap 返回的是字形名称，转换为 ID
    if isinstance(paren_glyph_name, str):
        paren_glyph_id = font.getGlyphID(paren_glyph_name)
    else:
        paren_glyph_id = paren_glyph_name
        paren_glyph_name = font.getGlyphName(paren_glyph_id)
    
    return {
        'baseGlyphId': paren_glyph_id,
        'baseGlyphName': paren_glyph_name,
        'variants': extract_glyph_variants(font, math_table, paren_glyph_id, paren_glyph_name),
        'parts': extract_glyph_assembly(font, math_table, paren_glyph_id, paren_glyph_name)
    }

def extract_symbol_data(font, math_table, unicode_char, symbol_name):
    """提取单个符号的数据"""
    cmap = font.getBestCmap()
    
    if unicode_char not in cmap:
        return None
    
    glyph_name = cmap[unicode_char]
    
    # 如果 cmap 返回的是字形名称，转换为 ID
    if isinstance(glyph_name, str):
        glyph_id = font.getGlyphID(glyph_name)
    else:
        glyph_id = glyph_name
        glyph_name = font.getGlyphName(glyph_id)
    
    return {
        'unicode': hex(unicode_char),
        'char': chr(unicode_char),
        'glyphId': glyph_id,
        'glyphName': glyph_name,
        'variants': extract_glyph_variants(font, math_table, glyph_id, glyph_name),
        'parts': extract_glyph_assembly(font, math_table, glyph_id, glyph_name)
    }

def extract_all_data(font_path):
    """提取所有数学数据"""
    print(f"Loading font: {font_path}")
    font = TTFont(font_path)
    
    if 'MATH' not in font:
        print("Error: Font doesn't have MATH table")
        return None
    
    math_table = font['MATH'].table
    cmap = font.getBestCmap()
    
    # 获取 unitsPerEm（用于缩放）
    units_per_em = font['head'].unitsPerEm
    
    print("Extracting math symbols...")
    
    # 定义要提取的符号
    symbols_to_extract = {
        # 希腊字母（小写）
        'greekLowercase': {
            'alpha': 0x03B1, 'beta': 0x03B2, 'gamma': 0x03B3, 'delta': 0x03B4,
            'epsilon': 0x03B5, 'zeta': 0x03B6, 'eta': 0x03B7, 'theta': 0x03B8,
            'iota': 0x03B9, 'kappa': 0x03BA, 'lambda': 0x03BB, 'mu': 0x03BC,
            'nu': 0x03BD, 'xi': 0x03BE, 'pi': 0x03C0, 'rho': 0x03C1,
            'sigma': 0x03C3, 'tau': 0x03C4, 'upsilon': 0x03C5, 'phi': 0x03C6,
            'chi': 0x03C7, 'psi': 0x03C8, 'omega': 0x03C9,
        },
        # 希腊字母（大写）
        'greekUppercase': {
            'Gamma': 0x0393, 'Delta': 0x0394, 'Theta': 0x0398, 'Lambda': 0x039B,
            'Xi': 0x039E, 'Pi': 0x03A0, 'Sigma': 0x03A3, 'Upsilon': 0x03A5,
            'Phi': 0x03A6, 'Psi': 0x03A8, 'Omega': 0x03A9,
        },
        # 基础运算符
        'basicOperators': {
            'plus': ord('+'), 'minus': ord('-'), 'times': 0x00D7, 'divide': 0x00F7,
            'equals': ord('='), 'plusminus': 0x00B1, 'minusplus': 0x2213,
            'cdot': 0x22C5, 'ast': 0x2217, 'star': 0x22C6,
        },
        # 关系符号
        'relations': {
            'lt': ord('<'), 'gt': ord('>'), 'le': 0x2264, 'ge': 0x2265,
            'leq': 0x2264, 'geq': 0x2265, 'equiv': 0x2261, 'approx': 0x2248,
            'neq': 0x2260, 'sim': 0x223C, 'cong': 0x2245, 'propto': 0x221D,
        },
        # 集合符号
        'setOperators': {
            'in': 0x2208, 'notin': 0x2209, 'subset': 0x2282, 'supset': 0x2283,
            'subseteq': 0x2286, 'supseteq': 0x2287, 'cup': 0x222A, 'cap': 0x2229,
            'emptyset': 0x2205, 'exists': 0x2203, 'forall': 0x2200,
        },
        # 箭头
        'arrows': {
            'leftarrow': 0x2190, 'rightarrow': 0x2192, 'uparrow': 0x2191, 'downarrow': 0x2193,
            'leftrightarrow': 0x2194, 'Leftarrow': 0x21D0, 'Rightarrow': 0x21D2,
            'Leftrightarrow': 0x21D4, 'mapsto': 0x21A6,
        },
        # 大型运算符
        'largeOperators': {
            'sum': 0x2211, 'prod': 0x220F, 'coprod': 0x2210,
            'int': 0x222B, 'iint': 0x222C, 'iiint': 0x222D, 'oint': 0x222E,
            'bigcup': 0x22C3, 'bigcap': 0x22C2, 'bigvee': 0x22C1, 'bigwedge': 0x22C0,
        },
        # 括号和定界符
        'delimiters': {
            'lparen': ord('('), 'rparen': ord(')'),
            'lbracket': ord('['), 'rbracket': ord(']'),
            'lbrace': ord('{'), 'rbrace': ord('}'),
            'langle': 0x27E8, 'rangle': 0x27E9,
            'lfloor': 0x230A, 'rfloor': 0x230B,
            'lceil': 0x2308, 'rceil': 0x2309,
            'vert': ord('|'), 'Vert': 0x2016,
        },
        # 其他常用符号
        'miscSymbols': {
            'infty': 0x221E, 'partial': 0x2202, 'nabla': 0x2207,
            'angle': 0x2220, 'parallel': 0x2225, 'perp': 0x22A5,
            'prime': 0x2032, 'hbar': 0x210F, 'ell': 0x2113,
            'radical': 0x221A,  # 根号
        },
        # 逻辑符号
        'logicSymbols': {
            'neg': 0x00AC, 'wedge': 0x2227, 'vee': 0x2228,
            'implies': 0x21D2, 'iff': 0x21D4,
        }
    }
    
    # 提取所有符号
    extracted_symbols = {}
    for category, symbols in symbols_to_extract.items():
        print(f"  Extracting {category}...")
        extracted_symbols[category] = {}
        for name, unicode_char in symbols.items():
            symbol_data = extract_symbol_data(font, math_table, unicode_char, name)
            if symbol_data:
                extracted_symbols[category][name] = symbol_data
    
    data = {
        'fontName': str(font['name'].getDebugName(1)),  # Family name
        'unitsPerEm': units_per_em,
        'constants': extract_math_constants(math_table),
        'symbols': extracted_symbols,
    }
    
    font.close()
    return data

def main():
    if len(sys.argv) < 2:
        print("Usage: python extract_math_data.py <font_path> [output_path]")
        print("\nExample:")
        print("  python extract_math_data.py latinmodern-math.otf")
        sys.exit(1)
    
    font_path = sys.argv[1]
    output_path = sys.argv[2] if len(sys.argv) > 2 else 'latin_modern_math_data.json'
    
    data = extract_all_data(font_path)
    
    if data:
        with open(output_path, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=2, ensure_ascii=False)
        
        print(f"\n✅ Data extracted successfully!")
        print(f"   Output: {output_path}")
        print(f"\n📊 Summary:")
        print(f"   Font: {data['fontName']}")
        print(f"   Units per em: {data['unitsPerEm']}")
        print(f"   Constants: {len(data['constants'])} entries")
        
        # 统计符号数量
        if 'symbols' in data:
            print(f"\n   Math Symbols:")
            total_symbols = 0
            for category, symbols in data['symbols'].items():
                count = len(symbols)
                total_symbols += count
                print(f"     - {category}: {count} symbols")
            print(f"   Total symbols: {total_symbols}")
            
            # 统计有变体的符号
            symbols_with_variants = 0
            symbols_with_parts = 0
            for category, symbols in data['symbols'].items():
                for name, sym_data in symbols.items():
                    if sym_data.get('variants'):
                        symbols_with_variants += 1
                    if sym_data.get('parts'):
                        symbols_with_parts += 1
            
            if symbols_with_variants > 0:
                print(f"   Symbols with variants: {symbols_with_variants}")
            if symbols_with_parts > 0:
                print(f"   Symbols with parts: {symbols_with_parts}")
    else:
        print("❌ Failed to extract data")
        sys.exit(1)

if __name__ == '__main__':
    main()

