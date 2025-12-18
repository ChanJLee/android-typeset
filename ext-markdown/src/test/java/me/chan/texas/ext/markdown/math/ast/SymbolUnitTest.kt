package me.chan.texas.ext.markdown.math.ast

import org.junit.Assert.*
import org.junit.Test

import me.chan.texas.ext.markdown.math.renderer.fonts.MathFontOptions
import me.chan.texas.ext.markdown.math.renderer.fonts.SymbolOptions

/**
 * 符号映射单元测试
 * 验证 MathFontOptions 中 AST 映射的符号引用是否正确
 * 基于 bnf_math.txt 定义
 */
class SymbolUnitTest {

    private val symbolOptions = SymbolOptions()

    // ============================================================
    // 辅助方法
    // ============================================================

    /**
     * 验证符号名能在 SymbolOptions 中找到
     */
    private fun assertSymbolExists(symbolName: String, description: String = "") {
        val symbol = symbolOptions.all[symbolName]
        assertNotNull("符号 '$symbolName' 应该存在 ($description)", symbol)
        println("✅ $symbolName -> ${symbol?.unicode} ($description)")
    }

    /**
     * 验证 AST 映射的符号能找到
     */
    private fun assertAstMappingExists(astKey: String, expectedSymbolName: String, description: String = "") {
        val symbol = MathFontOptions.symbol(expectedSymbolName)
        assertNotNull("AST '$astKey' 映射到 '$expectedSymbolName' 应该存在 ($description)", symbol)
        println("✅ \\$astKey -> $expectedSymbolName -> ${symbol?.unicode} ($description)")
    }

    // ============================================================
    // 大型运算符符号测试
    // ============================================================

    @Test
    fun test_LargeOperators_sum() {
        assertSymbolExists("summation", "\\sum")
    }

    @Test
    fun test_LargeOperators_prod() {
        assertSymbolExists("product", "\\prod")
    }

    @Test
    fun test_LargeOperators_coprod() {
        assertSymbolExists("uni2210", "\\coprod")
    }

    @Test
    fun test_LargeOperators_int() {
        assertSymbolExists("integral", "\\int")
    }

    @Test
    fun test_LargeOperators_iint() {
        assertSymbolExists("uni222C", "\\iint")
    }

    @Test
    fun test_LargeOperators_iiint() {
        assertSymbolExists("uni222D", "\\iiint")
    }

    @Test
    fun test_LargeOperators_oint() {
        assertSymbolExists("contourintegral", "\\oint")
    }

    @Test
    fun test_LargeOperators_oiint() {
        assertSymbolExists("uni222F", "\\oiint")
    }

    @Test
    fun test_LargeOperators_oiiint() {
        assertSymbolExists("uni2230", "\\oiiint")
    }

    @Test
    fun test_LargeOperators_bigcup() {
        assertSymbolExists("uni22C3", "\\bigcup")
    }

    @Test
    fun test_LargeOperators_bigcap() {
        assertSymbolExists("uni22C2", "\\bigcap")
    }

    @Test
    fun test_LargeOperators_bigvee() {
        assertSymbolExists("uni22C1", "\\bigvee")
    }

    @Test
    fun test_LargeOperators_bigwedge() {
        assertSymbolExists("uni22C0", "\\bigwedge")
    }

    @Test
    fun test_LargeOperators_bigoplus() {
        assertSymbolExists("uni2A01", "\\bigoplus")
    }

    @Test
    fun test_LargeOperators_bigotimes() {
        assertSymbolExists("uni2A02", "\\bigotimes")
    }

    @Test
    fun test_LargeOperators_bigodot() {
        assertSymbolExists("uni2A00", "\\bigodot")
    }

    @Test
    fun test_LargeOperators_biguplus() {
        assertSymbolExists("uni2A04", "\\biguplus")
    }

    @Test
    fun test_LargeOperators_bigsqcup() {
        assertSymbolExists("uni2A06", "\\bigsqcup")
    }

    // ============================================================
    // 二元运算符 - 基本算术
    // ============================================================

    @Test
    fun test_BinaryOperators_plus() {
        assertSymbolExists("plus", "+")
    }

    @Test
    fun test_BinaryOperators_minus() {
        assertSymbolExists("minus", "-")
    }

    @Test
    fun test_BinaryOperators_multiply() {
        assertSymbolExists("multiply", "* / \\times")
    }

