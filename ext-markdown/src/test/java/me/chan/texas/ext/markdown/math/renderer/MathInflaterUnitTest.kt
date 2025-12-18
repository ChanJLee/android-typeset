package me.chan.texas.ext.markdown.math.renderer

import me.chan.texas.ext.markdown.math.ast.MathList
import me.chan.texas.ext.markdown.math.ast.MathParseException
import me.chan.texas.ext.markdown.math.ast.MathParser
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint
import me.chan.texas.ext.markdown.math.renderer.core.MathPaintImpl
import me.chan.texas.ext.markdown.math.renderer.fonts.MathFontOptions
import me.chan.texas.misc.PaintSet
import me.chan.texas.renderer.core.graphics.TexasPaint
import me.chan.texas.renderer.core.graphics.TexasPaintImpl
import me.chan.texas.utils.CharStream
import org.junit.Before
import org.junit.Test

class MathInflaterUnitTest {
    private var inflater: MathRendererInflater? = null
    private var defaultStyles: MathPaint.Styles? = null
    private var defaultPaint: MathPaint? = null

    @Before
    fun setUp() {
        inflater = MathRendererInflater()
        val texasPaint: TexasPaint = TexasPaintImpl()
        texasPaint.reset(PaintSet(MockTextPaint(1)))
        defaultPaint = MathPaintImpl(texasPaint)
        defaultStyles = MathPaint.Styles(defaultPaint)
    }

    // ============================================================
    // 辅助方法
    // ============================================================
    /**
     * 解析输入并返回 AST
     */
    @Throws(MathParseException::class)
    private fun parse(input: String): MathList? {
        val stream = CharStream(input, 0, input.length)
        val parser = MathParser(stream)
        return parser.parse()
    }

    /**
     * 解析并渲染，返回 RendererNode
     */
    @Throws(MathParseException::class)
    private fun inflate(input: String, block: DispatchAsserter.() -> Unit) {
        val ast = parse(input)
        val node = inflater!!.inflate(defaultStyles, ast)
        // 触发 measure 以便获取子节点结构
        node.measure(defaultPaint)
        node.layout(0f, 0f)
        DispatchAsserter(node).block()
    }

    @Test
    fun `test accent single token`() {
        inflate("\\hat x") {
            accent {
                cmd {
                    symbol {
                        content(MathFontOptions.symbol("asciicircum"))
                    }
                }

                content {
                    text {
                        content("x")
                    }
                }
            }
        }
    }

    @Test
    fun `test accent list`() {
        inflate("\\hat {x+y}") {
            accent {
                cmd {
                    symbol {
                        content(MathFontOptions.symbol("asciicircum"))
                    }
                }

                content {
                    linearGroup {
                        child {
                            text {
                                content("x")
                            }
                        }
                        child {
                            symbol {
                                content(MathFontOptions.symbol("plus"))
                            }
                        }
                        child {
                            text {
                                content("y")
                            }
                        }
                    }
                }
            }
        }
    }
}