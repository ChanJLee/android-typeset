package me.chan.texas.ext.markdown.math.ast

import me.chan.texas.utils.CharStream
import org.junit.Assert
import org.junit.Test

/**
 * LaTeX 数学公式解析器完整单元测试
 * 基于 bnf_math_loose.txt 设计（移除了 Expression 层级，MathList 直接包含 Term 和 BinOp）
 */
class MathParserUnitTest {
    private fun assertParsesToWithAst(input: String, expectedOutput: String): MathList {
        try {
            val ast: MathList = parse(input)
            val actual = ast.toString()
            Assert.assertEquals("输入: " + input, expectedOutput, actual)
            println("✅ " + input + " → " + actual)
            return ast
        } catch (e: MathParseException) {
            Assert.fail("解析失败: " + input + " - " + e.pretty())
        }

        throw RuntimeException("assertParsesToWithAst failed")
    }

    private fun assertParseFails(input: String) {
        try {
            parse(input)
            Assert.fail("应该抛出异常: " + input)
        } catch (e: MathParseException) {
            println("✅ 正确拒绝: " + input + " (" + e.message + ")")
        }
    }

    private fun assertAst(mathList: MathList): MathListAsserter {
        return MathListAsserter(mathList)
    }

    // ============================================================
    // Part 1: 基础元素测试
    // ============================================================
    @Test
    fun test_01_01_Number_Integer() {
        assertParsesTo("0", "0")
        assertParsesTo("42", "42")

        val ast = assertParsesToWithAst("123", "123")
        assertAst(ast)
            .hasSize(1)
            .elementIsTerm(0)
            .atomIsNumber("123")
            .hasNoSuffix()
    }

    @Test
    fun test_01_03_Variable() {
        assertParsesTo("x", "x")

        val ast = assertParsesToWithAst("f'", "f'")
        assertAst(ast)
            .hasSize(1)
            .elementIsTerm(0)
            .atomIsVariable("f'")
    }

    // ============================================================
    // Part 2: 运算符测试 (测试扁平化结构的关键)
    // ============================================================
    @Test
    fun test_02_01_UnaryOperators() {
        // -1: Term("-", Number(1))
        val ast = assertParsesToWithAst("-1", "-1")
        assertAst(ast)
            .hasSize(2)
            .elementIsTerm(0)
            .atomIsNumber("1")
    }

    @Test
    fun test_02_02_BinaryOperators_Arithmetic() {
        // a-b: Term(a), BinOp(-), Term(b)
        val ast = assertParsesToWithAst("a-b", "a - b")
        val asserter = assertAst(ast).hasSize(3)

        asserter.nextTerm().atomIsVariable("a")
        asserter.nextBinOp().isOperator("-")
        asserter.nextTerm().atomIsVariable("b")
    }

    @Test
    fun test_02_03_BinaryOperators_Complex() {
        // a \times b
        var ast = assertParsesToWithAst("a\\times b", "a \\times b")
        var asserter = assertAst(ast).hasSize(3)

        asserter.nextTerm().atomIsVariable("a")
        asserter.nextBinOp().isOperator("\\times")
        asserter.nextTerm().atomIsVariable("b")

        // a \leq b
        ast = assertParsesToWithAst("a\\le b", "a \\le b")
        asserter = assertAst(ast).hasSize(3)
        asserter.nextTerm().atomIsVariable("a")
        asserter.nextBinOp().isOperator("\\le")
        asserter.nextTerm().atomIsVariable("b")
    }

    // ============================================================
    // Part 3: 逗号与隐式乘法
    // ============================================================
    @Test
    fun test_03_01_Punctuation_Comma() {
        // a,b -> Term(a), Term(,), Term(b)
        val ast = assertParsesToWithAst("a,b", "a , b")
        val asserter = assertAst(ast).hasSize(3)

        asserter.nextTerm().atomIsVariable("a")
        asserter.nextTerm().atomIsPunctuation(",") // 逗号是 Term
        asserter.nextTerm().atomIsVariable("b")
    }

    @Test
    fun test_06_01_ImplicitMultiplication() {
        // xy -> Term(x), Term(y)
        var ast = assertParsesToWithAst("xy", "x y")
        var asserter = assertAst(ast).hasSize(2)

        asserter.nextTerm().atomIsVariable("x")
        asserter.nextTerm().atomIsVariable("y")

        // 2x -> Term(2), Term(x)
        ast = assertParsesToWithAst("2x", "2 x")
        asserter = assertAst(ast).hasSize(2)
        asserter.nextTerm().atomIsNumber("2")
        asserter.nextTerm().atomIsVariable("x")
    }

    // ============================================================
    // Part 4: 原子表达式 (Group, Frac, Sqrt...)
    // ============================================================
    @Test
    fun test_04_01_Group() {
        // {a+b} -> Term(Group(MathList(Term(a), BinOp(+), Term(b))))
        val ast = assertParsesToWithAst("{a+b}", "{a + b}")
        assertAst(ast)
            .hasSize(1)
            .elementIsTerm(0)
            .atomIsGroup()
            .content() // Enter MathList of Group
            .hasSize(3) // a + b
            .nextTerm().atomIsVariable("a") // check first element inside group
    }