    @Test
    fun test_BinaryOperators_divide() {
        assertSymbolExists("divide", "/ / \\div")
    }

    @Test
    fun test_BinaryOperators_comma() {
        assertSymbolExists("comma", ",")
    }

    @Test
    fun test_BinaryOperators_cdot() {
        assertSymbolExists("uni22C5", "\\cdot")
    }

    @Test
    fun test_BinaryOperators_plusminus() {
        assertSymbolExists("plusminus", "\\pm")
    }

    @Test
    fun test_BinaryOperators_minusplus() {
        assertSymbolExists("minusplus", "\\mp")
    }

    // ============================================================
    // 二元运算符 - 关系
    // ============================================================

    @Test
    fun test_RelationalOperators_equal() {
        assertSymbolExists("equal", "=")
    }

    @Test
    fun test_RelationalOperators_notequal() {
        assertSymbolExists("notequal", "\\neq")
    }

    @Test
    fun test_RelationalOperators_equivalence() {
        assertSymbolExists("equivalence", "\\equiv")
    }

    @Test
    fun test_RelationalOperators_approxequal() {
        assertSymbolExists("approxequal", "\\approx")
    }

    @Test
    fun test_RelationalOperators_cong() {
        assertSymbolExists("uni2245", "\\cong")
    }

    @Test
    fun test_RelationalOperators_similar() {
        assertSymbolExists("similar", "\\sim")
    }

    @Test
    fun test_RelationalOperators_less() {
        assertSymbolExists("less", "<")
    }

    @Test
    fun test_RelationalOperators_greater() {
        assertSymbolExists("greater", ">")
    }

    @Test
    fun test_RelationalOperators_lessequal() {
        assertSymbolExists("lessequal", "\\le / \\leq")
    }

    @Test
    fun test_RelationalOperators_greaterequal() {
        assertSymbolExists("greaterequal", "\\ge / \\geq")
    }

    @Test
    fun test_RelationalOperators_lessmuch() {
        assertSymbolExists("lessmuch", "\\ll")
    }

    @Test
    fun test_RelationalOperators_greatermuch() {
        assertSymbolExists("greatermuch", "\\gg")
    }

    // ============================================================
    // 二元运算符 - 集合
    // ============================================================

    @Test
    fun test_SetOperators_element() {
        assertSymbolExists("element", "\\in")
    }

    @Test
    fun test_SetOperators_notin() {
        assertSymbolExists("uni2209", "\\notin")
    }

    @Test
    fun test_SetOperators_propersubset() {
        assertSymbolExists("propersubset", "\\subset")
    }

    @Test
    fun test_SetOperators_propersuperset() {
        assertSymbolExists("propersuperset", "\\supset")
    }

    @Test
    fun test_SetOperators_reflexsubset() {
        assertSymbolExists("reflexsubset", "\\subseteq")
    }

    @Test
    fun test_SetOperators_reflexsuperset() {
        assertSymbolExists("reflexsuperset", "\\supseteq")
    }

    @Test
    fun test_SetOperators_union() {
        assertSymbolExists("union", "\\cup")
    }

    @Test
    fun test_SetOperators_intersection() {
        assertSymbolExists("intersection", "\\cap")
    }

    @Test
    fun test_SetOperators_logicaland() {
        assertSymbolExists("logicaland", "\\wedge")
    }

    @Test
    fun test_SetOperators_logicalor() {
        assertSymbolExists("logicalor", "\\vee")
    }

    // ============================================================
    // 二元运算符 - 箭头
    // ============================================================

    @Test
    fun test_ArrowOperators_arrowright() {
        assertSymbolExists("arrowright", "\\to / \\rightarrow")
    }

    @Test
    fun test_ArrowOperators_arrowleft() {
        assertSymbolExists("arrowleft", "\\leftarrow")
    }

    @Test
    fun test_ArrowOperators_arrowboth() {
        assertSymbolExists("arrowboth", "\\leftrightarrow")
    }

    @Test
    fun test_ArrowOperators_arrowdblright() {
        assertSymbolExists("arrowdblright", "\\Rightarrow / \\implies")
    }

    @Test
    fun test_ArrowOperators_arrowdblleft() {
        assertSymbolExists("arrowdblleft", "\\Leftarrow")
    }

    @Test
    fun test_ArrowOperators_arrowdblboth() {
        assertSymbolExists("arrowdblboth", "\\Leftrightarrow / \\iff")
    }

