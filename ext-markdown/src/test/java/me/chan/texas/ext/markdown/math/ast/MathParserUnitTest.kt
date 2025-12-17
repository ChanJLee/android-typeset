package me.chan.texas.ext.markdown.math.ast

import me.chan.texas.utils.CharStream
import org.junit.Assert
import org.junit.Test

/**
 * LaTeX 数学公式解析器完整单元测试
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

    @Test
    fun test_01_01_Number_Integer() {
        assertParsesTo("0", "0")
        assertParsesTo("42", "42")

        val ast = assertParsesToWithAst("123", "123")
        assertAst(ast)
            .term {
                atomIsNumber("123")
                hasNoSuffix()
            }
            .eof()
    }

    @Test
    fun test_01_01_Number_Float() {
        val ast = assertParsesToWithAst("123.1", "123.1")
        assertAst(ast)
            .term {
                atomIsNumber("123.1")
                hasNoSuffix()
            }
            .eof()
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