    @Test
    fun test_04_02_Frac() {
        var ast = assertParsesToWithAst("\\frac{a}{b}", "\\frac{a}{b}")
        assertAst(ast)
            .hasSize(1)
            .elementIsTerm(0)
            .atomIsFrac()
            .command("frac")
            .numeratorToString("a")
            .denominatorToString("b")

        // 复杂分式 \frac{x+y}{z}
        ast = assertParsesToWithAst("\\frac{x+y}{z}", "\\frac{x + y}{z}")
        val frac = assertAst(ast).elementIsTerm(0).atomIsFrac()

        frac.numerator()
            .hasSize(3) // x + y
            .elementIsBinOp(1).isOperator("+")

        frac.denominatorToString("z")
    }

    @Test
    fun test_04_04_Delimited() {
        // \left( x \right)
        val ast = assertParsesToWithAst("\\left(x\\right)", "\\left( x \\right)")
        val delim = assertAst(ast)
            .hasSize(1)
            .elementIsTerm(0)
            .atomIsDelimited()

        delim.leftDelimiter("(").rightDelimiter(")")
        delim.content().hasSize(1).elementIsTerm(0).atomIsVariable("x")
    }

    // ============================================================
    // Part 6: Spacing (现在是顶层元素)
    // ============================================================
    @Test
    fun test_06_05_Spacing() {
        // a \quad b -> Term(a), Spacing(quad), Term(b)
        val ast = assertParsesToWithAst("a\\quad b", "a\\quad b")
        val asserter = assertAst(ast).hasSize(3)

        asserter.nextTerm().atomIsVariable("a")
        asserter.nextSpacing().command("quad")
        asserter.nextTerm().atomIsVariable("b")
    }

    // ============================================================
    // Part 9: 复杂真实案例
    // ============================================================
    @Test
    fun test_09_01_QuadraticFormula() {
        // -b \pm \sqrt{...}
        // Term(-b), BinOp(\pm), Term(\sqrt{...})
        val input = "-b\\pm\\sqrt{b^2-4ac}"
        val ast = assertParsesToWithAst(input, "-b \\pm \\sqrt{b^2 - 4 ac}")

        val asserter = assertAst(ast).hasSize(3)

        // 1. -b
        asserter.nextTerm().atomIsVariable("b")

        // 2. \pm
        asserter.nextBinOp().isOperator("\\pm")

        // 3. \sqrt
        val sqrt = asserter.nextTerm().atomIsSqrt()

        // Check content: b^2 - 4ac -> Term(b^2), BinOp(-), Term(4), Term(a), Term(c)
        val content = sqrt.content().hasSize(5)
        content.nextTerm().atomIsVariable("b").hasSuffix()
        content.nextBinOp().isOperator("-")
        content.nextTerm().atomIsNumber("4")
        content.nextTerm().atomIsVariable("a")
        content.nextTerm().atomIsVariable("c")
    }

    @Test
    fun test_09_03_PythagoreanTheorem() {
        // a^2 + b^2 = c^2 -> Term, BinOp, Term, BinOp, Term
        val ast = assertParsesToWithAst("a^2+b^2=c^2", "a^2 + b^2 = c^2")
        val asserter = assertAst(ast).hasSize(5)

        asserter.nextTerm().atomIsVariable("a").hasSuffix()
        asserter.nextBinOp().isOperator("+")
        asserter.nextTerm().atomIsVariable("b").hasSuffix()
        asserter.nextBinOp().isOperator("=")
        asserter.nextTerm().atomIsVariable("c").hasSuffix()
    }

    @Test
    fun test_99_Structure_Check() {
        // 验证扁平结构: a+b*c -> Term(a), BinOp(+), Term(b), BinOp(*), Term(c)
        val ast = assertParsesToWithAst("a+b*c", "a + b * c")
        assertAst(ast).hasSize(5)

        Assert.assertTrue(ast.elements.get(0) is Term)
        Assert.assertTrue(ast.elements.get(1) is SymbolAtom)
        Assert.assertTrue(ast.elements.get(2) is Term)
        Assert.assertTrue(ast.elements.get(3) is SymbolAtom)
        Assert.assertTrue(ast.elements.get(4) is Term)
    }

    companion object {
        // ============================================================
        // 辅助方法
        // ============================================================
        @Throws(MathParseException::class)
        fun parse(input: String): MathList {
            val stream = CharStream(input, 0, input.length)
            val parser = MathParser(stream)
            return parser.parse()
        }

        @JvmStatic
        fun assertParsesTo(input: String, expectedOutput: String?) {
            try {
                val ast: MathList = parse(input)
                val actual = ast.toString()
                Assert.assertEquals("输入: " + input, expectedOutput, actual)
                println("✅ " + input + " → " + actual)
            } catch (e: MathParseException) {
                Assert.fail("解析失败: " + input + " - " + e.pretty())
            }
        }
    }
}