    // ============================================================
    // 二元运算符 - 几何
    // ============================================================

    @Test
    fun test_GeometryOperators_perp() {
        assertSymbolExists("uni27C2", "\\perp")
    }

    @Test
    fun test_GeometryOperators_parallel() {
        assertSymbolExists("parallel", "\\parallel")
    }

    @Test
    fun test_GeometryOperators_divides() {
        assertSymbolExists("divides", "| / \\mid")
    }

    // ============================================================
    // 特殊符号
    // ============================================================

    @Test
    fun test_SpecialSymbols_infinity() {
        assertSymbolExists("infinity", "\\infty")
    }

    @Test
    fun test_SpecialSymbols_partialdiff() {
        assertSymbolExists("partialdiff", "\\partial")
    }

    @Test
    fun test_SpecialSymbols_nabla() {
        assertSymbolExists("nabla", "\\nabla")
    }

    @Test
    fun test_SpecialSymbols_emptyset() {
        assertSymbolExists("emptyset", "\\emptyset / \\varnothing")
    }

    @Test
    fun test_SpecialSymbols_universal() {
        assertSymbolExists("universal", "\\forall")
    }

    @Test
    fun test_SpecialSymbols_existential() {
        assertSymbolExists("existential", "\\exists")
    }

    @Test
    fun test_SpecialSymbols_nexists() {
        assertSymbolExists("uni2204", "\\nexists")
    }

    @Test
    fun test_SpecialSymbols_ellipsis() {
        assertSymbolExists("ellipsis", "\\dots / \\ldots")
    }

    @Test
    fun test_SpecialSymbols_cdots() {
        assertSymbolExists("uni22EF", "\\cdots")
    }

    @Test
    fun test_SpecialSymbols_vdots() {
        assertSymbolExists("uni22EE", "\\vdots")
    }

    @Test
    fun test_SpecialSymbols_ddots() {
        assertSymbolExists("uni22F1", "\\ddots")
    }

    @Test
    fun test_SpecialSymbols_therefore() {
        assertSymbolExists("therefore", "\\therefore")
    }

    @Test
    fun test_SpecialSymbols_because() {
        assertSymbolExists("because", "\\because")
    }

    @Test
    fun test_SpecialSymbols_angle() {
        assertSymbolExists("uni2220", "\\angle")
    }

    // ============================================================
    // 特殊变量符号
    // ============================================================

    @Test
    fun test_SpecialVariables_hbar() {
        assertSymbolExists("uni210F", "\\hbar")
    }

    @Test
    fun test_SpecialVariables_ell() {
        assertSymbolExists("uni2113", "\\ell")
    }

    @Test
    fun test_SpecialVariables_weierstrass() {
        assertSymbolExists("weierstrass", "\\wp")
    }

    @Test
    fun test_SpecialVariables_Rfraktur() {
        assertSymbolExists("Rfraktur", "\\Re")
    }

    @Test
    fun test_SpecialVariables_Ifraktur() {
        assertSymbolExists("Ifraktur", "\\Im")
    }

    @Test
    fun test_SpecialVariables_aleph() {
        assertSymbolExists("aleph", "\\aleph")
    }

    // ============================================================
    // 定界符
    // ============================================================

    @Test
    fun test_Delimiters_parenleft() {
        assertSymbolExists("parenleft", "(")
    }

    @Test
    fun test_Delimiters_parenright() {
        assertSymbolExists("parenright", ")")
    }

    @Test
    fun test_Delimiters_bracketleft() {
        assertSymbolExists("bracketleft", "[")
    }

    @Test
    fun test_Delimiters_bracketright() {
        assertSymbolExists("bracketright", "]")
    }

    @Test
    fun test_Delimiters_braceleft() {
        assertSymbolExists("braceleft", "{")
    }

    @Test
    fun test_Delimiters_braceright() {
        assertSymbolExists("braceright", "}")
    }

    @Test
    fun test_Delimiters_angleleft() {
        assertSymbolExists("angleleft", "\\langle")
    }

    @Test
    fun test_Delimiters_angleright() {
        assertSymbolExists("angleright", "\\rangle")
    }

    @Test
    fun test_Delimiters_lfloor() {
        assertSymbolExists("uni230A", "\\lfloor")
    }

    @Test
    fun test_Delimiters_rfloor() {
        assertSymbolExists("uni230B", "\\rfloor")
    }

