package me.chan.texas.ext.markdown.unitTest

import me.chan.texas.ext.markdown.math.ast.MathList
import me.chan.texas.ext.markdown.math.ast.MathListAsserter
import me.chan.texas.ext.markdown.math.ast.MathParseException
import me.chan.texas.ext.markdown.math.ast.MathParser
import org.junit.Test
import me.chan.texas.utils.CharStream
import org.junit.Assert

class MathParserUnitTest {

    // ================================================================
    // 1. 基础原子 (Atoms) - BNF: Number, Variable, Symbol
    // ================================================================

    @Test
    fun `test number integers and decimals`() {
        "123".assert {
            term { number("123") }
        }

        "3.14".assert {
            term { number("3.14") }
        }
    }

    @Test
    fun `test variables and greek letters`() {
        "x".assert {
            term { variable("x") }
        }

        "\\alpha".assert {
            term { variable("\\alpha") }
        }
    }

    @Test
    fun `test text atom`() {
        "\\text{abc}".assert {
            term {
                text { content("abc") }
            }
        }
    }

    // ================================================================
    // 2. 运算符与列表 (Operators & List) - BNF: Binary, Unary
    // ================================================================

    @Test
    fun `test simple binary operation`() {
        "a + b".assert {
            term { variable("a") }
            term {
                symbol("+") // 或 symbol("+")
            }
            term { variable("b") }
        }
    }

    @Test
    fun `test relation operators`() {
        "x \\le y".assert {
            term { variable("x") }
            term {
                symbol("\\le")
            }
            term { variable("y") }
        }
    }

    @Test
    fun `test implicit multiplication`() {
        // BNF 通常处理为两个连续的 Term
        "2x".assert {
            term { number("2") }
            term { variable("x") }
        }
    }

    // ================================================================
    // 3. 上下标 (Scripts) - BNF: Subscript, Superscript
    // ================================================================

    @Test
    fun `test superscript`() {
        "x^2".assert {
            term {
                variable("x")
                suffix {
                    superscript {
                        term { number("2") }
                    }
                }
            }
        }
    }

    @Test
    fun `test subscript`() {
        "x_i".assert {
            term {
                variable("x")
                suffix {
                    subscript {
                        term { variable("i") }
                    }
                }
            }
        }
    }

    @Test
    fun `test complex nested scripts`() {
        // x_i^2
        "x_i^2".assert {
            term {
                variable("x")
                suffix {
                    subscript { term { variable("i") } }
                    superscript { term { number("2") } }
                }
            }
        }

        // 2^{2^n}
        "2^{2^n}".assert {
            term {
                number("2")
                suffix {
                    superscript {
                        term {
                            number("2")
                            superscript { term { variable("n") } }
                            noSubscript()
                        }
                    }
                }
            }
        }
    }

    // ================================================================
    // 4. 分数 (Fractions) - BNF: \frac, \dfrac
    // ================================================================

    @Test
    fun `test fraction`() {
        "\\frac{1}{2}".assert {
            term {
                fraction {
                    numerator { term { number("1") } }
                    denominator { term { number("2") } }
                }
            }
        }
    }

    @Test
    fun `test display fraction`() {
        "\\dfrac{a}{b}".assert {
            term {
                fraction {
                    // 假设你的 Asserter 有检查 command 的方法
                    command("\\dfrac")
                    numerator { term { variable("a") } }
                    denominator { term { variable("b") } }
                }
            }
        }
    }

    // ================================================================
    // 5. 根号 (Roots) - BNF: \sqrt
    // ================================================================

    @Test
    fun `test sqrt without degree`() {
        "\\sqrt{x}".assert {
            term {
                sqrt {
                    noRoot()
                    content { term { variable("x") } }
                }
            }
        }
    }

    @Test
    fun `test sqrt with degree`() {
        "\\sqrt[3]{8}".assert {
            term {
                sqrt {
                    root { term { number("3") } }
                    content { term { number("8") } }
                }
            }
        }
    }

    // ================================================================
    // 6. 分组与定界符 (Groups & Delimiters) - BNF: {}, \left \right
    // ================================================================

    @Test
    fun `test curly braces group`() {
        "{a + b}".assert {
            term {
                group {
                    content {
                        term { variable("a") }
                        symbol("+")
                        term { variable("b") }
                    }
                }
            }
        }
    }

    @Test
    fun `test scalable delimiters`() {
        "\\left( x \\right)".assert {
            term {
                delimited {
                    left("(")
                    right(")")
                    content { term { variable("x") } }
                }
            }
        }
    }

    @Test
    fun `test null delimiters`() {
        // \left. \right| 常用于微积分
        "\\left. x \\right|".assert {
            term {
                delimited {
                    left(".") // 或 empty/null
                    right("|")
                }
            }
        }
    }

    // ================================================================
    // 7. 间距 (Spacing) - 参考你的 SpacingAsserter.kt
    // ================================================================

    @Test
    fun `test spacing command`() {
        "a \\quad b".assert {
            term { variable("a") }
            // 这里对应你提供的 SpacingAsserter
            spacing {
                command("quad")
                // 如果 spacing 还可以带长度或内容（根据你的kt文件）
                // length { ... } 
            }
            term { variable("b") }
        }
    }

    // ================================================================
    // 8. 样式与重音 (Styles & Accents)
    // ================================================================

    @Test
    fun `test math styles`() {
        "\\mathbf{X}".assert {
            term {
                font {
                    command("mathbf") // 或 styleName("mathbf")
                    content { term { variable("X") } }
                }
            }
        }
    }

    @Test
    fun `test accents`() {
        "\\hat{a}".assert {
            term {
                accent {
                    command("hat")
                    group { term { variable("a") } }
                }
            }
        }
    }

    // ================================================================
    // 9. 综合复杂测试 (Real World Cases)
    // ================================================================

    @Test
    fun `test quadratic formula`() {
        // -b \pm \sqrt{b^2 - 4ac}
        "-b \\pm \\sqrt{b^2 - 4ac}".assert {
            // Case 1: 负号通常解析为 symbol 或 Unary Op
            term {
                symbol("-")
            }
            term { variable("b") }
            term {
                symbol("\\pm")
            }
            term {
                sqrt {
                    content {
                        term {
                            variable("b")
                            suffix {
                                superscript { term { number("2") } }
                            }
                        }
                        symbol("-")
                        term { number("4") }
                        term { variable("a") }
                        term { variable("c") }
                    }
                }
            }
        }
    }

    // ================================================================
    // 10. 边界与错误 Case (Edge Cases)
    // ================================================================

    @Test
    fun `test empty group`() {
        "{}".assert {
            term {
                group {
                    content { /* expect empty */ }
                }
            }
        }
    }

    @Test(expected = Exception::class) // 或者使用 assertThrows
    fun `test fail on unclosed group`() {
        parse("{123")
    }

    @Test(expected = Exception::class)
    fun `test fail on missing fraction argument`() {
        parse("\\frac{1}")
    }

    // ================================================================
    // 工具方法 (Test Infrastructure)
    // ================================================================

    private fun parse(input: String) = MathParser(CharStream(input)).parse()

    /**
     * 连接 Parser 和你实现的 MathListAsserter
     */
    private fun String.assert(block: MathListAsserter.() -> Unit) {
        val ast = parse(this)
        // 假设你的入口 Asserter 叫 MathListAsserter
        MathListAsserter(ast).apply(block)
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