    @Test
    fun test_Delimiters_lceil() {
        assertSymbolExists("uni2308", "\\lceil")
    }

    @Test
    fun test_Delimiters_rceil() {
        assertSymbolExists("uni2309", "\\rceil")
    }

    // ============================================================
    // 可伸缩符号（用于括号等）
    // ============================================================

    @Test
    fun test_Stretchy_bracketTop() {
        assertSymbolExists("uni23A1", "[ top")
    }

    @Test
    fun test_Stretchy_bracketMid() {
        assertSymbolExists("uni23A2", "[ mid")
    }

    @Test
    fun test_Stretchy_bracketBottom() {
        assertSymbolExists("uni23A3", "[ bottom")
    }

    @Test
    fun test_Stretchy_bracketRightTop() {
        assertSymbolExists("uni23A4", "] top")
    }

    @Test
    fun test_Stretchy_bracketRightMid() {
        assertSymbolExists("uni23A5", "] mid")
    }

    @Test
    fun test_Stretchy_bracketRightBottom() {
        assertSymbolExists("uni23A6", "] bottom")
    }

    @Test
    fun test_Stretchy_braceTop() {
        assertSymbolExists("uni23A7", "{ top")
    }

    @Test
    fun test_Stretchy_braceMid() {
        assertSymbolExists("uni23A8", "{ mid")
    }

    @Test
    fun test_Stretchy_braceBottom() {
        assertSymbolExists("uni23A9", "{ bottom")
    }

    @Test
    fun test_Stretchy_braceExt() {
        assertSymbolExists("uni23AA", "{ ext")
    }

    @Test
    fun test_Stretchy_braceRightTop() {
        assertSymbolExists("uni23AB", "} top")
    }

    @Test
    fun test_Stretchy_braceRightMid() {
        assertSymbolExists("uni23AC", "} mid")
    }

    @Test
    fun test_Stretchy_braceRightBottom() {
        assertSymbolExists("uni23AD", "} bottom")
    }

    // ============================================================
    // 根号
    // ============================================================

    @Test
    fun test_Radical() {
        assertSymbolExists("radical", "\\sqrt")
    }

    // ============================================================
    // 综合测试 - 验证所有 AST 映射
    // ============================================================

    @Test
    fun test_AllAstMappings() {
        println("\n=== 综合验证所有 AST 映射 ===")

        // 大型运算符
        val largeOperators = mapOf(
            "sum" to "summation",
            "prod" to "product",
            "coprod" to "uni2210",
            "int" to "integral",
            "iint" to "uni222C",
            "iiint" to "uni222D",
            "oint" to "contourintegral",
            "oiint" to "uni222F",
            "oiiint" to "uni2230",
            "bigcup" to "uni22C3",
            "bigcap" to "uni22C2",
            "bigvee" to "uni22C1",
            "bigwedge" to "uni22C0",
            "bigoplus" to "uni2A01",
            "bigotimes" to "uni2A02",
            "bigodot" to "uni2A00",
            "biguplus" to "uni2A04",
            "bigsqcup" to "uni2A06"
        )

        // 二元运算符
        val binaryOperators = mapOf(
            "+" to "plus",
            "-" to "minus",
            "*" to "multiply",
            "/" to "divide",
            "," to "comma",
            "times" to "multiply",
            "cdot" to "uni22C5",
            "div" to "divide",
            "pm" to "plusminus",
            "mp" to "minusplus",
            "=" to "equal",
            "neq" to "notequal",
            "equiv" to "equivalence",
            "approx" to "approxequal",
            "cong" to "uni2245",
            "sim" to "similar",
            "<" to "less",
            ">" to "greater",
            "le" to "lessequal",
            "ge" to "greaterequal",
            "leq" to "lessequal",
            "geq" to "greaterequal",
            "ll" to "lessmuch",
            "gg" to "greatermuch",
            "in" to "element",
            "notin" to "uni2209",
            "subset" to "propersubset",
            "supset" to "propersuperset",
            "subseteq" to "reflexsubset",
            "supseteq" to "reflexsuperset",
            "cup" to "union",
            "cap" to "intersection",
            "wedge" to "logicaland",
            "vee" to "logicalor",
            "to" to "arrowright",
            "rightarrow" to "arrowright",
            "leftarrow" to "arrowleft",
            "leftrightarrow" to "arrowboth",
            "Rightarrow" to "arrowdblright",
            "Leftarrow" to "arrowdblleft",
            "Leftrightarrow" to "arrowdblboth",
            "implies" to "arrowdblright",
            "iff" to "arrowdblboth",
            "perp" to "uni27C2",
            "parallel" to "parallel",
            "|" to "divides",
            "mid" to "divides"
        )

        // 特殊符号
        val specialSymbols = mapOf(
            "infty" to "infinity",
            "partial" to "partialdiff",
            "nabla" to "nabla",
            "emptyset" to "emptyset",
            "varnothing" to "emptyset",
            "forall" to "universal",
            "exists" to "existential",
            "nexists" to "uni2204",
            "dots" to "ellipsis",
            "ldots" to "ellipsis",
            "cdots" to "uni22EF",
            "vdots" to "uni22EE",
            "ddots" to "uni22F1",
            "therefore" to "therefore",
            "because" to "because",
            "angle" to "uni2220",
            "hbar" to "uni210F",
            "ell" to "uni2113",
            "wp" to "weierstrass",
            "Re" to "Rfraktur",
            "Im" to "Ifraktur",
            "aleph" to "aleph"
        )

        // 定界符
        val delimiters = mapOf(
            "langle" to "angleleft",
            "rangle" to "angleright",
            "lfloor" to "uni230A",
            "rfloor" to "uni230B",
            "lceil" to "uni2308",
            "rceil" to "uni2309"
        )

        var passCount = 0
        var failCount = 0
        val failures = mutableListOf<String>()

        val allMappings = largeOperators + binaryOperators + specialSymbols + delimiters

        for ((key, symbolName) in allMappings) {
            val symbol = symbolOptions.all[symbolName]
            if (symbol != null) {
                passCount++
                println("✅ $key -> $symbolName -> ${symbol.unicode}")
            } else {
                failCount++
                failures.add("$key -> $symbolName")
                println("❌ $key -> $symbolName (NOT FOUND)")
            }
        }

        println("\n=== 测试结果 ===")
        println("通过: $passCount")
        println("失败: $failCount")

        if (failures.isNotEmpty()) {
            println("\n失败列表:")
            failures.forEach { println("  - $it") }
        }

        assertEquals("所有 AST 映射都应该能找到对应符号", 0, failCount)
    }

    // ============================================================
    // 测试 MathFontOptions 的静态方法
    // ============================================================

    @Test
    fun test_MathFontOptions_symbol() {
        // 测试 MathFontOptions.symbol() 方法
        assertNotNull("summation 应该存在", MathFontOptions.symbol("summation"))
        assertNotNull("integral 应该存在", MathFontOptions.symbol("integral"))
        assertNotNull("arrowright 应该存在", MathFontOptions.symbol("arrowright"))
        assertNotNull("infinity 应该存在", MathFontOptions.symbol("infinity"))
    }

    @Test
    fun test_MathFontOptions_ast_SymbolAtom() {
        // 测试 SymbolAtom 的符号查找
        val plusAtom = SymbolAtom("+")
        assertNotNull("+ 应该能找到符号", MathFontOptions.ast(plusAtom))

        val timesAtom = SymbolAtom("\\times")
        assertNotNull("\\times 应该能找到符号", MathFontOptions.ast(timesAtom))

        val toAtom = SymbolAtom("\\to")
        assertNotNull("\\to 应该能找到符号", MathFontOptions.ast(toAtom))
    }

    @Test
    fun test_MathFontOptions_ast_LargeOperatorAtom() {
        // 测试 LargeOperatorAtom 的符号查找
        val sumAtom = LargeOperatorAtom("sum")
        assertNotNull("sum 应该能找到符号", MathFontOptions.ast(sumAtom))

        val intAtom = LargeOperatorAtom("int")
        assertNotNull("int 应该能找到符号", MathFontOptions.ast(intAtom))

        val limAtom = LargeOperatorAtom("lim")
        // lim 用文本渲染，不是符号
        assertNull("lim 应该返回 null (使用文本渲染)", MathFontOptions.ast(limAtom))
    }

    @Test
    fun test_MathFontOptions_textOp() {
        // 测试希腊字母的文本渲染
        assertEquals("α", MathFontOptions.textOp("alpha"))
        assertEquals("β", MathFontOptions.textOp("beta"))
        assertEquals("γ", MathFontOptions.textOp("gamma"))
        assertEquals("Γ", MathFontOptions.textOp("Gamma"))
        assertEquals("Δ", MathFontOptions.textOp("Delta"))

        // 测试特殊符号（在 GREEK_LETTERS 中但不是真正的希腊字母）
        assertEquals("∞", MathFontOptions.textOp("infty"))

        // 测试函数名
        assertEquals("sin", MathFontOptions.textOp("sin"))
        assertEquals("cos", MathFontOptions.textOp("cos"))
        assertEquals("lim", MathFontOptions.textOp("lim"))
    }

    @Test
    fun test_MathFontOptions_ast_GreekLetterVariableAtom() {
        // 测试所有希腊字母（包括 infty）通过 GreekLetterVariableAtom 能正确获取文本
        val greekLetters = listOf(
            "alpha", "beta", "gamma", "delta", "epsilon", "varepsilon",
            "zeta", "eta", "theta", "vartheta", "iota", "kappa",
            "lambda", "mu", "nu", "xi", "pi", "varpi", "rho", "varrho",
            "sigma", "varsigma", "tau", "upsilon", "phi", "varphi",
            "chi", "psi", "omega",
            "Gamma", "Delta", "Theta", "Lambda", "Xi", "Pi",
            "Sigma", "Upsilon", "Phi", "Psi", "Omega",
            "infty"  // 特殊情况：定义在 GREEK_LETTERS 中
        )

        var passCount = 0
        var failCount = 0
        val failures = mutableListOf<String>()

        for (name in greekLetters) {
            val atom = GreekLetterVariableAtom(name, "")
            val text = MathFontOptions.ast(atom)
            if (text != null && text.isNotEmpty()) {
                passCount++
                println("✅ \\$name -> $text")
            } else {
                failCount++
                failures.add(name)
                println("❌ \\$name -> null 或空字符串")
            }
        }

        println("\n=== GreekLetterVariableAtom 测试结果 ===")
        println("通过: $passCount")
        println("失败: $failCount")

        if (failures.isNotEmpty()) {
            println("\n失败列表:")
            failures.forEach { println("  - \\$it") }
        }

        assertEquals("所有希腊字母和 infty 都应该能获取到渲染文本", 0, failCount)
    }

    @Test
    fun test_MathFontOptions_ast_SpecialSymbolAtom() {
        // 测试特殊符号
        val dotsAtom = SpecialSymbolAtom("dots")
        assertNotNull("dots 应该能找到符号", MathFontOptions.ast(dotsAtom))

        val angleAtom = SpecialSymbolAtom("angle")
        assertNotNull("angle 应该能找到符号", MathFontOptions.ast(angleAtom))

        val forallAtom = SpecialSymbolAtom("forall")
        assertNotNull("forall 应该能找到符号", MathFontOptions.ast(forallAtom))
    }

    @Test
    fun test_MathFontOptions_ast_ExtensibleArrowAtom() {
        // 测试可扩展箭头
        val xrightarrowAtom = ExtensibleArrowAtom("xrightarrow", null, null)
        assertNotNull("xrightarrow 应该能找到符号", MathFontOptions.ast(xrightarrowAtom))

        val xleftarrowAtom = ExtensibleArrowAtom("xleftarrow", null, null)
        assertNotNull("xleftarrow 应该能找到符号", MathFontOptions.ast(xleftarrowAtom))
    }

    // ============================================================
    // 测试缺失符号（确保代码健壮性）
    // ============================================================

    @Test
    fun test_MissingSymbols_Rfraktur() {
        // Rfraktur 和 Ifraktur 可能在某些字体中不存在
        val symbol = symbolOptions.all["Rfraktur"]
        if (symbol == null) {
            println("⚠️ Rfraktur 符号不存在，\\Re 可能无法正确渲染")
        } else {
            println("✅ Rfraktur -> ${symbol.unicode}")
        }
    }

    @Test
    fun test_MissingSymbols_Ifraktur() {
        val symbol = symbolOptions.all["Ifraktur"]
        if (symbol == null) {
            println("⚠️ Ifraktur 符号不存在，\\Im 可能无法正确渲染")
        } else {
            println("✅ Ifraktur -> ${symbol.unicode}")
        }
    }

    @Test
    fun test_MissingSymbols_aleph() {
        val symbol = symbolOptions.all["aleph"]
        if (symbol == null) {
            println("⚠️ aleph 符号不存在，\\aleph 可能无法正确渲染")
        } else {
            println("✅ aleph -> ${symbol.unicode}")
        }
    }
}